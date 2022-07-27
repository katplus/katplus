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
package plus.kat.entity;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

/**
 * @author kraity
 * @since 0.0.1
 */
@FunctionalInterface
public interface Getter<K, V> extends Target {
    /**
     * @param it the entity
     */
    @Nullable
    V apply(
        @NotNull K it
    );

    /**
     * @param it the entity
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default V onApply(
        @NotNull Object it
    ) {
        return apply(
            (K) it
        );
    }

    /**
     * Returns the {@link Class} of {@link K}
     */
    @Nullable
    @Override
    default Class<?> getType() {
        return null;
    }

    /**
     * Returns the {@link Coder} of {@link K}
     */
    @Nullable
    default Coder<?> getCoder() {
        return null;
    }
}
