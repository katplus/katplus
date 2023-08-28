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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

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
     */
    @Nullable
    default String getSpace() {
        return null;
    }

    /**
     * Returns the scope of {@link T}
     */
    @Nullable
    default Boolean getScope() {
        return null;
    }

    /**
     * Returns the {@link Border} of {@link T}
     *
     * @param flag the specified {@link Flag}
     * @throws IllegalStateException If not supported
     */
    @Nullable
    default Border getBorder(
        @NotNull Flag flag
    ) {
        return null;
    }

    /**
     * Returns the {@link Factory} of {@link T}
     *
     * @param type the specified actual {@link Type}
     * @throws IllegalStateException If not supported
     */
    @Nullable
    default Factory getFactory(
        @Nullable Type type
    ) {
        return null;
    }

    /**
     * Reads the {@link Alias} as {@link T}
     *
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the flag or alias is null
     */
    @Nullable
    default T read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) throws IOException {
        return read(
            flag, (Value) alias
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
        throw new IOException(
            "Failed to call `#read`"
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
            "Failed to call `#write`"
        );
    }

    /**
     * Writes the {@code value} to {@link Flux}
     *
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the flux or value is null
     */
    default void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        throw new IOException(
            "Failed to call `#write`"
        );
    }
}
