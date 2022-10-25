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

import plus.kat.chain.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Proxy {
    /**
     * {@link Solver} commands this {@code proxy} creates
     * the next receiver according to the {@code space} and
     * {@code alias}, activate use it and push on receiver stack
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    boolean attach(
        @NotNull Space space,
        @NotNull Alias alias
    ) throws IOException;

    /**
     * {@link Solver} requests the receiver
     * at the top of the receiver stack of this
     * {@code proxy} to update its attributes according
     * to the {@code space}, {@code alias} and {@code value}
     *
     * @throws IOException If an I/O error occurs
     */
    void submit(
        @NotNull Space space,
        @NotNull Alias alias,
        @NotNull Value value
    ) throws IOException;

    /**
     * {@link Solver} commands this {@code proxy}
     * to finish updating attributes on the receiver at the
     * top of the receiver stack and remove it from the receiver stack
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    boolean detach() throws IOException;
}
