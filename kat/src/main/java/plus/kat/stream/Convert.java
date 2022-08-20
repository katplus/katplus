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
import plus.kat.anno.Nullable;

/**
 * @author kraity
 * @since 0.0.1
 */
public final class Convert {
    /**
     * Parses the {@code UTF8} {@code byte[]} as a {@code char} or {@code def} value
     */
    public static char toChar(
        @NotNull byte[] it, int len, char def
    ) {
        if (len == 0) {
            return def;
        }

        // 0xxxxxxx
        if (len == 1) {
            return (char) (it[0] & 0xFF);
        }

        // 110xxxxx 10xxxxxx
        if (len == 2) {
            return (char) (((it[0] & 0x1F) << 6) | (it[1] & 0x3F));
        }

        // 1110xxxx 10xxxxxx 10xxxxxx
        if (len == 3) {
            return (char) (((it[0] & 0xF) << 12) | ((it[1] & 0x3F) << 6) | (it[2] & 0x3F));
        }

        return def;
    }

    /**
     * Parses the {@code byte[]} as a signed decimal {@code int} or {@code def} value
     *
     * @param rad the radix to be used while parsing {@code byte[]}
     */
    public static int toInt(
        @NotNull byte[] it, int len, int rad, int def
    ) {
        if (len == 0) {
            return def;
        }

        if (len == 1) {
            int dig = it[0];
            if (dig < 58) {
                dig -= 48;
            } else if (dig < 91) {
                dig -= 55;
            } else {
                dig -= 87;
            }
            if (dig < rad
                && dig > -1) {
                return dig;
            }
            return def;
        }

        byte bit = 0;
        int lim = -Integer.MAX_VALUE;
        boolean neg = false;

        switch (it[0]) {
            case '-': {
                neg = true;
                bit = 1;
                lim = Integer.MIN_VALUE;
                break;
            }
            case '0': {
                switch (it[1]) {
                    case 'x': {
                        bit = 2;
                        rad = 16;
                        break;
                    }
                    case 'b': {
                        bit = 2;
                        rad = 2;
                        break;
                    }
                    default: {
                        bit = 1;
                        rad = 8;
                    }
                }
                while (bit < len &&
                    it[bit] == 48) bit++;
            }
        }

        int num = 0;
        int mul = lim / rad;

        while (bit < len) {
            int dig = it[bit++];
            if (dig < 58) {
                dig -= 48;
            } else if (dig < 91) {
                dig -= 55;
            } else {
                dig -= 87;
            }
            if (dig < 0 ||
                num < mul ||
                dig >= rad) {
                return def;
            }
            num *= rad;
            if (num < lim + dig) {
                return def;
            }
            num -= dig;
        }

        return neg ? num : -num;
    }

    /**
     * Parses the {@link CharSequence} as a signed decimal {@code int} or {@code def} value
     *
     * @param rad the radix to be used while parsing {@link CharSequence}
     * @see #toInt(byte[], int, int, int)
     */
    public static int toInt(
        @NotNull CharSequence it, int len, int rad, int def
    ) {
        if (len == 0) {
            return def;
        }

        if (len == 1) {
            int dig = it.charAt(0);
            if (dig < 58) {
                dig -= 48;
            } else if (dig < 91) {
                dig -= 55;
            } else {
                dig -= 87;
            }
            if (dig < rad
                && dig > -1) {
                return dig;
            }
            return def;
        }

        byte bit = 0;
        int lim = -Integer.MAX_VALUE;
        boolean neg = false;

        switch (it.charAt(0)) {
            case '-': {
                neg = true;
                bit = 1;
                lim = Integer.MIN_VALUE;
                break;
            }
            case '0': {
                switch (it.charAt(1)) {
                    case 'x': {
                        bit = 2;
                        rad = 16;
                        break;
                    }
                    case 'b': {
                        bit = 2;
                        rad = 2;
                        break;
                    }
                    default: {
                        bit = 1;
                        rad = 8;
                    }
                }
                while (bit < len &&
                    it.charAt(bit) == 48) bit++;
            }
        }

        int num = 0;
        int mul = lim / rad;

        while (bit < len) {
            int dig = it.charAt(bit++);
            if (dig < 58) {
                dig -= 48;
            } else if (dig < 91) {
                dig -= 55;
            } else {
                dig -= 87;
            }
            if (dig < 0 ||
                num < mul ||
                dig >= rad) {
                return def;
            }
            num *= rad;
            if (num < lim + dig) {
                return def;
            }
            num -= dig;
        }

        return neg ? num : -num;
    }

