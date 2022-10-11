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
package plus.kat.entity;

import plus.kat.anno.Expose;
import plus.kat.anno.Format;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.spare.*;
import plus.kat.utils.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.*;
import java.util.Date;

/**
 * @author kraity
 * @since 0.0.4
 */
@SuppressWarnings("unchecked")
public interface Subject<K> extends Spare<K>, Maker<K> {
    /**
     * If this {@link Subject} can create an instance,
     * it returns it, otherwise it will throw {@link Collapse}
     *
     * @return {@link K}, it is not null
     * @throws Collapse If a failure occurs
     */
    @NotNull
    default K apply() {
        throw new Collapse(
            "Failure occurs"
        );
    }

    /**
     * If this {@link Subject} can create an instance,
     * it returns it, otherwise it will throw {@link Collapse}
     *
     * @param args the specified args
     * @return {@link K}, it is not null
     * @throws Collapse If a failure occurs
     */
    @NotNull
    default K apply(
        @NotNull Object[] args
    ) {
        throw new Collapse(
            "Failure occurs"
        );
    }

    /**
     * Returns the flag of {@link K}
     */
    @Override
    default Boolean getFlag() {
        return Boolean.TRUE;
    }

    /**
     * Check if {@code clazz} is a parent Class of {@link K}
     * or this {@link Subject} can create an instance of {@code clazz}
     */
    @Override
    default boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz.isAssignableFrom(getType());
    }

    /**
     * Returns a {@link Builder} of {@link K}
     *
     * @param type the specified actual type
     */
    @Nullable
    @Override
    default Builder<K> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0<>(this);
    }

    /**
     * Returns a set-capable {@link Member}
     * of the specified property {@code name}
     *
     * @param name the property name of the bean
     * @return {@link Member} or {@code null}
     * @throws NullPointerException If the alias is null
     */
    @Override
    default Member<K, ?> set(
        @NotNull Object name
    ) {
        return null;
    }

    /**
     * Returns a get-capable {@link Member}
     * of the specified property {@code name}
     *
     * @param name the property name of the bean
     * @return {@link Member} or {@code null}
     * @throws NullPointerException If the alias is null
     */
    @Override
    default Member<K, ?> get(
        @NotNull Object name
    ) {
        return null;
    }

    /**
     * Returns a set-capable {@link Member}
     * of the specified parameter {@code name}
     *
     * @param name the parameter name of the bean
     * @return {@link Member} or {@code null}
     * @throws NullPointerException If the alias is null
     */
    @Nullable
    default Member<Object[], ?> arg(
        @NotNull Object name
    ) {
        return null;
    }

    /**
     * Copy the property values of the specified spoiler into the given specified bean
     *
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     */
    @Override
    default int update(
        @NotNull K entity,
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) {
        int rows = 0;
        while (spoiler.hasNext()) {
            Member<K, ?> setter = set(
                spoiler.getKey()
            );
            if (setter == null) {
                continue;
            }

            Object value = spoiler.getValue();
            if (value == null) {
                continue;
            }

            Class<?> clazz = setter.getType();
            if (clazz.isInstance(value)) {
                rows++;
                setter.invoke(
                    entity, value
                );
                continue;
            }

            Spare<?> spare = supplier.lookup(clazz);
            if (spare != null) {
                rows++;
                setter.invoke(
                    entity, spare.cast(
                        value, supplier
                    )
                );
            }
        }

        return rows;
    }

    /**
     * Copy the property values of the specified spoiler into the given specified group
     *
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     */
    default int update(
        @NotNull Object[] group,
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) {
        int rows = 0;
        while (spoiler.hasNext()) {
            Member<Object[], ?> setter =
                arg(spoiler.getKey());
            if (setter == null) {
                continue;
            }

            Object value = spoiler.getValue();
            if (value == null) {
                continue;
            }

            Class<?> clazz = setter.getType();
            if (clazz.isInstance(value)) {
                rows++;
                setter.invoke(
                    group, value
                );
                continue;
            }

            Spare<?> spare = supplier.lookup(clazz);
            if (spare != null) {
                rows++;
                setter.invoke(
                    group, spare.cast(
                        value, supplier
                    )
                );
            }
        }

        return rows;
    }

    /**
     * Copy the property values of the specified resultSet into the given specified bean
     *
     * @return the number of rows affected
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the parameters contains null
     */
    @Override
    default int update(
        @NotNull K entity,
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        ResultSetMetaData meta =
            resultSet.getMetaData();
        int rows = 0, count =
            meta.getColumnCount();

        for (int i = 1; i <= count; i++) {
            String name = meta
                .getColumnLabel(i);
            if (name == null) {
                name = meta.getColumnName(i);
            }

            Member<K, ?> setter = set(name);
            if (setter == null) {
                throw new SQLCrash(
                    "Cannot find the `" + name + "` property of " + getType()
                );
            }

            Object value = resultSet
                .getObject(i);
            if (value == null) {
                continue;
            }

            Class<?> clazz = setter.getType();
            if (clazz.isInstance(value)) {
                rows++;
                setter.invoke(entity, value);
                continue;
            }

            Object result = supplier.cast(clazz, value);
            if (result != null) {
                rows++;
                setter.invoke(entity, result);
                continue;
            }

            throw new SQLCrash(
                "Unable to convert the `" + name + "` property type of "
                    + getType() + " from " + value.getClass() + " to " + clazz
            );
        }

        return rows;
    }

    /**
     * Copy the property values of the specified spoiler into the given specified group
     *
     * @return the number of rows affected
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the parameters contains null
     */
    default int update(
        @NotNull Object[] group,
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        ResultSetMetaData meta =
            resultSet.getMetaData();
        int rows = 0, count =
            meta.getColumnCount();

        for (int i = 1; i <= count; i++) {
            String name = meta
                .getColumnLabel(i);
            if (name == null) {
                name = meta.getColumnName(i);
            }

            Member<Object[], ?> setter = arg(name);
            if (setter == null) {
                throw new SQLCrash(
                    "Cannot find the `" + name + "` argument of " + getType()
                );
            }

            Object value = resultSet
                .getObject(i);
            if (value == null) {
                continue;
            }

            Class<?> clazz = setter.getType();
            if (clazz.isInstance(value)) {
                rows++;
                setter.invoke(group, value);
                continue;
            }

            Object result = supplier.cast(clazz, value);
            if (result != null) {
                rows++;
                setter.invoke(group, result);
                continue;
            }

            throw new SQLCrash(
                "Unable to convert the `" + name + "` argument type of "
                    + getType() + " from " + value.getClass() + " to " + clazz
            );
        }

        return rows;
    }

    /**
     * Returns the custom {@link Coder} for the {@link Member}
     *
     * @param expose the specified expose
     * @param member the specified member to be solved
     * @return {@link Coder} or {@code null}
     */
    @Nullable
    default Coder<?> inflate(
        @Nullable Expose expose,
        @Nullable Member<?, ?> member
    ) {
        Class<?> clazz;
        if (member == null) {
            return null;
        }

        if (expose == null || (clazz =
            expose.with()) == Coder.class) {
            Format format = member
                .getAnnotation(Format.class);
            if (format != null) {
                Class<?> type = member.getType();
                if (type == Date.class) {
                    return new DateSpare(format);
                } else if (type == Instant.class) {
                    return new InstantSpare(format);
                } else if (type == LocalDate.class) {
                    return new LocalDateSpare(format);
                } else if (type == LocalTime.class) {
                    return new LocalTimeSpare(format);
                } else if (type == LocalDateTime.class) {
                    return new LocalDateTimeSpare(format);
                } else if (type == ZonedDateTime.class) {
                    return new ZonedDateTimeSpare(format);
                }
            }
            return null;
        }

        if (!Coder.class.
            isAssignableFrom(clazz)) {
            return getSupplier().lookup(clazz);
        }

        if (clazz == ByteArrayCoder.class) {
            return ByteArrayCoder.INSTANCE;
        }

        try {
            Constructor<?>[] cs = clazz
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
                        args[i] = member.getType();
                    } else if (m == Type.class) {
                        args[i] = member.getActual();
                    } else if (m == Expose.class) {
                        args[i] = expose;
                    } else if (m == Supplier.class) {
                        args[i] = getSupplier();
                    } else if (m.isPrimitive()) {
                        args[i] = Find.value(m);
                    } else if (m.isAnnotation()) {
                        args[i] = member.getAnnotation(
                            (Class<? extends Annotation>) m
                        );
                    }
                }
            }

            if (!c.isAccessible()) {
                c.setAccessible(true);
            }
            return (Coder<?>) c.newInstance(args);
        } catch (Exception e) {
            // Nothing
        }

        return null;
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    interface Member<K, V> extends
        Setter<K, V>, Getter<K, V> {
        /**
         * Returns the {@link Class} of {@link V}
         */
        @NotNull
        Class<?> getType();

        /**
         * Gets the property of the bean
         *
         * @see #get(Object)
         * @see Getter#apply(Object)
         */
        @Nullable
        @Override
        default V apply(
            @NotNull K bean
        ) {
            throw new Collapse(
                "Unsupported"
            );
        }

        /**
         * Sets the specified value to the bean
         *
         * @see #set(Object)
         * @see Setter#accept(Object, Object)
         */
        @Override
        default boolean accept(
            @NotNull K bean,
            @Nullable V value
        ) {
            throw new Collapse(
                "Unsupported"
            );
        }

        /**
         * Returns {@code true} if processed
         */
        default boolean serialize(
            @NotNull Chan chan,
            @Nullable Object value
        ) throws IOException {
            return false;
        }

        /**
         * Returns the {@link Coder} of {@link V}
         */
        @Nullable
        default Coder<?> deserialize(
            @NotNull Space space,
            @Nullable Supplier supplier
        ) throws IOException {
            Coder<?> it = getCoder();
            if (it != null) {
                return it;
            }

            if (supplier != null) {
                return supplier.lookup(
                    getType(), space
                );
            }

            throw new UnexpectedCrash(
                "Unexpectedly, supplier not found"
            );
        }

        /**
         * Returns the flags of {@link V}
         *
         * @see Flag
         */
        @NotNull
        default int getFlags() {
            return 0;
        }

        /**
         * Returns the actual {@link Type} of {@link V}
         */
        @NotNull
        default Type getActual() {
            return getType();
        }

        /**
         * Returns the {@link Coder} of {@link V}
         */
        @Nullable
        default Coder<?> getCoder() {
            return null;
        }

        /**
         * Returns the {@link AnnotatedElement} of {@link V}
         */
        @Nullable
        default AnnotatedElement getAnnotated() {
            return null;
        }

        /**
         * Returns the annotation of the specified {@link Class}
         */
        @Nullable
        default <A extends Annotation> A getAnnotation(
            @NotNull Class<A> target
        ) {
            AnnotatedElement elem = getAnnotated();
            if (elem != null) {
                return elem.getAnnotation(target);
            } else {
                return getType().getAnnotation(target);
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    class Builder0<K> extends Builder<K> {

        protected K bean;
        protected int index;

        protected Subject<K> subject;
        protected Member<K, ?> setter;

        /**
         * default
         */
        public Builder0(
            @NotNull Subject<K> subject
        ) {
            this.subject = subject;
        }

        /**
         * Prepare before parsing
         */
        @Override
        public void onCreate() {
            bean = subject.apply();
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onReport(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            setter.invoke(
                bean, child.onPacket()
            );
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onReport(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            int i = index++;
            setter = subject.set(
                alias.isEmpty() ? i : alias
            );

            if (setter != null) {
                Coder<?> coder = setter
                    .deserialize(
                        space, supplier
                    );

                if (coder != null) {
                    value.setType(
                        setter.getActual()
                    );
                    setter.invoke(
                        bean, coder.read(
                            event, value
                        )
                    );
                }
            }
        }

        /**
         * Create a branch of this {@link Builder}
         *
         * @throws IOException If an I/O error occurs
         */
        @Nullable
        public Builder<?> onReport(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            int i = index++;
            setter = subject.set(
                alias.isEmpty() ? i : alias
            );

            if (setter != null) {
                Coder<?> coder = setter
                    .deserialize(
                        space, supplier
                    );

                if (coder != null) {
                    return coder.getBuilder(
                        setter.getActual()
                    );
                }
            }

            return null;
        }

        /**
         * Returns the result of building {@link K}
         */
        @Nullable
        public K onPacket() {
            return bean;
        }

        /**
         * Close the resources of this {@link Builder}
         */
        @Override
        public void onDestroy() {
            index = 0;
            bean = null;
            setter = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    class Builder1<K> extends Builder<K> {

        protected K bean;
        protected int index;
        protected Object[] data;

        protected Subject<K> subject;
        protected Member<Object[], ?> setter;

        public Builder1(
            @NotNull Object[] data,
            @NotNull Subject<K> subject
        ) {
            this.data = data;
            this.subject = subject;
        }

        /**
         * Prepare before parsing
         */
        @Override
        public void onCreate() {
            // Nothing
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onReport(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            setter.invoke(
                data, child.onPacket()
            );
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onReport(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            int i = index++;
            setter = subject.arg(
                alias.isEmpty() ? i : alias
            );

            if (setter != null) {
                Coder<?> coder = setter
                    .deserialize(
                        space, supplier
                    );

                if (coder != null) {
                    value.setType(
                        setter.getActual()
                    );
                    setter.invoke(
                        data, coder.read(
                            event, value
                        )
                    );
                }
            }
        }

        /**
         * Create a branch of this {@link Builder}
         *
         * @throws IOException If an I/O error occurs
         */
        @Nullable
        public Builder<?> onReport(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            int i = index++;
            setter = subject.arg(
                alias.isEmpty() ? i : alias
            );

            if (setter != null) {
                Coder<?> coder = setter
                    .deserialize(
                        space, supplier
                    );

                if (coder != null) {
                    return coder.getBuilder(
                        setter.getActual()
                    );
                }
            }

            return null;
        }

        /**
         * Returns the result of building {@link K}
         *
         * @throws IOException If a packaging error or IO error
         */
        @Nullable
        public K onPacket()
            throws IOException {
            if (bean == null) {
                try {
                    bean = subject.apply(data);
                } catch (Collapse e) {
                    throw new UnexpectedCrash(
                        "Error creating entity", e
                    );
                }
            }
            return bean;
        }

        /**
         * Close the resources of this {@link Builder}
         */
        @Override
        public void onDestroy() {
            index = 0;
            data = null;
            bean = null;
            setter = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    class Builder2<K> extends Builder<K> {

        protected K bean;
        protected int index;

        protected Class<?> cxt;
        protected Object[] data;

        protected Cache<K> cache;
        protected Subject<K> subject;

        protected Member<K, ?> setter;
        protected Member<Object[], ?> target;

        public Builder2(
            @NotNull Object[] data,
            @NotNull Subject<K> subject
        ) {
            this(
                null, data, subject
            );
        }

        public Builder2(
            @Nullable Class<?> cxt,
            @NotNull Object[] data,
            @NotNull Subject<K> subject
        ) {
            this.cxt = cxt;
            this.data = data;
            this.subject = subject;
        }

        /**
         * Prepare before parsing
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onCreate() throws IOException {
            Class<?> o = cxt;
            if (o != null) {
                Object res = getParent().onPacket();
                if (res == null) {
                    throw new UnexpectedCrash(
                        "Unexpectedly, the parent is is null"
                    );
                } else {
                    if (o.isInstance(res)) {
                        data[0] = res;
                    } else {
                        throw new UnexpectedCrash(
                            "Unexpectedly, the parent is not " + o
                        );
                    }
                }
            }
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onReport(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            if (target != null) {
                target.invoke(
                    data, child.onPacket()
                );
            } else {
                Cache<K> ca = new Cache<>();
                ca.setter = setter;
                ca.value = child.onPacket();
                if (cache == null) {
                    cache = ca;
                } else {
                    cache.next = ca;
                }
            }
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onReport(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            int i = index++;
            target = subject.arg(
                alias.isEmpty() ? i : alias
            );

            if (target != null) {
                Coder<?> coder = target
                    .deserialize(
                        space, supplier
                    );

                if (coder != null) {
                    value.setType(
                        target.getActual()
                    );
                    target.invoke(
                        data, coder.read(
                            event, value
                        )
                    );
                }
            } else {
                setter = subject.set(
                    alias.isEmpty() ? i : alias
                );

                if (setter != null) {
                    Coder<?> coder = setter
                        .deserialize(
                            space, supplier
                        );

                    if (coder != null) {
                        value.setType(
                            setter.getActual()
                        );

                        Cache<K> ca = new Cache<>();
                        ca.setter = setter;
                        ca.value = coder.read(
                            event, value
                        );
                        if (cache == null) {
                            cache = ca;
                        } else {
                            cache.next = ca;
                        }
                    }
                }
            }
        }

        /**
         * Create a branch of this {@link Builder}
         *
         * @throws IOException If an I/O error occurs
         */
        @Nullable
        public Builder<?> onReport(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            int i = index++;
            target = subject.arg(
                alias.isEmpty() ? i : alias
            );

            Coder<?> coder;
            if (target != null) {
                coder = target.deserialize(
                    space, supplier
                );
                if (coder != null) {
                    return coder.getBuilder(
                        target.getActual()
                    );
                }
            } else {
                setter = subject.set(
                    alias.isEmpty() ? i : alias
                );

                if (setter != null) {
                    coder = setter.deserialize(
                        space, supplier
                    );
                    if (coder != null) {
                        return coder.getBuilder(
                            setter.getActual()
                        );
                    }
                }
            }

            return null;
        }

        /**
         * @author kraity
         * @since 0.0.4
         */
        static class Cache<K> {
            Object value;
            Cache<K> next;
            Setter<K, ?> setter;
        }

        /**
         * Returns the result of building {@link K}
         *
         * @throws IOException If a packaging error or IO error
         */
        @Nullable
        public K onPacket() throws IOException {
            if (bean == null) {
                try {
                    bean = subject.apply(data);
                } catch (Collapse e) {
                    throw new UnexpectedCrash(
                        "Error creating entity", e
                    );
                }
            }

            while (cache != null) {
                cache.setter.invoke(
                    bean, cache.value
                );
                cache = cache.next;
            }
            return bean;
        }

        /**
         * Close the resources of this {@link Builder}
         */
        @Override
        public void onDestroy() {
            index = 0;
            bean = null;
            data = null;
            cache = null;
        }
    }
}
