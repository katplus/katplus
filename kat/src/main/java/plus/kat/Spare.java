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
     * Normally, {@link T} or the closest parent
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
     * @throws IllegalStateException If not supported
     */
    @Nullable
    Border getBorder(
        @NotNull Flag flag
    );

    /**
     * Returns the {@link Factory} of {@link T}
     *
     * @param type the specified actual {@link Type}
     * @throws IllegalStateException If not supported
     */
    @Nullable
    Factory getFactory(
        @Nullable Type type
    );

    /**
     * Returns an object based on no arguments,
     * otherwise return null or throw an error directly
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
     * Returns an object based on the arguments,
     * otherwise return null or throw an error directly
     *
     * @param args the specified args of constructor
     * @return {@link T} or {@code null}
     * @throws NullPointerException  If the args is null
     * @throws IllegalStateException If a build error occurs
     * @since 0.0.6
     */
    @Nullable
    default T apply(
        @NotNull Object... args
    ) {
        if (args.length == 0) {
            return apply();
        }

        throw new IllegalStateException(
            "Non-subject is not supported"
        );
    }

    /**
     * Resolves the kat text and converts the result to {@link T}
     *
     * <pre>{@code
     *  Flow flow = ...
     *  Spare<User> spare = ...
     *  User user = spare.read(flow);
     * }</pre>
     *
     * @param text the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    default T read(
        @NotNull Flow text
    ) throws IOException {
        return read(
            null, text
        );
    }

    /**
     * Resolves the kat text and converts the result to {@link T}
     *
     * <pre>{@code
     *  Type type = ...
     *  Flow flow = ...
     *
     *  Spare<User> spare = ...
     *  User user = spare.read(type, flow);
     * }</pre>
     *
     * @param type the specified type of {@link T}
     * @param text the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    default T read(
        @Nilable Type type,
        @NotNull Flow text
    ) throws IOException {
        try (Parser op = with(this)) {
            op.setType(type);
            return op.read(text);
        }
    }

    /**
     * Serializes the specified {@link T} to kat stream
     *
     * <pre>{@code
     *  User user = ...
     *  Spare<User> spare = ...
     *
     *  try (Chan chan = spare.write(user)) {
     *      byte[] b = chan.toBinary();
     *      String s = chan.toString();
     *  }
     * }</pre>
     *
     * @param value the specified value to serialized
     * @throws IOException If an I/O error or analysis error occurs
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
     *  User user = ...
     *  long flags = ...
     *
     *  Spare<User> spare = ...
     *  try (Chan chan = spare.write(user, flags)) {
     *      byte[] b = chan.toBinary();
     *      String s = chan.toString();
     *  }
     * }</pre>
     *
     * @param value the specified value to serialized
     * @param flags the specified flags for serialization
     * @throws IOException If an I/O error or analysis error occurs
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
     *  Flow flow = ...
     *  Spare<User> spare = ...
     *  User user = spare.down(flow);
     * }</pre>
     *
     * @param text the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    default T down(
        @NotNull Flow text
    ) throws IOException {
        return down(
            null, text
        );
    }

    /**
     * Resolves the xml text and converts the result to {@link T}
     *
     * <pre>{@code
     *  Type type = ...
     *  Flow flow = ...
     *
     *  Spare<User> spare = ...
     *  User user = spare.down(type, flow);
     * }</pre>
     *
     * @param type the specified type of {@link T}
     * @param text the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    default T down(
        @Nilable Type type,
        @NotNull Flow text
    ) throws IOException {
        try (Parser op = with(this)) {
            op.setType(type);
            return op.down(text);
        }
    }

    /**
     * Serializes the specified {@link T} to xml stream
     *
     * <pre>{@code
     *  User user = ...
     *  Spare<User> spare = ...
     *
     *  try (Chan chan = spare.mark(user)) {
     *      byte[] b = chan.toBinary();
     *      String s = chan.toString();
     *  }
     * }</pre>
     *
     * @param value the specified value to serialized
     * @throws IOException If an I/O error or analysis error occurs
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
     *  User user = ...
     *  long flags = ...
     *
     *  Spare<User> spare = ...
     *  try (Chan chan = spare.mark(user, flags)) {
     *      byte[] b = chan.toBinary();
     *      String s = chan.toString();
     *  }
     * }</pre>
     *
     * @param value the specified value to serialized
     * @param flags the specified flags for serialization
     * @throws IOException If an I/O error or analysis error occurs
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
     *  Flow flow = ...
     *  Spare<User> spare = ...
     *  User user = spare.parse(flow);
     * }</pre>
     *
     * @param text the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    default T parse(
        @NotNull Flow text
    ) throws IOException {
        return parse(
            null, text
        );
    }

    /**
     * Resolves the json text and converts the result to {@link T}
     *
     * <pre>{@code
     *  Type type = ...
     *  Flow flow = ...
     *
     *  Spare<User> spare = ...
     *  User user = spare.parse(type, flow);
     * }</pre>
     *
     * @param type the specified type of {@link T}
     * @param text the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    default T parse(
        @Nilable Type type,
        @NotNull Flow text
    ) throws IOException {
        try (Parser op = with(this)) {
            op.setType(type);
            return op.parse(text);
        }
    }

    /**
     * Serializes the specified {@link T} to json stream
     *
     * <pre>{@code
     *  User user = ...
     *  Spare<User> spare = ...
     *
     *  try (Chan chan = spare.serial(user)) {
     *      byte[] b = chan.toBinary();
     *      String s = chan.toString();
     *  }
     * }</pre>
     *
     * @param value the specified value to serialized
     * @throws IOException If an I/O error or analysis error occurs
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
     *  User user = ...
     *  long flags = ...
     *
     *  Spare<User> spare = ...
     *  try (Chan chan = spare.serial(user, flags)) {
     *      byte[] b = chan.toBinary();
     *      String s = chan.toString();
     *  }
     * }</pre>
     *
     * @param value the specified value to serialized
     * @param flags the specified flags for serialization
     * @throws IOException If an I/O error or analysis error occurs
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
     * Search for the spare of the specified type from the default {@link Context}
     *
     * <pre>{@code
     *  Type type = ...
     *  Spare<User> spare = Spare.of(type);
     * }</pre>
     *
     * @param type the specified actual type
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException  If the specified type is null
     * @throws IllegalStateException If the specified type is disabled or otherwise
     * @since 0.0.6
     */
    @Nullable
    static <T> Spare<T> of(
        @NotNull Type type
    ) {
        return INS.assign(type);
    }

    /**
     * Search for the spare of the specified type from the default {@link Context}
     *
     * <pre>{@code
     *  Class<User> type = User.class;
     *  Spare<User> spare = Spare.of(type);
     * }</pre>
     *
     * @param type the specified actual type
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException  If the specified type is null
     * @throws IllegalStateException If the specified type is disabled or otherwise
     * @since 0.0.6
     */
    @Nullable
    static <T> Spare<T> of(
        @NotNull Class<T> type
    ) {
        return INS.assign(type);
    }

    /**
     * Search for the spare of the specified type from the default {@link Context}
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
     * @throws NullPointerException  If the specified type or name is null
     * @throws IllegalStateException If the specified type is disabled or otherwise
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
     * Search for the spare of the specified type from the default {@link Context}
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
     * @throws NullPointerException  If the specified type or name is null
     * @throws IllegalStateException If the specified type is disabled or otherwise
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
