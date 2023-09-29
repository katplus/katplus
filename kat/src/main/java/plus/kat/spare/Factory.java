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

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.6
 */
public abstract class Factory implements Spider, Helper {

    protected Factory holder;
    protected Context context;

    /**
     * Prepare this {@link Factory} before parsing
     *
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    public void onOpen()
        throws IOException {
        // Do nothing by default
    }

    /**
     * Uses the received value to update property
     *
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    public void onEach(
        @Nullable Object value
    ) throws IOException {
        // Do nothing by default
    }

    /**
     * Closes the resources for this {@link Factory}
     *
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    public void onClose()
        throws IOException {
        // Do nothing by default
    }

    /**
     * Initializes the context for this factory
     *
     * @return this or the proxy spider
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    @NotNull
    public Spider init(
        @NotNull Factory parent,
        @NotNull Context context
    ) throws IOException {
        if (holder == null) {
            this.holder = parent;
            this.context = context;
        } else {
            throw new IOException(
                this + " is already working," +
                    " and its parent is " + holder
            );
        }
        onOpen();
        return this;
    }
}
