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
import java.lang.reflect.*;

import plus.kat.*;
import plus.kat.anno.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.utils.KatMap;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ReflectSpare<T> extends AspectSpare<T> {

    private Constructor<T> builder;
    private KatMap<Object, Param1> params;

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
    public T apply(
        @NotNull Alias alias
    ) throws Crash {
        try {
            return builder.newInstance();
        } catch (Exception e) {
            throw new Crash(e);
        }
    }

    @Override
    public Builder<T> getBuilder(
        @Nullable Type type
    ) {
        if (params == null) {
            return new Builder0<>(this);
        }
        return new Builder1<>(this);
    }

    /**
     * @param fields the specified {@link Field} collection
     */
    @Override
    protected void onFields(
        @NotNull Field[] fields
    ) {
        boolean direct = (flags & Embed.DIRECT) != 0;
        for (Field field : fields) {
            Expose expose = field
                .getAnnotation(
                    Expose.class
                );
            if (expose == null) {
                continue;
            }

            Field1<T> field1 =
                new Field1<>(
                    field, expose, supplier
                );

            int h = field1.getHash();
            // check use index
            if (direct && h > -1) put(
                h, field1.clone()
            );

            String[] keys = expose.value();
            if (keys.length == 0) {
                String name = field.getName();
                if (expose.export()) {
                    // register getter
                    addGetter(name, field1);
                    // register setter
                    put(name, field1.clone());
                } else put(
                    name, field1
                );
            } else {
                // register only the first alias
                if (expose.export()) {
                    addGetter(keys[0], field1);
                }

                for (String alias : keys) {
                    // check empty
                    if (!alias.isEmpty()) put(
                        alias, field1.clone()
                    );
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
            int count = method.getParameterCount();
            if (count > 1) {
                continue;
            }

            Method1<T> method1;
            Expose expose = method
                .getAnnotation(
                    Expose.class
                );

            if (expose == null) {
                // check flag
                if (sealed) {
                    continue;
                }

                // its modifier
                int mod = method.getModifiers();

                // check its modifier
                if ((mod & Modifier.PUBLIC) == 0 ||
                    (mod & Modifier.STATIC) != 0) {
                    continue;
                }
            } else {
                String[] keys = expose.value();
                if (keys.length != 0) {
                    method1 = new Method1<>(
                        method, expose, supplier
                    );

                    if (count != 0) {
                        int h = method1.getHash();
                        // check use index
                        if (direct && h >= 0) {
                            // register setter
                            put(h, method1);
                        }

                        for (String alias : keys) {
                            // check empty
                            if (!alias.isEmpty()) put(
                                alias, method1.clone()
                            );
                        }
                    } else {
                        // register all aliases
                        for (int i = 0; i < keys.length; i++) {
                            addGetter(
                                keys[i], i == 0 ? method1 : method1.clone()
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

            method1 = new Method1<>(
                method, expose, supplier
            );

            Alias alias = new Alias(name);
            if (count == 0) {
                // register getter
                addGetter(
                    alias, method1
                );
            } else {
                // register setter
                put(alias, method1);

                // check use index
                if (direct && expose != null) {
                    int h = expose.index();
                    if (h >= 0) put(
                        h, method1.clone()
                    );
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
        Constructor<?> c = constructors[0];
        for (int i = 1; i < constructors.length; i++) {
            if (c.getParameterCount() <
                constructors[i].getParameterCount()) {
                c = constructors[i];
            }
        }

        c.setAccessible(true);
        builder = (Constructor<T>) c;
        if (c.getParameterCount() != 0) {
            register(c, supplier);
        }
    }

    private void register(
        @NotNull Constructor<?> b,
        @NotNull Supplier supplier
    ) {
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

            Coder<?> c = Reflex.lookup(
                cs[i], expose, format, supplier
            );

            Param1 param =
                new Param1(
                    i, c, ts[i], cs[i]
                );

            if (expose == null) {
                if (ps == null) {
                    ps = b.getParameters();
                }
                params.put(
                    ps[i].getName(), param
                );
            } else {
                String[] keys = expose.value();
                for (int k = 0; k < keys.length; k++) {
                    params.put(
                        keys[k], k == 0 ? param : param.clone()
                    );
                }
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Field1<K> extends Node<K>
        implements Setter<K, Object>, Getter<K, Object> {

        final Field field;
        final Coder<?> coder;
        final Type type;
        final Class<?> klass;
        final boolean nullable;

        public Field1(
            Field1<?> field
        ) {
            this.field = field.field;
            this.coder = field.coder;
            this.type = field.type;
            this.klass = field.klass;
            this.nullable = field.nullable;
        }

        public Field1(
            Field field,
            Expose expose,
            Supplier supplier
        ) {
            super(expose == null
                ? -1 : expose.index()
            );
            this.field = field;
            field.setAccessible(true);

            this.klass = field.getType();
            this.type = field.getGenericType();
            this.nullable = field.getAnnotation(NotNull.class) == null;

            Format format = field
                .getAnnotation(Format.class);
            this.coder = Reflex.lookup(
                klass, expose, format, supplier
            );
        }

        @Override
        @Nullable
        public Object apply(
            @NotNull K it
        ) {
            try {
                return field.get(it);
            } catch (Exception e) {
                // nothing
            }
            return null;
        }

        @Override
        @Nullable
        public Object onApply(
            @NotNull Object it
        ) {
            try {
                return field.get(it);
            } catch (Exception e) {
                // nothing
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
                    field.set(it, val);
                } catch (Exception e) {
                    // nothing
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
                    field.set(it, val);
                } catch (Exception e) {
                    // nothing
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
        public Field1<K> clone() {
            return new Field1<>(this);
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Method1<K> extends Node<K>
        implements Setter<K, Object>, Getter<K, Object> {

        final Method method;
        final Coder<?> coder;
        final Type type;
        final Class<?> klass;

        public Method1(
            Method1<?> method
        ) {
            this.coder = method.coder;
            this.type = method.type;
            this.klass = method.klass;
            this.method = method.method;
        }

        public Method1(
            Method method,
            Expose expose,
            Supplier supplier
        ) {
            super(expose == null
                ? -1 : expose.index()
            );
            switch (method.getParameterCount()) {
                case 0: {
                    this.klass = method.getReturnType();
                    this.type = klass;
                    break;
                }
                case 1: {
                    this.klass = method.getParameterTypes()[0];
                    this.type = method.getGenericParameterTypes()[0];
                    break;
                }
                default: {
                    throw new NullPointerException(
                        "Unexpectedly the parameter length of '" + method.getName() + "' is greater than '1'"
                    );
                }
            }

            this.method = method;
            method.setAccessible(true);

            Format format = method
                .getAnnotation(Format.class);
            this.coder = Reflex.lookup(
                klass, expose, format, supplier
            );
        }

        @Override
        @Nullable
        public Object apply(
            @NotNull K it
        ) {
            try {
                return method.invoke(it);
            } catch (Exception e) {
                // nothing
            }
            return null;
        }

        @Override
        @Nullable
        public Object onApply(
            @NotNull Object it
        ) {
            try {
                return method.invoke(it);
            } catch (Exception e) {
                // nothing
            }
            return null;
        }

        @Override
        public void accept(
            @NotNull K it,
            @Nullable Object val
        ) {
            try {
                method.invoke(it, val);
            } catch (Exception e) {
                // nothing
            }
        }

        @Override
        public void onAccept(
            @NotNull K it,
            @Nullable Object val
        ) {
            try {
                method.invoke(it, val);
            } catch (Exception e) {
                // nothing
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
        public Method1<K> clone() {
            return new Method1<>(this);
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Param1 extends
        KatMap.Entry<String, Param1> {

        final int index;
        final Coder<?> coder;
        final Type type;
        final Class<?> klass;

        public Param1(
            int index, Coder<?> coder,
            Type type, Class<?> klass
        ) {
            super(0);
            this.index = index;
            this.coder = coder;
            this.type = type;
            this.klass = klass;
        }

        @Override
        public Param1 clone() {
            return new Param1(
                index, coder, type, klass
            );
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Builder1<K> extends Builder0<K> {

        private Object[] params;
        private Constructor<K> c;

        private int count;
        private Param1 param;
        private Cache<K> cache;

        private ReflectSpare<K> reflex;
        private KatMap<Object, Param1> a;

        public Builder1(
            @NotNull ReflectSpare<K> reflex
        ) {
            super(reflex);
            this.reflex = reflex;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) {
            a = reflex.params;
            c = reflex.builder;
            params = new Object[c.getParameterCount()];
        }

        @Override
        public void onAccept(
            @Nullable Object value
        ) throws IOCrash {
            if (entity != null) {
                setter.onAccept(
                    entity, value
                );
            } else {
                Cache<K> c = new Cache<>();
                c.value = value;
                c.setter = setter;

                if (cache == null) {
                    cache = c;
                } else {
                    cache.next = c;
                }
            }
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
            if (entity == null) {
                Param1 param = a.get(alias);
                if (param != null) {
                    ++index; // increment

                    // specified coder
                    Coder<?> coder = param.coder;

                    if (coder != null) {
                        params[param.index] =
                            coder.read(
                                flag, value
                            );
                    } else {
                        // specified spare
                        coder = supplier.lookup(
                            param.klass
                        );

                        // skip if null
                        if (coder != null) {
                            params[param.index] =
                                coder.read(
                                    flag, value
                                );
                        }
                    }

                    embark();
                    return;
                }
            }

            super.onAccept(
                space, alias, value
            );
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOCrash {
            if (entity != null) {
                setter.onAccept(
                    entity, child.getResult()
                );
            } else if (param == null) {
                onAccept(
                    child.getResult()
                );
            } else {
                params[param.index] = child.getResult();
                param = null;
                embark();
            }
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOCrash {
            if (entity == null) {
                param = a.get(alias);
                if (param != null) {
                    ++index; // increment

                    // specified coder
                    Coder<?> coder = param.coder;

                    if (coder == null) {
                        // specified spare
                        coder = supplier.lookup(
                            param.klass
                        );

                        if (coder == null) {
                            return null;
                        }
                    }

                    return coder.getBuilder(param.type);
                }
            }

            return super.getBuilder(
                space, alias
            );
        }

        static class Cache<K> {
            Object value;
            Cache<K> next;
            Setter<K, ?> setter;
        }

        private void embark()
            throws IOCrash {
            if (++count == params.length) {
                try {
                    entity = c.newInstance(params);
                    while (cache != null) {
                        cache.setter.onAccept(
                            entity, cache.value
                        );
                        cache = cache.next;
                    }
                } catch (Exception e) {
                    throw new IOCrash(e);
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            a = null;
            c = null;
            param = null;
            cache = null;
            params = null;
            reflex = null;
        }
    }
}
