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

import plus.kat.chain.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Pipage {
    /**
     * Opens a sub pipage of this pipage and returns the sub pipage
     *
     * @return the sub pipage, may be null
     * @throws IOException If an I/O error occurs
     */
    @Nullable
    Pipage onOpen(
        @NotNull Space space,
        @NotNull Alias alias
    ) throws IOException;

    /**
     * Reads the spare, alias and value from the solver
     *
     * @throws IOException If an I/O error occurs
     */
    void onEmit(
        @NotNull Space space,
        @NotNull Alias alias,
        @NotNull Value value
    ) throws IOException;

    /**
     * Closes the transport of this pipage and returns the parent pipage
     *
     * @return the parent pipage, may be null
     * @throws IOException If an I/O error occurs
     */
    @Nullable
    Pipage onClose(
        @NotNull boolean state,
        @NotNull boolean alarm
    ) throws IOException;
}