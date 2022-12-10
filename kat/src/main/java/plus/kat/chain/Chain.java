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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.stream.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Chain implements CharSequence, Comparable<CharSequence> {

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
     * Constructs an empty chain with the specified size
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
     * @param data the specified array to be used
     */
    public Chain(
        @NotNull byte[] data
    ) {
        value = data;
        count = data.length;
    }

    /**
     * Constructs a chain with the specified chain
     *
     * @param chain the specified chain to be used
     */
    public Chain(
        @NotNull Chain chain
    ) {
        this(
            chain.toBytes()
        );
    }

    /**
     * Constructs a chain with the specified bucket
     *
     * @param bucket the specified bucket to be used
     */
    public Chain(
        @Nullable Bucket bucket
    ) {
        value = EMPTY_BYTES;
        this.bucket = bucket;
    }

    /**
     * Constructs a chain with the specified sequence
     *
     * @param chars the specified sequence to be used
     */
    public Chain(
        @Nullable CharSequence chars
    ) {
        value = EMPTY_BYTES;
        if (chars != null) {
            int size = chars.length();
            if (size != 0) {
                join(chars, 0, size);
                asset |= 2;
                backup = chars.toString();
            }
        }
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
        if ((asset & 1) == 0) {
            int h = 0, c = count;
            if (c != 0) {
                byte[] v = value;
                for (int i = 0; i < c; i++) {
                    h = 31 * h + v[i];
                }
            }
            hash = h;
            asset |= 1;
        }
        return hash;
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
     *   new Chain("k").is(b); // true
     *   new Chain("kat").is(b); // false
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
     *   new Chain("k").is('k'); // true
     *   new Chain("kat").is('k'); // false
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
     *   Chain c0 = new Chain("k");
     *
     *   c0.is(0, b); // true
     *   c0.is(1, b); // false
     *
     *   Chain c1 = new Chain("kat");
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
     *   Chain c0 = new Chain("k");
     *
     *   c0.is(0, 'k'); // true
     *   c0.is(1, 'k'); // false
     *
     *   Chain c1 = new Chain("kat");
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
     * Gets the specified index value of the {@code Latin1} chain
     *
     * <pre>{@code
     *   Chain c = new Chain("kat");
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
            } else {
                throw new ArrayIndexOutOfBoundsException(
                    "Index " + i + " exceeds length " + count
                );
            }
        } else {
            if (i < count) {
                return value[i];
            } else {
                throw new ArrayIndexOutOfBoundsException(
                    "Index " + i + " out of bounds for length " + count
                );
            }
        }
    }

    /**
     * Sets the value of the specified index for the {@code Latin1} chain
     *
     * <pre>{@code
     *   Chain c = new Chain("kat");
     *   c.set(1, (byte) 'i'); // kit
     *   c.set(-3, (byte) '$'); // $it
     *   c.set(3, (byte) 'i'); // ERROR
     *   c.set(-4, (byte) 'i'); // ERROR
     * }</pre>
     *
     * @param i the specified index
     * @param b the specified byte value
     * @throws ArrayIndexOutOfBoundsException If the specified index is out of range
     */
    public void set(int i, byte b) {
        if (i < 0) {
            i += count;
            if (0 <= i) {
                asset = 0;
                value[i] = b;
            } else {
                throw new ArrayIndexOutOfBoundsException(
                    "Index " + i + " exceeds length " + count
                );
            }
        } else {
            if (i < count) {
                asset = 0;
                value[i] = b;
            } else {
                throw new ArrayIndexOutOfBoundsException(
                    "Index " + i + " out of bounds for length " + count
                );
            }
        }
    }

    /**
     * Gets the specified index value of the {@code Latin1} chain
     *
     * <pre>{@code
     *   Chain c = new Chain("kat");
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
     *   Chain c = new Chain("kat");
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
     *   Chain c = new Chain("kat");
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
     *   new Chain().isEmpty()          = true
     *   new Chain("").isEmpty()        = true
     *   new Chain(" ").isEmpty()       = false
     *   new Chain("kat").isEmpty()     = false
     *   new Chain("  kat  ").isEmpty() = false
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
     *   new Chain().isBlank()          = true
     *   new Chain("").isBlank()        = true
     *   new Chain(" ").isBlank()       = true
     *   new Chain("  ").isBlank()      = true
     *   new Chain("kat").isBlank()     = false
     *   new Chain("  kat  ").isBlank() = false
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
     * Concatenates the buffer to this
     * {@link Chain}, copy it directly
     *
     * @param in the specified {@link ByteBuffer} to be joined
     * @throws NullPointerException If the specified buffer is null
     */
    public void join(
        @NotNull ByteBuffer in
    ) {
        int size = in.remaining();
        if (size > 0) {
            in.get(
                grow(count + size), count, size
            );
            asset = 0;
            count += size;
        }
    }

    /**
     * Concatenates the stream to this
     * {@link Chain}, copy it directly
     *
     * @param in the specified {@link InputStream} to be joined
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified stream is null
     */
    public void join(
        @NotNull InputStream in
    ) throws IOException {
        join(in, 128);
    }

    /**
     * Concatenates the stream to this
     * {@link Chain}, copy it directly
     *
     * @param in the specified {@link InputStream} to be joined
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified stream is null
     */
    public void join(
        @NotNull InputStream in, int scale
    ) throws IOException {
        if (scale > 0) {
            int m, n, length;
            byte[] it = value;
            while (true) {
                m = in.available();
                if (m != 0) {
                    if (scale < m) {
                        m = scale;
                    }
                    n = it.length - count;
                    if (n < m) {
                        n = m;
                        it = grow(
                            count + m
                        );
                    }

                    length = in.read(
                        it, count, n
                    );
                    if (length == -1) {
                        break;
                    } else {
                        asset = 0;
                        count += length;
                    }
                } else {
                    m = in.read();
                    if (m == -1) {
                        break;
                    } else {
                        int size = count + 1;
                        if (size > it.length) {
                            it = grow(size);
                        }
                        asset = 0;
                        it[count++] = (byte) m;
                    }
                }
            }
        } else {
            throw new IOException(
                "The specified scale(" + scale + ") is not positive number"
            );
        }
    }

    /**
     * Concatenates the byte to this
     * {@link Chain}, copy it directly
     *
     * @param in the specified byte value to be joined
     */
    public void join(
        @NotNull byte in
    ) {
        byte[] it = value;
        if (count != it.length) {
            asset = 0;
            it[count++] = in;
        } else {
            asset = 0;
            grow(count + 1)[count++] = in;
        }
    }

    /**
     * Concatenates the array to this
     * {@link Chain}, copy it directly
     *
     * @param in the specified source to be joined
     * @throws NullPointerException If the specified array is null
     */
    public void join(
        @NotNull byte[] in
    ) {
        byte[] it;
        int d = count;
        int l = in.length;
        if (l == 1) {
            it = grow(d + 1);
            asset = 0;
            it[count++] = in[0];
        } else if (l != 0) {
            it = grow(d + l);
            asset = 0;
            count += l;
            System.arraycopy(
                in, 0, it, d, l
            );
        }
    }

    /**
     * Concatenates the array to this
     * {@link Chain}, copy it directly
     *
     * @param in the specified source to be joined
     * @param i  the specified start index for array
     * @param l  the specified length of bytes to join
     * @throws NullPointerException           If the specified array is null
     * @throws ArrayIndexOutOfBoundsException If the index or length out of range
     */
    public void join(
        @NotNull byte[] in, int i, int l
    ) {
        if (0 <= i && 0 <= l && i + l <= in.length) {
            if (l != 0) {
                System.arraycopy(
                    in, i, grow(count + l), count, l
                );
                asset = 0;
                count += l;
            }
        } else {
            throw new ArrayIndexOutOfBoundsException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + in.length
            );
        }
    }

    /**
     * Concatenates the chain to this
     * {@link Chain}, copy it directly
     *
     * @param in the specified chain to be joined
     * @throws NullPointerException If the specified chain is null
     */
    public void join(
        @NotNull Chain in
    ) {
        byte[] it;
        int d = count;
        int l = in.count;
        if (l == 1) {
            it = grow(d + 1);
            asset = 0;
            it[count++] = in.value[0];
        } else if (l != 0) {
            it = grow(d + l);
            asset = 0;
            count += l;
            System.arraycopy(
                in.value, 0, it, d, l
            );
        }
    }

    /**
     * Concatenates the chain to this
     * {@link Chain}, copy it directly
     *
     * @param in the specified chain to be joined
     * @param i  the specified start index for chain
     * @param l  the specified length of chain to join
     * @throws NullPointerException           If the specified chain is null
     * @throws ArrayIndexOutOfBoundsException If the index or length out of range
     */
    public void join(
        @Nullable Chain in, int i, int l
    ) {
        if (0 <= i && 0 <= l && i + l <= in.count) {
            if (l != 0) {
                System.arraycopy(
                    in.value, i, grow(count + l), count, l
                );
                asset = 0;
                count += l;
            }
        } else {
            throw new ArrayIndexOutOfBoundsException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + in.count
            );
        }
    }

    /**
     * Concatenates the char to this
     * {@link Chain}, converting it to UTF-8 first
     *
     * @param in the specified character to be joined
     */
    public void join(
        @NotNull char in
    ) {
        asset = 0;
        byte[] it = value;

        // U+0000 ~ U+007F
        if (in < 0x80) {
            if (count != it.length) {
                it[count++] = (byte) in;
            } else {
                grow(count + 1)[count++] = (byte) in;
            }
        }

        // U+0080 ~ U+07FF
        else if (in < 0x800) {
            int size = count + 2;
            if (size > it.length) {
                it = grow(size);
            }
            it[count++] = (byte) (in >> 6 | 0xC0);
            it[count++] = (byte) (in & 0x3F | 0x80);
        }

        // U+10000 ~ U+10FFFF
        else if (0xD7FF < in && in < 0xE000) {
            // crippled surrogate pair
            if (count != it.length) {
                it[count++] = (byte) '?';
            } else {
                grow(count + 1)[count++] = (byte) '?';
            }
        }

        // U+0800 ~ U+FFFF
        else {
            int size = count + 3;
            if (size > it.length) {
                it = grow(size);
            }
            it[count++] = (byte) (in >> 12 | 0xE0);
            it[count++] = (byte) (in >> 6 & 0x3F | 0x80);
            it[count++] = (byte) (in & 0x3F | 0x80);
        }
    }

    /**
     * Concatenates the array to this
     * {@link Chain}, converting it to UTF-8 first
     *
     * @param in the specified array to be joined
     * @throws NullPointerException If the specified array is null
     */
    public void join(
        @NotNull char[] in
    ) {
        join(
            in, 0, in.length
        );
    }

    /**
     * Concatenates the array to this
     * {@link Chain}, converting it to UTF-8 first
     *
     * @param in the specified array to be joined
     * @param i  the specified start index for array
     * @param l  the specified length of array to join
     * @throws NullPointerException           If the specified array is null
     * @throws ArrayIndexOutOfBoundsException If the index or length out of range
     */
    public void join(
        @NotNull char[] in, int i, int l
    ) {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= in.length) {
            if (l != 0) {
                asset = 0;
                byte[] it = grow(
                    count + l
                );
                do {
                    // next char
                    char code = in[i++];

                    // U+0000 ~ U+007F
                    if (code < 0x80) {
                        if (count == it.length) {
                            it = grow(count + 1);
                        }
                        it[count++] = (byte) code;
                    }

                    // U+0080 ~ U+07FF
                    else if (code < 0x800) {
                        int size = count + 2;
                        if (size > it.length) {
                            it = grow(size);
                        }
                        it[count++] = (byte) (code >> 6 | 0xC0);
                        it[count++] = (byte) (code & 0x3F | 0x80);
                    }

                    // U+10000 ~ U+10FFFF
                    else if (0xD7FF < code && code < 0xE000) {
                        int hi = -1, lo = -1;
                        if (code < 0xDC00 && i != k) {
                            char arch = in[i];
                            if (arch > 0xDBFF &&
                                arch < 0xE000) {
                                hi = code - 0xD7C0;
                                lo = arch - 0xDC00;
                            }
                        }

                        if (hi < 0) {
                            if (count == it.length) {
                                it = grow(count + 1);
                            }
                            it[count++] = (byte) '?';
                            continue;
                        }

                        int size = count + 4;
                        if (size > it.length) {
                            it = grow(size);
                        }
                        i++; // 2 chars
                        it[count++] = (byte) (hi >> 8 | 0xF0);
                        it[count++] = (byte) (hi >> 2 & 0x3F | 0x80);
                        it[count++] = (byte) (lo >> 6 | hi << 4 & 0x30 | 0x80);
                        it[count++] = (byte) (lo & 0x3F | 0x80);
                    }

                    // U+0800 ~ U+FFFF
                    else {
                        int size = count + 3;
                        if (size > it.length) {
                            it = grow(size);
                        }
                        it[count++] = (byte) (code >> 12 | 0xE0);
                        it[count++] = (byte) (code >> 6 & 0x3F | 0x80);
                        it[count++] = (byte) (code & 0x3F | 0x80);
                    }
                } while (i < k);
            }
        } else {
            throw new ArrayIndexOutOfBoundsException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + in.length
            );
        }
    }

    /**
     * Concatenates the char sequence to this
     * {@link Chain}, converting it to UTF-8 first
     *
     * @param in the specified sequence to be joined
     * @throws NullPointerException If the specified sequence is null
     */
    public void join(
        @NotNull CharSequence in
    ) {
        join(
            in, 0, in.length()
        );
    }

    /**
     * Concatenates the char sequence to this
     * {@link Chain}, converting it to UTF-8 first
     *
     * @param in the specified sequence to be joined
     * @param i  the specified start index for sequence
     * @param l  the specified length of sequence to join
     * @throws NullPointerException           If the specified sequence is null
     * @throws ArrayIndexOutOfBoundsException If the index or length out of range
     */
    public void join(
        @NotNull CharSequence in, int i, int l
    ) {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= in.length()) {
            if (l != 0) {
                asset = 0;
                byte[] it = grow(
                    count + l
                );
                do {
                    // next char
                    char code = in.charAt(i++);

                    // U+0000 ~ U+007F
                    if (code < 0x80) {
                        if (count == it.length) {
                            it = grow(count + 1);
                        }
                        it[count++] = (byte) code;
                    }

                    // U+0080 ~ U+07FF
                    else if (code < 0x800) {
                        int size = count + 2;
                        if (size > it.length) {
                            it = grow(size);
                        }
                        it[count++] = (byte) (code >> 6 | 0xC0);
                        it[count++] = (byte) (code & 0x3F | 0x80);
                    }

                    // U+10000 ~ U+10FFFF
                    else if (0xD7FF < code && code < 0xE000) {
                        int hi = -1, lo = -1;
                        if (code < 0xDC00 && i != k) {
                            char arch = in.charAt(i);
                            if (arch > 0xDBFF &&
                                arch < 0xE000) {
                                hi = code - 0xD7C0;
                                lo = arch - 0xDC00;
                            }
                        }

                        if (hi < 0) {
                            if (count == it.length) {
                                it = grow(count + 1);
                            }
                            it[count++] = (byte) '?';
                            continue;
                        }

                        int size = count + 4;
                        if (size > it.length) {
                            it = grow(size);
                        }
                        i++; // 2 chars
                        it[count++] = (byte) (hi >> 8 | 0xF0);
                        it[count++] = (byte) (hi >> 2 & 0x3F | 0x80);
                        it[count++] = (byte) (lo >> 6 | hi << 4 & 0x30 | 0x80);
                        it[count++] = (byte) (lo & 0x3F | 0x80);
                    }

                    // U+0800 ~ U+FFFF
                    else {
                        int size = count + 3;
                        if (size > it.length) {
                            it = grow(size);
                        }
                        it[count++] = (byte) (code >> 12 | 0xE0);
                        it[count++] = (byte) (code >> 6 & 0x3F | 0x80);
                        it[count++] = (byte) (code & 0x3F | 0x80);
                    }
                } while (i < k);
            }
        } else {
            throw new ArrayIndexOutOfBoundsException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + in.length()
            );
        }
    }

    /**
     * Sets the specified length of this chain
     *
     * <pre>{@code
     *  Chain chain = ..
     *  chain.join("plus.kat");
     *  chain.slip(3);
     *  int length = value.length(); // 3
     * }</pre>
     *
     * @param length the specified length
     * @throws IndexOutOfBoundsException if the index argument is negative or out of range
     */
    public void slip(int length) {
        if (length == 0) {
            asset = 0;
            count = 0;
        } else {
            if (length < 0 || length > value.length) {
                throw new IndexOutOfBoundsException();
            }
            asset = 0;
            count = length;
        }
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
     * Copy this {@link Chain}
     * into a new {@code byte} array
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
     * Copy this {@link Chain}
     * into a new {@code byte} array
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
     * Returns the value of this
     * {@link Chain} as a {@link String}
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
     * Returns the value of this
     * {@link Chain} as a {@link String}
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

    /**
     * Resets this {@link Chain}
     */
    public void reset() {
        asset = 0;
        count = 0;
        backup = null;
    }

    /**
     * Clears this {@link Chain}
     */
    public void clear() {
        byte[] it = value;
        if (it.length != 0) {
            this.reset();
            Bucket bt = bucket;
            if (bt != null) {
                value = bt.swap(it);
            }
        }
    }

    /**
     * Closes this {@link Chain}
     */
    public void close() {
        byte[] it = value;
        if (it.length != 0) {
            this.reset();
            Bucket bt = bucket;
            if (bt == null ||
                bt.join(it)) {
                value = EMPTY_BYTES;
            }
        }
    }

    /**
     * Unsafe method
     *
     * @param min the specified minimum capacity
     */
    protected byte[] grow(int min) {
        byte[] it = value;
        if (min > it.length) {
            Bucket bt = bucket;
            if (bt == null) {
                int size = it.length;
                size += size >> 1;
                if (size < min) {
                    size = min;
                }
                it = new byte[size];
                if ((size = count) == 0) {
                    value = it;
                } else {
                    System.arraycopy(
                        value, 0, value = it, 0, size
                    );
                }
            } else {
                value = it = bt.alloc(
                    it, count, min
                );
            }
        }
        return it;
    }

    /**
     * @author kraity
     * @since 0.0.5
     */
    public static final class Unsafe {
        /**
         * Unsafe method,
         * and may be removed
         */
        @Nullable
        public static byte[] value(
            @NotNull Chain chain
        ) {
            return chain.value;
        }

        /**
         * Unsafe method,
         * and may be removed
         */
        @Nullable
        public static Bucket bucket(
            @NotNull Chain chain
        ) {
            return chain.bucket;
        }
    }
}
