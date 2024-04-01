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
package plus.kat;

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

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
     * @return {@link T} or throw an exception
     * @throws IllegalStateException If failed to build
     */
    @NotNull
    default T apply() {
        throw new IllegalStateException(
            "No corresponding constructor"
        );
    }

    /**
     * Returns the {@link Segment} of
     * the specified param {@code name}
     *
     * @param name the param name
     * @return {@link Segment} or null
     * @throws IllegalArgumentException Wrong name
     */
    @Nullable
    Segment arg(
        @NotNull Object name
    );

    /**
     * Returns a set-capable {@link Segment}
     * of the specified property {@code name}
     *
     * @param name the property name
     * @return {@link Segment} or null
     * @throws IllegalArgumentException Wrong name
     */
    @Nullable
    Segment set(
        @NotNull Object name
    );

    /**
     * Returns a get-capable {@link Segment}
     * of the specified property {@code name}
     *
     * @param name the property name
     * @return {@link Segment} or null
     * @throws IllegalArgumentException Wrong name
     */
    @Nullable
    Segment get(
        @NotNull Object name
    );

    /**
     * Returns a new object based on the arguments,
     * otherwise an exception will be thrown directly
     *
     * @param args the specified args of constructor
     * @return {@link T} or throw an exception
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
     * @author kraity
     * @since 0.0.6
     */
    interface Segment {
        /**
         * Gets the value of this property
         *
         * @return the value of property
         * @throws IllegalStateException If failed to call this method
         */
        @Nullable
        Object apply(
            @NotNull Object bean
        );

        /**
         * Sets the new value of this property
         *
         * @return true on successful update
         * @throws IllegalStateException If failed to call this method
         */
        boolean accept(
            @NotNull Object bean,
            @Nullable Object value
        );

        /**
         * Returns the type of this property
         *
         * @return the generic type of property
         * @throws IllegalStateException If failed to call this method
         */
        @NotNull
        default Type getType() {
            throw new IllegalStateException(
                "Failed to call Property#getType"
            );
        }

        /**
         * Returns the coder of this property
         *
         * @return the custom coder of property
         * @throws IllegalStateException If failed to call this method
         */
        @Nullable
        default Coder<?> getCoder() {
            throw new IllegalStateException(
                "Failed to call Property#getCoder"
            );
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    class Builder0<T> extends Builder<T> {

        protected T bean;
        protected Type type;

        protected Segment setter;
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
                    "Received the subject is null"
                );
            }
        }

        /**
         * Prepares this {@link Factory} before parsing
         */
        @Override
        public void onCreate()
            throws IOException {
            bean = subject.apply();
        }

        /**
         * Opens a builder for the current property
         *
         * @param alias the alias of the current property
         * @param space the space of the current property
         */
        @Nullable
        public Pipe onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Segment segment = subject.set(alias);

            if (segment != null) {
                Type type = getType(
                    segment.getType()
                );
                Coder<?> coder = segment.getCoder();

                if (coder == null) {
                    coder = context.assign(type, space);
                    if (coder == null) {
                        throw new IOException(
                            "No Coder for property(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                Factory member =
                    coder.getFactory(type);
                if (member != null) {
                    setter = segment;
                    return member.attach(this);
                }
            }

            return null;
        }

        /**
         * Receives the alias, spare and value in a loop
         *
         * @param alias the alias of the current property
         * @param space the space of the current property
         * @param value the value of the current property
         */
        @Override
        public void onNext(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Segment segment = subject.set(alias);

            if (segment != null) {
                Coder<?> coder = segment.getCoder();

                if (coder == null) {
                    Type type = getType(
                        segment.getType()
                    );
                    coder = context.assign(type, space);

                    if (coder == null) {
                        throw new IOException(
                            "No spare for property(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                segment.accept(
                    bean, coder.read(
                        this, value
                    )
                );
            }
        }

        /**
         * Receives the value of the current property
         *
         * @param value the value of the current property
         */
        @Override
        public void onNext(
            @Nullable Object value
        ) throws IOException {
            setter.accept(
                bean, value
            );
        }

        /**
         * Returns the type of factory to build
         */
        @Override
        public Type getType() {
            return type;
        }

        /**
         * Use this factory to resolve generic type
         * and replace type variables as much as possible
         *
         * @param generic the specified generic type
         * @throws IllegalArgumentException If the generic is illegal
         */
        @Override
        public Type getType(Type generic) {
            if (generic instanceof Class ||
                generic instanceof GenericArrayType ||
                generic instanceof ParameterizedType) {
                return generic;
            }

            if (generic instanceof WildcardType) {
                return getType(
                    ((WildcardType) generic).getUpperBounds()[0]
                );
            }

            if (generic instanceof TypeVariable) {
                Type actor = type;
                Class<?> clazz = subject.getType();

                if (clazz != null) {
                    // If GenericDeclaration is method,
                    // then a ClassCastException is thrown
                    Class<?> entry = (Class<?>) (
                        (TypeVariable<?>) generic).getGenericDeclaration();

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
                        return super.getType(generic);
                    }

                    if (actor instanceof ParameterizedType) {
                        Object[] items = entry.getTypeParameters();
                        for (int i = 0; i < items.length; i++) {
                            if (generic == items[i]) {
                                return getType(
                                    ((ParameterizedType) actor).getActualTypeArguments()[i]
                                );
                            }
                        }
                    }
                }
                throw new IllegalStateException(
                    "Failed to resolve " + generic + " from " + actor
                );
            }
            return super.getType(generic);
        }

        /**
         * Returns the result of building {@link T}
         */
        @Nullable
        public T build() {
            return bean;
        }

        /**
         * Releases the resources of this {@link Builder}
         */
        @Override
        public void onDestroy() throws IOException {
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

        @Override
        public void onCreate()
            throws IOException {
            // Nothing
        }

        @Nullable
        public Pipe onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Segment segment = subject.arg(alias);

            if (segment != null) {
                Type type = getType(
                    segment.getType()
                );
                Coder<?> coder = segment.getCoder();

                if (coder == null) {
                    coder = context.assign(type, space);
                    if (coder == null) {
                        throw new IOException(
                            "No spare for argument(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                Factory member =
                    coder.getFactory(type);
                if (member != null) {
                    setter = segment;
                    return member.attach(this);
                }
            }

            return null;
        }

        @Override
        public void onNext(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Segment segment = subject.arg(alias);

            if (segment != null) {
                Coder<?> coder = segment.getCoder();

                if (coder == null) {
                    Type type = getType(
                        segment.getType()
                    );
                    coder = context.assign(type, space);

                    if (coder == null) {
                        throw new IOException(
                            "No spare for argument(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                segment.accept(
                    args, coder.read(
                        this, value
                    )
                );
            }
        }

        @Override
        public void onNext(
            @Nullable Object value
        ) throws IOException {
            setter.accept(
                args, value
            );
        }

        @Nullable
        public T build() {
            if (bean == null) {
                bean = subject.apply(args);
            }
            return bean;
        }

        @Override
        public void onDestroy() throws IOException {
            args = null;
            super.onDestroy();
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    class Builder2<T> extends Builder1<T> {

        protected Cache cache;
        protected Class<?> owner;
        protected boolean caching;

        public Builder2(
            Type type,
            Class<?> own,
            Object[] args,
            Subject<T> spare
        ) {
            super(type, args, spare);
            owner = own;
        }

        @Override
        public void onCreate()
            throws IOException {
            Class<?> own = owner;
            if (own != null) {
                Factory parent = getParent();
                if (parent instanceof Builder) {
                    Object bean = ((Builder<?>) parent).build();
                    if (bean == null) {
                        throw new IOException(
                            "The parent result is is null"
                        );
                    } else {
                        if (own.isInstance(bean)) {
                            args[0] = bean;
                        } else {
                            throw new IOException(
                                "The parent is not " + own
                            );
                        }
                    }
                } else {
                    throw new IOException(
                        "Could not find the result: " + parent
                    );
                }
            }
        }

        @Nullable
        public Pipe onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Segment segment = subject.arg(alias);

            scope:
            {
                caching = false;
                if (segment == null) {
                    segment = subject.set(alias);
                    if (segment == null) {
                        break scope;
                    } else {
                        caching = true;
                    }
                }

                Type type = getType(
                    segment.getType()
                );
                Coder<?> coder = segment.getCoder();

                if (coder == null) {
                    coder = context.assign(type, space);
                    if (coder == null) {
                        throw new IOException(
                            "No spare for attribute(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                Factory member =
                    coder.getFactory(type);
                if (member != null) {
                    setter = segment;
                    return member.attach(this);
                }
            }

            return null;
        }

        @Override
        public void onNext(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Segment segment = subject.arg(alias);

            scope:
            {
                boolean delay = false;
                if (segment == null) {
                    segment = subject.set(alias);
                    if (segment == null) {
                        break scope;
                    } else {
                        delay = true;
                    }
                }

                Coder<?> coder = segment.getCoder();

                if (coder == null) {
                    Type type = getType(
                        segment.getType()
                    );
                    coder = context.assign(type, space);

                    if (coder == null) {
                        throw new IOException(
                            "No spare for attribute(" + alias
                                + ") of " + subject.getType() + " was found"
                        );
                    }
                }

                if (delay) {
                    new Cache(segment,
                        coder.read(this, value)
                    );
                } else {
                    segment.accept(
                        args, coder.read(
                            this, value
                        )
                    );
                }
            }
        }

        @Override
        public void onNext(
            @Nullable Object value
        ) throws IOException {
            if (caching) {
                new Cache(
                    setter, value
                );
            } else {
                setter.accept(
                    args, value
                );
            }
        }

        public class Cache {
            Cache next;
            Object value;
            Segment segment;

            public Cache(
                Segment seg, Object obj
            ) {
                value = obj;
                segment = seg;
                if (cache == null) {
                    cache = this;
                } else {
                    cache.next = this;
                }
            }
        }

        @Nullable
        public T build() {
            if (bean == null) {
                bean = subject.apply(args);
            }

            while (cache != null) {
                cache.segment.accept(
                    bean, cache.value
                );
                cache = cache.next;
            }
            return bean;
        }

        @Override
        public void onDestroy() throws IOException {
            cache = null;
            super.onDestroy();
        }
    }
}
