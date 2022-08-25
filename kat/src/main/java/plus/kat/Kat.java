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

import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.spare.*;

import java.io.IOException;

import static plus.kat.Plan.DEF;
import static plus.kat.Supplier.Impl.INS;

/**
 * @author kraity
 * @since 0.0.1
 */
@FunctionalInterface
public interface Kat {
    /**
     * Returns the space of {@link Kat}
     *
     * @return {@link CharSequence}
     * @see Chan#set(CharSequence, CharSequence, Kat)
     */
    @NotNull
    default CharSequence space() {
        return Space.$M;
    }

    /**
     * Serializes this {@link Kat} at the current hierarchy
     *
     * @throws IOException If an I/O error occurs
     * @see Chan#set(CharSequence, CharSequence, Kat)
     */
    void coding(
        @NotNull Chan chan
    ) throws IOException;

    /**
     * Serialize to pretty {@link Kat} String
     *
     * @param value specify serialized value
     */
    @NotNull
    static String pretty(
        @Nullable Object value
    ) {
        return encode(
            null, value, Flag.PRETTY | DEF.writeFlags
        );
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
        return encode(
            null, value, DEF.writeFlags
        );
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
        return encode(
            null, value, flags
        );
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
        return encode(
            alias, value, DEF.writeFlags
        );
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
        Chan chan = new Chan(flags);
        try {
            chan.set(
                alias, value
            );
        } catch (Exception e) {
            // Nothing
        }
        return chan.toString();
    }

    /**
     * Parse {@link Kat} {@link CharSequence}
     *
     * @param text specify the {@code text} to be parsed
     * @throws SolverCrash If parsing fails
     * @see Spare#solve(Job, Event)
     */
    @Nullable
    static Object decode(
        @Nullable CharSequence text
    ) {
        if (text == null) {
            return null;
        }
        return ObjectSpare.INSTANCE.solve(
            Job.KAT, new Event<>(text)
        );
    }

    /**
     * Parse {@link Kat} {@link CharSequence}
     *
     * @param event specify the {@code event} to be handled
     * @throws SolverCrash If parsing fails
     * @see Spare#solve(Job, Event)
     */
    @Nullable
    static <T> T decode(
        @Nullable Event<T> event
    ) {
        if (event == null) {
            return null;
        }
        return ObjectSpare.INSTANCE.solve(
            Job.KAT, event
        );
    }

    /**
     * Parse {@link Kat} byte array
     *
     * @param text specify the {@code text} to be parsed
     * @throws SolverCrash If parsing fails
     * @see Supplier#read(Class, Event)
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
     * @throws SolverCrash If parsing fails
     * @see Supplier#read(Class, Event)
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
     * @throws SolverCrash If parsing fails
     * @see Supplier#read(Class, Event)
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
