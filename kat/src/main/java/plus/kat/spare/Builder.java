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
import plus.kat.stream.*;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public abstract class Builder<T> implements Channel {

    protected Channel holder;
    protected Callback handler;

    protected Flag flag;
    protected Supplier supplier;

    /**
     * Initializes the flag and supplier for
     * this pipage from the parent, and so on
     *
     * @return the proxy pipage
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    public Pipage init(
        @NotNull Channel parent,
        @NotNull Callback callback
    ) throws IOException {
        if (holder == null) {
            holder = parent;
            handler = callback;
            flag = parent.flag();
            supplier = parent.supplier();
        } else {
            throw new IOException(
                this + " is already working," +
                    " and its parent is " + holder
            );
        }
        onOpen();
        return this;
    }

    /**
     * Starts a sub pipage of this pipage and returns the sub pipage
     *
     * @return the sub pipage, may be null
     * @throws IOException If an I/O error occurs
     */
    @Nullable
    public Pipage onOpen(
        @NotNull Space space,
        @NotNull Alias alias
    ) throws IOException {
        throw new IOException(
            "Parsing bean not implemented"
        );
    }

    /**
     * Prepare the {@link Builder} before parsing
     *
     * @throws IOException If an I/O error occurs
     */
    public abstract void onOpen() throws IOException;

    /**
     * Returns the result of building {@link T}.
     * <p>
     * Can be called multiple times, and when implementing
     * this method, the return {@link T} must be the same each time
     *
     * @throws IOException If a packaging error or IO error
     */
    @Nullable
    public abstract T build() throws IOException;

    /**
     * Closes the iteration of this {@link Builder}
     *
     * @throws IOException If an I/O error occurs
     */
    public abstract void onClose() throws IOException;

    /**
     * Closes the property update of this pipage and returns the parent pipage
     *
     * @return the parent pipage, may be null
     * @throws IOException If an I/O error occurs
     */
    @Override
    public Pipage onClose(
        @NotNull boolean state,
        @NotNull boolean alarm
    ) throws IOException {
        if (state) {
            handler.onEmit(
                this, build()
            );
        }
        try {
            onClose();
            return holder;
        } catch (
            Exception e
        ) {
            if (alarm) {
                throw e;
            }
            return holder;
        } finally {
            flag = null;
            holder = null;
            handler = null;
            supplier = null;
        }
    }

    /**
     * Resolves the unknown type with this helper,
     * substituting type variables as far as possible
     */
    @Override
    public Type locate(
        @NotNull Type unknown
    ) {
        return holder.locate(unknown);
    }

    /**
     * Returns the flag of this {@link Channel}
     *
     * @return {@link Flag} or {@code null}
     */
    @Nullable
    public final Flag flag() {
        return flag;
    }

    /**
     * Returns the parent of this {@link Channel}
     *
     * @return {@link Channel} or {@code null}
     */
    @Nullable
    public final Channel holder() {
        return holder;
    }

    /**
     * Returns the supplier of this {@link Channel}
     *
     * @return {@link Supplier} or {@code null}
     */
    @Nullable
    public final Supplier supplier() {
        return supplier;
    }
}
