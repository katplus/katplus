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

import plus.kat.*;
import plus.kat.actor.*;

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
     * Returns a new object based on no arguments,
     * otherwise an exception will be thrown directly
     *
     * @throws IllegalStateException If failed to build
     */
    @NotNull
    default T apply() {
        throw new IllegalStateException(
            "No corresponding constructor"
        );
    }

    /**
     * Returns a new object based on the arguments,
     * otherwise an exception will be thrown directly
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
            "No corresponding constructor"
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
     * @throws IllegalArgumentException Wrong
     */
    @Nullable
    default Sensor setProperty(
        @NotNull Object name
    ) {
        if (name != null) {
            return null;
        }

        throw new IllegalArgumentException(
            "Received property name is illegal"
        );
    }

    /**
     * Returns a get-capable {@link Sensor}
     * of the specified property {@code name}
     *
     * @param name the property name
     * @throws IllegalArgumentException Wrong
     */
    @Nullable
    default Sensor getProperty(
        @NotNull Object name
    ) {
        if (name != null) {
            return null;
        }

        throw new IllegalArgumentException(
            "Received property name is illegal"
        );
    }

    /**
     * Returns a set-capable {@link Sensor}
     * of the specified parameter {@code name}
     *
     * @param name the parameter name
     * @throws IllegalArgumentException Wrong
     */
    @Nullable
    default Sensor setParameter(
        @NotNull Object name
    ) {
        if (name != null) {
            return null;
        }

        throw new IllegalArgumentException(
            "Received parameter name is illegal"
        );
    }

    /**
     * Returns a get-capable {@link Sensor}
     * of the specified parameter {@code name}
     *
     * @param name the parameter name
     * @throws IllegalArgumentException Wrong
     */
    @Nullable
    default Sensor getParameter(
        @NotNull Object name
    ) {
        if (name != null) {
            return null;
        }

        throw new IllegalArgumentException(
            "Received parameter name is illegal"
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
                Type type = getModel(
                    sensor.getType()
                );
                Coder<?> coder = sensor.getCoder();

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
                    Type type = getModel(
                        sensor.getType()
                    );
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
         * Use this builder to resolve unknown mold type
         * and replace type variables as much as possible
         *
         * @param mold the specified mold type
         * @throws IllegalArgumentException If the mold is illegal
         */
        @Override
        public Type getModel(
            @NotNull Type mold
        ) {
            if (mold instanceof Class ||
                mold instanceof GenericArrayType ||
                mold instanceof ParameterizedType) {
                return mold;
            }

            if (mold instanceof WildcardType) {
                return getModel(
                    ((WildcardType) mold).getUpperBounds()[0]
                );
            }

            if (mold instanceof TypeVariable) {
                Type actor = type;
                Class<?> clazz = subject.getType();

                if (clazz != null) {
                    // If GenericDeclaration is method,
                    // then a ClassCastException is thrown
                    Class<?> entry = (Class<?>) (
                        (TypeVariable<?>) mold).getGenericDeclaration();

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
                        return super.getModel(mold);
                    }

                    if (actor instanceof ParameterizedType) {
                        Object[] items = entry.getTypeParameters();
                        for (int i = 0; i < items.length; i++) {
                            if (mold == items[i]) {
                                return getModel(
                                    ((ParameterizedType) actor).getActualTypeArguments()[i]
                                );
                            }
                        }
                    }
                }
                throw new IllegalStateException(
                    "Failed to resolve " + mold + " from " + actor
                );
            }
            return super.getModel(mold);
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
        public void onOpen()
            throws IOException {
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
                Type type = getModel(
                    sensor.getType()
                );
                Coder<?> coder = sensor.getCoder();

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
                    Type type = getModel(
                        sensor.getType()
                    );
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
    class Builder2<T> extends Builder1<T> {

        protected Cache cache;
        protected Class<?> owner;
        protected boolean delay;

        public Builder2(
            Type type,
            Class<?> own,
            Object[] args,
            Subject<T> spare
        ) {
            super(
                type,
                args, spare
            );
            this.owner = own;
        }

        /**
         * Prepare before parsing
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onOpen() throws IOException {
            Class<?> own = owner;
            if (own != null) {
                Factory holder = getParent();
                if (holder instanceof Builder) {
                    Object bean = ((Builder<?>) holder).build();
                    if (bean == null) {
                        throw new IOException(
                            "The parent result is is null"
                        );
                    } else {
                        if (own.isInstance(bean)) {
                            args[0] = bean;
                        } else {
                            throw new IOException(
                                "The parent result is not " + own
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

                Type type = getModel(
                    sensor.getType()
                );
                Coder<?> coder = sensor.getCoder();

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
                    Type type = getModel(
                        sensor.getType()
                    );
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
