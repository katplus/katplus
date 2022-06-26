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

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * @author kraity
 * @since 0.0.1
 */
public class KatMap<K, V> {

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

        int m = table.length - 1;
        Entry<K, V> e = tab[m & h];

        while (e != null) {
            if (e.hash == h && key.equals(e.key)) {
                return e.val;
            }
            e = e.next;
        }

        return null;
    }

    /**
     * Associates the specified value with the specified key in this map
     *
     * @param key key with which the specified value is to be associated
     * @param val value to be associated with the specified key
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public V put(
        @NotNull K key,
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

            Entry<K, V> e = tab[i];
            if (e == null) {
                tab[i] = new Entry<>(
                    h, key, val
                );
                size++;
                return null;
            }

            if (e.hash == h && key.equals(e.key)) {
                V v = e.val;
                e.val = val;
                return v;
            }

            for (int b = 0; b < m; ++b) {
                if (e.next == null) {
                    e.next = new Entry<>(
                        h, key, val
                    );
                    size++;
                    return null;
                }

                e = e.next;
                if (e.hash == h && key.equals(e.key)) {
                    V v = e.val;
                    e.val = val;
                    return v;
                }
            }

            // resize
            int size = len << 1;
            Entry<K, V>[] bucket = new Entry[size];

            m = size - 1;
            Entry<K, V> n, b;

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

        int m = table.length - 1;
        int i = m & h;

        Entry<K, V> e = tab[i];
        if (e == null) {
            return null;
        }

        if (e.hash == h && key.equals(e.key)) {
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

            if (n.hash == h && key.equals(n.key)) {
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

        int m = table.length - 1;
        Entry<K, V> e = tab[m & h];

        while (e != null) {
            if (e.hash == h && key.equals(e.key)) {
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
     * @since 0.0.1
     */
    static class Entry<K, V> implements Map.Entry<K, V> {

        final int hash;
        final K key;
        V val;
        Entry<K, V> next;

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

        @Override
        public V setValue(V value) {
            V v = val;
            val = value;
            return v;
        }

        @Override
        public int hashCode() {
            return key.hashCode() ^
                (val == null ? 0 : val.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                return e.getKey().equals(key) &&
                    e.getValue().equals(val);
            }

            return false;
        }
    }
}
