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
    private int offset;

    private int begin;
    private int end;
    private byte[] value;

    private byte[] cache;
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
        this.cipher = cipher;
        this.end = data.length;
        this.index = data.length;
        this.offset = data.length;
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
        this.cipher = cipher;
        this.begin = index;
        this.end = offset;
        this.index = data.length;
        this.offset = data.length;
    }

    @Override
    public boolean also() throws IOCrash {
        if (index < offset) {
            return true;
        }

        if (offset > 0) try {
            offset = read(value);
            if (offset > 0) {
                index = 0;
                return true;
            }
        } catch (Exception e) {
            throw new IOCrash(e);
        }

        return false;
    }

    @Override
    public byte read() {
        return cache[index++];
    }

    @Override
    public byte next() throws IOCrash {
        if (index < offset) {
            return cache[index++];
        }

        if (offset > 0) try {
            offset = read(value);
            if (offset > 0) {
                index = 0;
                return cache[index++];
            }
        } catch (Exception e) {
            throw new IOCrash(e);
        }

        throw new UnexpectedCrash(
            "Unexpectedly, no readable byte"
        );
    }

    private int read(
        @NotNull byte[] buf
    ) {
        int more = end - begin;
        if (more > 0) {
            if (more > offset) {
                more = offset;
            }
            begin += more;
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

        if (offset != -1) try {
            cipher.doFinal();
        } catch (Exception e) {
            // NOOP
        } finally {
            end = 0;
            offset = 0;
            cipher = null;
        }
    }
}
