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

import plus.kat.*;
import plus.kat.actor.*;
import plus.kat.entity.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.lang.annotation.*;

import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.6
 */
@SuppressWarnings("unchecked")
public abstract class SimpleSpare<T> extends BeanSpare<T> {

    int grade;
    String space;

    Node[] table;
    Caller head, tail;

    static boolean IN_KOTLIN;
    static boolean HAS_TRANSIENT;

    static {
        try {
            Class.forName(
                "kotlin.Metadata"
            );
            IN_KOTLIN = true;
        } catch (ClassNotFoundException e) {
            // Ignore this exception
        }

        try {
            // Generally no
            // @Transient in Android
            Class.forName(
                "java.beans.Transient"
            );
            HAS_TRANSIENT = true;
        } catch (ClassNotFoundException e) {
            // Ignore this exception
        }
    }

    SimpleSpare(
        @Nilable String space,
        @NotNull Class<T> klass,
        @NotNull Context context
    ) {
        super(
            klass, context
        );
        this.space = space;
    }

    @Override
    public String getSpace() {
        return space;
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        for (Caller node = head; node != null; node = node.mate) {
            chan.set(
                node.name,
                node.coder,
                node.apply(value)
            );
        }
    }

    public Sensor getProperty(
        @NotNull Object name
    ) {
        Node[] tab = table;
        if (tab == null) {
            return null;
        }

        int m = tab.length - 1;
        long hash = hash1(name);

        boolean retry = true;
        while (true) {
            Node n = tab[(int) (m & hash)];
            for (; n != null; n = n.next) {
                if (n.hash == hash) {
                    return n.getter;
                }
            }
            if (retry) {
                if (hash != (hash = hash2(name))) {
                    retry = false;
                    continue;
                }
            }

            return null;
        }
    }

    @Override
    public Sensor setProperty(
        @NotNull Object name
    ) {
        Node[] tab = table;
        if (tab == null) {
            return null;
        }

        int m = tab.length - 1;
        long hash = hash1(name);

        boolean retry = true;
        while (true) {
            Node n = tab[(int) (m & hash)];
            for (; n != null; n = n.next) {
                if (n.hash == hash) {
                    return n.setter;
                }
            }
            if (retry) {
                if (hash != (hash = hash2(name))) {
                    retry = false;
                    continue;
                }
            }

            return null;
        }
    }

    @Override
    public Sensor getParameter(
        @NotNull Object name
    ) {
        return setParameter(name);
    }

    @Override
    public Sensor setParameter(
        @NotNull Object name
    ) {
        Node[] tab = table;
        if (tab == null) {
            return null;
        }

        int m = tab.length - 1;
        long hash = hash1(name);

        boolean retry = true;
        while (true) {
            Node n = tab[(int) (m & hash)];
            for (; n != null; n = n.next) {
                if (n.hash == hash) {
                    return n.arguer;
                }
            }
            if (retry) {
                if (hash != (hash = hash2(name))) {
                    retry = false;
                    continue;
                }
            }

            return null;
        }
    }

    Node node(
        long hash, Node node
    ) {
        Node[] tab = table;
        if (tab == null) {
            tab = table = new Node[4];
        }

        while (true) {
            int l = tab.length;
            int i = (int) (
                hash & (l - 1)
            );

            Node e = tab[i];
            if (e == null) {
                if (node.hash != 0) {
                    node = new Node();
                }
                node.hash = hash;
                return tab[i] = node;
            }

            for (int j = 0; ; j++) {
                if (e.hash == hash) {
                    return e;
                }

                Node next = e.next;
                if (next != null) {
                    e = next;
                } else {
                    if (l == j) break;
                    if (node.hash != 0) {
                        node = new Node();
                    }
                    node.hash = hash;
                    return e.next = node;
                }
            }

            int s = l << 1, u = s - 1;
            Node[] bucket = new Node[s];

            Node m, n;
            for (int j = 0; j < l; j++) {
                if ((e = tab[j]) != null) {
                    tab[j] = null;
                    do {
                        n = e.next;
                        i = (int) (
                            u & e.hash
                        );

                        m = bucket[i];
                        if (m != null) {
                            e.next = m.next;
                            m.next = e;
                        } else {
                            e.next = null;
                            bucket[i] = e;
                        }
                    } while (
                        (e = n) != null
                    );
                }
            }
            tab = table = bucket;
        }
    }

