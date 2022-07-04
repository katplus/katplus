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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import plus.kat.*;
import plus.kat.anno.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.utils.Casting;
import plus.kat.utils.KatMap;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ReflexWorker<E> extends KatMap<Object, Setter<E, ?>> implements Worker<E> {

    private final Class<E> klass;
    private final CharSequence space;

    private Node<E> head, tail;
    private final boolean refract;
    private final Constructor<E> builder;

    /**
     * @throws SecurityException     If the {@link Constructor#setAccessible(boolean)} is denied
     * @throws NoSuchMethodException If the klass has no parameterless constructor
     */
    public ReflexWorker(
        @NotNull Class<E> klass,
        @NotNull Supplier supplier
    ) throws NoSuchMethodException {
        this(klass, supplier, klass.getDeclaredConstructor());
    }

    /**
     * @throws SecurityException If the {@link Constructor#setAccessible(boolean)} is denied
     */
    public ReflexWorker(
        @NotNull Class<E> klass,
        @NotNull Supplier supplier,
        @NotNull Constructor<E> constructor
    ) {
        this.klass = klass;
        builder = constructor;
        builder.setAccessible(true);
        Embed embed = klass.getAnnotation(Embed.class);
        refract = embed != null && embed.index();

        register(klass.getDeclaredFields(), supplier);
        register(klass.getMethods(), supplier);
        space = supplier.register(embed, klass, this);
    }

    /**
     * @throws SecurityException     If the {@link Constructor#setAccessible(boolean)} is denied
     * @throws NoSuchMethodException If the klass has no parameterless constructor
     */
    public ReflexWorker(
        @Nullable Embed embed,
        @NotNull Class<E> klass,
        @NotNull Supplier supplier
    ) throws NoSuchMethodException {
        this.klass = klass;
        builder = klass.getDeclaredConstructor();
        builder.setAccessible(true);
        refract = embed != null && embed.index();

        register(klass.getDeclaredFields(), supplier);
        register(klass.getMethods(), supplier);
        space = supplier.register(embed, klass, this);
    }

    @Override
    @Nullable
    public E apply(
        @NotNull Alias alias
    ) throws Crash {
        try {
            return builder.newInstance();
        } catch (Exception e) {
            throw new Crash(e);
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
            E entity = builder.newInstance();

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

    private void register(
        @NotNull Field[] fields,
        @NotNull Supplier supplier
    ) {
        for (Field field : fields) {
            Expose expose = field.getAnnotation(Expose.class);
            if (expose == null) continue;

            field.setAccessible(true);
            ReflexField<E> reflex =
                new ReflexField<>(
                    field, expose, supplier
                );

            int h = expose.index();
            if (refract && h > -1) put(
                h, reflex.clone()
            );

            String name;
            String[] names = expose.value();

            if (names.length == 0) {
                name = field.getName();
                put(
                    name, reflex.clone()
                );
            } else {
                name = names[0];
                for (String alias : names) {
                    if (!alias.isEmpty()) put(
                        alias, reflex.clone()
                    );
                }
            }

            if (expose.export()) {
                register(name, reflex);
            }
        }
    }

    private void register(
        @NotNull Method[] methods,
        @NotNull Supplier supplier
    ) {
        for (Method method : methods) {
            int count = method.getParameterCount();
            if (count > 1) continue;

            Expose expose = method.getAnnotation(Expose.class);
            if (expose == null) continue;

            method.setAccessible(true);
            ReflexMethod<E> reflex =
                new ReflexMethod<>(
                    method, expose, supplier
                );

            int h = expose.index();
            String[] names = expose.value();

            if (count == 0) {
                if (expose.export()) {
                    if (names.length != 0) {
                        register(names[0], reflex);
                    }
                }
            } else {
                if (refract && h > -1) put(
                    h, reflex.clone()
                );

                for (String alias : names) {
                    if (!alias.isEmpty()) put(
                        alias, reflex.clone()
                    );
                }
            }
        }
    }

    private void register(
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

    /**
     * @author kraity
     * @since 0.0.2
     */
    static abstract class Node<E>
        extends Entry<E, Node<E>>
        implements Getter<E, Object> {

        CharSequence key;
        Node<E> next;

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
