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
package plus.kat.utils;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author kraity
 * @since 0.0.1
 */
public class KatMap<K, V> implements Iterable<KatMap.Entry<K, V>> {

    private int size;
    private Entry<K, V>[] table;

    /**
     * Returns the number of key-value mappings in this map.
     */
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     */
    @Nullable
    public V get(
        @NotNull Object key
    ) {
        Entry<K, V>[] tab = table;
        if (tab == null) {
            return null;
        }

        int h = key.hashCode();
        h = h ^ (h >>> 16);

        int m = tab.length - 1;
        Entry<K, V> e = tab[m & h];

        while (e != null) {
            if (e.hash == h &&
                (key.equals(e.key) ||
                    e.key.equals(key))) {
                return e.val;
            }
            e = e.next;
        }

        return null;
    }

    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or {@code defaultValue} if this map contains no mapping for the key
     * @since 0.0.3
     */
    @Nullable
    public V getOrDefault(
        @NotNull Object key,
        @Nullable V defaultValue
    ) {
        Entry<K, V>[] tab = table;
        if (tab == null) {
            return defaultValue;
        }

        int h = key.hashCode();
        h = h ^ (h >>> 16);

        int m = tab.length - 1;
        Entry<K, V> e = tab[m & h];

        while (e != null) {
            if (e.hash == h &&
                (key.equals(e.key) ||
                    e.key.equals(key))) {
                return e.val;
            }
            e = e.next;
        }

        return defaultValue;
    }

    /**
     * Associates the specified value with the specified key in this map
     *
     * @param key key with which the specified value is to be associated
     * @param del if false, don't change existing value
     * @param val value to be associated with the specified key
     * @since 0.0.3
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public V set(
        @NotNull K key,
        boolean del,
        @Nullable V val
    ) {
        Entry<K, V>[] tab = table;
        if (tab == null) {
            tab = table = new Entry[4];
        }

        int h = key.hashCode();
        h = h ^ (h >>> 16);

        while (true) {
            int len = tab.length;
            int m = len - 1;
            int i = m & h;

            Entry<K, V> n, e = tab[i];
            if (e == null) {
                if (val instanceof Entry) {
                    n = (Entry<K, V>) val;
                    if (n.key == null) {
                        n.hash = h;
                        n.key = key;
                        n.val = val;
                        n.next = null;
                        tab[i] = n;
                    } else {
                        tab[i] = new Entry<>(
                            h, key, val
                        );
                    }
                } else {
                    tab[i] = new Entry<>(
                        h, key, val
                    );
                }
                size++;
                return null;
            }

            if (e.hash == h &&
                (key.equals(e.key) ||
                    e.key.equals(key))) {
                V v = e.val;
                if (del) {
                    e.val = val;
                }
                return v;
            }

            for (int b = 0; b < m; ++b) {
                if (e.next == null) {
                    if (val instanceof Entry) {
                        n = (Entry<K, V>) val;
                        if (n.key == null) {
                            n.hash = h;
                            n.key = key;
                            n.val = val;
                            n.next = null;
                            e.next = n;
                        } else {
                            e.next = new Entry<>(
                                h, key, val
                            );
                        }
                    } else {
                        e.next = new Entry<>(
                            h, key, val
                        );
                    }
                    size++;
                    return null;
                }

                e = e.next;
                if (e.hash == h &&
                    (key.equals(e.key) ||
                        e.key.equals(key))) {
                    V v = e.val;
                    if (del) {
                        e.val = val;
                    }
                    return v;
                }
            }

            // resize
            int size = len << 1;
            Entry<K, V>[] bucket = new Entry[size];

            m = size - 1;
            Entry<K, V> b;

            for (int k = 0; k < len; ++k) {
                if ((e = tab[k]) != null) {
                    tab[k] = null;
                    do {
                        n = e.next;
                        i = m & e.hash;

                        b = bucket[i];
                        if (b == null) {
                            bucket[i] = e;
                            e.next = null;
                        } else {
                            e.next = b.next;
                            b.next = e;
                        }
                    } while (
                        (e = n) != null
                    );
                }
            }
            tab = table = bucket;
        }
    }

    /**
     * Associates the specified value with the specified key in this map
     *
     * @param key key with which the specified value is to be associated
     * @param val value to be associated with the specified key
     */
    @Nullable
    public V put(
        @NotNull K key,
        @Nullable V val
    ) {
        return set(
            key, true, val
        );
    }

    /**
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}) associates it with the given value and returns {@code null}, else returns the current value
     *
     * @param key key with which the specified value is to be associated
     * @param val value to be associated with the specified key
     * @since 0.0.3
     */
    @Nullable
    public V putIfAbsent(
        @NotNull K key,
        @Nullable V val
    ) {
        return set(
            key, false, val
        );
    }

