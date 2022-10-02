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

import plus.kat.*;
import plus.kat.stream.*;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Solver extends Firm, Closeable {
    /**
     * {@link Reader} as the data source and {@link Proxy} as the data consumer.
     * This {@link Solver} uses reader to read the source, translate it and then stream it to share.
     *
     * @param proxy  the specified data transfer pipeline
     * @param reader the specified data source to be parsed
     * @throws IOException Unexpected errors by {@link Proxy} or {@link Reader}
     */
    void read(
        @NotNull Proxy proxy,
        @NotNull Reader reader
    ) throws IOException;

    /**
     * Close this {@link Solver}
     */
    @Override
    void close();

    /**
     * Clear this {@link Solver}
     */
    default void clear() {
        // Nothing
    }
}
