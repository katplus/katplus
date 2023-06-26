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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.spare.*;

import java.io.*;
import java.lang.reflect.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public interface Subject<T> extends Spare<T> {
    /**
     * Apply for a nonnull bean,
     * otherwise throws an error
     *
     * @throws IllegalStateException If failed to build
     */
    @NotNull
    default T apply() {
        throw new IllegalStateException(
            "No matching constructor found"
        );
    }

    /**
     * Apply for a nonnull bean,
     * otherwise throws an error
     *
     * @param args the specified args of constructor
     * @throws IllegalStateException If failed to build
     */
    @NotNull
    default T apply(
        @NotNull Object... args
    ) {
        if (args.length == 0) {
            return apply();
        }

        throw new IllegalStateException(
            "No matching constructor found"
        );
    }

    /**
     * Returns the flag of {@link T}
     */
    @Override
    default Boolean getScope() {
        return Boolean.TRUE;
    }

    /**
     * Returns the border of {@link T}
     */
    @Override
    default Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.BRACE;
    }

    /**
     * Returns a factory of {@link T}
     */
    @Override
    default Factory getFactory(
        @Nullable Type type
    ) {
        return new Builder0<>(type, this);
    }

    /**
     * Returns a set-capable {@link Sensor}
     * of the specified property {@code name}
     *
     * @param name the property name
     * @throws IllegalStateException If the name is invalid
     */
    @Nullable
    default Sensor setProperty(
        @NotNull Object name
    ) {
        if (name != null) {
            return null;
        }

        throw new IllegalStateException(
            "Received property name is invalid"
        );
    }

    /**
     * Returns a get-capable {@link Sensor}
     * of the specified property {@code name}
     *
     * @param name the property name
     * @throws IllegalStateException If the name is invalid
     */
    @Nullable
    default Sensor getProperty(
        @NotNull Object name
    ) {
        if (name != null) {
            return null;
        }

        throw new IllegalStateException(
            "Received property name is invalid"
        );
    }

    /**
     * Returns a set-capable {@link Sensor}
     * of the specified parameter {@code name}
     *
     * @param name the parameter name
     * @throws IllegalStateException If the name is invalid
     */
    @Nullable
    default Sensor setParameter(
        @NotNull Object name
    ) {
        if (name != null) {
            return null;
        }

        throw new IllegalStateException(
            "Received parameter name is invalid"
        );
    }

    /**
     * Returns a get-capable {@link Sensor}
     * of the specified parameter {@code name}
     *
     * @param name the parameter name
     * @throws IllegalStateException If the name is invalid
     */
    @Nullable
    default Sensor getParameter(
        @NotNull Object name
    ) {
        if (name != null) {
            return null;
        }

        throw new IllegalStateException(
            "Received parameter name is invalid"
        );
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    class Builder0<T> extends Builder<T> {

        protected T bean;
        protected Type type;

        protected Sensor setter;
        protected Subject<T> subject;

        /**
         * default
         */
        public Builder0(
            Type type,
            Subject<T> spare
        ) {
            if (spare != null) {
                this.type = type;
                this.subject = spare;
            } else {
                throw new NullPointerException(
                    "Received the spare is null"
                );
            }
        }

        /**
         * Prepare before parsing
         */
        @Override
        public void onOpen()
            throws IOException {
            bean = subject.apply();
        }

        /**
         * Create a builder for the property {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Nullable
        public Spider onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Sensor sensor =
                subject.setProperty(alias);

            if (sensor != null) {
                Type type = sensor.getType();
                Coder<?> coder = sensor.getCoder();

                while (true) {
                    if (type instanceof Class ||
                        type instanceof GenericArrayType ||
                        type instanceof ParameterizedType) {
                        break;
                    } else {
                        if (type == (type = solve(type))) {
                            throw new IllegalStateException(
                                "Failed to handle " + type
                            );
                        }
                    }
                }

                if (coder == null) {
                    coder = context.assign(
                        type, space
                    );
                    if (coder == null) {
                        throw new IOException(
                            "No spare for property(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                Factory child =
                    coder.getFactory(type);
                if (child != null) {
                    setter = sensor;
                    return child.init(this, context);
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
        public void onEach(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Sensor sensor =
                subject.setProperty(alias);

            if (sensor != null) {
                Coder<?> coder =
                    sensor.getCoder();

                if (coder == null) {
                    Type type = sensor.getType();
                    while (true) {
                        if (type instanceof Class ||
                            type instanceof GenericArrayType ||
                            type instanceof ParameterizedType) {
                            break;
                        } else {
                            if (type == (type = solve(type))) {
                                throw new IllegalStateException(
                                    "Failed to handle " + type
                                );
                            }
                        }
                    }

                    coder = context.assign(
                        type, space
                    );
                    if (coder == null) {
                        throw new IOException(
                            "No spare for property(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                sensor.accept(
                    bean, coder.read(this, value)
                );
            }
        }

        /**
         * Receive the property of {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onEach(
            @Nullable Object value
        ) throws IOException {
            setter.accept(
                bean, value
            );
        }

        /**
         * Resolves the unknown type with this helper,
         * substituting type variables as far as possible
         */
        @Override
        public Type solve(Type type) {
            if (type instanceof WildcardType) {
                return solve(
                    ((WildcardType) type).getUpperBounds()[0]
                );
            }

            if (type instanceof TypeVariable) {
                Type actor = this.type;
                Class<?> clazz = subject.getType();

                if (clazz != null) {
                    // If GenericDeclaration is method,
                    // then a ClassCastException is thrown
                    Class<?> entry = (Class<?>) (
                        (TypeVariable<?>) type).getGenericDeclaration();

                    Search:
                    for (Class<?> cls; ; clazz = cls) {
                        if (entry == clazz) break;
                        if (entry.isInterface()) {
                            Class<?>[] a = clazz.getInterfaces();
                            for (int i = 0; i < a.length; i++) {
                                cls = a[i];
                                if (cls == entry) {
                                    actor = clazz.getGenericInterfaces()[i];
                                    break Search;
                                } else if (entry.isAssignableFrom(cls)) {
                                    actor = clazz.getGenericInterfaces()[i];
                                    continue Search;
                                }
                            }
                        }
                        if (!clazz.isInterface()) {
                            for (; clazz != Object.class; clazz = cls) {
                                cls = clazz.getSuperclass();
                                if (cls == entry) {
                                    actor = clazz.getGenericSuperclass();
                                    break Search;
                                } else if (entry.isAssignableFrom(cls)) {
                                    actor = clazz.getGenericSuperclass();
                                    continue Search;
                                }
                            }
                        }
                        return holder.solve(type);
                    }

                    if (actor instanceof ParameterizedType) {
                        Object[] items = entry.getTypeParameters();
                        for (int i = 0; i < items.length; i++) {
                            if (type == items[i]) {
                                return solve(
                                    ((ParameterizedType) actor).getActualTypeArguments()[i]
                                );
                            }
                        }
                    }
                }
                throw new IllegalStateException(
                    "Failed to resolve " + type + " from " + actor
                );
            }
            return type;
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
        public void onClose() throws IOException {
            bean = null;
            setter = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    class Builder1<T> extends Builder0<T> {

        protected Object[] args;

        public Builder1(
            Type type,
            Object[] args,
            Subject<T> spare
        ) {
            super(type, spare);
            if (args != null) {
                this.args = args;
            } else {
                throw new NullPointerException(
                    "Received args-array is null"
                );
            }
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
        public Spider onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Sensor sensor =
                subject.setParameter(alias);

            if (sensor != null) {
                Type type = sensor.getType();
                Coder<?> coder = sensor.getCoder();

                while (true) {
                    if (type instanceof Class ||
                        type instanceof GenericArrayType ||
                        type instanceof ParameterizedType) {
                        break;
                    } else {
                        if (type == (type = solve(type))) {
                            throw new IllegalStateException(
                                "Failed to handle " + type
                            );
                        }
                    }
                }

                if (coder == null) {
                    coder = context.assign(
                        type, space
                    );
                    if (coder == null) {
                        throw new IOException(
                            "No spare for argument(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                Factory child =
                    coder.getFactory(type);
                if (child != null) {
                    setter = sensor;
                    return child.init(this, context);
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
        public void onEach(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Sensor sensor =
                subject.setParameter(alias);

            if (sensor != null) {
                Coder<?> coder =
                    sensor.getCoder();

                if (coder == null) {
                    Type type = sensor.getType();
                    while (true) {
                        if (type instanceof Class ||
                            type instanceof GenericArrayType ||
                            type instanceof ParameterizedType) {
                            break;
                        } else {
                            if (type == (type = solve(type))) {
                                throw new IllegalStateException(
                                    "Failed to handle " + type
                                );
                            }
                        }
                    }

                    coder = context.assign(
                        type, space
                    );
                    if (coder == null) {
                        throw new IOException(
                            "No spare for argument(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                sensor.accept(
                    args, coder.read(this, value)
                );
            }
        }

        /**
         * Receive the property of {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onEach(
            @Nullable Object value
        ) throws IOException {
            setter.accept(
                args, value
            );
        }

        /**
         * Returns the result of building {@link T}
         *
         * @throws IllegalStateException If a fatal error occurs
         */
        @Nullable
        public T build() {
            if (bean == null) {
                bean = subject.apply(args);
            }
            return bean;
        }

        /**
         * Close the resources of this {@link Builder}
         */
        @Override
        public void onClose() throws IOException {
            args = null;
            bean = null;
            setter = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    class Builder2<T> extends Builder0<T> {

        protected Cache cache;
        protected Class<?> own;
        protected Object[] args;
        protected boolean delay;

        public Builder2(
            Type type,
            Class<?> own,
            Object[] args,
            Subject<T> spare
        ) {
            super(type, spare);
            if (args != null) {
                this.own = own;
                this.args = args;
            } else {
                throw new NullPointerException(
                    "Received args-array is null"
                );
            }
        }

        /**
         * Prepare before parsing
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onOpen() throws IOException {
            Class<?> o = own;
            if (o != null) {
                Factory holder = holder();
                if (holder instanceof Builder) {
                    Object bean = ((Builder<?>) holder).build();
                    if (bean == null) {
                        throw new IOException(
                            "The parent result is is null"
                        );
                    } else {
                        if (o.isInstance(bean)) {
                            args[0] = bean;
                        } else {
                            throw new IOException(
                                "The parent result is not " + o
                            );
                        }
                    }
                } else {
                    throw new IOException(
                        "Could not find the result of the parent spider: " + holder
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
        public Spider onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Sensor sensor =
                subject.setParameter(alias);

            block:
            {
                delay = false;
                if (sensor == null) {
                    sensor = subject.setProperty(alias);
                    if (sensor == null) {
                        break block;
                    } else {
                        delay = true;
                    }
                }

                Type type = sensor.getType();
                Coder<?> coder = sensor.getCoder();

                while (true) {
                    if (type instanceof Class ||
                        type instanceof GenericArrayType ||
                        type instanceof ParameterizedType) {
                        break;
                    } else {
                        if (type == (type = solve(type))) {
                            throw new IllegalStateException(
                                "Failed to handle " + type
                            );
                        }
                    }
                }

                if (coder == null) {
                    coder = context.assign(
                        type, space
                    );
                    if (coder == null) {
                        throw new IOException(
                            "No spare for attribute(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                Factory child =
                    coder.getFactory(type);
                if (child != null) {
                    setter = sensor;
                    return child.init(this, context);
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
        public void onEach(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Sensor sensor =
                subject.setParameter(alias);

            block:
            {
                boolean delay = false;
                if (sensor == null) {
                    sensor = subject.setProperty(alias);
                    if (sensor == null) {
                        break block;
                    } else {
                        delay = true;
                    }
                }

                Coder<?> coder =
                    sensor.getCoder();

                if (coder == null) {
                    Type type = sensor.getType();
                    while (true) {
                        if (type instanceof Class ||
                            type instanceof GenericArrayType ||
                            type instanceof ParameterizedType) {
                            break;
                        } else {
                            if (type == (type = solve(type))) {
                                throw new IllegalStateException(
                                    "Failed to handle " + type
                                );
                            }
                        }
                    }

                    coder = context.assign(
                        type, space
                    );
                    if (coder == null) {
                        throw new IOException(
                            "No spare for attribute(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                if (delay) {
                    new Cache(sensor,
                        coder.read(this, value)
                    );
                } else {
                    sensor.accept(
                        args, coder.read(this, value)
                    );
                }
            }
        }

        /**
         * Receive the property of {@link T}
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onEach(
            @Nullable Object value
        ) throws IOException {
            if (delay) {
                new Cache(
                    setter, value
                );
            } else {
                setter.accept(
                    args, value
                );
            }
        }

        /**
         * @author kraity
         * @since 0.0.6
         */
        class Cache {
            Cache next;
            Object value;
            Sensor sensor;

            public Cache(
                Sensor sensor, Object value
            ) {
                this.value = value;
                this.sensor = sensor;
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
         * @throws IllegalStateException If a fatal error occurs
         */
        @Nullable
        public T build() {
            if (bean == null) {
                bean = subject.apply(args);
            }

            while (cache != null) {
                cache.sensor.accept(
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
        public void onClose() throws IOException {
            args = null;
            bean = null;
            cache = null;
            setter = null;
        }
    }
}
