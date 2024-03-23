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

import lombok.*;

import plus.kat.*;
import plus.kat.actor.*;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.6
 */
@Getter
public abstract class Factory implements Pipe, Flag {

    protected Factory parent;
    protected Context context;

    /**
     * Prepares this {@link Factory} before parsing
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
     * Receives the value of the current property
     *
     * @param value the value of the current property
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    public void onNext(
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
     * Prepares the context for this factory
     *
     * @return this or the proxy pipe
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    @NotNull
    public Pipe onOpen(
        @NotNull Factory parent,
        @NotNull Context context
    ) throws IOException {
        if (this.parent == null) {
            this.parent = parent;
            this.context = context;
        } else {
            throw new IOException(
                this + " is already working"
            );
        }
        onOpen();
        return this;
    }

    /**
     * Returns the type of factory to build
     */
    @Nullable
    public abstract Type getType();

    /**
     * Use this factory to resolve generic type
     * and replace type variables as much as possible
     *
     * @param generic the specified generic type
     * @throws IllegalArgumentException If the generic is illegal
     */
    @Nullable
    public abstract Type getType(Type generic);
}
