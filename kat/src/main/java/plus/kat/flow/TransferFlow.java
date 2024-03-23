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
package plus.kat.flow;

import plus.kat.Flow;

/**
 * @author kraity
 * @since 0.0.6
 */
public abstract class TransferFlow extends Flow {
    /**
     * Encode the source as UTF-8 and
     * writes it to {@link Flow#v}
     *
     * @param source the specified source
     * @param from   the start index of {@link #v}
     * @param start  the start index of specified source, inclusive
     * @param offset the offset index of specified source, exclusive
     * @return the offset index of {@link #v}
     * @throws IndexOutOfBoundsException If the queue capacity is insufficient
     */
    protected int load(
        char[] source, int from, int start, int offset
    ) {
        i = from;
        byte[] it = v;

        while (start < offset) {
            char c1 = source[start++];

            // U+0000 ~ U+007F
            if (c1 < 0x80) {
                it[from++] = (byte) c1;
            }

            // U+0080 ~ U+07FF
            else if (c1 < 0x800) {
                it[from++] = (byte) (c1 >> 6 | 0xC0);
                it[from++] = (byte) (c1 & 0x3F | 0x80);
            }

            // U+0800 ~ U+D7FF
            // U+E000 ~ U+FFFF
            else if (c1 < 0xD800 || 0xDFFF < c1) {
                it[from++] = (byte) (c1 >> 12 | 0xE0);
                it[from++] = (byte) (c1 >> 6 & 0x3F | 0x80);
                it[from++] = (byte) (c1 & 0x3F | 0x80);
            }

            // U+10000 ~ U+10FFFF
            else {
                if (c1 > 0xDBFF ||
                    start == offset) {
                    it[from++] = '?';
                    continue;
                }

                char c2 = source[start++];
                if (c2 < 0xDC00 ||
                    c2 > 0xDFFF) {
                    it[from++] = '?';
                    continue;
                }

                int hi = c1 - 0xD7C0;
                int lo = c2 - 0xDC00;

                it[from++] = (byte) (hi >> 8 | 0xF0);
                it[from++] = (byte) (hi >> 2 & 0x3F | 0x80);
                it[from++] = (byte) (lo >> 6 | hi << 4 & 0x30 | 0x80);
                it[from++] = (byte) (lo & 0x3F | 0x80);
            }
        }

        return l = from;
    }

    /**
     * Encode the source as UTF-8 and
     * writes it to {@link Flow#v}
     *
     * @param source the specified source
     * @param from   the start index of {@link #v}
     * @param start  the start index of specified source, inclusive
     * @param offset the offset index of specified source, exclusive
     * @return the offset index of {@link #v}
     * @throws IndexOutOfBoundsException If the queue capacity is insufficient
     */
    protected int load(
        CharSequence source, int from, int start, int offset
    ) {
        i = from;
        byte[] it = v;

        while (start < offset) {
            char c1 = source.charAt(start++);

            // U+0000 ~ U+007F
            if (c1 < 0x80) {
                it[from++] = (byte) c1;
            }

            // U+0080 ~ U+07FF
            else if (c1 < 0x800) {
                it[from++] = (byte) (c1 >> 6 | 0xC0);
                it[from++] = (byte) (c1 & 0x3F | 0x80);
            }

            // U+0800 ~ U+D7FF
            // U+E000 ~ U+FFFF
            else if (c1 < 0xD800 || 0xDFFF < c1) {
                it[from++] = (byte) (c1 >> 12 | 0xE0);
                it[from++] = (byte) (c1 >> 6 & 0x3F | 0x80);
                it[from++] = (byte) (c1 & 0x3F | 0x80);
            }

            // U+10000 ~ U+10FFFF
            else {
                if (c1 > 0xDBFF ||
                    start == offset) {
                    it[from++] = '?';
                    continue;
                }

                char c2 = source.charAt(start++);
                if (c2 < 0xDC00 ||
                    c2 > 0xDFFF) {
                    it[from++] = '?';
                    continue;
                }

                int hi = c1 - 0xD7C0;
                int lo = c2 - 0xDC00;

                it[from++] = (byte) (hi >> 8 | 0xF0);
                it[from++] = (byte) (hi >> 2 & 0x3F | 0x80);
                it[from++] = (byte) (lo >> 6 | hi << 4 & 0x30 | 0x80);
                it[from++] = (byte) (lo & 0x3F | 0x80);
            }
        }

        return l = from;
    }
}
