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
package plus.kat.spare;

import plus.kat.anno.*;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.utils.*;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static plus.kat.utils.Reflect.lookup;

/**
 * @author kraity
 * @since 0.0.3
 */
public abstract class Workman<T> extends KatMap<Object, Object> implements Worker<T> {

    protected Provider provider;
    protected Supplier supplier;

    protected Node<T, ?>[] table;
    protected Node<T, ?> head, tail;

    protected int flags;
    protected final String space;
    protected final Class<T> klass;

    protected Workman(
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        this(
            klass.getAnnotation(Embed.class),
            klass, supplier, null
        );
    }

    protected Workman(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier,
        @Nullable Provider provider
    ) {
        this.klass = klass;
        this.provider = provider;
        this.supplier = supplier;

        if (embed != null) {
            flags = embed.mode();
        }

        initialize();
        space = supplier.register(
            embed, klass, this
        );
    }

    /**
     * Initialize the build job
     */
    protected void initialize() {
        // Nothing
    }

    @Override
    public String getSpace() {
        return space;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz.isAssignableFrom(klass);
    }

    @Override
    public Boolean getFlag() {
        return Boolean.TRUE;
    }

    @Override
    public Class<T> getType() {
        return klass;
    }

    @Override
    public Provider getProvider() {
        return provider;
    }

