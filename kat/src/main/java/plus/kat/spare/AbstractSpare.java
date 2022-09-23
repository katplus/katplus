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
import plus.kat.stream.*;
import plus.kat.utils.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static plus.kat.It.*;

/**
 * @author kraity
 * @since 0.0.4
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractSpare<T> implements Subject<T> {

    protected String[] spaces;
    protected int flags;
    protected final String space;

    protected final Class<T> klass;
    protected final Supplier supplier;

    protected Bundle<T>[] table;
    protected Medium<T, ?> head, tail;

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
        if (spaces != null) {
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
            return Convert.toObject(
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
        for (Medium<T, ?> m = head; m != null; m = m.near) {
            m.serialize(
                chan, m.invoke(value)
            );
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
        for (Medium<T, ?> m = head; m != null; m = m.near) {
            Object data = m.apply(bean);
            if (data != null || (m.flags & NotNull) == 0) {
                visitor.visit(
                    m.name, data
                );
            }
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
            return Convert.toObject(
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
    public Member<T, ?> set(
        @NotNull Object name
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
        @NotNull Object key
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
        @NotNull Object name
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
     * Returns true if the {@link Medium} is settled
     *
     * @param name the specified key of bundle
     * @param node the specified bundle to be settled
     */
    private boolean bundle(
        @NotNull int grade,
        @NotNull String name,
        @NotNull Medium<T, ?> node
    ) {
        if (node.name == null) {
            node.name = name;
            node.grade = grade;
        } else {
            return false;
        }

        Medium<T, ?> m = head;
        Medium<T, ?> n = null;

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
        @NotNull Object key,
        @NotNull Medium<T, ?> node
    ) {
        bundle(key, node).setter = node;
    }

    /**
     * Sets the specified property.
     * Returns true if the node is settled
     */
    protected boolean setReader(
        @NotNull boolean fix,
        @NotNull Object key,
        @NotNull Medium<T, ?> node
    ) {
        Bundle<T> e = bundle(key, node);
        if (fix || e.setter == null) {
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
        @NotNull Medium<T, ?> node
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
        @NotNull Medium<T, ?> node
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
        @NotNull Medium<T, ?> node
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
        @NotNull Medium<T, ?> node
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
        @NotNull Object key,
        @NotNull Medium<Object[], ?> node
    ) {
        bundle(key, node).target = node;
    }

    /**
     * Sets the specified parameter.
     * Returns true if the node is settled
     */
    protected boolean setParameter(
        @NotNull boolean fix,
        @NotNull Object key,
        @NotNull Medium<Object[], ?> node
    ) {
        Bundle<Object[]> e = bundle(key, node);
        if (fix || e.target == null) {
            e.target = node;
            return true;
        } else {
            return false;
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Folder<K>
        implements Spoiler {

        protected Medium<K, ?> near;
        protected Medium<K, ?> node;

        protected final K bean;
        protected AbstractSpare<K> spare;

        public Folder(
            @NotNull K bean,
            @NotNull AbstractSpare<K> spare
        ) {
            near = spare.head;
            this.bean = bean;
            this.spare = spare;
        }

        @Override
        public String getKey() {
            return node.name;
        }

        @Override
        public Object getValue() {
            return node.apply(bean);
        }

        @Override
        public Class<?> getType() {
            return node.clazz;
        }

        @Override
        public boolean hasNext() {
            Medium<K, ?> n = near;
            if (n != null) {
                node = n;
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
    public static class Bundle<K> {
        private int hash;
        private Object key;
        private Bundle<K> next;

        private Member<K, ?> setter;
        private Member<K, ?> getter;
        private Member<Object[], ?> target;
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public abstract static class Medium<K, V>
        extends Bundle<K> implements Member<K, V> {

        private int grade;
        private String name;
        private Medium<K, ?> near;

        protected Type actual;
        protected Class<?> clazz;

        protected int flags;
        public final int index;

        protected Coder<?> coder;
        protected AnnotatedElement element;

        /**
         * <pre>{@code
         *  Medium node = new MediumImpl(0);
         * }</pre>
         */
        protected Medium(
            int index
        ) {
            this.index = index;
        }

        /**
         * <pre>{@code
         *  Expose expose = ...
         *  Medium node = new MediumImpl(expose);
         * }</pre>
         */
        protected Medium(
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
         *  Medium node = ...
         *  Medium node0 = new MediumImpl(node);
         * }</pre>
         */
        protected Medium(
            @NotNull Medium<?, ?> node
        ) {
            coder = node.coder;
            clazz = node.clazz;
            index = node.index;
            flags = node.flags;
            actual = node.actual;
            element = node.element;
        }

        /**
         * <pre>{@code
         *  Field field = ...
         *  Expose expose = ...
         *  Medium medium = new MediumImpl(field, expose);
         * }</pre>
         */
        protected Medium(
            @NotNull Field field,
            @Nullable Expose expose
        ) {
            this(expose);
            element = field;
            Class<?> type = field.getType();
            actual = field.getGenericType();
            if (!type.isPrimitive()) {
                clazz = type;
            } else {
                flags |= NotNull;
                clazz = Find.kind(type);
            }
        }

        /**
         * <pre>{@code
         *  Method method = ...
         *  Expose expose = ...
         *  Medium medium = new MediumImpl(method, expose);
         * }</pre>
         */
        protected Medium(
            @NotNull Method method,
            @Nullable Expose expose
        ) {
            this(expose);
            Class<?> type;
            element = method;
            switch (method.getParameterCount()) {
                case 0: {
                    actual = type = method.getReturnType();
                    break;
                }
                case 1: {
                    type = method.getParameterTypes()[0];
                    actual = method.getGenericParameterTypes()[0];
                    break;
                }
                default: {
                    throw new NullPointerException(
                        "Unexpectedly, the parameter length of `"
                            + method.getName() + "` is greater than '1'"
                    );
                }
            }
            if (!type.isPrimitive()) {
                clazz = type;
            } else {
                flags |= NotNull;
                clazz = Find.kind(type);
            }
        }

        /**
         * Returns the {@link Class} of {@link V}
         */
        @Override
        public Class<?> getType() {
            return clazz;
        }

        /**
         * Returns {@code true} if processed
         */
        @Override
        public boolean serialize(
            @NotNull Chan chan,
            @Nullable Object value
        ) throws IOException {
            if (value == null) {
                if ((flags & NotNull) == 0) {
                    chan.set(name, null);
                }
            } else {
                if ((flags & unwrapped) == 0) {
                    chan.set(
                        name, coder, value
                    );
                } else {
                    Coder<?> it = coder;
                    if (it != null) {
                        it.write(chan, value);
                    } else {
                        it = chan.getSupplier().lookup(
                            value.getClass()
                        );
                        if (it == null) {
                            return false;
                        } else {
                            it.write(chan, value);
                        }
                    }
                }
            }
            return true;
        }

        /**
         * Returns the {@link Coder} of {@link V}
         */
        @Override
        public Coder<?> deserialize(
            @NotNull Space space,
            @Nullable Supplier supplier
        ) throws IOException {
            Coder<?> it = coder;
            if (it != null) {
                return it;
            }

            if (supplier != null) {
                return supplier.lookup(
                    clazz, space
                );
            }

            throw new UnexpectedCrash(
                "Unexpectedly, supplier not found"
            );
        }

        /**
         * Returns the flags of {@link V}
         */
        @Override
        public int getFlags() {
            return flags;
        }

        /**
         * Returns the {@link Type} of {@link V}
         */
        @Override
        public Type getActual() {
            return actual;
        }

        /**
         * Returns the {@link Coder} of {@link V}
         */
        @Nullable
        public Coder<?> getCoder() {
            return coder;
        }

        /**
         * Returns the {@link AnnotatedElement} of {@link V}
         */
        @Override
        public AnnotatedElement getAnnotated() {
            return element;
        }

        /**
         * Returns the annotation of the specified type
         */
        @Override
        public <A extends Annotation> A getAnnotation(
            @NotNull Class<A> target
        ) {
            AnnotatedElement elem = element;
            if (elem != null) {
                return elem.getAnnotation(target);
            } else {
                return clazz.getAnnotation(target);
            }
        }
    }

    /**
     * @see MethodHandles
     * @since 0.0.4
     */
    protected static final MethodHandles.Lookup
        LOOKUP = MethodHandles.lookup();

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Accessor<K> extends Medium<K, Object> {

        protected MethodHandle getter;
        protected MethodHandle setter;

        public Accessor(
            Expose expose,
            Field field,
            Subject<?> subject
        ) throws IllegalAccessException {
            this(
                expose, field, false, subject
            );
        }

        public Accessor(
            Expose expose,
            Field field,
            Boolean status,
            Subject<?> subject
        ) throws IllegalAccessException {
            super(field, expose);
            coder = subject.inflate(
                expose, this
            );

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            if (status == null) {
                getter = LOOKUP.unreflectGetter(field);
            } else {
                setter = LOOKUP.unreflectSetter(field);
                if (!status) {
                    getter = LOOKUP.unreflectGetter(field);
                }
            }
        }

        public Accessor(
            Expose expose,
            Method method,
            Subject<?> subject
        ) throws IllegalAccessException {
            super(method, expose);
            coder = subject.inflate(
                expose, this
            );

            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            if (method.getParameterCount() != 0) {
                setter = LOOKUP.unreflect(method);
            } else {
                getter = LOOKUP.unreflect(method);
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
            if (value != null || (flags & NotNull) == 0) {
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
    public static class Argument extends Medium<Object[], Object> {

        protected Annotation[] annotations;

        public Argument(
            int index,
            Expose expose,
            Field field,
            Subject<?> subject
        ) {
            super(index);
            element = field;
            Class<?> type = field.getType();
            actual = field.getGenericType();
            if (!type.isPrimitive()) {
                clazz = type;
            } else {
                flags |= NotNull;
                clazz = Find.kind(type);
            }
            coder = subject.inflate(
                expose, this
            );
        }

        public Argument(
            int index,
            Type type,
            Class<?> kind,
            Subject<?> subject,
            Annotation[] annotations
        ) {
            super(index);
            this.actual = type;
            if (!kind.isPrimitive()) {
                clazz = kind;
            } else {
                flags |= NotNull;
                clazz = Find.kind(kind);
            }
            this.annotations = annotations;
            coder = subject.inflate(
                getAnnotation(Expose.class), this
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
            if (value != null || (flags & NotNull) == 0) {
                bean[index] = value;
                return true;
            }
            return false;
        }

        @Override
        public <A extends Annotation> A getAnnotation(
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
