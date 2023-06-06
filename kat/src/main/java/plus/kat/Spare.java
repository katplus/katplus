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

import plus.kat.actor.*;
import plus.kat.chain.*;
import plus.kat.spare.*;

import java.io.IOException;
import java.lang.reflect.Type;

import static plus.kat.spare.Parser.*;
import static plus.kat.Supplier.Vendor.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Spare<T> extends Coder<T> {
    /**
     * Returns the space of {@link T}
     *
     * <pre>{@code
     *  null  ->  the fully qualified name
     *  void  ->  the blank space name
     *  space ->  the specified space name
     * }</pre>
     */
    @Nullable
    String getSpace();

    /**
     * Returns the scope of {@link T}
     *
     * <pre>{@code
     *  null  ->  the generic is the meta type
     *  true  ->  the generic is the map type
     *  false ->  the generic is the list type
     * }</pre>
     */
    @Nullable
    Boolean getScope();

    /**
     * Returns the {@link Class} of {@link T}.
     * Normally, it's T.lass or the closest parent
     */
    @NotNull
    Class<T> getType();

    /**
     * Returns the {@link Context} of {@link T}.
     * Normally, this spare is loaded by the context
     */
    @NotNull
    Context getContext();

    /**
     * Returns the {@link Border} of {@link T}
     *
     * @param flag the specified {@link Flag}
     */
    @Nullable
    Border getBorder(
        @NotNull Flag flag
    );

    /**
     * Returns the {@link Factory} of {@link T}
     *
     * @param type the specified actual {@link Type}
     */
    @Nullable
    Factory getFactory(
        @Nullable Type type
    );

    /**
     * If this space can create an instance,
     * otherwise it will return {@code null}
     *
     * @return {@link T} or {@code null}
     * @throws IllegalStateException If a build error occurs
     * @since 0.0.3
     */
    @Nullable
    default T apply() {
        return null;
    }

    /**
     * If this space can create an instance of this
     * subclass type, otherwise will throw exception
     *
     * @param type the specified actual subclass type
     * @return {@link T} or {@code null}
     * @throws IllegalStateException If failed to create an instance, or
     *                               this type is not a subtype of {@link T}
     * @since 0.0.4
     */
    @Nullable
    default T apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == getType()) {
            return apply();
        }

        throw new IllegalStateException(
            "Failed to build this " + type
        );
    }

    /**
     * Resolves the kat text and converts the result to {@link T}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   Spare<User> spare = ...
     *   User user = spare.read(flow);
     * }</pre>
     *
     * @param flow the specified flow to be resolved
     * @throws NullPointerException  If the specified {@code flow} is null
     * @throws IllegalStateException If an I/O error or parsing error occurs
     */
    @Nullable
    default T read(
        @NotNull Flow flow
    ) {
        return read(
            null, flow
        );
    }

    /**
     * Resolves the kat text and converts the result to {@link T}
     *
     * <pre>{@code
     *   Type type = ...
     *   Flow flow = ...
     *
     *   Spare<User> spare = ...
     *   User user = spare.read(type, flow);
     * }</pre>
     *
     * @param type the specified type of {@link T}
     * @param flow the specified flow to be resolved
     * @throws NullPointerException  If the specified {@code flow} is null
     * @throws IllegalStateException If an I/O error or parsing error occurs
     */
    @Nullable
    default T read(
        @Nilable Type type,
        @NotNull Flow flow
    ) {
        try (Parser op = with(this)) {
            op.setType(type);
            return op.read(flow);
        }
    }

    /**
     * Serializes the specified {@link T} to kat stream
     *
     * <pre>{@code
     *   User user = ...
     *   Spare<User> spare = ...
     *
     *   try (Chan chan = spare.write(user)) {
     *       byte[] b = chan.toBinary();
     *       String s = chan.toString();
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
        return write(value, 0);
    }

    /**
     * Serializes the specified {@link T} to kat stream with the flags
     *
     * <pre>{@code
     *   User user = ...
     *   long flags = ...
     *
     *   Spare<User> spare = ...
     *   try (Chan chan = spare.write(user, flags)) {
     *       byte[] b = chan.toBinary();
     *       String s = chan.toString();
     *   }
     * }</pre>
     *
     * @param value the specified value to serialized
     * @param flags the specified flags for serialization
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan write(
        @Nullable T value, long flags
    ) throws IOException {
        Chan chan = new Kat(
            flags, getContext()
        );
        try {
            chan.set(
                null, this, value
            );
        } catch (Throwable alas) {
            try {
                chan.close();
            } catch (Throwable e) {
                alas.addSuppressed(e);
            }
            throw alas;
        }
        return chan;
    }

    /**
     * Resolves the xml text and converts the result to {@link T}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   Spare<User> spare = ...
     *   User user = spare.down(flow);
     * }</pre>
     *
     * @param flow the specified flow to be resolved
     * @throws NullPointerException  If the specified {@code flow} is null
     * @throws IllegalStateException If an I/O error or parsing error occurs
     */
    @Nullable
    default T down(
        @NotNull Flow flow
    ) {
        return down(
            null, flow
        );
    }

    /**
     * Resolves the xml text and converts the result to {@link T}
     *
     * <pre>{@code
     *   Type type = ...
     *   Flow flow = ...
     *
     *   Spare<User> spare = ...
     *   User user = spare.down(type, flow);
     * }</pre>
     *
     * @param type the specified type of {@link T}
     * @param flow the specified flow to be resolved
     * @throws NullPointerException  If the specified {@code flow} is null
     * @throws IllegalStateException If an I/O error or parsing error occurs
     */
    @Nullable
    default T down(
        @Nilable Type type,
        @NotNull Flow flow
    ) {
        try (Parser op = with(this)) {
            op.setType(type);
            return op.down(flow);
        }
    }

    /**
     * Serializes the specified {@link T} to xml stream
     *
     * <pre>{@code
     *   User user = ...
     *   Spare<User> spare = ...
     *
     *   try (Chan chan = spare.mark(user)) {
     *       byte[] b = chan.toBinary();
     *       String s = chan.toString();
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
        return mark(value, 0);
    }

    /**
     * Serializes the specified {@link T} to xml stream with the flags
     *
     * <pre>{@code
     *   User user = ...
     *   long flags = ...
     *
     *   Spare<User> spare = ...
     *   try (Chan chan = spare.mark(user, flags)) {
     *       byte[] b = chan.toBinary();
     *       String s = chan.toString();
     *   }
     * }</pre>
     *
     * @param value the specified value to serialized
     * @param flags the specified flags for serialization
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan mark(
        @Nullable T value, long flags
    ) throws IOException {
        Chan chan = new Doc(
            flags, getContext()
        );
        try {
            chan.set(
                null, this, value
            );
        } catch (Throwable alas) {
            try {
                chan.close();
            } catch (Throwable e) {
                alas.addSuppressed(e);
            }
            throw alas;
        }
        return chan;
    }

    /**
     * Resolves the json text and converts the result to {@link T}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   Spare<User> spare = ...
     *   User user = spare.parse(flow);
     * }</pre>
     *
     * @param flow the specified flow to be resolved
     * @throws NullPointerException  If the specified {@code flow} is null
     * @throws IllegalStateException If an I/O error or parsing error occurs
     */
    @Nullable
    default T parse(
        @NotNull Flow flow
    ) {
        return parse(
            null, flow
        );
    }

    /**
     * Resolves the json text and converts the result to {@link T}
     *
     * <pre>{@code
     *   Type type = ...
     *   Flow flow = ...
     *
     *   Spare<User> spare = ...
     *   User user = spare.parse(type, flow);
     * }</pre>
     *
     * @param type the specified type of {@link T}
     * @param flow the specified flow to be resolved
     * @throws NullPointerException  If the specified {@code flow} is null
     * @throws IllegalStateException If an I/O error or parsing error occurs
     */
    @Nullable
    default T parse(
        @Nilable Type type,
        @NotNull Flow flow
    ) {
        try (Parser op = with(this)) {
            op.setType(type);
            return op.parse(flow);
        }
    }

    /**
     * Serializes the specified {@link T} to json stream
     *
     * <pre>{@code
     *   User user = ...
     *   Spare<User> spare = ...
     *
     *   try (Chan chan = spare.serial(user)) {
     *       byte[] b = chan.toBinary();
     *       String s = chan.toString();
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
        return serial(value, 0);
    }

    /**
     * Serializes the specified {@link T} to json stream with the flags
     *
     * <pre>{@code
     *   User user = ...
     *   long flags = ...
     *
     *   Spare<User> spare = ...
     *   try (Chan chan = spare.serial(user, flags)) {
     *       byte[] b = chan.toBinary();
     *       String s = chan.toString();
     *   }
     * }</pre>
     *
     * @param value the specified value to serialized
     * @param flags the specified flags for serialization
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    default Chan serial(
        @Nullable T value, long flags
    ) throws IOException {
        Chan chan = new Json(
            flags, getContext()
        );
        try {
            chan.set(
                null, this, value
            );
        } catch (Throwable alas) {
            try {
                chan.close();
            } catch (Throwable e) {
                alas.addSuppressed(e);
            }
            throw alas;
        }
        return chan;
    }

    /**
     * Returns the {@link Spare} of the {@code type} from the default context
     *
     * <pre>{@code
     *  Type type = ...
     *  Spare<User> spare = Spare.of(type);
     * }</pre>
     *
     * @param type the specified type for lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     * @since 0.0.6
     */
    @Nullable
    static <T> Spare<T> of(
        @NotNull Type type
    ) {
        return INS.assign(type);
    }

    /**
     * Returns the {@link Spare} of the {@code type} from the default context
     *
     * <pre>{@code
     *  Class<User> type = User.class;
     *  Spare<User> spare = Spare.of(type);
     * }</pre>
     *
     * @param type the specified type for lookup
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code type} is null
     * @since 0.0.6
     */
    @Nullable
    static <T> Spare<T> of(
        @NotNull Class<T> type
    ) {
        return INS.assign(type);
    }

    /**
     * Returns the {@link Spare} of the {@code type} from the default context
     *
     * <pre>{@code
     *  Type type = ...
     *  Space name = ...
     *  Spare<User> spare = Spare.of(type, name);
     * }</pre>
     *
     * @param type the specified parent type
     * @param name the specified actual name
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code params} is null
     * @since 0.0.6
     */
    @Nullable
    static <T> Spare<T> of(
        @NotNull Type type,
        @NotNull Space name
    ) {
        return INS.assign(type, name);
    }

    /**
     * Returns the {@link Spare} of the {@code type} from the default context
     *
     * <pre>{@code
     *  Space name = ...
     *  Class<User> type = User.class;
     *  Spare<User> spare = Spare.of(name, type);
     * }</pre>
     *
     * @param name the specified actual name
     * @param type the specified parent type
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException If the specified {@code params} is null
     * @since 0.0.6
     */
    @Nullable
    static <T> Spare<T> of(
        @NotNull Space name,
        @NotNull Class<T> type
    ) {
        return INS.assign(type, name);
    }
}
