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

import javax.crypto.Cipher;
import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public class CipherByteReader extends AbstractReader {

    private int begin;
    private final int end;

    private byte[] value;
    private Cipher cipher;

    /**
     * @throws NullPointerException If the specified {@code data} or {@code cipher} is null
     */
    public CipherByteReader(
        @NotNull byte[] data,
        @NotNull Cipher cipher
    ) {
        if (data == null ||
            cipher == null) {
            throw new NullPointerException();
        }

        this.value = data;
        this.end = data.length;
        this.cipher = cipher;
    }

    /**
     * @throws NullPointerException      If the specified {@code data} or {@code cipher} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     */
    public CipherByteReader(
        @NotNull byte[] data,
        int index,
        int length,
        @NotNull Cipher cipher
    ) {
        if (data == null ||
            cipher == null) {
            throw new NullPointerException();
        }

        int offset = index + length;
        if (index < 0 ||
            offset <= index ||
            offset > data.length
        ) {
            throw new IndexOutOfBoundsException();
        }

        this.value = data;
        this.begin = index;
        this.end = offset;
        this.cipher = cipher;
    }

    @Override
    protected int load()
        throws IOException {
        int b = begin;
        int length = end - b;

        if (length > 0) {
            int s = scale;
            if (s != 0 &&
                length > s) {
                length = s;
            }
            begin += length;
            cache = cipher.update(
                value, b, length
            );
            if (cache == null ||
                cache.length == 0) {
                return load();
            }
        } else {
            try {
                cache = cipher.doFinal();
                if (cache == null) {
                    return -1;
                }
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        return cache.length;
    }

    @Override
    public void close() {
        value = null;
        cache = null;

        if (offset != -1) try {
            cipher.doFinal();
        } catch (Exception e) {
            // NOOP
        } finally {
            offset = -1;
            cipher = null;
        }
    }
}
