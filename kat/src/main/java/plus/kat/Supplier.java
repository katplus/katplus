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
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.reflex.*;
import plus.kat.utils.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

import static plus.kat.Plan.DEF;
import static plus.kat.Supplier.Impl.INS;
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
     * @param klass the specified klass
     * @param spare the specified spare to be embedded
     * @return the previous {@link Spare} or {@code null}
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
     * @param klass the specified klass
     * @return the previous {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
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
     * @param klass the specified klass
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
     * @param klass the specified klass
     * @return the previous {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Impl#revoke(CharSequence)
     */
    @Nullable
    Spare<?> revoke(
        @NotNull CharSequence klass
    );

    /**
     * Returns the coder of {@code element}, and applies
     * for a coder if the {@code element} needs it, otherwise returns null
     *
     * <pre>{@code
     *   Expose expose = ...
     *   Target element = ...
     *
     *   Supplier supplier = ...
     *   Coder<User> coder = supplier.assign(expose, element);
     * }</pre>
     *
     * @param expose  the specified expose
     * @param element the specified target
     * @return the coder of {@code klass} or {@code null}
     * @see Target
     * @see Impl#assign(Expose, Target)
     * @since 0.0.4
     */
    @Nullable <T>
    Coder<T> assign(
        @Nullable Expose expose,
        @Nullable Target element
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
     * @param klass the specified klass
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
     * }</pre>
     *
     * @param klass the specified klass
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
     * @param type the specified type
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
     * @param type the specified type
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
     * Returns the {@link Spare} of {@code type}.
     * Find the class of the type as the parent
     * class, and then look for an alternative to the klass
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Type type = User.class;
     *  Spare<UserVO> spare = supplier.search(type, "plus.kat.entity.UserVO");
     * }</pre>
     *
     * @param type the specified parent type
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code klass} is null
     * @see Spare#accept(Class)
     * @see Impl#search(Type, CharSequence)
     * @since 0.0.4
     */
    @Nullable <T>
    Spare<T> search(
        @Nullable Type type,
        @Nullable CharSequence klass
    );

    /**
     * Returns the {@link Spare} of {@code klass}
     * and then you actively call {@link Spare#accept(Class)} to check.
     * <p>
     * If not cached first through the custom {@link Provider} set to look up,
     * if not, then use {@link Class#forName(String, boolean, ClassLoader)}
     * to find and judge whether it is a subclass of {@code type} and then find its {@link Spare}.
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Class<User> type = User.class;
     *  Spare<UserVO> spare = supplier.search(type, "plus.kat.entity.UserVO");
     * }</pre>
     *
     * @param type the specified parent class
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
     * <pre>{@code
     *  Supplier supplier = ...
     *  Class<User> clazz = ...
     *  User user = supplier.apply(
     *     clazz, supplier.flat(bean)
     *  );
     * }</pre>
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
     * @param data the specified data to be converted
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
     * @param data the specified data to be converted
     * @return {@link E} or {@code null}
     * @throws ClassCastException If {@link E} is not an instance of {@code klass}
     * @see Spare#cast(Object, Supplier)
     */
    @Nullable
    default <E> E cast(
        @NotNull CharSequence klass,
        @NotNull Object data
    ) {
        Spare<E> spare = search(
            Object.class, klass
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
     * @param event the specified event to be handled
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
     * @param event the specified event to be handled
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
     * @param event the specified event to be handled
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
     * @param event the specified event to be handled
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
     * @param event the specified event to be handled
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
     * @param event the specified event to be handled
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
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code job} or {@code event} is null
     * @see Spare#solve(Job, Event)
     * @since 0.0.4
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <T> T solve(
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        Spare<T> spare = (Spare<T>)
            event.getSpare();

        if (spare != null) {
            event.prepare(this);
            return spare.solve(
                job, event
            );
        } else {
            Type type = event.getType();
            if (type != null) {
                return solve(
                    type, job, event
                );
            } else {
                return solve(
                    Object.class, job, event
                );
            }
        }
    }

    /**
     * Parse {@link Event} and convert result to {@link T}
     *
     * @param event the specified event to be handled
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
        Spare<T> spare = search(
            Object.class, klass
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
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws NullPointerException If the specified {@code klass} or {@code event} is null
     * @see Spare#solve(Job, Event)
     * @since 0.0.2
     */
    @NotNull
    default <T> T solve(
        @NotNull Type type,
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        Spare<T> spare = lookup(type, null);

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
     * @param event the specified event to be handled
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
     * @author kraity
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
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
        public <T> Coder<T> assign(
            @Nullable Expose expose,
            @Nullable Target target
        ) {
            Class<?> clazz;
            if (target == null) {
                return null;
            }

            if (expose == null || (clazz =
                expose.with()) == Coder.class) {
                Format format = target
                    .getAnnotation(Format.class);
                if (format == null) {
                    return null;
                }

                Spare<?> spare = null;
                Class<?> klass = target.getType();

                if (klass == Date.class) {
                    spare = new DateSpare(format);
                } else if (klass == Instant.class) {
                    spare = new InstantSpare(format);
                } else if (klass == LocalDate.class) {
                    spare = new LocalDateSpare(format);
                } else if (klass == LocalTime.class) {
                    spare = new LocalTimeSpare(format);
                } else if (klass == LocalDateTime.class) {
                    spare = new LocalDateTimeSpare(format);
                } else if (klass == ZonedDateTime.class) {
                    spare = new ZonedDateTimeSpare(format);
                }
                return (Coder<T>) spare;
            }

            // coder of klass
            Object coder = null;
            Cluster cluster = Cluster.INS;

            // pointing to clazz?
            if (!Coder.class.isAssignableFrom(clazz)) {
                coder = cluster.load(
                    clazz, this
                );
            }

            // byte[]
            else if (clazz == ByteArrayCoder.class) {
                coder = ByteArrayCoder.INSTANCE;
            } else try {
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
                    args = Reflect.EMPTY;
                } else {
                    args = new Object[size];
                    Class<?>[] cls =
                        c.getParameterTypes();
                    for (int i = 0; i < size; i++) {
                        Class<?> m = cls[i];
                        if (m == Class.class) {
                            args[i] = target.getType();
                        } else if (m == Type.class) {
                            args[i] = target.getRawType();
                        } else if (m == Expose.class) {
                            args[i] = expose;
                        } else if (m == Supplier.class) {
                            args[i] = this;
                        } else if (m == Provider.class) {
                            args[i] = cluster;
                        } else if (m.isAnnotation()) {
                            args[i] = target.getAnnotation(
                                (Class<? extends Annotation>) m
                            );
                        }
                    }
                }

                if (!c.isAccessible()) {
                    c.setAccessible(true);
                }
                coder = c.newInstance(args);
            } catch (Exception e) {
                // Nothing
            }

            return (Coder<T>) coder;
        }

        @Override
        public <T> Spare<T> lookup(
            @NotNull Class<T> klass
        ) {
            return Cluster.INS.load(klass, this);
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
            if (type == null) {
                return search(
                    null, klass
                );
            }

            if (type instanceof Class) {
                Class<T> clazz;
                Spare<T> spare = lookup(
                    clazz = (Class<T>) type
                );
                if (spare != null ||
                    klass == null) {
                    return spare;
                }

                return search(clazz, klass);
            }

            if (type instanceof Space) {
                Space s = (Space) type;
                type = s.getType();
                if (type == null) {
                    return lookup(s);
                }
                return lookup(type, klass);
            }

            if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) type;
                return lookup(
                    p.getRawType(), klass
                );
            }

            if (type instanceof TypeVariable) {
                TypeVariable<?> v = (TypeVariable<?>) type;
                return lookup(
                    v.getBounds()[0], klass
                );
            }

            if (type instanceof WildcardType) {
                WildcardType w = (WildcardType) type;
                type = w.getUpperBounds()[0];
                if (type == Object.class) {
                    Type[] bounds = w.getLowerBounds();
                    if (bounds.length != 0) {
                        type = bounds[0];
                    }
                }
                return lookup(type, klass);
            }

            if (type instanceof ArrayType) {
                return (Spare<T>) lookup(
                    Object[].class
                );
            }

            if (type instanceof GenericArrayType) {
                return (Spare<T>) lookup(
                    Object[].class
                );
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
            @Nullable Type type,
            @Nullable CharSequence klass
        ) {
            if (type == null) {
                return search(
                    null, klass
                );
            }

            if (type instanceof Class) {
                return search(
                    (Class<T>) type, klass
                );
            }

            if (type instanceof Space) {
                Space s = (Space) type;
                type = s.getType();
                if (type == null) {
                    return lookup(s);
                }
                return search(type, klass);
            }

            if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) type;
                return search(
                    p.getRawType(), klass
                );
            }

            if (type instanceof TypeVariable) {
                TypeVariable<?> v = (TypeVariable<?>) type;
                return search(
                    v.getBounds()[0], klass
                );
            }

            if (type instanceof WildcardType) {
                WildcardType w = (WildcardType) type;
                type = w.getUpperBounds()[0];
                if (type == Object.class) {
                    Type[] bounds = w.getLowerBounds();
                    if (bounds.length != 0) {
                        type = bounds[0];
                    }
                }
                return search(type, klass);
            }

            if (type instanceof ArrayType) {
                return (Spare<T>) lookup(
                    Object[].class
                );
            }

            if (type instanceof GenericArrayType) {
                return (Spare<T>) lookup(
                    Object[].class
                );
            }

            return null;
        }

        @Override
        public <K, T extends K> Spare<T> search(
            @Nullable Class<K> type,
            @Nullable CharSequence klass
        ) {
            if (klass == null) {
                return null;
            }

            Spare<?> spare = get(klass);
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

            String name = klass.toString();
            for (Provider p : Cluster.PRO) {
                try {
                    spare = p.lookup(
                        type, name, this
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

            if (type != null) {
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

                if (type.isAssignableFrom(child)) {
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
    }
}
