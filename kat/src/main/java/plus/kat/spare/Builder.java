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
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public abstract class Builder<T> extends Factory implements Flag {
    /**
     * Opens a pipe for the current property
     *
     * @param alias the alias of the current property
     * @param space the space of the current property
     * @return the child pipeline, may be null
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    @Nullable
    public Pipe onOpen(
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
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    @Nullable
    public abstract T build();

    /**
     * Check if this factory uses the feature
     *
     * @param flag the specified flag code
     */
    public boolean isFlag(
        @NotNull long flag
    ) {
        return parent.isFlag(flag);
    }

    /**
     * Use this factory to resolve generic type
     * and replace type variables as much as possible
     *
     * @param generic the specified generic type
     * @throws IllegalArgumentException If the generic is illegal
     */
    @Override
    public Type getType(Type generic) {
        return parent.getType(generic);
    }

    /**
     * Closes this pipeline and returns the parent
     *
     * @param alert the flag that can be thrown errors
     * @param state the flag that was successfully built
     * @return the parent pipeline, may be null
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    @Override
    public Pipe onClose(
        @NotNull boolean alert,
        @NotNull boolean state
    ) throws IOException {
        if (state) {
            parent.onNext(
                build()
            );
        }
        return detach(alert);
    }
}
