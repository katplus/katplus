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
package plus.kat.kernel;

import plus.kat.anno.*;
import plus.kat.stream.*;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public abstract class Chain implements CharSequence, Comparable<CharSequence> {

    protected int count;
    protected byte[] value;

    protected int hash;
    protected String backup;

    protected int asset;
    protected Bucket bucket;

    /**
     * empty bytes
     */
    public static final byte[]
        EMPTY_BYTES = {};

    /**
     * empty chars
     */
    public static final char[]
        EMPTY_CHARS = {};

    /**
     * Constructs an empty chain
     */
    public Chain() {
        value = EMPTY_BYTES;
    }

    /**
     * Constructs a chain with the specified size
     *
     * @param size the initial capacity
     */
    public Chain(
        int size
    ) {
        if (size <= 0) {
            value = EMPTY_BYTES;
        } else {
            value = new byte[size];
        }
    }

    /**
     * Constructs a chain with the specified data
     *
     * @param data the initial byte array
     */
    public Chain(
        @Nullable byte[] data
    ) {
        value = data == null ? EMPTY_BYTES : data;
    }

    /**
     * Returns the charset of this {@link Chain}
     */
    @NotNull
    public Charset charset() {
        return UTF_8;
    }

    /**
     * Returns the hash code of this {@link Chain}
     * <p>
     * Similar to {@link String#hashCode()} when the chain is the {@code Latin1}
     *
     * @return a hash code value for this {@link Chain}
     * @see String#hashCode()
     */
    @Override
    public int hashCode() {
        int h = hash;
        if ((asset & 1) == 0) {
            h = 0;
            int size = count;
            if (size != 0) {
                byte[] v = value;
                for (int i = 0; i < size; i++) {
                    h = 31 * h + v[i];
                }
            }
            hash = h;
            asset |= 1;
        }
        return h;
    }

    /**
     * Compares a {@link String} with this {@code Latin1}
     * chain to determine if their contents are the same
     *
     * @param o the {@link String} to compare this {@link Chain} against
     */
    public boolean equals(String o) {
        if (o != null) {
            int range = o.length();
            if (count == range) {
                byte[] it = value;
                for (int i = 0; i < range; i++) {
                    if (o.charAt(i) !=
                        (char) (it[i] & 0xFF)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Compares a {@link Chain} or {@link CharSequence} with this
     * {@code Latin1} chain to determine if their contents are the same
     *
     * @param o the {@link Object} to compare this {@link Chain} against
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof Chain) {
            Chain c = (Chain) o;
            int range = c.count;
            if (count == range) {
                byte[] it = value;
                byte[] dest = c.value;
                for (int i = 0; i < range; i++) {
                    if (it[i] != dest[i]) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        if (o instanceof CharSequence) {
            CharSequence c = (CharSequence) o;
            int range = c.length();
            if (count == range) {
                byte[] it = value;
                for (int i = 0; i < range; i++) {
                    if (c.charAt(i) !=
                        (char) (it[i] & 0xFF)) {
                        return false;
                    }
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Compares this chain as the {@code Latin1}
     * with the specified {@link CharSequence} for order
     *
     * @param o the specified chars to be compared
     * @throws NullPointerException If the specified {@code chars} is null
     */
    @Override
    public int compareTo(CharSequence o) {
        if (this == o) {
            return 0;
        }

        int size = count;
        int length = o.length();

        int apex = size;
        if (apex > length) {
            apex = length;
        }

        byte[] it = value;
        for (int i = 0; i < apex; i++) {
            int arch = (
                it[i] & 0xFF
            ) - o.charAt(i);
            if (arch != 0) {
                return arch;
            }
        }

        return size - length;
    }

    /**
     * Returns a {@link CharSequence} that
     * is a subsequence of this {@link Chain}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public CharSequence subSequence(
        int start, int end
    ) {
        return toString(start, end);
    }

    /**
     * Compares this chain as the {@code Latin1} with the specified {@code byte} value
     *
     * <pre>{@code
     *   byte b = 'k';
     *   new Alpha("k").is(b); // true
     *   new Alpha("kat").is(b); // false
     * }</pre>
     *
     * @param b the specified value to be compared
     */
    public boolean is(byte b) {
        return count == 1 && value[0] == b;
    }

    /**
     * Compares this chain as the {@code Latin1} with the specified {@code char} value
     *
     * <pre>{@code
     *   new Alpha("k").is('k'); // true
     *   new Alpha("kat").is('k'); // false
     * }</pre>
     *
     * @param c the specified value to be compared
     */
    public boolean is(char c) {
        return count == 1 && (char) (value[0] & 0xFF) == c;
    }

    /**
     * Compares the specified index value of this chain
     * as the {@code Latin1} with the specified {@code byte} value
     *
     * <pre>{@code
     *   byte b = 'k';
     *   Chain c0 = new Alpha("k");
     *
     *   c0.is(0, b); // true
     *   c0.is(1, b); // false
     *
     *   Chain c1 = new Alpha("kat");
     *   c1.is(0, b); // true
     *   c1.is(1, b); // false
     *
     *   byte c = 't';
     *   c1.is(2, c); // true
     *   c1.is(1, c); // false
     * }</pre>
     *
     * @param i the specified index
     * @param b the specified value to be compared
     */
    public boolean is(int i, byte b) {
        return 0 <= i && i < count && value[i] == b;
    }

    /**
     * Compares the specified index value of the chain
     * as the {@code Latin1} with the specified {@code char} value
     *
     * <pre>{@code
     *   Chain c0 = new Alpha("k");
     *
     *   c0.is(0, 'k'); // true
     *   c0.is(1, 'k'); // false
     *
     *   Chain c1 = new Alpha("kat");
     *   c1.is(0, 'k'); // true
     *   c1.is(1, 'k'); // false
     *
     *   c1.is(2, 't'); // true
     *   c1.is(1, 't'); // false
     * }</pre>
     *
     * @param i the specified index
     * @param c the specified value to be compared
     */
    public boolean is(int i, char c) {
        return 0 <= i && i < count && (char) (value[i] & 0xFF) == c;
    }

    /**
     * Returns the specified index value of the {@code Latin1} chain
     *
     * <pre>{@code
     *   Chain c = new Alpha("kat");
     *   byte b0 = c.get(0); // 'k'
     *   byte b1 = c.get(1); // 'a'
     *   byte b2 = c.get(2); // 't'
     *   byte b3 = c.get(3); // ERROR
     *   byte b4 = c.get(-1); // 't'
     *   byte b5 = c.get(-3); // 'k'
     *   byte b6 = c.get(-4); // ERROR
     * }</pre>
     *
     * @param i the specified index
     * @throws ArrayIndexOutOfBoundsException If the specified index is out of range
     */
    public byte get(int i) {
        if (i < 0) {
            i += count;
            if (0 <= i) {
                return value[i];
            }
        } else {
            if (i < count) {
                return value[i];
            }
        }

        throw new ArrayIndexOutOfBoundsException(
            "Index " + i + " out of bounds for length " + count
        );
    }

    /**
     * Returns the specified index value of the {@code Latin1} chain
     *
     * <pre>{@code
     *   Chain c = new Alpha("kat");
     *   byte def = '$';
     *   byte b0 = c.get(0, def); // 'k'
     *   byte b1 = c.get(1, def); // 'a'
     *   byte b2 = c.get(2, def); // 't'
     *   byte b3 = c.get(3, def); // '$'
     *   byte b4 = c.get(-1, def); // 't'
     *   byte b5 = c.get(-3, def); // 'k'
     *   byte b6 = c.get(-4, def); // '$'
     * }</pre>
     *
     * @param i   the specified index
     * @param def the specified default value
     */
    public byte get(int i, byte def) {
        if (i < 0) {
            i += count;
            return i < 0 ? def : value[i];
        } else {
            return i < count ? value[i] : def;
        }
    }

    /**
     * Returns the specified index value of the {@code Latin1} chain
     *
     * <pre>{@code
     *   Chain c = new Alpha("kat");
     *   byte b0 = c.at(0); // 'k'
     *   byte b1 = c.at(1); // 'a'
     *   byte b2 = c.at(2); // 't'
     *   byte b3 = c.at(3); // ERROR
     *   byte b4 = c.at(-1); // ERROR
     *   byte b5 = c.at(-3); // ERROR
     *   byte b6 = c.at(-4); // ERROR
     * }</pre>
     *
     * @param i the specified index
     * @throws ArrayIndexOutOfBoundsException If the specified index is negative or out of range
     */
    public byte at(int i) {
        if (i < count) {
            return value[i];
        }

        throw new ArrayIndexOutOfBoundsException(
            "Index " + i + " out of bounds for length " + count
        );
    }

    /**
     * Returns the specified index value of the {@code Latin1} chain
     *
     * <pre>{@code
     *   Chain c = new Alpha("kat");
     *   char c0 = c.charAt(0); // 'k'
     *   char c1 = c.charAt(1); // 'a'
     *   char c2 = c.charAt(2); // 't'
     *   char c3 = c.charAt(3); // ERROR
     *   char c4 = c.charAt(-1); // ERROR
     *   char c5 = c.charAt(-3); // ERROR
     *   char c6 = c.charAt(-4); // ERROR
     * }</pre>
     *
     * @param i the specified index
     * @throws ArrayIndexOutOfBoundsException If the specified index is negative or out of range
     */
    @Override
    public char charAt(int i) {
        if (i < count) {
            return (char) (
                value[i] & 0xFF
            );
        }

        throw new ArrayIndexOutOfBoundsException(
            "Index " + i + " out of bounds for length " + count
        );
    }

    /**
     * Returns the length of this {@code Latin1} chain
     */
    @Override
    public int length() {
        return count;
    }

    /**
     * Returns the length of internal {@code byte} array
     *
     * @see Chain#length()
     */
    public int capacity() {
        return value.length;
    }

    /**
     * Returns true if and only if
     * the count of this chain is {@code 0}
     *
     * <pre>{@code
     *   new Alpha().isEmpty()          = true
     *   new Alpha("").isEmpty()        = true
     *   new Alpha(" ").isEmpty()       = false
     *   new Alpha("kat").isEmpty()     = false
     *   new Alpha("  kat  ").isEmpty() = false
     * }</pre>
     *
     * @see Chain#length()
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Returns true if this chain is empty
     * or contains only white space codepoints
     * <p>
     * White space: {@code 9,10,11,12,13,28,29,30,31,32}
     *
     * <pre>{@code
     *   new Alpha().isBlank()          = true
     *   new Alpha("").isBlank()        = true
     *   new Alpha(" ").isBlank()       = true
     *   new Alpha("  ").isBlank()      = true
     *   new Alpha("kat").isBlank()     = false
     *   new Alpha("  kat  ").isBlank() = false
     * }</pre>
     *
     * @see Character#isWhitespace(char)
     */
    public boolean isBlank() {
        int l = count;
        if (l != 0) {
            byte[] it = value;
            for (int i = 0; i < l; i++) {
                byte b = it[i];
                if (b > 32 || b < 9) {
                    return false;
                }
                if (13 < b && b < 28) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the index of the first match of
     * the specified byte value in this {@link Chain}
     *
     * @param b the specified value to search for
     */
    public int indexOf(byte b) {
        int i = 0, l = count;
        for (byte[] it = value; i < l; i++) {
            if (it[i] == b) return i;
        }
        return -1;
    }

    /**
     * Returns the index of the first match of
     * the specified byte value in this {@link Chain}
     *
     * @param b the specified value to search for
     * @param i the index to start the search from
     */
    public int indexOf(byte b, int i) {
        int l = count;
        if (i < 0) i = 0;
        for (byte[] it = value; i < l; i++) {
            if (it[i] == b) return i;
        }
        return -1;
    }

    /**
     * Returns the index of the last match of
     * the specified byte value in this {@link Chain}
     *
     * @param b the specified value to search for
     */
    public int lastIndexOf(byte b) {
        int i = count - 1;
        for (byte[] it = value; i > -1; i--) {
            if (it[i] == b) return i;
        }
        return -1;
    }

    /**
     * Returns the index of the last match of
     * the specified byte value in this {@link Chain}
     *
     * @param b the specified value to search for
     * @param i the index from which to start the search
     */
    public int lastIndexOf(byte b, int i) {
        int l = count;
        if (l <= i) {
            i = l - 1;
        }
        for (byte[] it = value; i > -1; i--) {
            if (it[i] == b) return i;
        }
        return -1;
    }

    /**
     * Copy this {@link Chain} into a new {@code byte} array
     */
    @NotNull
    public byte[] toBytes() {
        int size = count;
        if (size != 0) {
            byte[] copy = new byte[size];
            System.arraycopy(
                value, 0, copy, 0, size
            );
            return copy;
        }
        return EMPTY_BYTES;
    }

    /**
     * Copy this {@link Chain} into a new {@code byte} array
     *
     * @param b the specified begin index, inclusive
     * @param e the specified end index, exclusive
     * @throws IndexOutOfBoundsException If the start is negative or the end out of range
     */
    @NotNull
    public byte[] toBytes(int b, int e) {
        int size = e - b;
        if (0 <= b && 0 <= size && e <= count) {
            if (size != 0) {
                byte[] copy = new byte[size];
                System.arraycopy(
                    value, b, copy, 0, size
                );
                return copy;
            }
            return EMPTY_BYTES;
        } else {
            throw new IndexOutOfBoundsException(
                "Unexpectedly, start: " + b
                    + " end: " + e + " size: " + count
            );
        }
    }

    /**
     * Parses this {@link Chain} as a signed decimal {@code int}
     *
     * @return the specified {@code int}, {@code '0'} on error
     * @see Convert#toInt(byte[], int, int, int)
     */
    public int toInt() {
        return Convert.toInt(
            value, count, 10, 0
        );
    }

    /**
     * Parses this {@link Chain} as a signed decimal {@code int}
     *
     * @return the specified {@code int}, {@code def} value on error
     * @see Convert#toInt(byte[], int, int, int)
     */
    public int toInt(int def) {
        return Convert.toInt(
            value, count, 10, def
        );
    }

    /**
     * Parses this {@link Chain} as a signed decimal {@code long}
     *
     * @return the specified {@code long}, {@code '0L'} on error
     * @see Convert#toLong(byte[], int, long, long)
     */
    public long toLong() {
        return Convert.toLong(
            value, count, 10L, 0L
        );
    }

    /**
     * Parses this {@link Chain} as a signed decimal {@code long}
     *
     * @return the specified {@code long}, {@code def} value on error
     * @see Convert#toLong(byte[], int, long, long)
     */
    public long toLong(long def) {
        return Convert.toLong(
            value, count, 10L, def
        );
    }

    /**
     * Parses this {@link Chain} as a {@code float}
     *
     * @return the specified {@code float}, {@code '0F'} on error
     * @see Convert#toFloat(byte[], int, float)
     */
    public float toFloat() {
        return Convert.toFloat(
            value, count, 0F
        );
    }

    /**
     * Parses this {@link Chain} as a {@code float}
     *
     * @return the specified {@code float}, {@code def} value on error
     * @see Convert#toFloat(byte[], int, float)
     */
    public float toFloat(float def) {
        return Convert.toFloat(
            value, count, def
        );
    }

    /**
     * Parses this {@link Chain} as a {@code double}
     *
     * @return the specified {@code double}, {@code '0D'} on error
     * @see Convert#toDouble(byte[], int, double)
     */
    public double toDouble() {
        return Convert.toDouble(
            value, count, 0D
        );
    }

    /**
     * Parses this {@link Chain} as a {@code double}
     *
     * @return the specified {@code double}, {@code def} value on error
     * @see Convert#toDouble(byte[], int, double)
     */
    public double toDouble(double def) {
        return Convert.toDouble(
            value, count, def
        );
    }

    /**
     * Parses this {@link Chain} as a {@code int},
     * {@code long}, {@code double}, or {@code null}
     *
     * @return the specified {@link Number}, {@code 'null'} on error
     * @see Convert#toNumber(byte[], int, Number)
     */
    @Nullable
    public Number toNumber() {
        return Convert.toNumber(
            value, count, null
        );
    }

    /**
     * Parses this {@link Chain} as a {@code int},
     * {@code long}, {@code double}, or {@code def} value
     *
     * @return the specified {@link Number}, {@code def} value on error
     * @see Convert#toNumber(byte[], int, Number)
     */
    @Nullable
    public Number toNumber(Number def) {
        return Convert.toNumber(
            value, count, def
        );
    }

    /**
     * Parses this {@link Chain} as a {@code boolean}
     *
     * @return the specified {@code boolean}, {@code 'false'} on error
     * @see Convert#toBoolean(byte[], int, boolean)
     */
    public boolean toBoolean() {
        return Convert.toBoolean(
            value, count, false
        );
    }

    /**
     * Parses this {@link Chain} as a {@code boolean}
     *
     * @return the specified {@code boolean}, {@code def} value on error
     * @see Convert#toBoolean(byte[], int, boolean)
     */
    public boolean toBoolean(boolean def) {
        return Convert.toBoolean(
            value, count, def
        );
    }

    /**
     * Returns the value of this {@link Chain} as a {@link String}
     */
    @Override
    @SuppressWarnings("deprecation")
    public String toString() {
        if (count == 0) {
            return "";
        }

        if ((asset & 2) == 0) {
            asset |= 2;
        } else {
            String data = backup;
            if (data != null) {
                return data;
            }
        }

        Charset c = charset();
        if (c != ISO_8859_1) {
            return backup = new String(
                value, 0, count, c
            );
        } else {
            return backup = new String(
                value, 0, 0, count
            );
        }
    }

    /**
     * Returns the value of this {@link Chain} as a {@link String}
     *
     * @param b the specified begin index, inclusive
     * @param e the specified end index, exclusive
     * @throws ArrayIndexOutOfBoundsException If the specified begin/end index is out of range
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public String toString(int b, int e) {
        int size = e - b;
        if (0 <= b && 0 <= size &&
            e <= count) {
            if (size == 0) {
                return "";
            }
            if (size != count) {
                Charset c = charset();
                if (c != ISO_8859_1) {
                    return new String(
                        value, b, size, c
                    );
                } else {
                    return new String(
                        value, 0, 0, size
                    );
                }
            }
            return toString();
        } else {
            throw new ArrayIndexOutOfBoundsException(
                "Specified begin(" + b + ")/end(" + e +
                    ") index is out of range: " + count
            );
        }
    }
}
