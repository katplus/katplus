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
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

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
     * Embeds the {@link Spare} of {@link Class}
     * If there is no cache, try to create a {@link Spare}
     *
     * @param klass specify the type of embedding
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    static <T> Spare<T> embed(
        @NotNull Class<T> klass
    ) {
        return Cluster.INS.embed(
            klass, Impl.INS
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
    static <T> Spare<?> embed(
        @NotNull Class<T> klass,
        @NotNull Spare<? super T> spare
    ) {
        return Cluster.INS.put(
            klass, spare
        );
    }

    /**
     * Removes the {@code type} and returns the previous value associated with {@code type}
     *
     * @param type specify the type of revoking
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     */
    @Nullable
    static Spare<?> revoke(
        @NotNull Type type
    ) {
        return Cluster.INS.remove(type);
    }

    /**
     * Lookup the {@link Spare} of {@link Type}
     *
     * @param type specify the type of lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     */
    @Nullable
    static Spare<?> lookup(
        @NotNull Type type
    ) {
        return Cluster.INS.get(type);
    }

    /**
     * Lookup the {@link Spare} of {@link Class}
     *
     * @param klass specify the type of lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    static <T> Spare<T> lookup(
        @NotNull Class<T> klass
    ) {
        return Cluster.INS.lookup(
            klass, Impl.INS
        );
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    class Cluster extends ConcurrentHashMap<Type, Spare<?>> {
        /**
         * default cluster
         */
        static final Cluster INS = new Cluster();

        public Cluster() {
            super(Config.get(
                "kat.spare.capacity", 32
            ));
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
            INS.put(BigInteger.class, BigIntegerSpare.INSTANCE);
            INS.put(BigDecimal.class, BigDecimalSpare.INSTANCE);
            INS.put(Map.class, MapSpare.INSTANCE);
            INS.put(HashMap.class, MapSpare.INSTANCE);
            INS.put(LinkedHashMap.class, MapSpare.INSTANCE);
            INS.put(Set.class, SetSpare.INSTANCE);
            INS.put(HashSet.class, SetSpare.INSTANCE);
            INS.put(List.class, ListSpare.INSTANCE);
            INS.put(ArrayList.class, ListSpare.INSTANCE);
            INS.put(Date.class, DateSpare.INSTANCE);
            INS.put(LocalDate.class, LocalDateSpare.INSTANCE);
        }

        /**
         * Embeds {@link Spare} of the specified {@link Class}
         *
         * @throws NullPointerException If the specified {@code klass} is null
         */
        @Nullable
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T> Spare<T> embed(
            @NotNull Class<T> klass,
            @NotNull Supplier supplier
        ) {
            Spare<?> spare = get(klass);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            if (klass.isArray()) {
                return (Spare<T>) get(
                    Object[].class
                );
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
                        return (Spare<T>) spare;
                    }

                    if (!with.isInterface()) {
                        return Reflex.apply(with);
                    }
                }
            }

            if (klass.isEnum()) {
                Spare<T> $spare;
                put(klass, $spare =
                    new EnumSpare(klass, embed)
                );
                return $spare;
            }

            if (klass.isInterface()) {
                return null;
            }

            try {
                Spare<T> $spare;
                put(klass, $spare =
                    new ReflectSpare<>(
                        embed, klass, supplier
                    )
                );
                return $spare;
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Lookup {@link Spare} of the specified {@link Class}
         *
         * @throws NullPointerException If the specified {@code klass} is null
         */
        @Nullable
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T> Spare<T> lookup(
            @NotNull Class<T> klass,
            @NotNull Supplier supplier
        ) {
            Spare<?> spare = get(klass);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            if (klass.isArray()) {
                return (Spare<T>) get(
                    Object[].class
                );
            }

            Embed embed = klass
                .getAnnotation(Embed.class);

            if (embed == null) {
                return null;
            }

            Class<? extends Spare>
                with = embed.with();

            if (with != Spare.class) {
                // static inject
                // and double-checking
                spare = get(klass);

                if (spare != null) {
                    return (Spare<T>) spare;
                }

                if (!with.isInterface()) {
                    return Reflex.apply(with);
                }
            }

            if (klass.isInterface()) {
                return null;
            }

            try {
                Spare<T> $spare;
                put(klass, $spare =
                    new ReflectSpare<>(
                        embed, klass, supplier
                    )
                );
                return $spare;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
