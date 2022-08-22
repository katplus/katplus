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

import plus.kat.chain.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Pipe {
    /**
     * Notify to create a receiver
     *
     * @throws IOException If an I/O error occurs
     */
    boolean attach(
        @NotNull Space space,
        @NotNull Alias alias
    ) throws IOException;

    /**
     * Sends data to the current receiver
     *
     * @throws IOException If an I/O error occurs
     */
    void accept(
        @NotNull Space space,
        @NotNull Alias alias,
        @NotNull Value value
    ) throws IOException;

    /**
     * Notify the current receiver to end the transmission
     *
     * @throws IOException If an I/O error occurs
     */
    boolean detach() throws IOException;
}
