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
import plus.kat.crash.*;

/**
 * @author kraity
 * @since 0.0.2
 */
public interface Provider extends Comparable<Provider> {
    /**
     * Returns true to indicate that this
     * provider has been used as a loader for a long time
     *
     * @param supplier the specified supplier to be loaded
     * @see Provider#lookup(Class, Supplier)
     * @see Provider#search(Class, String, Supplier)
     * @since 0.0.4
     */
    default boolean accept(
        @NotNull Supplier supplier
    ) {
        return true;
    }

    /**
     * Returns the level of this provider
     *
     * @see Provider#compareTo(Provider)
     * @since 0.0.4
     */
    default int grade() {
        return 0;
    }

    /**
     * Returns the result of the comparison
     *
     * @see Comparable#compareTo(Object)
     * @since 0.0.4
     */
    @Override
    default int compareTo(
        @NotNull Provider o
    ) {
        int m = grade();
        int n = o.grade();

        if (m == n) {
            return 0;
        }
        return m > n ? 1 : -1;
    }

    /**
     * Returns the {@link Spare} of the specified {@code klass}
     *
     * @throws RuntimeException     If this provider signals to interrupt subsequent lookup
     * @throws NullPointerException If the specified {@code name} or {@code supplier} is null
     */
    @Nullable
    default Spare<?> lookup(
        @NotNull Class<?> klass,
        @NotNull Supplier supplier
    ) {
        return null;
    }

    /**
     * Returns the {@link Spare} of the specified {@code name} and {@code type}
     *
     * @throws RuntimeException     If this provider signals to interrupt subsequent search
     * @throws NullPointerException If the specified {@code name} or {@code supplier} is null
     * @see Supplier#lookup(Class, CharSequence)
     * @see Supplier#search(Class, CharSequence)
     * @since 0.0.4
     */
    @Nullable
    default Spare<?> search(
        @Nullable Class<?> type,
        @NotNull String name,
        @NotNull Supplier supplier
    ) {
        return null;
    }
}
