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

import plus.kat.anno.Nullable;

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.2
 */
public interface Param {
    /**
     * Returns the {@link Class} of {@link Param}
     */
    @Nullable
    Class<?> getKlass();

    /**
     * Returns the index of {@link Param}
     */
    default int index() {
        return 0;
    }

    /**
     * Returns the {@link Type} of {@link Param}
     */
    @Nullable
    default Type getType() {
        return getKlass();
    }

    /**
     * Returns the {@link Coder} of {@link Param}
     */
    @Nullable
    default Coder<?> getCoder() {
        return null;
    }
}
