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
import plus.kat.chain.*;
import plus.kat.entity.*;

import java.io.*;
import java.lang.reflect.*;

import static plus.kat.spare.Parser.*;
import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.4
 */
public abstract class BeanSpare<T> implements Subject<T> {

    protected int grade;
    protected String space;

    protected Nodus[] table;
    protected Widget head, tail;

    protected final Class<T> klass;
    protected final Context context;

    protected BeanSpare(
        @NotNull Class<T> klass,
        @NotNull Context context
    ) {
        if (klass != null && context != null) {
            this.klass = klass;
            this.context = context;
        } else {
            throw new NullPointerException(
                "Received: (" + klass + ", " + context + ")"
            );
        }
    }

    protected BeanSpare(
        @Nilable String space,
        @NotNull Class<T> klass,
        @NotNull Context context
    ) {
        if (klass != null && context != null) {
            this.space = space;
            this.klass = klass;
            this.context = context;
        } else {
            throw new NullPointerException(
                "Received: (" + space + ", " + klass + ", " + context + ")"
            );
        }
    }

    @Override
    public T apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == klass) {
            return apply();
        }

        Class<?> clazz = classOf(type);
        if (clazz == null) {
            throw new IllegalStateException(
                "Failed to resolve " + type
            );
        }

        if (klass == clazz) {
            return apply();
        }

        if (klass.isAssignableFrom(clazz)) {
            Spare<T> spare =
                context.assign(clazz);

            if (spare != null &&
                spare != this) {
                return spare.apply(type);
            }
        }

        throw new IllegalStateException(
            "Failed to build this " + type
        );
    }

    @Override
    public String getSpace() {
        return space;
    }

    @Override
    public Class<T> getType() {
        return klass;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public T read(
        @NotNull Flag flag,
        @NotNull Value data
    ) throws IOException {
        if (data.isNothing()) {
            return null;
        }

        if (flag.isFlag(Flag.VALUE_AS_BEAN)) {
            Algo algo = algoOf(data);
            if (algo == null) {
                return null;
            }
            try (Parser op = with(this)) {
                return op.solve(
                    algo, Flow.of(data)
                );
            }
        }

        throw new IOException(
            "Failed to parse the value to `" + klass
                + "` unless `Flag.VALUE_AS_BEAN` is enabled"
        );
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        for (Widget node = head; node != null; node = node.mate) {
            chan.set(
                node.name, node.coder, node.apply(value)
            );
        }
    }

    /**
     * Sets the specified property
     */
    protected void setReader(
        @NotNull String name,
        @NotNull Widget node
    ) {
        bundle(node, name).setter = node;
    }

    /**
     * Adds the specified property.
     * Returns true if the node is settled
     */
    protected boolean addReader(
        @NotNull String name,
        @NotNull Widget node
    ) {
        Nodus e = bundle(node, name);
        if (e.setter == null) {
            e.setter = node;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the specified attribute
     */
    protected void setWriter(
        @NotNull String name,
        @NotNull Widget node
    ) {
        Nodus e = bundle(
            node, name
        );
        e.getter = node;
        concat(name, node);
    }

    /**
     * Adds the specified attribute.
     * Returns true if the node is settled
     */
    protected boolean addWriter(
        @NotNull String name,
        @NotNull Widget node
    ) {
        Nodus e = bundle(node, name);
        if (e.getter != null) {
            return false;
        } else {
            e.getter = node;
            return concat(name, node);
        }
    }

    /**
     * Sets the specified property
     */
    protected void setProperty(
        @NotNull String name,
        @NotNull Widget node
    ) {
        Nodus e = bundle(
            node, name
        );
        e.setter = node;
        e.getter = node;
        concat(name, node);
    }

    /**
     * Adds the specified property.
     * Returns true if the node is settled
     */
    protected boolean addProperty(
        @NotNull String name,
        @NotNull Widget node
    ) {
        Nodus e = bundle(node, name);
        if (e.setter != null ||
            e.getter != null) {
            return false;
        } else {
            e.setter = node;
            e.getter = node;
            return concat(name, node);
        }
    }

    /**
     * Sets the specified parameter
     */
    protected void setParameter(
        @NotNull String name,
        @NotNull Widget node
    ) {
        bundle(node, name).target = node;
    }

    /**
     * Adds the specified parameter.
     * Returns true if the node is settled
     */
    protected boolean addParameter(
        @NotNull String name,
        @NotNull Widget node
    ) {
        Nodus e = bundle(node, name);
        if (e.target == null) {
            e.target = node;
            return true;
        } else {
            return false;
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    public static class Nodus {
        Object id;
        Widget mate;

        int hash;
        Nodus next;

        Sensor target;
        Sensor setter, getter;
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    public abstract static class Widget
        extends Nodus implements Sensor {

        private int grade;
        private String name;

        protected Type type;
        protected int index;
        protected Coder<?> coder;

        /**
         * @param i the specified index
         */
        public Widget(int i) {
            index = i;
        }

        /**
         * Returns the type of {@link Sensor}
         */
        @Override
        public Type getType() {
            return type;
        }

        /**
         * Returns the coder of {@link Sensor}
         */
        @Override
        public Coder<?> getCoder() {
            return coder;
        }
    }

    /**
     * Returns the {@link Nodus} being used
     *
     * @param node the specified bundle
     * @param name the specified key of bundle
     */
    @NotNull
    private Nodus bundle(
        @NotNull Nodus node,
        @NotNull Object name
    ) {
        Nodus[] tab = table;
        if (tab == null) {
            tab = table = new Nodus[4];
        }

        int hash = name.hashCode();
        hash = hash ^ (hash >>> 16);

        while (true) {
            int l = tab.length;
            int i = (l - 1) & hash;

            Nodus e = tab[i];
            if (e == null) {
                if (node.id != null) {
                    node = new Nodus();
                }
                node.id = name;
                node.hash = hash;
                return tab[i] = node;
            }

            for (int k = 0; ; k++) {
                if (e.hash == hash &&
                    (name.equals(e.id) ||
                        e.id.equals(name))) {
                    return e;
                }

                Nodus next = e.next;
                if (next != null) {
                    e = next;
                } else {
                    if (l <= k) break;
                    if (node.id != null) {
                        node = new Nodus();
                    }
                    node.id = name;
                    node.hash = hash;
                    return e.next = node;
                }
            }

            int s = l << 1;
            Nodus[] bucket = new Nodus[s];

            Nodus m, n;
            int u = s - 1;

            for (int k = 0; k < l; k++) {
                if ((e = tab[k]) != null) {
                    tab[k] = null;
                    do {
                        n = e.next;
                        i = u & e.hash;

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

    /**
     * Returns true if the {@link Widget} is settled
     *
     * @param name the specified key of bundle
     * @param node the specified bundle to be settled
     */
    private boolean concat(
        @NotNull String name,
        @NotNull Widget node
    ) {
        if (node.name == null) {
            node.name = name;
            node.grade = grade;
        } else {
            return false;
        }

        Widget m = head;
        Widget n = null;

        int g = grade;
        int d = node.index;

        if (d == -1 && g == 0) {
            if (m == null) {
                head = node;
                tail = node;
                return true;
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
                return true;
            }

            if (d < 0) {
                int c;
                if (d != -1) {
                    m = tail;
                    if (m == null) m = head;
                }
                do {
                    if ((c = m.index) < d ||
                        (c == d && g > m.grade)) {
                        if (n == null) {
                            head = node;
                        } else {
                            n.mate = node;
                        }
                        node.mate = m;
                        return true;
                    }
                } while (
                    (m = (n = m).mate) != null
                );
            } else {
                do {
                    int c = m.index;
                    if ((c < 0 || d < c) ||
                        (c == d && g > m.grade)) {
                        if (n == null) {
                            head = node;
                        } else {
                            n.mate = node;
                        }
                        node.mate = m;
                        return true;
                    }
                } while (
                    (m = (n = m).mate) != null
                );
            }
            n.mate = node;
        }

        return true;
    }

    @Override
    public Sensor setProperty(
        @NotNull Object name
    ) {
        if (name != null) {
            Nodus[] tab = table;
            if (tab == null) {
                return null;
            }

            int hash = name.hashCode();
            hash = hash ^ (hash >>> 16);

            int m = tab.length - 1;
            Nodus n = tab[m & hash];

            for (; n != null; n = n.next) {
                if (n.hash == hash &&
                    (name.equals(n.id) ||
                        n.id.equals(name))) {
                    return n.setter;
                }
            }

            return null;
        }
        throw new IllegalStateException(
            "Received property name is invalid"
        );
    }

    public Sensor getProperty(
        @NotNull Object name
    ) {
        if (name != null) {
            Nodus[] tab = table;
            if (tab == null) {
                return null;
            }

            int hash = name.hashCode();
            hash = hash ^ (hash >>> 16);

            int m = tab.length - 1;
            Nodus n = tab[m & hash];

            for (; n != null; n = n.next) {
                if (n.hash == hash &&
                    (name.equals(n.id) ||
                        n.id.equals(name))) {
                    return n.getter;
                }
            }

            return null;
        }
        throw new IllegalStateException(
            "Received parameter name is invalid"
        );
    }

    @Override
    public Sensor setParameter(
        @NotNull Object name
    ) {
        if (name != null) {
            Nodus[] tab = table;
            if (tab == null) {
                return null;
            }

            int hash = name.hashCode();
            hash = hash ^ (hash >>> 16);

            int m = tab.length - 1;
            Nodus n = tab[m & hash];

            for (; n != null; n = n.next) {
                if (n.hash == hash &&
                    (name.equals(n.id) ||
                        n.id.equals(name))) {
                    return n.target;
                }
            }

            return null;
        }
        throw new IllegalStateException(
            "Received property name is invalid"
        );
    }

    @Override
    public Sensor getParameter(
        @NotNull Object name
    ) {
        if (name != null) {
            Nodus[] tab = table;
            if (tab == null) {
                return null;
            }

            int hash = name.hashCode();
            hash = hash ^ (hash >>> 16);

            int m = tab.length - 1;
            Nodus n = tab[m & hash];

            for (; n != null; n = n.next) {
                if (n.hash == hash &&
                    (name.equals(n.id) ||
                        n.id.equals(name))) {
                    return n.target;
                }
            }

            return null;
        }
        throw new IllegalStateException(
            "Received property name is invalid"
        );
    }
}