    /**
     * Parses the {@code byte[]} as a signed decimal {@code long} or {@code def} value
     *
     * @param rad radix the radix to be used while parsing {@code byte[]}
     */
    public static long toLong(
        @NotNull byte[] it, int len, long rad, long def
    ) {
        if (len == 0) {
            return def;
        }

        if (len == 1) {
            long dig = it[0];
            if (dig < 58L) {
                dig -= 48L;
            } else if (dig < 91L) {
                dig -= 55L;
            } else {
                dig -= 87L;
            }
            if (dig < rad
                && dig > -1L) {
                return dig;
            }
            return def;
        }

        byte bit = 0;
        long lim = -Long.MAX_VALUE;
        boolean neg = false;

        switch (it[0]) {
            case '-': {
                neg = true;
                bit = 1;
                lim = Long.MIN_VALUE;
                break;
            }
            case '0': {
                switch (it[1]) {
                    case 'x': {
                        bit = 2;
                        rad = 16L;
                        break;
                    }
                    case 'b': {
                        bit = 2;
                        rad = 2L;
                        break;
                    }
                    default: {
                        bit = 1;
                        rad = 8L;
                    }
                }
                while (bit < len &&
                    it[bit] == 48) bit++;
            }
        }

        long num = 0L;
        long mul = lim / rad;

        while (bit < len) {
            long dig = it[bit++];
            if (dig < 58L) {
                dig -= 48L;
            } else if (dig < 91L) {
                dig -= 55L;
            } else {
                dig -= 87L;
            }
            if (dig < 0L ||
                num < mul ||
                dig >= rad) {
                return def;
            }
            num *= rad;
            if (num < lim + dig) {
                return def;
            }
            num -= dig;
        }

        return neg ? num : -num;
    }

    /**
     * Parses the {@link CharSequence} as a signed decimal {@code long} or {@code def} value
     *
     * @param rad radix the radix to be used while parsing {@link CharSequence}
     * @see #toLong(byte[], int, long, long)
     */
    public static long toLong(
        @NotNull CharSequence it, int len, long rad, long def
    ) {
        if (len == 0) {
            return def;
        }

        if (len == 1) {
            long dig = it.charAt(0);
            if (dig < 58L) {
                dig -= 48L;
            } else if (dig < 91L) {
                dig -= 55L;
            } else {
                dig -= 87L;
            }
            if (dig < rad
                && dig > -1L) {
                return dig;
            }
            return def;
        }

        byte bit = 0;
        long lim = -Long.MAX_VALUE;
        boolean neg = false;

        switch (it.charAt(0)) {
            case '-': {
                neg = true;
                bit = 1;
                lim = Long.MIN_VALUE;
                break;
            }
            case '0': {
                switch (it.charAt(1)) {
                    case 'x': {
                        bit = 2;
                        rad = 16L;
                        break;
                    }
                    case 'b': {
                        bit = 2;
                        rad = 2L;
                        break;
                    }
                    default: {
                        bit = 1;
                        rad = 8L;
                    }
                }
                while (bit < len &&
                    it.charAt(bit) == 48) bit++;
            }
        }

        long num = 0L;
        long mul = lim / rad;

        while (bit < len) {
            long dig = it.charAt(bit++);
            if (dig < 58L) {
                dig -= 48L;
            } else if (dig < 91L) {
                dig -= 55L;
            } else {
                dig -= 87L;
            }
            if (dig < 0L ||
                num < mul ||
                dig >= rad) {
                return def;
            }
            num *= rad;
            if (num < lim + dig) {
                return def;
            }
            num -= dig;
        }

        return neg ? num : -num;
    }

