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

import java.io.IOException;
import java.io.EOFException;

/**
 * @author kraity
 * @since 0.0.3
 */
public abstract class AbstractReader implements Reader {

    protected int index;
    protected int limit;

    protected int buflen;
    protected byte[] queue;

    /**
     * Returns the length of readable bytes
     *
     * @throws IOException If an I/O error occurs
     */
    protected abstract int load()
        throws IOException;

    /**
     * Checks {@link Reader} for readable bytes
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    @Override
    public boolean also() throws IOException {
        if (index < limit) {
            return true;
        }

        if (index == limit) {
            if ((limit = load()) > 0) {
                index = 0;
                return true;
            }
        }

        return false;
    }

    /**
     * Reads a byte in {@link Reader} and index switch to next
     */
    @Override
    public byte read() {
        return queue[index++];
    }

    /**
     * Skips over and discards exactly bytes of the specified length from this {@link Reader}
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    @Override
    public void skip(
        int size
    ) throws IOException {
        while (size > 0) {
            int num = limit - index;
            if (num > 0) {
                if (size <= num) {
                    index += size;
                    return;
                }
                if ((limit = load()) > 0) {
                    index = 0;
                    size -= num;
                } else {
                    size -= num;
                    index += num;
                }
                continue;
            }

            if (num == 0) {
                if ((limit = load()) > 0) {
                    index = 0;
                    continue;
                } else {
                    throw new EOFException(
                        "Unable to skip exactly"
                    );
                }
            }
            throw new EOFException(
                "Failed to skip the <" + size + "> exactly"
            );
        }
    }

    /**
     * Reads a byte if {@link Reader} has readable bytes, otherwise raise {@link IOException}
     *
     * @throws FlowCrash,IOException If this has been closed or I/O error occurs
     */
    @Override
    public byte next() throws IOException {
        if (index < limit) {
            return queue[index++];
        }

        if (index == limit) {
            if ((limit = load()) > 0) {
                index = 1;
                return queue[0];
            }
        }

        throw new FlowCrash(
            "No readable byte, please " +
                "check whether the stream is damaged"
        );
    }

    /**
     * Closes this reader and releases
     * any system resources associated with it
     */
    @Override
    public void close() throws IOException {
        limit = -1;
        queue = null;
    }
}
