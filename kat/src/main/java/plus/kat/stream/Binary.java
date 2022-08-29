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

import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.kernel.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
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
     * @param i index
     * @throws ArrayIndexOutOfBoundsException if the index argument is negative or greater than thirty-six
     */
    public static byte lower(int i) {
        return LOWER[i];
    }

    /**
     * @param i index
     * @throws ArrayIndexOutOfBoundsException if the index argument is negative or greater than thirty-six
     */
    public static byte upper(int i) {
        return UPPER[i];
    }

    /**
     * @param d specify the {@code byte[]} to be encoded
     */
    @NotNull
    public static byte[] lower(
        @NotNull byte[] d
    ) {
        int i = 0, j = 0;
        byte[] it = new byte[d.length * 2];

        while (i < d.length) {
            int o = d[i++] & 0xFF;
            it[j++] = LOWER[o >> 4];
            it[j++] = LOWER[o & 0xF];
        }

        return it;
    }

    /**
     * @param d specify the {@code byte[]} to be encoded
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public static String toLower(
        @NotNull byte[] d
    ) {
        int i = 0, j = 0;
        byte[] it = new byte[d.length * 2];

        while (i < d.length) {
            int o = d[i++] & 0xFF;
            it[j++] = LOWER[o >> 4];
            it[j++] = LOWER[o & 0xF];
        }

        return new String(
            it, 0, 0, j
        );
    }

    /**
     * @param d specify the {@code byte[]} to be encoded
     */
    @NotNull
    public static byte[] upper(
        @NotNull byte[] d
    ) {
        int i = 0, j = 0;
        byte[] it = new byte[d.length * 2];

        while (i < d.length) {
            int o = d[i++] & 0xFF;
            it[j++] = UPPER[o >> 4];
            it[j++] = UPPER[o & 0xF];
        }

        return it;
    }

    /**
     * @param d specify the {@code byte[]} to be encoded
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public static String toUpper(
        @NotNull byte[] d
    ) {
        int i = 0, j = 0;
        byte[] it = new byte[d.length * 2];

        while (i < d.length) {
            int o = d[i++] & 0xFF;
            it[j++] = UPPER[o >> 4];
            it[j++] = UPPER[o & 0xF];
        }

        return new String(
            it, 0, 0, j
        );
    }

    /**
     * @param b specify the {@code b} to be converted
     * @throws IOException If the {@code b} is not a hexadecimal number
     */
    public static int hex(
        byte b
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
        throw new UnexpectedCrash(
            "Unexpectedly, " + (char) b + " is not a hexadecimal number"
        );
    }

    /**
     * @param d specify the {@code d} to be converted
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public static byte[] ascii(
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
     * @param d specify the {@code d} to be converted
     * @since 0.0.3
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public static String ascii(
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
     * @param d specify the {@code d} to be referenced
     * @since 0.0.3
     */
    @NotNull
    public static Alias alias(
        @NotNull byte[] d
    ) {
        if (d.length == 0) {
            return Alias.EMPTY;
        } else {
            return new Alias(d);
        }
    }
}
