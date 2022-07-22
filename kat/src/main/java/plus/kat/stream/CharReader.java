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

/**
 * @author kraity
 * @since 0.0.1
 */
public class CharReader implements Reader {

    private int index;
    private int offset;

    private int begin;
    private final int end;

    private byte[] cache;
    private CharSequence value;

    public CharReader(
        @NotNull CharSequence data
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        value = data;
        end = data.length();
        cache = new byte[64];
        index = cache.length;
        offset = cache.length;
    }

    /**
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     */
    public CharReader(
        @NotNull CharSequence data, int index, int length
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        int end = index + length;
        if (index < 0 ||
            end <= index ||
            end > data.length()
        ) {
            throw new IndexOutOfBoundsException();
        }

        this.value = data;
        this.begin = index;
        this.end = end;
        this.cache = new byte[64];
        this.index = cache.length;
        this.offset = cache.length;
    }

    @Override
    public boolean also() {
        if (index < offset) {
            return true;
        }

        if (offset > 0) {
            offset = read(cache);
            if (offset > 0) {
                index = 0;
                return true;
            }
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

        if (offset > 0) {
            offset = read(cache);
            if (offset > 0) {
                index = 0;
                return cache[index++];
            }
        }

        throw new UnexpectedCrash(
            "Unexpectedly, no readable byte"
        );
    }

    private int read(
        @NotNull byte[] buf
    ) {
        int i = 0, l = buf.length;
        for (; i < l && begin < end; begin++) {
            // get char
            char c = value.charAt(begin);

            // U+0000 ~ U+007F
            if (c < 0x80) {
                buf[i++] = (byte) c;
            }

            // U+0080 ~ U+07FF
            else if (c < 0x800) {
                if (i + 2 > l) break;
                buf[i++] = (byte) ((c >> 6) | 0xC0);
                buf[i++] = (byte) ((c & 0x3F) | 0x80);
            }

            // U+10000 ~ U+10FFFF
            // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
            else if (c >= 0xD800 && c <= 0xDFFF) {
                if (i + 4 > l) break;
                if (++begin >= end) {
                    buf[i++] = '?';
                    break;
                }

                char d = value.charAt(begin);
                if (d < 0xDC00 || d > 0xDFFF) {
                    buf[i++] = '?';
                    continue;
                }

                int u = (c << 10) + d - 0x35F_DC00;
                buf[i++] = (byte) ((u >> 18) | 0xF0);
                buf[i++] = (byte) (((u >> 12) & 0x3F) | 0x80);
                buf[i++] = (byte) (((u >> 6) & 0x3F) | 0x80);
                buf[i++] = (byte) ((u & 0x3F) | 0x80);
            }

            // U+0800 ~ U+FFFF
            else {
                if (i + 3 > l) break;
                buf[i++] = (byte) ((c >> 12) | 0xE0);
                buf[i++] = (byte) (((c >> 6) & 0x3F) | 0x80);
                buf[i++] = (byte) ((c & 0x3F) | 0x80);
            }
        }

        return i;
    }

    @Override
    public void close() {
        offset = 0;
        value = null;
        cache = null;
    }
}
