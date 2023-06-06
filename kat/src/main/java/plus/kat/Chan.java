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
package plus.kat;

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.spare.*;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Chan extends Flag, Closeable {
    /**
     * Serializes the specified alias
     * and value at the current hierarchy
     *
     * <pre>{@code
     *  Chan chan = ...
     *  chan.set("id", 1);
     *  chan.set("name", "kraity");
     * }</pre>
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    boolean set(
        @Nullable String alias,
        @Nullable Object value
    ) throws IOException;

    /**
     * Serializes the specified alias
     * and value at the current hierarchy
     *
     * <pre>{@code
     *  Chan chan = ...
     *  chan.set("user", it -> {
     *     it.set("id", 1);
     *     it.set("name", "kraity");
     *     it.set("meta", meta -> {
     *         meta.set("id", 1);
     *         meta.set("tag", "kat");
     *     });
     *  });
     * }</pre>
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    boolean set(
        @Nullable String alias,
        @Nullable Entity value
    ) throws IOException;

    /**
     * Serializes the specified alias,
     * space and value at the current hierarchy
     *
     * <pre>{@code
     *  Chan chan = ...
     *  chan.set("me", "User", it -> {
     *     it.set("id", 1);
     *     it.set("name", "kraity");
     *  });
     * }</pre>
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    boolean set(
        @Nullable String alias,
        @Nullable String space,
        @Nullable Entity value
    ) throws IOException;

    /**
     * Serializes the specified alias,
     * coder and value at the current hierarchy
     *
     * <pre>{@code
     *  Coder<Integer> c1 = ...
     *  Coder<String> c2 = ...
     *
     *  Chan chan = ...
     *  chan.set("id", c1, 1);
     *  chan.set("name", c2, "kraity");
     * }</pre>
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    boolean set(
        @Nullable String alias,
        @Nullable Coder<?> coder,
        @Nullable Object value
    ) throws IOException;

    /**
     * Returns the {@link Flux} of this
     * {@link Chan} as a serialized {@code byte[]}
     *
     * <pre>{@code
     *   Chan chan = ...
     *   byte[] data = chan.toBinary();
     * }</pre>
     */
    @NotNull
    byte[] toBinary();

    /**
     * Returns the {@link Flux} of this
     * {@link Chan} as a serialized {@link String}
     *
     * <pre>{@code
     *   Chan chan = ...
     *   String text = chan.toString();
     * }</pre>
     */
    @NotNull
    String toString();

    /**
     * Returns the internal {@link Flux}
     *
     * <pre>{@code
     *   Chan chan = ...
     *   Flux flux = chan.getFlux();
     * }</pre>
     */
    @NotNull
    Flux getFlux();

    /**
     * Returns the internal {@link Context}
     *
     * <pre>{@code
     *   Chan chan = ...
     *   Context context = chan.getContext();
     * }</pre>
     */
    @NotNull
    Context getContext();
}
