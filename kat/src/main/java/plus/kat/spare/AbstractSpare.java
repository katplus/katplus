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
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static plus.kat.utils.Reflect.LOOKUP;

/**
 * @author kraity
 * @since 0.0.4
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractSpare<T> implements Subject<T> {

    protected String[] spaces;
    protected final String space;

    protected int flags;
    protected boolean expose;

    protected final Class<T> klass;
    protected final Supplier supplier;

    protected Entry<T>[] table;
    protected Element<T, ?> head, tail;

    protected AbstractSpare(
        @NotNull String space,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        this.space = space;
        this.klass = klass;
        this.supplier = supplier;
    }

    protected AbstractSpare(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        this.klass = klass;
        this.supplier = supplier;
        if (embed == null) {
            space = klass.getName();
        } else {
            flags = embed.mode();
            String[] names = embed.value();
            if (names.length == 0) {
                space = klass.getName();
            } else {
                space = (spaces = names)[0];
                expose = (embed.mode() & Embed.HIDDEN) == 0;
            }
        }
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
    public Supplier getSupplier() {
        return supplier;
    }

    @Override
    public void embed(
        @NotNull Supplier supplier
    ) {
        supplier.embed(klass, this);
        if (expose) {
            for (String space : spaces) {
                if (space.indexOf('.', 1) != -1) {
                    supplier.embed(space, this);
                }
            }
        }
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
        Element<T, ?> m = head;
        while (m != null) {
            Object data = m.invoke(value);
            if (data == null) {
                if ((m.flags & Expose.NOTNULL) == 0) {
                    chan.set(m.name, null);
                }
            } else {
                if ((m.flags & Expose.UNWRAPPED) == 0) {
                    chan.set(
                        m.name, m.coder, data
                    );
                } else {
                    Coder<?> coder = m.coder;
                    if (coder != null) {
                        coder.write(chan, data);
                    } else {
                        coder = supplier.lookup(
                            data.getClass()
                        );
                        if (coder != null) {
                            coder.write(chan, data);
                        }
                    }
                }
            }
            m = m.near;
        }
    }

    @Override
    public Spoiler flat(
        @NotNull T bean
    ) {
        return new Folder<>(
            bean, this
        );
    }

    @Override
    public boolean flat(
        @NotNull T bean,
        @NotNull Visitor visitor
    ) {
        Element<T, ?> m = head;
        while (m != null) {
            Object data = m.apply(bean);
            if (data != null || (m.flags & Expose.NOTNULL) == 0) {
                visitor.visit(
                    m.name, data
                );
            }
            m = m.near;
        }
        return true;
    }

    @Override
    public T cast(
        @Nullable Object data
    ) {
        return cast(
            data, supplier
        );
    }

    @Override
    public T cast(
        @Nullable Object data,
        @NotNull Supplier supplier
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

        try {
            return convert(
                data, supplier
            );
        } catch (Exception e) {
            return null;
        }
    }

    @NotNull
    @Override
    public T apply(
        @NotNull Spoiler spoiler
    ) throws Collapse {
        return apply(
            spoiler, supplier
        );
    }

    @NotNull
    @Override
    public T apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) throws Collapse {
        try {
            T bean = apply(
                Alias.EMPTY
            );
            update(
                bean, spoiler, supplier
            );
            return bean;
        } catch (Collapse e) {
            throw e;
        } catch (Throwable e) {
            throw new Collapse(
                "Error creating " + getType(), e
            );
        }
    }

    @NotNull
    @Override
    public T apply(
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return apply(
            supplier, resultSet
        );
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
        @NotNull Object data,
        @NotNull Supplier supplier
    ) throws Exception {
        if (data instanceof Map) {
            return apply(
                Spoiler.of((Map<?, ?>) data), supplier
            );
        }

        if (data instanceof Spoiler) {
            return apply(
                (Spoiler) data, supplier
            );
        }

        if (data instanceof ResultSet) {
            return apply(
                supplier, (ResultSet) data
            );
        }

        Spoiler spoiler =
            supplier.flat(data);
        if (spoiler == null) {
            return null;
        }

        return apply(spoiler, supplier);
    }

    @Override
    public Setter<T, ?> set(
        @NotNull Object key
    ) {
        return getProperty(key);
    }

    @Override
    public Getter<T, ?> get(
        @NotNull Object key
    ) {
        return getAttribute(key);
    }

    @Override
    public Member<T, ?> getProperty(
        @NotNull Object name
    ) {
        Entry<T>[] tab = table;
        if (tab == null) {
            return null;
        }

        int hash = name.hashCode();
        hash = hash ^ (hash >>> 16);

        int m = tab.length - 1;
        Entry<T> e = tab[m & hash];

        while (e != null) {
            if (e.hash == hash &&
                (name.equals(e.key) ||
                    e.key.equals(name))) {
                return e.art;
            }
            e = e.next;
        }

        return null;
    }

    @Override
    public Member<T, ?> getAttribute(
        @NotNull Object name
    ) {
        Entry<T>[] tab = table;
        if (tab == null) {
            return null;
        }

        int hash = name.hashCode();
        hash = hash ^ (hash >>> 16);

        int m = tab.length - 1;
        Entry<T> e = tab[m & hash];

        while (e != null) {
            if (e.hash == hash &&
                (name.equals(e.key) ||
                    e.key.equals(name))) {
                return e.att;
            }
            e = e.next;
        }

        return null;
    }

    @Override
    public Member<Object[], ?> getArgument(
        @NotNull Object name
    ) {
        Entry<T>[] tab = table;
        if (tab == null) {
            return null;
        }

        int hash = name.hashCode();
        hash = hash ^ (hash >>> 16);

        int m = tab.length - 1;
        Entry<T> e = tab[m & hash];

        while (e != null) {
            if (e.hash == hash &&
                (name.equals(e.key) ||
                    e.key.equals(name))) {
                return e.arg;
            }
            e = e.next;
        }

        return null;
    }

    /**
     * Returns true if the elem is settled otherwise false
     *
     * @param g the specified grade of elem
     * @param k the specified key of elem
     * @param m the specified elem to be settled
     */
    protected boolean setup(
        @NotNull int g,
        @NotNull String k,
        @NotNull Element<T, ?> m
    ) {
        Entry[] tab = table;
        if (tab == null) {
            tab = table = new Entry[4];
        }

        int i, h = k.hashCode();
        h = h ^ (h >>> 16);

        i = (tab.length - 1) & h;
        Entry<T> node, e = tab[i];

        if (e == null) {
            node = m;
            if (node.key != null) {
                node = new Entry<>();
            }
            tab[i] = node;
            node.att = m;
            node.key = k;
            node.hash = h;
        } else {
            while (true) {
                if (e.hash == h &&
                    (k.equals(e.key) ||
                        e.key.equals(k))) {
                    if (e.att == null) {
                        e.att = m;
                        break;
                    } else {
                        return false;
                    }
                }

                Entry<T> next = e.next;
                if (next != null) {
                    e = next;
                } else {
                    node = m;
                    if (node.key != null) {
                        node = new Entry<>();
                    }
                    e.next = node;
                    node.att = m;
                    node.key = k;
                    node.hash = h;
                    break;
                }
            }
        }

        Element<T, ?> n = head;
        Element<T, ?> u = null;

        m.name = k;
        m.grade = g;

        int d = m.index;
        if (d == -1 && g == 0) {
            if (n == null) {
                head = m;
                tail = m;
                return true;
            }

            if (n.index < -1) {
                head = m;
                tail = m;
                m.near = n;
            } else {
                u = tail;
                tail = m;
                if (u == null) {
                    do {
                        u = n;
                        n = n.near;
                    } while (
                        n != null
                    );
                } else {
                    m.near = u.near;
                }
                u.near = m;
            }
        } else {
            if (n == null) {
                head = m;
                return true;
            }

            if (d < 0) {
                int c;
                if (d != -1) {
                    n = tail;
                    if (n == null) n = head;
                }
                do {
                    if ((c = n.index) < d ||
                        (c == d && g > n.grade)) {
                        if (u == null) {
                            head = m;
                        } else {
                            u.near = m;
                        }
                        m.near = n;
                        return true;
                    }
                } while (
                    (n = (u = n).near) != null
                );
            } else {
                do {
                    int c = n.index;
                    if ((c < 0 || d < c) ||
                        (c == d && g > n.grade)) {
                        if (u == null) {
                            head = m;
                        } else {
                            u.near = m;
                        }
                        m.near = n;
                        return true;
                    }
                } while (
                    (n = (u = n).near) != null
                );
            }
            u.near = m;
        }

        return true;
    }

    /**
     * Returns true if the elem is settled otherwise false
     *
     * @param key  the specified key of elem
     * @param elem the specified elem to be settled
     */
    protected boolean setup(
        @Nullable Boolean fix,
        @NotNull Object key,
        @NotNull Element elem
    ) {
        Entry[] tab = table;
        if (tab == null) {
            tab = table = new Entry[4];
        }

        int hash = key.hashCode();
        hash = hash ^ (hash >>> 16);

        while (true) {
            int l = tab.length;
            int i = (l - 1) & hash;

            Entry node, e = tab[i];
            if (e == null) {
                node = elem;
                if (node.key != null) {
                    node = new Entry();
                }
                tab[i] = node;
                node.key = key;
                node.hash = hash;
                if (fix == null) {
                    node.arg = elem;
                } else {
                    node.art = elem;
                }
                return true;
            }

            for (int k = 0; ; k++) {
                if (e.hash == hash &&
                    (key.equals(e.key) ||
                        e.key.equals(key))) {
                    if (fix == null) {
                        e.arg = elem;
                    } else {
                        if (fix || e.art == null) {
                            e.art = elem;
                        } else {
                            return false;
                        }
                    }
                    return true;
                }

                Entry next = e.next;
                if (next != null) {
                    e = next;
                } else {
                    if (l <= k) break;
                    node = elem;
                    if (node.key != null) {
                        node = new Entry();
                    }
                    e.next = node;
                    node.key = key;
                    node.hash = hash;
                    if (fix == null) {
                        node.arg = elem;
                    } else {
                        node.art = elem;
                    }
                    return true;
                }
            }

            int s = l << 1;
            Entry[] bucket = new Entry[s];

            Entry n, b;
            int m = s - 1;

            for (int k = 0; k < l; k++) {
                if ((e = tab[k]) != null) {
                    tab[k] = null;
                    do {
                        n = e.next;
                        i = m & e.hash;

                        b = bucket[i];
                        if (b != null) {
                            e.next = b.next;
                            b.next = e;
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
     * Returns true if the elem is settled otherwise false
     */
    protected boolean setProperty(
        @NotNull Object key,
        @NotNull Element<T, ?> elem
    ) {
        return setup(
            true, key, elem
        );
    }

    /**
     * Returns true if the elem is settled otherwise false
     */
    protected boolean setArgument(
        @NotNull Object key,
        @NotNull Element<Object[], ?> elem
    ) {
        return setup(
            null, key, elem
        );
    }

    /**
     * Returns true if the elem is settled otherwise false
     */
    protected boolean setAttribute(
        @NotNull String key,
        @NotNull Element<T, ?> elem
    ) {
        return setup(
            0, key, elem
        );
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Folder<K>
        implements Spoiler {

        protected Element<K, ?> near;
        protected Element<K, ?> elem;

        protected K bean;
        protected AbstractSpare<K> spare;

        public Folder(
            @NotNull K bean,
            @NotNull AbstractSpare<K> abs
        ) {
            this.bean = bean;
            near = abs.head;
            this.spare = abs;
        }

        @Override
        public String getKey() {
            return elem.name;
        }

        @Override
        public Object getValue() {
            return elem.apply(bean);
        }

        @Override
        public Class<?> getType() {
            return elem.kind;
        }

        @Override
        public boolean hasNext() {
            Element<K, ?> n = near;
            if (n != null) {
                elem = n;
                near = n.near;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Entry<K> {
        private int hash;
        private Object key;
        private Entry<K> next;

        private Element<K, ?> att, art;
        private Element<Object[], ?> arg;
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public abstract static class Element<K, V>
        extends Entry<K> implements Member<K, V> {

        private int grade;
        private String name;
        private Element<K, ?> near;

        protected Type type;
        protected Class<?> kind;

        protected int flags;
        public final int index;

        protected Coder<?> coder;
        protected AnnotatedElement element;

        /**
         * <pre>{@code
         *  Element elem = new ElementImpl(0);
         * }</pre>
         */
        protected Element(
            int index
        ) {
            this.index = index;
        }

        /**
         * <pre>{@code
         *  Expose expose = ...
         *  Element elem = new ElementImpl(expose);
         * }</pre>
         */
        protected Element(
            @Nullable Expose elem
        ) {
            if (elem == null) {
                index = -1;
            } else {
                flags = elem.mode();
                index = elem.index();
            }
        }

        /**
         * <pre>{@code
         *  Element element = ...
         *  Element element0 = new ElementImpl(element);
         * }</pre>
         */
        protected Element(
            @NotNull Element<?, ?> elem
        ) {
            kind = elem.kind;
            type = elem.type;
            coder = elem.coder;
            flags = elem.flags;
            index = elem.index;
            element = elem.element;
        }

        /**
         * Returns the {@link Type} of {@link V}
         */
        @Override
        public Type getType() {
            return type;
        }

        /**
         * Returns the {@link Class} of {@link V}
         */
        @Override
        public Class<?> getKind() {
            return kind;
        }

        /**
         * Returns the {@link Coder} of {@link V}
         */
        @Override
        public Coder<?> assign(
            @NotNull Space space,
            @NotNull Supplier supplier
        ) {
            Coder<?> co = coder;
            if (co != null) {
                return co;
            }
            return supplier.lookup(
                kind, space
            );
        }

        /**
         * Sets the {@code element} of {@link Member}
         *
         * @param type the specified type
         */
        protected void setup(
            @NotNull Class<?> type
        ) {
            if (!type.isPrimitive()) {
                kind = type;
            } else {
                flags |= Expose.NOTNULL;
                kind = Reflect.wrap(type);
            }
        }

        /**
         * Sets the {@code element} of {@link Member}
         *
         * @param field the specified field
         */
        protected void setup(
            @NotNull Field field
        ) {
            element = field;
            type = field.getGenericType();
            setup(
                field.getType()
            );
        }

        /**
         * Sets the {@code element} of {@link Member}
         *
         * @param method the specified method
         */
        protected void setup(
            @NotNull Method method
        ) {
            element = method;
            switch (method.getParameterCount()) {
                case 0: {
                    Class<?> kind = method.getReturnType();
                    setup(kind);
                    type = kind;
                    break;
                }
                case 1: {
                    setup(method.getParameterTypes()[0]);
                    type = method.getGenericParameterTypes()[0];
                    break;
                }
                default: {
                    throw new NullPointerException(
                        "Unexpectedly, the parameter length of '" + method.getName() + "' is greater than '1'"
                    );
                }
            }
        }

        /**
         * Returns the annotation of the specified type
         */
        @Override
        public <A extends Annotation> A annotate(
            @NotNull Class<A> target
        ) {
            AnnotatedElement elem = element;
            if (elem != null) {
                return elem.getAnnotation(target);
            }
            return null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Accessor<K> extends Element<K, Object> {

        protected final MethodHandle getter;
        protected final MethodHandle setter;

        public Accessor(
            @Nullable Expose expose,
            @NotNull Field field,
            @NotNull Supplier supplier
        ) throws IllegalAccessException {
            super(expose);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            setup(field);
            coder = supplier.assign(
                expose, this
            );
            getter = LOOKUP.unreflectGetter(field);
            setter = LOOKUP.unreflectSetter(field);
        }

        public Accessor(
            @Nullable Expose expose,
            @NotNull Method method,
            @NotNull Supplier supplier
        ) throws IllegalAccessException {
            super(expose);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            setup(method);
            coder = supplier.assign(
                expose, this
            );
            if (method.getParameterCount() == 0) {
                setter = null;
                getter = LOOKUP.unreflect(method);
            } else {
                getter = null;
                setter = LOOKUP.unreflect(method);
            }
        }

        @Override
        public Object apply(
            @NotNull K bean
        ) {
            MethodHandle method = getter;
            if (method == null) {
                throw new Collapse(
                    "Getter is not supported"
                );
            } else {
                try {
                    return method.invoke(bean);
                } catch (Throwable e) {
                    throw new Collapse(
                        "Accessor call 'invoke' failed", e
                    );
                }
            }
        }

        @Override
        public boolean accept(
            @NotNull K bean,
            @Nullable Object value
        ) {
            MethodHandle method = setter;
            if (method == null) {
                throw new Collapse(
                    "Setter is not supported"
                );
            }
            if (value != null || (flags & Expose.NOTNULL) == 0) {
                try {
                    method.invoke(
                        bean, value
                    );
                    return true;
                } catch (Throwable e) {
                    throw new Collapse(
                        "Edge call 'invoke' failed", e
                    );
                }
            }
            return false;
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Argument extends Element<Object[], Object> {

        protected Annotation[] annotations;

        public Argument(
            int index,
            Expose expose,
            Field field,
            Supplier supplier
        ) {
            super(index);
            setup(field);
            coder = supplier.assign(
                expose, this
            );
        }

        public Argument(
            int index,
            Type type,
            Class<?> kind,
            Supplier supplier,
            Annotation[] annotations
        ) {
            super(index);
            this.type = type;
            setup(kind);
            this.annotations = annotations;
            coder = supplier.assign(
                annotate(Expose.class), this
            );
        }

        @Override
        public Object apply(
            @NotNull Object[] bean
        ) {
            return bean[index];
        }

        @Override
        public boolean accept(
            @NotNull Object[] bean,
            @Nullable Object value
        ) {
            if (value != null || (flags & Expose.NOTNULL) == 0) {
                bean[index] = value;
                return true;
            }
            return false;
        }

        @Override
        public <A extends Annotation> A annotate(
            @NotNull Class<A> target
        ) {
            AnnotatedElement elem = element;
            if (elem != null) {
                return elem.getAnnotation(target);
            }
            Annotation[] array = annotations;
            if (array != null) {
                for (Annotation a : array) {
                    if (a.annotationType() == target) {
                        return (A) a;
                    }
                }
            }
            return null;
        }
    }
}
