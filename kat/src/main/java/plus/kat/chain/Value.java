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
package plus.kat.chain;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Value extends Space {
    /**
     * Constructs an empty value
     */
    public Value() {
        super();
    }

    /**
     * Constructs a value with the size
     *
     * @param size the specified capacity
     */
    public Value(
        int size
    ) {
        super(size);
    }

    /**
     * Constructs a value with the flow
     *
     * @param flow the specified value of binary
     */
    public Value(
        byte[] flow
    ) {
        super(flow);
    }

    /**
     * Constructs a value with the flow and size
     *
     * @param size the specified size of binary
     * @param flow the specified value of binary
     */
    public Value(
        int size,
        byte[] flow
    ) {
        super(size, flow);
    }

    /**
     * Sets the specified length of this {@link Value}
     *
     * @param i the specified size
     * @return this {@link Value} itself
     * @throws IndexOutOfBoundsException If index is out of bounds
     */
    public Value slip(int i) {
        if (i == 0) {
            size = 0;
            hash = 0;
            state = 0;
        } else {
            if (0 < i && i <= value.length) {
                size = i;
                hash = 0;
                state = 0;
            } else {
                throw new IndexOutOfBoundsException(
                    "Index<" + i + "> is out of bounds"
                );
            }
        }
        return this;
    }

    /**
     * Sets the specified length of this {@link Value}
     *
     * @param i the specified size
     * @param v the specified border
     * @return this {@link Value} itself
     * @throws IndexOutOfBoundsException If index is out of bounds
     */
    public Value slip(int i, byte v) {
        if (i == 0) {
            size = 0;
            hash = 0;
            state = v;
        } else {
            if (0 < i && i <= value.length) {
                size = i;
                hash = 0;
                state = v;
            } else {
                throw new IndexOutOfBoundsException(
                    "Index<" + i + "> is out of bounds"
                );
            }
        }
        return this;
    }

    /**
     * Returns {@code true} if this
     * {@link Value} contains only digit
     */
    public boolean isDigits() {
        int l = size;
        if (l != 0) {
            int i = 0;
            byte[] v = value;
            while (true) {
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
                    case 0x39: {
                        if (i != l) {
                            break;
                        } else {
                            return true;
                        }
                    }
                    default: {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if this
     * {@link Value} is a decimal number
     */
    public boolean isNumber() {
        int l = size;
        if (l != 0) {
            int i = 0, e = 0;
            byte[] v = value;
            while (true) {
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
                    case 0x39: {
                        if (i != l) {
                            break;
                        } else {
                            return true;
                        }
                    }
                    case 0x2B:
                    case 0x2D: {
                        if (i == 1 &&
                            l != 1) {
                            break;
                        } else {
                            return false;
                        }
                    }
                    case 0x2E: {
                        if (e == 0 &&
                            i != 1 &&
                            i != l) {
                            e = 1;
                            break;
                        } else {
                            return false;
                        }
                    }
                    default: {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if and only if this
     * {@link Value} is blank or the same as `null`
     */
    public boolean isNothing() {
        int l = size;
        if (l != 0) {
            int i = 0, e = 0;
            byte[] v = value;
            while (i < l) {
                switch (v[i++]) {
                    case 0x09:
                    case 0x0A:
                    case 0x0B:
                    case 0x0C:
                    case 0x0D:
                    case 0x1C:
                    case 0x1D:
                    case 0x1E:
                    case 0x1F:
                    case 0x20: {
                        continue;
                    }
                    case 0x6E: {
                        if (i + 2 < l &&
                            v[i++] == 0x75 &&
                            v[i++] == 0x6C &&
                            v[i++] == 0x6C) {
                            e++;
                            continue;
                        }
                    }
                }
                return false;
            }
            return e < 2;
        }
        return true;
    }

    /**
     * Returns {@code true} if and only if this
     * {@link Value} is not blank and differs from `null`
     */
    public boolean isAnything() {
        int l = size;
        if (l != 0) {
            int i = 0, e = 0;
            byte[] v = value;
            while (i < l) {
                switch (v[i++]) {
                    case 0x09:
                    case 0x0A:
                    case 0x0B:
                    case 0x0C:
                    case 0x0D:
                    case 0x1C:
                    case 0x1D:
                    case 0x1E:
                    case 0x1F:
                    case 0x20: {
                        continue;
                    }
                    case 0x6E: {
                        if (i + 2 < l &&
                            v[i++] == 0x75 &&
                            v[i++] == 0x6C &&
                            v[i++] == 0x6C) {
                            e++;
                            continue;
                        }
                    }
                }
                return true;
            }
            return e > 1;
        }
        return false;
    }

    /**
     * Parses this {@link Value}
     * as a signed decimal {@link Integer}
     *
     * @throws NumberFormatException If parsing fails
     */
    public int toInt() {
        int w, l = size;
        if (l != 0) {
            byte[] v = value;
            stage:
            {
                if (l == 1) {
                    w = v[0];
                    if (w < 48 ||
                        w > 57) {
                        break stage;
                    } else {
                        return w - 48;
                    }
                }

                int i = 0, n = 0;
                boolean e = false;

                int m = -Integer.MAX_VALUE;
                int u = -Integer.MAX_VALUE / 10;

                while (i < l) {
                    w = v[i++];
                    if (w < 58) {
                        w -= 48;
                    }

                    if (w > -1) {
                        if (w < 10) {
                            if (n < u) {
                                break stage;
                            }
                            n *= 10;
                            if (n < m + w) {
                                break stage;
                            } else {
                                n -= w;
                                continue;
                            }
                        }
                    } else {
                        if (i == 1) {
                            if (w == -5) {
                                w = v[1];
                                if (w < 48 ||
                                    w > 57) {
                                    break stage;
                                }
                                continue;
                            }

                            if (w == -3) {
                                w = v[1];
                                if (w < 48 ||
                                    w > 57) {
                                    break stage;
                                }
                                e = true;
                                m = Integer.MIN_VALUE;
                                continue;
                            }
                        }

                        if (w == -2) {
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
                    }
                    break stage;
                }
                return e ? n : -n;
            }
        }

        throw new NumberFormatException(
            "Failed to convert the value to Int, " +
                "where this value is literally `" + this + '`'
        );
    }

    /**
     * Converts this {@link Value}
     * as a signed decimal {@link Integer}
     *
     * @param def the specified default value
     * @throws IllegalArgumentException If parsing fails
     */
    public Integer toInt(Integer def) {
        int l = size;
        if (l == 0) {
            return def;
        }

        byte[] v = value;
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
                        case 0x2E: {
                            i = 1;
                            break;
                        }
                        default: {
                            i = 1;
                            r = 8;
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
                "where this value is literally `" + this + '`'
        );
    }

    /**
     * Parses this {@link Value}
     * as a signed decimal {@link Long}
     *
     * @throws NumberFormatException If parsing fails
     */
    public long toLong() {
        int w, l = size;
        if (l != 0) {
            byte[] v = value;
            stage:
            {
                if (l == 1) {
                    w = v[0];
                    if (w < 48 ||
                        w > 57) {
                        break stage;
                    } else {
                        return w - 48;
                    }
                }

                int i = 0;
                long n = 0;
                boolean e = false;

                long m = -Long.MAX_VALUE;
                long u = -Long.MAX_VALUE / 10;

                while (i < l) {
                    w = v[i++];
                    if (w < 58) {
                        w -= 48;
                    } else if (w > 96) {
                        w -= 87;
                    } else if (w > 64) {
                        w -= 55;
                    }

                    if (w > -1) {
                        if (w < 10) {
                            if (n < u) {
                                break stage;
                            }
                            n *= 10;
                            if (n < m + w) {
                                break stage;
                            } else {
                                n -= w;
                                continue;
                            }
                        }
                    } else {
                        if (i == 1) {
                            if (w == -5) {
                                w = v[1];
                                if (w < 48 ||
                                    w > 57) {
                                    break stage;
                                }
                                continue;
                            }

                            if (w == -3) {
                                w = v[1];
                                if (w < 48 ||
                                    w > 57) {
                                    break stage;
                                }
                                e = true;
                                m = Long.MIN_VALUE;
                                continue;
                            }
                        }

                        if (w == -2) {
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
                    }
                    break stage;
                }
                return e ? n : -n;
            }
        }

        throw new NumberFormatException(
            "Failed to convert the value to Long, " +
                "where this value is literally `" + this + '`'
        );
    }

    /**
     * Converts this {@link Value}
     * as a signed decimal {@link Long}
     *
     * @param def the specified default value
     * @throws IllegalArgumentException If parsing fails
     */
    public Long toLong(Long def) {
        int l = size;
        if (l == 0) {
            return def;
        }

        byte[] v = value;
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
                        case 0x2E: {
                            i = 1;
                            break;
                        }
                        default: {
                            i = 1;
                            r = 8;
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
                "where this value is literally `" + this + '`'
        );
    }

    /**
     * Parses this {@link Value} as a {@link Float}
     *
     * @throws NumberFormatException If parsing fails
     */
    @SuppressWarnings("deprecation")
    public float toFloat() {
        if (isNumber()) {
            return Float.parseFloat(
                new String(
                    value, 0, 0, size
                )
            );
        }

        throw new NumberFormatException(
            "Failed to convert the value to Float, " +
                "where this value is literally `" + this + '`'
        );
    }

    /**
     * Converts this value as a {@link Float}
     *
     * @param def the specified value
     * @throws IllegalArgumentException If parsing fails
     */
    @SuppressWarnings("deprecation")
    public Float toFloat(Float def) {
        int l = size;
        if (l == 0) {
            return def;
        }

        byte[] v = value;
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
                "where this value is literally `" + this + '`'
        );
    }

    /**
     * Parses this {@link Value} as a {@link Double}
     *
     * @throws NumberFormatException If parsing fails
     */
    @SuppressWarnings("deprecation")
    public double toDouble() {
        if (isNumber()) {
            return Double.parseDouble(
                new String(
                    value, 0, 0, size
                )
            );
        }

        throw new NumberFormatException(
            "Failed to convert the value to Double, " +
                "where this value is literally `" + this + '`'
        );
    }

    /**
     * Converts this value as a {@link Double}
     *
     * @param def the specified value
     * @throws IllegalArgumentException If parsing fails
     */
    @SuppressWarnings("deprecation")
    public Double toDouble(Double def) {
        int l = size;
        if (l == 0) {
            return def;
        }

        byte[] v = value;
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
                "where this value is literally `" + this + '`'
        );
    }

    /**
     * Converts this value as a {@link Number}
     *
     * @param def the specified value
     */
    @SuppressWarnings("deprecation")
    public Number toNumber(Number def) {
        int l = size;
        if (l == 0) {
            return def;
        }

        byte[] v = value;
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
                        case 0x2E: {
                            i = 1;
                            break;
                        }
                        default: {
                            i = 1;
                            r = 8;
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
     * Parses this {@link Value} as a {@link Boolean}
     *
     * @throws IllegalArgumentException If parsing fails
     */
    public boolean toBoolean() {
        int w, l = size;
        if (l != 0) {
            byte[] v = value;
            stage:
            {
                int i;
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
                            break;
                        } else {
                            return true;
                        }
                    }
                    case 0x30: {
                        if (l != 1) {
                            i = 0;
                            break;
                        } else {
                            return false;
                        }
                    }
                    case 0x2E:
                    case 0x2B:
                    case 0x2D: {
                        if (l != 1) {
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
                }

                int n = 0;
                while (i < l) {
                    w = v[i++];
                    if (w < 58) {
                        w -= 48;
                    }

                    if (w > -1) {
                        if (w < 10) {
                            n |= w;
                            continue;
                        }
                    } else {
                        if (w == -2) {
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
                    }
                    break stage;
                }
                return n != 0;
            }
        }

        throw new IllegalArgumentException(
            "Failed to convert the value to Boolean, " +
                "where this value is literally `" + this + '`'
        );
    }

    /**
     * Converts this value as a {@link Boolean}
     *
     * @param def the specified value
     * @throws IllegalArgumentException If parsing fails
     */
    public Boolean toBoolean(Boolean def) {
        int l = size;
        if (l == 0) {
            return def;
        }

        byte[] v = value;
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
                "where this value is literally `" + this + '`'
        );
    }
}
