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
package plus.kat.entity;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public abstract class Builder<K> {

    private Alias alias;
    private Builder<?> parent;

    /**
     * flag etc.
     */
    protected Flag flag;
    protected Supplier supplier;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    final void onAttach(
        @NotNull Alias a,
        @NotNull Event<?> e,
        @NotNull Builder<?> b
    ) throws Crash, IOCrash {
        if (parent == null) {
            alias = a;
            parent = b;
            flag = e.getFlag();
            supplier = e.getSupplier();
            onCreate(a);
        } else {
            throw new UnexpectedCrash(
                "Unexpectedly, this Builder is already working"
            );
        }
    }

    /**
     * @throws IOCrash If an I/O error occurs
     */
    public abstract void onCreate(
        @NotNull Alias alias
    ) throws Crash, IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    public abstract void onAccept(
        @NotNull Alias alias,
        @NotNull Builder<?> child
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    public abstract void onAccept(
        @NotNull Space space,
        @NotNull Alias alias,
        @NotNull Value value
    ) throws IOCrash;

    /**
     * Create a branch of this {@link Builder}
     */
    @Nullable
    public abstract Builder<?> getBuilder(
        @NotNull Space space,
        @NotNull Alias alias
    ) throws IOCrash;

    /**
     * Returns the result of building {@link K}
     * <p>
     * May be called multiple times,
     * when implementing this method, make sure that the {@link K} returned each time is the same
     */
    @Nullable
    public abstract K getResult();

    /**
     * Close the resources of this {@link Builder}
     */
    public abstract void onDestroy();

    /**
     * Returns the alias of this {@link Builder}
     */
    @Nullable
    public final Alias getAlias() {
        return alias;
    }

    /**
     * Returns the parent of this {@link Builder}
     */
    @Nullable
    public final Builder<?> getParent() {
        return parent;
    }

    /**
     * Destroy the resources of this {@link Builder}
     */
    final void onDetach() {
        onDestroy();
        alias = null;
        flag = null;
        parent = null;
        supplier = null;
    }
}
