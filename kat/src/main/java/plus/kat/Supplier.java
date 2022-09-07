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
     * Register the {@link Spare} of {@code klass}
     * and returns the previous value associated with {@code klass}
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Spare<User> spare = ...
     *  supplier.embed(User.class, spare);
     * }</pre>
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
     * Removes the {@link Spare} cache for {@code klass}
     * and returns the previous value associated with {@code klass}
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  supplier.revoke(User.class);
     * }</pre>
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
     * <pre>{@code
     *  Supplier supplier = ...
     *  Spare<User> spare = ...
     *  supplier.embed(
     *      "plus.kat.entity.User", spare
     *  );
     * }</pre>
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
     * Removes the {@link Spare} cache for {@code klass}
     * and returns the previous value associated with {@code klass}
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  supplier.revoke(
     *      "plus.kat.entity.User"
     *  );
     * }</pre>
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
     * Returns the {@link Spare} of {@code klass}, if not cached first through
     * the custom {@link Provider} set and then through this {@link Supplier} final lookup
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Spare<User> spare = supplier.lookup(User.class);
     * }</pre>
     *
     * @param klass specify the type of lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#lookup(Class)
     * @see Spare#lookup(Class)
     */
    @Nullable <T>
    Spare<T> lookup(
        @NotNull Class<T> klass
    );

    /**
     * Returns the {@link Spare} of {@code klass}
     * and then you actively call {@link Spare#accept(Class)} to check.
     * If there is no cache, use a custom {@link Provider} set to look up.
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Spare<User> spare = supplier.lookup(
     *      "plus.kat.entity.User"
     *  );
     *
     *  // check for match
     *  boolean status = spare.accept(User.class); // status may be false
     * }</pre>
     *
     * @param klass specify the type of lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#lookup(CharSequence)
     */
    @Nullable <T>
    Spare<T> lookup(
        @NotNull CharSequence klass
    );

    /**
     * Returns the {@link Spare} of {@code klass}
     * and then you actively call {@link Spare#accept(Class)} to check.
     * <p>
     * If not cached first through the custom {@link Provider} set to look up,
     * if not, then use {@link Class#forName(String, boolean, ClassLoader)}
     * to find and judge whether it is a subclass of {@code parent} and then find its {@link Spare}.
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Spare<User> spare = supplier.lookup(
     *      "plus.kat.entity.User", User.class
     *  );
     *
     *  // check for match
     *  boolean status = spare.accept(User.class); // status may be false
     * }</pre>
     *
     * @param klass specify the type of lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Spare#accept(Class)
     * @see Impl#lookup(CharSequence, Class)
     * @since 0.0.3
     */
    @Nullable <K, T extends K>
    Spare<T> lookup(
        @NotNull CharSequence klass,
        @Nullable Class<K> parent
    );

    /**
     * Returns the {@link Coder} of {@code klass}.
     * if there is no cache, then judge whether {@code klass} is a subclass of {@link Coder},
     * if so, instantiate and return it, otherwise point to find the {@link Spare} of {@code klass}
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Coder<User> coder0 = supplier.activate(User.class);
     *  Coder<User> coder1 = supplier.activate(UserCoder.class);
     * }</pre>
     *
     * @param klass specify the type of apply
     * @return {@link Coder} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#activate(Class)
     */
    @Nullable <T>
    Coder<T> activate(
        @NotNull Class<?> klass
    );

    /**
     * Deactivates the {@link Coder} cache for {@code klass}
     * and returns the previous value associated with {@code klass}
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  supplier.deactivate(User.class);
     *  supplier.deactivate(UserCoder.class);
     * }</pre>
     *
     * @param klass specify the type of revoking
     * @return {@link Coder} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     * @see Impl#deactivate(Class)
     */
    @Nullable
    Coder<?> deactivate(
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
     * If {@link E} is a Bean or spoiler has elements,
     * then perform a given {@link Spoiler} to create a {@link E}
     *
     * @return {@link E}, it is not null
     * @throws Collapse             If it fails to create
     * @throws NullPointerException If the klass or spoiler is null
     * @see Spare#apply(Spoiler, Supplier)
     * @since 0.0.4
     */
    @NotNull
    default <E> E apply(
        @NotNull Class<E> klass,
        @NotNull Spoiler spoiler
    ) throws Collapse {
        Spare<E> spare = lookup(klass);

        if (spare == null) {
            throw new Collapse(
                "No spare of " + klass
            );
        }

        return spare.apply(spoiler, this);
    }

    /**
     * If {@link E} is a Bean or resultSet has elements,
     * then perform a given {@link ResultSet} to create a {@link E}
     *
     * @return {@link E}, it is not null
     * @throws SQLCrash             If it fails to create
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the klass or result is null
     * @see Spare#apply(Supplier, ResultSet)
     * @since 0.0.3
     */
    @NotNull
    default <E> E apply(
        @NotNull Class<E> klass,
        @NotNull ResultSet result
    ) throws SQLException {
        Spare<E> spare = lookup(klass);

        if (spare == null) {
            throw new SQLCrash(
                "No spare of " + klass
            );
        }

        return spare.apply(this, result);
    }

    /**
     * Convert the {@link Object} to {@code K}
     *
     * @param data specify the {@code data} to convert
     * @return {@link E} or {@code null}
     * @see Spare#cast(Object, Supplier)
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

        return spare.cast(data, this);
    }

    /**
     * Convert the {@link Object} to {@code K}
     *
     * @param data specify the {@code data} to convert
     * @return {@link E} or {@code null}
     * @throws ClassCastException If {@link E} is not an instance of {@code klass}
     * @see Spare#cast(Object, Supplier)
     */
    @Nullable
    default <E> E cast(
        @NotNull CharSequence klass,
        @NotNull Object data
    ) {
        Spare<E> spare = lookup(
            klass, Object.class
        );

        if (spare == null) {
            return null;
        }

        return spare.cast(data, this);
    }

    /**
     * If {@link K} is a Bean, then returns
     * a spoiler over all elements of the {@code bean}
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Spoiler spoiler = supplier.flat(user);
     *
     *  while (spoiler.hasNext()) {
     *      String key = spoiler.getKey();
     *      Object val = spoiler.getValue();
     *  }
     * }</pre>
     *
     * @return {@link Spoiler} or {@code null}
     * @throws NullPointerException If the parameters contains null
     * @since 0.0.3
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <K> Spoiler flat(
        @NotNull K bean
    ) {
        Class<K> klass = (Class<K>)
            bean.getClass();
        Spare<K> spare = lookup(klass);

        if (spare == null) {
            return null;
        }

        return spare.flat(bean);
    }

    /**
     * If {@link K} is a Bean, then perform a given
     * visitor in each item until all entries are processed
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Map<String, Object> collector = ...
     *
     *  User user = ...
     *  supplier.flat(
     *    user, collector::put
     *  );
     *
     *  int id = (int) collector.get("id");
     *  String name = (String) collector.get("name");
     * }</pre>
     *
     * @return {@code true} if the bean can be flattened otherwise {@code false}
     * @throws NullPointerException If the parameters contains null
     * @see Spare#flat(Object, Visitor)
     * @since 0.0.3
     */
    @SuppressWarnings("unchecked")
    default <K> boolean flat(
        @NotNull K bean,
        @NotNull Visitor visitor
    ) {
        Class<K> klass = (Class<K>)
            bean.getClass();
        Spare<K> spare = lookup(klass);

        if (spare == null) {
            return false;
        }

        return spare.flat(bean, visitor);
    }

    /**
     * Copy the property values of the specified spoiler into the given specified bean
     *
     * <pre>{@code
     *  Spoiler spoiler = ...
     *  Supplier supplier = ...
     *
     *  User user = new User();
     *  supplier.update(user, spoiler);
     * }</pre>
     *
     * @return {@code true} if successful update
     * @throws NullPointerException If the parameters contains null
     * @since 0.0.4
     */
    @SuppressWarnings("unchecked")
    default <K> boolean update(
        @NotNull K bean,
        @NotNull Spoiler spoiler
    ) {
        Class<K> klass = (Class<K>)
            bean.getClass();
        Spare<K> spare = lookup(klass);

        if (spare == null) {
            return false;
        }

        return spare.update(bean, spoiler, this) != 0;
    }

    /**
     * Copy the property values of the specified spoiler into the given specified bean
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  ResultSet resultSet = ...
     *
     *  User user = new User();
     *  supplier.update(user, resultSet);
     * }</pre>
     *
     * @return {@code true} if successful update
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the parameters contains null
     * @since 0.0.4
     */
    @SuppressWarnings("unchecked")
    default <K> boolean update(
        @NotNull K bean,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        Class<K> klass = (Class<K>)
            bean.getClass();
        Spare<K> spare = lookup(klass);

        if (spare == null) {
            return false;
        }

        return spare.update(bean, this, resultSet) != 0;
    }

    /**
     * Copy the property values of the given source bean into the given target bean
     *
     * <pre>{@code
     *  Object source = ...
     *  Object target = ...
     *
     *  Supplier supplier = ...
     *  supplier.migrate(source, target);
     * }</pre>
     *
     * @return {@code true} if successful update
     * @throws NullPointerException If the parameters contains null
     * @see Spare#update(Object, Spoiler, Supplier)
     * @since 0.0.4
     */
    @SuppressWarnings("unchecked")
    default <S, T> boolean migrate(
        @NotNull S source,
        @NotNull T target
    ) {
        Spare<S> spare0 = lookup(
            (Class<S>) source.getClass()
        );
        if (spare0 == null) {
            return false;
        }

        Spoiler spoiler = spare0.flat(source);
        if (spoiler == null) {
            return false;
        }

        Spare<T> spare = lookup(
            (Class<T>) target.getClass()
        );

        return spare != null && spare.update(target, spoiler, this) != 0;
    }

    /**
     * Parse {@link Kat} {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws ClassCastException   If {@link T} is not an instance of {@code klass}
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(CharSequence, Job, Event)
     */
    @NotNull
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
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(Class, Job, Event)
     */
    @NotNull
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
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(CharSequence, Job, Event)
     */
    @NotNull
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
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(Class, Job, Event)
     */
    @NotNull
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
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code klass} {@code event} is null
     * @see Supplier#solve(CharSequence, Job, Event)
     */
    @NotNull
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
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code klass} {@code event} is null
     * @see Supplier#solve(Class, Job, Event)
     */
    @NotNull
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
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Spare#solve(Job, Event)
     * @since 0.0.2
     */
    @NotNull
    default <T> T solve(
        @NotNull CharSequence klass,
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        Spare<T> spare = lookup(
            klass, Object.class
        );

        if (spare == null) {
            throw new Collapse(
                "No spare of " + klass
            );
        }

        event.with(this);
        return spare.solve(job, event);
    }

    /**
     * Parse {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Spare#solve(Job, Event)
     * @since 0.0.2
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <T> T solve(
        @NotNull Type type,
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        Spare<T> spare = (Spare<T>)
            Reflect.lookup(type, this);

        if (spare == null) {
            throw new Collapse(
                "No spare of " + type
            );
        }

        event.with(this);
        event.prepare(type);

        return spare.solve(job, event);
    }

    /**
     * Parse {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Spare#solve(Job, Event)
     * @since 0.0.2
     */
    @NotNull
    default <E, T extends E> T solve(
        @NotNull Class<E> klass,
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        Spare<E> spare = lookup(klass);

        if (spare == null) {
            throw new Collapse(
                "No spare of " + klass
            );
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

        if ((embed.mode() & Embed.HIDDEN) == 0) {
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
            INS.put($E, ErrorSpare.INSTANCE);
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
            return lookup(klass, null);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <K, T extends K> Spare<T> lookup(
            CharSequence klass, Class<K> parent
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
                        parent, name, this
                    );
                } catch (Collapse e) {
                    return null;
                } catch (Exception e) {
                    continue;
                }

                if (spare != null) {
                    return (Spare<T>) spare;
                }
            }

            if (parent != null) {
                ClassLoader loader = Thread
                    .currentThread()
                    .getContextClassLoader();

                if (loader == null) {
                    loader = ClassLoader.getSystemClassLoader();
                }

                Class<?> child;
                try {
                    child = Class.forName(
                        name, false, loader
                    );
                } catch (LinkageError |
                    ClassNotFoundException e) {
                    return null;
                }

                if (parent.isAssignableFrom(child)) {
                    spare = Cluster.INS.load(
                        child, this
                    );
                    if (spare != null) {
                        putIfAbsent(
                            name, spare
                        );
                    }
                    return (Spare<T>) spare;
                }
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
