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

import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.spare.*;
import plus.kat.utils.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.lang.annotation.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public interface Supplier extends Converter {
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
     * @see Supplier#revoke(Class, Spare)
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
     *  supplier.revoke(User.class, null); // removes the spare of the User.class
     *  supplier.revoke(User.class, getSpare()); // removes the specified klass and spare
     * }</pre>
     *
     * @param klass the specified klass for revoke
     * @param spare the specified spare to be removed
     * @return the previous {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Supplier#embed(Class, Spare)
     * @since 0.0.5
     */
    @Nullable
    Spare<?> revoke(
        @NotNull Class<?> klass,
        @Nullable Spare<?> spare
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
     * @see Supplier#revoke(String, Spare)
     */
    @Nullable
    Spare<?> embed(
        @NotNull String klass,
        @NotNull Spare<?> spare
    );

    /**
     * Removes the {@link Spare} cache for {@code klass}
     * and returns the previous value associated with {@code klass}
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  String klass = "plus.kat.entity.User"
     *
     *  supplier.revoke(klass, null); // removes the spare of the specified klass
     *  supplier.revoke(klass, getSpare()); // removes the specified klass and spare
     * }</pre>
     *
     * @param klass the specified klass for revoke
     * @param spare the specified spare to be removed
     * @return the previous {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Supplier#embed(String, Spare)
     * @since 0.0.5
     */
    @Nullable
    Spare<?> revoke(
        @NotNull String klass,
        @Nullable Spare<?> spare
    );

    /**
     * Look up the {@link Spare} of the {@code type}. If there's
     * no cache, the {@link Provider}s is used to search for the spare
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Type type = ...
     *  Spare<User> spare = supplier.lookup(type);
     * }</pre>
     *
     * @param type the specified type for search
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     */
    @Nullable <T>
    Spare<T> lookup(
        @NotNull Type type
    );

    /**
     * Look up the {@link Spare} of the {@code klass}. If there's
     * no cache, the {@link Provider}s is used to search for the spare
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Space klass = ...
     *  Spare<User> spare = supplier.lookup(klass);
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable <T>
    Spare<T> lookup(
        @NotNull Space klass
    );

    /**
     * Look up the {@link Spare} of the {@code klass}. If there's
     * no cache, the {@link Provider}s is used to search for the spare
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
     */
    @Nullable <T>
    Spare<T> lookup(
        @NotNull String klass
    );

    /**
     * Look up the {@link Spare} of the {@code klass}. If there's
     * no cache, the {@link Provider}s is used to search for the spare
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Spare<User> spare = supplier.lookup(User.class);
     * }</pre>
     *
     * @param klass the specified klass for lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable <T>
    Spare<T> lookup(
        @NotNull Class<T> klass
    );

    /**
     * Look up the {@link Spare} of the {@code type}. If there's
     * no cache, the {@link Provider}s is used to look for the spare
     * If still not found, use the {@code type} and {@code klass} to search for spare
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Space name = new Space(
     *      "plus.kat.entity.UserVO"
     *  );
     *  Spare<UserVO> spare = supplier.lookup(name, User.class);
     * }</pre>
     *
     * @param name  the specified actual name
     * @param klass the specified parent class
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code params} is null
     */
    @Nullable <T>
    Spare<T> lookup(
        @NotNull Space name,
        @NotNull Class<T> klass
    );

    /**
     * Look up the {@link Spare} of {@code klass}. If there's
     * no cache, the {@link Provider}s is used to search for the spare.
     * {@link Class#forName(String, boolean, ClassLoader)} method maybe used to
     * find and judge whether it's a subclass of the type and then find its spare
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  String klass = "plus.kat.entity.UserVO";
     *  Spare<UserVO> spare = supplier.lookup(klass, User.class);
     * }</pre>
     *
     * @param klass  the specified actual klass
     * @param parent the specified parent class
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code params} is null
     */
    @Nullable <K, T extends K>
    Spare<T> lookup(
        @NotNull String klass,
        @NotNull Class<K> parent
    );

    /**
     * Returns the default supplier
     */
    @NotNull
    static Supplier ins() {
        return Sample.INS;
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
                "Not found the spare of " + klass
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

        if (spare != null) {
            return spare.apply(
                spoiler, this
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + klass
            );
        }
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

        if (spare != null) {
            return spare.apply(
                this, result
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + klass
            );
        }
    }

    /**
     * Converts the {@link Object} to {@link E}
     *
     * <pre>{@code
     *  String clazz = "plus.kat.entity.User"
     *  Supplier supplier = ...
     *
     *  User user = supplier.cast(
     *      clazz, "{:id(1):name(kraity)}"
     *  );
     *  User user = supplier.cast(
     *      clazz, Map.of("id", 1, "name", "kraity")
     *  );
     * }</pre>
     *
     * @param klass  the specified klass for lookup
     * @param object the specified data to be converted
     * @return {@link E} or {@code null}
     * @throws Collapse              If a build error occurs
     * @throws FatalCrash            If no spare available for klass is found
     * @throws ClassCastException    If {@link E} is not an instance of the klass
     * @throws IllegalStateException If the object cannot be converted to {@link E}
     * @see Spare#cast(Object, Supplier)
     */
    @Nullable
    default <E> E cast(
        @NotNull String klass,
        @Nullable Object object
    ) {
        Spare<E> spare = lookup(klass);

        if (spare != null) {
            return spare.cast(
                object, this
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + klass
            );
        }
    }

    /**
     * Converts the {@link Object} to {@link E}
     *
     * <pre>{@code
     *  Class<User> clazz = ...
     *  Supplier supplier = ...
     *
     *  User user = supplier.cast(
     *      clazz, "{:id(1):name(kraity)}"
     *  );
     *  User user = supplier.cast(
     *      clazz, Map.of("id", 1, "name", "kraity")
     *  );
     * }</pre>
     *
     * @param klass  the specified klass for lookup
     * @param object the specified data to be converted
     * @return {@link E} or {@code null}
     * @throws Collapse              If a build error occurs
     * @throws FatalCrash            If no spare available for klass is found
     * @throws IllegalStateException If the object cannot be converted to {@link E}
     * @see Spare#cast(Object, Supplier)
     */
    @Nullable
    default <E> E cast(
        @NotNull Class<E> klass,
        @Nullable Object object
    ) {
        Spare<E> spare = lookup(klass);

        if (spare != null) {
            return spare.cast(
                object, this
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + klass
            );
        }
    }

    /**
     * If {@link T} is a Bean, then returns
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
     * @throws FatalCrash           If no spare available for bean is found
     * @throws NullPointerException If the specified bean is null
     * @since 0.0.3
     */
    @Nullable
    default <T> Spoiler flat(
        @NotNull T bean
    ) {
        Class<T> klass = (Class<T>)
            bean.getClass();
        Spare<T> spare = lookup(klass);

        if (spare != null) {
            return spare.flat(bean);
        } else {
            throw new FatalCrash(
                "Not found the spare of " + klass
            );
        }
    }

    /**
     * If {@link T} is a Bean, then perform a given
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
     * @throws FatalCrash           If no spare available for bean is found
     * @throws NullPointerException If the {@code bean} or {@code visitor} is null
     * @see Spare#flat(Object, Visitor)
     * @since 0.0.3
     */
    default <T> boolean flat(
        @NotNull T bean,
        @NotNull Visitor visitor
    ) {
        Class<T> klass = (Class<T>)
            bean.getClass();
        Spare<T> spare = lookup(klass);

        if (spare != null) {
            return spare.flat(
                bean, visitor
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + klass
            );
        }
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
     * @return the number of rows affected
     * @throws FatalCrash           If no spare available for bean is found
     * @throws NullPointerException If the specified parameters contains null
     * @since 0.0.4
     */
    default <T> int update(
        @NotNull T bean,
        @NotNull Spoiler spoiler
    ) {
        Class<T> klass = (Class<T>)
            bean.getClass();
        Spare<T> spare = lookup(klass);

        if (spare != null) {
            return spare.update(
                bean, spoiler, this
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + klass
            );
        }
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
     * @return the number of rows affected
     * @throws SQLException         If a database access error occurs
     * @throws FatalCrash           If no spare available for bean is found
     * @throws NullPointerException If the specified parameters contains null
     * @since 0.0.4
     */
    default <T> int update(
        @NotNull T bean,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        Class<T> klass = (Class<T>)
            bean.getClass();
        Spare<T> spare = lookup(klass);

        if (spare != null) {
            return spare.update(
                bean, this, resultSet
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + klass
            );
        }
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
     * @return the number of rows affected
     * @throws FatalCrash           If no spare available for beans is found
     * @throws NullPointerException If the specified parameters contains null
     * @see Spare#update(Object, Spoiler, Supplier)
     * @since 0.0.4
     */
    default <S, T> int mutate(
        @NotNull S source,
        @NotNull T target
    ) {
        Spare<S> spare0 = lookup(
            (Class<S>) source.getClass()
        );
        if (spare0 == null) {
            throw new FatalCrash(
                "Not found the spare of " + source
            );
        }

        Spoiler spoiler =
            spare0.flat(source);
        if (spoiler == null) {
            throw new FatalCrash(
                "The spoiler is null"
            );
        }

        Spare<T> spare = lookup(
            (Class<T>) target.getClass()
        );

        if (spare != null) {
            return spare.update(
                target, spoiler, this
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + target
            );
        }
    }

    /**
     * Resolves the Kat {@link Event} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   Type type = ...
     *   User user = supplier.read(type, event);
     * }</pre>
     *
     * @param type  the specified type for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for type is found
     * @throws ClassCastException   If {@link T} is not an instance of {@code type}
     * @throws NullPointerException If the specified {@code type} or {@code event} is null
     * @see Supplier#solve(Type, Algo, Event)
     */
    @NotNull
    default <T> T read(
        @NotNull Type type,
        @NotNull Event<T> event
    ) {
        return solve(
            type, Algo.KAT, event
        );
    }

    /**
     * Resolves the Kat {@link Event} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   Class<User> clazz = ...
     *   User user = supplier.read(clazz, event);
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
     * Serializes to {@link Kat}
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
        return write(value, 0);
    }

    /**
     * Serializes to {@link Kat}
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
        Chan chan = new Kat(
            flags, this
        );
        chan.set(null, value);
        return chan;
    }

    /**
     * Resolves the Doc {@link Event} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   Type type = ...
     *   User user = supplier.down(type, event);
     * }</pre>
     *
     * @param type  the specified type for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for type is found
     * @throws NullPointerException If the specified {@code type} or {@code event} is null
     * @see Supplier#solve(Type, Algo, Event)
     */
    @NotNull
    default <T> T down(
        @NotNull Type type,
        @NotNull Event<T> event
    ) {
        return solve(
            type, Algo.DOC, event
        );
    }

    /**
     * Resolves the Doc {@link Event} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   Class<User> clazz = ...
     *   User user = supplier.down(clazz, event);
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
     * Serializes to {@link Doc}
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
    default Chan mark(
        @Nullable Object value
    ) throws IOException {
        return mark(value, 0);
    }

    /**
     * Serializes to {@link Doc}
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
    default Chan mark(
        @Nullable Object value, long flags
    ) throws IOException {
        Chan chan = new Doc(
            flags, this
        );
        chan.set(null, value);
        return chan;
    }

    /**
     * Resolves the Json {@link Event} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   Type type = ...
     *   User user = supplier.parse(type, event);
     * }</pre>
     *
     * @param type  the specified type for lookup
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no spare available for type is found
     * @throws NullPointerException If the specified {@code type} {@code event} is null
     * @see Supplier#solve(Type, Algo, Event)
     */
    @NotNull
    default <T> T parse(
        @NotNull Type type,
        @NotNull Event<T> event
    ) {
        return solve(
            type, Algo.JSON, event
        );
    }

    /**
     * Resolves the Json {@link Event} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   Class<User> clazz = ...
     *   User user = supplier.parse(clazz, event);
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
     * Serializes to {@link Json}
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
    default Chan serial(
        @Nullable Object value
    ) throws IOException {
        return serial(value, 0);
    }

    /**
     * Serializes to {@link Json}
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
    default Chan serial(
        @Nullable Object value, long flags
    ) throws IOException {
        Chan chan = new Json(
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
                return new Kat(
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
     * Resolves the {@link Event} and converts the result to {@link T}
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
     *         Flag.VALUE_AS_BEAN
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
        Spare<T> spare = lookup(type);

        if (spare != null) {
            event.with(this);
            return spare.solve(
                algo, event.setup(type)
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + type
            );
        }
    }

    /**
     * Resolves the {@link Event} and converts the result to {@link T}
     *
     * <pre>{@code
     *   Event<User> event = ...
     *   Supplier supplier = ...
     *
     *   Algo algo = ...
     *   Space type = new Space("plus.kat.entity.User");
     *
     *   User user = supplier.solve(
     *      type, algo, event.with(
     *         Flag.VALUE_AS_BEAN
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
     * @since 0.0.6
     */
    @NotNull
    default <T> T solve(
        @NotNull Space klass,
        @NotNull Algo algo,
        @NotNull Event<T> event
    ) {
        Spare<T> spare = lookup(klass);

        if (spare != null) {
            return spare.solve(
                algo, event.with(this)
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + klass
            );
        }
    }

    /**
     * Resolves the {@link Event} and converts the result to {@link T}
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
     *         Flag.VALUE_AS_BEAN
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
        @NotNull String klass,
        @NotNull Algo algo,
        @NotNull Event<T> event
    ) {
        Spare<T> spare = lookup(klass);

        if (spare != null) {
            return spare.solve(
                algo, event.with(this)
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + klass
            );
        }
    }

    /**
     * Resolves the {@link Event} and converts the result to {@link T}
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
     *         Flag.VALUE_AS_BEAN
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

        if (spare != null) {
            event.with(this);
            return spare.solve(
                algo, event.setup(klass)
            );
        } else {
            throw new FatalCrash(
                "Not found the spare of " + klass
            );
        }
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    @SuppressWarnings("rawtypes")
    class Sample implements Supplier, Provider {
        /**
         * default supplier
         */
        static Supplier INS;

        /**
         * default providers
         */
        static Provider[] PRO;

        static {
            Sample sample;
            INS = sample = new Sample(
                Config.get(
                    "kat.supplier.buffer", 32
                ),
                Config.get(
                    "kat.supplier.capacity", 64
                )
            );

            sample.minor.put("", ObjectSpare.INSTANCE);
            sample.minor.put("$", ObjectSpare.INSTANCE);
            sample.minor.put("s", StringSpare.INSTANCE);
            sample.minor.put("b", BooleanSpare.INSTANCE);
            sample.minor.put("i", IntegerSpare.INSTANCE);
            sample.minor.put("l", LongSpare.INSTANCE);
            sample.minor.put("f", FloatSpare.INSTANCE);
            sample.minor.put("d", DoubleSpare.INSTANCE);
            sample.minor.put("c", CharSpare.INSTANCE);
            sample.minor.put("o", ByteSpare.INSTANCE);
            sample.minor.put("u", ShortSpare.INSTANCE);
            sample.minor.put("n", NumberSpare.INSTANCE);
            sample.minor.put("M", MapSpare.INSTANCE);
            sample.minor.put("A", ArraySpare.INSTANCE);
            sample.minor.put("L", ListSpare.INSTANCE);
            sample.minor.put("S", SetSpare.INSTANCE);
            sample.minor.put("B", ByteArraySpare.INSTANCE);
            sample.minor.put("I", BigIntegerSpare.INSTANCE);
            sample.minor.put("D", BigDecimalSpare.INSTANCE);

            sample.major.put(String.class, StringSpare.INSTANCE);
            sample.major.put(int.class, IntegerSpare.INSTANCE);
            sample.major.put(Integer.class, IntegerSpare.INSTANCE);
            sample.major.put(long.class, LongSpare.INSTANCE);
            sample.major.put(Long.class, LongSpare.INSTANCE);
            sample.major.put(float.class, FloatSpare.INSTANCE);
            sample.major.put(Float.class, FloatSpare.INSTANCE);
            sample.major.put(double.class, DoubleSpare.INSTANCE);
            sample.major.put(Double.class, DoubleSpare.INSTANCE);
            sample.major.put(boolean.class, BooleanSpare.INSTANCE);
            sample.major.put(Boolean.class, BooleanSpare.INSTANCE);
            sample.major.put(byte.class, ByteSpare.INSTANCE);
            sample.major.put(Byte.class, ByteSpare.INSTANCE);
            sample.major.put(short.class, ShortSpare.INSTANCE);
            sample.major.put(Short.class, ShortSpare.INSTANCE);
            sample.major.put(Map.class, MapSpare.INSTANCE);
            sample.major.put(Set.class, SetSpare.INSTANCE);
            sample.major.put(List.class, ListSpare.INSTANCE);
            sample.major.put(void.class, VoidSpare.INSTANCE);
            sample.major.put(Void.class, VoidSpare.INSTANCE);
            sample.major.put(char.class, CharSpare.INSTANCE);
            sample.major.put(Character.class, CharSpare.INSTANCE);
            sample.major.put(Number.class, NumberSpare.INSTANCE);
            sample.major.put(Object.class, ObjectSpare.INSTANCE);
            sample.major.put(byte[].class, ByteArraySpare.INSTANCE);
            sample.major.put(Object[].class, ArraySpare.INSTANCE);
            sample.major.put(BigInteger.class, BigIntegerSpare.INSTANCE);
            sample.major.put(BigDecimal.class, BigDecimalSpare.INSTANCE);

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
                    PS[i++] = sample;
                    while (loader.hasNext()) {
                        PS[i++] = loader.next();
                    }

                    int k = i = 1;
                    for (; i < size; i++) {
                        Provider P = PS[i];
                        try {
                            if (P.alive(sample)) {
                                PS[k++] = P;
                            }
                        } catch (Exception e) {
                            throw new Error(
                                "Failed to activate " + P, e
                            );
                        }
                    }

                    if (k != i) {
                        Provider[] RS = new Provider[k];
                        System.arraycopy(
                            PS, 0, PS = RS, 0, k
                        );
                    }

                    if (k != 1) {
                        Arrays.sort(
                            PS, Collections.reverseOrder()
                        );
                    }
                    PRO = PS;
                } else {
                    PRO = new Provider[]{sample};
                }
            } catch (Exception e) {
                throw new Error(
                    "Failed to load the external providers", e
                );
            }
        }

        /**
         * the minor internal mapping table
         */
        protected final Map<Object, Spare<?>> minor;

        /**
         * the major internal mapping table
         */
        protected final Map<Object, Spare<?>> major;

        /**
         * Constructs a supplier which has a service provider
         *
         * @param buffer   the init capacity of minor mapping table
         * @param capacity the init capacity of major mapping table
         */
        public Sample(
            int buffer, int capacity
        ) {
            minor = new ConcurrentHashMap<>(buffer);
            major = new ConcurrentHashMap<>(capacity);
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
            return major.put(klass, spare);
        }

        @Override
        public Spare<?> revoke(
            @NotNull Class<?> klass,
            @Nullable Spare<?> spare
        ) {
            if (spare == null) {
                return major.remove(klass);
            }
            return major.remove(
                klass, spare) ? spare : null;
        }

        @Override
        public Spare<?> embed(
            @NotNull String klass,
            @NotNull Spare<?> spare
        ) {
            return minor.put(klass, spare);
        }

        @Override
        public Spare<?> revoke(
            @NotNull String klass,
            @Nullable Spare<?> spare
        ) {
            if (spare == null) {
                return minor.remove(klass);
            }
            return minor.remove(
                klass, spare) ? spare : null;
        }

        @Override
        public <T> Spare<T> lookup(
            @NotNull Type type
        ) {
            Spare<?> spare = major.get(type);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            Provider[] PS = PRO;
            if (PS != null) {
                for (Provider p : PS) {
                    try {
                        spare = p.search(
                            type, this
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
            @NotNull Class<T> klass
        ) {
            Spare<?> spare = major.get(klass);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            Provider[] PS = PRO;
            if (PS != null) {
                for (Provider p : PS) {
                    try {
                        spare = p.search(
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
            @NotNull Space klass
        ) {
            Spare<?> spare = minor.get(klass);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            Provider[] PS = PRO;
            if (PS != null) {
                for (Provider p : PS) {
                    try {
                        spare = p.search(
                            klass, null, this
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
            @NotNull String klass
        ) {
            Spare<?> spare = minor.get(klass);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            Provider[] PS = PRO;
            if (PS != null) {
                for (Provider p : PS) {
                    try {
                        spare = p.search(
                            klass, Object.class, this
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
            @NotNull Space name,
            @NotNull Class<T> klass
        ) {
            Spare<?> spare = major.get(klass);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            Provider[] PS = PRO;
            if (PS != null) {
                for (Provider p : PS) {
                    try {
                        spare = p.search(
                            name, klass, this
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
        public <K, T extends K> Spare<T> lookup(
            @NotNull String klass,
            @NotNull Class<K> parent
        ) {
            Spare<?> spare = minor.get(klass);

            if (spare != null) {
                if (parent.isAssignableFrom(
                    spare.getType()
                )) {
                    return (Spare<T>) spare;
                }
                return null;
            }

            if (parent != null) {
                Provider[] PS = PRO;
                if (PS != null) {
                    for (Provider p : PS) {
                        try {
                            spare = p.search(
                                klass, parent, this
                            );
                        } catch (Collapse e) {
                            return null;
                        }

                        if (spare != null) {
                            return (Spare<T>) spare;
                        }
                    }
                }
            } else {
                throw new NullPointerException(
                    "Method #lookup(String, Class) receives null type"
                );
            }

            return null;
        }

        @Override
        public Spare<?> search(
            @NotNull Type type,
            @NotNull Supplier supplier
        ) {
            if (type instanceof Class) {
                return lookup(
                    (Class<?>) type
                );
            }

            if (type instanceof ParameterizedType) {
                return search(
                    ((ParameterizedType) type).getRawType(), supplier
                );
            }

            if (type instanceof Space) {
                return lookup(
                    (Space) type, Object.class
                );
            }

            if (type instanceof WildcardType) {
                return search(
                    ((WildcardType) type).getUpperBounds()[0], supplier
                );
            }

            if (type instanceof GenericArrayType) {
                GenericArrayType g = (GenericArrayType) type;
                Class<?> cls = Space.wipe(
                    g.getGenericComponentType()
                );
                if (cls != null) {
                    if (cls == Object.class) {
                        return lookup(Object[].class);
                    }
                    return lookup(
                        Array.newInstance(cls, 0).getClass()
                    );
                }
            }

            if (type != null) {
                return null;
            } else {
                throw new NullPointerException(
                    "Method #lookup(Type, Supplier) receives null type"
                );
            }
        }

        @Override
        public Spare<?> search(
            @NotNull Class<?> klass,
            @NotNull Supplier supplier
        ) {
            String name = klass.getName();
            switch (name.charAt(0)) {
                case 'j': {
                    if (name.startsWith("java.")) {
                        return sought(
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
                    return new ArraySpare(klass).join(this);
                }
            }

            Embed embed = klass.getAnnotation(Embed.class);
            if (embed == null) {
                if (klass.isInterface() ||
                    Coder.class.isAssignableFrom(klass) ||
                    Entity.class.isAssignableFrom(klass) ||
                    Throwable.class.isAssignableFrom(klass)) {
                    return null;
                }
                if (Chain.class.isAssignableFrom(klass)) {
                    if (klass == Chain.class) {
                        return ChainSpare.INSTANCE.join(this);
                    } else {
                        return new ChainSpare(klass).join(this);
                    }
                }
            } else {
                Class<?> clazz = embed.with();
                if (clazz != Spare.class) {
                    if (!Spare.class.
                        isAssignableFrom(clazz)) {
                        Spare<?> spare = lookup(clazz);
                        if (spare != null) {
                            major.putIfAbsent(
                                klass, spare
                            );
                        }
                        return spare;
                    }

                    try {
                        // double-checking
                        Spare<?> spare = major.get(klass);

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
                                    args[i] = lookup(m).apply();
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
                        return ((Spare<?>)
                            c.newInstance(args)
                        ).join(this);
                    } catch (Exception e) {
                        throw new FatalCrash(
                            "Failed to build the '"
                                + klass + "' coder: " + clazz, e
                        );
                    }
                }

                if (klass.isInterface()) {
                    return new ProxySpare(embed, klass, this).join(this);
                }
            }

            Spare<?> spare;
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

            return spare.join(this);
        }

        @Override
        public Spare<?> search(
            @NotNull Space name,
            @Nullable Class<?> parent,
            @NotNull Supplier supplier
        ) {
            if (parent != null) {
                Spare<?> spare = search(
                    parent, supplier
                );

                if (spare != null) {
                    return spare;
                }

                spare = minor.get(name);
                if (spare != null) {
                    if (parent.isAssignableFrom(
                        spare.getType())) {
                        return spare;
                    }
                    return null;
                }

                return search(
                    name.toString(), parent, supplier
                );
            }
            return null;
        }

        @Override
        public Spare<?> search(
            @NotNull String name,
            @Nullable Class<?> parent,
            @NotNull Supplier supplier
        ) {
            if (parent != null) {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread()
                        .getContextClassLoader();
                } catch (Throwable e) {
                    // Cannot access thread ClassLoader
                }

                if (cl == null) {
                    try {
                        cl = parent.getClassLoader();
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

                if (parent.isAssignableFrom(child)) {
                    Spare<?> spare = lookup(child);
                    if (spare != null) {
                        minor.putIfAbsent(
                            name, spare
                        );
                    }
                    return spare;
                }
            }

            return null;
        }

        @Nullable
        protected <T> Spare<T> sought(
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
                    major.put(klass, spare);
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

                    major.put(klass, spare);
                    return (Spare<T>) spare;
                }
                // java.lang
                case 'l': {
                    if (klass == Class.class) {
                        spare = ClassSpare.INSTANCE;
                    } else if (klass == Iterable.class) {
                        spare = ListSpare.INSTANCE;
                    } else if (klass == CharSequence.class) {
                        spare = StringSpare.INSTANCE;
                    } else if (klass == StringBuffer.class) {
                        spare = StringBufferSpare.INSTANCE;
                    } else if (klass == StringBuilder.class) {
                        spare = StringBuilderSpare.INSTANCE;
                    } else {
                        return null;
                    }

                    major.put(klass, spare);
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
                            } else if (klass == Currency.class) {
                                spare = CurrencySpare.INSTANCE;
                            } else if (klass == Locale.class) {
                                spare = LocaleSpare.INSTANCE;
                            } else if (klass == Queue.class) {
                                spare = lookup(LinkedList.class);
                            } else if (klass == Deque.class) {
                                spare = lookup(LinkedList.class);
                            } else if (klass == Collection.class) {
                                spare = ListSpare.INSTANCE;
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

                            major.put(klass, spare);
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

                            major.put(klass, spare);
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

                            major.put(klass, spare);
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
            return "plus.kat.Supplier.Sample@"
                + Integer.toHexString(
                System.identityHashCode(this)
            );
        }
    }
}
