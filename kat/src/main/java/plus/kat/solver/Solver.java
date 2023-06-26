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
package plus.kat.solver;

import plus.kat.*;
import plus.kat.actor.*;
import plus.kat.spare.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Solver {
    /**
     * Reads the specified {@link Flow} and transfer
     * the solution of this flow to the specified spider
     *
     * @param flow   the specified flow as the reader
     * @param spider the specified spider as the receiver
     * @throws IOException           If a read error occurs
     * @throws IllegalStateException If a fatal error occurs
     */
    void solve(
        @NotNull Flow flow,
        @NotNull Spider spider
    ) throws IOException;

    /**
     * Clear this solver and releases resources associated with it.
     * If this solver is closed then invoking this method has no effect
     *
     * @throws IllegalStateException If a fatal error occurs
     */
    default void clear() {
        // Do nothing by default
    }
}
