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

import plus.kat.anno.*;
import plus.kat.spare.*;
import plus.kat.crash.*;
import plus.kat.reflex.*;
import plus.kat.utils.*;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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

import static plus.kat.Plan.DEF;
import static plus.kat.chain.Space.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public interface Supplier extends Cloneable {
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
     * @param klass the specified klass for embed
     * @param spare the specified spare to be embedded
     * @return the previous {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#embed(Class, Spare)
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
     * @param klass the specified klass for revoke
     * @return the previous {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#revoke(Class)
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
     * @param klass the specified klass for embed
     * @param spare the specified spare to be embedded
     * @return the previous {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} or {@code spare} is null
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
     * @param klass the specified klass for revoke
     * @return the previous {@link Spare} or {@code null}
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
     * @param klass the specified klass for lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#lookup(Class)
     */
    @Nullable <T>
    Spare<T> lookup(
        @NotNull Class<T> klass
    );

    /**
     * Returns the {@link Spare} of {@code klass}
     * and then call {@link Spare#accept(Class)} to check.
     * If there is no cache, use the {@link Provider} to search for it
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Spare<User> spare = supplier.lookup(
     *      "plus.kat.entity.User"
     *  );
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#lookup(CharSequence)
     */
    @Nullable <T>
    Spare<T> lookup(
        @NotNull CharSequence klass
    );

    /**
     * Returns the {@link Spare} of {@code type}.
     * If the spare of the type does not exist, search according to klass
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Type type = User.class;
     *  Spare<User> spare = supplier.lookup(
     *      type, "plus.kat.entity.User"
     *  );
     * }</pre>
     *
     * @param type  the specified type for lookup
     * @param klass the specified alternate klass
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Spare#accept(Class)
     * @see Impl#lookup(Type, CharSequence)
     * @since 0.0.4
     */
    @Nullable <T>
    Spare<T> lookup(
        @Nullable Type type,
        @Nullable CharSequence klass
    );

    /**
     * Returns the {@link Spare} of {@code type}.
     * If the spare of the type does not exist, search according to klass
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Spare<User> spare = supplier.lookup(
     *      User.class, "plus.kat.entity.User"
     *  );
     * }</pre>
     *
     * @param type  the specified type for lookup
     * @param klass the specified alternate klass
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Spare#accept(Class)
     * @see Impl#lookup(Class, CharSequence)
     * @since 0.0.4
     */
    @Nullable <T>
    Spare<T> lookup(
        @Nullable Class<T> type,
        @Nullable CharSequence klass
    );

    /**
     * Returns the {@link Spare} of {@code klass}
     * and then call {@link Spare#accept(Class)} to check. If there is no cache,
     * use the {@link Provider} to search for it, if null try to search as class name
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Spare<User> spare = supplier.search(
     *      "plus.kat.entity.User"
     *  );
     * }</pre>
     *
     * @param klass the specified klass for search
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Spare#accept(Class)
     * @see Impl#search(CharSequence)
     * @see Supplier#search(Class, CharSequence)
     * @since 0.0.4
     */
    @Nullable <T>
    Spare<T> search(
        @NotNull CharSequence klass
    );

    /**
     * Returns the {@link Spare} of {@code type}.
     * Find the class of the type as the parent class, and then
     * look for an alternative to the klass. If there is no cache,
     * use the {@link Provider} to search for it, if null try to search as class name
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Type type = User.class;
     *  Spare<UserVO> spare = supplier.search(type, "plus.kat.entity.UserVO");
     * }</pre>
     *
     * @param type  the specified parent type
     * @param klass the specified actual type
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Spare#accept(Class)
     * @see Impl#search(Type, CharSequence)
     * @see Supplier#search(Class, CharSequence)
     * @since 0.0.4
     */
    @Nullable <T>
    Spare<T> search(
        @Nullable Type type,
        @Nullable CharSequence klass
    );

    /**
     * Returns the {@link Spare} of {@code klass}
     * and then call {@link Spare#accept(Class)} to check.
     * <p>
     * If not cached first through uses the {@link Provider} to search for it,
     * if not found, then use {@link Class#forName(String, boolean, ClassLoader)}
     * to find and judge whether it is a subclass of {@code type} and then find its {@link Spare}.
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Class<User> type = User.class;
     *  Spare<UserVO> spare = supplier.search(type, "plus.kat.entity.UserVO");
     * }</pre>
     *
     * @param type  the specified parent class
     * @param klass the specified actual class
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Spare#accept(Class)
     * @see Impl#search(Class, CharSequence)
     * @since 0.0.4
     */
    @Nullable <K, T extends K>
    Spare<T> search(
        @Nullable Class<K> type,
        @Nullable CharSequence klass
    );

    /**
     * Returns the default supplier
     */
    @NotNull
    static Supplier ins() {
        return Impl.INS;
    }

    /**
     * If this spare can be built,
     * then perform a given type to create a {@link E}
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  User user = supplier.apply(User.class);
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @return {@link E}, it is not null
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Spare#apply(Type)
     * @since 0.0.4
     */
    @NotNull
    default <E> E apply(
        @NotNull Class<E> klass
    ) throws Collapse {
        Spare<E> spare = lookup(klass);

        if (spare != null) {
            return spare.apply(klass);
        } else {
            throw new FatalCrash(
                "No spare of " + klass
            );
        }
    }

    /**
     * If the spare can be built with spiller,
     * then perform a given {@link Spoiler} to create a {@link E}
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Class<User> clazz = ...
     *  User user = supplier.apply(
     *     clazz, supplier.flat(bean)
     *  );
     * }</pre>
     *
     * @param klass   the specified klass for lookup
     * @param spoiler the specified spoiler to be used
     * @return {@link E}, it is not null
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws NullPointerException If the specified klass or the spoiler is null
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
            throw new FatalCrash(
                "No spare of " + klass
            );
        }

        return spare.apply(spoiler, this);
    }

    /**
     * If the spare can be built with resultSet,
     * then perform a given {@link ResultSet} to create a {@link E}
     *
     * <pre>{@code
     *  ResultSet rs = stmt.executeQuery(sql);
     *  List<User> users = new ArrayList<>();
     *  User user = supplier.apply(
     *     clazz, supplier.flat(bean)
     *  );
     *
     *  Class<User> clazz = ...
     *  Supplier supplier = ...
     *  while (rs.next()) {
     *    users.add(
     *      supplier.apply(clazz, rs)
     *    );
     *  }
     * }</pre>
     *
     * @return {@link E}, it is not null
     * @throws SQLCrash             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the specified klass or resultSet is null
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
            throw new FatalCrash(
                "No spare of " + klass
            );
        }

        return spare.apply(this, result);
    }

    /**
     * Convert the {@link Object} to {@code K}
     *
     * <pre>{@code
     *  Class<User> clazz = ...
     *  Supplier supplier = ...
     *
     *  User user = spare.supplier(
     *      clazz, "{:id(1):name(kraity)}"
     *  );
     *  User user = spare.cast(
     *      clazz, Map.of("id", 1, "name", "kraity")
     *  );
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @param data  the specified data to be converted
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
     * <pre>{@code
     *  String clazz = "plus.kat.entity.User"
     *  Supplier supplier = ...
     *
     *  User user = spare.supplier(
     *      clazz, "{:id(1):name(kraity)}"
     *  );
     *  User user = spare.cast(
     *      clazz, Map.of("id", 1, "name", "kraity")
     *  );
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @param data  the specified data to be converted
     * @return {@link E} or {@code null}
     * @throws ClassCastException If {@link E} is not an instance of {@code klass}
     * @see Spare#cast(Object, Supplier)
     */
    @Nullable
    default <E> E cast(
        @NotNull CharSequence klass,
        @NotNull Object data
    ) {
        Spare<E> spare = search(klass);

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
     * @param bean the specified bean to be flattened
     * @return {@link Spoiler} or {@code null}
     * @throws NullPointerException If the specified bean is null
     * @since 0.0.3
     */
    @Nullable
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
     * @param bean    the specified bean to be flattened
     * @param visitor the specified visitor used to access bean
     * @return {@code true} if the bean can be flattened otherwise {@code false}
     * @throws NullPointerException If the {@code bean} or {@code visitor} is null
     * @see Spare#flat(Object, Visitor)
     * @since 0.0.3
     */
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
     * @param bean    the specified bean to be updated
     * @param spoiler the specified spoiler as data source
     * @return {@code true} if successful update
     * @throws NullPointerException If the parameters contains null
     * @since 0.0.4
     */
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
     * @param bean      the specified bean to be updated
     * @param resultSet the specified spoiler as data source
     * @return {@code true} if successful update
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the parameters contains null
     * @since 0.0.4
     */
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
     *  supplier.mutate(source, target);
     * }</pre>
     *
     * @param target the specified target to be updated
     * @param source the specified source as data source
     * @return {@code true} if successful update
     * @throws NullPointerException If the parameters contains null
     * @see Spare#update(Object, Spoiler, Supplier)
     * @since 0.0.4
     */
    default <S, T> boolean mutate(
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
     * Resolve the Kat {@code text} and convert the result to {@link T}
     *
     * <pre>{@code
     *   Supplier supplier = ...
     *   String text = ...
     *   User user = supplier.read(text);
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws ClassCastException   If {@link T} is not an instance of {@code klass}
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(CharSequence, Algo, Event)
     */
    @NotNull
    default <T> T read(
        @NotNull CharSequence klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Algo.KAT, event
        );
    }

    /**
     * Resolve the Kat {@link Event} and convert the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   User user = supplier.read(
     *      event.with(
     *         Flag.STRING_AS_OBJECT
     *      )
     *   );
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(Class, Algo, Event)
     */
    @NotNull
    default <E, T extends E> T read(
        @NotNull Class<E> klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Algo.KAT, event
        );
    }

    /**
     * Serialize to {@link Chan}
     *
     * <pre>{@code
     *   User user = ...
     *   Supplier supplier = ...
     *
     *   try (Chan chan = supplier.write(user)) {
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
        @Nullable Object value
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
     *   Supplier supplier = ...
     *
     *   try (Chan chan = supplier.write(user, Flag.UNICODE)) {
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
        @Nullable Object value, long flags
    ) throws IOException {
        Chan chan = new Chan(
            flags, this
        );
        chan.set(null, value);
        return chan;
    }

    /**
     * Resolve the Doc {@code text} and convert the result to {@link T}
     *
     * <pre>{@code
     *   Supplier supplier = ...
     *   String text = ...
     *   User user = supplier.down(text);
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(CharSequence, Algo, Event)
     */
    @NotNull
    default <T> T down(
        @NotNull CharSequence klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Algo.DOC, event
        );
    }

    /**
     * Resolve the Doc {@link Event} and convert the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   User user = supplier.down(
     *      event.with(
     *         Flag.STRING_AS_OBJECT
     *      )
     *   );
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Supplier#solve(Class, Algo, Event)
     */
    @NotNull
    default <E, T extends E> T down(
        @NotNull Class<E> klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Algo.DOC, event
        );
    }

    /**
     * Serialize to {@link Doc}
     *
     * <pre>{@code
     *   User user = ...
     *   Supplier supplier = ...
     *
     *   try (Doc doc = supplier.mark(user)) {
     *       byte[] bs = doc.toBytes();
     *       String st = doc.toString();
     *   }
     * }</pre>
     *
     * @param value the specified value to serialized
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
     * <pre>{@code
     *   User user = ...
     *   Supplier supplier = ...
     *
     *   try (Doc doc = supplier.mark(user, Flag.UNICODE)) {
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
     * Resolve the Json {@code text} and convert the result to {@link T}
     *
     * <pre>{@code
     *   Supplier supplier = ...
     *   String text = ...
     *   User user = supplier.parse(text);
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws NullPointerException If the specified {@code klass} {@code event} is null
     * @see Supplier#solve(CharSequence, Algo, Event)
     */
    @NotNull
    default <T> T parse(
        @NotNull CharSequence klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Algo.JSON, event
        );
    }

    /**
     * Resolve the Json {@link Event} and convert the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   User user = supplier.parse(
     *      event.with(
     *         Flag.STRING_AS_OBJECT
     *      )
     *   );
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws NullPointerException If the specified {@code klass} {@code event} is null
     * @see Supplier#solve(Class, Algo, Event)
     */
    @NotNull
    default <E, T extends E> T parse(
        @NotNull Class<E> klass,
        @NotNull Event<T> event
    ) {
        return solve(
            klass, Algo.JSON, event
        );
    }

    /**
     * Serialize to {@link Json}
     *
     * <pre>{@code
     *   User user = ...
     *   Supplier supplier = ...
     *
     *   try (Json json = supplier.serial(user)) {
     *       byte[] bs = json.toBytes();
     *       String st = json.toString();
     *   }
     * }</pre>
     *
     * @param value the specified value to serialized
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
     * <pre>{@code
     *   User user = ...
     *   Supplier supplier = ...
     *
     *   try (Json json = supplier.serial(user, Flag.UNICODE)) {
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
     * Returns the {@link Chan} of the algo with the specified plan
     *
     * <pre>{@code
     *  Algo algo = ...
     *  Supplier supplier = ..
     *
     *  Plan plan = ...
     *  Object data = ...
     *  try (Chan chan = supplier.telex(algo, plan)) {
     *      if (chan.set(null, data)) {
     *          String text = chan.toString();
     *      } else {
     *          throw new Exception(
     *              "Cannot serialize " + data + " to " + algo
     *          );
     *      }
     *  }
     * }</pre>
     *
     * @param algo the specified algo for telex
     * @param plan the specified plan for serialize
     * @throws FatalCrash           If no chan available for algo is found
     * @throws NullPointerException If the specified algo or the plan is null
     * @since 0.0.5
     */
    @NotNull
    default Chan telex(
        @NotNull Algo algo,
        @NotNull Plan plan
    ) {
        return telex(
            algo, plan.writeFlags
        );
    }

    /**
     * Returns the {@link Chan} of the algo with the specified flags
     *
     * <pre>{@code
     *  Algo algo = ...
     *  Supplier supplier = ..
     *
     *  Object data = ...
     *  try (Chan chan = supplier.telex(algo, Flag.PRETTY)) {
     *      if (chan.set(null, data)) {
     *          String text = chan.toString();
     *      } else {
     *          throw new Exception(
     *              "Cannot serialize " + data + " to " + algo
     *          );
     *      }
     *  }
     * }</pre>
     *
     * @param algo  the specified algo for telex
     * @param flags the specified flags for serialize
     * @throws FatalCrash           If no chan available for algo is found
     * @throws NullPointerException If the specified algo for telex is null
     * @since 0.0.5
     */
    @NotNull
    default Chan telex(
        @NotNull Algo algo,
        @NotNull long flags
    ) {
        switch (algo.name()) {
            case "kat": {
                return new Chan(
                    flags, this
                );
            }
            case "xml": {
                return new Doc(
                    flags, this
                );
            }
            case "json": {
                return new Json(
                    flags, this
                );
            }
            default: {
                throw new FatalCrash(
                    "Supplier didn't find the chan of " + algo
                );
            }
        }
    }

    /**
     * Resolve the {@link Event} and convert the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   Algo algo = ...
     *   User user = supplier.solve(
     *      algo, event.with(
     *         Flag.STRING_AS_OBJECT
     *      )
     *   );
     * }</pre>
     *
     * @param algo  the specified algo for solve
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws NullPointerException If the specified algo or the event is null
     * @see Spare#solve(Algo, Event)
     * @since 0.0.4
     */
    @NotNull
    default <T> T solve(
        @NotNull Algo algo,
        @NotNull Event<T> event
    ) {
        Spare<T> spare = (Spare<T>)
            event.getSpare();

        if (spare != null) {
            event.prepare(this);
            return spare.solve(
                algo, event
            );
        } else {
            Type type = event.getType();
            if (type != null) {
                return solve(
                    type, algo, event
                );
            } else {
                return solve(
                    Object.class, algo, event
                );
            }
        }
    }

    /**
     * Resolve the {@link Event} and convert the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   Algo algo = ...
     *   String type = "plus.kat.entity.User";
     *
     *   User user = supplier.solve(
     *      type, algo, event.with(
     *         Flag.STRING_AS_OBJECT
     *      )
     *   );
     * }</pre>
     *
     * @param algo  the specified algo for solve
     * @param klass the specified klass for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws NullPointerException If the specified klass, algo or the event is null
     * @see Spare#solve(Algo, Event)
     * @since 0.0.2
     */
    @NotNull
    default <T> T solve(
        @NotNull CharSequence klass,
        @NotNull Algo algo,
        @NotNull Event<T> event
    ) {
        Spare<T> spare = search(klass);

        if (spare == null) {
            throw new FatalCrash(
                "No spare of " + klass
            );
        }

        event.with(this);
        return spare.solve(algo, event);
    }

    /**
     * Resolve the {@link Event} and convert the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   Algo algo = ...
     *   Type type = User.class;
     *
     *   User user = supplier.solve(
     *      type, algo, event.with(
     *         Flag.STRING_AS_OBJECT
     *      )
     *   );
     * }</pre>
     *
     * @param algo  the specified algo for solve
     * @param type  the specified type for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws NullPointerException If the specified type, algo or the event is null
     * @see Spare#solve(Algo, Event)
     * @since 0.0.2
     */
    @NotNull
    default <T> T solve(
        @NotNull Type type,
        @NotNull Algo algo,
        @NotNull Event<T> event
    ) {
        Spare<T> spare = lookup(type, null);

        if (spare == null) {
            throw new FatalCrash(
                "No spare of " + type
            );
        }

        event.with(this);
        event.prepare(type);

        return spare.solve(algo, event);
    }

    /**
     * Resolve the {@link Event} and convert the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   Algo algo = ...
     *   Class<User> type = User.class;
     *
     *   User user = supplier.solve(
     *      type, algo, event.with(
     *         Flag.STRING_AS_OBJECT
     *      )
     *   );
     * }</pre>
     *
     * @param algo  the specified algo for solve
     * @param klass the specified klass for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for klass is found
     * @throws NullPointerException If the specified klass, algo or the event is null
     * @see Spare#solve(Algo, Event)
     * @since 0.0.2
     */
    @NotNull
    default <E, T extends E> T solve(
        @NotNull Class<E> klass,
        @NotNull Algo algo,
        @NotNull Event<T> event
    ) {
        Spare<E> spare = lookup(klass);

        if (spare == null) {
            throw new FatalCrash(
                "No spare of " + klass
            );
        }

        event.with(this);
        event.prepare(klass);

        return spare.solve(algo, event);
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    @SuppressWarnings("rawtypes")
    class Impl extends ConcurrentHashMap<Class<?>, Spare<?>> implements Supplier, Provider {
        /**
         * default supplier
         */
        static final Impl INS;

        /**
         * default providers
         */
        static Provider[] PRO;

        static {
            INS = new Impl(
                Config.get(
                    "kat.sponsor.capacity", 24
                ),
                Config.get(
                    "kat.supplier.capacity", 32
                )
            );

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

            INS.table.put($, ObjectSpare.INSTANCE);
            INS.table.put($s, StringSpare.INSTANCE);
            INS.table.put($b, BooleanSpare.INSTANCE);
            INS.table.put($i, IntegerSpare.INSTANCE);
            INS.table.put($l, LongSpare.INSTANCE);
            INS.table.put($f, FloatSpare.INSTANCE);
            INS.table.put($d, DoubleSpare.INSTANCE);
            INS.table.put($c, CharSpare.INSTANCE);
            INS.table.put($o, ByteSpare.INSTANCE);
            INS.table.put($u, ShortSpare.INSTANCE);
            INS.table.put($M, MapSpare.INSTANCE);
            INS.table.put($A, ArraySpare.INSTANCE);
            INS.table.put($L, ListSpare.INSTANCE);
            INS.table.put($S, SetSpare.INSTANCE);
            INS.table.put($E, ErrorSpare.INSTANCE);
            INS.table.put($B, ByteArraySpare.INSTANCE);
            INS.table.put($I, BigIntegerSpare.INSTANCE);
            INS.table.put($D, BigDecimalSpare.INSTANCE);
            INS.table.put(EMPTY, ObjectSpare.INSTANCE);

            try (KatLoader<Provider> loader =
                     new KatLoader<>(Provider.class)) {
                loader.load(
                    Config.get(
                        "kat.spare.provider",
                        "plus.kat.spare.Provider"
                    )
                );

                if (loader.hasNext()) {
                    final int size = loader.size() + 1;
                    Provider[] PS = new Provider[size];

                    int i = 0;
                    PS[i++] = INS;
                    while (loader.hasNext()) {
                        PS[i++] = loader.next();
                    }

                    int k = i = 1;
                    for (; i < size; i++) {
                        Provider P = PS[i];
                        try {
                            if (P.accept(INS)) {
                                PS[k++] = P;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (k != i) {
                        Provider[] RS = new Provider[k];
                        System.arraycopy(
                            PS, 0, RS, 0, k
                        );
                        PS = RS;
                    }

                    PRO = PS;
                    if (k != 1) {
                        Arrays.sort(
                            PRO, Collections.reverseOrder()
                        );
                    }
                } else {
                    PRO = new Provider[]{INS};
                }
            } catch (Exception e) {
                throw new Error(
                    "Unexpectedly, cannot be loaded", e
                );
            }
        }

        /**
         * default relationship table
         */
        protected final Map<CharSequence, Spare<?>> table;

        public Impl(
            int sponsor, int capacity
        ) {
            super(capacity);
            table = new ConcurrentHashMap<>(sponsor);
        }

        @Override
        public int grade() {
            return 0x88888888;
        }

        @Override
        public Spare<?> embed(
            @NotNull Class<?> klass,
            @NotNull Spare<?> spare
        ) {
            return put(
                klass, spare
            );
        }

        @Override
        public Spare<?> revoke(
            @NotNull Class<?> klass
        ) {
            return remove(klass);
        }

        @Override
        public Spare<?> embed(
            @NotNull CharSequence klass,
            @NotNull Spare<?> spare
        ) {
            return table.put(
                klass, spare
            );
        }

        @Override
        public Spare<?> revoke(
            @NotNull CharSequence klass
        ) {
            return table.remove(klass);
        }

        @Override
        public <T> Spare<T> lookup(
            @NotNull Class<T> klass
        ) {
            Spare<?> spare = get(klass);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            Provider[] PS = PRO;
            if (PS != null) {
                for (Provider p : PS) {
                    try {
                        spare = p.lookup(
                            klass, this
                        );
                    } catch (Collapse e) {
                        return null;
                    }

                    if (spare != null) {
                        return (Spare<T>) spare;
                    }
                }
            }

            return null;
        }

        @Override
        public <T> Spare<T> lookup(
            @NotNull CharSequence klass
        ) {
            return search(null, klass);
        }

        @Override
        public <T> Spare<T> lookup(
            @Nullable Type type,
            @Nullable CharSequence klass
        ) {
            return lookup(
                (Class<T>) Find.clazz(type), klass
            );
        }

        @Override
        public Spare<?> lookup(
            @NotNull Class<?> klass,
            @NotNull Supplier supplier
        ) {
            Spare<?> spare = null;
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
                case '[': {
                    if (klass.isArray()) {
                        spare = new ArraySpare(klass);
                        spare.embed(this);
                        return spare;
                    }
                }
            }

            Embed embed = klass
                .getAnnotation(Embed.class);
            if (embed == null) {
                if (klass.isInterface() ||
                    Kat.class.isAssignableFrom(klass) ||
                    Coder.class.isAssignableFrom(klass)) {
                    return null;
                }
            } else {
                Class<?> clazz = embed.with();
                if (clazz != Spare.class) {
                    // Pointing to clazz?
                    if (!Spare.class.
                        isAssignableFrom(clazz)) {
                        spare = lookup(clazz);
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

                        Object[] args;
                        int size = c.getParameterCount();

                        if (size == 0) {
                            args = ArraySpare.EMPTY_ARRAY;
                        } else {
                            args = new Object[size];
                            Class<?>[] cls =
                                c.getParameterTypes();
                            for (int i = 0; i < size; i++) {
                                Class<?> m = cls[i];
                                if (m == Class.class) {
                                    args[i] = klass;
                                } else if (m == Embed.class) {
                                    args[i] = embed;
                                } else if (m == Supplier.class) {
                                    args[i] = this;
                                } else if (m.isPrimitive()) {
                                    args[i] = Find.value(m);
                                } else if (m.isAnnotation()) {
                                    args[i] = klass.getAnnotation(
                                        (Class<? extends Annotation>) m
                                    );
                                }
                            }
                        }

                        if (!c.isAccessible()) {
                            c.setAccessible(true);
                        }
                        spare = (Spare<?>) c.newInstance(args);
                        spare.embed(this);
                    } catch (Exception e) {
                        // Nothing
                    }
                    return spare;
                }

                if (klass.isInterface()) {
                    spare = new ProxySpare(
                        embed, klass, this
                    );
                    spare.embed(this);
                    return spare;
                }
            }

            try {
                Class<?> sc = klass.getSuperclass();
                if (sc == Enum.class) {
                    spare = new EnumSpare(
                        embed, klass, this
                    );
                } else {
                    String sn = sc.getName();
                    if (sn.equals("java.lang.Record")) {
                        spare = new RecordSpare<>(
                            embed, klass, this
                        );
                    } else {
                        spare = new ReflectSpare<>(
                            embed, klass, this
                        );
                    }
                }

                spare.embed(this);
                return spare;
            } catch (Exception e) {
                // Nothing
            }

            return null;
        }

        @Override
        public <T> Spare<T> lookup(
            @Nullable Class<T> type,
            @Nullable CharSequence klass
        ) {
            if (type == null) {
                return search(
                    null, klass
                );
            }

            Spare<T> spare = lookup(type);
            if (spare != null ||
                klass == null) {
                return spare;
            }

            return search(type, klass);
        }

        @Override
        public <T> Spare<T> search(
            @NotNull CharSequence klass
        ) {
            return search(
                Object.class, klass
            );
        }

        @Override
        public <T> Spare<T> search(
            @Nullable Type type,
            @Nullable CharSequence klass
        ) {
            return search(
                (Class<T>) Find.clazz(type), klass
            );
        }

        @Override
        public <K, T extends K> Spare<T> search(
            @Nullable Class<K> type,
            @Nullable CharSequence klass
        ) {
            if (klass == null) {
                return null;
            }

            Spare<?> spare = table.get(klass);
            if (spare != null) {
                if (type == null ||
                    spare.accept(type)) {
                    return (Spare<T>) spare;
                }
                return null;
            }

            int i = klass.length();
            if (i < 2 || i > 191) {
                return null;
            }

            Provider[] PS = PRO;
            if (PS != null) {
                String name = klass.toString();
                for (Provider p : PS) {
                    try {
                        spare = p.search(
                            type, name, this
                        );
                    } catch (Collapse e) {
                        return null;
                    }

                    if (spare != null) {
                        return (Spare<T>) spare;
                    }
                }
            }

            return null;
        }

        @Override
        public Spare<?> search(
            @Nullable Class<?> type,
            @NotNull String name,
            @NotNull Supplier supplier
        ) {
            if (type != null) {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread()
                        .getContextClassLoader();
                } catch (Throwable e) {
                    // Cannot access thread ClassLoader
                }

                if (cl == null) {
                    try {
                        cl = type.getClassLoader();
                    } catch (Throwable e) {
                        // Cannot access caller ClassLoader
                    }

                    if (cl == null) {
                        try {
                            cl = ClassLoader.getSystemClassLoader();
                        } catch (Throwable e) {
                            // Cannot access system ClassLoader
                        }
                    }
                }

                Class<?> child;
                try {
                    child = Class.forName(
                        name, false, cl
                    );
                } catch (LinkageError |
                         ClassNotFoundException e) {
                    return null;
                }

                if (type.isAssignableFrom(child)) {
                    Spare<?> spare = lookup(child);
                    if (spare != null) {
                        table.putIfAbsent(
                            name, spare
                        );
                    }
                    return spare;
                }
            }

            return null;
        }

        @Nullable
        protected <T> Spare<T> onJava(
            @NotNull String name,
            @NotNull Class<T> klass
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
                    this.put(klass, spare);
                    return (Spare<T>) spare;
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

                    this.put(klass, spare);
                    return (Spare<T>) spare;
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

                    this.put(klass, spare);
                    return (Spare<T>) spare;
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
                                if (klass == Map.class ||
                                    klass == LinkedHashMap.class) {
                                    spare = MapSpare.INSTANCE;
                                } else {
                                    spare = new MapSpare(klass);
                                }
                            } else if (Set.class.isAssignableFrom(klass)) {
                                if (klass == Set.class ||
                                    klass == HashSet.class) {
                                    spare = SetSpare.INSTANCE;
                                } else {
                                    spare = new SetSpare(klass);
                                }
                            } else if (List.class.isAssignableFrom(klass)) {
                                if (klass == List.class ||
                                    klass == ArrayList.class) {
                                    spare = ListSpare.INSTANCE;
                                } else {
                                    spare = new ListSpare(klass);
                                }
                            } else {
                                return null;
                            }

                            this.put(klass, spare);
                            return (Spare<T>) spare;
                        }
                        // java.util.concurrent.
                        case 20: {
                            if (Map.class.isAssignableFrom(klass)) {
                                spare = new MapSpare(klass);
                            } else if (Set.class.isAssignableFrom(klass)) {
                                spare = new SetSpare(klass);
                            } else if (List.class.isAssignableFrom(klass)) {
                                spare = new ListSpare(klass);
                            } else {
                                return null;
                            }

                            this.put(klass, spare);
                            return (Spare<T>) spare;
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

                            this.put(klass, spare);
                            return (Spare<T>) spare;
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

        @Override
        public String toString() {
            return "plus.kat.Supplier.Impl@"
                + Integer.toHexString(
                System.identityHashCode(this)
            );
        }
    }
}
