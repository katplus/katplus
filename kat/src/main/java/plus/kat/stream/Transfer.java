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

/**
 * @author kraity
 * @since 0.0.6
 */
public final class Transfer {

    private Transfer() {
        throw new IllegalStateException();
    }

    /**
     * Converts the specified binary
     * as a signed decimal {@link Long}
     *
     * @throws IllegalArgumentException If parsing fails
     */
    public static Long toLong(
        Binary bin, Long def
    ) {
        int l = bin.size;
        if (l == 0) {
            return def;
        }

        byte[] v = bin.value;
        stage:
        {
            int w = v[0];
            if (l == 1) {
                if (w < 48 ||
                    w > 57) {
                    break stage;
                } else {
                    return w - 48L;
                }
            }

            int i = 0, r = 10;
            long m = -Long.MAX_VALUE;

            boolean e = false;
            switch (w) {
                case 0x2E:
                case 0x31:
                case 0x32:
                case 0x33:
                case 0x34:
                case 0x35:
                case 0x36:
                case 0x37:
                case 0x38:
                case 0x39:
                    break;
                default: {
                    break stage;
                }
                case 0x2D: {
                    e = true;
                    m = Long.MIN_VALUE;
                }
                case 0x2B: {
                    w = v[i = 1];
                    if (w > 47 &&
                        w < 58) {
                        break;
                    } else {
                        break stage;
                    }
                }
                case 0x74: {
                    if (l == 4) {
                        if (v[1] == 0x72 &&
                            v[2] == 0x75 &&
                            v[3] == 0x65) {
                            return 1L;
                        }
                    }
                    break stage;
                }
                case 0x66: {
                    if (l == 5) {
                        if (v[1] == 0x61 &&
                            v[2] == 0x6C &&
                            v[3] == 0x73 &&
                            v[4] == 0x65) {
                            return 0L;
                        }
                    }
                    break stage;
                }
                case 0x6E: {
                    if (l == 4) {
                        if (v[1] == 0x75 &&
                            v[2] == 0x6C &&
                            v[3] == 0x6C) {
                            return def;
                        }
                    }
                    break stage;
                }
                case 0x30: {
                    switch (v[1]) {
                        case 0x42:
                        case 0x62: {
                            i = 2;
                            r = 2;
                            break;
                        }
                        default: {
                            i = 1;
                            r = 8;
                            break;
                        }
                        case 0x2E: {
                            i = 1;
                            r = 10;
                            break;
                        }
                        case 0x58:
                        case 0x78: {
                            i = 2;
                            r = 16;
                            break;
                        }
                    }
                    if (i == 2) {
                        if (l == 2) {
                            break stage;
                        }
                        while (i < l &&
                            v[i] == 48) i++;
                    }
                }
            }

            long n = 0;
            long u = m / r;

            actor:
            while (i < l) {
                w = v[i++];
                if (w < 58) {
                    w -= 48;
                } else if (w > 96) {
                    w -= 87;
                } else if (w > 64) {
                    w -= 55;
                }

                if (w < r) {
                    if (w > -1) {
                        if (n < u) {
                            break stage;
                        }
                        n *= r;
                        if (n < m + w) {
                            break stage;
                        } else {
                            n -= w;
                            continue;
                        }
                    }

                    if (w == -2 && r == 10) {
                        space:
                        while (i < l) {
                            switch (v[i++]) {
                                case 0x30:
                                case 0x31:
                                case 0x32:
                                case 0x33:
                                case 0x34:
                                case 0x35:
                                case 0x36:
                                case 0x37:
                                case 0x38:
                                case 0x39:
                                    break;
                                case 0x44:
                                case 0x46:
                                case 0x64:
                                case 0x66:
                                    break space;
                                default: {
                                    break stage;
                                }
                            }
                        }
                        if (i == l) {
                            break;
                        } else {
                            break stage;
                        }
                    }
                } else {
                    switch (w) {
                        case 13:
                        case 15:
                        case 21: {
                            if (i == l) {
                                break actor;
                            } else {
                                break stage;
                            }
                        }
                    }
                }
                break stage;
            }
            return e ? n : -n;
        }

        throw new IllegalArgumentException(
            "Failed to convert the value to Long, " +
                "where this value is literally `" + bin + '`'
        );
    }