    /**
     * Copies all the mappings from the specified map to this map.
     *
     * @param m mappings to be stored in this map
     */
    public void putAll(
        @NotNull Map<? extends K, ? extends V> m
    ) {
        int size = m.size();
        if (size != 0) {
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                this.put(
                    e.getKey(), e.getValue()
                );
            }
        }
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    @Nullable
    public V remove(
        @NotNull Object key
    ) {
        Entry<K, V>[] tab = table;
        if (tab == null) {
            return null;
        }

        int h = key.hashCode();
        h = h ^ (h >>> 16);

        int m = tab.length - 1;
        int i = m & h;

        Entry<K, V> e = tab[i];
        if (e == null) {
            return null;
        }

        if (e.hash == h &&
            (key.equals(e.key) ||
                e.key.equals(key))) {
            tab[i] = null;
            size--;
            V v = e.val;
            e.val = null;
            return v;
        }

        for (Entry<K, V> n; ; e = n) {
            n = e.next;
            if (n == null) {
                return null;
            }

            if (n.hash == h &&
                (key.equals(e.key) ||
                    e.key.equals(key))) {
                e.next = n.next;
                size--;
                V v = n.val;
                n.val = null;
                return v;
            }
        }
    }

    /**
     * Returns {@code true} if this map contains the mapping of the specified key.
     *
     * @param key key whose presence in this map is to be tested
     */
    public boolean containsKey(
        @NotNull Object key
    ) {
        Entry<K, V>[] tab = table;
        if (tab == null) {
            return false;
        }

        int h = key.hashCode();
        h = h ^ (h >>> 16);

        int m = tab.length - 1;
        Entry<K, V> e = tab[m & h];

        while (e != null) {
            if (e.hash == h &&
                (key.equals(e.key) ||
                    e.key.equals(key))) {
                return true;
            }
            e = e.next;
        }

        return false;
    }

    /**
     * Return {@code true} If this mapping is mapped to the specified value.
     *
     * @param value value whose presence in this map is to be tested
     */
    public boolean containsValue(
        @Nullable Object value
    ) {
        Entry<K, V>[] tab = table;
        if (tab == null) {
            return false;
        }

        for (Entry<K, V> e : tab) {
            if (e != null) do {
                if (e.val == value)
                    return true;
            } while (
                (e = e.next) != null
            );
        }

        return false;
    }

    /**
     * @param action The action to be performed for each entry
     * @since 0.0.2
     */
    public void forEach(
        @Nullable Consumer<? super Entry<K, V>> action
    ) {
        Entry<K, V>[] tab = table;
        if (tab != null &&
            size != 0 &&
            action != null) {
            for (Entry<K, V> e : tab) {
                for (; e != null; e = e.next) {
                    action.accept(e);
                }
            }
        }
    }

    /**
     * @param action The action to be performed for each entry
     */
    public void forEach(
        @Nullable BiConsumer<? super K, ? super V> action
    ) {
        Entry<K, V>[] tab = table;
        if (tab != null &&
            size != 0 &&
            action != null) {
            for (Entry<K, V> e : tab) {
                for (; e != null; e = e.next) {
                    action.accept(
                        e.key, e.val
                    );
                }
            }
        }
    }

    /**
     * Returns an iterator over elements of type {@code T}
     *
     * @since 0.0.2
     */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iter<>(this);
    }

    /**
     * @param function the function to apply to each entry
     */
    public void replaceAll(
        @Nullable BiFunction<? super K, ? super V, ? extends V> function
    ) {
        Entry<K, V>[] tab = table;
        if (tab != null &&
            size != 0 &&
            function != null) {
            for (Entry<K, V> e : tab) {
                for (; e != null; e = e.next) {
                    e.val = function.apply(
                        e.key, e.val
                    );
                }
            }
        }
    }

    /**
     * Removes all the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear() {
        Entry<K, V>[] tab = table;
        if (tab != null && size != 0) {
            size = 0;
            Entry<K, V> e, n;
            for (int k = 0; k < tab.length; ++k) {
                if ((e = tab[k]) != null) {
                    tab[k] = null;
                    do {
                        n = e.next;
                        e.val = null;
                        e.next = null;
                    } while (
                        (e = n) != null
                    );
                }
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    public static class Iter<K, V>
        implements Iterator<Entry<K, V>> {

        int index;
        Entry<K, V> next;
        final KatMap<K, V> map;

        public Iter(
            KatMap<K, V> map
        ) {
            this.map = map;
            Entry<K, V>[] t = map.table;
            if (t != null && map.size > 0) do {
                // Nothing
            } while (
                index < t.length && (next = t[index++]) == null
            );
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Entry<K, V> next() {
            Entry<K, V> node = next;
            next = node.next;

            if (next == null) {
                Entry<K, V>[] t = map.table;
                if (t != null) do {
                    // Nothing
                } while (
                    index < t.length && (next = t[index++]) == null
                );
            }

            return node;
        }
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    public static class Entry<K, V>
        implements Map.Entry<K, V> {

        private int hash;
        private K key;
        private V val;
        private Entry<K, V> next;

        public Entry() {
            // Nothing
        }

        public Entry(
            int hash, K key, V val
        ) {
            this.key = key;
            this.val = val;
            this.hash = hash;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return val;
        }

        public int getHash() {
            return hash;
        }

        @Override
        public V setValue(V value) {
            V v = val;
            val = value;
            return v;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                return Objects.equals(key, e.getKey())
                    && Objects.equals(val, e.getValue());
            }

            return false;
        }
    }
}
