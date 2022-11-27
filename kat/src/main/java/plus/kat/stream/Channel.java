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
package plus.kat.stream;

import plus.kat.*;
import plus.kat.anno.*;

/**
 * @author kraity
 * @since 0.0.5
 */
public interface Channel extends Pipage {
    /**
     * Returns the flag of this {@link Channel}
     *
     * @return {@link Flag} or {@code null}
     */
    @Nullable
    Flag flag();

    /**
     * Returns the holder of this {@link Channel}
     *
     * @return {@link Channel} or {@code null}
     */
    @Nullable
    Channel holder();

    /**
     * Returns the supplier of this {@link Channel}
     *
     * @return {@link Supplier} or {@code null}
     */
    @Nullable
    Supplier supplier();
}
