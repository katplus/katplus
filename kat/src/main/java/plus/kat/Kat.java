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

import plus.kat.crash.*;
import plus.kat.spare.*;

import static plus.kat.Supplier.Impl.INS;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Kat {
    /**
     * Returns the space of {@link Kat}
     */
    @Nullable
    default CharSequence getSpace() {
        return null;
    }

    /**
     * Returns the flag of {@link Kat}
     */
    @Nullable
    default Boolean getFlag() {
        return Boolean.TRUE;
    }

    /**
     * Serialization if {@link #getSpace()} is not null
     *
     * @param chan the specified {@link Chan}
     * @throws IOCrash If an I/O error occurs
     */
    default void onCoding(
        @NotNull Chan chan
    ) throws IOCrash {
        // nothing
    }

    /**
     * Serialization if {@link #getSpace()} is not null
     *
     * @param page the specified {@link Flow}
     * @throws IOCrash If an I/O error occurs
     */
    default void onCoding(
        @NotNull Flow page
    ) throws IOCrash {
        // nothing
    }

    /**
     * Serialize to pretty {@link Kat} String
     *
     * @param value specify serialized value
     */
    @NotNull
    static String pretty(
        @Nullable Object value
    ) {
        return new Chan(value, Flag.PRETTY).toString();
    }

    /**
     * Serialize to {@link Kat} String
     *
     * @param value specify serialized value
     */
    @NotNull
    static String encode(
        @Nullable Object value
    ) {
        return new Chan(value).toString();
    }

    /**
     * Serialize to {@link Kat} String
     *
     * @param value specify serialized value
     */
    @NotNull
    static String encode(
        @Nullable Object value, long flags
    ) {
        return new Chan(value, flags).toString();
    }

    /**
     * Serialize to {@link Kat} String
     *
     * @param value specify serialized value
     */
    @NotNull
    static String encode(
        @Nullable CharSequence alias,
        @Nullable Object value
    ) {
        return new Chan(alias, value).toString();
    }

    /**
     * Serialize to {@link Kat} String
     *
     * @param value specify serialized value
     */
    @NotNull
    static String encode(
        @Nullable CharSequence alias,
        @Nullable Object value, long flags
    ) {
        return new Chan(alias, value, flags).toString();
    }

    /**
     * Parse {@link Kat} {@link CharSequence}
     *
     * @param text specify the {@code text} to be parsed
     */
    @Nullable
    static Object decode(
        @Nullable CharSequence text
    ) {
        if (text == null) {
            return null;
        }
        return Parser.solve(
            Job.KAT, new Event<>(text)
        );
    }

    /**
     * Parse {@link Kat} {@link CharSequence}
     *
     * @param event specify the {@code event} to be handled
     */
    @Nullable
    static <T> T decode(
        @Nullable Event<T> event
    ) {
        if (event == null) {
            return null;
        }
        return Parser.solve(
            Job.KAT, event
        );
    }

    /**
     * Parse {@link Kat} byte array
     *
     * @param text specify the {@code text} to be parsed
     */
    @Nullable
    static <T> T decode(
        @Nullable Class<T> klass,
        @Nullable byte[] text
    ) {
        if (text == null |
            klass == null) {
            return null;
        }

        return INS.read(
            klass, new Event<>(text)
        );
    }

    /**
     * Parse {@link Kat} {@link CharSequence}
     *
     * @param text specify the {@code text} to be parsed
     */
    @Nullable
    static <T> T decode(
        @Nullable Class<T> klass,
        @Nullable CharSequence text
    ) {
        if (text == null |
            klass == null) {
            return null;
        }

        return INS.read(
            klass, new Event<>(text)
        );
    }

    /**
     * Parse {@link Kat} {@link CharSequence}
     *
     * @param event specify the {@code event} to be handled
     */
    @Nullable
    static <E, T extends E> T decode(
        @Nullable Class<E> klass,
        @Nullable Event<T> event
    ) {
        if (klass == null ||
            event == null) {
            return null;
        }

        return INS.read(klass, event);
    }
}
