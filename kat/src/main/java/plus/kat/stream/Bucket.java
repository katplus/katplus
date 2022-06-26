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
package plus.kat.stream;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Bucket {
    /**
     * @param it  the specified array that will be released
     * @param len the specified length of array
     * @param min the specified minimum size
     */
    @NotNull
    byte[] alloc(
        @NotNull byte[] it, int len, int min
    );

    /**
     * @param it the specified array that will be recycled
     */
    void push(
        @NotNull byte[] it
    );

    /**
     * @param it the specified array that will be released
     */
    @Nullable
    byte[] revert(
        @NotNull byte[] it
    );
}
