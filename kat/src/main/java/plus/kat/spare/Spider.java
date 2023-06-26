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

import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.6
 */
public interface Spider {
    /**
     * Opens a child of this spider and returns the child
     *
     * @return the child spider, may be null
     * @throws IOException           If a read error occurs
     * @throws IllegalStateException If a fatal error occurs
     */
    @Nullable
    Spider onOpen(
        @NotNull Alias alias,
        @NotNull Space space
    ) throws IOException;

    /**
     * Uses the alias, spare and value to update property
     *
     * @throws IOException           If a read error occurs
     * @throws IllegalStateException If a fatal error occurs
     */
    void onEach(
        @NotNull Alias alias,
        @NotNull Space space,
        @NotNull Value value
    ) throws IOException;

    /**
     * Closes the transport of this spider and returns the parent
     *
     * @return the parent spider, may be null
     * @throws IOException           If a read error occurs
     * @throws IllegalStateException If a fatal error occurs
     */
    @Nullable
    Spider onClose(
        @NotNull boolean alert,
        @NotNull boolean state
    ) throws IOException;
}
