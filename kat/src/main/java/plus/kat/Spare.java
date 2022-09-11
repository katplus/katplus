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
import plus.kat.entity.*;
import plus.kat.spare.*;
import plus.kat.reflex.*;
import plus.kat.utils.*;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static plus.kat.Plan.DEF;
import static plus.kat.Supplier.Impl;
import static plus.kat.spare.Parser.Group;

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
     * If {@link K} is a Bean or spoiler has elements,
     * then perform a given {@link Spoiler} to create a {@link K}
     *
     * @param spoiler the specified spoiler to be used
     * @return {@link K}, it is not null
     * @throws Collapse             If it fails to create
     * @throws NullPointerException If the spoiler is null
     * @see Spare#apply(Spoiler, Supplier)
     * @see Workman#apply(Spoiler, Supplier)
     * @see Property#apply(Spoiler, Supplier)
     * @since 0.0.4
     */
    @NotNull
    default K apply(
        @NotNull Spoiler spoiler
    ) throws Collapse {
        return apply(
            spoiler, Impl.INS
        );
    }

    /**
     * If {@link K} is a Bean or spoiler has elements,
     * then perform a given {@link Spoiler} to create a {@link K}
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  Supplier supplier = ...;
     *
     *  User spare = spare.apply(
     *     supplier.flat(user)
     *  );
     * }</pre>
     *
     * @param spoiler  the specified spoiler
     * @param supplier the specified supplier
     * @return {@link K}, it is not null
     * @throws Collapse             If it fails to create
     * @throws NullPointerException If the supplier or spoiler is null
     * @see Workman#apply(Spoiler, Supplier)
     * @since 0.0.4
     */
    @NotNull
    default K apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) throws Collapse {
        throw new Collapse(
            "Unexpectedly, '" + getType() + "' not a Bean"
        );
    }

    /**
     * If {@link K} is a Bean or resultSet has elements,
     * then perform a given {@link ResultSet} to create a {@link K}
     *
     * @param result the specified result to be used
     * @return {@link K}, it is not null
     * @throws SQLCrash             If it fails to create
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the result is null
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
     * If {@link K} is a Bean or resultSet has elements,
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
     * @param supplier  the specified supplier
     * @param resultSet the specified resultSet to be used
     * @return {@link K}, it is not null
     * @throws SQLCrash             If it fails to create
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the supplier or resultSet is null
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
     * Returns the {@link Target}
     * of the specified param name
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  User user = ...
     *  Target tag = spare.tag("name");
     * }</pre>
     *
     * @param key the param name of the bean
     * @return {@link Target} or {@code null}
     * @throws NullPointerException If the key is null
     * @since 0.0.4
     */
    @Nullable
    default Target tag(
        @NotNull Object key
    ) {
        return null;
    }

    /**
     * Returns the {@link Setter}
     * of the specified property name
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  User user = ...
     *  spare.set("name").call(user, "kraity");
     * }</pre>
     *
     * @param key the property name of the bean
     * @return {@link Setter} or {@code null}
     * @throws NullPointerException If the key is null
     * @since 0.0.4
     */
    @Nullable
    default Setter<K, ?> set(
        @NotNull Object key
    ) {
        return null;
    }

    /**
     * Returns the {@link Getter}
     * of the specified property name
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  User user = ...
     *  Object name = spare.get("name").call(user);
     * }</pre>
     *
     * @param key the property name of the bean
     * @return {@link Getter} or {@code null}
     * @throws NullPointerException If the key is null
     * @since 0.0.4
     */
    @Nullable
    default Getter<K, ?> get(
        @NotNull Object key
    ) {
        return null;
    }

    /**
     * If {@link K} is a Bean, then returns
     * a spoiler over all elements of the {@code bean}
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  Spoiler spoiler = spare.flat(user);
     *
     *  while (spoiler.hasNext()) {
     *      String key = spoiler.getKey();
     *      Object val = spoiler.getValue();
     *  }
     * }</pre>
     *
     * @return {@link Spoiler} or {@code null}
     * @throws NullPointerException If the {@code bean} is null
     * @since 0.0.3
     */
    @Nullable
    default Spoiler flat(
        @NotNull K bean
    ) {
        return null;
    }

    /**
     * If {@link K} is a Bean, then perform a given
     * visitor in each item until all entries are processed
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
     * @return {@code true} if the bean can be flattened otherwise {@code false}
     * @throws NullPointerException If the {@code bean} or {@code visitor} is null
     * @since 0.0.3
     */
    default boolean flat(
        @NotNull K bean,
        @NotNull Visitor visitor
    ) {
        return false;
    }

    /**
     * Parse {@link Kat} {@link CharSequence} and convert result to {@link K}
     *
     * @param text specify the {@code text} to be parsed
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code text} is null
     */
    @NotNull
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
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     */
    @NotNull
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
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code text} is null
     */
    @NotNull
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
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     */
    @NotNull
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
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code text} is null
     */
    @NotNull
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
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     */
    @NotNull
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
     * @return {@link K} or {@code null}
     * @see Spare#cast(Object, Supplier)
     */
    @Nullable
    default K cast(
        @Nullable Object data
    ) {
        return cast(
            data, Impl.INS
        );
    }

    /**
     * Convert the {@link Object} to {@code K}
     *
     * @param supplier the specified {@code supplier}
     * @param data     specify the {@code data} to convert
     * @return {@link K} or {@code null}
     */
    @Nullable
    default K cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, null, supplier
            );
        }
        return null;
    }

    /**
     * Copy the property values of the specified spoiler into the given specified bean
     *
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     * @see Spare#update(Object, Spoiler, Supplier)
     * @since 0.0.4
     */
    default int update(
        @NotNull K entity,
        @NotNull Spoiler spoiler
    ) {
        return update(
            entity, spoiler, Impl.INS
        );
    }

    /**
     * Copy the property values of the specified spoiler into the given specified bean
     *
     * <pre>{@code
     *  Object source = ...
     *  Supplier supplier = ...
     *
     *  Spare<User> spare = ...
     *  Spoiler spoiler = supplier.flat(source);
     *
     *  User user = new User();
     *  spare.update(user, spoiler);
     * }</pre>
     *
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     * @since 0.0.4
     */
    default int update(
        @NotNull K entity,
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) {
        int rows = 0;
        while (spoiler.hasNext()) {
            String key = spoiler.getKey();
            Setter<K, ?> setter = set(key);
            if (setter == null) {
                continue;
            }

            Object val = spoiler.getValue();
            if (val == null) {
                continue;
            }

            Class<?> klass = setter.getType();
            if (klass.isInstance(val)) {
                rows++;
                setter.call(
                    entity, val
                );
                continue;
            }

            Spare<?> spare = supplier.lookup(klass);
            if (spare != null) {
                rows++;
                setter.call(
                    entity, spare.cast(
                        val, supplier
                    )
                );
            }
        }

        return rows;
    }

    /**
     * Copy the property values of the specified spoiler into the given specified group
     *
     * <pre>{@code
     *  Object source = ...
     *  Supplier supplier = ...
     *
     *  Spare<User> spare = ...
     *  Spoiler spoiler = supplier.flat(source);
     *
     *  Object[] group = new Object[2];
     *  spare.update(group, spoiler, supplier);
     *
     *  User user = new User(
     *    (int) group[0], (String) group[1]
     *  );
     * }</pre>
     *
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     * @see Spare#update(Object, Spoiler, Supplier)
     * @since 0.0.4
     */
    default int update(
        @NotNull Object[] group,
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) {
        int rows = 0;
        while (spoiler.hasNext()) {
            String key = spoiler.getKey();
            Target target = tag(key);
            if (target == null) {
                continue;
            }

            int k = target.getIndex();
            if (k < 0 || k >= group.length) {
                throw new Collapse(
                    "'" + k + "' out of range"
                );
            }

            Object val = spoiler.getValue();
            if (val == null) {
                continue;
            }

            Class<?> klass = target.getType();
            if (klass.isInstance(val)) {
                rows++;
                group[k] = val;
                continue;
            }

            Spare<?> spare = supplier.lookup(klass);
            if (spare != null) {
                rows++;
                group[k] = spare.cast(
                    val, supplier
                );
            }
        }

        return rows;
    }

    /**
     * Copy the property values of the specified resultSet into the given specified bean
     *
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     * @see Spare#update(Object, Supplier, ResultSet)
     * @since 0.0.4
     */
    default int update(
        @NotNull K entity,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return update(
            entity, Impl.INS, resultSet
        );
    }

    /**
     * Copy the property values of the specified resultSet into the given specified bean
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *
     *  ResultSet rs = stmt.executeQuery(sql);
     *  List<User> users = new ArrayList<>();
     *
     *  while (rs.next()) {
     *    User user = new User();
     *    users.add(user);
     *    spare.update(user, rs);
     *  }
     * }</pre>
     *
     * @return the number of rows affected
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the parameters contains null
     * @since 0.0.4
     */
    default int update(
        @NotNull K entity,
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        ResultSetMetaData meta =
            resultSet.getMetaData();
        int rows = 0;
        int count = meta.getColumnCount();

        for (int i = 1; i <= count; i++) {
            String key = meta.getColumnLabel(i);
            if (key == null) {
                key = meta.getColumnName(i);
            }

            Setter<K, ?> setter = set(key);
            if (setter == null) {
                throw new SQLCrash(
                    "Can't find the Setter of " + key
                );
            }

            Object val = resultSet.getObject(i);
            if (val == null) {
                continue;
            }

            Class<?> klass = setter.getType();
            if (klass.isInstance(val)) {
                rows++;
                setter.call(entity, val);
                continue;
            }

            Spare<?> spare = supplier.lookup(klass);
            if (spare != null) {
                Object var = spare.cast(
                    val, supplier
                );
                if (var != null) {
                    rows++;
                    setter.call(entity, var);
                    continue;
                }
            }

            throw new SQLCrash(
                "Cannot convert the type of " + key
                    + " from " + val.getClass() + " to " + klass
            );
        }

        return rows;
    }

    /**
     * Copy the property values of the specified spoiler into the given specified group
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  Supplier supplier = ...
     *
     *  ResultSet rs = stmt.executeQuery(sql);
     *  List<User> users = new ArrayList<>();
     *
     *  while (rs.next()) {
     *    Object[] group = new Object[2];
     *    spare.update(group, supplier, rs);
     *    users.add(
     *      new User(
     *        (int) group[0], (String) group[1]
     *      )
     *    );
     *  }
     * }</pre>
     *
     * @return the number of rows affected
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the parameters contains null
     * @see Spare#update(Object, Supplier, ResultSet)
     * @since 0.0.4
     */
    default int update(
        @NotNull Object[] group,
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        ResultSetMetaData meta =
            resultSet.getMetaData();
        int rows = 0;
        int count = meta.getColumnCount();

        for (int i = 1; i <= count; i++) {
            String key = meta.getColumnLabel(i);
            if (key == null) {
                key = meta.getColumnName(i);
            }

            Target target = tag(key);
            if (target == null) {
                throw new SQLCrash(
                    "Can't find the Target of " + key
                );
            }

            int k = target.getIndex();
            if (k < 0 || k >= group.length) {
                throw new SQLCrash(
                    "'" + k + "' out of range"
                );
            }

            Object val = resultSet.getObject(i);
            if (val == null) {
                continue;
            }

            Class<?> klass = target.getType();
            if (klass.isInstance(val)) {
                rows++;
                group[k] = val;
                continue;
            }

            Spare<?> spare = supplier.lookup(klass);
            if (spare != null) {
                Object var = spare.cast(
                    val, supplier
                );
                if (var != null) {
                    rows++;
                    group[k] = var;
                    continue;
                }
            }

            throw new SQLCrash(
                "Cannot convert the type of " + key
                    + " from " + val.getClass() + " to " + klass
            );
        }

        return rows;
    }

    /**
     * Parse {@link Event} and convert result to {@link K}
     *
     * @param event specify the {@code event} to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     * @since 0.0.2
     */
    @NotNull
    default <T extends K> T solve(
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        // parser pool
        Group group = Group.INS;

        // borrow parser
        Parser parser = group.borrow();

        try {
            event.with(this);
            return parser.read(
                job, event
            );
        } catch (Collapse error) {
            throw error;
        } catch (Exception error) {
            throw new Collapse(
                "Failed to solve " + job, error
            );
        } finally {
            // returns parser
            group.retreat(parser);
        }
    }

    /**
     * Register the {@link Spare} of {@code klass}
     * and returns the previous value associated with {@code klass}
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  Spare.embed(User.class, spare);
     * }</pre>
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
     * Removes the {@link Spare} cache for {@code klass}
     * and returns the previous value associated with {@code klass}
     *
     * <pre>{@code
     *  Spare.revoke(User.class);
     * }</pre>
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
     * Returns the {@link Spare} of {@code klass}, if not cached first through
     * the custom {@link Provider} set and then through default {@link Supplier} final lookup
     *
     * <pre>{@code
     *  Spare<User> spare = Spare.lookup(User.class);
     * }</pre>
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
            INS.put(Map.class, MapSpare.INSTANCE);
            INS.put(Set.class, SetSpare.INSTANCE);
            INS.put(List.class, ListSpare.INSTANCE);
            INS.put(void.class, VoidSpare.INSTANCE);
            INS.put(Void.class, VoidSpare.INSTANCE);
            INS.put(CharSequence.class, StringSpare.INSTANCE);
            INS.put(BigInteger.class, BigIntegerSpare.INSTANCE);
            INS.put(BigDecimal.class, BigDecimalSpare.INSTANCE);
            INS.put(StringBuffer.class, StringBufferSpare.INSTANCE);
            INS.put(StringBuilder.class, StringBuilderSpare.INSTANCE);
        }

        /**
         * default providers
         */
        static final Provider[] PRO;

        static {
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
                PRO = new Provider[size];

                int i = 0;
                while (loader.hasNext()) {
                    PRO[i++] = loader.next();
                }
            } catch (Exception e) {
                throw new Error(
                    "Unexpectedly, cannot be loaded", e
                );
            }
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

            for (Provider p : Cluster.PRO) {
                try {
                    spare = p.lookup(
                        klass, supplier
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

            return (Spare<T>) lookup(
                klass, supplier
            );
        }

        /**
         * Returns {@link Spare} of the specified {@code klass}
         *
         * @throws Collapse             The Provider signals to interrupt subsequent lookup
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

            if (embed == null) {
                if (klass.isInterface() ||
                    Kat.class.isAssignableFrom(klass) ||
                    Coder.class.isAssignableFrom(klass)) {
                    return null;
                }
            } else {
                Class<?> clazz = embed.with();
                if (clazz != Spare.class) {
                    // spare of klass
                    Spare<?> spare = null;

                    if (!Spare.class.
                        isAssignableFrom(clazz)) {
                        spare = load(
                            clazz, supplier
                        );
                        if (spare != null) {
                            putIfAbsent(
                                klass, spare
                            );
                        }
                    } else try {
                        // double-checking
                        spare = get(klass);

                        // check for cache
                        if (spare != null ||
                            clazz.isInterface()) {
                            return spare;
                        }

                        Constructor<?>[] cs = clazz
                            .getDeclaredConstructors();
                        Constructor<?> d, c = cs[0];
                        for (int i = 1; i < cs.length; i++) {
                            d = cs[i];
                            if (c.getParameterCount() <=
                                d.getParameterCount()) c = d;
                        }

                        int size = c.getParameterCount();
                        Object[] args = new Object[size];
                        if (size != 0) {
                            Class<?>[] cls =
                                c.getParameterTypes();
                            for (int i = 0; i < size; i++) {
                                Class<?> m = cls[i];
                                if (m == Class.class) {
                                    args[i] = klass;
                                } else if (m == Embed.class) {
                                    args[i] = embed;
                                } else if (m == Provider.class) {
                                    args[i] = this;
                                } else if (m == Supplier.class) {
                                    args[i] = supplier;
                                } else if (m.isAnnotation()) {
                                    args[i] = klass.getAnnotation(
                                        (Class<? extends Annotation>) m
                                    );
                                }
                            }
                        }

                        c.setAccessible(true);
                        putIfAbsent(klass, spare =
                            (Spare<?>) c.newInstance(args)
                        );
                    } catch (Exception e) {
                        // Nothing
                    }
                    return spare;
                }

                if (klass.isInterface()) {
                    return new ProxySpare(
                        embed, klass, supplier, this
                    );
                }
            }

            try {
                Class<?> sc = klass.getSuperclass();
                if (sc == Enum.class) {
                    return new EnumSpare(
                        embed, klass, supplier, this
                    );
                }

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
                                spare = MapSpare.of(klass);
                            } else if (Set.class.isAssignableFrom(klass)) {
                                spare = SetSpare.of(klass);
                            } else if (List.class.isAssignableFrom(klass)) {
                                spare = ListSpare.of(klass);
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
                                spare = MapSpare.of(klass);
                            } else if (Set.class.isAssignableFrom(klass)) {
                                spare = SetSpare.of(klass);
                            } else if (List.class.isAssignableFrom(klass)) {
                                spare = ListSpare.of(klass);
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
