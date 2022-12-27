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

import plus.kat.*;
import plus.kat.stream.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.6
 */
public interface Factory extends Pipage, Helper {
    /**
     * Initializes the flag and supplier for
     * this pipage from the holder, and so on
     *
     * @return this or the proxy pipage
     * @throws IOException If an I/O error occurs
     */
    Pipage init(
        Factory holder,
        Callback handler
    ) throws IOException;

    /**
     * Returns the flag of this {@link Factory}
     *
     * @return {@link Flag} or {@code null}
     */
    Flag flag();

    /**
     * Returns the holder of this {@link Factory}
     *
     * @return {@link Factory} or {@code null}
     */
    Factory holder();

    /**
     * Returns the supplier of this {@link Factory}
     *
     * @return {@link Supplier} or {@code null}
     */
    Supplier supplier();
}
