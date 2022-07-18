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
import plus.kat.utils.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static plus.kat.Supplier.Impl;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Spare<K> extends Coder<K> {
    /**
     * Returns the space of {@link Spare}
     */
    @NotNull
    CharSequence getSpace();

    /**
     * Returns the flag of {@link Spare}
     */
    @Nullable
    Boolean getFlag();

    /**
     * Check if this {@link Spare} can build this {@code klass}
     *
     * @param klass specify the {@link Class} to compare
     * @throws NullPointerException If the specified {@code klass} is null
     */
    boolean accept(
        @NotNull Class<?> klass
    );

    /**
     * Returns the {@link Class} of {@link K}
     */
    @NotNull
    Class<? extends K> getType();

    /**
     * Create a {@link Builder} of {@link K}
     */
    @Nullable
    Builder<? extends K> getBuilder(
        @Nullable Type type
    );

    /**
     * Parse {@link Kat} {@link CharSequence} and convert result to {@link K}
     *
     * @param text specify the {@code text} to be parsed
     * @throws NullPointerException If the specified {@code text} is null
     */
    @Nullable
    default K read(
        @NotNull CharSequence text
    ) {
        return read(
            new Event<>(text)
        );
    }

    /**
     * Parse {@link Kat} {@link Event} and convert result to {@link K}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code event} is null
     */
    @Nullable
    default <T extends K> T read(
        @NotNull Event<T> event
    ) {
        return solve(
            Job.KAT, event
        );
    }

    /**
     * Serialize to {@link Chan}
     *
     * @param value specify serialized value
     */
    @NotNull
    default Chan write(
        @Nullable K value
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
        @Nullable K value, long flags
    ) {
        return new Chan(this, value, flags);
    }

    /**
     * Parse {@link Doc} {@link CharSequence} and convert result to {@link K}
     *
     * @param text specify the {@code text} to be parsed
     * @throws NullPointerException If the specified {@code text} is null
     */
    @Nullable
    default K down(
        @NotNull CharSequence text
    ) {
        return down(
            new Event<>(text)
        );
    }

    /**
     * Parse {@link Doc} {@link Event} and convert result to {@link K}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code event} is null
     */
    @Nullable
    default <T extends K> T down(
        @NotNull Event<T> event
    ) {
        return solve(
            Job.DOC, event
        );
    }

    /**
     * Serialize to {@link Doc}
     *
     * @param value specify serialized value
     */
    @NotNull
    default Doc mark(
        @Nullable K value
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
        @Nullable K value, long flags
    ) {
        return new Doc(this, value, flags);
    }

    /**
     * Parse {@link Json} {@link CharSequence} and convert result to {@link K}
     *
     * @param text specify the {@code text} to be parsed
     * @throws NullPointerException If the specified {@code text} is null
     */
    @Nullable
    default K parse(
        @NotNull CharSequence text
    ) {
        return parse(
            new Event<>(text)
        );
    }

    /**
     * Parse {@link Json} {@link Event} and convert result to {@link K}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code event} is null
     */
    @Nullable
    default <T extends K> T parse(
        @NotNull Event<T> event
    ) {
        return solve(
            Job.JSON, event
        );
    }

    /**
     * Serialize to {@link Json}
     *
     * @param value specify serialized value
     */
    @NotNull
    default Json serial(
        @Nullable K value
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
        @Nullable K value, long flags
    ) {
        return new Json(this, value, flags);
    }

    /**
     * Convert the {@link Object} to {@code K}
     *
     * @param data specify the {@code data} to convert
     * @see Spare#cast(Supplier, Object)
     */
    @Nullable
    default K cast(
        @Nullable Object data
    ) {
        return cast(
            Impl.INS, data
        );
    }

    /**
     * Convert the {@link Object} to {@code K}
     *
     * @param supplier the specified {@code supplier}
     * @param data     specify the {@code data} to convert
     */
    @Nullable
    default K cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, supplier
            );
        }
        return null;
    }

    /**
     * Parse {@link Event} and convert result to {@link K}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code event} is null
     * @since 0.0.2
     */
    @Nullable
    default <T extends K> T solve(
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        event.with(this);
        return Parser.solve(
            job, event
        );
    }

    /**
     * Register the {@link Spare} of {@link Class}
     *
     * @param klass specify the type of embedding
     * @param spare specify the {@code spare} of {@link Class}
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    static Spare<?> embed(
        @NotNull Class<?> klass,
        @NotNull Spare<?> spare
    ) {
        return Cluster.INS.put(
            klass, spare
        );
    }

    /**
     * Removes the {@code klass} and returns the previous value associated with {@code type}
     *
     * @param klass specify the type of revoking
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     */
    @Nullable
    static Spare<?> revoke(
        @NotNull Class<?> klass
    ) {
        return Cluster.INS.remove(klass);
    }

    /**
     * Returns the {@link Spare} of {@link Class}
     *
     * @param klass specify the type of lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    static <T> Spare<T> lookup(
        @NotNull Class<T> klass
    ) {
        return Cluster.INS.load(
            klass, Impl.INS
        );
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    class Cluster extends ConcurrentHashMap<Type, Spare<?>> implements Provider {
        /**
         * default cluster
         */
        static final Cluster INS = new Cluster();

        /**
         * default providers
         */
        protected final Provider[] providers;

        public Cluster() {
            super(Config.get(
                "kat.spare.capacity", 32
            ));

            Loader<Provider> loader =
                new Loader<>(Provider.class);

            try {
                loader.load(
                    Config.get(
                        "kat.spare.provider",
                        "plus.kat.spare.Provider"
                    )
                );

                int size = loader.size();
                providers = new Provider[size + 1];

                int i = 0;
                while (loader.hasNext()) {
                    providers[i++] = loader.next();
                }
                providers[i] = this;
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        static {
            INS.put(Object.class, ObjectSpare.INSTANCE);
            INS.put(String.class, StringSpare.INSTANCE);
            INS.put(int.class, IntegerSpare.INSTANCE);
            INS.put(Integer.class, IntegerSpare.INSTANCE);
            INS.put(long.class, LongSpare.INSTANCE);
            INS.put(Long.class, LongSpare.INSTANCE);
            INS.put(float.class, FloatSpare.INSTANCE);
            INS.put(Float.class, FloatSpare.INSTANCE);
            INS.put(double.class, DoubleSpare.INSTANCE);
            INS.put(Double.class, DoubleSpare.INSTANCE);
            INS.put(boolean.class, BooleanSpare.INSTANCE);
            INS.put(Boolean.class, BooleanSpare.INSTANCE);
            INS.put(byte.class, ByteSpare.INSTANCE);
            INS.put(Byte.class, ByteSpare.INSTANCE);
            INS.put(short.class, ShortSpare.INSTANCE);
            INS.put(Short.class, ShortSpare.INSTANCE);
            INS.put(char.class, CharSpare.INSTANCE);
            INS.put(Character.class, CharSpare.INSTANCE);
            INS.put(byte[].class, ByteArraySpare.INSTANCE);
            INS.put(Object[].class, ArraySpare.INSTANCE);
            INS.put(Map.class, MapSpare.INSTANCE);
            INS.put(Set.class, SetSpare.INSTANCE);
            INS.put(List.class, ListSpare.INSTANCE);
            INS.put(Iterable.class, IterableSpare.INSTANCE);
            INS.put(BigInteger.class, BigIntegerSpare.INSTANCE);
            INS.put(BigDecimal.class, BigDecimalSpare.INSTANCE);
        }

        /**
         * Embeds {@link Spare} of the specified {@link Class}
         *
         * @throws NullPointerException If the specified {@code klass} is null
         */
        @Nullable
        @SuppressWarnings("unchecked")
        public <T> Spare<T> load(
            @NotNull Class<T> klass,
            @NotNull Supplier supplier
        ) {
            Spare<?> spare = get(klass);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            for (Provider p : providers) {
                spare = p.lookup(
                    klass, supplier
                );

                if (spare != null) {
                    return (Spare<T>) spare;
                }
            }

            return null;
        }

        /**
         * Returns {@link Spare} of the specified {@link Class}
         *
         * @throws NullPointerException If the specified {@code klass} is null
         */
        @Nullable
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Spare<?> lookup(
            @NotNull Class<?> klass,
            @NotNull Supplier supplier
        ) {
            Spare<?> spare;
            if (klass.isArray()) {
                return ArraySpare.INSTANCE;
            }

            // filter platform type
            String name = klass.getName();
            switch (name.charAt(0)) {
                case 'j': {
                    if (name.startsWith("java.")) {
                        return onJava(
                            name, klass
                        );
                    }
                    if (name.startsWith("javax.")) {
                        return null;
                    }
                    break;
                }
                case 'k': {
                    if (name.startsWith("kotlin.") ||
                        name.startsWith("kotlinx.")) {
                        return null;
                    }
                    break;
                }
                case 's': {
                    if (name.startsWith("scala.")) {
                        return null;
                    }
                    break;
                }
                case 'a': {
                    if (name.startsWith("android.") ||
                        name.startsWith("androidx.")) {
                        return null;
                    }
                    break;
                }
            }

            Embed embed = klass
                .getAnnotation(Embed.class);

            if (embed != null) {
                Class<? extends Spare>
                    with = embed.with();

                if (with != Spare.class) {
                    // static inject
                    // and double-checking
                    spare = get(klass);

                    if (spare != null) {
                        return spare;
                    }

                    if (!with.isInterface()) {
                        return Reflex.apply(with);
                    }
                }
            }

            if (klass.isInterface()) {
                return null;
            }

            if (klass.isEnum()) {
                put(klass, spare =
                    new EnumSpare(
                        klass, embed, supplier
                    )
                );
                return spare;
            }

            try {
                put(klass, spare =
                    new ReflectSpare<>(
                        embed, klass, supplier
                    )
                );
                return spare;
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Returns {@link Spare} of the specified {@link Class}
         *
         * @throws NullPointerException If the specified {@code klass} is null
         */
        public Spare<?> onJava(
            @NotNull String name,
            @NotNull Class<?> klass
        ) {
            // filter internal class
            int d = name.indexOf('$', 6);
            if (d != -1) {
                return null;
            }

            // lookup the appropriate spare
            Spare<?> spare;
            switch (name.charAt(5)) {
                // java.nio
                // java.net
                case 'n': {
                    if (klass == URI.class) {
                        spare = URISpare.INSTANCE;
                    } else if (klass == URL.class) {
                        spare = URLSpare.INSTANCE;
                    } else if (ByteBuffer.class.isAssignableFrom(klass)) {
                        spare = ByteBufferSpare.INSTANCE;
                    } else {
                        return null;
                    }

                    this.put(
                        klass, spare
                    );
                    return spare;
                }
                // java.time
                case 't': {
                    if (klass == Instant.class) {
                        spare = InstantSpare.INSTANCE;
                    } else if (klass == LocalDate.class) {
                        spare = LocalDateSpare.INSTANCE;
                    } else {
                        return null;
                    }

                    this.put(
                        klass, spare
                    );
                    return spare;
                }
                // java.util
                case 'u': {
                    switch (name.lastIndexOf('.')) {
                        // java.util.
                        case 9: {
                            if (klass == Date.class) {
                                spare = DateSpare.INSTANCE;
                            } else if (klass == UUID.class) {
                                spare = UUIDSpare.INSTANCE;
                            } else if (klass == BitSet.class) {
                                spare = BitSetSpare.INSTANCE;
                            } else if (klass == Currency.class) {
                                spare = CurrencySpare.INSTANCE;
                            } else if (klass == Locale.class) {
                                spare = LocaleSpare.INSTANCE;
                            } else if (Map.class.isAssignableFrom(klass)) {
                                spare = MapSpare.INSTANCE;
                            } else if (Set.class.isAssignableFrom(klass)) {
                                spare = SetSpare.INSTANCE;
                            } else if (List.class.isAssignableFrom(klass)) {
                                spare = ListSpare.INSTANCE;
                            } else {
                                return null;
                            }

                            this.put(
                                klass, spare
                            );
                            return spare;
                        }
                        // java.util.concurrent.
                        case 20: {
                            if (Map.class.isAssignableFrom(klass)) {
                                spare = MapSpare.INSTANCE;
                            } else if (Set.class.isAssignableFrom(klass)) {
                                spare = SetSpare.INSTANCE;
                            } else if (List.class.isAssignableFrom(klass)) {
                                spare = ListSpare.INSTANCE;
                            } else {
                                return null;
                            }

                            this.put(
                                klass, spare
                            );
                            return spare;
                        }
                        // java.util.concurrent.atomic.
                        case 27: {
                            if (klass == AtomicLong.class) {
                                spare = AtomicLongSpare.INSTANCE;
                            } else if (klass == AtomicInteger.class) {
                                spare = AtomicIntegerSpare.INSTANCE;
                            } else if (klass == AtomicBoolean.class) {
                                spare = AtomicBooleanSpare.INSTANCE;
                            } else {
                                return null;
                            }

                            this.put(
                                klass, spare
                            );
                            return spare;
                        }
                        default: {
                            return null;
                        }
                    }
                }
                default: {
                    return null;
                }
            }
        }
    }
}
