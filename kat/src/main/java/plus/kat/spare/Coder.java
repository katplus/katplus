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
package plus.kat.spare;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Coder<T> {
    /**
     * Returns the space of {@link T}
     *
     * @see Spare#getSpace()
     */
    @NotNull
    default String getSpace() {
        return "$";
    }

    /**
     * Returns the flag of {@link T}
     *
     * @see Spare#getFlag()
     */
    @Nullable
    default Boolean getFlag() {
        return null;
    }

    /**
     * Returns the border of {@link T}
     *
     * @see Spare#getBorder(Flag)
     */
    @Nullable
    default Boolean getBorder(
        @NotNull Flag flag
    ) {
        return null;
    }

    /**
     * Create a {@link Builder} of {@link T}
     *
     * @see Spare#getBuilder(Type)
     */
    @Nullable
    default Builder<? extends T> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    /**
     * Reads the {@link Chain} as {@link T}
     *
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the flag or alias is null
     */
    @Nullable
    default T read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) throws IOException {
        throw new IOException(
            "Parsing bean not implemented"
        );
    }

    /**
     * Reads the {@link Value} as {@link T}
     *
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the flag or value is null
     */
    @Nullable
    default T read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        return read(
            flag, (Chain) value
        );
    }

    /**
     * Writes the {@code value} to {@link Chan}
     *
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the chan or value is null
     */
    default void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        throw new IOException(
            "Serializing bean is not implemented"
        );
    }

    /**
     * Writes the {@code value} to {@link Flow}
     *
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the flow or value is null
     */
    default void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        throw new IOException(
            "Serializing bean is not implemented"
        );
    }
}
