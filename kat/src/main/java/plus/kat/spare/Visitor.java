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

import java.util.function.BiConsumer;

/**
 * @author kraity
 * @since 0.0.3
 */
@FunctionalInterface
public interface Visitor extends BiConsumer<Object, Object> {
    /**
     * Performs this operation on the given arguments
     *
     * @param key the specified key
     * @param val the specified val
     */
    void visit(
        @NotNull String key,
        @Nullable Object val
    );

    /**
     * Performs this operation on the given arguments
     *
     * @param key the specified key
     * @param val the specified val
     */
    @Override
    default void accept(
        @Nullable Object key,
        @Nullable Object val
    ) {
        if (key != null) {
            visit(
                key.toString(), val
            );
        }
    }
}
