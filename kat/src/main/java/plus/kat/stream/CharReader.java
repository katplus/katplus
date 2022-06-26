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

/**
 * @author kraity
 * @since 0.0.1
 */
public class CharReader implements Reader {

    private int index;
    private int range;
    private int offset;

    private final int length;
    private byte[] cache;
    private CharSequence value;

    public CharReader(
        @NotNull CharSequence data
    ) {
        range = 16;
        index = 16;
        value = data;
        cache = new byte[16];
        length = data.length();
    }

    /**
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     */
    public CharReader(
        @NotNull CharSequence data, int index, int length
    ) {
        if (index < 0 ||
            index >= length ||
            length > data.length()
        ) {
            throw new IndexOutOfBoundsException();
        }

        this.value = data;
        this.index = 16;
        this.range = 16;
        this.cache = new byte[16];
        this.length = length;
    }

    @Override
    public byte read() {
        return cache[index++];
    }

    @Override
    public boolean also() {
        if (index < range) {
            return true;
        }

        if (range > 0) {
            range = read(cache);
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
            // get char
            char c = value.charAt(offset);

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
                if (++offset >= length) {
                    buf[i++] = '?';
                    break;
                }

                char d = value.charAt(offset);
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
        range = 0;
        value = null;
        cache = null;
    }
}
