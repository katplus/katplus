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
import plus.kat.utils.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.*;

import static plus.kat.Plan.DEF;
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
     * @see Impl#embed(Class, Spare)
     * @see Spare#embed(Class, Spare)
     * @since 0.0.2
     */
    @Nullable
    Spare<?> embed(
        @NotNull Class<?> klass,
        @NotNull Spare<?> spare
    );

    /**
     * Removes the {@code type} and returns the previous value associated with {@code type}
     *
     * @param klass specify the klass of revoking
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     * @see Impl#revoke(Class)
     * @see Spare#revoke(Class)
     */
    @Nullable
    Spare<?> revoke(
        @NotNull Class<?> klass
    );

    /**
     * Register the {@link Spare} of {@code klass}
     * and returns the previous value associated with {@code klass}
     *
     * @param klass specify the type of embedding
     * @param spare specify the {@code spare} of {@code klass}
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#embed(CharSequence, Spare)
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
     * @see Impl#revoke(CharSequence)
     */
    @Nullable
    Spare<?> revoke(
        @NotNull CharSequence klass
    );

    /**
     * Returns the {@link Spare} of {@link Class}
     *
     * @param klass specify the type of lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#lookup(Class)
     * @see Spare#lookup(Class)
     */
    @Nullable <T> Spare<T> lookup(
        @NotNull Class<T> klass
    );

    /**
     * Returns the {@link Spare} of {@link CharSequence}
     *
     * @param klass specify the type of lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#lookup(CharSequence)
     */
    @Nullable <T> Spare<T> lookup(
        @NotNull CharSequence klass
    );

    /**
     * Returns the {@link Spare} of {@link CharSequence}
     *
     * @param klass specify the type of lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#lookup(CharSequence, boolean)
     * @since 0.0.3
     */
    @Nullable <T> Spare<T> lookup(
        @NotNull CharSequence klass, boolean search
    );

    /**
     * Activate an instance of {@link Coder}
     *
     * @param klass specify the type of apply
     * @return {@link Coder} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#activate(Class)
     */
    @Nullable <T> Coder<T> activate(
        @NotNull Class<?> klass
    );

    /**
     * Deactivate the {@code klass} and returns the previous value associated with {@code klass}
     *
     * @param klass specify the type of revoking
     * @return {@link Coder} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     * @see Impl#deactivate(Class)
     */
    @Nullable Coder<?> deactivate(
        @NotNull Class<?> klass
    );

    /**
     * Returns the default {@link Supplier}
     */
    @NotNull
    static Supplier ins() {
        return Impl.INS;
    }

    /**
     * If {@link E} is a Bean or resultSet only has one element,
     * then perform a given {@link ResultSet} to create a {@link E}
     *
     * @throws SQLCrash             If it fails to create
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the {@code klass} or {@code resultSet} is null
     * @see Spare#apply(Supplier, ResultSet)
     * @since 0.0.3
     */
    @Nullable
    default <E> E apply(
        @NotNull Class<E> klass,
        @NotNull ResultSet result
    ) throws SQLException {
        Spare<E> spare = lookup(klass);

        if (spare == null) {
            return null;
        }

        return spare.apply(this, result);
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
     * @throws ClassCastException If {@link E} is not an instance of {@code klass}
     * @see Spare#cast(Supplier, Object)
     */
    @Nullable
    default <E> E cast(
        @NotNull CharSequence klass,
        @NotNull Object data
    ) {
        Spare<E> spare = lookup(
            klass, true
        );

        if (spare == null) {
            return null;
        }

        return spare.cast(this, data);
    }

    /**
     * Parse {@link Kat} {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws ClassCastException   If {@link T} is not an instance of {@code klass}
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
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan write(
        @Nullable Object value
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
        @Nullable Object value, long flags
    ) throws IOException {
        Chan chan = new Chan(
            flags, this
        );
        chan.set(null, value);
        return chan;
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
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Doc mark(
        @Nullable Object value
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
        @Nullable Object value, long flags
    ) throws IOException {
        Doc chan = new Doc(
            flags, this
        );
        chan.set(null, value);
        return chan;
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
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Json serial(
        @Nullable Object value
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
        @Nullable Object value, long flags
    ) throws IOException {
        Json chan = new Json(
            flags, this
        );
        chan.set(null, value);
        return chan;
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
    default <T> T solve(
        @NotNull CharSequence klass,
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        Spare<T> spare = lookup(
            klass, true
        );

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
            Reflect.lookup(type, this);

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
     * Register an instance of {@link Coder}
     *
     * @param klass specify the type of embedding
     * @param coder specify the {@code spare} of {@link Class}
     * @return {@link Coder} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#register(Class, Coder)
     * @since 0.0.3
     */
    @Nullable
    Coder<?> register(
        @NotNull Class<?> klass,
        @NotNull Coder<?> coder
    );

    /**
     * Register the {@link Spare} of {@link Class} with {@link Embed}
     *
     * @param embed specify the {@link Embed}
     * @param klass specify the type of embedding
     * @param spare specify the {@code spare} of {@link Class}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @NotNull
    default <T> String register(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Spare<? super T> spare
    ) {
        embed(klass, spare);
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

        private Impl() {
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
        public Spare<?> revoke(
            @NotNull CharSequence klass
        ) {
            return remove(klass);
        }

        @Override
        public <T> Spare<T> lookup(
            Class<T> klass
        ) {
            return Cluster.INS.load(klass, this);
        }

        @Override
        public <T> Spare<T> lookup(
            CharSequence klass
        ) {
            return lookup(klass, false);
        }

        /**
         * auto search
         */
        static final boolean AUTO;

        static {
            AUTO = Config.get(
                "kat.supplier.search", true
            );
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Spare<T> lookup(
            CharSequence klass, boolean search
        ) {
            Spare<?> spare = get(klass);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            int i = klass.length();
            if (i < 2 || i > 191) {
                return null;
            }

            String name = klass.toString();
            for (Provider p : Cluster.PRO) {
                try {
                    spare = p.lookup(
                        name, this
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

            if (search && AUTO) try {
                ClassLoader loader = Thread
                    .currentThread()
                    .getContextClassLoader();

                if (loader == null) {
                    loader = ClassLoader.getSystemClassLoader();
                }

                spare = Cluster.INS.load(
                    Class.forName(name, false, loader), this
                );
                if (spare != null) {
                    putIfAbsent(
                        name, spare
                    );
                }
                return (Spare<T>) spare;
            } catch (Exception e) {
                // Nothing
            }

            return null;
        }

        /**
         * default cluster
         */
        static final Map<Class<?>, Coder<?>> PLUG;

        static {
            PLUG = new ConcurrentHashMap<>(
                Config.get(
                    "kat.coder.capacity", 16
                )
            );
            PLUG.put(ByteArrayCoder.class, ByteArrayCoder.INSTANCE);
        }

        @Override
        public Coder<?> register(
            Class<?> klass,
            Coder<?> coder
        ) {
            return PLUG.put(klass, coder);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Coder<T> activate(
            Class<?> klass
        ) {
            if (klass == Coder.class) {
                return null;
            }

            Coder<?> coder = PLUG.get(klass);

            if (coder != null) {
                return (Coder<T>) coder;
            }

            if (Coder.class.isAssignableFrom(klass)) {
                if (klass.isInterface()) {
                    return null;
                } else {
                    return Reflect.apply(klass);
                }
            }

            return (Coder<T>) Cluster.INS.load(klass, this);
        }

        @Override
        public Coder<?> deactivate(
            Class<?> klass
        ) {
            return PLUG.remove(klass);
        }
    }
}
