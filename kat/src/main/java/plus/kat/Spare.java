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

import plus.kat.crash.*;
import plus.kat.spare.*;
import plus.kat.reflex.*;
import plus.kat.utils.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.BiConsumer;

import static plus.kat.Plan.DEF;
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
     * Check if {@code klass} is a parent Class of {@link K}
     * or this {@link Spare} can create an instance of {@code klass}
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
     * Returns the {@link Provider} of {@link Spare}
     *
     * @since 0.0.3
     */
    @Nullable
    default Provider getProvider() {
        return null;
    }

    /**
     * If this {@link Spare} can create an instance,
     * it returns it, otherwise it will return {@code null}
     *
     * @return {@link K} or {@code null}
     * @since 0.0.3
     */
    @Nullable
    default K apply() {
        return null;
    }

    /**
     * If {@link K} is a Bean or resultSet only has one element,
     * then perform a given {@link ResultSet} to create a {@link K}
     *
     * @param result the specified {@code resultSet} to be used
     * @throws SQLCrash             If it fails to create
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the {@code result} is null
     * @see Spare#apply(Supplier, ResultSet)
     * @see Workman#apply(Supplier, ResultSet)
     * @see Property#apply(Supplier, ResultSet)
     * @since 0.0.3
     */
    @NotNull
    default K apply(
        @NotNull ResultSet result
    ) throws SQLException {
        return apply(
            Impl.INS, result
        );
    }

    /**
     * If {@link K} is a Bean or resultSet only has one element,
     * then perform a given {@link ResultSet} to create a {@link K}
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *
     *  ResultSet rs = stmt.executeQuery(sql);
     *  List<User> users = new ArrayList<>();
     *
     *  while (rs.next()) {
     *    users.add(
     *      spare.apply(rs)
     *    );
     *  }
     * }</pre>
     *
     * @param supplier  the specified {@code supplier}
     * @param resultSet the specified {@code resultSet} to be used
     * @throws SQLCrash             If it fails to create
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the {@code supplier} or {@code resultSet} is null
     * @see Workman#apply(Supplier, ResultSet)
     * @since 0.0.3
     */
    @NotNull
    default K apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        throw new SQLCrash(
            "Unexpectedly, '" + getType() + "' not a Bean"
        );
    }

    /**
     * If {@link K} is a Bean, then perform a given
     * action in each item until all entries are processed.
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  Map<String, Object> collector = ...
     *
     *  User user = ...
     *  spare.flat(
     *    user, collector::put
     *  );
     *
     *  int id = (int) collector.get("id");
     *  String name = (String) collector.get("name");
     * }</pre>
     *
     * @return {@code true} if the action is consumed otherwise {@code false}
     * @throws NullPointerException If the {@code bean} or {@code action} is null
     * @since 0.0.3
     */
    default boolean flat(
        @NotNull K bean,
        @NotNull BiConsumer<String, Object> action
    ) {
        return false;
    }

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
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan write(
        @Nullable K value
    ) throws IOException {
        return write(
            value, DEF.writeFlags
        );
    }

    /**
     * Serialize to {@link Chan}
     *
     * @param value specify serialized value
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan write(
        @Nullable K value, long flags
    ) throws IOException {
        Chan chan = new Chan(flags);
        chan.set(null, this, value);
        return chan;
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
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Doc mark(
        @Nullable K value
    ) throws IOException {
        return mark(
            value, DEF.writeFlags
        );
    }

    /**
     * Serialize to {@link Doc}
     *
     * @param value specify serialized value
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Doc mark(
        @Nullable K value, long flags
    ) throws IOException {
        Doc chan = new Doc(flags);
        chan.set(null, this, value);
        return chan;
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
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Json serial(
        @Nullable K value
    ) throws IOException {
        return serial(
            value, DEF.writeFlags
        );
    }

    /**
     * Serialize to {@link Json}
     *
     * @param value specify serialized value
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Json serial(
        @Nullable K value, long flags
    ) throws IOException {
        Json chan = new Json(flags);
        chan.set(null, this, value);
        return chan;
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
                this, (CharSequence) data, null, supplier
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

        private Cluster() {
            super(Config.get(
                "kat.spare.capacity", 32
            ));

            KatLoader<Provider> loader =
                new KatLoader<>(Provider.class);

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
            INS.put(Number.class, NumberSpare.INSTANCE);
            INS.put(byte[].class, ByteArraySpare.INSTANCE);
            INS.put(Object[].class, ArraySpare.INSTANCE);
            INS.put(CharSequence.class, StringSpare.INSTANCE);
            INS.put(Map.class, MapSpare.INSTANCE);
            INS.put(Set.class, SetSpare.INSTANCE);
            INS.put(List.class, ListSpare.INSTANCE);
            INS.put(Iterable.class, IterableSpare.INSTANCE);
            INS.put(BigInteger.class, BigIntegerSpare.INSTANCE);
            INS.put(BigDecimal.class, BigDecimalSpare.INSTANCE);
            INS.put(StringBuffer.class, StringBufferSpare.INSTANCE);
            INS.put(StringBuilder.class, StringBuilderSpare.INSTANCE);
        }

        /**
         * spare providers
         */
        final Provider[] providers;

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
                try {
                    spare = p.lookup(
                        klass, supplier
                    );
                } catch (RunCrash e) {
                    return null;
                } catch (Exception e) {
                    continue;
                }

                if (spare != null) {
                    return (Spare<T>) spare;
                }
            }

            return null;
        }

        /**
         * Returns {@link Spare} of the specified {@code klass}
         *
         * @throws RunCrash             The Provider signals to interrupt subsequent lookup
         * @throws NullPointerException If the specified {@code klass} is null
         */
        @Nullable
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Spare<?> lookup(
            @NotNull Class<?> klass,
            @NotNull Supplier supplier
        ) {
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
                    if (name.startsWith("jdk.") ||
                        name.startsWith("javax.")) {
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
                    if (name.startsWith("sun.") ||
                        name.startsWith("scala.")) {
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
                .getAnnotation(
                    Embed.class
                );

            if (embed != null) {
                Class<?> target = embed.with();
                if (target != Spare.class) {
                    // static inject
                    // and double-checking
                    Spare<?> spare =
                        get(klass);

                    if (spare != null) {
                        return spare;
                    }

                    if (!Spare.class.
                        isAssignableFrom(target)) {
                        return load(
                            target, supplier
                        );
                    }

                    if (target.isInterface()) {
                        return null;
                    }

                    spare = Reflect.apply(target);
                    if (spare != null) {
                        putIfAbsent(
                            klass, spare
                        );
                    }
                    return spare;
                }
            }

            if (klass.isInterface() ||
                Kat.class.isAssignableFrom(klass)) {
                return null;
            }

            Class<?> sc = klass.getSuperclass();
            if (sc == Enum.class) {
                return new EnumSpare(
                    klass, embed, supplier
                );
            }

            try {
                String sn = sc.getName();
                if (sn.equals("java.lang.Record")) {
                    return new RecordSpare<>(
                        embed, klass, supplier, this
                    );
                } else {
                    return new ReflectSpare<>(
                        embed, klass, supplier, this
                    );
                }
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
                // java.io
                case 'i': {
                    if (klass == File.class) {
                        spare = FileSpare.INSTANCE;
                    } else {
                        return null;
                    }
                    this.put(
                        klass, spare
                    );
                    return spare;
                }
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
                    } else if (klass == LocalTime.class) {
                        spare = LocalTimeSpare.INSTANCE;
                    } else if (klass == LocalDateTime.class) {
                        spare = LocalDateTimeSpare.INSTANCE;
                    } else if (klass == ZonedDateTime.class) {
                        spare = ZonedDateTimeSpare.INSTANCE;
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