    /**
     * Converts the specified binary
     * as a signed decimal {@link Integer}
     *
     * @throws IllegalArgumentException If parsing fails
     */
    public static Integer toInteger(
        Binary bin, Integer def
    ) {
        int l = bin.size;
        if (l == 0) {
            return def;
        }

        byte[] v = bin.value;
        stage:
        {
            int w = v[0];
            if (l == 1) {
                if (w < 48 ||
                    w > 57) {
                    break stage;
                } else {
                    return w - 48;
                }
            }

            int i = 0, r = 10;
            int m = -Integer.MAX_VALUE;

            boolean e = false;
            switch (w) {
                case 0x2E:
                case 0x31:
                case 0x32:
                case 0x33:
                case 0x34:
                case 0x35:
                case 0x36:
                case 0x37:
                case 0x38:
                case 0x39:
                    break;
                default: {
                    break stage;
                }
                case 0x2D: {
                    e = true;
                    m = Integer.MIN_VALUE;
                }
                case 0x2B: {
                    w = v[i = 1];
                    if (w > 47 &&
                        w < 58) {
                        break;
                    } else {
                        break stage;
                    }
                }
                case 0x74: {
                    if (l == 4) {
                        if (v[1] == 0x72 &&
                            v[2] == 0x75 &&
                            v[3] == 0x65) {
                            return 1;
                        }
                    }
                    break stage;
                }
                case 0x66: {
                    if (l == 5) {
                        if (v[1] == 0x61 &&
                            v[2] == 0x6C &&
                            v[3] == 0x73 &&
                            v[4] == 0x65) {
                            return 0;
                        }
                    }
                    break stage;
                }
                case 0x6E: {
                    if (l == 4) {
                        if (v[1] == 0x75 &&
                            v[2] == 0x6C &&
                            v[3] == 0x6C) {
                            return def;
                        }
                    }
                    break stage;
                }
                case 0x30: {
                    switch (v[1]) {
                        case 0x42:
                        case 0x62: {
                            i = 2;
                            r = 2;
                            break;
                        }
                        default: {
                            i = 1;
                            r = 8;
                            break;
                        }
                        case 0x2E: {
                            i = 1;
                            r = 10;
                            break;
                        }
                        case 0x58:
                        case 0x78: {
                            i = 2;
                            r = 16;
                            break;
                        }
                    }
                    if (i == 2) {
                        if (l == 2) {
                            break stage;
                        }
                        while (i < l &&
                            v[i] == 48) i++;
                    }
                }
            }

            int n = 0;
            int u = m / r;

            actor:
            while (i < l) {
                w = v[i++];
                if (w < 58) {
                    w -= 48;
                } else if (w > 96) {
                    w -= 87;
                } else if (w > 64) {
                    w -= 55;
                }

                if (w < r) {
                    if (w > -1) {
                        if (n < u) {
                            break stage;
                        }
                        n *= r;
                        if (n < m + w) {
                            break stage;
                        } else {
                            n -= w;
                            continue;
                        }
                    }

                    if (w == -2 && r == 10) {
                        space:
                        while (i < l) {
                            switch (v[i++]) {
                                case 0x30:
                                case 0x31:
                                case 0x32:
                                case 0x33:
                                case 0x34:
                                case 0x35:
                                case 0x36:
                                case 0x37:
                                case 0x38:
                                case 0x39:
                                    break;
                                case 0x44:
                                case 0x46:
                                case 0x64:
                                case 0x66:
                                    break space;
                                default: {
                                    break stage;
                                }
                            }
                        }
                        if (i == l) {
                            break;
                        } else {
                            break stage;
                        }
                    }
                } else {
                    switch (w) {
                        case 13:
                        case 15:
                        case 21: {
                            if (i == l) {
                                break actor;
                            } else {
                                break stage;
                            }
                        }
                    }
                }
                break stage;
            }
            return e ? n : -n;
        }

        throw new IllegalArgumentException(
            "Failed to convert the value to Integer, " +
                "where this value is literally `" + bin + '`'
        );
    }

