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

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.kernel.*;

import static plus.kat.Algo.*;
import static plus.kat.kernel.Chain.*;
import static plus.kat.stream.Binary.*;

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
            int b1 = it[0];
            if (b1 < 0) {
                return def;
            }
            return (char) (b1 & 0xFF);
        }

        // 110xxxxx 10xxxxxx
        if (len == 2) {
            int b1 = it[0],
                b2 = it[1];
            if ((b1 >> 5) != -2 ||
                (b2 >> 6) != -2) {
                return def;
            }

            return (char) (((b1 & 0x1F) << 6) | (b2 & 0x3F));
        }

        // 1110xxxx 10xxxxxx 10xxxxxx
        if (len == 3) {
            int b1 = it[0],
                b2 = it[1],
                b3 = it[2];
            if ((b1 >> 4) != -2 ||
                (b2 >> 6) != -2 ||
                (b3 >> 6) != -2) {
                return def;
            }

            return (char) (((b1 & 0xF) << 12) | ((b2 & 0x3F) << 6) | (b3 & 0x3F));
        }

        return def;
    }

    /**
     * Parses the UTF-8 {@code byte[]} as a {@code char[]}.
     * Strictly check UTF-8 code, if illegal returns the empty array
     *
     * @param i the specified begin index, include
     * @param e the specified end index, exclude
     * @since 0.0.4
     */
    @NotNull
    public static char[] toChars(
        @NotNull byte[] it, int i, int e
    ) {
        int size = length(
            it, i, e
        );

        if (size == 0) {
            return EMPTY_CHARS;
        }

        int len = e - i;
        char[] ch = new char[size];

        if (size == len) {
            for (int m = 0, n = i; n < e; ) {
                ch[m++] = (char) it[n++];
            }
            return ch;
        }

        for (int m = 0, n = i; n < e; ) {
            // next byte
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
                // true
                if (b == 't') {
                    return it[1] == 'r'
                        && it[2] == 'u'
                        && it[3] == 'e';
                }

                // TRUE/True
                if (b == 'T') {
                    byte c = it[1];
                    if (c == 'R') {
                        return it[2] == 'U'
                            && it[3] == 'E';
                    }

                    // True
                    else if (c == 'r') {
                        return it[2] == 'u'
                            && it[3] == 'e';
                    }

                    return false;
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
                // true
                if (c == 't') {
                    return it.charAt(1) == 'r'
                        && it.charAt(2) == 'u'
                        && it.charAt(3) == 'e';
                }

                // TRUE/True
                if (c == 'T') {
                    char d = it.charAt(1);
                    if (d == 'R') {
                        return it.charAt(2) == 'U'
                            && it.charAt(3) == 'E';
                    }

                    // True
                    else if (d == 'r') {
                        return it.charAt(2) == 'u'
                            && it.charAt(3) == 'e';
                    }

                    return false;
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

    /**
     * Parse {@link CharSequence} and convert result to {@link T}
     *
     * @param text the specified text to be parsed
     * @return {@link T} or {@code null}
     * @throws NullPointerException If the {@code spare} is null
     * @since 0.0.4
     */
    @Nullable
    public static <T> T toObject(
        @NotNull Spare<T> spare,
        @Nullable CharSequence text
    ) {
        if (text == null) {
            return null;
        }

        if (spare == null) {
            throw new NullPointerException();
        }

        return Convert.toObject(
            spare, text, null, null
        );
    }

    /**
     * Parse {@link CharSequence} and convert result to {@link T}
     *
     * @param text the specified text to be parsed
     * @return {@link T} or {@code null}
     * @throws NullPointerException If the {@code clazz} is null
     * @since 0.0.4
     */
    @Nullable
    public static <T> T toObject(
        @NotNull Class<T> clazz,
        @Nullable CharSequence text
    ) {
        if (text == null) {
            return null;
        }

        Supplier supplier = Supplier.ins();
        Spare<T> spare = supplier.lookup(clazz);

        if (spare == null) {
            return null;
        }

        return Convert.toObject(
            spare, text, null, supplier
        );
    }

    /**
     * Parse {@link CharSequence} and convert result to {@link T}
     *
     * @param text the specified text to be parsed
     * @return {@link T} or {@code null}
     * @throws NullPointerException If the {@code text} or {@code spare} is null
     * @since 0.0.4
     */
    @Nullable
    public static <T> T toObject(
        @NotNull Spare<T> spare,
        @NotNull Flag flag,
        @NotNull CharSequence text
    ) {
        if (flag instanceof Event) {
            Event<?> event = (Event<?>) flag;
            return toObject(
                spare, text, flag, event.getSupplier()
            );
        } else {
            return toObject(
                spare, text, flag, null
            );
        }
    }

    /**
     * Parse {@link CharSequence} and convert result to {@link T}
     *
     * @param text the specified text to be parsed
     * @return {@link T} or {@code null}
     * @throws NullPointerException If the {@code text} or {@code spare} is null
     * @since 0.0.4
     */
    @Nullable
    public static <T> T toObject(
        @NotNull Spare<T> spare,
        @NotNull CharSequence text,
        @Nullable Flag flag,
        @Nullable Supplier supplier
    ) {
        int e = text.length();
        if (e < 2) {
            return null;
        }

        if (spare == null) {
            throw new NullPointerException();
        }

        int i = 0;
        char c1, c2;

        do {
            c1 = text.charAt(i);
            if (c1 <= 0x20) {
                i++;
            } else {
                break;
            }
        } while (i < e);

        do {
            c2 = text.charAt(e - 1);
            if (c2 <= 0x20) {
                e--;
            } else {
                break;
            }
        } while (i < e);

        Algo algo;
        if (c2 != '}') {
            // ()
            if (c2 == ')') {
                algo = KAT;
            }

            // []
            else if (c2 == ']') {
                if (c1 == '[') {
                    algo = JSON;
                } else {
                    return null;
                }
            }

            // <>
            else if (c2 == '>') {
                if (c1 == '<' && e > 6) {
                    algo = DOC;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            if (c1 != '{') {
                algo = KAT;
            } else {
                char ch;
                int t = i + 1;

                Strap:
                while (true) {
                    ch = text.charAt(t++);
                    switch (ch) {
                        case '"':
                        case '\'':
                        case '\\': {
                            algo = JSON;
                            break Strap;
                        }
                        default: {
                            if (ch > 0x20 || t >= e) {
                                algo = KAT;
                                break Strap;
                            }
                        }
                    }
                }
            }
        }

        Event<T> event;
        if (text instanceof Chain) {
            Chain c = (Chain) text;
            event = new Event<>(
                flag, c.reader(i, e - i)
            );
            if (c instanceof Value) {
                event.with(
                    ((Value) c).getType()
                );
            }
        } else {
            event = new Event<>(
                flag, new CharReader(text, i, e - i)
            );
        }

        try {
            return spare.solve(
                algo, event.with(supplier)
            );
        } catch (Exception ex) {
            // Nothing
        }

        return null;
    }
}
