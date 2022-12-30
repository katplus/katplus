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

import plus.kat.crash.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.EOFException;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Paper extends Closeable {
    /**
     * Reads a byte in this {@link Paper} and index switch to next
     *
     * @throws IOException                    If this has been closed or I/O error occurs
     * @throws ArrayIndexOutOfBoundsException If the index is greater than or equal to the size of the array
     */
    byte read() throws IOException;

    /**
     * Checks this {@link Paper} for readable bytes
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    boolean also() throws IOException;

    /**
     * Skips over and discards exactly bytes of the specified length from this {@link Paper}
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    default void skip(
        int size
    ) throws IOException {
        while (size > 0) {
            if (also()) {
                read();
                size--;
            } else {
                throw new EOFException(
                    "Unable to skip exactly"
                );
            }
        }
    }

    /**
     * Reads a byte if this {@link Paper} has readable bytes, otherwise raise {@link IOException}
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    default byte next() throws IOException {
        if (also()) {
            return read();
        }

        throw new FlowCrash(
            "No readable byte, please " +
                "check whether the stream is damaged"
        );
    }
}