    /**
     * Converts the specified binary as a {@link Float}
     *
     * @throws IllegalArgumentException If parsing fails
     */
    @SuppressWarnings("deprecation")
    public static Float toFloat(
        Binary bin, Float def
    ) {
        int l = bin.size;
        if (l == 0) {
            return def;
        }

        byte[] v = bin.value;
        stage:
        {
            switch (v[0]) {
                case 0x2B:
                case 0x2D:
                case 0x2E:
                case 0x30:
                case 0x31:
                case 0x32:
                case 0x33:
                case 0x34:
                case 0x35:
                case 0x36:
                case 0x37:
                case 0x38:
                case 0x39:
                    break;
                default: {
                    break stage;
                }
                case 0x74: {
                    if (l == 4) {
                        if (v[1] == 0x72 &&
                            v[2] == 0x75 &&
                            v[3] == 0x65) {
                            return 1F;
                        }
                    }
                    break stage;
                }
                case 0x66: {
                    if (l == 5) {
                        if (v[1] == 0x61 &&
                            v[2] == 0x6C &&
                            v[3] == 0x73 &&
                            v[4] == 0x65) {
                            return 0F;
                        }
                    }
                    break stage;
                }
                case 0x6E: {
                    if (l == 4) {
                        if (v[1] == 0x75 &&
                            v[2] == 0x6C &&
                            v[3] == 0x6C) {
                            return def;
                        }
                    }
                    break stage;
                }
            }

            if (l > 26) {
                int i = 1;
                while (i < l) {
                    switch (v[i++]) {
                        case 0x2B:
                        case 0x2D:
                        case 0x2E:
                        case 0x30:
                        case 0x31:
                        case 0x32:
                        case 0x33:
                        case 0x34:
                        case 0x35:
                        case 0x36:
                        case 0x37:
                        case 0x38:
                        case 0x39:
                        case 0x45:
                        case 0x65:
                            break;
                        default: {
                            break stage;
                        }
                    }
                }
            }

            return Float.parseFloat(
                new String(
                    v, 0, 0, l
                )
            );
        }

        throw new IllegalArgumentException(
            "Failed to convert the value to Float, " +
                "where this value is literally `" + bin + '`'
        );
    }

    /**
     * Converts the specified binary as a {@link Double}
     *
     * @throws IllegalArgumentException If parsing fails
     */
    @SuppressWarnings("deprecation")
    public static Double toDouble(
        Binary bin, Double def
    ) {
        int l = bin.size;
        if (l == 0) {
            return def;
        }

        byte[] v = bin.value;
        stage:
        {
            switch (v[0]) {
                case 0x2B:
                case 0x2D:
                case 0x2E:
                case 0x30:
                case 0x31:
                case 0x32:
                case 0x33:
                case 0x34:
                case 0x35:
                case 0x36:
                case 0x37:
                case 0x38:
                case 0x39:
                    break;
                default: {
                    break stage;
                }
                case 0x74: {
                    if (l == 4) {
                        if (v[1] == 0x72 &&
                            v[2] == 0x75 &&
                            v[3] == 0x65) {
                            return 1D;
                        }
                    }
                    break stage;
                }
                case 0x66: {
                    if (l == 5) {
                        if (v[1] == 0x61 &&
                            v[2] == 0x6C &&
                            v[3] == 0x73 &&
                            v[4] == 0x65) {
                            return 0D;
                        }
                    }
                    break stage;
                }
                case 0x6E: {
                    if (l == 4) {
                        if (v[1] == 0x75 &&
                            v[2] == 0x6C &&
                            v[3] == 0x6C) {
                            return def;
                        }
                    }
                    break stage;
                }
            }

            if (l > 26) {
                int i = 1;
                while (i < l) {
                    switch (v[i++]) {
                        case 0x2B:
                        case 0x2D:
                        case 0x2E:
                        case 0x30:
                        case 0x31:
                        case 0x32:
                        case 0x33:
                        case 0x34:
                        case 0x35:
                        case 0x36:
                        case 0x37:
                        case 0x38:
                        case 0x39:
                        case 0x45:
                        case 0x65:
                            break;
                        default: {
                            break stage;
                        }
                    }
                }
            }

            return Double.parseDouble(
                new String(
                    v, 0, 0, l
                )
            );
        }

        throw new IllegalArgumentException(
            "Failed to convert the value to Double, " +
                "where this value is literally `" + bin + '`'
        );
    }

