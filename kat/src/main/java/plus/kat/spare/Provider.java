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
public interface Provider {
    /**
     * Returns {@link Spare} of the specified {@code klass}
     *
     * @throws RunCrash             The Provider signals to interrupt subsequent lookup
     * @throws NullPointerException If the specified {@code klass} is null
     */
    @Nullable
    Spare<?> lookup(
        @NotNull Class<?> klass,
        @NotNull Supplier supplier
    );

    /**
     * Returns {@link Spare} of the specified {@code klass}
     *
     * @throws RunCrash             The Provider signals to interrupt subsequent lookup
     * @throws NullPointerException If the specified {@code klass} is null
     * @since 0.0.3
     */
    @Nullable
    default Spare<?> lookup(
        @NotNull String klass,
        @NotNull Supplier supplier
    ) {
        return null;
    }
}
