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
package plus.kat;

import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.6
 */
public interface Pipe {
    /**
     * Opens a child of this pipe and returns the child
     *
     * @param alias the alias of the current property
     * @param space the space of the current property
     * @return the child pipeline, may be null
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    @Nullable
    Pipe onOpen(
        @NotNull Alias alias,
        @NotNull Space space
    ) throws IOException;

    /**
     * Receives the alias, spare and value in a loop
     *
     * @param alias the alias of the current property
     * @param space the space of the current property
     * @param value the value of the current property
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    void onNext(
        @NotNull Alias alias,
        @NotNull Space space,
        @NotNull Value value
    ) throws IOException;

    /**
     * Closes the transport of this pipe and returns the parent
     *
     * @param alert the flag that can be thrown errors
     * @param state the flag that was successfully built
     * @return the parent pipeline, may be null
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    @Nullable
    Pipe onClose(
        @NotNull boolean alert,
        @NotNull boolean state
    ) throws IOException;
}
