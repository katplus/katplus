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
package plus.kat.kernel;

import plus.kat.anno.NotNull;

import plus.kat.crash.*;
import plus.kat.stream.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Solver {
    /**
     * @param pipe   specify the data transfer pipeline
     * @param reader specify the source of decoded data
     * @throws IOCrash Unexpected errors by {@link Pipe} or {@link Reader}
     */
    void read(
        @NotNull Pipe pipe,
        @NotNull Reader reader
    ) throws IOCrash;

    /**
     * close this {@link Solver}
     */
    void close();

    /**
     * clear this {@link Solver}
     */
    default void clear() {
        // nothing
    }
}
