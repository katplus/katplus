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

import java.io.IOException;
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
public final class ReflectSpare<T> extends Workman<T> implements Maker<T>, Worker<T> {

    private MethodHandle handle;
    private Constructor<T> builder;

    private Class<?> owner;
    private boolean marker;

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

    @NotNull
    @Override
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

    @NotNull
    @Override
    public T apply(
        @NotNull Alias alias,
        @NotNull Object... data
    ) throws Crash {
        if (marker) {
            int i = 0, flag = 0;
            for (; i < args.length; i++) {
                if (data[i] == null) {
                    flag |= (1 << i);
                    Class<?> c = args[i];
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
            return builder.newInstance(data);
        } catch (Throwable e) {
            throw new Crash(e);
        }
    }

    @Nullable
    @Override
    public T apply(
        @NotNull Supplier supplier,
        @NotNull Map<?, ?> data
    ) throws Crash {
        if (params == null) {
            return compose(
                supplier, data
            );
        } else {
            if (size() != 0 || owner != null) {
                throw new Crash(
                    "Not currently supported"
                );
            }
            return compose(
                supplier, new Object[args.length], data
            );
        }
    }

    @NotNull
    @Override
    public T apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        if (params == null) {
            return compose(
                supplier, resultSet
            );
        } else {
            if (size() != 0 || owner != null) {
                throw new SQLCrash(
                    "Not currently supported"
                );
            }
            return compose(
                supplier, new Object[args.length], resultSet
            );
        }
    }

    @Override
    @Nullable
    public Builder<T> getBuilder(
        @Nullable Type type
    ) {
        if (params == null) {
            return new Builder0<>(this);
        }
        return new Builder2<>(this);
    }

    @Nullable
    @Override
    public Target target(
        Object alias
    ) {
        if (params == null) {
            return null;
        }
        return params.get(alias);
    }

    @Nullable
    @Override
    public Target target(
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

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public Setter<T, ?> setter(
        @NotNull Object alias
    ) {
        return (Setter<T, ?>) get(alias);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
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
                if ((mod & Modifier.PUBLIC) == 0 ||
                    (mod & Modifier.TRANSIENT) != 0) {
                    continue;
                }
            }

            Task<T> node;
            try {
                node = new Task<>(
                    field, expose, supplier
                );
            } catch (Throwable e) {
                continue;
            }

            if (expose == null) {
                String name = field.getName();
                setup(
                    name, node
                );
                super.put(
                    name, node
                );
                continue;
            }

            // check whether to use direct index
            if (direct) {
                int index = node.getIndex();
                if (index > -1) {
                    super.put(
                        index, node
                    );
                }
            }

            String[] keys = expose.value();
            if (keys.length == 0) {
                String name = field.getName();
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
                        keys[0], node
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

                // check its modifier
                if ((mod & Modifier.PUBLIC) == 0) {
                    continue;
                }
            } else {
                String[] keys = expose.value();
                if (keys.length != 0) {
                    try {
                        node = new Task<>(
                            method, expose, supplier
                        );
                    } catch (Throwable e) {
                        continue;
                    }

                    if (count != 0) {
                        // check whether to use direct index
                        if (direct) {
                            int index = node.getIndex();
                            if (index > -1) {
                                super.put(
                                    index, node
                                );
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
                    } else {
                        // register all aliases
                        for (int i = 0; i < keys.length; i++) {
                            setup(
                                keys[i], i == 0 ? node : new Task<>(node)
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
                key.getBytes(i, l, name, 1);
            }

            try {
                node = new Task<>(
                    method, expose, supplier
                );
            } catch (Throwable e) {
                continue;
            }

            if (count == 0) {
                setup(
                    Binary.ascii(name), node
                );
            } else {
                super.put(
                    Binary.alias(name), node
                );

                // check whether to use direct index
                if (direct) {
                    int index = node.getIndex();
                    if (index > -1) {
                        super.put(
                            index, node
                        );
                    }
                }
            }
        }
    }

    /**
     * @param constructors the specified {@link Constructor} collection
     */
    @SuppressWarnings("unchecked")
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
        protected Class<?>[] args;

        protected Target target;
        protected Setter<K, ?> setter;

        protected Cache<K> cache;
        protected Worker<K> worker;

        public Builder2(
            @NotNull ReflectSpare<K> spare
        ) {
            this.worker = spare;
            this.owner = spare.owner;
            this.args = spare.args;
            this.marker = spare.marker;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOException {
            int size = args.length;
            if (marker) {
                size += 2;
            }
            data = new Object[size];
            if (owner != null) {
                Object own = getParent()
                    .getResult();
                if (own != null) {
                    data[count++] = own;
                    if (args.length == 1) {
                        this.embark();
                    }
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
        ) throws IOException {
            if (entity != null) {
                setter.onAccept(
                    entity, value
                );
            } else if (target != null) {
                int i = target.getIndex();
                target = null;
                data[i] = value;
                if (args.length == ++count) {
                    try {
                        this.embark();
                    } catch (Crash e) {
                        throw new IOException(e);
                    }
                }
            } else {
                Cache<K> c = new Cache<>();
                c.value = value;
                c.setter = setter;

                setter = null;
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
        ) throws IOException {
            onAccept(
                null, child.getResult()
            );
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
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
        ) throws IOException {
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

        private void embark() throws Crash {
            entity = worker.apply(
                getAlias(), data
            );
            while (cache != null) {
                cache.setter.onAccept(
                    entity, cache.value
                );
                cache = cache.next;
            }
        }

        @Override
        public K getResult() {
            if (marker && entity == null) {
                try {
                    this.embark();
                } catch (Exception e) {
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
