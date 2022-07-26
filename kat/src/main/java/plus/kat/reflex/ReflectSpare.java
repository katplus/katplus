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

import java.lang.annotation.Annotation;
import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.Map;

import plus.kat.*;
import plus.kat.anno.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.spare.*;
import plus.kat.utils.KatMap;
import plus.kat.utils.Reflect;

import static plus.kat.utils.Reflect.lookup;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ReflectSpare<T> extends SuperSpare<T, Setter<T, ?>> implements Maker<T>, Worker<T> {

    private MethodHandle handle;
    private Constructor<T> builder;

    protected Class<?> owner;
    protected boolean marker;

    protected Class<?>[] args;
    protected KatMap<Object, Target> params;

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
    protected void initialize() {
        onFields(
            klass.getDeclaredFields()
        );
        onMethods(
            klass.getDeclaredMethods()
        );
        onConstructors(
            klass.getDeclaredConstructors()
        );
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

    @Override
    @Nullable
    public T cast(
        @NotNull Supplier supplier,
        @NotNull Map<?, ?> data
    ) throws Exception {
        // create ins
        T entity = apply(
            Alias.EMPTY
        );

        if (entity == null) {
            return null;
        }

        // foreach
        for (Map.Entry<?, ?> entry : data.entrySet()) {
            // key
            Object key = entry.getKey();
            if (key == null) {
                continue;
            }

            // try lookup
            Setter<T, ?> setter = get(key);
            if (setter == null) {
                continue;
            }

            // get class specified
            Class<?> klass = setter.getType();

            // get spare specified
            Spare<?> spare = supplier.lookup(klass);
            if (spare == null) {
                continue;
            }

            setter.onAccept(
                entity, spare.cast(
                    supplier, entry.getValue()
                )
            );
        }

        return entity;
    }

    @Override
    public Builder<T> getBuilder(
        @Nullable Type type
    ) {
        if (params == null) {
            return new Builder0<>(this);
        }
        return new Builder2<>(this);
    }

    @Override
    public Target target(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        if (params == null) {
            return null;
        }
        return params.get(alias);
    }

    @Override
    @Nullable
    public Setter<T, ?> setter(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return get(
            alias.isEmpty() ? index : alias
        );
    }

    /**
     * @param fields the specified {@link Field} collection
     */
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
                getter(
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
                    getter(
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
                    getter(
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
                            getter(
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
                getter(
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
    @SuppressWarnings("unchecked")
    protected void onConstructors(
        @NotNull Constructor<?>[] constructors
    ) {
        Constructor<?> $ = null,
            b = constructors[0];
        for (int i = 1; i < constructors.length; i++) {
            Constructor<?> c = constructors[i];
            if (b.getParameterCount() <=
                c.getParameterCount()) {
                $ = b;
                b = c;
            } else if ($ == null) {
                $ = c;
            } else {
                if ($.getParameterCount() <=
                    c.getParameterCount()) {
                    $ = c;
                }
            }
        }

        b.setAccessible(true);
        builder = (Constructor<T>) b;

        int count = b.getParameterCount();
        if (count == 0) {
            try {
                handle = lookup.
                    unreflectConstructor(b);
            } catch (Throwable e) {
                // Nothing
            }
        } else {
            Parameter[] ps = null;
            params = new KatMap<>();

            args = b.getParameterTypes();
            if ($ != null) {
                int i = $.getParameterCount();
                if (i + 2 == count && args[i] == int.class &&
                    "kotlin.jvm.internal.DefaultConstructorMarker".equals(args[i + 1].getName())) {
                    marker = true;
                    b = $;
                    args = $.getParameterTypes();
                }
            }

            Type[] ts = b.getGenericParameterTypes();
            Annotation[][] as = b.getParameterAnnotations();

            int i = 0, j = as.length - args.length;
            Class<?> enclosingClass = klass.getEnclosingClass();

            if (enclosingClass != null &&
                (klass.getModifiers() & Modifier.STATIC) == 0) {
                if (enclosingClass == args[0]) {
                    i++;
                    owner = enclosingClass;
                }
            }

            for (; i < args.length; i++) {
                Format format = null;
                Expose expose = null;
                for (Annotation a : as[i + j]) {
                    Class<?> at = a.annotationType();
                    if (at == Expose.class) {
                        expose = (Expose) a;
                    } else if (at == Format.class) {
                        format = (Format) a;
                    }
                }

                Coder<?> c = Reflect.activate(
                    args[i], expose, format, supplier
                );

                Edge edge = new Edge(i);
                edge.setCoder(c);
                edge.setType(args[i]);
                edge.setActualType(ts[i]);

                if (expose == null) {
                    if (ps == null) {
                        ps = b.getParameters();
                    }
                    params.put(
                        ps[i].getName(), edge
                    );
                } else {
                    String[] keys = expose.value();
                    for (int k = 0; k < keys.length; k++) {
                        params.put(
                            keys[k], k == 0 ? edge : edge.clone()
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
    static class Handle<K>
        extends Node<K>
        implements Setter<K, Object> {

        final Class<?> klass;
        final Type type;
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
            super(expose);
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
            super(expose);
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
        public Class<?> getType() {
            return klass;
        }

        @Override
        public Type getActualType() {
            return type;
        }

        @Override
        public Handle<K> clone() {
            return new Handle<>(this);
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    public static class Builder2<K> extends Builder$<K> {

        protected K entity;
        protected int index;

        protected Object[] data;
        protected int count;
        protected boolean marker;

        protected Class<?> owner;
        protected Class<?>[] params;

        protected Target target;
        protected Setter<K, ?> setter;

        protected Cache<K> cache;
        protected Worker<K> worker;

        public Builder2(
            @NotNull ReflectSpare<K> spare
        ) {
            this.worker = spare;
            this.owner = spare.owner;
            this.params = spare.args;
            this.marker = spare.marker;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws IOCrash {
            int size = params.length;
            if (marker) {
                size += 2;
            }
            data = new Object[size];
            if (owner != null) {
                Object own = getParent()
                    .getResult();
                if (own != null) {
                    data[count++] = own;
                } else {
                    throw new UnexpectedCrash(
                        "Unexpectedly, getParent().getResult() is null"
                    );
                }
            }
        }

        @Override
        public void onAccept(
            @NotNull Target tag,
            @NotNull Object value
        ) throws IOCrash {
            if (entity != null) {
                setter.onAccept(
                    entity, value
                );
            } else if (target != null) {
                int i = target.getIndex();
                target = null;
                data[i] = value;
                if (params.length == ++count) {
                    this.embark();
                }
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
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOCrash {
            onAccept(
                null, child.getResult()
            );
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
            int i = index++;
            if (entity != null) {
                setter = worker.setter(
                    i, alias
                );
                if (setter != null) {
                    onAccept(
                        space, value, setter
                    );
                }
            } else {
                target = worker.target(
                    i, alias
                );
                if (target != null) {
                    onAccept(
                        space, value, target
                    );
                } else {
                    setter = worker.setter(
                        i, alias
                    );
                    if (setter != null) {
                        onAccept(
                            space, value, setter
                        );
                    }
                }
            }
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOCrash {
            int i = index++;
            if (entity != null) {
                setter = worker.setter(
                    i, alias
                );
                if (setter != null) {
                    return getBuilder(
                        space, setter
                    );
                }
            } else {
                target = worker.target(
                    i, alias
                );
                if (target != null) {
                    return getBuilder(
                        space, target
                    );
                } else {
                    setter = worker.setter(
                        i, alias
                    );
                    if (setter != null) {
                        return getBuilder(
                            space, setter
                        );
                    }
                }
            }

            return null;
        }

        /**
         * @author kraity
         * @since 0.0.2
         */
        static class Cache<K> {
            Object value;
            Cache<K> next;
            Setter<K, ?> setter;
        }

        private void embark()
            throws IOCrash {
            if (marker) {
                int i = 0, flag = 0;
                for (; i < params.length; i++) {
                    if (data[i] == null) {
                        flag |= (1 << i);
                        Class<?> c = params[i];
                        if (c.isPrimitive()) {
                            if (c == int.class) {
                                data[i] = 0;
                            } else if (c == long.class) {
                                data[i] = 0L;
                            } else if (c == float.class) {
                                data[i] = 0F;
                            } else if (c == double.class) {
                                data[i] = 0D;
                            } else if (c == boolean.class) {
                                data[i] = false;
                            } else if (c == byte.class) {
                                data[i] = (byte) 0;
                            } else if (c == char.class) {
                                data[i] = (char) 0;
                            } else if (c == short.class) {
                                data[i] = (short) 0;
                            }
                        }
                    }
                }
                data[i] = flag;
            }

            try {
                entity = worker.apply(
                    getAlias(), data
                );
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

        @Override
        public K getResult() {
            if (entity == null &&
                (marker || owner != null)) {
                try {
                    embark();
                } catch (IOCrash e) {
                    return null;
                }
            }
            return entity;
        }

        @Override
        public void onDestroy() {
            index = 0;
            count = 0;
            data = null;
            cache = null;
            setter = null;
            entity = null;
            target = null;
        }
    }
}
