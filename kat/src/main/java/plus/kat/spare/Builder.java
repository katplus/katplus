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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public abstract class Builder<T> extends Factory implements Flag {
    /**
     * Starts a child of this spider and returns the child
     *
     * @return the child spider, may be null
     * @throws IOException           If a read error occurs
     * @throws IllegalStateException If a fatal error occurs
     */
    @Nullable
    public Spider onOpen(
        @NotNull Alias alias,
        @NotNull Space space
    ) throws IOException {
        throw new IOException(
            "Not support entity"
        );
    }

    /**
     * Returns the result of building {@link T}.
     * <p>
     * Can be called multiple times, and when implementing
     * this method, the return {@link T} must be the same each time
     *
     * @throws IllegalStateException If a fatal error occurs
     */
    @Nullable
    public abstract T build();

    /**
     * Closes the property update of this spider and returns the parent
     *
     * @return the parent spider, may be null
     * @throws IOException           If a read error occurs
     * @throws IllegalStateException If a fatal error occurs
     */
    @Override
    public Spider onClose(
        @NotNull boolean alert,
        @NotNull boolean state
    ) throws IOException {
        if (state) {
            holder.onEach(
                build()
            );
        }
        try {
            onClose();
            return holder;
        } catch (
            Exception e
        ) {
            if (alert) {
                throw e;
            }
            return holder;
        } finally {
            holder = null;
            context = null;
        }
    }
}
