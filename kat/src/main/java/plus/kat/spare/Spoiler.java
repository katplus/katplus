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
     * Returns {@code true} if the spoiler has more elements.
     * In other words, returns {@code true} if {@link #getValue} would
     * return the value of current element rather than throwing an exception
     *
     * <pre>{@code
     *   Spoiler s0 = ...
     *   while (s0.hasNext()) {
     *       String k = s0.getKey();
     *       Object v = s0.getValue();
     *       // then next step
     *   }
     *
     *   Spoiler s1 = ...
     *   while (s1.hasNext()) {
     *       String k = s1.getKey();
     *       if (check k) {
     *           Object v = s1.getValue();
     *           // then next step
     *       }
     *   }
     * }</pre>
     *
     * @return {@code true} if the spoiler has more elements
     */
    boolean hasNext();

    /**
     * Returns the key of current element.
     * Can only be called at most once each iteration
     *
     * <pre>{@code
     *   Spoiler spoiler = ...
     *   while (spoiler.hasNext()) {
     *       String k1 = spoiler.getKey();
     *       String k2 = spoiler.getKey();
     *       // k2 equals k1, the k2 may be the result of repeated calculations
     *   }
     * }</pre>
     *
     * @see Spoiler#hasNext()
     */
    @NotNull
    String getKey();

    /**
     * Returns the value of current element.
     * Can only be called at most once each iteration
     *
     * <pre>{@code
     *   Spoiler spoiler = ...
     *   while (spoiler.hasNext()) {
     *       Object v1 = spoiler.getValue();
     *       Object v2 = spoiler.getValue();
     *       // v2 equals v1, the v2 may be the result of repeated calculations
     *   }
     * }</pre>
     *
     * @see Spoiler#hasNext()
     */
    @Nullable
    Object getValue();

    /**
     * Returns a spoiler of the {@code map}
     *
     * @param map the  specified map
     * @throws NullPointerException If the {@code map} is null
     */
    @NotNull
    static Spoiler of(
        @NotNull Map<?, ?> map
    ) {
        return new MapSpare.Spoiler0(map);
    }
}
