/*
 * Copyright 2022 Kat+ Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package plus.kat.reflex;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import java.lang.annotation.*;
import java.lang.invoke.*;
import java.lang.reflect.*;

import plus.kat.*;
import plus.kat.anno.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.utils.KatMap;
import plus.kat.utils.Reflect;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ReflectSpare<T> extends AspectSpare<T> implements Maker<T> {

    static final MethodHandles.Lookup
        lookup = MethodHandles.lookup();

    private MethodHandle handle;
    private Constructor<T> builder;

    /**
     * @throws SecurityException If the {@link Constructor#setAccessible(boolean)} is denied
     */
    public ReflectSpare(
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        this(klass.getAnnotation(Embed.class), klass, supplier);
    }

    /**
     * @throws SecurityException If the {@link Constructor#setAccessible(boolean)} is denied
     */
    public ReflectSpare(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        super(embed, klass, supplier);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public T apply(
        @NotNull Alias alias
    ) throws Crash {
        try {
            return (T) handle.invoke();
        } catch (Throwable e) {
            throw new Crash(e);
        }
    }

    @Override
    @Nullable
    public T apply(
        @NotNull Alias alias,
        @NotNull Object... params
    ) throws Crash {
        try {
            return builder.newInstance(params);
        } catch (Throwable e) {
            throw new Crash(e);
        }
    }

    /**
     * @param fields the specified {@link Field} collection
     */
    @Override
    protected void onFields(
        @NotNull Field[] fields
    ) {
        boolean sealed = (flags & Embed.SEALED) != 0;
        boolean direct = (flags & Embed.DIRECT) != 0;

        for (Field field : fields) {
            // its modifier
            int mod = field.getModifiers();

            // check its modifier
            if ((mod & Modifier.STATIC) != 0) {
                continue;
            }

            Expose expose = field
                .getAnnotation(
                    Expose.class
                );

            if (expose == null) {
                // check flag
                if (sealed) {
                    continue;
                }

                // check its modifier
                if ((mod & Modifier.PUBLIC) == 0) {
                    continue;
                }
            }

            Handle<T> handle;
            try {
                handle = new Handle<>(
                    field, expose, supplier
                );
            } catch (Throwable e) {
                continue;
            }

            if (expose == null) {
                String name = field.getName();
                // register getter
                addGetter(
                    name, handle
                );

                // register setter
                put(
                    name, handle.clone()
                );
                continue;
            }

            int k = handle.getHash();
            // check use index
            if (direct && k > -1) {
                put(
                    k, handle.clone()
                );
            }

            String[] keys = expose.value();
            if (keys.length == 0) {
                String name = field.getName();
                if (expose.export()) {
                    addGetter(
                        name, handle
                    );
                    put(
                        name, handle.clone()
                    );
                } else {
                    put(
                        name, handle
                    );
                }
            } else {
                // register only the first alias
                if (expose.export()) {
                    addGetter(
                        keys[0], handle
                    );
                }

                for (String alias : keys) {
                    // check empty
                    if (!alias.isEmpty()) {
                        put(
                            alias, handle.clone()
                        );
                    }
                }
            }
        }
    }

    /**
     * @param methods the specified {@link Method} collection
     */
    @Override
    protected void onMethods(
        @NotNull Method[] methods
    ) {
        boolean sealed = (flags & Embed.SEALED) != 0;
        boolean direct = (flags & Embed.DIRECT) != 0;

        for (Method method : methods) {
            int count = method.
                getParameterCount();
            if (count > 1) {
                continue;
            }

            // its modifier
            int mod = method.getModifiers();

            // check its modifier
            if ((mod & Modifier.STATIC) != 0) {
                continue;
            }

            Handle<T> handle;
            Expose expose = method
                .getAnnotation(
                    Expose.class
                );

            if (expose == null) {
                // check flag
                if (sealed) {
                    continue;
                }

                // check its modifier
                if ((mod & Modifier.PUBLIC) == 0) {
                    continue;
                }
            } else {
                String[] keys = expose.value();
                if (keys.length != 0) {
                    try {
                        handle = new Handle<>(
                            method, expose, supplier
                        );
                    } catch (Throwable e) {
                        continue;
                    }

                    if (count != 0) {
                        int k = handle.getHash();
                        // check use index
                        if (direct && k >= 0) {
                            // register setter
                            put(k, handle);
                        }

                        for (String alias : keys) {
                            // check empty
                            if (!alias.isEmpty()) put(
                                alias, handle.clone()
                            );
                        }
                    } else {
                        // register all aliases
                        for (int i = 0; i < keys.length; i++) {
                            addGetter(
                                keys[i], i == 0 ? handle : handle.clone()
                            );
                        }
                    }
                    continue;
                }
            }

            String key = method.getName();
            int i = 0, l = key.length();
            if (l < 4) {
                continue;
            }

            char ch = key.charAt(i++);
            if (ch == 's') {
                if (count == 0 ||
                    key.charAt(i++) != 'e' ||
                    key.charAt(i++) != 't') {
                    continue;
                }
            } else if (ch == 'g') {
                if (count != 0 ||
                    key.charAt(i++) != 'e' ||
                    key.charAt(i++) != 't') {
                    continue;
                }
            } else if (ch == 'i') {
                if (count != 0 ||
                    key.charAt(i++) != 's') {
                    continue;
                }
            } else {
                continue;
            }

            byte[] name;
            char c1 = key.charAt(i++);
            if (c1 < 'A' || 'Z' < c1) {
                continue;
            }

            if (i == l) {
                name = new byte[]{
                    (byte) (c1 + 0x20)
                };
            } else {
                // See: java.beans.Introspector#decapitalize(String)
                char c2 = key.charAt(i);
                if (c2 < 'A' || 'Z' < c2) {
                    c1 += 0x20;
                }

                name = new byte[l - i + 1];
                name[0] = (byte) c1;
                name[1] = (byte) c2;

                for (int k = 2; ++i < l; ) {
                    name[k++] = (byte) key.charAt(i);
                }
            }

            try {
                handle = new Handle<>(
                    method, expose, supplier
                );
            } catch (Throwable e) {
                continue;
            }

            Alias alias = new Alias(name);
            if (count == 0) {
                // register getter
                addGetter(
                    alias, handle
                );
            } else {
                // register setter
                put(alias, handle);

                // check use index
                if (direct && expose != null) {
                    int k = expose.index();
                    if (k >= 0) {
                        put(k, handle.clone());
                    }
                }
            }
        }
    }

    /**
     * @param constructors the specified {@link Constructor} collection
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onConstructors(
        @NotNull Constructor<?>[] constructors
    ) {
        Constructor<?> b = constructors[0];
        for (int i = 1; i < constructors.length; i++) {
            Constructor<?> c = constructors[i];
            if (b.getParameterCount() <
                c.getParameterCount()) {
                b = c;
            }
        }

        args = b.getParameterCount();
        b.setAccessible(true);
        builder = (Constructor<T>) b;

        if (args == 0) {
            try {
                handle = lookup.
                    unreflectConstructor(b);
            } catch (Throwable e) {
                // Nothing
            }
        } else {
            Parameter[] ps = null;
            params = new KatMap<>();

            Class<?>[] cs = b.getParameterTypes();
            Type[] ts = b.getGenericParameterTypes();
            Annotation[][] as = b.getParameterAnnotations();

            for (int i = 0; i < cs.length; i++) {
                Format format = null;
                Expose expose = null;
                for (Annotation a : as[i]) {
                    Class<?> at = a.annotationType();
                    if (at == Expose.class) {
                        expose = (Expose) a;
                    } else if (at == Format.class) {
                        format = (Format) a;
                    }
                }

                Coder<?> c = Reflect.activate(
                    cs[i], expose, format, supplier
                );

                Arg arg = new Arg(
                    cs[i], ts[i], i, c
                );

                if (expose == null) {
                    if (ps == null) {
                        ps = b.getParameters();
                    }
                    params.put(
                        ps[i].getName(), arg
                    );
                } else {
                    String[] keys = expose.value();
                    for (int k = 0; k < keys.length; k++) {
                        params.put(
                            keys[k], k == 0 ? arg : arg.clone()
                        );
                    }
                }
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Arg extends
        Entry<String, Param> implements Param {

        final Class<?> klass;
        final Type type;
        final int index;
        final Coder<?> coder;

        public Arg(
            Class<?> klass,
            Type type,
            int index,
            Coder<?> coder
        ) {
            super(0);
            this.klass = klass;
            this.type = type;
            this.index = index;
            this.coder = coder;
        }

        @Override
        public Class<?> getKlass() {
            return klass;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Coder<?> getCoder() {
            return coder;
        }

        @Override
        public Arg clone() {
            return new Arg(
                klass, type, index, coder
            );
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Handle<K> extends Node<K>
        implements Setter<K, Object>, Getter<K, Object> {

        final Class<?> klass;
        final Type type;
        final Coder<?> coder;
        final MethodHandle setter;
        final MethodHandle getter;

        public Handle(
            Handle<?> handle
        ) {
            this.klass = handle.klass;
            this.type = handle.type;
            this.coder = handle.coder;
            this.setter = handle.setter;
            this.getter = handle.getter;
            this.nullable = handle.nullable;
        }

        public Handle(
            Field field,
            Expose expose,
            Supplier supplier
        ) throws IllegalAccessException {
            super(expose == null
                ? -1 : expose.index()
            );

            klass = field.getType();
            type = field.getGenericType();

            field.setAccessible(true);
            setter = lookup.unreflectSetter(field);
            getter = lookup.unreflectGetter(field);
            nullable = field.getAnnotation(NotNull.class) == null;

            Format format = field
                .getAnnotation(Format.class);
            coder = Reflect.activate(
                klass, expose, format, supplier
            );
        }

        public Handle(
            Method method,
            Expose expose,
            Supplier supplier
        ) throws IllegalAccessException {
            super(expose == null
                ? -1 : expose.index()
            );

            switch (method.getParameterCount()) {
                case 0: {
                    type = klass = method.getReturnType();
                    break;
                }
                case 1: {
                    klass = method.getParameterTypes()[0];
                    type = method.getGenericParameterTypes()[0];
                    break;
                }
                default: {
                    throw new NullPointerException(
                        "Unexpectedly, the parameter length of '" + method.getName() + "' is greater than '1'"
                    );
                }
            }

            method.setAccessible(true);
            getter = setter = lookup.unreflect(method);
            nullable = method.getAnnotation(NotNull.class) == null;

            Format format = method
                .getAnnotation(Format.class);
            coder = Reflect.activate(
                klass, expose, format, supplier
            );
        }

        @Nullable
        @Override
        public Object apply(
            @NotNull K it
        ) {
            try {
                return getter.invoke(it);
            } catch (Throwable e) {
                // Nothing
            }
            return null;
        }

        @Nullable
        @Override
        public Object onApply(
            @NotNull Object it
        ) {
            try {
                return getter.invoke(it);
            } catch (Throwable e) {
                // Nothing
            }
            return null;
        }

        @Override
        public void accept(
            @NotNull K it,
            @Nullable Object val
        ) {
            if (val != null || nullable) {
                try {
                    setter.invoke(it, val);
                } catch (Throwable e) {
                    // Nothing
                }
            }
        }

        @Override
        public void onAccept(
            @NotNull K it,
            @Nullable Object val
        ) {
            if (val != null || nullable) {
                try {
                    setter.invoke(it, val);
                } catch (Throwable e) {
                    // Nothing
                }
            }
        }

        @Override
        public Coder<?> getCoder() {
            return coder;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Class<?> getKlass() {
            return klass;
        }

        @Override
        public Handle<K> clone() {
            return new Handle<>(this);
        }
    }
}
