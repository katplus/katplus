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
package plus.kat.solver;

import plus.kat.anno.NotNull;

import plus.kat.*;
import plus.kat.crash.*;
import plus.kat.stream.*;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Solver extends Closeable {
    /**
     * Returns the algo of this {@link Solver}
     *
     * @return {@link Algo} and not null
     */
    Algo algo();

    /**
     * {@link Reader} as the data source and {@link Pipage} as the data pipeline
     *
     * @param pipage the specified data transfer pipeline
     * @param reader the specified data source to be parsed
     * @throws FlowCrash   If an I/O error occurs by reader
     * @throws SolverCrash If an I/O error occurs by solver
     */
    void read(
        @NotNull Reader reader,
        @NotNull Pipage pipage
    ) throws IOException;

    /**
     * Clears this {@link Solver}
     */
    default void clear() {
        // Nothing
    }
}
