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

import javax.crypto.Cipher;

/**
 * @author kraity
 * @since 0.0.1
 */
public class CipherCharReader implements Reader {

    private int index;
    private int range;
    private int offset;

    private byte[] cache;
    private byte[] buffer;

    private Cipher cipher;
    private final int length;
    private CharSequence value;

    public CipherCharReader(
        @NotNull CharSequence data,
        @NotNull Cipher cipher
    ) {
        this(data, cipher, 64);
    }

    /**
     * @throws IndexOutOfBoundsException If the range is less than {@code 32}
     */
    public CipherCharReader(
        @NotNull CharSequence data, @NotNull Cipher cipher, int range
    ) {
        if (range < 32) {
            throw new IndexOutOfBoundsException();
        }

        this.value = data;
        this.index = range;
        this.range = range;
        this.cipher = cipher;
        this.buffer = new byte[range];
        this.length = data.length();
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

        if (range > 0) {
            range = read(buffer);
            if (range > 0) {
                index = 0;
                return true;
            }
        }

        return false;
    }

    private int read(
        @NotNull byte[] buf
    ) {
        int i = 0, l = buf.length;
        for (; i < l && offset < length; offset++) {
            buf[i++] = (byte) value.charAt(offset);
        }

        if (i > 0) {
            cache = cipher.update(
                buf, 0, i
            );
            if (cache == null ||
                cache.length == 0) {
                return read(buf);
            }
        } else try {
            cache = cipher.doFinal();
            if (cache == null) {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }

        return cache.length;
    }

    @Override
    public void close() {
        value = null;
        cache = null;
        buffer = null;

        if (range != -1) try {
            cipher.doFinal();
        } catch (Exception e) {
            // NOOP
        } finally {
            range = 0;
            cipher = null;
        }
    }
}
