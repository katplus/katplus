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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.spare.*;

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.6
 */
public interface Sensor {
    /**
     * Gets the value of this property
     *
     * @return the value of property
     * @throws IllegalArgumentException If failed to call this method
     */
    @Nullable
    Object apply(
        @NotNull Object bean
    );

    /**
     * Sets the new value of this property
     *
     * @return true if successful otherwise false
     * @throws IllegalArgumentException If failed to call this method
     */
    boolean accept(
        @NotNull Object bean,
        @Nullable Object value
    );

    /**
     * Returns the type of this property
     *
     * @return the generic type of property
     * @throws IllegalStateException If failed to call this method
     */
    @NotNull
    default Type getType() {
        throw new IllegalStateException(
            "Failed to call Sensor#getType"
        );
    }

    /**
     * Returns the coder of this property
     *
     * @return the custom coder of property
     * @throws IllegalStateException If failed to call this method
     */
    @Nullable
    default Coder<?> getCoder() {
        throw new IllegalStateException(
            "Failed to call Sensor#getCoder"
        );
    }
}
