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

import plus.kat.anno.NotNull;

import plus.kat.crash.*;

import java.io.InputStream;

/**
 * @author kraity
 * @since 0.0.1
 */
public class InputStreamReader implements Reader {

    private int index;
    private int range;

    private byte[] cache;
    private InputStream value;

    public InputStreamReader(
        @NotNull InputStream data
    ) {
        this(data, 16);
    }

    /**
     * @throws IndexOutOfBoundsException If the range is less than {@code 4}
     */
    public InputStreamReader(
        @NotNull InputStream data, int range
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        if (range < 4) {
            throw new IndexOutOfBoundsException();
        }

        this.value = data;
        this.index = range;
        this.range = range;
        this.cache = new byte[range];
    }

    @Override
    public byte read() {
        return cache[index++];
    }

    @Override
    public boolean also() throws IOCrash {
        if (index < range) {
            return true;
        }

        if (range > 0) try {
            range = value.read(
                cache, 0, cache.length
            );

            if (range > 0) {
                index = 0;
                return true;
            }
        } catch (Exception e) {
            throw new IOCrash(e);
        }

        return false;
    }

    @Override
    public void close() {
        try {
            value.close();
        } catch (Exception e) {
            // NOOP
        } finally {
            range = 0;
            cache = null;
            value = null;
        }
    }
}
