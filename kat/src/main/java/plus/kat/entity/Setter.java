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

import plus.kat.crash.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@FunctionalInterface
public interface Setter<K, V> {
    /**
     * Sets the specified value to the bean
     *
     * @return {@code true} if successful otherwise {@code false}
     * @throws FatalCrash If the underlying method throws a fatal exception
     */
    boolean accept(
        @NotNull K bean,
        @Nullable V value
    );

    /**
     * Sets the specified value to the bean
     *
     * @return {@code true} if successful otherwise {@code false}
     * @throws FatalCrash If the underlying method throws a fatal exception
     * @see Setter#accept(Object, Object)
     */
    @SuppressWarnings("unchecked")
    default boolean invoke(
        @NotNull Object bean,
        @Nullable Object value
    ) {
        try {
            return accept(
                (K) bean, (V) value
            );
        } catch (ClassCastException e) {
            throw new FatalCrash(
                "Failed to cast", e
            );
        }
    }
}
