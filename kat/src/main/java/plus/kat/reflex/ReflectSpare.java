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
import java.util.Map;

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
@SuppressWarnings("unchecked")
public final class ReflectSpare<T> extends Workman<T> implements Maker<T>, Worker<T> {

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
            throw new Crash(e);
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

        if (as.length !=
            data.length) {
            data[i] = flag;
        }

        try {
            return b.newInstance(data);
        } catch (Throwable e) {
            throw new Crash(e);
        }
    }

    @Override
    public T apply(
        @NotNull Supplier supplier,
        @NotNull Map<?, ?> data
    ) throws Crash {
        if (params == null) {
            return super.apply(
                supplier, data
            );
        } else {
            if (size() != 0 || master != null) {
                throw new Crash(
                    "Not currently supported"
                );
            }
            return super.apply(
                supplier, new Object[args.length], data
            );
        }
    }

    @Override
    public T apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        if (params == null) {
            return super.apply(
                supplier, resultSet
            );
        } else {
            if (size() != 0 || master != null) {
                throw new SQLCrash(
                    "Not currently supported"
                );
            }
            return super.apply(
                supplier, new Object[args.length], resultSet
            );
        }
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
    public Target target(
        Object alias
    ) {
        KatMap<Object, Target> map = params;
        return map == null ?
            null : map.get(alias);
    }

    @Override
    public Target target(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        KatMap<Object, Target> map = params;
        return map == null ?
            null : map.get(
            alias.isEmpty() ? index : alias
        );
    }

    @Override
    public Setter<T, ?> setter(
        @NotNull Object alias
    ) {
        return (Setter<T, ?>) get(alias);
    }

    @Override
    public Setter<T, ?> setter(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return (Setter<T, ?>) get(
            alias.isEmpty() ? index : alias
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
                    if (expose.export()) {
                        setup(
                            name, node
                        );
                    }
                    super.put(
                        name, node
                    );
                } else {
                    // register only the first alias
                    if (expose.export()) {
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
    @SuppressWarnings("deprecation")
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

                String key = method.getName();
                int k = 0, l = key.length();
                if (l < 4) {
                    continue;
                }

                char ch = key.charAt(k++);
                if (ch == 's') {
                    if (count == 0 ||
                        key.charAt(k++) != 'e' ||
                        key.charAt(k++) != 't') {
                        continue;
                    }
                } else if (ch == 'g') {
                    if (count != 0 ||
                        key.charAt(k++) != 'e' ||
                        key.charAt(k++) != 't') {
                        continue;
                    }
                } else if (ch == 'i') {
                    if (count != 0 ||
                        key.charAt(k++) != 's') {
                        continue;
                    }
                } else {
                    continue;
                }

                byte[] name;
                char c1 = key.charAt(k++);
                if (c1 < 'A' || 'Z' < c1) {
                    continue;
                }

                if (k == l) {
                    name = new byte[]{
                        (byte) (c1 + 0x20)
                    };
                } else {
                    // See: java.beans.Introspector#decapitalize(String)
                    char c2 = key.charAt(k);
                    if (c2 < 'A' || 'Z' < c2) {
                        c1 += 0x20;
                    }

                    name = new byte[l - k + 1];
                    name[0] = (byte) c1;
                    key.getBytes(k, l, name, 1);
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
            Class<?> enclosingClass = klass.getEnclosingClass();

            if (enclosingClass != null &&
                (klass.getModifiers() & Modifier.STATIC) == 0) {
                if (enclosingClass == args[0]) {
                    i++;
                    master = enclosingClass;
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

                Coder<?> coder;
                Class<?> type = args[i];
                if (type.isPrimitive()) {
                    type = Reflect.wrap(type);
                    coder = Reflect.activate(expose, supplier);
                } else {
                    if (format != null) {
                        coder = Reflect.activate(type, format);
                    } else {
                        coder = Reflect.activate(expose, supplier);
                    }
                }

                Item item = new Item(
                    i, type, ts[i], coder
                );

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
    public static class Task<K>
        extends Node<K> implements Setter<K, Object> {

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
            super(expose);
            field.setAccessible(true);
            getter = lookup.unreflectGetter(field);
            setter = lookup.unreflectSetter(field);

            klass = field.getType();
            actual = field.getGenericType();

            if (klass.isPrimitive()) {
                klass = Reflect.wrap(klass);
                coder = Reflect.activate(expose, supplier);
            } else {
                nullable = field.getAnnotation(NotNull.class) == null;
                unwrapped = field.getAnnotation(Unwrapped.class) != null;

                Format format = field
                    .getAnnotation(
                        Format.class
                    );
                if (format != null) {
                    coder = Reflect.activate(klass, format);
                } else {
                    coder = Reflect.activate(expose, supplier);
                }
            }
        }

        public Task(
            Method method,
            Expose expose,
            Supplier supplier
        ) throws IllegalAccessException {
            super(expose);
            method.setAccessible(true);
            switch (method.getParameterCount()) {
                case 0: {
                    setter = null;
                    getter = lookup.unreflect(method);
                    actual = klass = method.getReturnType();
                    break;
                }
                case 1: {
                    getter = null;
                    setter = lookup.unreflect(method);
                    klass = method.getParameterTypes()[0];
                    actual = method.getGenericParameterTypes()[0];
                    break;
                }
                default: {
                    throw new NullPointerException(
                        "Unexpectedly, the parameter length of '" + method.getName() + "' is greater than '1'"
                    );
                }
            }

            if (klass.isPrimitive()) {
                klass = Reflect.wrap(klass);
                coder = Reflect.activate(expose, supplier);
            } else {
                nullable = method.getAnnotation(NotNull.class) == null;
                unwrapped = method.getAnnotation(Unwrapped.class) != null;

                Format format = method
                    .getAnnotation(
                        Format.class
                    );
                if (format != null) {
                    coder = Reflect.activate(klass, format);
                } else {
                    coder = Reflect.activate(expose, supplier);
                }
            }
        }

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
    }
}
