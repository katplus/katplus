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
package plus.kat.kernel;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.crash.*;
import plus.kat.stream.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public final class Unsafe {
    /**
     * Unsafe method, and may be removed
     *
     * @throws NullPointerException If the specified chain is null
     */
    @Nullable
    public static byte[] value(
        @NotNull Chain c
    ) {
        return c.isFixed() ? null : c.value;
    }

    /**
     * Unsafe method, and may be removed
     *
     * @throws Collapse             If the chain is finally fixed
     * @throws NullPointerException If the specified chain is null
     */
    public static void bucket(
        @NotNull Chain c,
        @Nullable Bucket b
    ) {
        if (!c.isFixed()) {
            c.bucket = b;
        } else {
            throw new Collapse(
                "Unexpectedly, the chain is finally fixed"
            );
        }
    }
}