    void show(
        Object name, Caller node
    ) {
        if (node.name != null) {
            return;
        }

        if (name != null) {
            node.name = name;
            node.grade = grade;
        } else {
            throw new IllegalStateException(
                "Received name cannot be null"
            );
        }

        int g = grade,
            i = node.index;
        Caller n = null, m = head;

        if (i == -1 && g == 0) {
            if (m == null) {
                head = node;
                tail = node;
                return;
            }

            if (m.index < -1) {
                head = node;
                tail = node;
                node.mate = m;
            } else {
                n = tail;
                tail = node;
                if (n == null) {
                    do {
                        n = m;
                        m = m.mate;
                    } while (
                        m != null
                    );
                } else {
                    node.mate = n.mate;
                }
                n.mate = node;
            }
        } else {
            if (m == null) {
                head = node;
                return;
            }

            if (i < 0) {
                int c;
                if (i != -1) {
                    m = tail;
                    if (m == null) m = head;
                }
                do {
                    if ((c = m.index) < i ||
                        (c == i && g > m.grade)) {
                        if (n == null) {
                            head = node;
                        } else {
                            n.mate = node;
                        }
                        node.mate = m;
                        return;
                    }
                } while (
                    (m = (n = m).mate) != null
                );
            } else {
                do {
                    int c = m.index;
                    if ((c < 0 || i < c) ||
                        (c == i && g > m.grade)) {
                        if (n == null) {
                            head = node;
                        } else {
                            n.mate = node;
                        }
                        node.mate = m;
                        return;
                    }
                } while (
                    (m = (n = m).mate) != null
                );
            }
            n.mate = node;
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    static class Node {
        long hash;
        Node next;

        Sensor arguer;
        Sensor setter, getter;
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    static abstract class Caller extends Node implements Sensor {

        Caller mate;
        Object name;

        int grade;
        final int index;

        Type type;
        Coder<?> coder;
        AnnotatedElement element;

        public Caller(
            int row
        ) {
            index = row;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Coder<?> getCoder() {
            return coder;
        }

        void prepare(
            Type type,
            Magic magic,
            Context context
        ) {
            Class<?> agent;
            if (magic == null || (agent =
                magic.agent()) == void.class) {
                if (type instanceof Class) {
                    agent = (Class<?>) type;
                    if (agent.isPrimitive() ||
                        agent == String.class) {
                        coder = context.assign(type);
                    }
                }
                return;
            }

            if (Coder.class.isAssignableFrom(agent)) {
                Class<?>[] cs = null;
                Constructor<?> ctor = null;

                int mx = -1;
                for (Constructor<?> cto : agent
                    .getDeclaredConstructors()) {
                    Class<?>[] cls =
                        cto.getParameterTypes();

                    int i = 0,
                        j = cls.length;
                    while (true) {
                        if (i == j) {
                            if (mx < j) {
                                mx = j;
                                cs = cls;
                                ctor = cto;
                            }
                        } else {
                            Class<?> m = cls[i++];
                            if (m == Type.class ||
                                m == Class.class ||
                                m == String.class ||
                                m == Context.class ||
                                m.isAnnotation()) {
                                continue;
                            }
                        }
                        break;
                    }
                }

                if (ctor == null) {
                    throw new IllegalStateException(
                        "No valid constructor found"
                    );
                }

                try {
                    if (!ctor.isAccessible()) {
                        ctor.setAccessible(true);
                    }

                    Object[] args = null;
                    if (mx != 0) {
                        args = new Object[mx];
                        for (int i = 0; i < mx; i++) {
                            Class<?> m = cs[i];
                            if (m == Class.class) {
                                args[i] = classOf(type);
                            } else if (m == Type.class) {
                                args[i] = type;
                            } else if (m == Context.class) {
                                args[i] = context;
                            } else if (m.isAnnotation()) {
                                args[i] = getAnnotation(
                                    (Class<? extends Annotation>) m
                                );
                            }
                        }
                    }

                    coder = (Coder<?>) ctor.newInstance(args);
                } catch (Exception e) {
                    throw new IllegalStateException(
                        "Failed to build the " +
                            type + "'s agent coder: " + agent, e
                    );
                }
            } else {
                coder = context.assign(agent);
            }
        }

        <A extends Annotation> A getAnnotation(
            @NotNull Class<A> clazz
        ) {
            return element.getAnnotation(clazz);
        }
    }
}