    /**
     * Parses the {@code byte[]} as a {@code float} or {@code def} value
     */
    @SuppressWarnings("deprecation")
    public static float toFloat(
        @NotNull byte[] it, int len, float def
    ) {
        switch (len) {
            case 0: {
                return def;
            }
            case 1: {
                int dig = it[0];
                if (dig < 58) {
                    dig -= 48;
                } else if (dig < 91) {
                    dig -= 55;
                } else {
                    dig -= 87;
                }
                if (dig < 10 && dig > -1) {
                    return dig;
                }
                return def;
            }
            case 10: {
                if (it[0] == '0' &&
                    it[1] == 'x') {
                    int bits = it[2];
                    for (int i = 3; i < 10; i++) {
                        int dig = it[i];
                        if (dig < 58) {
                            dig -= 48;
                        } else if (dig < 91) {
                            dig -= 55;
                        } else {
                            dig -= 87;
                        }
                        if (dig < 0 || dig > 15) {
                            return def;
                        } else {
                            bits <<= 4;
                            bits |= dig;
                        }
                    }
                    return Float.intBitsToFloat(bits);
                }
            }
        }

        try {
            return Float.parseFloat(
                new String(
                    it, 0, 0, len
                )
            );
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Parses the {@code byte[]} as a {@code double} or {@code def} value
     */
    @SuppressWarnings("deprecation")
    public static double toDouble(
        @NotNull byte[] it, int len, double def
    ) {
        switch (len) {
            case 0: {
                return def;
            }
            case 1: {
                int dig = it[0];
                if (dig < 58) {
                    dig -= 48;
                } else if (dig < 91) {
                    dig -= 55;
                } else {
                    dig -= 87;
                }
                if (dig < 10 && dig > -1) {
                    return dig;
                }
                return def;
            }
            case 18: {
                if (it[0] == '0' &&
                    it[1] == 'x') {
                    long bits = it[2];
                    for (int i = 3; i < 18; i++) {
                        long dig = it[i];
                        if (dig < 58) {
                            dig -= 48;
                        } else if (dig < 91) {
                            dig -= 55;
                        } else {
                            dig -= 87;
                        }
                        if (dig < 0 || dig > 15) {
                            return def;
                        } else {
                            bits <<= 4;
                            bits |= dig;
                        }
                    }
                    return Double.longBitsToDouble(bits);
                }
            }
        }

        try {
            return Double.parseDouble(
                new String(
                    it, 0, 0, len
                )
            );
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Parses the {@code byte[]} as a {@code int},
     * {@code long}, {@code double}, or {@code def} value
     */
    @Nullable
    @SuppressWarnings("deprecation")
    public static Number toNumber(
        @NotNull byte[] it, int len, @Nullable Number def
    ) {
        if (len == 0) {
            return def;
        }

        byte b = it[0];
        if (b > 0x39) {
            return def;
        }

        if (b > 0x2F) {
            if (len < 10) {
                int num = toInt(
                    it, len, 10, -1
                );
                if (num != -1) {
                    return num;
                }
            } else {
                long num = toLong(
                    it, len, 10, -1
                );
                if (num > Integer.MAX_VALUE) {
                    return num;
                } else if (num != -1) {
                    return (int) num;
                }
            }
        } else {
            if (b != 0x2D) {
                return def;
            }

            if (len < 11) {
                int num = toInt(
                    it, len, 10, 1
                );
                if (num != 1) {
                    return num;
                }
            } else {
                long num = toLong(
                    it, len, 10, 1
                );
                if (num < Integer.MIN_VALUE) {
                    return num;
                } else if (num != 1) {
                    return (int) num;
                }
            }
        }

        int i = 1, r = 0;
        while (i < len) {
            byte t = it[i++];
            if (t > 0x39) {
                return def;
            }

            if (t < 0x30) {
                if (t != '.' ||
                    ++r == 2) {
                    return def;
                }
            }
        }

        try {
            return Double.parseDouble(
                new String(
                    it, 0, 0, len
                )
            );
        } catch (Exception e) {
            // Nothing
        }

        return def;
    }

    /**
     * Parses the {@code byte[]} as a {@code boolean} or {@code def} value
     */
    public static boolean toBoolean(
        @NotNull byte[] it, int len, boolean def
    ) {
        switch (len) {
            case 0: {
                return def;
            }
            case 1: {
                byte b = it[0];
                return '1' <= b && b <= '9';
            }
            case 4: {
                byte b = it[0];
                if (b == 't') {
                    return it[1] == 'r'
                        && it[2] == 'u'
                        && it[3] == 'e';
                }

                if (b == 'T') {
                    return it[1] == 'R'
                        && it[2] == 'U'
                        && it[3] == 'E';
                }

                return toInt(it, len, 10, 0) != 0;
            }
            case 5: {
                byte c = it[0];
                if (c == 'f' || c == 'F') {
                    return false;
                }

                return toInt(it, len, 10, 0) != 0;
            }
        }

        return toLong(it, len, 10L, 0L) != 0L;
    }

    /**
     * Parses the {@link CharSequence} as a {@code boolean} or {@code def} value
     */
    public static boolean toBoolean(
        @NotNull CharSequence it, int len, boolean def
    ) {
        switch (len) {
            case 0: {
                return def;
            }
            case 1: {
                char c = it.charAt(0);
                return '1' <= c && c <= '9';
            }
            case 4: {
                char c = it.charAt(0);
                if (c == 't') {
                    return it.charAt(1) == 'r'
                        && it.charAt(2) == 'u'
                        && it.charAt(3) == 'e';
                }

                if (c == 'T') {
                    return it.charAt(1) == 'R'
                        && it.charAt(2) == 'U'
                        && it.charAt(3) == 'E';
                }

                return toInt(it, len, 10, 0) != 0;
            }
            case 5: {
                char c = it.charAt(0);
                if (c == 'f' || c == 'F') {
                    return false;
                }

                return toInt(it, len, 10, 0) != 0;
            }
        }

        return toLong(it, len, 10L, 0L) != 0L;
    }
}
