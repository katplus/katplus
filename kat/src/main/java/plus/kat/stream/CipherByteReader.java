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
public class CipherByteReader implements Reader {

    private int index;
    private int range;

    private int length;
    private int offset;
    private byte[] value;

    private byte[] cache;
    private Cipher cipher;

    /**
     * @throws NullPointerException If the specified {@code data} or {@code cipher} is null
     */
    public CipherByteReader(
        @NotNull byte[] data, @NotNull Cipher cipher
    ) {
        this(data, data.length, cipher, 32);
    }

    /**
     * @throws NullPointerException      If the specified {@code data} or {@code cipher} is null
     * @throws IndexOutOfBoundsException If the index and the range are out of range
     */
    public CipherByteReader(
        @NotNull byte[] data, int length, @NotNull Cipher cipher, int range
    ) {
        if (range < 16) {
            throw new IndexOutOfBoundsException();
        }

        if (data == null || cipher == null) {
            throw new NullPointerException();
        }

        this.value = data;
        this.range = range;
        this.length = length;
        this.cipher = cipher;
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
            range = read(value);
            if (range > 0) {
                index = 0;
                return true;
            }
        } catch (Exception e) {
            throw new IOCrash(e);
        }

        return false;
    }

    private int read(
        @NotNull byte[] buf
    ) {
        int more = length - offset;
        if (more > 0) {
            if (more > range) {
                more = range;
            }
            offset += more;
            cache = cipher.update(
                buf, 0, more
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

        if (range != -1) try {
            cipher.doFinal();
        } catch (Exception e) {
            // NOOP
        } finally {
            range = 0;
            length = 0;
            cipher = null;
        }
    }
}
