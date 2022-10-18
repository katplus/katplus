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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public abstract class Builder<K> {

    protected Event<?> event;
    private Builder<?> parent;
    protected Supplier supplier;

    /**
     * @throws IOException If an I/O error occurs
     */
    final void onAttach(
        @NotNull Event<?> e,
        @NotNull Builder<?> b
    ) throws IOException {
        if (parent == null) {
            event = e;
            parent = b;
            supplier = e.getSupplier();
            onCreate();
        } else {
            throw new ProxyCrash(
                "Unexpectedly, this Builder is already working"
            );
        }
    }

    /**
     * Prepare before parsing
     *
     * @throws Collapse    If it signals to skip
     * @throws IOException If an I/O error occurs
     */
    public abstract void onCreate() throws IOException;

    /**
     * Receive the property of {@link K}
     *
     * @throws IOException If an I/O error occurs
     */
    public abstract void onAttain(@NotNull Space space, @NotNull Alias alias, @NotNull Value value) throws IOException;

    /**
     * Receive the property of {@link K}
     *
     * @throws IOException If an I/O error occurs
     */
    public void onDetain(
        @NotNull Builder<?> child
    ) throws IOException {
        throw new ProxyCrash(
            "Supports for structures is not implemented"
        );
    }

    /**
     * Create a builder for the property {@link K}
     *
     * @throws IOException If an I/O error occurs
     */
    @Nullable
    public Builder<?> onAttain(
        @NotNull Space space,
        @NotNull Alias alias
    ) throws IOException {
        throw new ProxyCrash(
            "Supports for structures is not implemented"
        );
    }

    /**
     * Returns the result of building {@link K}
     * <p>
     * May be called multiple times,
     * when implementing this method, make sure that the {@link K} returned each time is the same
     *
     * @throws IOException If a packaging error or IO error
     */
    @Nullable
    public abstract K onPacket() throws IOException;

    /**
     * Close the resources of this {@link Builder}
     */
    public abstract void onDestroy() throws IOException;

    /**
     * Returns the parent of this {@link Builder}
     *
     * @return {@link Builder} or {@code null}
     */
    public final Builder<?> getParent() {
        return parent;
    }

    /**
     * Destroy the resources of this {@link Builder}
     */
    final void onDetach() throws IOException {
        onDestroy();
        event = null;
        parent = null;
        supplier = null;
    }
}
