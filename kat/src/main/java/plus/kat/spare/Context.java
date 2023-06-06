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

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.6
 */
public interface Context {
    /**
     * Returns the {@link Spare} of this {@code type}.
     * If no cache, the providers searches for the spare
     *
     * @param type the specified actual type
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException  If the specified {@code type} is null
     * @throws IllegalStateException If the specified {@code type} is disabled
     */
    @Nullable
    <T>
    Spare<T> assign(
        @NotNull Type type
    );

    /**
     * Returns the {@link Spare} of this {@code type}.
     * If no cache, the providers searches for the spare
     *
     * @param type the specified parent type
     * @param name the specified actual name
     * @return {@link Spare} or {@code null}
     * @throws NullPointerException  If the specified arguments is null
     * @throws IllegalStateException If the specified {@code type} is disabled
     */
    @Nullable
    <T>
    Spare<T> assign(
        @NotNull Type type,
        @NotNull Space name
    );

    /**
     * Actives the {@link Spare} of the {@code type} and
     * returns the previous spare associated with this {@code type}
     *
     * <pre>{@code
     *  Type type = ...
     *  Context context = ...
     *
     *  Spare<?> spare = ...
     *  context.active(type, spare);  // actives the spare of this type
     * }</pre>
     *
     * @param type  the specified type to active
     * @param spare the specified spare to be associated
     * @return the previous {@link Spare} or {@code null}
     * @throws NullPointerException  If the specified arguments is null
     * @throws IllegalStateException If this {@link Context} is not modifiable
     */
    @Nullable
    Spare<?> active(
        @NotNull Type type,
        @NotNull Spare<?> spare
    );

    /**
     * Revokes the {@link Spare} cache for this {@code type} and
     * returns the previous spare associated with this {@code type}
     *
     * <pre>{@code
     *  Type type = ...
     *  Context context = ...
     *
     *  context.revoke(type, null); // revokes the spare of this type
     *  context.revoke(type, getSpare()); // revokes the specified type and spare
     * }</pre>
     *
     * @param type  the specified type to revoke
     * @param spare the specified spare to be unassociated
     * @return the previous {@link Spare} or {@code null}
     * @throws NullPointerException  If the specified {@code type} is null
     * @throws IllegalStateException If this {@link Context} is not modifiable
     */
    @Nullable
    Spare<?> revoke(
        @NotNull Type type,
        @Nullable Spare<?> spare
    );
}
