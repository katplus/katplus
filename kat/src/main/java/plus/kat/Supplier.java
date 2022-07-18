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

import plus.kat.anno.Embed;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.spare.*;
import plus.kat.entity.*;
import plus.kat.reflex.*;
import plus.kat.utils.Config;

import java.lang.reflect.Type;
import java.util.concurrent.*;

import static plus.kat.chain.Space.*;
import static plus.kat.Spare.Cluster;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Supplier {
    /**
     * Register the {@link Spare} of {@link Class}
     * and returns the previous value associated with {@code klass}
     *
     * @param klass specify the type of embedding
     * @param spare specify the {@code spare} of {@code klass}
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @since 0.0.2
     */
    @Nullable
    Spare<?> embed(
        @NotNull Class<?> klass,
        @NotNull Spare<?> spare
    );

    /**
     * Register the {@link Spare} of {@code klass}
     * and returns the previous value associated with {@code klass}
     *
     * @param klass specify the type of embedding
     * @param spare specify the {@code spare} of {@code klass}
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    Spare<?> embed(
        @NotNull CharSequence klass,
        @NotNull Spare<?> spare
    );

    /**
     * Removes the {@code klass} and returns the previous value associated with {@code klass}
     *
     * @param klass specify the klass of revoking
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    Spare<?> revoke(
        @NotNull CharSequence klass
    );

    /**
     * Removes the {@code type} and returns the previous value associated with {@code type}
     *
     * @param klass specify the klass of revoking
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     * @see Spare#revoke(Type)
     */
    @Nullable
    Spare<?> revoke(
        @NotNull Class<?> klass
    );

    /**
     * Returns the {@link Spare} of {@link CharSequence}
     *
     * @param klass specify the type of lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    Spare<?> lookup(
        @NotNull CharSequence klass
    );

    /**
     * Returns the {@link Spare} of {@link Class}
     *
     * @param klass specify the type of lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Spare#lookup(Class)
     */
    @Nullable
    default <T> Spare<T> lookup(
        @NotNull Class<T> klass
    ) {
        return Cluster.INS.load(klass, this);
    }

    /**
     * Activate an instance of {@link Coder}
     *
     * @param klass specify the type of apply
     * @return {@link Coder} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    default <T> Coder<T> activate(
        @NotNull Class<? extends Coder<T>> klass
    ) {
        return Plug.INS.apply(klass);
    }

    /**
     * Register an instance of {@link Coder}
     *
     * @param klass specify the type of embedding
     * @param coder specify the {@code spare} of {@link Class}
     * @return {@link Coder} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    default <T> Coder<?> activate(
        @NotNull Class<? extends Coder<T>> klass,
        @NotNull Coder<? super T> coder
    ) {
        return Plug.INS.put(klass, coder);
    }

    /**
     * Deactivate the {@code klass} and returns the previous value associated with {@code klass}
     *
     * @param klass specify the type of revoking
     * @return {@link Coder} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     */
    @Nullable
    default Coder<?> deactivate(
        @NotNull Class<? extends Coder<?>> klass
    ) {
        return Plug.INS.remove(klass);
    }

    /**
     * Returns the default plugins
     */
    @NotNull
    static Plug plug() {
        return Plug.INS;
    }

    /**
     * Returns the default {@link Supplier}
     */
    @NotNull
    static Supplier ins() {
        return Impl.INS;
    }

    /**
     * Convert the {@link Object} to {@code K}
     *
     * @param data specify the {@code data} to convert
     * @see Spare#cast(Supplier, Object)
     */
    @Nullable
    default <E> E cast(
        @NotNull Class<E> klass,
        @NotNull Object data
    ) {
        Spare<E> spare = lookup(klass);

        if (spare == null) {
            return null;
        }

        return spare.cast(this, data);
    }

    /**
     * Convert the {@link Object} to {@code K}
     *
     * @param data specify the {@code data} to convert
     * @see Spare#cast(Supplier, Object)
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <E> E cast(
        @NotNull CharSequence klass,
        @NotNull Object data
    ) {
        Spare<E> spare =
            (Spare<E>) lookup(klass);

        if (spare == null) {
            return null;
        }

        return spare.cast(this, data);
    }

    /**
     * Parse {@link Kat} {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(CharSequence, Job, Event)
     */
    @Nullable
    default <T> T read(
        @NotNull CharSequence klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Job.KAT, event
        );
    }

    /**
     * Parse {@link Kat} {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(Class, Job, Event)
     */
    @Nullable
    default <E, T extends E> T read(
        @NotNull Class<E> klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Job.KAT, event
        );
    }

    /**
     * Serialize to {@link Chan}
     *
     * @param value specify serialized value
     */
    @NotNull
    default Chan write(
        @Nullable Object value
    ) {
        return new Chan(this, value);
    }

    /**
     * Serialize to {@link Chan}
     *
     * @param value specify serialized value
     */
    @NotNull
    default Chan write(
        @Nullable Object value, long flags
    ) {
        return new Chan(this, value, flags);
    }

    /**
     * Parse {@link Doc} {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(CharSequence, Job, Event)
     */
    @Nullable
    default <T> T down(
        @NotNull CharSequence klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Job.DOC, event
        );
    }

    /**
     * Parse {@link Doc} {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(Class, Job, Event)
     */
    @Nullable
    default <E, T extends E> T down(
        @NotNull Class<E> klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Job.DOC, event
        );
    }

    /**
     * Serialize to {@link Doc}
     *
     * @param value specify serialized value
     */
    @NotNull
    default Doc mark(
        @Nullable Object value
    ) {
        return new Doc(this, value);
    }

    /**
     * Serialize to {@link Doc}
     *
     * @param value specify serialized value
     */
    @NotNull
    default Doc mark(
        @Nullable Object value, long flags
    ) {
        return new Doc(this, value, flags);
    }

    /**
     * Parse {@link Json} {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code klass} {@code event} is null
     * @see Supplier#solve(CharSequence, Job, Event)
     */
    @Nullable
    default <T> T parse(
        @NotNull CharSequence klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Job.JSON, event
        );
    }

    /**
     * Parse {@link Json} {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code klass} {@code event} is null
     * @see Supplier#solve(Class, Job, Event)
     */
    @Nullable
    default <E, T extends E> T parse(
        @NotNull Class<E> klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Job.JSON, event
        );
    }

    /**
     * Serialize to {@link Json}
     *
     * @param value specify serialized value
     */
    @NotNull
    default Json serial(
        @Nullable Object value
    ) {
        return new Json(this, value);
    }

    /**
     * Serialize to {@link Json}
     *
     * @param value specify serialized value
     */
    @NotNull
    default Json serial(
        @Nullable Object value, long flags
    ) {
        return new Json(this, value, flags);
    }

    /**
     * Parse {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Spare#solve(Job, Event)
     * @since 0.0.2
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <T> T solve(
        @NotNull CharSequence klass,
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        Spare<T> spare =
            (Spare<T>) lookup(klass);

        if (spare == null) {
            return null;
        }

        event.with(this);
        return spare.solve(job, event);
    }

    /**
     * Parse {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Spare#solve(Job, Event)
     * @since 0.0.2
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <T> T solve(
        @NotNull Type type,
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        Spare<T> spare = (Spare<T>)
            Reflex.lookup(type, this);

        if (spare == null) {
            return null;
        }

        event.with(this);
        event.prepare(type);

        return spare.solve(job, event);
    }

    /**
     * Parse {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Spare#solve(Job, Event)
     * @since 0.0.2
     */
    @Nullable
    default <E, T extends E> T solve(
        @NotNull Class<E> klass,
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        Spare<E> spare = lookup(klass);

        if (spare == null) {
            return null;
        }

        event.with(this);
        event.prepare(klass);

        return spare.solve(job, event);
    }

    /**
     * Register the {@link Spare} of {@link Class} with {@link Embed}
     *
     * @param embed specify the {@link Embed}
     * @param klass specify the type of embedding
     * @param spare specify the {@code spare} of {@link Class}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @NotNull
    default <T> CharSequence register(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Spare<? super T> spare
    ) {
        if (embed == null) {
            return klass.getName();
        }

        String[] spaces = embed.value();
        if (spaces.length == 0) {
            return klass.getName();
        }

        if (embed.expose()) {
            for (String space : spaces) {
                // start from the second char, require contains '.'
                if (space.indexOf('.', 1) != -1) {
                    embed(space, spare);
                }
            }
        }

        String space = spaces[0];
        return space.isEmpty() ? klass.getName() : space;
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    class Impl extends ConcurrentHashMap<CharSequence, Spare<?>> implements Supplier {
        /**
         * default supplier
         */
        static final Impl INS = new Impl();

        static {
            INS.put($, ObjectSpare.INSTANCE);
            INS.put($s, StringSpare.INSTANCE);
            INS.put($b, BooleanSpare.INSTANCE);
            INS.put($i, IntegerSpare.INSTANCE);
            INS.put($l, LongSpare.INSTANCE);
            INS.put($f, FloatSpare.INSTANCE);
            INS.put($d, DoubleSpare.INSTANCE);
            INS.put($c, CharSpare.INSTANCE);
            INS.put($o, ByteSpare.INSTANCE);
            INS.put($u, ShortSpare.INSTANCE);
            INS.put($M, MapSpare.INSTANCE);
            INS.put($A, ArraySpare.INSTANCE);
            INS.put($L, ListSpare.INSTANCE);
            INS.put($S, SetSpare.INSTANCE);
            INS.put($E, CrashSpare.INSTANCE);
            INS.put($B, ByteArraySpare.INSTANCE);
            INS.put($I, BigIntegerSpare.INSTANCE);
            INS.put($D, BigDecimalSpare.INSTANCE);
            INS.put(EMPTY, ObjectSpare.INSTANCE);
        }

        public Impl() {
            super(Config.get(
                "kat.supplier.capacity", 24
            ));
        }

        @Override
        public Spare<?> embed(
            @NotNull Class<?> klass,
            @NotNull Spare<?> spare
        ) {
            return Cluster.INS.put(
                klass, spare
            );
        }

        @Override
        public Spare<?> revoke(
            @NotNull Class<?> klass
        ) {
            return Cluster.INS.remove(klass);
        }

        @Override
        public Spare<?> embed(
            @NotNull CharSequence klass,
            @NotNull Spare<?> spare
        ) {
            return put(klass, spare);
        }

        @Override
        public Spare<?> lookup(
            CharSequence klass
        ) {
            return get(klass);
        }

        @Override
        public Spare<?> revoke(
            @NotNull CharSequence klass
        ) {
            return remove(klass);
        }
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    class Plug extends ConcurrentHashMap<Class<? extends Coder<?>>, Coder<?>> {
        /**
         * default cluster
         */
        static final Plug INS = new Plug();

        static {
            INS.put(ByteArrayCoder.class, ByteArrayCoder.INSTANCE);
        }

        public Plug() {
            super(Config.get(
                "kat.coder.capacity", 8
            ));
        }

        /**
         * Apply an instance of the specified {@link Coder}
         *
         * @throws NullPointerException If the specified {@code klass} is null
         */
        @Nullable
        @SuppressWarnings("unchecked")
        public <T> Coder<T> apply(
            @NotNull Class<? extends Coder<T>> klass
        ) {
            Coder<?> coder = get(klass);

            if (coder != null) {
                return (Coder<T>) coder;
            }

            if (klass.isInterface()) {
                return null;
            }

            return Reflex.apply(klass);
        }

        /**
         * Apply an instance of the specified {@link Coder}
         *
         * @throws NullPointerException If the specified {@code klass} is null
         */
        @Nullable
        @SuppressWarnings("unchecked")
        public <T> Coder<T> embed(
            @NotNull Class<? extends Coder<T>> klass
        ) {
            Coder<?> coder = get(klass);

            if (coder == null) {
                if (klass.isInterface()) {
                    return null;
                }

                coder = Reflex.apply(klass);
                if (coder != null) {
                    this.put(
                        klass, coder
                    );
                }
            }

            return (Coder<T>) coder;
        }
    }
}
