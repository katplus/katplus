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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.spare.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.stream.*;

import java.sql.*;
import java.io.IOException;
import java.lang.reflect.Type;

import static plus.kat.Plan.DEF;
import static plus.kat.Supplier.Impl.INS;
import static plus.kat.utils.Find.clazz;
import static plus.kat.spare.Parser.Group;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public interface Spare<K> extends Coder<K> {
    /**
     * Returns the space of {@link K}.
     * Generally, it is class name, or custom name
     */
    @NotNull
    CharSequence getSpace();

    /**
     * Returns the flag of {@link K}
     *
     * <pre>{@code
     *  null  ->  the K is a attribute
     *  true  ->  the K is a bean object
     *  false ->  the K is a list or array
     * }</pre>
     */
    @Nullable
    Boolean getFlag();

    /**
     * Check if {@code clazz} is a parent Class of {@link K}
     * or this {@link Spare} can create an instance of {@code clazz}
     *
     * @param clazz the specified clazz to be compared
     * @throws NullPointerException If the specified {@code clazz} is null
     */
    boolean accept(
        @NotNull Class<?> clazz
    );

    /**
     * Returns the supplier of {@link K}
     */
    @NotNull
    Supplier getSupplier();

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
     * If this {@link Spare} can create an instance,
     * it returns it, otherwise it will return {@code null}
     *
     * @return {@link K} or {@code null}
     * @throws Collapse If a build error occurs
     * @since 0.0.3
     */
    @Nullable
    default K apply() {
        return null;
    }

    /**
     * If this {@link Spare} can create an instance of the actual
     * subclass type, it returns it, otherwise it will throw collapse
     *
     * @param type the specified actual subclass type
     * @return {@link K} or throws collapse
     * @throws Collapse If failed to build an instance of a subclass
     *                  or the {@code type} is not a subclass of {@link K}
     * @since 0.0.4
     */
    @NotNull
    default K apply(
        @NotNull Type type
    ) {
        Class<?> klass = getType();
        if (type == klass) {
            // default value
            K it = apply();

            // check for null
            if (it != null) {
                return it;
            }

            throw new Collapse(
                "Failed to create"
            );
        }

        // Find the class of the type
        Class<?> clazz = clazz(type);
        if (clazz == null) {
            throw new Collapse(
                "Can't find class of " + type
            );
        }

        // Check if subclass of the kind
        if (klass.isAssignableFrom(clazz)) {
            // Using this spare's Supplier
            Supplier supplier = getSupplier();

            // Find the spare of the subclass
            Spare<K> spare = supplier.lookup(
                (Class<K>) clazz
            );

            if (spare != null &&
                spare != this) {
                return spare.apply(type);
            }
        }

        throw new Collapse(
            "Unable to create an instance of " + type
        );
    }

    /**
     * If this spare can be built with spiller,
     * then perform a given {@link Spoiler} to create a {@link K}
     *
     * @param spoiler the specified spoiler to be used
     * @return {@link K}, it is not null
     * @throws Collapse             If a build error occurs
     * @throws NullPointerException If the specified spoiler is null
     * @see Spare#apply(Spoiler, Supplier)
     * @see Property#apply(Spoiler, Supplier)
     * @see AbstractSpare#apply(Spoiler, Supplier)
     * @since 0.0.4
     */
    @NotNull
    default K apply(
        @NotNull Spoiler spoiler
    ) throws Collapse {
        return apply(
            spoiler, getSupplier()
        );
    }

    /**
     * If this spare can be built with spiller,
     * then perform a given {@link Spoiler} to create a {@link K}
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  Supplier supplier = ...;
     *
     *  User user = spare.apply(
     *     supplier.flat(bean)
     *  );
     * }</pre>
     *
     * @param spoiler  the specified spoiler
     * @param supplier the specified supplier
     * @return {@link K}, it is not null
     * @throws Collapse             If a build error occurs
     * @throws NullPointerException If the specified supplier or specified spoiler is null
     * @see AbstractSpare#apply(Spoiler, Supplier)
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
     * If this spare can be built with resultSet,
     * then perform a given {@link ResultSet} to create a {@link K}
     *
     * @param resultSet the specified result to be used
     * @return {@link K}, it is not null
     * @throws SQLCrash             If a build error occurs
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the specified resultSet is null
     * @see Spare#apply(Supplier, ResultSet)
     * @see Property#apply(Supplier, ResultSet)
     * @see AbstractSpare#apply(Supplier, ResultSet)
     * @since 0.0.3
     */
    @NotNull
    default K apply(
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return apply(
            getSupplier(), resultSet
        );
    }

    /**
     * If this spare can be built with resultSet,
     * then perform a given {@link ResultSet} to create a {@link K}
     *
     * <pre>{@code
     *  ResultSet rs = stmt.executeQuery(sql);
     *  List<User> users = new ArrayList<>();
     *
     *  Spare<User> spare = ...
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
     * @throws SQLCrash             If a build error occurs
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the supplier or resultSet is null
     * @see AbstractSpare#apply(Supplier, ResultSet)
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
     * Register to the specified supplier
     *
     * <pre>{@code
     *   supplier.embed(getType(), this);
     *   supplier.embed("plus.kat.entity.User", this);
     *
     *   String[] spaces = ...
     *   for (String space : spaces) {
     *      if (space.indexOf('.', 1) != -1) {
     *         supplier.embed(space, this);
     *      }
     *   }
     * }</pre>
     *
     * @param supplier the specified supplier
     * @see AbstractSpare#embed(Supplier)
     * @since 0.0.4
     */
    default void embed(
        @NotNull Supplier supplier
    ) {
        supplier.embed(
            getType(), this
        );
    }

    /**
     * Returns the {@link Setter}
     * of the specified property name
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  User user = ...
     *  spare.set("name").invoke(user, "kraity");
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
     *  Object name = spare.get("name").invoke(user);
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
     * Resolve the Kat {@code text} and convert the result to {@link K}
     *
     * <pre>{@code
     *   Spare<User> spare = ...
     *   String text = ...
     *   User user = spare.read(text);
     * }</pre>
     *
     * @param text the specified text to be parsed
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
     * Resolve the Kat {@link Event} and convert the result to {@link K}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Spare<User> spare = ...
     *
     *   User user = spare.read(
     *      event.with(
     *         Flag.STRING_AS_OBJECT
     *      )
     *   );
     * }</pre>
     *
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     */
    @NotNull
    default <T extends K> T read(
        @NotNull Event<T> event
    ) {
        return solve(
            Algo.KAT, event
        );
    }

    /**
     * Serialize to {@link Chan}
     *
     * <pre>{@code
     *   User user = ...
     *   Spare<User> spare = ...
     *
     *   try (Chan chan = spare.write(user)) {
     *       byte[] bs = chan.toBytes();
     *       String st = chan.toString();
     *   }
     * }</pre>
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
     * <pre>{@code
     *   User user = ...
     *   Spare<User> spare = ...
     *
     *   try (Chan chan = spare.write(user, Flag.UNICODE)) {
     *       byte[] bs = chan.toBytes();
     *       String st = chan.toString();
     *   }
     * }</pre>
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
     * Resolve the Doc {@code text} and convert the result to {@link K}
     *
     * <pre>{@code
     *   Spare<User> spare = ...
     *   String text = ...
     *   User user = spare.down(text);
     * }</pre>
     *
     * @param text the specified text to be parsed
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
     * Resolve the Doc {@link Event} and convert the result to {@link K}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Spare<User> spare = ...
     *
     *   User user = spare.down(
     *      event.with(
     *         Flag.STRING_AS_OBJECT
     *      )
     *   );
     * }</pre>
     *
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     */
    @NotNull
    default <T extends K> T down(
        @NotNull Event<T> event
    ) {
        return solve(
            Algo.DOC, event
        );
    }

    /**
     * Serialize to {@link Doc}
     *
     * <pre>{@code
     *   User user = ...
     *   Spare<User> spare = ...
     *
     *   try (Doc doc = spare.mark(user)) {
     *       byte[] bs = doc.toBytes();
     *       String st = doc.toString();
     *   }
     * }</pre>
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
     * <pre>{@code
     *   User user = ...
     *   Spare<User> spare = ...
     *
     *   try (Doc doc = spare.mark(user, Flag.UNICODE)) {
     *       byte[] bs = doc.toBytes();
     *       String st = doc.toString();
     *   }
     * }</pre>
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
     * Resolve the Json {@code text} and convert the result to {@link K}
     *
     * <pre>{@code
     *   Spare<User> spare = ...
     *   String text = ...
     *   User user = spare.parse(text);
     * }</pre>
     *
     * @param text the specified text to be parsed
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
     * Resolve the Json {@link Event} and convert the result to {@link K}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Spare<User> spare = ...
     *
     *   User user = spare.parse(
     *      event.with(
     *         Flag.STRING_AS_OBJECT
     *      )
     *   );
     * }</pre>
     *
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     */
    @NotNull
    default <T extends K> T parse(
        @NotNull Event<T> event
    ) {
        return solve(
            Algo.JSON, event
        );
    }

    /**
     * Serialize to {@link Json}
     *
     * <pre>{@code
     *   User user = ...
     *   Spare<User> spare = ...
     *
     *   try (Json json = spare.serial(user)) {
     *       byte[] bs = json.toBytes();
     *       String st = json.toString();
     *   }
     * }</pre>
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
     * <pre>{@code
     *   User user = ...
     *   Spare<User> spare = ...
     *
     *   try (Json json = spare.serial(user, Flag.UNICODE)) {
     *       byte[] bs = json.toBytes();
     *       String st = json.toString();
     *   }
     * }</pre>
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
     * <pre>{@code
     *  Spare<User> spare = ...
     *  User user = spare.cast(
     *      "{:id(1):name(kraity)}"
     *  );
     *  User user = spare.cast(
     *      Map.of("id", 1, "name", "kraity")
     *  );
     * }</pre>
     *
     * @param data the specified data to be to converted
     * @return {@link K} or {@code null}
     * @see Spare#cast(Object, Supplier)
     */
    @Nullable
    default K cast(
        @Nullable Object data
    ) {
        return cast(
            data, getSupplier()
        );
    }

    /**
     * Convert the {@link Object} to {@code K}
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  Supplier supplier = ...
     *
     *  User user = spare.cast(
     *      "{:id(1):name(kraity)}", supplier
     *  );
     *  User user = spare.cast(
     *      Map.of("id", 1, "name", "kraity"), supplier
     *  );
     * }</pre>
     *
     * @param supplier the specified supplier
     * @param data     the specified data to be to converted
     * @return {@link K} or {@code null}
     */
    @Nullable
    default K cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data == null) {
            return apply();
        }

        if (getType().isInstance(data)) {
            return (K) data;
        }

        if (data instanceof CharSequence) {
            return Convert.toObject(
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
            entity, spoiler, getSupplier()
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
     * @see Subject#update(Object, Spoiler, Supplier)
     * @since 0.0.4
     */
    default int update(
        @NotNull K entity,
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) {
        return 0;
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
            entity, getSupplier(), resultSet
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
     * @see Subject#update(Object, Supplier, ResultSet)
     * @since 0.0.4
     */
    default int update(
        @NotNull K entity,
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return 0;
    }

    /**
     * Parse {@link Event} and convert result to {@link K}
     *
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     * @since 0.0.2
     */
    @NotNull
    default <T extends K> T solve(
        @NotNull Algo algo,
        @NotNull Event<T> event
    ) {
        // parser pool
        Group group = Group.INS;

        // borrow parser
        Parser parser = group.borrow();

        try {
            event.with(this);
            return parser.read(
                algo, event
            );
        } catch (Collapse error) {
            throw error;
        } catch (Exception error) {
            throw new Collapse(
                "Failed to solve " + algo, error
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
     * @param klass the specified klass
     * @param spare the specified spare to be embedded
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    static Spare<?> embed(
        @NotNull Class<?> klass,
        @NotNull Spare<?> spare
    ) {
        return INS.put(
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
     * @param klass the specified klass
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     */
    @Nullable
    static Spare<?> revoke(
        @NotNull Class<?> klass
    ) {
        return INS.revoke(klass);
    }

    /**
     * Returns the {@link Spare} of {@code klass}, if not cached first through
     * the custom {@link Provider} set and then through default {@link Supplier} final lookup
     *
     * <pre>{@code
     *  Spare<User> spare = Spare.lookup(User.class);
     * }</pre>
     *
     * @param klass the specified klass
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    static <T> Spare<T> lookup(
        @NotNull Class<T> klass
    ) {
        return INS.lookup(klass);
    }
}
