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

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public abstract class Builder<K> implements Flag {

    private Alias alias;
    private Builder<?> parent;

    /**
     * event etc.
     */
    protected Event<?> event;
    protected Supplier supplier;

    /**
     * Check if this use the {@code flag}
     *
     * @param flag the specified {@code flag}
     */
    @Override
    public boolean isFlag(
        long flag
    ) {
        return event.isFlag(flag);
    }

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
            event = e;
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
     * Returns the {@link Type} object of the
     * current attribute declaration type, it can be variable, not {@link K}
     *
     * @see Coder#read(Flag, Value)
     * @see Worker.Builder$#onAccept(Space, Value, Target)
     * @since 0.0.3
     */
    @Nullable
    @Override
    public Type getType() {
        return null;
    }

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
        event = null;
        parent = null;
        supplier = null;
    }
}
