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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            node = node.later;
        }
    }

    @Override
    public Spoiler flat(
        @NotNull T bean
    ) {
        assert bean != null;
        return new Iter<>(
            bean, this
        );
    }

    @Override
    public boolean flat(
        @NotNull T bean,
        @NotNull Visitor visitor
    ) {
        assert bean != null;
        Node<T, ?> node = head;
        while (node != null) {
            Object val = node.apply(bean);
            if (val != null || (node.flags & Expose.NOTNULL) == 0) {
                visitor.visit(
                    node.key, val
                );
            }
            node = node.later;
        }
        return true;
    }

    /**
     * @param key  the ket of {@link Node}
     * @param node the specified {@link Node}
     * @return {@code true} if the node is settled otherwise {@code false}
     * @throws CallCrash            If the {@code node} is already used
     * @throws NullPointerException If the {@code key} or {@code node} is null
     * @since 0.0.3
     */
    @SuppressWarnings("unchecked")
    public boolean setup(
        @NotNull String key,
        @NotNull Node<T, ?> node
    ) {
        if (node.key != null) {
            throw new CallCrash(
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

        node.key = key;
        node.hash = hash;

        if (tail == null) {
            head = node;
            tail = node;
        } else if (node.index < 0) {
            tail.later = node;
            tail = node;
        } else {
            Node<T, ?> m = head;
            Node<T, ?> n = null;

            while (true) {
                if (m.index < 0) {
                    node.later = m;
                    if (m == head) {
                        head = node;
                    }
                    break;
                }

                if (m.index > node.index) {
                    if (n == null) {
                        head = node;
                    } else {
                        n.later = node;
                    }
                    node.later = m;
                    break;
                } else {
                    n = m;
                    m = m.later;
                    if (m == null) {
                        tail = node;
                        n.later = node;
                        break;
                    }
                }
            }
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
            node = node.later;
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
            supplier, data
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public T cast(
        @NotNull Supplier supplier,
        @Nullable Object data
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

        return convert(
            data, supplier
        );
    }

    @NotNull
    @Override
    public T apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) throws CallCrash {
        try {
            T bean = apply(
                Alias.EMPTY
            );
            update(
                bean, spoiler, supplier
            );
            return bean;
        } catch (CallCrash e) {
            throw e;
        } catch (Throwable e) {
            throw new CallCrash(
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
        @NotNull Object result,
        @NotNull Supplier supplier
    ) {
        Spoiler spoiler =
            supplier.flat(result);
        if (spoiler != null) {
            try {
                T bean = apply(
                    Alias.EMPTY
                );
                update(
                    bean, spoiler, supplier
                );
                return bean;
            } catch (Exception e) {
                // Nothing
            }
        }
        return null;
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
                next = n.later;
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

        /**
         * @param index the specified {@code index}
         */
        public Item(
            int index
        ) {
            this.index = index;
        }

        /**
         * @param item the specified {@link Item}
         */
        public Item(
            @NotNull Item item
        ) {
            flags = item.flags;
            index = item.index;
            coder = item.coder;
            klass = item.klass;
            actual = item.actual;
        }

        /**
         * @param expose the specified {@link Expose}
         */
        public Item(
            Expose expose
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

        private int hash;
        private String key;

        private Node<K, ?> next;
        private Node<K, ?> later;

        /**
         * @param index the specified {@code index}
         */
        protected Node(
            int index
        ) {
            super(index);
        }

        /**
         * @param Item the specified {@link Item}
         */
        protected Node(
            Item Item
        ) {
            super(Item);
        }

        /**
         * @param expose the specified {@link Expose}
         */
        protected Node(
            Expose expose
        ) {
            super(expose);
        }

        /**
         * @param expose the specified {@link Expose}
         */
        protected Node(
            Expose expose,
            Field field,
            Supplier supplier
        ) {
            this(expose);
            klass = field.getType();
            actual = field.getGenericType();

            if (klass.isPrimitive()) {
                klass = Reflect.wrap(klass);
                coder = Reflect.activate(expose, supplier);
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

        /**
         * @param expose the specified {@link Expose}
         */
        protected Node(
            Expose expose,
            Method method,
            Supplier supplier
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
                klass = Reflect.wrap(klass);
                coder = Reflect.activate(expose, supplier);
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
}