    @Override
    public T read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (flag.isFlag(Flag.STRING_AS_OBJECT)) {
            return Casting.cast(
                this, value, flag, supplier
            );
        }
        return null;
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        Node<T, ?> node = head;
        while (node != null) {
            Object val = node.call(value);
            if (val == null) {
                if ((node.flags & Expose.NOTNULL) == 0) {
                    chan.set(node.key, null);
                }
            } else {
                if ((node.flags & Expose.UNWRAPPED) == 0) {
                    chan.set(
                        node.key, node.coder, val
                    );
                } else {
                    Coder<?> coder = node.coder;
                    if (coder != null) {
                        coder.write(chan, val);
                    } else {
                        coder = chan.getSupplier()
                            .lookup(val.getClass());
                        if (coder != null) {
                            coder.write(chan, val);
                        }
                    }
                }
            }
            node = node.near;
        }
    }

    @Override
    public Spoiler flat(
        @NotNull T bean
    ) {
        return new Iter<>(
            bean, this
        );
    }

    @Override
    public boolean flat(
        @NotNull T bean,
        @NotNull Visitor visitor
    ) {
        Node<T, ?> node = head;
        while (node != null) {
            Object val = node.apply(bean);
            if (val != null || (node.flags & Expose.NOTNULL) == 0) {
                visitor.visit(
                    node.key, val
                );
            }
            node = node.near;
        }
        return true;
    }

    /**
     * @param key the alias of getter
     * @since 0.0.3
     */
    @Override
    public Getter<T, ?> get(
        @NotNull Object key
    ) {
        Node<T, ?>[] tab = table;
        if (tab == null) {
            return null;
        }

        int h = key.hashCode() & 0xFFFF;
        Node<T, ?> e = tab[h % tab.length];

        while (e != null) {
            if (e.hash == h &&
                key.equals(e.key)) {
                return e;
            }
            e = e.next;
        }

        return null;
    }

    /**
     * @param alias the alias of getter
     * @since 0.0.3
     */
    @Override
    public Getter<T, ?> get(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        if (alias.isNotEmpty()) {
            return get(alias);
        }

        if (index < 0) {
            return null;
        }

        Node<T, ?> node = head;
        while (node != null) {
            if (index == 0) {
                return node;
            }
            index--;
            node = node.near;
        }

        return null;
    }

    /**
     * @param key the alias of getter
     * @since 0.0.3
     */
    public boolean contains(
        @NotNull Object key
    ) {
        Node<T, ?>[] tab = table;
        if (tab == null) {
            return false;
        }

        int h = key.hashCode() & 0xFFFF;
        Node<T, ?> e = tab[h % tab.length];

        while (e != null) {
            if (e.hash == h &&
                key.equals(e.key)) {
                return true;
            }
            e = e.next;
        }

        return false;
    }

    @Override
    public T cast(
        @Nullable Object data
    ) {
        return cast(
            data, supplier
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public T cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data == null) {
            return null;
        }

        if (klass.isInstance(data)) {
            return (T) data;
        }

        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, null, supplier
            );
        }

        try {
            return convert(
                data, supplier
            );
        } catch (Exception e) {
            return null;
        }
    }

    @NotNull
    @Override
    public T apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) throws Collapse {
        try {
            T bean = apply(
                Alias.EMPTY
            );
            update(
                bean, spoiler, supplier
            );
            return bean;
        } catch (Collapse e) {
            throw e;
        } catch (Throwable e) {
            throw new Collapse(
                "Error creating " + getType(), e
            );
        }
    }

    @NotNull
    @Override
    public T apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        try {
            T bean = apply(
                Alias.EMPTY
            );
            update(
                bean, supplier, resultSet
            );
            return bean;
        } catch (SQLException e) {
            throw e;
        } catch (Throwable e) {
            throw new SQLCrash(
                "Error creating " + getType(), e
            );
        }
    }

    @Nullable
    public T convert(
        @NotNull Object data,
        @NotNull Supplier supplier
    ) throws Exception {
        if (data instanceof Map) {
            return apply(
                Spoiler.of(
                    (Map<?, ?>) data
                ), supplier
            );
        }

        if (data instanceof Spoiler) {
            return apply(
                (Spoiler) data, supplier
            );
        }

        if (data instanceof ResultSet) {
            return apply(
                supplier, (ResultSet) data
            );
        }

        Spoiler spoiler =
            supplier.flat(data);
        if (spoiler == null) {
            return null;
        }

        return apply(spoiler, supplier);
    }

    /**
     * Returns true if the node is settled otherwise false
     *
     * @since 0.0.4
     */
    protected boolean display(
        @NotNull String key,
        @NotNull Node<T, ?> node
    ) {
        return display(
            0, key, node
        );
    }

    /**
     * Returns true if the node is settled otherwise false
     *
     * @since 0.0.4
     */
    @SuppressWarnings("unchecked")
    protected boolean display(
        @NotNull int grade,
        @NotNull String key,
        @NotNull Node<T, ?> node
    ) {
        if (node.key != null) {
            throw new Collapse(
                node + " is already used"
            );
        }

        Node<T, ?>[] tab = table;
        if (tab == null) {
            tab = table = new Node[6];
        }

        int i, hash = key.hashCode() & 0xFFFF;
        Node<T, ?> e = tab[i = (hash % tab.length)];

        if (e == null) {
            tab[i] = node;
        } else {
            while (true) {
                if (e.hash == hash &&
                    key.equals(e.key)) {
                    return false;
                }
                if (e.next != null) {
                    e = e.next;
                } else {
                    e.next = node;
                    break;
                }
            }
        }

        int u = node.index;
        node.grade = grade;

        node.key = key;
        node.hash = hash;

        Node<T, ?> m = head;
        Node<T, ?> n = null;

        if (m == null) {
            head = node;
            if (u == -1) {
                tail = node;
            }
            return true;
        }

        if (u == -1 && grade == 0) {
            if (m.index < -1) {
                head = node;
                tail = node;
                node.near = m;
            } else {
                n = tail;
                tail = node;
                if (n == null) {
                    do {
                        n = m;
                        m = m.near;
                    } while (
                        m != null
                    );
                } else {
                    node.near = n.near;
                }
                n.near = node;
            }
        } else {
            if (u < 0) {
                int x;
                if (u < -1) {
                    m = tail;
                    if (m == null) m = head;
                }
                do {
                    if ((x = m.index) < u ||
                        (x == u && grade > m.grade)) {
                        if (n == null) {
                            head = node;
                        } else {
                            n.near = node;
                        }
                        node.near = m;
                        return true;
                    }
                } while (
                    (m = (n = m).near) != null
                );
            } else {
                do {
                    int x = m.index;
                    if ((x < 0 || u < x) ||
                        (x == u && grade > m.grade)) {
                        if (n == null) {
                            head = node;
                        } else {
                            n.near = node;
                        }
                        node.near = m;
                        return true;
                    }
                } while (
                    (m = (n = m).near) != null
                );
            }
            n.near = node;
        }

        return true;
    }

    /**
     * @author kraity
     * @since 0.0.3
     */
    public static class Iter<K>
        implements Spoiler {

        protected Node<K, ?> node;
        protected Node<K, ?> next;

        protected K bean;
        protected Workman<K> workman;

        public Iter(
            @NotNull K bean,
            @NotNull Workman<K> workman
        ) {
            this.bean = bean;
            next = workman.head;
            this.workman = workman;
        }

        @Override
        public String getKey() {
            return node.key;
        }

        @Override
        public Object getValue() {
            return node.apply(bean);
        }

        @Override
        public boolean hasNext() {
            Node<K, ?> n = next;
            if (n != null) {
                node = n;
                next = n.near;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    public static class Item
        extends Entry<Object, Item> implements Target {

        protected Type actual;
        protected Class<?> klass;

        protected Coder<?> coder;
        protected int flags;
        protected final int index;

        public Item(
            int index
        ) {
            this.index = index;
        }

        public Item(
            @NotNull Item item
        ) {
            flags = item.flags;
            index = item.index;
            coder = item.coder;
            klass = item.klass;
            actual = item.actual;
        }

        public Item(
            @Nullable Expose expose
        ) {
            if (expose == null) {
                index = -1;
            } else {
                flags = expose.mode();
                index = expose.index();
            }
        }

        /**
         * Returns the index of {@link Item}
         */
        @Override
        public int getIndex() {
            return index;
        }

        /**
         * Returns the {@code klass} of {@link Item}
         */
        @Override
        public Class<?> getType() {
            return klass;
        }

        /**
         * Sets the {@code klass} of {@link Item}
         *
         * @param type the specified type
         * @since 0.0.4
         */
        public void setType(
            @NotNull Class<?> type
        ) {
            if (type != null) {
                klass = type;
            }
        }

        /**
         * Returns the {@code actual} of {@link Item}
         */
        @Override
        public Type getRawType() {
            return actual;
        }

        /**
         * Sets the {@code actual} of {@link Item}
         *
         * @param type the specified type
         * @since 0.0.4
         */
        public void setRawType(
            @NotNull Type type
        ) {
            if (type != null) {
                actual = type;
            }
        }

        /**
         * Returns the flags of {@link Item}
         *
         * @since 0.0.4
         */
        public int getFlags() {
            return flags;
        }

        /**
         * Sets the {@code flags} of {@link Item}
         *
         * @param target the specified flags
         * @since 0.0.4
         */
        public void setFlags(
            int target
        ) {
            flags = target;
        }

        /**
         * Returns the {@code coder} of {@link Item}
         */
        @Override
        public Coder<?> getCoder() {
            return coder;
        }

        /**
         * Sets the {@code coder} of {@link Item}
         *
         * @param target the specified coder
         * @since 0.0.4
         */
        public void setCoder(
            @NotNull Coder<?> target
        ) {
            if (target != null) {
                coder = target;
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    public static abstract class Node<K, V>
        extends Item implements Setter<K, V>, Getter<K, V> {

        private Node<K, ?> near;
        private Node<K, ?> next;

        private String key;
        private int hash, grade;

        protected Node(
            int index
        ) {
            super(index);
        }

        protected Node(
            @NotNull Item Item
        ) {
            super(Item);
        }

        protected Node(
            @Nullable Expose expose
        ) {
            super(expose);
        }

        protected Node(
            @Nullable Expose expose,
            @NotNull Field field,
            @NotNull Supplier supplier
        ) {
            this(expose);
            klass = field.getType();
            actual = field.getGenericType();

            if (klass.isPrimitive()) {
                flags |= Expose.NOTNULL;
                klass = Reflect.wrap(klass);
                coder = Reflect.activate(
                    expose, supplier
                );
            } else {
                Format format = field
                    .getAnnotation(
                        Format.class
                    );
                if (format != null) {
                    coder = Reflect.activate(
                        klass, format
                    );
                } else {
                    coder = Reflect.activate(
                        expose, supplier
                    );
                }
            }
        }

        protected Node(
            @Nullable Expose expose,
            @NotNull Method method,
            @NotNull Supplier supplier
        ) {
            this(expose);
            switch (method.getParameterCount()) {
                case 0: {
                    actual = klass = method.getReturnType();
                    break;
                }
                case 1: {
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
                flags |= Expose.NOTNULL;
                klass = Reflect.wrap(klass);
                coder = Reflect.activate(
                    expose, supplier
                );
            } else {
                Format format = method
                    .getAnnotation(
                        Format.class
                    );
                if (format != null) {
                    coder = Reflect.activate(
                        klass, format
                    );
                } else {
                    coder = Reflect.activate(
                        expose, supplier
                    );
                }
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Edge<K> extends Node<K, Object> {

        protected final MethodHandle getter;
        protected final MethodHandle setter;

        public Edge(
            @NotNull Edge<K> edge
        ) {
            super(edge);
            getter = edge.getter;
            setter = edge.setter;
        }

        public Edge(
            @Nullable Expose expose,
            @NotNull Field field,
            @NotNull Supplier supplier
        ) throws IllegalAccessException {
            super(expose, field, supplier);
            field.setAccessible(true);
            getter = lookup.unreflectGetter(field);
            setter = lookup.unreflectSetter(field);
        }

        public Edge(
            @Nullable Expose expose,
            @NotNull Method method,
            @NotNull Supplier supplier
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
            MethodHandle method = getter;
            if (method == null) {
                throw new Collapse(
                    "Getter is not supported"
                );
            } else {
                try {
                    return method.invoke(bean);
                } catch (Throwable e) {
                    throw new Collapse(
                        "Edge call 'invoke' failed", e
                    );
                }
            }
        }

        @Override
        public boolean accept(
            @NotNull K bean,
            @Nullable Object value
        ) {
            MethodHandle method = setter;
            if (method == null) {
                throw new Collapse(
                    "Setter is not supported"
                );
            }
            if (value != null || (flags & Expose.NOTNULL) == 0) {
                try {
                    method.invoke(
                        bean, value
                    );
                    return true;
                } catch (Throwable e) {
                    throw new Collapse(
                        "Edge call 'invoke' failed", e
                    );
                }
            }
            return false;
        }
    }
}
