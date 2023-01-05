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
import plus.kat.crash.*;

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.2
 */
public interface Provider extends Comparable<Provider> {
    /**
     * Loads spares for the specified supplier
     *
     * @param o the specified supplier to be loaded
     * @return true, indicating to remain active as a provider
     */
    default boolean alive(
        @NotNull Supplier o
    ) {
        return true;
    }

    /**
     * Returns the level of this provider
     *
     * @see #compareTo(Provider)
     */
    default int grade() {
        return 0;
    }

    /**
     * Returns the result of the comparison
     *
     * @see Comparable#compareTo(Object)
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
     * Returns the {@link Spare} of the specified {@code type}
     *
     * @throws Collapse             If this provider signals to interrupt subsequent lookup
     * @throws NullPointerException If the specified {@code type} or {@code supplier} is null
     */
    @Nullable
    default Spare<?> search(
        @NotNull Type type,
        @NotNull Supplier supplier
    ) {
        return null;
    }

    /**
     * Returns the {@link Spare} of the specified {@code klass}
     *
     * @throws Collapse             If this provider signals to interrupt subsequent lookup
     * @throws NullPointerException If the specified {@code klass} or {@code supplier} is null
     */
    @Nullable
    default Spare<?> search(
        @NotNull Class<?> klass,
        @NotNull Supplier supplier
    ) {
        return null;
    }

    /**
     * Returns the {@link Spare} of the specified {@code name} and {@code type}
     *
     * @throws Collapse             If this provider signals to interrupt subsequent lookup
     * @throws NullPointerException If the specified {@code name} or {@code supplier} is null
     */
    @Nullable
    default Spare<?> search(
        @NotNull Space name,
        @Nullable Class<?> parent,
        @NotNull Supplier supplier
    ) {
        return parent == null ? null : search(parent, supplier);
    }

    /**
     * Returns the {@link Spare} of the specified {@code name} and {@code type}
     *
     * @throws Collapse             If this provider signals to interrupt subsequent lookup
     * @throws NullPointerException If the specified {@code name} or {@code supplier} is null
     */
    @Nullable
    default Spare<?> search(
        @NotNull String name,
        @Nullable Class<?> parent,
        @NotNull Supplier supplier
    ) {
        return parent == null ? null : search(parent, supplier);
    }
}
