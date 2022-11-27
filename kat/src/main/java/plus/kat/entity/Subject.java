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
import plus.kat.stream.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * @author kraity
 * @since 0.0.4
 */
public interface Subject<T> extends Spare<T> {
    /**
     * If this {@link Subject} can create an instance,
     * it returns it, otherwise it will throw {@link Collapse}
     *
     * @return {@link T}, it is not null
     * @throws Collapse If a build error occurs
     */
    @NotNull
    default T apply() {
        throw new Collapse(
            "Failed to apply"
        );
    }

    /**
     * If this {@link Subject} can create an instance,
     * it returns it, otherwise it will throw {@link Collapse}
     *
     * @param args the specified args for constructs
     * @return {@link T}, it is not null
     * @throws Collapse If a build error occurs
     */
    @NotNull
    default T apply(
        @NotNull Object[] args
    ) {
        throw new Collapse(
            "Failed to apply"
        );
    }

    /**
     * Returns the flag of {@link T}
     */
    @Override
    default Boolean getFlag() {
        return Boolean.TRUE;
    }

    /**
     * Returns the border of {@link T}
     */
    @Override
    default Boolean getBorder(
        @NotNull Flag flag
    ) {
        return null;
    }

    /**
     * Returns a {@link Builder} of {@link T}
     *
     * @param type the specified actual type
     */
    @Override
    default Builder<T> getBuilder(
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
     * @throws NullPointerException If the specified alias is null
     */
    @Override
    default Member<T, ?> set(
        @NotNull CharSequence name
    ) {
        return null;
    }

    /**
     * Returns a get-capable {@link Member}
     * of the specified property {@code name}
     *
     * @param name the property name of the bean
     * @return {@link Member} or {@code null}
     * @throws NullPointerException If the specified alias is null
     */
    @Override
    default Member<T, ?> get(
        @NotNull CharSequence name
    ) {
        return null;
    }

    /**
     * Returns a set-capable {@link Member}
     * of the specified parameter {@code name}
     *
     * @param name the parameter name of the bean
     * @return {@link Member} or {@code null}
     * @throws NullPointerException If the specified alias is null
     */
    @Nullable
    default Member<Object[], ?> arg(
        @NotNull CharSequence name
    ) {
        return null;
    }

    /**
     * Copy the property values of the specified spoiler into the given specified bean
     *
     * @param bean     the specified bean to be updated
     * @param spoiler  the specified spoiler as data source
     * @param supplier the specified supplier as the spare loader
     * @return the number of rows affected
     * @throws NullPointerException If the arguments contains null
     */
    @Override
    default int update(
        @NotNull T bean,
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) {
        int rows = 0;
        while (spoiler.hasNext()) {
            Member<T, ?> setter = set(
                spoiler.getKey()
            );
            if (setter == null) {
                continue;
            }

            Object value = spoiler.getValue();
            if (value == null) {
                continue;
            }

            Class<?> clazz = setter.kind();
            if (clazz.isInstance(value)) {
                rows++;
                setter.invoke(
                    bean, value
                );
                continue;
            }

            Spare<?> spare = supplier.lookup(clazz);
            if (spare != null) {
                rows++;
                setter.invoke(
                    bean, spare.cast(
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
     * @param group    the specified array to be updated
     * @param spoiler  the specified spoiler as data source
     * @param supplier the specified supplier as the spare loader
     * @return the number of rows affected
     * @throws NullPointerException If the arguments contains null
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

            Class<?> clazz = setter.kind();
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
     * @param bean      the specified bean to be updated
     * @param supplier  the specified supplier as the loader
     * @param resultSet the specified spoiler as data source
     * @return the number of rows affected
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the arguments contains null
     */
    @Override
    default int update(
        @NotNull T bean,
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        ResultSetMetaData meta =
            resultSet.getMetaData();
        int rows = 0, size =
            meta.getColumnCount();

        for (int i = 1; i <= size; i++) {
            String name = meta
                .getColumnLabel(i);
            if (name == null) {
                name = meta.getColumnName(i);
            }

            Member<T, ?> setter = set(name);
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

            Class<?> clazz = setter.kind();
            if (clazz.isInstance(value)) {
                rows++;
                setter.invoke(bean, value);
                continue;
            }

            Object result = supplier.cast(clazz, value);
            if (result != null) {
                rows++;
                setter.invoke(bean, result);
                continue;
            }

            throw new SQLCrash(
                "Unable to convert the `" + name + "` property type of "
                    + getType() + " from " + value.getClass() + " to " + setter.type()
            );
        }

        return rows;
    }

    /**
     * Copy the property values of the specified spoiler into the given specified group
     *
     * @param group     the specified array to be updated
     * @param supplier  the specified supplier as the loader
     * @param resultSet the specified spoiler as data source
     * @return the number of rows affected
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the arguments contains null
     */
    default int update(
        @NotNull Object[] group,
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        ResultSetMetaData meta =
            resultSet.getMetaData();
        int rows = 0, size =
            meta.getColumnCount();

        for (int i = 1; i <= size; i++) {
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

            Class<?> clazz = setter.kind();
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
                    + getType() + " from " + value.getClass() + " to " + setter.type()
            );
        }

        return rows;
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    interface Member<T, V> extends
        Sketch<V>, Setter<T, V>, Getter<T, V> {
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    class Builder0<T> extends Builder<T> implements Callback {

        protected T bean;
        protected Subject<T> subject;
        protected Member<T, ?> setter;

        /**
         * default
         */
        public Builder0(
            @NotNull Subject<T> subject
        ) {
            this.subject = subject;
        }

        /**
         * Prepare before parsing
         */
        @Override
        public void onOpen() {
            bean = subject.apply();
        }

        /**
         * Create a builder for the property {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Nullable
        public Pipage onOpen(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            Member<T, ?> member =
                setter = subject.set(alias);

            if (member != null) {
                Type type = member.type();
                Coder<?> coder = member.coder();

                if (coder != null) {
                    Builder<?> child =
                        coder.getBuilder(type);
                    if (child != null) {
                        return child.init(this, this);
                    }
                } else {
                    coder = supplier.lookup(
                        member.kind(), space
                    );

                    if (coder != null) {
                        Builder<?> child =
                            coder.getBuilder(type);
                        if (child != null) {
                            return child.init(this, this);
                        }
                    } else {
                        throw new IOException(
                            "No spare for member(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }
            }

            return null;
        }

        /**
         * Receive the property of {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onEmit(
            @NotNull Pipage pipage,
            @Nullable Object result
        ) throws IOException {
            setter.invoke(
                bean, result
            );
        }

        /**
         * Receive the property of {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onEmit(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            Member<T, ?> member =
                setter = subject.set(alias);

            if (member != null) {
                Coder<?> coder =
                    member.coder();

                if (coder != null) {
                    member.invoke(
                        bean, coder.read(
                            flag, value
                        )
                    );
                } else {
                    coder = supplier.lookup(
                        member.kind(), space
                    );

                    if (coder != null) {
                        member.invoke(
                            bean, coder.read(
                                flag, value
                            )
                        );
                    } else {
                        throw new IOException(
                            "No spare for member(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }
            }
        }

        /**
         * Returns the result of building {@link T}
         */
        @Nullable
        public T build() {
            return bean;
        }

        /**
         * Close the resources of this {@link Builder}
         */
        @Override
        public void onClose() {
            bean = null;
            setter = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    class Builder1<T> extends Builder<T> implements Callback {

        protected T bean;
        protected Object[] data;

        protected Subject<T> subject;
        protected Member<Object[], ?> target;

        public Builder1(
            @NotNull Object[] data,
            @NotNull Subject<T> subject
        ) {
            this.data = data;
            this.subject = subject;
        }

        /**
         * Prepare before parsing
         */
        @Override
        public void onOpen() {
            // Nothing
        }

        /**
         * Create a builder for the property {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Nullable
        public Pipage onOpen(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            Member<Object[], ?> member =
                target = subject.arg(alias);

            if (member != null) {
                Type type = member.type();
                Coder<?> coder = member.coder();

                if (coder != null) {
                    Builder<?> child =
                        coder.getBuilder(type);
                    if (child != null) {
                        return child.init(this, this);
                    }
                } else {
                    coder = supplier.lookup(
                        member.kind(), space
                    );

                    if (coder != null) {
                        Builder<?> child =
                            coder.getBuilder(type);
                        if (child != null) {
                            return child.init(this, this);
                        }
                    } else {
                        throw new IOException(
                            "No spare for member(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }
            }

            return null;
        }

        /**
         * Receive the property of {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onEmit(
            @NotNull Pipage pipage,
            @Nullable Object result
        ) throws IOException {
            target.invoke(
                data, result
            );
        }

        /**
         * Receive the property of {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onEmit(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            Member<Object[], ?> member =
                target = subject.arg(alias);

            if (member != null) {
                Coder<?> coder =
                    member.coder();

                if (coder != null) {
                    member.invoke(
                        data, coder.read(
                            flag, value
                        )
                    );
                } else {
                    coder = supplier.lookup(
                        member.kind(), space
                    );

                    if (coder != null) {
                        member.invoke(
                            data, coder.read(
                                flag, value
                            )
                        );
                    } else {
                        throw new IOException(
                            "No spare for member(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }
            }
        }

        /**
         * Returns the result of building {@link T}
         *
         * @throws IOException If a packaging error or IO error
         */
        @Nullable
        public T build()
            throws IOException {
            if (bean == null) {
                try {
                    bean = subject.apply(data);
                } catch (Collapse e) {
                    throw new IOException(
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
        public void onClose() {
            data = null;
            bean = null;
            target = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    class Builder2<T> extends Builder<T> implements Callback {

        protected T bean;
        protected Class<?> cxt;
        protected Object[] data;

        protected Cache cache;
        protected Subject<T> subject;

        protected Member<T, ?> setter;
        protected Member<Object[], ?> target;

        public Builder2(
            @NotNull Object[] data,
            @NotNull Subject<T> subject
        ) {
            this(
                null, data, subject
            );
        }

        public Builder2(
            @Nullable Class<?> cxt,
            @NotNull Object[] data,
            @NotNull Subject<T> subject
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
        public void onOpen() throws IOException {
            Class<?> o = cxt;
            if (o != null) {
                Channel holder = holder();
                if (holder instanceof Builder) {
                    Object bean = ((Builder<?>) holder).build();
                    if (bean == null) {
                        throw new IOException(
                            "The parent result is is null"
                        );
                    } else {
                        if (o.isInstance(bean)) {
                            data[0] = bean;
                        } else {
                            throw new IOException(
                                "The parent result is not " + o
                            );
                        }
                    }
                } else {
                    throw new IOException(
                        "Could not find the result of the parent pipage: " + holder
                    );
                }
            }
        }

        /**
         * Create a builder for the property {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Nullable
        public Pipage onOpen(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            target = subject.arg(alias);
            if (target != null) {
                Type type = target.type();
                Coder<?> coder = target.coder();

                if (coder != null) {
                    Builder<?> child =
                        coder.getBuilder(type);
                    if (child != null) {
                        return child.init(this, this);
                    }
                } else {
                    coder = supplier.lookup(
                        target.kind(), space
                    );

                    if (coder != null) {
                        Builder<?> child =
                            coder.getBuilder(type);
                        if (child != null) {
                            return child.init(this, this);
                        }
                    } else {
                        throw new IOException(
                            "No spare for param(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }
            } else {
                setter = subject.set(alias);
                if (setter != null) {
                    Type type = setter.type();
                    Coder<?> coder = setter.coder();

                    if (coder != null) {
                        Builder<?> child =
                            coder.getBuilder(type);
                        if (child != null) {
                            return child.init(
                                this, new Cache(setter)
                            );
                        }
                    } else {
                        coder = supplier.lookup(
                            setter.kind(), space
                        );

                        if (coder != null) {
                            Builder<?> child =
                                coder.getBuilder(type);
                            if (child != null) {
                                return child.init(
                                    this, new Cache(setter)
                                );
                            }
                        } else {
                            throw new IOException(
                                "No spare for param(" + alias
                                    + ") of " + subject.getType() + " was found"
                            );
                        }
                    }
                }
            }

            return null;
        }

        /**
         * Receive the property of {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onEmit(
            @NotNull Pipage pipage,
            @Nullable Object result
        ) throws IOException {
            target.invoke(
                data, result
            );
        }

        /**
         * Receive the property of {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onEmit(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            target = subject.arg(alias);
            if (target != null) {
                Coder<?> coder =
                    target.coder();

                if (coder != null) {
                    target.invoke(
                        data, coder.read(
                            flag, value
                        )
                    );
                } else {
                    coder = supplier.lookup(
                        target.kind(), space
                    );

                    if (coder != null) {
                        target.invoke(
                            data, coder.read(
                                flag, value
                            )
                        );
                    } else {
                        throw new IOException(
                            "No spare for param(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }
            } else {
                setter = subject.set(alias);
                if (setter != null) {
                    Coder<?> coder =
                        setter.coder();

                    if (coder != null) {
                        new Cache(setter,
                            coder.read(flag, value)
                        );
                    } else {
                        coder = supplier.lookup(
                            setter.kind(), space
                        );

                        if (coder != null) {
                            new Cache(setter,
                                coder.read(flag, value)
                            );
                        } else {
                            throw new IOException(
                                "No spare for member(" + alias
                                    + ") of " + subject.getType() + " was found"
                            );
                        }
                    }
                }
            }
        }

        /**
         * @author kraity
         * @since 0.0.4
         */
        class Cache implements Callback {
            Cache next;
            Object value;
            Setter<T, ?> setter;

            public Cache(
                Setter<T, ?> setter
            ) {
                this.setter = setter;
            }

            public Cache(
                Setter<T, ?> setter, Object value
            ) {
                this.value = value;
                this.setter = setter;
                if (cache == null) {
                    cache = this;
                } else {
                    cache.next = this;
                }
            }

            @Override
            public void onEmit(
                Pipage pipage, Object result) throws IOException {
                value = result;
                if (cache == null) {
                    cache = this;
                } else {
                    cache.next = this;
                }
            }
        }

        /**
         * Returns the result of building {@link T}
         *
         * @throws IOException If a packaging error or IO error
         */
        @Nullable
        public T build() throws IOException {
            if (bean == null) {
                try {
                    bean = subject.apply(data);
                } catch (Collapse e) {
                    throw new IOException(
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
        public void onClose() {
            bean = null;
            data = null;
            cache = null;
        }
    }
}