    /**
     * Converts the specified binary as a {@link Number}
     */
    @SuppressWarnings("deprecation")
    public static Number toNumber(
        Binary bin, Number def
    ) {
        int l = bin.size;
        if (l == 0) {
            return def;
        }

        byte[] v = bin.value;
        stage:
        {
            int w = v[0];
            if (l == 1) {
                if (w < 48 ||
                    w > 57) {
                    break stage;
                } else {
                    return w - 48;
                }
            }

            int i = 0, r = 10;
            long m = -Long.MAX_VALUE;

            boolean e = false;
            switch (w) {
                case 0x2E:
                case 0x31:
                case 0x32:
                case 0x33:
                case 0x34:
                case 0x35:
                case 0x36:
                case 0x37:
                case 0x38:
                case 0x39:
                    break;
                default: {
                    break stage;
                }
                case 0x2D: {
                    e = true;
                    m = Long.MIN_VALUE;
                }
                case 0x2B: {
                    w = v[i = 1];
                    if (w > 47 &&
                        w < 58) {
                        break;
                    } else {
                        break stage;
                    }
                }
                case 0x74: {
                    if (l == 4) {
                        if (v[1] == 0x72 &&
                            v[2] == 0x75 &&
                            v[3] == 0x65) {
                            return 1;
                        }
                    }
                    break stage;
                }
                case 0x66: {
                    if (l == 5) {
                        if (v[1] == 0x61 &&
                            v[2] == 0x6C &&
                            v[3] == 0x73 &&
                            v[4] == 0x65) {
                            return 0;
                        }
                    }
                    break stage;
                }
                case 0x30: {
                    switch (v[1]) {
                        case 0x42:
                        case 0x62: {
                            i = 2;
                            r = 2;
                            break;
                        }
                        default: {
                            i = 1;
                            r = 8;
                            break;
                        }
                        case 0x2E: {
                            i = 1;
                            r = 10;
                            break;
                        }
                        case 0x58:
                        case 0x78: {
                            i = 2;
                            r = 16;
                            break;
                        }
                    }
                    if (i == 2) {
                        if (l == 2) {
                            break stage;
                        }
                        while (i < l &&
                            v[i] == 48) i++;
                    }
                }
            }

            long n = 0;
            long u = m / r;

            actor:
            while (i < l) {
                w = v[i++];
                if (w < 58) {
                    w -= 48;
                } else if (w > 96) {
                    w -= 87;
                } else if (w > 64) {
                    w -= 55;
                }

                if (w < r) {
                    if (w > -1) {
                        if (n < u) {
                            break stage;
                        }
                        n *= r;
                        if (n < m + w) {
                            break stage;
                        } else {
                            n -= w;
                            continue;
                        }
                    }

                    if (w == -2 && r == 10) {
                        space:
                        while (i < l) {
                            switch (v[i++]) {
                                case 0x31:
                                case 0x32:
                                case 0x33:
                                case 0x34:
                                case 0x35:
                                case 0x36:
                                case 0x37:
                                case 0x38:
                                case 0x39:
                                    w = 0;
                                case 0x30:
                                    break;
                                default: {
                                    break stage;
                                }
                                case 0x44:
                                case 0x64: {
                                    if (i == l) {
                                        break space;
                                    } else {
                                        break stage;
                                    }
                                }
                                case 0x46:
                                case 0x66: {
                                    if (i != l) {
                                        break stage;
                                    }
                                    return Float.parseFloat(
                                        new String(
                                            v, 0, 0, l
                                        )
                                    );
                                }
                            }
                        }
                        if (i != l) {
                            break stage;
                        }
                        if (w == -2) {
                            return (double) (
                                e ? n : -n
                            );
                        } else {
                            return Double.parseDouble(
                                new String(
                                    v, 0, 0, l
                                )
                            );
                        }
                    }
                } else {
                    switch (w) {
                        case 21: {
                            if (i != l) {
                                return def;
                            } else {
                                return e ? n : -n;
                            }
                        }
                        case 15: {
                            if (i != l) {
                                return def;
                            } else {
                                return (float) (e ? n : -n);
                            }
                        }
                        case 13: {
                            if (i != l) {
                                return def;
                            } else {
                                return (double) (e ? n : -n);
                            }
                        }
                    }
                }
                break stage;
            }

            if (n < m / 0x100000000L) {
                return e ? n : -n;
            } else {
                return (int) (e ? n : -n);
            }
        }

        return def;
    }

