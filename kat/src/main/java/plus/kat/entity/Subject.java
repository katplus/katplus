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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.spare.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author kraity
 * @since 0.0.4
 */
public interface Subject<K> extends Spare<K>, Maker<K> {
    /**
     * If this {@link Subject} can create an instance,
     * it returns it, otherwise it will return {@code null}
     *
     * @return {@link K} or {@code null}
     */
    @Nullable
    @Override
    default K apply() {
        try {
            return apply(
                Alias.EMPTY
            );
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * If this {@link Subject} can create an instance,
     * it returns it, otherwise it will throw {@link Crash}
     *
     * @param alias the alias of entity
     * @return {@link K}, it is not null
     * @throws Crash If a failure occurs
     */
    @NotNull
    K apply(
        @NotNull Alias alias
    ) throws Crash;

    /**
     * If this {@link Subject} can create an instance,
     * it returns it, otherwise it will throw {@link Crash}
     *
     * @param alias the alias of entity
     * @return {@link K}, it is not null
     * @throws Crash If a failure occurs
     */
    @NotNull
    @Override
    default K apply(
        @NotNull Alias alias,
        @NotNull Object... params
    ) throws Crash {
        throw new Crash(
            "Unsupported method"
        );
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
            Member<K, ?> setter = getProperty(
                spoiler.getKey()
            );
            if (setter == null) {
                continue;
            }

            Object value = spoiler.getValue();
            if (value == null) {
                continue;
            }

            Class<?> kind = setter.getKind();
            if (kind.isInstance(value)) {
                rows++;
                setter.invoke(
                    entity, value
                );
                continue;
            }

            Spare<?> spare = supplier.lookup(kind);
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
                getArgument(spoiler.getKey());
            if (setter == null) {
                continue;
            }

            Object value = spoiler.getValue();
            if (value == null) {
                continue;
            }

            Class<?> kind = setter.getKind();
            if (kind.isInstance(value)) {
                rows++;
                setter.invoke(
                    group, value
                );
                continue;
            }

            Spare<?> spare = supplier.lookup(kind);
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

            Member<K, ?> setter =
                getProperty(name);
            if (setter == null) {
                throw new SQLCrash(
                    "Can't find the property of " + name
                );
            }

            Object value = resultSet
                .getObject(i);
            if (value == null) {
                continue;
            }

            Class<?> kind = setter.getKind();
            if (kind.isInstance(value)) {
                rows++;
                setter.invoke(entity, value);
                continue;
            }

            Object result = supplier.cast(kind, value);
            if (result != null) {
                rows++;
                setter.invoke(entity, result);
                continue;
            }

            throw new SQLCrash(
                "Cannot convert the type of " + name
                    + " from " + value.getClass() + " to " + kind
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

            Member<Object[], ?> setter =
                getArgument(name);
            if (setter == null) {
                throw new SQLCrash(
                    "Can't find the argument of " + name
                );
            }

            Object value = resultSet
                .getObject(i);
            if (value == null) {
                continue;
            }

            Class<?> kind = setter.getKind();
            if (kind.isInstance(value)) {
                rows++;
                setter.invoke(group, value);
                continue;
            }

            Object result = supplier.cast(kind, value);
            if (result != null) {
                rows++;
                setter.invoke(group, result);
                continue;
            }

            throw new SQLCrash(
                "Cannot convert the type of " + name
                    + " from " + value.getClass() + " to " + kind
            );
        }

        return rows;
    }

    /**
     * Returns a {@link Builder} of {@link K}
     *
     * @see Builder0
     */
    @Nullable
    @Override
    default Builder<K> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0<>(this);
    }

    /**
     * Returns the {@link Member}
     * of the specified property name
     *
     * @param name the property name of the bean
     * @return {@link Member} or {@code null}
     * @throws NullPointerException If the alias is null
     */
    @Nullable
    default Member<K, ?> getProperty(
        @NotNull Object name
    ) {
        return null;
    }

    /**
     * Returns the {@link Member}
     * of the specified attribute name
     *
     * @param name the attribute name of the bean
     * @return {@link Member} or {@code null}
     * @throws NullPointerException If the alias is null
     */
    @Nullable
    default Member<K, ?> getAttribute(
        @NotNull Object name
    ) {
        return null;
    }

    /**
     * Returns the {@link Member}
     * of the specified argument name
     *
     * @param name the argument name of the bean
     * @return {@link Member} or {@code null}
     * @throws NullPointerException If the alias is null
     */
    @Nullable
    default Member<Object[], ?> getArgument(
        @NotNull Object name
    ) {
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
        Class<?> getKind();

        /**
         * Returns the {@link Type} of {@link V}
         */
        @NotNull
        default Type getType() {
            return getKind();
        }

        /**
         * Returns the {@link Coder} of {@link V}
         */
        @Nullable
        default Coder<?> assign(
            @NotNull Space space,
            @NotNull Supplier supplier
        ) {
            return supplier.lookup(
                getKind(), space
            );
        }

        /**
         * Returns the annotation of the specified type
         */
        @Nullable
        default <A extends Annotation> A annotate(
            @NotNull Class<A> target
        ) {
            return getKind().getAnnotation(target);
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
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash {
            bean = subject.apply(alias);
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            setter.invoke(
                bean, child.getResult()
            );
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            int i = index++;
            setter = subject.getProperty(
                alias.isEmpty() ? i : alias
            );

            if (setter != null) {
                Coder<?> coder = setter
                    .assign(space, supplier);

                if (coder != null) {
                    value.setType(
                        setter.getType()
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
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            int i = index++;
            setter = subject.getProperty(
                alias.isEmpty() ? i : alias
            );

            if (setter != null) {
                Coder<?> coder = setter
                    .assign(space, supplier);

                if (coder != null) {
                    return coder.getBuilder(
                        setter.getType()
                    );
                }
            }

            return null;
        }

        /**
         * Returns the result of building {@link K}
         */
        @Nullable
        @Override
        public K getResult() {
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
        public void onCreate(
            @NotNull Alias alias
        ) {
            // Nothing
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            setter.invoke(
                data, child.getResult()
            );
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            int i = index++;
            setter = subject.getArgument(
                alias.isEmpty() ? i : alias
            );

            if (setter != null) {
                Coder<?> coder = setter
                    .assign(space, supplier);

                if (coder != null) {
                    value.setType(
                        setter.getType()
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
        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            int i = index++;
            setter = subject.getArgument(
                alias.isEmpty() ? i : alias
            );

            if (setter != null) {
                Coder<?> coder = setter
                    .assign(space, supplier);

                if (coder != null) {
                    return coder.getBuilder(
                        setter.getType()
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
        @Override
        public K getResult()
            throws IOException {
            if (bean == null) {
                try {
                    bean = subject.apply(
                        getAlias(), data
                    );
                } catch (Crash e) {
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
        public void onCreate(
            @NotNull Alias alias
        ) throws IOException {
            Class<?> o = cxt;
            if (o != null) {
                Object res = getParent().getResult();
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
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            if (target != null) {
                target.invoke(
                    data, child.getResult()
                );
            } else {
                Cache<K> ca = new Cache<>();
                ca.setter = setter;
                ca.value = child.getResult();
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
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            int i = index++;
            target = subject.getArgument(
                alias.isEmpty() ? i : alias
            );

            if (target != null) {
                Coder<?> coder = target
                    .assign(space, supplier);

                if (coder != null) {
                    value.setType(
                        target.getType()
                    );
                    target.invoke(
                        data, coder.read(
                            event, value
                        )
                    );
                }
            } else {
                setter = subject.getProperty(
                    alias.isEmpty() ? i : alias
                );

                if (setter != null) {
                    Coder<?> coder = setter
                        .assign(space, supplier);

                    if (coder != null) {
                        value.setType(
                            setter.getType()
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
        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            int i = index++;
            target = subject.getArgument(
                alias.isEmpty() ? i : alias
            );

            Coder<?> coder;
            if (target != null) {
                coder = target.assign(
                    space, supplier
                );
                if (coder != null) {
                    return coder.getBuilder(
                        target.getType()
                    );
                }
            } else {
                setter = subject.getProperty(
                    alias.isEmpty() ? i : alias
                );

                if (setter != null) {
                    coder = setter.assign(
                        space, supplier
                    );
                    if (coder != null) {
                        return coder.getBuilder(
                            setter.getType()
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
        @Override
        public K getResult()
            throws IOException {
            if (bean == null) {
                try {
                    bean = subject.apply(
                        getAlias(), data
                    );
                } catch (Crash e) {
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
