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

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.5
 */
public interface Helper {
    /**
     * Returns the holder of this {@link Helper}
     */
    @Nullable
    Helper holder();

    /**
     * Resolves the unknown type with this helper,
     * substituting type variables as far as possible
     */
    @NotNull
    default Type locate(
        @NotNull Type unknown
    ) {
        Helper holder = holder();
        // handled by parent, default
        return holder == null ?
            unknown : holder.locate(unknown);
    }
}
