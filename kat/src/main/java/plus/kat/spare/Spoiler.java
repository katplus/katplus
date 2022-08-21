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

import java.util.Map;

/**
 * @author kraity
 * @since 0.0.3
 */
public interface Spoiler {
    /**
     * Returns {@code true} if the {@link Spoiler} has more elements
     */
    boolean hasNext();

    /**
     * Returns the current key corresponding to this {@link Spoiler}
     */
    @NotNull
    String getKey();

    /**
     * Returns the current value corresponding to this {@link Spoiler}
     */
    @Nullable
    Object getValue();

    /**
     * Returns a visitor of the {@code map}
     *
     * @param map the  specified map
     * @throws NullPointerException If the {@code map} is null
     */
    static Spoiler of(
        @NotNull Map<?, ?> map
    ) {
        return new MapSpare.Spoiler0(map);
    }
}
