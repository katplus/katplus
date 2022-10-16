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

import plus.kat.kernel.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("deprecation")
public final class Binary {

    private static final byte[] LOWER = {
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f', 'g', 'h',
        'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final byte[] UPPER = {
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F', 'G', 'H',
        'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /**
     * Convert the number to lowercase thirty-six-decimal character
     *
     * <pre>{@code
     *  byte b = Binary.lower(1); // 1
     *  byte b = Binary.lower(10); // a
     *  byte b = Binary.lower(21); // l
     * }</pre>
     *
     * @param i the specified number
     * @throws ArrayIndexOutOfBoundsException If the {@code number} is negative or greater than thirty-six
     */
    public static byte lower(int i) {
        return LOWER[i];
    }

    /**
     * Convert the number to uppercase thirty-six-decimal character
     *
     * <pre>{@code
     *  byte b = Binary.upper(1); // 1
     *  byte b = Binary.upper(10); // A
     *  byte b = Binary.upper(21); // L
     * }</pre>
     *
     * @param i the specified number
     * @throws ArrayIndexOutOfBoundsException If the {@code number} is negative or greater than thirty-six
     */
    public static byte upper(int i) {
        return UPPER[i];
    }

    /**
     * Convert the bytes to a lowercase hexadecimal array
     *
     * <pre>{@code
     *   // 6b6174
     *   byte[] b = Binary.lower(
     *       new byte[]{'k', 'a', 't'}
     *   );
     * }</pre>
     *
     * @param d the specified {@code byte[]} to be encoded
     */
    @NotNull
    public static byte[] lower(
        @NotNull byte[] d
    ) {
        int i = 0, k = 0;
        byte[] it = new byte[d.length * 2];

        while (i < d.length) {
            int o = d[i++] & 0xFF;
            it[k++] = LOWER[o >> 4];
            it[k++] = LOWER[o & 0xF];
        }

        return it;
    }

    /**
     * Convert the bytes to a lowercase hexadecimal string
     *
     * <pre>{@code
     *   // 6b6174
     *   String s = Binary.toLower(
     *       new byte[]{'k', 'a', 't'}
     *   );
     * }</pre>
     *
     * @param d the specified {@code byte[]} to be encoded
     */
    @NotNull
    public static String toLower(
        @NotNull byte[] d
    ) {
        int i = 0, k = 0;
        byte[] it = new byte[d.length * 2];

        while (i < d.length) {
            int o = d[i++] & 0xFF;
            it[k++] = LOWER[o >> 4];
            it[k++] = LOWER[o & 0xF];
        }

        return new String(
            it, 0, 0, k
        );
    }

    /**
     * Convert the bytes to an uppercase hexadecimal array
     *
     * <pre>{@code
     *   // 6B6174
     *   byte[] b = Binary.upper(
     *       new byte[]{'k', 'a', 't'}
     *   );
     * }</pre>
     *
     * @param d the specified {@code byte[]} to be encoded
     */
    @NotNull
    public static byte[] upper(
        @NotNull byte[] d
    ) {
        int i = 0, k = 0;
        byte[] it = new byte[d.length * 2];

        while (i < d.length) {
            int o = d[i++] & 0xFF;
            it[k++] = UPPER[o >> 4];
            it[k++] = UPPER[o & 0xF];
        }

        return it;
    }

    /**
     * Convert the bytes to an uppercase hexadecimal string
     *
     * <pre>{@code
     *   // 6B6174
     *   String s = Binary.toUpper(
     *       new byte[]{'k', 'a', 't'}
     *   );
     * }</pre>
     *
     * @param d the specified {@code byte[]} to be encoded
     */
    @NotNull
    public static String toUpper(
        @NotNull byte[] d
    ) {
        int i = 0, k = 0;
        byte[] it = new byte[d.length * 2];

        while (i < d.length) {
            int o = d[i++] & 0xFF;
            it[k++] = UPPER[o >> 4];
            it[k++] = UPPER[o & 0xF];
        }

        return new String(
            it, 0, 0, k
        );
    }

    /**
     * Convert the ascii character to a hexadecimal number
     *
     * <pre>{@code
     *   Binary.digit((byte) '0'); // 0
     *   Binary.digit((byte) '6'); // 6
     *   Binary.digit((byte) 'a'); // 10
     *   Binary.digit((byte) 'C'); // 12
     *   Binary.digit((byte) '-'); // IOException
     *   Binary.digit((byte) 'L'); // IOException
     * }</pre>
     *
     * @param b the specified {@code b} to be converted
     * @return {@code [0, 16)}
     * @since 0.0.4
     */
    public static int digit(
        @NotNull byte b
    ) throws IOException {
        if (b > 0x2F) {
            if (b < 0x3A) {
                return b - 0x30;
            }
            if (b > 0x60 && b < 0x67) {
                return b - 0x57;
            }
            if (b > 0x40 && b < 0x47) {
                return b - 0x37;
            }
        }
        throw new IOException(
            "Unexpectedly, " + (char) b + " is not a hexadecimal number"
        );
    }

    /**
     * Convert the string to an ascii byte array
     *
     * @param d the specified {@code d} to be converted
     * @throws NullPointerException If the specified {@code chars} is null
     * @since 0.0.4
     */
    @NotNull
    public static byte[] latin(
        @NotNull String d
    ) {
        int len = d.length();
        if (len != 0) {
            byte[] it = new byte[len];
            d.getBytes(0, len, it, 0);
            return it;
        }
        return Chain.EMPTY_BYTES;
    }

    /**
     * Convert the byte array to an ascii string
     *
     * @param d the specified {@code d} to be converted
     * @throws NullPointerException If the specified {@code bytes} is null
     * @since 0.0.4
     */
    @NotNull
    public static String latin(
        @NotNull byte[] d
    ) {
        if (d.length == 0) {
            return "";
        }
        return new String(
            d, 0, 0, d.length
        );
    }

    /**
     * Returns the length of UTF-8 {@code byte[]}.
     * Strictly check UTF-8 code, if illegal returns {@code 0}
     *
     * @param i the specified begin index, include
     * @param e the specified end index, exclude
     * @return {@code [0, +∞)}
     * @since 0.0.4
     */
    public static int length(
        @NotNull byte[] it, int i, int e
    ) {
        if (e <= i) {
            return 0;
        }

        int size = 0;
        for (; i < e; size++) {
            // next byte
            byte b = it[i++];

            // U+0000 ~ U+007F
            // 0xxxxxxx
            if (b >= 0) {
                continue;
            }

            // U+0080 ~ U+07FF
            // 110xxxxx 10xxxxxx
            if ((b >> 5) == -2) {
                // overflow
                if (i >= e) {
                    return 0;
                }

                // check code
                if ((it[i++] & 0xC0) != 0x80) {
                    return 0;
                }
            }

            // U+0800 ~ U+FFFF
            // 1110xxxx 10xxxxxx 10xxxxxx
            else if ((b >> 4) == -2) {
                // overflow
                if (i + 1 >= e) {
                    return 0;
                }

                // check code
                if ((it[i++] & 0xC0) != 0x80 ||
                    (it[i++] & 0xC0) != 0x80) {
                    return 0;
                }
            }

            // U+10000 ~ U+10FFFF
            // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
            // 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
            else if ((b >> 3) == -2) {
                // overflow
                if (i + 2 >= e) {
                    return 0;
                }

                // check code
                if ((it[i++] & 0xC0) != 0x80 ||
                    (it[i++] & 0xC0) != 0x80 ||
                    (it[i++] & 0xC0) != 0x80) {
                    return 0;
                }

                size++; // another of the surrogate pair
            }

            // Not the current computing category
            else {
                return 0;
            }
        }

        return size;
    }
}
