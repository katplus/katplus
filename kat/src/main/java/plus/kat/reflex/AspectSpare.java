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

import plus.kat.anno.Embed;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.utils.Casting;
import plus.kat.utils.KatMap;

import java.util.Map;

/**
 * @author kraity
 * @since 0.0.2
 */
public abstract class AspectSpare<E> extends KatMap<Object, Setter<E, ?>> implements Sketch<E> {

    protected final Class<E> klass;
    protected final CharSequence space;

    protected Supplier supplier;
    protected int flags;
    protected Node<E> head, tail;

    protected AspectSpare(
        @Nullable Embed embed,
        @NotNull Class<E> klass,
        @NotNull Supplier supplier
    ) {
        this.klass = klass;
        this.supplier = supplier;
        if (embed != null) {
            flags = embed.claim();
        }

        this.onHandle();
        this.onFields();
        this.onMethods();

        this.space = supplier.register(
            embed, klass, this
        );
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
    public Class<E> getType() {
        return klass;
    }

    @Override
    public E cast(
        @Nullable Object data
    ) {
        return cast(
            supplier, data
        );
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public E cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        Class<?> clazz = data.getClass();
        if (klass.isAssignableFrom(clazz)) {
            return (E) data;
        }

        if (data instanceof Map) try {
            // source
            Map<?, ?> map = (Map<?, ?>) data;

            // create ins
            E entity = apply(
                Alias.EMPTY
            );

            if (entity == null) {
                return null;
            }

            // foreach
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                // key
                Object key = entry.getKey();
                if (key == null) {
                    continue;
                }

                // try lookup
                Setter<E, ?> setter = get(key);
                if (setter == null) {
                    continue;
                }

                // get class specified
                Class<?> klass = setter.getKlass();

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

    @Override
    public E read(
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
        Node<E> node = head;
        while (node != null) {
            chan.set(
                node.key, node.getCoder(), node.onApply(value)
            );
            node = node.next;
        }
    }

    @Override
    @Nullable
    public Setter<E, ?> setter(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return get(
            alias.isEmpty() ? index : alias
        );
    }

    protected void addSetter(
        @NotNull Object key,
        @NotNull Setter<E, ?> setter
    ) {
        this.put(
            key, setter
        );
    }

    protected void addGetter(
        @NotNull CharSequence key,
        @NotNull Node<E> node
    ) {
        node.key = key;
        int hash = node.getHash();
        if (tail == null) {
            head = node;
            tail = node;
        } else if (hash < 0) {
            tail.next = node;
            tail = node;
        } else {
            Node<E> m = head;
            Node<E> n = null;

            int wgt;
            while (true) {
                wgt = m.getHash();
                if (wgt < 0) {
                    node.next = m;
                    if (m == head) {
                        head = node;
                    }
                    break;
                }

                if (wgt > hash) {
                    if (n == null) {
                        head = node;
                    } else {
                        n.next = node;
                    }
                    node.next = m;
                    break;
                } else {
                    n = m;
                    m = m.next;
                    if (m == null) {
                        tail = node;
                        n.next = node;
                        break;
                    }
                }
            }
        }
    }

    protected void onHandle() {
        // Nothing
    }

    protected void onFields() {
        // Nothing
    }

    protected void onMethods() {
        // Nothing
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    public static abstract class Node<E>
        extends Entry<E, Node<E>>
        implements Getter<E, Object> {

        Node<E> next;
        CharSequence key;

        public Node() {
            super(0);
        }

        public Node(
            int hash
        ) {
            super(hash);
        }
    }
}
