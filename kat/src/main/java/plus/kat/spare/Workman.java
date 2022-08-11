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

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.utils.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author kraity
 * @since 0.0.3
 */
public abstract class Workman<T, E> extends KatMap<Object, E> implements Worker<T> {

    protected Provider provider;
    protected Supplier supplier;

    protected int flags;
    protected Node<T> head, tail;

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
            flags = embed.claim();
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

    @NotNull
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

    @Nullable
    @Override
    public Boolean getFlag() {
        return Boolean.TRUE;
    }

    @NotNull
    @Override
    public Class<T> getType() {
        return klass;
    }

    @Nullable
    @Override
    public Provider getProvider() {
        return provider;
    }

    @Nullable
    public T apply(
        @NotNull Supplier supplier,
        @NotNull Map<?, ?> data
    ) throws Crash {
        return null;
    }

    @Override
    public T cast(
        @Nullable Object data
    ) {
        return cast(
            supplier, data
        );
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public T cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        Class<?> clazz = data.getClass();
        if (klass.isAssignableFrom(clazz)) {
            return (T) data;
        }

        if (data instanceof Map) try {
            return apply(
                supplier, (Map<?, ?>) data
            );
        } catch (Exception e) {
            return null;
        }

        if (data instanceof ResultSet) try {
            return apply(
                supplier, (ResultSet) data
            );
        } catch (Exception e) {
            return null;
        }

        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, null, supplier
            );
        }

        return null;
    }

    @Override
    public T read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOCrash {
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
        Node<T> node = head;
        while (node != null) {
            Object val = node.onApply(value);
            if (val == null) {
                if (node.nullable) {
                    chan.set(node.key, null);
                }
            } else {
                if (node.unwrapped) {
                    Coder<?> coder = node.coder;
                    if (coder != null) {
                        coder.write(chan, val);
                    } else {
                        coder = chan.getSupplier()
                            .lookup(
                                val.getClass()
                            );
                        if (coder != null) {
                            coder.write(chan, val);
                        }
                    }
                } else {
                    chan.set(
                        node.key, node.coder, val
                    );
                }
            }
            node = node.next;
        }
    }

    @Override
    public boolean flat(
        @NotNull T bean,
        @NotNull BiConsumer<String, Object> action
    ) {
        Node<T> node = head;
        while (node != null) {
            Object val = node.onApply(bean);
            if (val != null || node.nullable) {
                action.accept(
                    node.key, val
                );
            }
            node = node.next;
        }
        return true;
    }

    /**
     * @param supplier the specified supplier
     * @param result   the specified result
     * @since 0.0.3
     */
    @NotNull
    public T compose(
        @NotNull Supplier supplier,
        @NotNull Map<?, ?> result
    ) throws Crash {
        T entity = apply(
            Alias.EMPTY
        );

        for (Map.Entry<?, ?> entry : result.entrySet()) {
            // get its key
            Object key = entry.getKey();
            if (key == null) {
                continue;
            }

            // try lookup
            Setter<T, ?> setter = setter(key);
            if (setter == null) {
                continue;
            }

            // get the value
            Object val = entry.getValue();
            if (val == null) {
                continue;
            }

            // get class specified
            Class<?> type = val.getClass();
            Class<?> klass = setter.getType();

            // update field
            if (klass.isAssignableFrom(type)) {
                setter.onAccept(
                    entity, val
                );
                continue;
            }

            // get spare specified
            Spare<?> spare = supplier.lookup(klass);

            // update field
            if (spare != null) {
                setter.onAccept(
                    entity, spare.cast(
                        supplier, val
                    )
                );
            }
        }

        return entity;
    }

    /**
     * @param supplier the specified supplier
     * @param data     the specified params
     * @param result   the specified result
     * @since 0.0.3
     */
    @NotNull
    public T compose(
        @NotNull Supplier supplier,
        @NotNull Object[] data,
        @NotNull Map<?, ?> result
    ) throws Crash {
        for (Map.Entry<?, ?> entry : result.entrySet()) {
            // get its key
            Object key = entry.getKey();
            if (key == null) {
                continue;
            }

            // try lookup
            Target target = target(key);
            if (target == null) {
                continue;
            }

            // check index
            int k = target.getIndex();
            if (k < 0 || k >= data.length) {
                throw new Crash(
                    "'" + k + "' out of range"
                );
            }

            // get the value
            Object val = entry.getValue();
            if (val == null) {
                continue;
            }

            // get class specified
            Class<?> type = val.getClass();
            Class<?> klass = target.getType();

            // update field
            if (klass.isAssignableFrom(type)) {
                data[k] = val;
                continue;
            }

            // get spare specified
            Spare<?> spare = supplier.lookup(klass);

            // update field
            if (spare != null) {
                data[k] = spare.cast(
                    supplier, val
                );
            }
        }

        return apply(
            Alias.EMPTY, data
        );
    }


    /**
     * @param supplier  the specified supplier
     * @param resultSet the specified resultSet
     * @since 0.0.3
     */
    @NotNull
    public T compose(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        T entity;
        try {
            entity = apply(
                Alias.EMPTY
            );
        } catch (Throwable e) {
            throw new SQLCrash(
                "Error creating " + getType(), e
            );
        }

        ResultSetMetaData meta =
            resultSet.getMetaData();
        int count = meta.getColumnCount();

        // update fields
        for (int i = 1; i <= count; i++) {
            // get its key
            String key = meta.getColumnName(i);

            // try lookup
            Setter<T, ?> setter = setter(key);
            if (setter == null) {
                throw new SQLCrash(
                    "Can't find the Setter of " + key
                );
            }

            // get the value
            Object val = resultSet.getObject(i);

            // skip if null
            if (val == null) {
                continue;
            }

            // get class specified
            Class<?> type = val.getClass();
            Class<?> klass = setter.getType();

            // update field
            if (klass.isAssignableFrom(type)) {
                setter.onAccept(
                    entity, val
                );
                continue;
            }

            // get spare specified
            Spare<?> spare = supplier.lookup(klass);

            // update field
            if (spare != null) {
                val = spare.cast(
                    supplier, val
                );
                if (val != null) {
                    setter.onAccept(
                        entity, val
                    );
                    continue;
                }
            }

            throw new SQLCrash(
                "Cannot convert the type of " + key + " from " + type + " to " + klass
            );
        }

        return entity;
    }

    /**
     * @param supplier  the specified supplier
     * @param data      the specified params
     * @param resultSet the specified resultSet
     * @since 0.0.3
     */
    @NotNull
    public T compose(
        @NotNull Supplier supplier,
        @NotNull Object[] data,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        ResultSetMetaData meta =
            resultSet.getMetaData();
        int count = meta.getColumnCount();

        // update params
        for (int i = 1; i <= count; i++) {
            // get its key
            String key = meta.getColumnName(i);

            // try lookup
            Target target = target(key);
            if (target == null) {
                throw new SQLCrash(
                    "Can't find the Target of " + key
                );
            }

            // check index
            int k = target.getIndex();
            if (k < 0 || k >= data.length) {
                throw new SQLCrash(
                    "'" + k + "' out of range"
                );
            }

            // get the value
            Object val = resultSet.getObject(i);

            // skip if null
            if (val == null) {
                continue;
            }

            // get class specified
            Class<?> type = val.getClass();
            Class<?> klass = target.getType();

            // update field
            if (klass.isAssignableFrom(type)) {
                data[k] = val;
                continue;
            }

            // get spare specified
            Spare<?> spare = supplier.lookup(klass);

            // update field
            if (spare != null) {
                val = spare.cast(
                    supplier, val
                );
                if (val != null) {
                    data[k] = val;
                    continue;
                }
            }

            throw new SQLCrash(
                "Cannot convert the type of " + key + " from " + type + " to " + klass
            );
        }

        try {
            return apply(
                Alias.EMPTY, data
            );
        } catch (Throwable e) {
            throw new SQLCrash(
                "Error creating " + getType(), e
            );
        }
    }

    /**
     * @param alias the alias of getter
     * @since 0.0.3
     */
    @Override
    public Getter<T, ?> getter(
        @NotNull Object alias
    ) {
        Node<T> node = head;
        int hash = alias.hashCode();

        while (node != null) {
            String key = node.key;
            if (hash == key.hashCode()
                && alias.equals(key)) {
                return node;
            }
            node = node.next;
        }

        return null;
    }

    /**
     * @param alias the alias of getter
     * @since 0.0.3
     */
    @Override
    public Getter<T, ?> getter(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        if (alias.isNotEmpty()) {
            return getter(alias);
        }

        if (index < 0) {
            return null;
        }

        Node<T> node = head;
        while (node != null) {
            if (index == 0) {
                return node;
            }
            index--;
            node = node.next;
        }

        return null;
    }

    /**
     * @param getter the specified {@link Getter}
     * @since 0.0.3
     */
    protected void getter(
        @NotNull String key,
        @NotNull Node<T> getter
    ) {
        getter.key = key;
        if (tail == null) {
            head = getter;
            tail = getter;
        } else if (getter.index < 0) {
            tail.next = getter;
            tail = getter;
        } else {
            Node<T> m = head;
            Node<T> n = null;

            while (true) {
                if (m.index < 0) {
                    getter.next = m;
                    if (m == head) {
                        head = getter;
                    }
                    break;
                }

                if (m.index > getter.index) {
                    if (n == null) {
                        head = getter;
                    } else {
                        n.next = getter;
                    }
                    getter.next = m;
                    break;
                } else {
                    n = m;
                    m = m.next;
                    if (m == null) {
                        tail = getter;
                        n.next = getter;
                        break;
                    }
                }
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    public static abstract class Node<E>
        extends Item implements Getter<E, Object> {

        private String key;
        private Node<E> next;

        protected boolean nullable;
        protected boolean unwrapped;

        /**
         * @param index the specified {@code index}
         */
        protected Node(
            int index
        ) {
            super(index);
        }

        /**
         * @param node the specified {@link Node}
         */
        protected Node(
            Node<?> node
        ) {
            super(node);
            this.nullable = node.nullable;
            this.unwrapped = node.unwrapped;
        }

        /**
         * @param expose the specified {@link Expose}
         */
        protected Node(
            Expose expose
        ) {
            super(expose == null ? -1 : expose.index());
        }

        /**
         * Returns a clone of this {@link Node}
         */
        @Override
        public abstract Node<E> clone();
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    public static class Item
        extends Entry<Object, Item>
        implements Target, Cloneable {

        protected Type type;
        protected Class<?> klass;

        protected Coder<?> coder;
        protected final int index;

        /**
         * @param index the specified {@code index}
         */
        public Item(
            int index
        ) {
            super();
            this.index = index;
        }

        /**
         * @param item the specified {@link Item}
         */
        public Item(
            @NotNull Item item
        ) {
            super();
            index = item.index;
            type = item.type;
            coder = item.coder;
            klass = item.klass;
        }

        /**
         * @param index the specified {@code index}
         */
        public Item(
            @NotNull int index,
            @NotNull Class<?> klass,
            @NotNull Type type,
            @Nullable Coder<?> coder
        ) {
            super();
            this.index = index;
            this.type = type;
            this.coder = coder;
            this.klass = klass;
        }

        /**
         * Returns a clone of this {@link Item}
         */
        @Override
        public Item clone() {
            return new Item(this);
        }

        /**
         * Returns the index of {@link Item}
         */
        @Override
        public int getIndex() {
            return index;
        }

        /**
         * Returns the {@link Class} of {@link Item}
         */
        @Override
        public Class<?> getType() {
            return klass;
        }

        /**
         * Returns the {@link Coder} of {@link Item}
         */
        @Override
        public Coder<?> getCoder() {
            return coder;
        }

        /**
         * Returns the {@link Type} of {@link Item}
         */
        @Override
        public Type getActualType() {
            return type;
        }
    }
}
