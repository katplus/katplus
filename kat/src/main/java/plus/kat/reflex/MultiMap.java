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

import plus.kat.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.utils.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class MultiMap<E> extends KatMap<Object, Setter<E, ?>> {

    private Node<E> head;
    private Node<E> tail;

    /**
     * Appends the specified element to the end of this list.
     *
     * @param key    key with which the specified value is to be associated
     * @param getter value to be associated with the specified key
     */
    public void add(
        @NotNull CharSequence key,
        @NotNull Getter<E, ?> getter
    ) {
        this.add(
            -1, key, getter
        );
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param weight the specified weight of getter
     * @param key    key with which the specified value is to be associated
     * @param getter value to be associated with the specified key
     */
    public void add(
        @NotNull int weight,
        @NotNull CharSequence key,
        @NotNull Getter<E, ?> getter
    ) {
        Node<E> node = new Node<>(
            weight, key, getter
        );
        if (tail == null) {
            head = node;
            tail = node;
        } else if (weight < 0) {
            tail.next = node;
            tail = node;
        } else {
            Node<E> m = head;
            Node<E> n = null;

            int wgt;
            while (true) {
                wgt = m.weight;
                if (wgt < 0) {
                    node.next = m;
                    if (m == head) {
                        head = node;
                    }
                    break;
                }

                if (wgt > weight) {
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
     * Iterates over all {@link Getter}
     *
     * @throws IOCrash If an I/O error occurs
     */
    public void each(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOCrash {
        Node<E> node = head;
        while (node != null) {
            Getter<E, ?> getter = node.getter;
            chan.set(
                node.key, getter.getCoder(), getter.onApply(value)
            );
            node = node.next;
        }
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    private static class Node<E> {
        CharSequence key;
        int weight;
        Node<E> next;
        Getter<E, ?> getter;

        private Node(
            @NotNull int weight,
            @NotNull CharSequence key,
            @NotNull Getter<E, ?> getter
        ) {
            this.weight = weight;
            this.key = key;
            this.getter = getter;
        }
    }
}
