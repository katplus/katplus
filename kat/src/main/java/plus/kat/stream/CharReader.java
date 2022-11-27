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
public class CharReader extends AbstractReader {

    protected int begin;
    protected final int end;
    protected CharSequence value;

    /**
     * @throws NullPointerException If the data is null
     */
    public CharReader(
        @NotNull CharSequence data
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        value = data;
        end = data.length();
    }

    /**
     * @throws NullPointerException      If the data is null
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
        this.end = end;
        this.begin = index;
    }

    @Override
    protected int load() {
        int cap = end - begin;
        if (cap <= 0) {
            return -1;
        }

        byte[] it = cache;
        if (it == null) {
            int r = range;
            if (r == 0) {
                r = 128;
            }
            if ((cap *= 3) < r) {
                r = cap;
            }
            cache = it = new byte[r];
        }

        int i = 0, l = it.length;
        for (; i < l && begin < end; begin++) {
            // next char
            char code = value.charAt(begin);

            // U+0000 ~ U+007F
            if (code < 0x80) {
                it[i++] = (byte) code;
            }

            // U+0080 ~ U+07FF
            else if (code < 0x800) {
                if (i + 2 > l) break;
                it[i++] = (byte) (code >> 6 | 0xC0);
                it[i++] = (byte) (code & 0x3F | 0x80);
            }

            // U+10000 ~ U+10FFFF
            else if (0xD7FF < code && code < 0xE000) {
                if (i + 4 > l) break;
                if (code > 0xDBFF) {
                    it[i++] = '?';
                    continue;
                }

                if (end == ++begin) {
                    it[i++] = '?';
                    break;
                }

                char arch = value.charAt(begin);
                if (arch < 0xDC00 ||
                    arch > 0xDFFF) {
                    it[i++] = '?';
                    continue;
                }

                int hi = code - 0xD7C0;
                int lo = arch - 0xDC00;

                it[i++] = (byte) (hi >> 8 | 0xF0);
                it[i++] = (byte) (hi >> 2 & 0x3F | 0x80);
                it[i++] = (byte) (lo >> 6 | hi << 4 & 0x30 | 0x80);
                it[i++] = (byte) (lo & 0x3F | 0x80);
            }

            // U+0800 ~ U+FFFF
            else {
                if (i + 3 > l) break;
                it[i++] = (byte) (code >> 12 | 0xE0);
                it[i++] = (byte) (code >> 6 & 0x3F | 0x80);
                it[i++] = (byte) (code & 0x3F | 0x80);
            }
        }

        return i;
    }

    @Override
    public void close() {
        value = null;
        cache = null;
        limit = -1;
    }
}
