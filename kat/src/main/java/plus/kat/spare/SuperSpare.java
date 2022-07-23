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
import plus.kat.utils.Casting;
import plus.kat.utils.KatMap;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author kraity
 * @since 0.0.2
 */
@SuppressWarnings("unchecked")
public abstract class SuperSpare<T, E> extends KatMap<Object, E> implements Spare<T> {

    protected final Class<T> klass;
    protected final CharSequence space;

    protected Node<T> head;
    protected Node<T> tail;

    protected int flags;
    protected Supplier supplier;

    protected SuperSpare(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        this.klass = klass;
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
    public CharSequence getSpace() {
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
            return cast(
                supplier, (Map<?, ?>) data
            );
        } catch (Exception e) {
            return null;
        }

        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, supplier
            );
        }

        return null;
    }

    @Nullable
    public T cast(
        @NotNull Supplier supplier,
        @NotNull Map<?, ?> data
    ) throws Exception {
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
    ) throws IOCrash {
        Node<T> node = head;
        while (node != null) {
            Object val = node.onApply(value);
            if (val != null || node.nullable) {
                chan.set(
                    node.key, node.coder, val
                );
            }
            node = node.next;
        }
    }

    /**
     * @param getter the specified {@link Getter}
     */
    protected void getter(
        @NotNull CharSequence key,
        @NotNull Node<T> getter
    ) {
        getter.key = key;
        int hash = getter.getHash();
        if (tail == null) {
            head = getter;
            tail = getter;
        } else if (hash < 0) {
            tail.next = getter;
            tail = getter;
        } else {
            Node<T> m = head;
            Node<T> n = null;

            int wgt;
            while (true) {
                wgt = m.getHash();
                if (wgt < 0) {
                    getter.next = m;
                    if (m == head) {
                        head = getter;
                    }
                    break;
                }

                if (wgt > hash) {
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
        extends Entry<E, Node<E>>
        implements Getter<E, Object> {

        private Node<E> next;
        private CharSequence key;

        protected Coder<?> coder;
        protected boolean nullable;

        protected Node() {
            super(0);
        }

        /**
         * @param hash the specified {@code hash}
         */
        protected Node(
            int hash
        ) {
            super(hash);
        }

        /**
         * @param expose the specified {@link Expose}
         */
        protected Node(
            Expose expose
        ) {
            super(expose == null
                ? -1 : expose.index()
            );
        }

        /**
         * Returns a clone of this {@link Node}
         */
        @Override
        public abstract Node<E> clone();

        /**
         * Returns the {@link Coder} of {@link Node}
         */
        @Override
        public Coder<?> getCoder() {
            return coder;
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    public static class Edge
        extends Entry<Object, Edge>
        implements Param {

        private Type type;
        private Class<?> klass;

        private int index;
        private Coder<?> coder;

        public Edge() {
            super(0);
        }

        /**
         * @param edge the specified {@link Edge}
         */
        public Edge(
            @NotNull Edge edge
        ) {
            super(0);
            type = edge.type;
            klass = edge.klass;
            coder = edge.coder;
            index = edge.index;
        }

        /**
         * @param type the specified {@link Type}
         */
        public void setType(
            Type type
        ) {
            this.type = type;
        }

        /**
         * Returns the {@link Type} of {@link Edge}
         */
        @Override
        public Type getType() {
            return type;
        }

        /**
         * @param klass the specified {@link Class}
         */
        public void setKlass(
            Class<?> klass
        ) {
            this.klass = klass;
        }

        /**
         * Returns the {@link Class} of {@link Edge}
         */
        @Override
        public Class<?> getKlass() {
            return klass;
        }

        /**
         * @param index the specified {@code index}
         */
        public void setIndex(
            int index
        ) {
            this.index = index;
        }

        /**
         * Returns the {@code index} of {@link Edge}
         */
        @Override
        public int getIndex() {
            return index;
        }

        /**
         * @param coder the specified {@link Coder}
         */
        public void setCoder(
            Coder<?> coder
        ) {
            this.coder = coder;
        }

        /**
         * Returns the {@link Coder} of {@link Edge}
         */
        @Override
        public Coder<?> getCoder() {
            return coder;
        }

        /**
         * Returns a clone of this {@link Edge}
         */
        @Override
        public Edge clone() {
            return new Edge(this);
        }
    }
}