    /**
     * Converts the specified binary as a {@link Boolean}
     *
     * @throws IllegalArgumentException If parsing fails
     */
    public static Boolean toBoolean(
        Binary bin, Boolean def
    ) {
        int l = bin.size;
        if (l == 0) {
            return def;
        }

        byte[] v = bin.value;
        stage:
        {
            int w, i, r;
            switch (v[0]) {
                case 0x74: {
                    if (l == 4) {
                        if (v[1] == 0x72 &&
                            v[2] == 0x75 &&
                            v[3] == 0x65) {
                            return true;
                        }
                    }
                    break stage;
                }
                case 0x54: {
                    if (l == 4) {
                        w = v[1];
                        if (w == 0x72) {
                            if (v[2] == 0x75 &&
                                v[3] == 0x65) {
                                return true;
                            }
                        } else if (w == 0x52) {
                            if (v[2] == 0x55 &&
                                v[3] == 0x45) {
                                return true;
                            }
                        }
                    }
                    break stage;
                }
                case 0x6E: {
                    if (l == 4) {
                        if (v[1] == 0x75 &&
                            v[2] == 0x6C &&
                            v[3] == 0x6C) {
                            return def;
                        }
                    }
                    break stage;
                }
                case 0x66: {
                    if (l == 5) {
                        if (v[1] == 0x61 &&
                            v[2] == 0x6C &&
                            v[3] == 0x73 &&
                            v[4] == 0x65) {
                            return false;
                        }
                    }
                    break stage;
                }
                case 0x46: {
                    if (l == 5) {
                        w = v[1];
                        if (w == 0x61) {
                            if (v[2] == 0x6C &&
                                v[3] == 0x73 &&
                                v[4] == 0x65) {
                                return false;
                            }
                        } else if (w == 0x41) {
                            if (v[2] == 0x4C &&
                                v[3] == 0x53 &&
                                v[4] == 0x45) {
                                return false;
                            }
                        }
                    }
                    break stage;
                }
                case 0x31:
                case 0x32:
                case 0x33:
                case 0x34:
                case 0x35:
                case 0x36:
                case 0x37:
                case 0x38:
                case 0x39: {
                    if (l != 1) {
                        i = 0;
                        r = 10;
                        break;
                    } else {
                        return true;
                    }
                }
                case 0x2E:
                case 0x2B:
                case 0x2D: {
                    if (l != 1) {
                        r = 10;
                        w = v[i = 1];
                        if (w > 47 &&
                            w < 58) {
                            break;
                        } else {
                            break stage;
                        }
                    }
                }
                default: {
                    break stage;
                }
                case 0x30: {
                    if (l != 1) {
                        switch (v[1]) {
                            case 0x42:
                            case 0x62: {
                                i = 2;
                                r = 2;
                                break;
                            }
                            default: {
                                i = 1;
                                r = 10;
                                break;
                            }
                            case 0x58:
                            case 0x78: {
                                i = 2;
                                r = 16;
                                break;
                            }
                        }
                        if (i == 2) {
                            if (l == 2) {
                                break stage;
                            }
                            while (i < l &&
                                v[i] == 48) i++;
                        }
                        break;
                    } else {
                        return false;
                    }
                }
            }

            int n = 0;
            while (i < l) {
                w = v[i++];
                if (w < 58) {
                    w -= 48;
                } else if (w > 96) {
                    w -= 87;
                } else if (w > 64) {
                    w -= 55;
                }

                if (w < r) {
                    if (w > -1) {
                        n |= w;
                        continue;
                    }
                    if (w == -2 && r == 10) {
                        space:
                        while (i < l) {
                            switch (v[i++]) {
                                case 0x31:
                                case 0x32:
                                case 0x33:
                                case 0x34:
                                case 0x35:
                                case 0x36:
                                case 0x37:
                                case 0x38:
                                case 0x39:
                                    n = 1;
                                case 0x30:
                                    break;
                                case 0x44:
                                case 0x46:
                                case 0x64:
                                case 0x66:
                                    break space;
                                default: {
                                    break stage;
                                }
                            }
                        }
                        if (i != l) {
                            break stage;
                        } else {
                            return n != 0;
                        }
                    }
                } else {
                    switch (w) {
                        case 13:
                        case 15:
                        case 21: {
                            if (i != l) {
                                break stage;
                            } else {
                                return n != 0;
                            }
                        }
                    }
                }
                break stage;
            }
            return n != 0;
        }

        throw new IllegalArgumentException(
            "Failed to convert the value to Boolean, " +
                "where this value is literally `" + bin + '`'
        );
    }
}
