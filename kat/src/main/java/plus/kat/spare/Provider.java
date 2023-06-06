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

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.2
 */
public interface Provider extends Comparable<Provider> {
    /**
     * Prepared for the specified context
     *
     * @param o the specified context to process
     * @return true, indicating to remain active as a provider
     */
    default boolean alive(
        @NotNull Context o
    ) {
        return true;
    }

    /**
     * Returns the level of this provider
     */
    default int grade() {
        return 0;
    }

    /**
     * Returns the result of the comparison
     *
     * @see Provider#grade()
     */
    @Override
    default int compareTo(
        @NotNull Provider o
    ) {
        return Integer.compare(
            grade(), o.grade()
        );
    }

    /**
     * Returns the {@link Spare} of the specified type
     *
     * @throws NullPointerException  If the specified argument is null
     * @throws IllegalStateException If the specified {@code type} is disabled
     */
    @Nullable
    default Spare<?> search(
        @NotNull Type type,
        @NotNull Context context
    ) {
        if (type != null &&
            context != null) {
            return null;
        }
        throw new NullPointerException(
            "Received arguments contains null"
        );
    }

    /**
     * Returns the {@link Spare} of the specified type with the name and context
     *
     * @throws NullPointerException  If the specified argument is null
     * @throws IllegalStateException If the specified {@code type} is disabled
     */
    @Nullable
    default Spare<?> search(
        @NotNull Type type,
        @NotNull Space name,
        @NotNull Context context
    ) {
        if (name != null &&
            type != null &&
            context != null) {
            return search(
                type, context
            );
        }
        throw new NullPointerException(
            "Received arguments contains null"
        );
    }
}
