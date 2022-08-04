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

import plus.kat.spare.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@FunctionalInterface
public interface Setter<K, V> extends Target {
    /**
     * @param it  the entity
     * @param val the value of entity
     */
    void accept(
        @NotNull K it,
        @Nullable V val
    );

    /**
     * @param it  the entity
     * @param val the value of entity
     */
    @SuppressWarnings("unchecked")
    default void onAccept(
        @NotNull K it,
        @Nullable Object val
    ) {
        try {
            accept(
                it, (V) val
            );
        } catch (Exception e) {
            // nothing
        }
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
    @Override
    default Coder<?> getCoder() {
        return null;
    }
}
