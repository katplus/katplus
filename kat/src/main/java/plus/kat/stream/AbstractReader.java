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

/**
 * @author kraity
 * @since 0.0.3
 */
public abstract class AbstractReader implements Reader {

    protected int index;
    protected int limit;

    protected int range;
    protected int scale;
    protected byte[] cache;

    /**
     * Returns the value of scale
     */
    public int getScale() {
        return scale;
    }

    /**
     * @param value the specified scale
     */
    public void setScale(
        int value
    ) {
        if (value >= 0) {
            scale = value;
        }
    }

    /**
     * Returns the value of range
     */
    public int getRange() {
        return range;
    }

    /**
     * @param value the specified range
     */
    public void setRange(
        int value
    ) {
        if (value >= 0) {
            range = value;
        }
    }

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

        if (limit != -1) {
            limit = load();
            if (limit <= 0) {
                limit = -1;
            } else {
                index = 0;
                return true;
            }
        }

        return false;
    }

    /**
     * Reads a byte in {@link Reader} and index switch to next
     *
     * @throws ArrayIndexOutOfBoundsException If the index is greater than or equal to the size of the array
     */
    @Override
    public byte read() {
        return cache[index++];
    }

    /**
     * Reads a byte if {@link Reader} has readable bytes, otherwise raise IOException
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    @Override
    public byte next() throws IOException {
        if (index < limit) {
            return cache[index++];
        }

        if (limit != -1) {
            limit = load();
            if (limit <= 0) {
                limit = -1;
            } else {
                index = 1;
                return cache[0];
            }
        }

        throw new FlowCrash(
            "No readable byte, please " +
                "check whether the stream is damaged"
        );
    }
}
