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

import plus.kat.actor.Nilable;
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
     * Check to see if this used as the hot
     * load source for the specified context
     *
     * @return true if as the hot load source
     */
    default boolean alive(
        @NotNull Context o
    ) {
        return true;
    }

    /**
     * Returns the priority of this provider
     */
    default int grade() {
        return 0;
    }

    /**
     * Returns the result of their comparison
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
     * @throws NullPointerException  If the specified type or context is null
     * @throws IllegalStateException If the specified type is disabled or otherwise
     */
    @Nullable
    default Spare<?> search(
        @NotNull Type type,
        @NotNull Context context
    ) {
        return search(
            type, null, context
        );
    }

    /**
     * Returns the {@link Spare} of the specified type with the name
     *
     * @throws NullPointerException  If the specified type or context is null
     * @throws IllegalStateException If the specified type is disabled or otherwise
     */
    @Nullable
    default Spare<?> search(
        @NotNull Type type,
        @Nilable Space name,
        @NotNull Context context
    ) {
        if (type != null &&
            context != null) {
            return null;
        }
        throw new NullPointerException(
            "Received: (" + type + ", "
                + name + ", " + context + ")"
        );
    }
}
