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

import plus.kat.anno.*;
import plus.kat.crash.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Bucket {
    /**
     * Shares the specified old buffer array to bucket
     *
     * @param old the specified array that will be shared
     * @return {@code true} if successful
     */
    boolean join(
        @NotNull byte[] old
    );

    /**
     * Releases the old buffer array and returns small buffer array
     *
     * @param old the specified array that will be released
     */
    @NotNull
    byte[] swap(
        @NotNull byte[] old
    );

    /**
     * Requests for a buffer array of the specified minimum size and
     * copy it from the old buffer array, and then recycles the old buffer array
     *
     * @param old  the specified array that will be released
     * @param len  the specified length of old buffer array
     * @param size the specified minimum size of buffer array
     * @throws FatalCrash If a fatal error occurs in the process
     */
    @NotNull
    byte[] alloc(
        @NotNull byte[] old, int len, int size
    );
}
