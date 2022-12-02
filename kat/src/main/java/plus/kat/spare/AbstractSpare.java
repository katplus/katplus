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
import plus.kat.anno.Format;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import static plus.kat.Flag.*;

/**
 * @author kraity
 * @since 0.0.4
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractSpare<T> implements Subject<T> {

    protected String[] spaces;
    protected long flags;
    protected final String space;

    protected final Class<T> klass;
    protected final Supplier supplier;

    protected Bundle<T>[] table;
    protected Explorer<T, ?> head, tail;

    protected AbstractSpare(
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        this(
            klass.getAnnotation(Embed.class), klass, supplier
        );
    }

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
            flags = embed.require();
            String[] names = embed.value();
            if (names.length == 0) {
                space = klass.getName();
            } else {
                space = (spaces = names)[0];
            }
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

        Class<?> clazz = Space.wipe(type);
        if (clazz == null) {
            throw new Collapse(
                "Failed to resolve " + type
            );
        }

        if (klass == clazz) {
            return apply();
        }

        if (klass.isAssignableFrom(clazz)) {
            Spare<T> spare =
                getSupplier().lookup(
                    (Class<T>) clazz
                );

            if (spare != null &&
                spare != this) {
                return spare.apply(type);
            }
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public Spare<T> join(
        @NotNull Supplier supplier
    ) {
        supplier.embed(
            klass, this
        );
        if (spaces != null) {
            for (String space : spaces) {
                if (space.indexOf('.', 1) != -1) {
                    supplier.embed(space, this);
                }
            }
        }
        return this;
    }

    @Override
    public Spare<T> drop(
        @NotNull Supplier supplier
    ) {
        supplier.revoke(
            klass, this
        );
        if (spaces != null) {
            for (String space : spaces) {
                if (space.indexOf('.', 1) != -1) {
                    supplier.revoke(space, this);
                }
            }
        }
        return this;
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
    public Supplier getSupplier() {
        return supplier;
    }

    @Override
    public T read(
        @NotNull Flag flag,
        @NotNull Chain value
    ) throws IOException {
        if (flag.isFlag(Flag.VALUE_AS_BEAN)) {
            Algo algo = Algo.of(value);
            if (algo == null) {
                return null;
            }
            return solve(
                algo, new Event<T>(value).with(flag)
            );
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
        for (Explorer<T, ?> m = head; m != null; m = m.near) {
            Object data = m.invoke(value);
            if (data == null) {
                if ((m.flags & NOTNULL) == 0) {
                    chan.set(
                        m.name, m.coder, null
                    );
                }
            } else {
                if ((m.flags & UNWRAPPED) == 0) {
                    chan.set(
                        m.name, m.coder, data
                    );
                } else {
                    Coder<?> it = m.coder;
                    if (it != null) {
                        it.write(chan, data);
                    } else {
                        it = chan.getSupplier().lookup(
                            data.getClass()
                        );
                        if (it != null) {
                            it.write(chan, data);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Spoiler flat(
        @NotNull T bean
    ) {
        if (bean != null) {
            return new Broker<>(
                bean, head
            );
        }
        throw new NullPointerException(
            "The specified bean is null"
        );
    }

    @Override
    public boolean flat(
        @NotNull T bean,
        @NotNull Visitor visitor
    ) {
        if (bean != null) {
            for (Explorer<T, ?> m = head; m != null; m = m.near) {
                Object data = m.apply(bean);
                if (data != null || (m.flags & NOTNULL) == 0) {
                    visitor.visit(
                        m.name, data
                    );
                }
            }
            return true;
        }
        throw new NullPointerException(
            "The specified bean is null"
        );
    }

    @Nullable
    public T cast(
        @Nullable Object object
    ) {
        return cast(
            object, supplier
        );
    }

    @Nullable
    public T cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (klass.isInstance(object)) {
            return (T) object;
        }

        if (object instanceof CharSequence) {
            CharSequence cs =
                (CharSequence) object;
            Algo algo = Algo.of(cs);
            if (algo == null) {
                return null;
            }
            return solve(
                algo, new Event<T>(cs).with(supplier)
            );
        }

        if (object instanceof Map) {
            return apply(
                Spoiler.of((Map<?, ?>) object), supplier
            );
        }

        if (object instanceof Spoiler) {
            return apply(
                (Spoiler) object, supplier
            );
        }

        if (object instanceof ResultSet) {
            try {
                return apply(
                    supplier, (ResultSet) object
                );
            } catch (SQLException e) {
                throw new IllegalStateException(
                    object + " cannot be converted to " + klass, e
                );
            }
        }

        Spoiler spoiler =
            supplier.flat(object);
        if (spoiler != null) {
            return apply(
                spoiler, supplier
            );
        } else {
            throw new IllegalStateException(
                "Not found the Spoiler of " + object
            );
        }
    }

    @NotNull
    public T apply(
        @NotNull Spoiler spoiler
    ) throws Collapse {
        return apply(
            spoiler, supplier
        );
    }

    @NotNull
    public T apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) throws Collapse {
        try {
            T bean = apply();
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
    public T apply(
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return apply(
            supplier, resultSet
        );
    }

    @NotNull
    public T apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        try {
            T bean = apply();
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

    @Override
    public Member<T, ?> set(
        @NotNull CharSequence name
    ) {
        Bundle<T>[] tab = table;
        if (tab == null) {
            return null;
        }

        int hash = name.hashCode();
        hash = hash ^ (hash >>> 16);

        int m = tab.length - 1;
        Bundle<T> b = tab[m & hash];

        while (b != null) {
            if (b.hash == hash &&
                (name.equals(b.key) ||
                    b.key.equals(name))) {
                return b.setter;
            }
            b = b.next;
        }

        return null;
    }

    @Override
    public Member<T, ?> get(
        @NotNull CharSequence key
    ) {
        Bundle<T>[] tab = table;
        if (tab == null) {
            return null;
        }

        int hash = key.hashCode();
        hash = hash ^ (hash >>> 16);

        int m = tab.length - 1;
        Bundle<T> b = tab[m & hash];

        while (b != null) {
            if (b.hash == hash &&
                (key.equals(b.key) ||
                    b.key.equals(key))) {
                return b.getter;
            }
            b = b.next;
        }

        return null;
    }

    @Override
    public Member<Object[], ?> arg(
        @NotNull CharSequence name
    ) {
        Bundle<T>[] tab = table;
        if (tab == null) {
            return null;
        }

        int hash = name.hashCode();
        hash = hash ^ (hash >>> 16);

        int m = tab.length - 1;
        Bundle<T> b = tab[m & hash];

        while (b != null) {
            if (b.hash == hash &&
                (name.equals(b.key) ||
                    b.key.equals(name))) {
                return b.target;
            }
            b = b.next;
        }

        return null;
    }

    /**
     * Returns the {@link Bundle} being used
     *
     * @param key  the specified key of bundle
     * @param node the specified bundle to be settled
     */
    @NotNull
    private <K> Bundle<K> bundle(
        @NotNull Object key,
        @NotNull Bundle<K> node
    ) {
        Bundle[] tab = table;
        if (tab == null) {
            tab = table = new Bundle[4];
        }

        int hash = key.hashCode();
        hash = hash ^ (hash >>> 16);

        while (true) {
            int l = tab.length;
            int i = (l - 1) & hash;

            Bundle b = tab[i];
            if (b == null) {
                if (node.key != null) {
                    node = new Bundle<>();
                }
                node.key = key;
                node.hash = hash;
                return tab[i] = node;
            }

            for (int k = 0; ; k++) {
                if (b.hash == hash &&
                    (key.equals(b.key) ||
                        b.key.equals(key))) {
                    return b;
                }

                Bundle next = b.next;
                if (next != null) {
                    b = next;
                } else {
                    if (l <= k) break;
                    if (node.key != null) {
                        node = new Bundle<>();
                    }
                    node.key = key;
                    node.hash = hash;
                    return b.next = node;
                }
            }

            int s = l << 1;
            Bundle[] bucket = new Bundle[s];

            Bundle m, n;
            int u = s - 1;

            for (int k = 0; k < l; k++) {
                if ((b = tab[k]) != null) {
                    tab[k] = null;
                    do {
                        n = b.next;
                        i = u & b.hash;

                        m = bucket[i];
                        if (m != null) {
                            b.next = m.next;
                            m.next = b;
                        } else {
                            b.next = null;
                            bucket[i] = b;
                        }
                    } while (
                        (b = n) != null
                    );
                }
            }
            tab = table = bucket;
        }
    }

    /**
     * Returns true if the {@link Explorer} is settled
     *
     * @param name the specified key of bundle
     * @param node the specified bundle to be settled
     */
    private boolean bundle(
        @NotNull int grade,
        @NotNull String name,
        @NotNull Explorer<T, ?> node
    ) {
        if (node.name == null) {
            node.name = name;
            node.grade = grade;
        } else {
            return false;
        }

        Explorer<T, ?> m = head;
        Explorer<T, ?> n = null;

        int d = node.index;
        if (d == -1 && grade == 0) {
            if (m == null) {
                head = node;
                tail = node;
                return true;
            }

            if (m.index < -1) {
                head = node;
                tail = node;
                node.near = m;
            } else {
                n = tail;
                tail = node;
                if (n == null) {
                    do {
                        n = m;
                        m = m.near;
                    } while (
                        m != null
                    );
                } else {
                    node.near = n.near;
                }
                n.near = node;
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
                        (c == d && grade > m.grade)) {
                        if (n == null) {
                            head = node;
                        } else {
                            n.near = node;
                        }
                        node.near = m;
                        return true;
                    }
                } while (
                    (m = (n = m).near) != null
                );
            } else {
                do {
                    int c = m.index;
                    if ((c < 0 || d < c) ||
                        (c == d && grade > m.grade)) {
                        if (n == null) {
                            head = node;
                        } else {
                            n.near = node;
                        }
                        node.near = m;
                        return true;
                    }
                } while (
                    (m = (n = m).near) != null
                );
            }
            n.near = node;
        }

        return true;
    }

    /**
     * Sets the specified property
     */
    protected void setReader(
        @NotNull String key,
        @NotNull Explorer<T, ?> node
    ) {
        bundle(key, node).setter = node;
    }

    /**
     * Adds the specified property.
     * Returns true if the node is settled
     */
    protected boolean addReader(
        @NotNull Object name,
        @NotNull Explorer<T, ?> node
    ) {
        Bundle<T> e = bundle(name, node);
        if (e.setter == null) {
            e.setter = node;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the specified attribute.
     * Returns true if the node is settled
     */
    protected boolean setWriter(
        @NotNull String name,
        @NotNull Explorer<T, ?> node
    ) {
        return setWriter(
            0, name, node
        );
    }

    /**
     * Sets the specified attribute.
     * Returns true if the node is settled
     */
    protected boolean setWriter(
        @NotNull int grade,
        @NotNull String name,
        @NotNull Explorer<T, ?> node
    ) {
        Bundle<T> e = bundle(name, node);
        if (e.getter != null) {
            return false;
        } else {
            e.getter = node;
            return bundle(
                grade, name, node
            );
        }
    }

    /**
     * Sets the specified property.
     * Returns true if the node is settled
     */
    protected boolean setProperty(
        @NotNull String name,
        @NotNull Explorer<T, ?> node
    ) {
        return setProperty(
            0, name, node
        );
    }

    /**
     * Sets the specified property.
     * Returns true if the node is settled
     */
    protected boolean setProperty(
        @NotNull int grade,
        @NotNull String name,
        @NotNull Explorer<T, ?> node
    ) {
        Bundle<T> e = bundle(name, node);
        e.setter = node;
        if (e.getter != null) {
            return false;
        } else {
            e.getter = node;
            return bundle(
                grade, name, node
            );
        }
    }

    /**
     * Sets the specified parameter
     */
    protected void setParameter(
        @NotNull String name,
        @NotNull Explorer<Object[], ?> node
    ) {
        bundle(name, node).target = node;
    }

    /**
     * Adds the specified parameter.
     * Returns true if the node is settled
     */
    protected boolean addParameter(
        @NotNull String name,
        @NotNull Explorer<Object[], ?> node
    ) {
        Bundle<Object[]> e = bundle(name, node);
        if (e.target == null) {
            e.target = node;
            return true;
        } else {
            return false;
        }
    }

    /**
     * @author kraity
     * @since 0.0.5
     */
    public static class Broker<T>
        implements Spoiler {

        protected final T bean;
        protected Explorer<T, ?> near;
        protected Explorer<T, ?> node;

        protected Broker(
            @NotNull T bean,
            @NotNull Explorer<T, ?> near
        ) {
            this.bean = bean;
            this.near = near;
        }

        @Override
        public boolean hasNext() {
            Explorer<T, ?> n = near;
            if (n != null) {
                node = n;
                near = n.near;
                return true;
            }
            return false;
        }

        @Override
        public Class<?> getType() {
            return node.clazz;
        }

        @Override
        public String getKey() {
            return node.name;
        }

        @Override
        public Object getValue() {
            return node.apply(bean);
        }
    }

    /**
     * @author kraity
     * @since 0.0.5
     */
    public static class Bundle<T> {
        private int hash;
        private Object key;
        private Bundle<T> next;

        private Member<T, ?> setter;
        private Member<T, ?> getter;
        private Member<Object[], ?> target;
    }

    /**
     * @author kraity
     * @since 0.0.5
     */
    public abstract static class Explorer<T, V>
        extends Bundle<T> implements Member<T, V> {

        private int grade;
        private String name;
        private Explorer<T, ?> near;

        protected int index;
        protected long flags;

        protected Type klass;
        protected Class<?> clazz;

        protected Coder<?> coder;
        protected AnnotatedElement element;

        /**
         * <pre>{@code
         *  Explorer node = new ExplorerImpl(0);
         * }</pre>
         */
        protected Explorer(
            int index
        ) {
            this.index = index;
        }

        /**
         * <pre>{@code
         *  Expose expose = ...
         *  Explorer node = new ExplorerImpl(expose);
         * }</pre>
         */
        protected Explorer(
            @Nullable Expose elem
        ) {
            if (elem == null) {
                index = -1;
            } else {
                index = elem.index();
                flags = elem.require();
            }
        }

        /**
         * <pre>{@code
         *  Explorer node = ...
         *  Explorer node0 = new ExplorerImpl(node);
         * }</pre>
         */
        protected Explorer(
            @NotNull Explorer<?, ?> node
        ) {
            coder = node.coder;
            flags = node.flags;
            index = node.index;
            clazz = node.clazz;
            klass = node.klass;
            element = node.element;
        }

        /**
         * <pre>{@code
         *  Field field = ...
         *  Expose expose = ...
         *  Explorer medium = new ExplorerImpl(field, expose);
         * }</pre>
         */
        protected Explorer(
            @NotNull Field field,
            @Nullable Expose expose
        ) {
            this(expose);
            element = field;
            Class<?> cls = field.getType();
            if (cls.isPrimitive()) {
                flags |= NOTNULL;
                klass = clazz = Space.wrap(cls);
            } else {
                clazz = cls;
                klass = field.getGenericType();
            }
        }

        /**
         * <pre>{@code
         *  Method method = ...
         *  Expose expose = ...
         *  Explorer medium = new ExplorerImpl(method, expose);
         * }</pre>
         */
        protected Explorer(
            @NotNull Method method,
            @Nullable Expose expose
        ) {
            this(expose);
            element = method;
            switch (method.getParameterCount()) {
                case 0: {
                    Class<?> cls = method.getReturnType();
                    if (cls.isPrimitive()) {
                        flags |= NOTNULL;
                        klass = clazz = Space.wrap(cls);
                    } else {
                        clazz = cls;
                        klass = method.getGenericReturnType();
                    }
                    break;
                }
                case 1: {
                    Class<?> cls = method.getParameterTypes()[0];
                    if (cls.isPrimitive()) {
                        flags |= NOTNULL;
                        klass = clazz = Space.wrap(cls);
                    } else {
                        clazz = cls;
                        klass = method.getGenericParameterTypes()[0];
                    }
                    break;
                }
                default: {
                    throw new NullPointerException(
                        "Unexpectedly, the parameter length of `"
                            + method.getName() + "` is greater than '1'"
                    );
                }
            }
        }

        protected void init(
            Expose expose,
            Subject<?> subject
        ) {
            Class<?> with;
            if (expose == null || (with =
                expose.with()) == Coder.class) {
                Format format = custom(Format.class);
                if (format != null) {
                    if (clazz == Date.class) {
                        coder = new DateSpare(format);
                    }
                }
                return;
            }

            if (!Coder.class.
                isAssignableFrom(with)) {
                coder = subject.getSupplier().lookup(with);
            } else if (with == ByteArrayCoder.class) {
                coder = ByteArrayCoder.INSTANCE;
            } else try {
                Constructor<?>[] cs = with
                    .getDeclaredConstructors();
                Constructor<?> d, c = cs[0];
                for (int i = 1; i < cs.length; i++) {
                    d = cs[i];
                    if (c.getParameterCount() <=
                        d.getParameterCount()) c = d;
                }

                Object[] args;
                int size = c.getParameterCount();

                if (size == 0) {
                    args = ArraySpare.EMPTY_ARRAY;
                } else {
                    args = new Object[size];
                    Class<?>[] cls =
                        c.getParameterTypes();
                    for (int i = 0; i < size; i++) {
                        Class<?> m = cls[i];
                        if (m == Class.class) {
                            args[i] = clazz;
                        } else if (m == Type.class) {
                            args[i] = klass;
                        } else if (m == Expose.class) {
                            args[i] = expose;
                        } else if (m == Supplier.class) {
                            args[i] = subject.getSupplier();
                        } else if (m.isPrimitive()) {
                            args[i] = Spare.lookup(m).apply();
                        } else if (m.isAnnotation()) {
                            args[i] = custom(
                                (Class<? extends Annotation>) m
                            );
                        }
                    }
                }

                if (!c.isAccessible()) {
                    c.setAccessible(true);
                }
                coder = (Coder<?>) c.newInstance(args);
            } catch (Exception e) {
                throw new IllegalStateException(
                    "Failed to build the '"
                        + this + "' coder: " + with, e
                );
            }
        }

        /**
         * Returns the class of {@link V}
         */
        @Override
        public Class<?> kind() {
            return clazz;
        }

        /**
         * Returns the type of {@link V}
         */
        @Override
        public Type type() {
            return klass;
        }

        /**
         * Returns the flags of {@link V}
         */
        @Override
        public long flags() {
            return flags;
        }

        /**
         * Returns the coder of {@link V}
         */
        @Override
        public Coder<?> coder() {
            return coder;
        }

        /**
         * Returns the annotation of the {@code class}
         */
        @Override
        public <A extends Annotation> A custom(
            @NotNull Class<A> clazz
        ) {
            AnnotatedElement elem = element;
            if (elem != null) {
                return elem.getAnnotation(clazz);
            } else {
                return clazz.getAnnotation(clazz);
            }
        }
    }

    /**
     * @see MethodHandles
     * @since 0.0.5
     */
    protected static final MethodHandles.Lookup
        LOOKUP = MethodHandles.lookup();

    /**
     * @author kraity
     * @since 0.0.5
     */
    public static class Callable<T> extends Explorer<T, Object> {

        protected MethodHandle getter;
        protected MethodHandle setter;

        public Callable(
            Expose expose,
            Field field,
            Subject<?> subject
        ) {
            this(
                expose, field, false, subject
            );
        }

        public Callable(
            Expose expose,
            Field field,
            Boolean status,
            Subject<?> subject
        ) {
            super(field, expose);
            init(expose, subject);

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            try {
                if (status == null) {
                    getter = LOOKUP.unreflectGetter(field);
                } else {
                    setter = LOOKUP.unreflectSetter(field);
                    if (!status) {
                        getter = LOOKUP.unreflectGetter(field);
                    }
                }
            } catch (Exception e) {
                throw new FatalCrash(
                    field + " cannot be reflected by MethodHandle", e
                );
            }
        }

        public Callable(
            Expose expose,
            Method method,
            Subject<?> subject
        ) {
            super(method, expose);
            init(expose, subject);

            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

            try {
                if (method.getParameterCount() != 0) {
                    setter = LOOKUP.unreflect(method);
                } else {
                    getter = LOOKUP.unreflect(method);
                }
            } catch (Exception e) {
                throw new FatalCrash(
                    method + " cannot be reflected by MethodHandle", e
                );
            }
        }

        @Override
        public Object apply(
            @NotNull T bean
        ) {
            MethodHandle method = getter;
            if (method == null) {
                throw new FatalCrash(
                    "Getter is not supported"
                );
            } else {
                try {
                    return method.invoke(bean);
                } catch (Throwable e) {
                    throw new FatalCrash(
                        method + " call 'invoke' failed", e
                    );
                }
            }
        }

        @Override
        public boolean accept(
            @NotNull T bean,
            @Nullable Object value
        ) {
            MethodHandle method = setter;
            if (method == null) {
                throw new FatalCrash(
                    "Setter is not supported"
                );
            }
            if (value != null || (flags & NOTNULL) == 0) {
                try {
                    method.invoke(
                        bean, value
                    );
                    return true;
                } catch (Throwable e) {
                    throw new FatalCrash(
                        method + " call 'invoke' failed", e
                    );
                }
            }
            return false;
        }
    }

    /**
     * @author kraity
     * @since 0.0.5
     */
    public static class Argument extends Explorer<Object[], Object> {

        protected Annotation[] annotations;

        public Argument(
            int index,
            Expose expose,
            Field field,
            AbstractSpare<?> subject
        ) {
            super(index);
            element = field;
            Class<?> cls = field.getType();
            if (cls.isPrimitive()) {
                flags |= NOTNULL;
                klass = clazz = Space.wrap(cls);
            } else {
                clazz = cls;
                klass = field.getGenericType();
            }
            init(expose, subject);
        }

        public Argument(
            int index,
            Type type,
            Class<?> kind,
            AbstractSpare<?> subject,
            Annotation[] annotations
        ) {
            super(index);
            klass = type;
            if (kind.isPrimitive()) {
                flags |= NOTNULL;
                clazz = Space.wrap(kind);
            } else {
                clazz = kind;
            }
            this.annotations = annotations;
            init(custom(Expose.class), subject);
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
            if (value != null || (flags & NOTNULL) == 0) {
                bean[index] = value;
                return true;
            }
            return false;
        }

        @Override
        public <A extends Annotation> A custom(
            @NotNull Class<A> clazz
        ) {
            AnnotatedElement elem = element;
            if (elem != null) {
                return elem.getAnnotation(clazz);
            }
            Annotation[] array = annotations;
            if (array != null) {
                for (Annotation a : array) {
                    if (a.annotationType() == clazz) {
                        return (A) a;
                    }
                }
            }
            return null;
        }
    }
}
