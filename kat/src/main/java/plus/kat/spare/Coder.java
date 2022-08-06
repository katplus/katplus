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
public interface Coder<K> {
    /**
     * Returns the space of {@link Coder}
     */
    @NotNull
    default CharSequence getSpace() {
        return Space.$;
    }

    /**
     * Returns the flag of {@link Coder}
     */
    @Nullable
    default Boolean getFlag() {
        return null;
    }

    /**
     * Create a {@link Builder} of {@link K}
     */
    @Nullable
    default Builder<? extends K> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    /**
     * @throws IOException If an I/O error occurs
     */
    @Nullable
    default K read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) throws IOException {
        return null;
    }

    /**
     * @throws IOException If an I/O error occurs
     */
    @Nullable
    default K read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        return null;
    }

    /**
     * @throws IOException If an I/O error occurs
     */
    default void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        // nothing
    }

    /**
     * @throws IOException If an I/O error occurs
     */
    default void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        // nothing
    }
}
