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
import static plus.kat.spare.Parser.Group;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public interface Spare<T> extends Coder<T> {
    /**
     * Returns the space of {@link T}.
     * Normally, it's a class name or a custom name
     */
    @NotNull
    String getSpace();

    /**
     * Returns the flag of {@link T}
     *
     * <pre>{@code
     *  null  ->  the T is a attribute
     *  true  ->  the T is a bean object
     *  false ->  the T is a list or array
     * }</pre>
     */
    @Nullable
    Boolean getFlag();

    /**
     * Returns the border of {@link T}
     *
     * <pre>{@code
     *  null  ->  requires on-demand use
     *  true  ->  requires to use border
     *  false ->  requires not to use border
     * }</pre>
     */
    @Nullable
    Boolean getBorder(
        @NotNull Flag flag
    );

    /**
     * Returns a {@link Factory} of {@link T}
     *
     * @param type the specified actual type
     */
    @Nullable
    Factory getFactory(
        @Nullable Type type
    );

    /**
     * Returns the {@link Class} of {@link T}
     */
    @NotNull
    Class<T> getType();

    /**
     * Returns the {@link Supplier} of {@link T}.
     * Normally, this spare is loaded by the supplier
     */
    @NotNull
    Supplier getSupplier();

    /**
     * If this {@link Spare} can create an instance,
     * it returns it, otherwise it will return {@code null}
     *
     * @return {@link T} or {@code null}
     * @throws Collapse If a build error occurs
     * @since 0.0.3
     */
    @Nullable
    default T apply() {
        return null;
    }

    /**
     * If this {@link Spare} can create an instance of the actual
     * subclass type, it returns it, otherwise it will throw collapse
     *
     * @param type the specified actual subclass type
     * @return {@link T} or throws collapse
     * @throws Collapse If failed to build an instance of a subclass
     *                  or the {@code type} is not a subclass of {@link T}
     * @since 0.0.4
     */
    @NotNull
    default T apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == getType()
        ) {
            T bean = apply();
            if (bean != null) {
                return bean;
            }

            throw new Collapse(
                "Failed to apply"
            );
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    /**
     * If this spare can be built with spiller,
     * then perform a given {@link Spoiler} to create a {@link T}
     *
     * @param spoiler the specified spoiler as source
     * @return {@link T}, it is not null
     * @throws Collapse             If a build error occurs
     * @throws NullPointerException If the specified spoiler is null
     * @see Spare#apply(Spoiler, Supplier)
     * @see Property#apply(Spoiler, Supplier)
     * @see AbstractSpare#apply(Spoiler, Supplier)
     * @since 0.0.4
     */
    @NotNull
    default T apply(
        @NotNull Spoiler spoiler
    ) throws Collapse {
        return apply(
            spoiler, getSupplier()
        );
    }

    /**
     * If this spare can be built with spiller,
     * then perform a given {@link Spoiler} to create a {@link T}
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
     * @param spoiler  the specified spoiler as source
     * @param supplier the specified supplier as the loader
     * @return {@link T}, it is not null
     * @throws Collapse             If a build error occurs
     * @throws NullPointerException If the specified supplier or the spoiler is null
     * @see AbstractSpare#apply(Spoiler, Supplier)
     * @since 0.0.4
     */
    @NotNull
    default T apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) throws Collapse {
        throw new Collapse(
            "'" + getType() + "' not a Bean"
        );
    }

    /**
     * If this spare can be built with resultSet,
     * then perform a given {@link ResultSet} to create a {@link T}
     *
     * @param resultSet the specified result as data source
     * @return {@link T}, it is not null
     * @throws SQLCrash             If a build error occurs
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the specified resultSet is null
     * @see Spare#apply(Supplier, ResultSet)
     * @see Property#apply(Supplier, ResultSet)
     * @see AbstractSpare#apply(Supplier, ResultSet)
     * @since 0.0.3
     */
    @NotNull
    default T apply(
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return apply(
            getSupplier(), resultSet
        );
    }

    /**
     * If this spare can be built with resultSet,
     * then perform a given {@link ResultSet} to create a {@link T}
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
     * @param supplier  the specified supplier as the loader
     * @param resultSet the specified resultSet as data source
     * @return {@link T}, it is not null
     * @throws SQLCrash             If a build error occurs
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the specified supplier or the resultSet is null
     * @see AbstractSpare#apply(Supplier, ResultSet)
     * @since 0.0.3
     */
    @NotNull
    default T apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        throw new SQLCrash(
            "'" + getType() + "' not a Bean"
        );
    }

    /**
     * Register to the specified supplier
     *
     * <pre>{@code
     *   public Spare<T> join(
     *       Supplier supplier
     *   ) {
     *       supplier.embed(getType(), this);
     *       supplier.embed("plus.kat.entity.User", this);
     *
     *       String[] spaces = ...
     *       for (String space : spaces) {
     *          if (space.indexOf('.', 1) != -1) {
     *             supplier.embed(space, this);
     *          }
     *       }
     *       return this;
     *   }
     * }</pre>
     *
     * @param supplier the specified supplier to be loaded
     * @return this {@link Spare}
     * @see AbstractSpare#join(Supplier)
     * @see Supplier#embed(Class, Spare)
     * @see Supplier#embed(CharSequence, Spare)
     * @since 0.0.5
     */
    @NotNull
    default Spare<T> join(
        @NotNull Supplier supplier
    ) {
        supplier.embed(
            getType(), this
        );
        return this;
    }

    /**
     * Removes from the specified supplier
     *
     * <pre>{@code
     *   public Spare<T> drop(
     *       Supplier supplier
     *   ) {
     *       supplier.revoke(getType(), this);
     *       supplier.revoke("plus.kat.entity.User", this);
     *
     *       String[] spaces = ...
     *       for (String space : spaces) {
     *          if (space.indexOf('.', 1) != -1) {
     *             supplier.revoke(space, this);
     *          }
     *       }
     *       return this;
     *   }
     * }</pre>
     *
     * @param supplier the specified supplier to be purged
     * @return this {@link Spare}
     * @see AbstractSpare#drop(Supplier)
     * @see Supplier#revoke(Class, Spare)
     * @see Supplier#revoke(CharSequence, Spare)
     * @since 0.0.5
     */
    @NotNull
    default Spare<T> drop(
        @NotNull Supplier supplier
    ) {
        supplier.revoke(
            getType(), this
        );
        return this;
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
     * @param name the property name of the bean
     * @return {@link Setter} or {@code null}
     * @throws NullPointerException If the specified name is null
     * @since 0.0.4
     */
    @Nullable
    default Setter<T, ?> set(
        @NotNull CharSequence name
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
     * @param name the property name of the bean
     * @return {@link Getter} or {@code null}
     * @throws NullPointerException If the specified name is null
     * @since 0.0.4
     */
    @Nullable
    default Getter<T, ?> get(
        @NotNull CharSequence name
    ) {
        return null;
    }

    /**
     * If {@link T} is a Bean, then returns
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
     * @param bean the specified bean to be flattened
     * @return {@link Spoiler} or {@code null}
     * @throws NullPointerException If the specified bean is null
     * @since 0.0.3
     */
    @Nullable
    default Spoiler flat(
        @NotNull T bean
    ) {
        return null;
    }

    /**
     * If {@link T} is a Bean, then perform a given
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
     * @param bean    the specified bean to be flattened
     * @param visitor the specified visitor used to access bean
     * @return {@code true} if the bean can be flattened otherwise {@code false}
     * @throws NullPointerException If the {@code bean} or {@code visitor} is null
     * @since 0.0.3
     */
    default boolean flat(
        @NotNull T bean,
        @NotNull Visitor visitor
    ) {
        return false;
    }

    /**
     * Resolves the Kat {@link Paper} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Spare<User> spare = ...
     *   Paper paper = ...
     *   User user = spare.read(paper);
     * }</pre>
     *
     * @param paper the specified paper to be parsed
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code paper} is null
     */
    @NotNull
    default T read(
        @NotNull Paper paper
    ) {
        return read(
            new Event<>(paper)
        );
    }

    /**
     * Resolves the Kat {@link Event} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Spare<User> spare = ...
     *
     *   User user = spare.read(
     *      event.with(
     *         Flag.VALUE_AS_BEAN
     *      )
     *   );
     * }</pre>
     *
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     */
    @NotNull
    default <V extends T> V read(
        @NotNull Event<V> event
    ) {
        return solve(
            Algo.KAT, event
        );
    }

    /**
     * Serializes the specified {@link T} to {@link Kat}
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
     * @param value the specified value to serialized
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan write(
        @Nullable T value
    ) throws IOException {
        return write(
            value, DEF.writeFlags
        );
    }

    /**
     * Serializes the specified {@link T} to {@link Kat}
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
     * @param value the specified value to serialized
     * @param flags the specified flags for serialize
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan write(
        @Nullable T value, long flags
    ) throws IOException {
        Chan chan = new Kat(
            flags, getSupplier()
        );
        chan.set(null, this, value);
        return chan;
    }

    /**
     * Resolves the Doc {@link Paper} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Spare<User> spare = ...
     *   Paper paper = ...
     *   User user = spare.down(paper);
     * }</pre>
     *
     * @param paper the specified paper to be parsed
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code paper} is null
     */
    @NotNull
    default T down(
        @NotNull Paper paper
    ) {
        return down(
            new Event<>(paper)
        );
    }

    /**
     * Resolves the Doc {@link Event} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Spare<User> spare = ...
     *
     *   User user = spare.down(
     *      event.with(
     *         Flag.VALUE_AS_BEAN
     *      )
     *   );
     * }</pre>
     *
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     */
    @NotNull
    default <V extends T> V down(
        @NotNull Event<V> event
    ) {
        return solve(
            Algo.DOC, event
        );
    }

    /**
     * Serializes the specified {@link T} to {@link Doc}
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
     * @param value the specified value to serialized
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan mark(
        @Nullable T value
    ) throws IOException {
        return mark(
            value, DEF.writeFlags
        );
    }

    /**
     * Serializes the specified {@link T} to {@link Doc}
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
     * @param value the specified value to serialized
     * @param flags the specified flags for serialize
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan mark(
        @Nullable T value, long flags
    ) throws IOException {
        Chan chan = new Doc(
            flags, getSupplier()
        );
        chan.set(null, this, value);
        return chan;
    }

    /**
     * Resolves the Json {@link Paper} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Spare<User> spare = ...
     *   Paper paper = ...
     *   User user = spare.parse(paper);
     * }</pre>
     *
     * @param paper the specified paper to be parsed
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code paper} is null
     */
    @NotNull
    default T parse(
        @NotNull Paper paper
    ) {
        return parse(
            new Event<>(paper)
        );
    }

    /**
     * Resolves the Json {@link Event} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Spare<User> spare = ...
     *
     *   User user = spare.parse(
     *      event.with(
     *         Flag.VALUE_AS_BEAN
     *      )
     *   );
     * }</pre>
     *
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     */
    @NotNull
    default <V extends T> V parse(
        @NotNull Event<V> event
    ) {
        return solve(
            Algo.JSON, event
        );
    }

    /**
     * Serializes the specified {@link T} to {@link Json}
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
     * @param value the specified value to serialized
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan serial(
        @Nullable T value
    ) throws IOException {
        return serial(
            value, DEF.writeFlags
        );
    }

    /**
     * Serializes the specified {@link T} to {@link Json}
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
     * @param value the specified value to serialized
     * @param flags the specified flags for serialize
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan serial(
        @Nullable T value, long flags
    ) throws IOException {
        Chan chan = new Json(
            flags, getSupplier()
        );
        chan.set(null, this, value);
        return chan;
    }

    /**
     * Converts the {@link Object} to {@code T}
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
     * @param object the specified object to be to converted
     * @return {@link T} or {@code null}
     * @throws Collapse              If a build error occurs
     * @throws IllegalStateException If the object cannot be converted to {@link T}
     * @see Spare#cast(Object, Supplier)
     */
    @Nullable
    default T cast(
        @Nullable Object object
    ) {
        return cast(
            object, getSupplier()
        );
    }

    /**
     * Converts the {@link Object} to {@code T}
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
     * @param object   the object to be converted
     * @param supplier the specified supplier to be used
     * @return {@link T} or {@code null}
     * @throws Collapse              If a build error occurs
     * @throws NullPointerException  If the specified supplier is null
     * @throws IllegalStateException If the object cannot be converted to {@link T}
     */
    @Nullable
    default T cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        Class<?> clazz = getType();
        if (clazz.isInstance(object)) {
            return (T) object;
        }

        throw new IllegalStateException(
            object + " cannot be converted to " + clazz
        );
    }

    /**
     * Copy the property values of the specified spoiler into the given specified bean
     *
     * @param bean    the specified bean to be updated
     * @param spoiler the specified spoiler as data source
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     * @see Spare#update(Object, Spoiler, Supplier)
     * @since 0.0.4
     */
    default int update(
        @NotNull T bean,
        @NotNull Spoiler spoiler
    ) {
        return update(
            bean, spoiler, getSupplier()
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
     * @param bean     the specified bean to be updated
     * @param spoiler  the specified spoiler as data source
     * @param supplier the specified supplier as the spare loader
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     * @see Subject#update(Object, Spoiler, Supplier)
     * @since 0.0.4
     */
    default int update(
        @NotNull T bean,
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) {
        return 0;
    }

    /**
     * Copy the property values of the specified resultSet into the given specified bean
     *
     * @param bean      the specified bean to be updated
     * @param resultSet the specified spoiler as data source
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     * @see Spare#update(Object, Supplier, ResultSet)
     * @since 0.0.4
     */
    default int update(
        @NotNull T bean,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return update(
            bean, getSupplier(), resultSet
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
     * @param bean      the specified bean to be updated
     * @param supplier  the specified supplier as the loader
     * @param resultSet the specified spoiler as data source
     * @return the number of rows affected
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the parameters contains null
     * @see Subject#update(Object, Supplier, ResultSet)
     * @since 0.0.4
     */
    default int update(
        @NotNull T bean,
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return 0;
    }

    /**
     * Parse {@link Event} and convert result to {@link T}
     *
     * @param algo  the specified algo for solve
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code event} is null
     * @since 0.0.2
     */
    @NotNull
    default <V extends T> V solve(
        @NotNull Algo algo,
        @NotNull Event<V> event
    ) {
        // parser pool
        Group group = Group.INS;

        // borrow parser
        Parser parser = group.borrow();

        try {
            return parser.read(
                algo, event.with(this)
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
     * @param klass the specified klass for embed
     * @param spare the specified spare to be embedded
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the parameters contains null
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
     *  Spare.revoke(User.class, null);
     *  Spare.revoke(User.class, getSpare());
     * }</pre>
     *
     * @param klass the specified klass for revoke
     * @param spare the specified spare to be removed
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     */
    @Nullable
    static Spare<?> revoke(
        @NotNull Class<?> klass,
        @Nullable Spare<?> spare
    ) {
        return INS.revoke(
            klass, spare
        );
    }

    /**
     * Returns the {@link Spare} of {@code klass}
     *
     * <pre>{@code
     *  Spare<User> spare = Spare.lookup(User.class);
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Supplier#lookup(Class)
     */
    @Nullable
    static <T> Spare<T> lookup(
        @NotNull Class<T> klass
    ) {
        return INS.lookup(klass);
    }

    /**
     * Returns the {@link Spare} of {@code klass}
     *
     * <pre>{@code
     *  Spare<User> spare = Spare.search("plus.kat.entity.User");
     * }</pre>
     *
     * @param klass the specified klass for search
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Supplier#search(CharSequence)
     * @since 0.0.5
     */
    @Nullable
    static <T> Spare<T> search(
        @NotNull CharSequence klass
    ) {
        return INS.search(klass);
    }
}
