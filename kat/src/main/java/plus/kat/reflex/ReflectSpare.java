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
import java.sql.ResultSet;
import java.sql.SQLException;

import plus.kat.*;
import plus.kat.anno.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.spare.*;
import plus.kat.stream.*;
import plus.kat.utils.KatMap;
import plus.kat.utils.Reflect;

import static plus.kat.utils.Reflect.lookup;

/**
 * @author kraity
 * @since 0.0.2
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class ReflectSpare<T> extends Workman<T> implements Maker<T> {

    private MethodHandle handle;
    private Constructor<T> builder;

    private int edge;
    private Class<?> master;

    private Class<?>[] args;
    private KatMap<Object, Target> params;

    /**
     * @throws SecurityException If the {@link Constructor#setAccessible(boolean)} is denied
     */
    public ReflectSpare(
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        super(klass, supplier);
    }

    /**
     * @throws SecurityException If the {@link Constructor#setAccessible(boolean)} is denied
     */
    public ReflectSpare(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier,
        @Nullable Provider provider
    ) {
        super(embed, klass, supplier, provider);
    }

    @Override
    protected void initialize() {
        Class<?> clazz = klass;
        onConstructors(
            clazz.getDeclaredConstructors()
        );
        do {
            onFields(
                clazz.getDeclaredFields()
            );
            onMethods(
                clazz.getDeclaredMethods()
            );
        } while (
            (clazz = clazz.getSuperclass()) != Object.class
        );
    }

    @Override
    public T apply() {
        MethodHandle m = handle;
        if (m != null) {
            try {
                return (T) m.invoke();
            } catch (Throwable e) {
                // Nothing
            }
        }
        return null;
    }

    @Override
    public T apply(
        @NotNull Alias alias
    ) throws Crash {
        MethodHandle m = handle;
        if (m == null) {
            throw new Crash(
                "Not supported"
            );
        }
        try {
            return (T) m.invoke();
        } catch (Throwable e) {
            throw new Crash(
                "Failed to create", e
            );
        }
    }

    @Override
    public T apply(
        @NotNull Alias alias,
        @NotNull Object... data
    ) throws Crash {
        Constructor<T> b = builder;
        if (b == null) {
            throw new Crash(
                "Not supported"
            );
        }

        int i = 0, flag = 0;
        Class<?>[] as = args;
        for (; i < as.length; i++) {
            if (data[i] == null) {
                flag |= (1 << i);
                Class<?> c = as[i];
                if (c.isPrimitive()) {
                    data[i] = Reflect.def(c);
                }
            }
        }

        if (as.length !=
            data.length) {
            data[i] = flag;
        }

        try {
            return b.newInstance(data);
        } catch (Throwable e) {
            throw new Crash(
                "Failed to create", e
            );
        }
    }

    @Override
    public T apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) throws CallCrash {
        try {
            if (params == null) {
                T bean = apply(Alias.EMPTY);
                update(
                    bean, spoiler, supplier
                );
                return bean;
            }

            if (size() == 0 && master == null) {
                Object[] group = new Object[args.length];
                update(
                    group, spoiler, supplier
                );
                return apply(
                    Alias.EMPTY, group
                );
            }
        } catch (CallCrash e) {
            throw e;
        } catch (Throwable e) {
            throw new CallCrash(
                "Error creating " + getType(), e
            );
        }

        throw new CallCrash(
            "Not currently supported"
        );
    }

    @Override
    public T apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        try {
            if (params == null) {
                T bean = apply(Alias.EMPTY);
                update(
                    bean, supplier, resultSet
                );
                return bean;
            }

            if (size() == 0 && master == null) {
                Object[] group = new Object[args.length];
                update(
                    group, supplier, resultSet
                );
                return apply(
                    Alias.EMPTY, group
                );
            }
        } catch (SQLException e) {
            throw e;
        } catch (Throwable e) {
            throw new SQLCrash(
                "Error creating " + getType(), e
            );
        }

        throw new SQLCrash(
            "Not currently supported"
        );
    }

    @Nullable
    public T convert(
        @NotNull Object result,
        @NotNull Supplier supplier
    ) {
        Spoiler spoiler =
            supplier.flat(result);
        if (spoiler != null) try {
            if (params == null) {
                T bean = apply(Alias.EMPTY);
                update(
                    bean, spoiler, supplier
                );
                return bean;
            }

            if (size() == 0 && master == null) {
                Object[] group = new Object[args.length];
                update(
                    group, spoiler, supplier
                );
                return apply(
                    Alias.EMPTY, group
                );
            }
        } catch (Exception e) {
            // Nothing
        }

        return null;
    }

    @Override
    public Builder<T> getBuilder(
        @Nullable Type type
    ) {
        if (params == null) {
            return new Builder0<>(this);
        }

        Class<?> main = master;
        int size = args.length + edge;

        if (main == null && size() == 0) {
            return new Builder1<>(
                this, new Object[size]
            );
        }

        return new Builder2<>(
            this, new Object[size], main
        );
    }

    @Override
    public Target tag(
        Object alias
    ) {
        if (params == null) {
            return null;
        }
        return params.get(alias);
    }

    @Override
    public Target tag(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        if (params == null) {
            return null;
        }
        return params.get(
            alias.isEmpty() ? index : alias
        );
    }

    @Override
    public Setter set(
        @NotNull Object alias
    ) {
        return (Setter) getOrDefault(alias, null);
    }

    @Override
    public Setter set(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return (Setter) getOrDefault(
            alias.isEmpty() ? index : alias, null
        );
    }

    /**
     * @param fields the specified {@link Field} collection
     */
    private void onFields(
        @NotNull Field[] fields
    ) {
        boolean sealed = (flags & Embed.SEALED) != 0;
        boolean direct = (flags & Embed.DIRECT) != 0;

        for (Field field : fields)
            try {
                // its modifier
                int mod = field.getModifiers();

                // filter invalid
                if ((mod & Modifier.STATIC) != 0) {
                    continue;
                }

                Task<T> node;
                Expose expose = field
                    .getAnnotation(
                        Expose.class
                    );

                if (expose == null) {
                    // check flag
                    if (sealed) {
                        continue;
                    }

                    // filter invalid
                    if ((mod & Modifier.PUBLIC) == 0 ||
                        (mod & Modifier.TRANSIENT) != 0) {
                        continue;
                    }
                }

                String name;
                String[] keys;

                if (expose == null) {
                    name = field.getName();
                    if (containsKey(name)) {
                        continue;
                    }

                    node = new Task<>(
                        field, null, supplier
                    );

                    setup(
                        name, node
                    );
                    super.put(
                        name, node
                    );
                    continue;
                } else {
                    keys = expose.value();
                    if (keys.length != 0) {
                        name = keys[0];
                    } else {
                        name = field.getName();
                    }

                    if (containsKey(name)) {
                        continue;
                    }

                    node = new Task<>(
                        field, expose, supplier
                    );
                }

                // check whether to use direct index
                if (direct) {
                    int i = node.getIndex();
                    if (i >= 0) {
                        super.put(i, node);
                    }
                }

                if (keys.length == 0) {
                    if ((expose.mode() &
                        Expose.HIDDEN) == 0) {
                        setup(
                            name, node
                        );
                    }
                    super.put(
                        name, node
                    );
                } else {
                    // register only the first alias
                    if ((expose.mode() &
                        Expose.HIDDEN) == 0) {
                        setup(
                            name, node
                        );
                    }

                    for (String alias : keys) {
                        // check empty
                        if (!alias.isEmpty()) {
                            super.put(
                                alias, node
                            );
                        }
                    }
                }
            } catch (Exception e) {
                // Nothing
            }
    }

    /**
     * @param methods the specified {@link Method} collection
     */
    private void onMethods(
        @NotNull Method[] methods
    ) {
        boolean sealed = (flags & Embed.SEALED) != 0;
        boolean direct = (flags & Embed.DIRECT) != 0;

        for (Method method : methods)
            try {
                int count = method.
                    getParameterCount();
                if (count > 1) {
                    continue;
                }

                // its modifier
                int mod = method.getModifiers();

                // filter invalid
                if ((mod & Modifier.STATIC) != 0 ||
                    (mod & Modifier.ABSTRACT) != 0) {
                    continue;
                }

                Task<T> node;
                Expose expose = method
                    .getAnnotation(
                        Expose.class
                    );

                if (expose == null) {
                    // check flag
                    if (sealed) {
                        continue;
                    }

                    // filter invalid
                    if ((mod & Modifier.PUBLIC) == 0) {
                        continue;
                    }
                } else {
                    String[] keys = expose.value();
                    if (keys.length != 0) {
                        if (count == 0) {
                            if (contains(keys[0])) {
                                continue;
                            }

                            node = new Task<>(
                                method, expose, supplier
                            );

                            // register all aliases
                            for (int i = 0; i < keys.length; i++) {
                                setup(
                                    keys[i], i == 0 ? node : new Task<>(node)
                                );
                            }
                        } else {
                            if (containsKey(keys[0])) {
                                continue;
                            }

                            node = new Task<>(
                                method, expose, supplier
                            );

                            // check whether to use direct index
                            if (direct) {
                                int i = node.getIndex();
                                if (i >= 0) {
                                    super.put(i, node);
                                }
                            }

                            for (String alias : keys) {
                                // check empty
                                if (!alias.isEmpty()) {
                                    super.put(
                                        alias, node
                                    );
                                }
                            }
                        }
                        continue;
                    }
                }

                byte[] name = Reflect
                    .alias(method);
                if (name == null) {
                    continue;
                }

                if (count == 0) {
                    String id = Binary
                        .ascii(name);
                    if (contains(id)) {
                        continue;
                    }

                    setup(
                        id, new Task<>(
                            method, expose, supplier
                        )
                    );
                } else {
                    Alias id = Binary
                        .alias(name);
                    if (containsKey(id)) {
                        continue;
                    }

                    node = new Task<>(
                        method, expose, supplier
                    );

                    super.put(
                        id, node
                    );

                    // check whether to use direct index
                    if (direct) {
                        int i = node.getIndex();
                        if (i >= 0) {
                            super.put(i, node);
                        }
                    }
                }
            } catch (Exception e) {
                // Nothing
            }
    }

    /**
     * @param constructors the specified {@link Constructor} collection
     */
    private void onConstructors(
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
            builder = (Constructor<T>) b;

            args = b.getParameterTypes();
            if ($ != null) {
                int i = $.getParameterCount();
                if (i + 2 == count && args[i] == int.class &&
                    "kotlin.jvm.internal.DefaultConstructorMarker".equals(args[i + 1].getName())) {
                    b = $;
                    edge = 2;
                    args = $.getParameterTypes();
                }
            }

            Type[] ts = b.getGenericParameterTypes();
            Annotation[][] as = b.getParameterAnnotations();

            int i = 0, j = as.length - args.length;
            Class<?> declaringClass = klass.getDeclaringClass();

            if (declaringClass != null &&
                (klass.getModifiers() & Modifier.STATIC) == 0) {
                if (declaringClass == args[0]) {
                    i++;
                    master = declaringClass;
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

                Item item = new Item(i);
                item.setRawType(ts[i]);
                Class<?> type = args[i];

                if (type.isPrimitive()) {
                    item.setType(
                        Reflect.wrap(type)
                    );
                    item.setCoder(
                        Reflect.activate(expose, supplier)
                    );
                } else {
                    item.setType(type);
                    if (format != null) {
                        item.setCoder(
                            Reflect.activate(type, format)
                        );
                    } else {
                        item.setCoder(
                            Reflect.activate(expose, supplier)
                        );
                    }
                }

                if (expose == null) {
                    if (ps == null) {
                        ps = b.getParameters();
                    }
                    params.put(
                        ps[i].getName(), item
                    );
                } else {
                    String[] keys = expose.value();
                    for (String key : keys) {
                        params.put(
                            key, item
                        );
                    }
                }
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.3
     */
    public static class Task<K> extends Node<K, Object> {

        final MethodHandle getter;
        final MethodHandle setter;

        public Task(
            Task<K> node
        ) {
            super(node);
            getter = node.getter;
            setter = node.setter;
        }

        public Task(
            Field field,
            Expose expose,
            Supplier supplier
        ) throws IllegalAccessException {
            super(expose, field, supplier);
            field.setAccessible(true);
            getter = lookup.unreflectGetter(field);
            setter = lookup.unreflectSetter(field);
        }

        public Task(
            Method method,
            Expose expose,
            Supplier supplier
        ) throws IllegalAccessException {
            super(expose, method, supplier);
            method.setAccessible(true);
            if (method.getParameterCount() == 0) {
                setter = null;
                getter = lookup.unreflect(method);
            } else {
                getter = null;
                setter = lookup.unreflect(method);
            }
        }

        @Override
        public Object apply(
            @NotNull K bean
        ) {
            try {
                return getter.invoke(bean);
            } catch (Throwable e) {
                throw new CallCrash(e);
            }
        }

        @Override
        public boolean accept(
            @NotNull K bean,
            @Nullable Object value
        ) {
            if (value != null || (flags & Expose.NOTNULL) == 0) {
                try {
                    setter.invoke(
                        bean, value
                    );
                    return true;
                } catch (Throwable e) {
                    throw new CallCrash(e);
                }
            }
            return false;
        }
    }
}
