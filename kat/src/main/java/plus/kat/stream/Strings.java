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

import static plus.kat.kernel.Chain.EMPTY_CHARS;

/**
 * @author kraity
 * @since 0.0.2
 */
public class Strings {
    /**
     * Returns the size of UTF-8 {@code byte[]}.
     * Strictly check UTF-8 code, if illegal returns {@code 0}
     *
     * @param i begin index, include
     * @param j end index, exclude
     * @since 0.0.2
     */
    public static int size(
        @NotNull byte[] it, int i, int j
    ) {
        if (i >= j) {
            return 0;
        }

        int k = i;
        int size = 0;

        for (; k < j; size++) {
            // get byte
            byte b = it[k++];

            // U+0000 ~ U+007F
            // 0xxxxxxx
            if (b >= 0) {
                continue;
            }

            // U+0080 ~ U+07FF
            // 110xxxxx 10xxxxxx
            if ((b >> 5) == -2) {
                // overflow
                if (k >= j) {
                    return 0;
                }

                // check code
                if ((it[k++] & 0xC0) != 0x80) {
                    return 0;
                }
            }

            // U+0800 ~ U+FFFF
            // 1110xxxx 10xxxxxx 10xxxxxx
            else if ((b >> 4) == -2) {
                // overflow
                if (k + 1 >= j) {
                    return 0;
                }

                // check code
                if ((it[k++] & 0xC0) != 0x80 ||
                    (it[k++] & 0xC0) != 0x80) {
                    return 0;
                }
            }

            // U+10000 ~ U+10FFFF
            // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
            // 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
            else if ((b >> 3) == -2) {
                // overflow
                if (k + 2 >= j) {
                    return 0;
                }

                // check code
                if ((it[k++] & 0xC0) != 0x80 ||
                    (it[k++] & 0xC0) != 0x80 ||
                    (it[k++] & 0xC0) != 0x80) {
                    return 0;
                }

                size++; // another agent pair
            }

            // beyond the current range
            else {
                return 0;
            }
        }

        return size;
    }

    /**
     * Parses the UTF-8 {@code byte[]} as a {@code char[]}.
     * Strictly check UTF-8 code, if illegal returns the empty array
     *
     * @param i begin index, include
     * @param j end index, exclude
     * @since 0.0.2
     */
    @NotNull
    public static char[] toChars(
        @NotNull byte[] it, int i, int j
    ) {
        int size = size(
            it, i, j
        );

        if (size == 0) {
            return EMPTY_CHARS;
        }

        int len = j - i;
        char[] ch = new char[size];

        if (size == len) {
            for (int m = 0, n = i; n < j; ) {
                ch[m++] = (char) it[n++];
            }
            return ch;
        }

        for (int m = 0, n = i; n < j; ) {
            // get byte
            byte b = it[n++];

            // U+0000 ~ U+007F
            // 0xxxxxxx
            if (b >= 0) {
                ch[m++] = (char) b;
            }

            // U+0080 ~ U+07FF
            // 110xxxxx 10xxxxxx
            else if ((b >> 5) == -2) {
                ch[m++] = (char) (
                    (b << 6) | (it[n++] & 0x3F)
                );
            }

            // U+0800 ~ U+FFFF
            // 1110xxxx 10xxxxxx 10xxxxxx
            else if ((b >> 4) == -2) {
                ch[m++] = (char) (
                    (b << 12) | ((it[n++] & 0x3F) << 6) | (it[n++] & 0x3F)
                );
            }

            // U+10000 ~ U+10FFFF
            // 11110x xx : 10xxxx xx : 10xx xx xx : 10xx xxxx
            // 11110x xx : 10x100 00
            // 1101 10xx xxxx xxxx 1101 11xx xxxx xxxx
            else if ((b >> 3) == -2) {
                byte b2 = it[n++];
                byte b3 = it[n++];
                ch[m++] = (char) (
                    ((0xD8 | (b & 0x03)) << 8) |
                        ((((b2 - 0x10 >> 2)) & 0x0F) << 4) |
                        (((b2 & 0x03) << 2) | ((b3 >> 4) & 0x03))
                );

                byte b4 = it[n++];
                ch[m++] = (char) (
                    ((0xDC | ((b3 >> 2) & 0x03)) << 8) |
                        ((((b3 & 0x3) << 2) | ((b4 >> 4) & 0x03)) << 4) | (b4 & 0x0F)
                );
            }

            // beyond the current range
            else {
                return EMPTY_CHARS;
            }
        }

        return ch;
    }
}
