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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import javax.crypto.*;
import java.io.*;
import java.security.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import plus.kat.crash.*;
import plus.kat.stream.*;
import plus.kat.utils.Config;

import static plus.kat.stream.Binary.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kraity
 * @since 0.0.1
 */
public abstract class Chain implements CharSequence, Comparable<CharSequence> {

    protected int count;
    protected byte[] value;

    protected int hash;
    protected Type type;
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
     * default
     */
    public Chain() {
        value = EMPTY_BYTES;
    }

    /**
     * Initialize a {@code byte[]} of the specified size internally
     *
     * @param size the initial capacity
     */
    public Chain(
        int size
    ) {
        value = size > 0 ? new byte[size] : EMPTY_BYTES;
    }

    /**
     * Initialize the specified byte[] internally
     *
     * @param data the initial byte array
     */
    public Chain(
        @Nullable byte[] data
    ) {
        value = data == null ? EMPTY_BYTES : data;
    }

    /**
     * Initialize the internal specified byte[] to copy from {@link Chain}
     *
     * @param chain specify the {@link Chain} to be mirrored
     */
    public Chain(
        @Nullable Chain chain
    ) {
        if (chain == null) {
            value = EMPTY_BYTES;
        } else {
            value = chain.toBytes();
            count = value.length;
        }
    }

    /**
     * Initialize the internal specified {@code bucket}
     *
     * @param bucket the specified {@link Bucket} to be used
     */
    public Chain(
        @Nullable Bucket bucket
    ) {
        value = EMPTY_BYTES;
        this.bucket = bucket;
    }

    /**
     * Returns a hash code for this {@link Chain}
     * <p>
     * {@link Chain} is similar to {@link String#hashCode()} when {@code byte[]} is ascii codes
     *
     * @return a hash code value for this {@link Chain}
     * @see String#hashCode()
     */
    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            int l = count;
            if (l != 0) {
                byte[] v = value;
                for (int i = 0; i < l; i++) {
                    h = 31 * h + v[i];
                }
                hash = h;
            }
        }
        return h;
    }

    /**
     * Compares a {@link Chain} or {@link CharSequence} to this chain
     * to determine if their contents are equal, only supports ASCII code comparison
     *
     * @param o the {@link Object} to compare this {@link Chain} against
     */
    @Override
    public boolean equals(
        @Nullable Object o
    ) {
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
     * Returns a {@link CharSequence} of this {@link Chain}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @NotNull
    @Override
    public CharSequence subSequence(
        int start, int end
    ) {
        if (end <= count) {
            return new String(
                value, start, end - start, UTF_8
            );
        } else {
            throw new StringIndexOutOfBoundsException(end);
        }
    }

    /**
     * Compares this {@link Chain} with the specified
     * {@link CharSequence} for order, only supports ASCII code comparison
     *
     * @param o the {@link CharSequence} to be compared
     * @throws NullPointerException If the specified {@code chars} is null
     * @see String#compareTo(String)
     */
    @Override
    public int compareTo(
        @NotNull CharSequence o
    ) {
        if (this == o) {
            return 0;
        }

        int len1, len2, res,
            limit = Math.min(
                len1 = count,
                len2 = o.length()
            );

        byte[] it = value;
        for (int i = 0; i < limit; i++) {
            res = (it[i] & 0xFF) - (
                o.charAt(i) & 0xFFFF
            );
            if (res != 0) {
                return res;
            }
        }

        return len1 - len2;
    }

    /**
     * Compares this chain and specified {@code byte} value
     *
     * <pre>{@code
     *   byte b = 'k';
     *   new Value("k").is(b); // true
     *   new Value("kat").is(b); // false
     * }</pre>
     *
     * @param b the byte value to be compared
     */
    public boolean is(
        byte b
    ) {
        return count == 1 && value[0] == b;
    }

    /**
     * Compares this UTF-8 chain and specified {@code char} value
     *
     * <pre>{@code
     *   char c = 'k';
     *   new Value("k").is(c); // true
     *   new Value("kat").is(c); // false
     * }</pre>
     *
     * @param c the char value to be compared
     */
    public boolean is(
        char c
    ) {
        int l = count;
        byte[] it = value;

        // U+0000 ~ U+007F
        if (c < 0x80) {
            if (l != 1) {
                return false;
            }

            return it[0] == (byte) c;
        }

        // U+0080 ~ U+07FF
        else if (c < 0x800) {
            if (l != 2) {
                return false;
            }

            return it[0] == (byte) ((c >> 6) | 0xC0)
                && it[1] == (byte) ((c & 0x3F) | 0x80);
        }

        // U+10000 ~ U+10FFFF
        // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
        else if (c >= 0xD800 && c <= 0xDFFF) {
            return false;
        }

        // U+0800 ~ U+FFFF
        else {
            if (l != 3) {
                return false;
            }

            return it[0] == (byte) ((c >> 12) | 0xE0)
                && it[1] == (byte) (((c >> 6) & 0x3F) | 0x80)
                && it[2] == (byte) ((c & 0x3F) | 0x80);
        }
    }

    /**
     * Compares the specified index value of chain and specified {@code byte} value
     *
     * <pre>{@code
     *   byte b = 'k';
     *   Chain c0 = new Value("k");
     *
     *   c0.is(0, b); // true
     *   c0.is(1, b); // false
     *
     *   Chain c1 = new Value("kat");
     *   c1.is(0, b); // true
     *   c1.is(1, b); // false
     *
     *   byte c = 't';
     *   c1.is(2, c); // true
     *   c1.is(1, c); // false
     * }</pre>
     *
     * @param i the specified index
     * @param b the byte value to be compared
     * @throws ArrayIndexOutOfBoundsException if the {@code index} argument is negative
     */
    public boolean is(
        int i, byte b
    ) {
        return i < count && value[i] == b;
    }

    /**
     * Compares the specified index value of UTF8 chain and specified {@code char} value
     *
     * <pre>{@code
     *   char b = 'k';
     *   Chain c0 = new Value("k");
     *
     *   c0.is(0, b); // true
     *   c0.is(1, b); // false
     *
     *   Chain c1 = new Value("kat");
     *   c1.is(0, b); // true
     *   c1.is(1, b); // false
     *
     *   char c = 't';
     *   c1.is(2, c); // true
     *   c1.is(1, c); // false
     * }</pre>
     *
     * @param i the specified index
     * @param c the byte value to be compared
     * @throws ArrayIndexOutOfBoundsException if the {@code index} argument is negative
     */
    public boolean is(
        int i, char c
    ) {
        int l = count;
        if (i >= l) {
            return false;
        }

        int o = 0;
        byte[] it = value;

        for (int k = 0; k < l; o++) {
            if (i == o) {
                // U+0000 ~ U+007F
                if (c < 0x80) {
                    return it[k] == (byte) c;
                }

                // U+0080 ~ U+07FF
                else if (c < 0x800) {
                    if (k + 2 > l) {
                        return false;
                    }

                    return it[k] == (byte) ((c >> 6) | 0xC0)
                        && it[k + 1] == (byte) ((c & 0x3F) | 0x80);
                }

                // U+10000 ~ U+10FFFF
                // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
                else if (c >= 0xD800 && c <= 0xDFFF) {
                    if (k + 2 >= l ||
                        c > 0xDBFF) {
                        return false;
                    }

                    byte b2 = it[k + 1];
                    byte b3 = it[k + 2];
                    return c == (char) (
                        ((0xD8 | (it[k] & 0x03)) << 8) |
                            ((((b2 - 0x10 >> 2)) & 0x0F) << 4) |
                            (((b2 & 0x03) << 2) | ((b3 >> 4) & 0x03))
                    );
                }

                // U+0800 ~ U+FFFF
                else {
                    if (k + 3 > l) {
                        return false;
                    }

                    return it[k] == (byte) ((c >> 12) | 0xE0)
                        && it[k + 1] == (byte) (((c >> 6) & 0x3F) | 0x80)
                        && it[k + 2] == (byte) ((c & 0x3F) | 0x80);
                }
            }

            // get byte
            byte b = it[k];

            // U+0000 ~ U+007F
            // 0xxxxxxx
            if (b >= 0) {
                k++;
            }

            // U+0080 ~ U+07FF
            // 110xxxxx 10xxxxxx
            else if ((b >> 5) == -2) {
                k += 2;
            }

            // U+0800 ~ U+FFFF
            // 1110xxxx 10xxxxxx 10xxxxxx
            else if ((b >> 4) == -2) {
                k += 3;
            }

            // U+10000 ~ U+10FFFF
            // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
            // 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
            else if ((b >> 3) == -2) {
                if (i != ++o) {
                    k += 4;
                    continue;
                }

                if (k + 3 >= l ||
                    c < 0xDC00 ||
                    c > 0xDFFF) {
                    return false;
                }

                byte b3 = it[k + 2];
                byte b4 = it[k + 3];
                return c == (char) (
                    ((0xDC | ((b3 >> 2) & 0x03)) << 8) |
                        ((((b3 & 0x3) << 2) | ((b4 >> 4) & 0x03)) << 4) | (b4 & 0x0F)
                );
            }

            // beyond the current range
            else {
                return false;
            }
        }

        return false;
    }

    /**
     * Compares this UTF8 chain and specified {@link CharSequence}
     *
     * <pre>{@code
     *   new Value("k").is("k"); // true
     *   new Value("kat").is("k"); // false
     *
     *   new Value("k").is("kat"); // false
     *   new Value("kat").is("kat"); // true
     * }</pre>
     *
     * @param ch the {@link CharSequence} to compare this {@link Chain} against
     * @throws NullPointerException If the specified {@code chars} is null
     */
    public boolean is(
        @Nullable CharSequence ch
    ) {
        if (ch == null) {
            return false;
        }

        int l = count;
        int r = ch.length();

        int i = 0, j = 0;
        byte[] it = value;

        for (; i < l && j < r; j++) {
            // get char
            char c = ch.charAt(j);

            // U+0000 ~ U+007F
            if (c < 0x80) {
                if (it[i++] != (byte) c) {
                    return false;
                }
            }

            // U+0080 ~ U+07FF
            else if (c < 0x800) {
                if (i + 2 > l) {
                    return false;
                }

                if (it[i++] != (byte) ((c >> 6) | 0xC0) ||
                    it[i++] != (byte) ((c & 0x3F) | 0x80)) {
                    return false;
                }
            }

            // U+10000 ~ U+10FFFF
            // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
            else if (c >= 0xD800 && c <= 0xDFFF) {
                if (i + 4 > l) {
                    return false;
                }

                if (++j >= r) {
                    return false;
                }

                char d = ch.charAt(j);
                if (d < 0xDC00 || d > 0xDFFF) {
                    return false;
                }

                int u = (c << 10) + d - 0x35F_DC00;
                if (it[i++] != (byte) ((u >> 18) | 0xF0) ||
                    it[i++] != (byte) (((u >> 12) & 0x3F) | 0x80) ||
                    it[i++] != (byte) (((u >> 6) & 0x3F) | 0x80) ||
                    it[i++] != (byte) ((u & 0x3F) | 0x80)) {
                    return false;
                }
            }

            // U+0800 ~ U+FFFF
            else {
                if (i + 3 > l) {
                    return false;
                }

                if (it[i++] != (byte) ((c >> 12) | 0xE0) ||
                    it[i++] != (byte) (((c >> 6) & 0x3F) | 0x80) ||
                    it[i++] != (byte) ((c & 0x3F) | 0x80)) {
                    return false;
                }
            }
        }

        return i == l && j == r;
    }

    /**
     * Compares this chain and specified {@code byte[]}
     *
     * <pre>{@code
     *   byte[] b = new byte[]{'k'};
     *   new Value("k").same(b); // true
     *   new Value("kat").same(b); // false
     *
     *   byte[] c = new byte[]{'k', 'a', 't'};
     *   new Value("k").same(c); // false
     *   new Value("kat").same(c); // true
     * }</pre>
     *
     * @param b the {@code byte[]} to compare this {@link Chain} against
     * @throws NullPointerException If the specified {@code bytes} is null
     * @since 0.0.4
     */
    public boolean same(
        @Nullable byte[] b
    ) {
        if (b != null) {
            int range = b.length;
            if (count == range) {
                byte[] it = value;
                for (int i = 0; i < range; i++) {
                    if (it[i] != b[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Compares this chain and specified {@code byte[]}
     *
     * <pre>{@code
     *   char[] c = new char[]{'k'};
     *   new Value("k").same(c); // true
     *   new Value("kat").same(c); // false
     *
     *   char[] d = new char[]{'k', 'a', 't'};
     *   new Value("k").same(d); // false
     *   new Value("kat").same(d); // true
     * }</pre>
     *
     * @param c the {@code byte[]} to compare this {@link Chain} against
     * @throws NullPointerException If the specified {@code bytes} is null
     * @since 0.0.4
     */
    public boolean same(
        @Nullable char[] c
    ) {
        if (c != null) {
            int range = c.length;
            if (count == range) {
                byte[] it = value;
                for (int i = 0; i < range; i++) {
                    if (c[i] != (char) (it[i] & 0xFF)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Compares this chain and specified {@link CharSequence}
     *
     * <pre>{@code
     *   new Value("k").same("k"); // true
     *   new Value("kat").same("k"); // false
     *
     *   new Value("k").same("kat"); // false
     *   new Value("kat").same("kat"); // true
     * }</pre>
     *
     * @param c the {@link CharSequence} to compare this {@link Chain} against
     * @throws NullPointerException If the specified {@code chars} is null
     * @since 0.0.4
     */
    public boolean same(
        @Nullable CharSequence c
    ) {
        if (c != null) {
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
     * Returns the byte value at the specified index
     *
     * <pre>{@code
     *   Chain c = new Value("kat");
     *   byte b0 = c.at(0); // 'k'
     *   byte b1 = c.at(1); // 'a'
     *   byte b2 = c.at(2); // 't'
     *   byte b3 = c.at(3); // may be '\0' or NPE
     * }</pre>
     *
     * @param i the index of the byte value
     * @throws ArrayIndexOutOfBoundsException if the {@code index} argument is negative or not less than the length of this
     */
    public byte at(int i) {
        return value[i];
    }

    /**
     * Returns the byte value at the specified index
     *
     * <pre>{@code
     *   Chain c = new Value("kat");
     *   byte b0 = c.get(0); // 'k'
     *   byte b1 = c.get(1); // 'a'
     *   byte b2 = c.get(2); // 't'
     *   byte b3 = c.get(3); // -1
     * }</pre>
     *
     * @param i the index of the byte value
     * @throws ArrayIndexOutOfBoundsException if the {@code index} argument is negative
     */
    public byte get(int i) {
        byte[] it = value;
        return i < it.length ? it[i] : -1;
    }

    /**
     * Returns the byte value at the specified index
     *
     * <pre>{@code
     *   Chain c = new Value("kat");
     *   byte def = 'K'
     *   byte b0 = c.get(0, def); // 'k'
     *   byte b1 = c.get(1, def); // 'a'
     *   byte b2 = c.get(2, def); // 't'
     *   byte b3 = c.get(3, def); // 'K'
     * }</pre>
     *
     * @param i   index
     * @param def default
     * @throws ArrayIndexOutOfBoundsException if the {@code index} argument is negative
     */
    public byte get(int i, byte def) {
        byte[] it = value;
        return i < it.length ? it[i] : def;
    }

    /**
     * Returns the byte value at the specified index
     *
     * @param i the index of the byte value
     * @throws ArrayIndexOutOfBoundsException if the {@code index} argument is negative or out of range {@code count}
     */
    public byte byteAt(int i) {
        if (i < count) {
            return value[i];
        }

        throw new ArrayIndexOutOfBoundsException(
            "Index " + i + " out of bounds for length " + count
        );
    }

    /**
     * Returns the char value at the specified index
     *
     * @param i the index of the char value
     * @throws ArrayIndexOutOfBoundsException if the {@code index} argument is negative or out of range {@code count}
     */
    @Override
    public char charAt(int i) {
        if (i < count) {
            return (char) (value[i] & 0xFF);
        }

        throw new ArrayIndexOutOfBoundsException(
            "Index " + i + " out of bounds for length " + count
        );
    }

    /**
     * Returns the length of this {@link Chain}
     *
     * @return the length of the sequence of characters represented by this object
     */
    @Override
    public int length() {
        return count;
    }

    /**
     * Returns the length of internal byte array
     */
    public int capacity() {
        return value.length;
    }

    /**
     * Returns true if, and only if,
     * the length of chain is {@code 0}
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Returns false if and only if this chain
     * has an element {@code byte > 32 or byte< 0}
     */
    public boolean isBlank() {
        int i = 0, l = count;
        byte[] it = value;
        while (i < l) {
            byte b = it[i++];
            // Ascii code > 32, other code < 0
            if (b > 32 || b < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if, and only if,
     * the length of chain is not {@code 0}
     */
    public boolean isNotEmpty() {
        return count != 0;
    }

    /**
     * Returns true if and only if this chain
     * has an element {@code byte > 32 or byte< 0}
     */
    public boolean isNotBlank() {
        int i = 0, l = count;
        byte[] it = value;
        while (i < l) {
            byte b = it[i++];
            // Ascii code > 32, other code < 0
            if (b > 32 || b < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if, and only if,
     * the length of chain is {@code 1}
     */
    public boolean isSole() {
        return count == 1;
    }

    /**
     * Returns true if, and only if, this chain can be shared
     *
     * @see Chain#getSource()
     * @since 0.0.2
     */
    public boolean isShared() {
        return false;
    }

    /**
     * Returns the internal value of this {@link Chain}
     * and only if, {@link #isShared()} is true, otherwise throw collapse
     *
     * @throws Collapse If the internal value cannot be shared
     * @see Chain#isShared()
     * @since 0.0.4
     */
    @NotNull
    public byte[] getSource() {
        if (isShared()) {
            return value;
        }

        throw new Collapse(
            "Unexpectedly, the internal value cannot be shared"
        );
    }

    /**
     * Tests if this {@link Chain} starts with the
     * specified prefix, only supports ASCII code comparison.
     *
     * <pre>{@code
     *   Chain c = new Value("kat");
     *   boolean b = c.startWith("ka"); // true
     *   boolean b = c.startWith("kat.plus"); // false
     * }</pre>
     *
     * @param c the prefix
     * @throws NullPointerException If the specified {@code prefix} is null
     * @see String#startsWith(String)
     */
    public boolean startWith(
        @NotNull CharSequence c
    ) {
        int l = c.length();
        if (count < l) {
            return false;
        }

        char ch;
        byte[] it = value;

        for (int i = 0; i < l; i++) {
            ch = (char) (
                it[i] & 0xFF
            );
            if (ch != c.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Tests if this {@link Chain} ends with the
     * specified suffix, only supports ASCII code comparison
     *
     * <pre>{@code
     *   Chain c = new Value("kat");
     *   boolean b = c.endsWith("at"); // true
     *   boolean b = c.endsWith("plus.kat"); // false
     * }</pre>
     *
     * @param c the suffix
     * @throws NullPointerException If the specified {@code suffix} is null
     * @see String#endsWith(String)
     */
    public boolean endsWith(
        @NotNull CharSequence c
    ) {
        int l = c.length();
        int k = count - l;
        if (k < 0) {
            return false;
        }

        char ch;
        byte[] it = value;

        for (int i = 0; i < l; i++, k++) {
            ch = (char) (
                it[k] & 0xFF
            );
            if (ch != c.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the first element only if the length of
     * chain is not {@code 0}, otherwise returns {@code -1}
     */
    public byte head() {
        if (count == 0) {
            return -1;
        }
        return value[0];
    }

    /**
     * Check the specified value comparison with the first element
     *
     * @param b data
     */
    public boolean head(
        byte b
    ) {
        if (count == 0) {
            return false;
        }
        return value[0] == b;
    }

    /**
     * Returns the last element only if the length of
     * chain is not {@code 0}, otherwise returns {@code -1}
     */
    public byte tail() {
        int i = count - 1;
        if (i < 0) {
            return -1;
        }
        return value[i];
    }

    /**
     * Check the specified value comparison with the last element
     *
     * @param b data
     */
    public boolean tail(
        byte b
    ) {
        int i = count - 1;
        if (i < 0) {
            return false;
        }
        return value[i] == b;
    }

    /**
     * Returns the last element only if the length of
     * chain is {@code 1}, otherwise returns {@code -1}
     */
    public byte sole() {
        if (count != 1) {
            return -1;
        }
        return value[0];
    }

    /**
     * Returns the index within this chain of
     * the first occurrence of the specified byte value
     *
     * @param b the byte value to search for
     * @see String#indexOf(int)
     */
    public int indexOf(byte b) {
        int max = count;
        byte[] it = value;
        for (int o = 0; o < max; o++) {
            if (it[o] == b) {
                return o;
            }
        }
        return -1;
    }

    /**
     * Returns the index within this chain of
     * the first occurrence of the specified character
     *
     * @param b the character to search for
     * @see Chain#indexOf(byte)
     */
    public int indexOf(int b) {
        return indexOf((byte) b);
    }

    /**
     * Returns the index within this chain of the first occurrence of
     * the specified byte value, starting the search at the specified index
     *
     * @param b the character to search for
     * @param o the index to start the search from
     * @see String#indexOf(int, int)
     */
    public int indexOf(
        byte b, int o
    ) {
        int max = count;
        if (o < 0) {
            o = 0;
        } else if (o >= max) {
            return -1;
        }

        byte[] it = value;
        for (; o < max; o++) {
            if (it[o] == b) {
                return o;
            }
        }
        return -1;
    }

    /**
     * Returns the index within this chain of the first occurrence of
     * the specified character, starting the search at the specified index
     *
     * @param b the character to search for
     * @param o the index to start the search from
     * @see Chain#indexOf(byte, int)
     */
    public int indexOf(
        int b, int o
    ) {
        return indexOf((byte) b, o);
    }

    /**
     * Returns the index within this chain of the first
     * occurrence of the specified chars, only supports ASCII code comparison
     *
     * @param c the specified chars to search for
     * @throws NullPointerException If the specified {@code chars} is null
     * @see String#indexOf(String)
     */
    public int indexOf(
        @NotNull CharSequence c
    ) {
        return indexOf(c, 0);
    }

    /**
     * Returns the index within this chain of the first occurrence of
     * the specified chars, starting at the specified index, only supports ASCII code comparison
     *
     * @param c the specified chars to search for
     * @param o the index from which to start the search
     * @throws NullPointerException           If the specified {@code chars} is null
     * @throws ArrayIndexOutOfBoundsException if the offset argument is negative
     * @see String#indexOf(String, int)
     */
    public int indexOf(
        @NotNull CharSequence c, int o
    ) {
        int len = c.length();
        if (len == 0) {
            return 0;
        }
        if (count == 0 ||
            o >= count) {
            return -1;
        }

        int lim = count - len;
        if (lim < 0) {
            return -1;
        }

        char ch = c.charAt(0);
        if (ch > 0xFF) {
            return -1;
        }

        byte fir = (byte) ch;
        byte[] it = value;

        for (; o <= lim; o++) {
            if (it[o] != fir) {
                continue;
            }

            char ot;
            int o1 = o, o2 = 0;
            while (++o2 < len) {
                ot = (char) (it[++o1] & 0xFF);
                if (ot != c.charAt(o2)) break;
            }
            if (o2 == len) {
                return o;
            }
        }

        return -1;
    }

    /**
     * Returns the index within this chain of
     * the last occurrence of the specified byte value
     *
     * @param b the byte value to search for
     * @see String#lastIndexOf(int)
     */
    public int lastIndexOf(byte b) {
        int o = count - 1;
        byte[] it = value;
        for (; o >= 0; o--) {
            if (it[o] == b) {
                return o;
            }
        }
        return -1;
    }

    /**
     * Returns the index within this chain of
     * the last occurrence of the specified character
     *
     * @param b the character to search for
     * @see Chain#lastIndexOf(byte)
     */
    public int lastIndexOf(int b) {
        return lastIndexOf((byte) b);
    }

    /**
     * Returns the index within this chain of the last occurrence of
     * the specified byte value, searching backward starting at the specified index
     *
     * @param b the byte value to search for
     * @param o the index from which to start the search
     * @see String#lastIndexOf(int, int)
     */
    public int lastIndexOf(
        byte b, int o
    ) {
        if (o >= count) {
            o = count - 1;
        }
        byte[] it = value;
        for (; o >= 0; o--) {
            if (it[o] == b) {
                return o;
            }
        }
        return -1;
    }

    /**
     * Returns the index within this chain of the last occurrence of
     * the specified character, searching backward starting at the specified index
     *
     * @param b the character to search for
     * @param o the index from which to start the search
     * @see Chain#lastIndexOf(byte, int)
     */
    public int lastIndexOf(
        int b, int o
    ) {
        return lastIndexOf((byte) b, o);
    }

    /**
     * Returns the index within this chain of the last
     * occurrence of the specified chars, only supports ASCII code comparison
     *
     * @param c the specified chars to search for
     * @throws NullPointerException If the specified {@code chars} is null
     * @see String#lastIndexOf(String)
     */
    public int lastIndexOf(
        @NotNull CharSequence c
    ) {
        return lastIndexOf(c, count);
    }

    /**
     * Returns the index within this chain of the last occurrence of the specified chars,
     * searching backward starting at the specified index, only supports ASCII code comparison
     *
     * @param c the specified chars to search for
     * @param f the index from which to start the search
     * @throws NullPointerException If the specified {@code chars} is null
     * @see String#lastIndexOf(String, int)
     */
    public int lastIndexOf(
        @NotNull CharSequence c, int f
    ) {
        int len = c.length(),
            r = count - len;
        if (f > r) {
            f = r;
        }

        if (f < 0) {
            return -1;
        }
        if (len == 0) {
            return f;
        }

        char ch = c.charAt(0);
        if (ch > 0xFF) {
            return -1;
        }

        byte fir = (byte) ch;
        byte[] it = value;

        char ot;
        for (; f >= 0; --f) {
            if (it[f] != fir) {
                continue;
            }

            int o1 = f, o2 = 0;
            while (++o2 < len) {
                ot = (char) (
                    it[++o1] & 0xFF
                );
                if (ot != c.charAt(o2)) break;
            }
            if (o2 == len) {
                return f;
            }
        }

        return -1;
    }

    /**
     * Returns true if and only if this
     * chain contains the specified byte value
     *
     * @param b the byte value to search for
     * @see Chain#indexOf(byte)
     */
    public boolean contains(byte b) {
        return indexOf(b) != -1;
    }

    /**
     * Returns true if and only if this
     * chain contains the specified character
     *
     * @param b the int value to search for
     * @see Chain#indexOf(byte)
     */
    public boolean contains(int b) {
        return indexOf((byte) b) != -1;
    }

    /**
     * Returns true if and only if this chain contains
     * the specified chars. only supports ASCII code comparison
     *
     * @param c the {@link CharSequence} to search for
     * @throws NullPointerException If the specified {@code chars} is null
     * @see Chain#indexOf(CharSequence)
     * @see String#contains(CharSequence)
     */
    public boolean contains(
        @NotNull CharSequence c
    ) {
        return indexOf(c, 0) != -1;
    }

    /**
     * Copy bytes from this {@link Chain} into the destination byte array
     *
     * @param index the start index
     * @param dst   the specified {@code dst}
     * @throws NullPointerException If the specified {@code dst} is null
     * @since 0.0.3
     */
    public int getBytes(
        int index, byte[] dst
    ) {
        int length = count - index;
        if (length <= 0) {
            return -1;
        }

        if (length > dst.length) {
            length = dst.length;
        }

        System.arraycopy(
            value, index, dst, 0, length
        );
        return length;
    }

    /**
     * Copy bytes from this {@link Chain} into the destination byte array
     *
     * @param index the start index
     * @param dst   the specified {@code dst}
     * @throws NullPointerException If the specified {@code dst} is null
     * @since 0.0.3
     */
    public int getBytes(
        int index, byte[] dst, int dstIndex, int length
    ) {
        int len = count - index;
        if (len <= 0) {
            return -1;
        }

        int cap = dst.length - dstIndex;
        if (cap <= 0) {
            return 0;
        }

        if (cap < length) {
            length = cap;
        }

        if (len < length) {
            length = len;
        }

        System.arraycopy(
            value, index, dst, dstIndex, length
        );
        return length;
    }

    /**
     * Copy this chain into a new byte array
     *
     * @since 0.0.4
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
     * Copy this chain into a new byte array
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     * @throws IndexOutOfBoundsException If the start is negative or the end out of range
     * @since 0.0.4
     */
    @NotNull
    public byte[] toBytes(
        int start, int end
    ) {
        int length = end - start;
        if (0 <= start && 0 <= length && end <= count) {
            if (length != 0) {
                byte[] copy = new byte[length];
                System.arraycopy(
                    value, start, copy, 0, length
                );
                return copy;
            }
            return EMPTY_BYTES;
        } else {
            throw new IndexOutOfBoundsException(
                "Unexpectedly, start: " + start
                    + " end: " + end + " size: " + count
            );
        }
    }

    /**
     * Copy this UTF8 chain into a new char array
     *
     * @since 0.0.4
     */
    @NotNull
    public char[] toChars() {
        int size = count;
        if (size == 0) {
            return EMPTY_CHARS;
        }
        return Convert.toChars(
            value, 0, size
        );
    }

    /**
     * Copy this UTF8 chain into a new char array
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     * @throws IndexOutOfBoundsException If the start is negative or the end out of range
     * @since 0.0.4
     */
    @NotNull
    public char[] toChars(
        int start, int end
    ) {
        int length = end - start;
        if (0 <= start && 0 <= length && end <= count) {
            if (length == 0) {
                return EMPTY_CHARS;
            }
            return Convert.toChars(
                value, start, end
            );
        } else {
            throw new IndexOutOfBoundsException(
                "Unexpectedly, start: " + start
                    + " end: " + end + " size: " + count
            );
        }
    }

    /**
     * Writes to the {@link OutputStream} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code stream} is null
     * @see OutputStream#write(byte[], int, int)
     * @since 0.0.2
     */
    public void update(
        @NotNull OutputStream s
    ) throws IOException {
        s.write(
            value, 0, count
        );
    }

    /**
     * Writes to the {@link OutputStream} using the internal value of this {@link Chain}
     *
     * @param o the specified offset
     * @param l the specified length
     * @throws NullPointerException      If the specified {@code stream} is null
     * @throws IndexOutOfBoundsException If the offset is negative or the length out of range
     * @see OutputStream#write(byte[], int, int)
     * @since 0.0.2
     */
    public void update(
        @NotNull OutputStream s, int o, int l
    ) throws IOException {
        s.write(
            value, o, l
        );
    }

    /**
     * Updates the {@link Mac} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code mac} is null
     * @see Mac#update(byte[], int, int)
     */
    public void update(
        @NotNull Mac m
    ) {
        m.update(
            value, 0, count
        );
    }

    /**
     * Updates the {@link Mac} using the internal value of this {@link Chain}
     *
     * @param o the specified offset
     * @param l the specified length
     * @throws NullPointerException     If the specified {@code mac} is null
     * @throws IllegalArgumentException If the offset is negative or the length out of range
     * @see Mac#update(byte[], int, int)
     */
    public void update(
        @NotNull Mac m, int o, int l
    ) {
        m.update(
            value, o, l
        );
    }

    /**
     * Updates the {@link Signature} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code signature} is null
     * @see Signature#update(byte[], int, int)
     */
    public void update(
        @NotNull Signature s
    ) throws SignatureException {
        s.update(
            value, 0, count
        );
    }

    /**
     * Updates the {@link Signature} using the internal value of this {@link Chain}
     *
     * @param o the specified offset
     * @param l the specified length
     * @throws NullPointerException     If the specified {@code signature} is null
     * @throws IllegalArgumentException If the offset is negative or the length out of range
     * @see Signature#update(byte[], int, int)
     */
    public void update(
        @NotNull Signature s, int o, int l
    ) throws SignatureException {
        s.update(
            value, o, l
        );
    }

    /**
     * Updates the {@link MessageDigest} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code digest} is null
     * @see MessageDigest#update(byte[], int, int)
     */
    public void update(
        @NotNull MessageDigest m
    ) {
        m.update(
            value, 0, count
        );
    }

    /**
     * Updates the {@link MessageDigest} using the internal value of this {@link Chain}
     *
     * @param o the specified offset
     * @param l the specified length
     * @throws NullPointerException     If the specified {@code digest} is null
     * @throws IllegalArgumentException If the offset is negative or the length out of range
     * @see MessageDigest#update(byte[], int, int)
     */
    public void update(
        @NotNull MessageDigest m, int o, int l
    ) {
        m.update(
            value, o, l
        );
    }

    /**
     * Updates the {@link Cipher} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code cipher} is null
     * @see Cipher#update(byte[], int, int)
     */
    @Nullable
    public byte[] update(
        @NotNull Cipher c
    ) {
        return c.update(
            value, 0, count
        );
    }

    /**
     * Updates the {@link Cipher} using the internal value of this {@link Chain}
     *
     * @param o the specified offset
     * @param l the specified length
     * @throws NullPointerException     If the specified {@code cipher} is null
     * @throws IllegalArgumentException If the offset is negative or the length out of range
     * @see Cipher#update(byte[], int, int)
     */
    @Nullable
    public byte[] update(
        @NotNull Cipher c, int o, int l
    ) {
        return c.update(
            value, o, l
        );
    }

    /**
     * Completes the {@link Cipher} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code cipher} is null
     * @see Cipher#doFinal(byte[], int, int)
     */
    @Nullable
    public byte[] doFinal(
        @NotNull Cipher c
    ) throws IllegalBlockSizeException, BadPaddingException {
        return c.doFinal(
            value, 0, count
        );
    }

    /**
     * Completes the {@link Cipher} using the internal value of this {@link Chain}
     *
     * @param o the specified offset
     * @param l the specified length
     * @throws NullPointerException     If the specified {@code cipher} is null
     * @throws IllegalArgumentException If the offset is negative or the length out of range
     * @see Cipher#doFinal(byte[], int, int)
     */
    @Nullable
    public byte[] doFinal(
        @NotNull Cipher c, int o, int l
    ) throws IllegalBlockSizeException, BadPaddingException {
        return c.doFinal(
            value, o, l
        );
    }

    /**
     * Returns a lowercase {@code MD5} of this {@link Chain}
     *
     * @throws UnsupportedCrash If not supports the MD5
     */
    @NotNull
    public String digest() {
        return digest(
            "MD5", 0, count
        );
    }

    /**
     * Returns a lowercase message digest of this {@link Chain}
     *
     * @param algo the name of the algorithm requested
     * @throws UnsupportedCrash If not supports the algo
     * @see MessageDigest
     * @see Binary#toLower(byte[])
     * @see Chain#digest(String, int, int)
     */
    @NotNull
    public String digest(
        @NotNull String algo
    ) {
        return digest(
            algo, 0, count
        );
    }

    /**
     * Returns a lowercase message digest of this {@link Chain}
     *
     * @param algo the name of the algorithm requested
     * @param o    the specified offset
     * @param l    the specified length
     * @throws UnsupportedCrash         If not supports the algo
     * @throws IllegalArgumentException If the length out of range
     * @see MessageDigest
     * @see Binary#toLower(byte[])
     */
    @NotNull
    public String digest(
        @NotNull String algo, int o, int l
    ) {
        try {
            MessageDigest md = MessageDigest
                .getInstance(algo);

            md.update(
                value, o, l
            );

            return toLower(
                md.digest()
            );
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedCrash(
                "Unexpectedly, " + algo + " unsupported", e
            );
        }
    }

    /**
     * Returns a {@code REC4648|Basic} encoded byte array of {@link Chain}
     */
    @NotNull
    public byte[] toBase() {
        return Base64.REC4648.INS.encode(
            value, 0, count
        );
    }

    /**
     * Returns a {@code REC4648|Basic} encoded String of {@link Chain}
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public String toBase64() {
        byte[] d = toBase();
        return new String(
            d, 0, 0, d.length
        );
    }

    /**
     * Returns a {@code REC4648|Basic} decoded byte array of {@link Chain}
     */
    @NotNull
    public byte[] fromBase() {
        return Base64.REC4648.INS.decode(
            value, 0, count
        );
    }

    /**
     * Returns a {@code RFC4648_SAFE|URL/Filename Safe} encoded byte array of {@link Chain}
     */
    @NotNull
    public byte[] toSafe() {
        return Base64.RFC4648_SAFE.INS.encode(
            value, 0, count
        );
    }

    /**
     * Returns a {@code RFC4648_SAFE|URL/Filename Safe} encoded String of {@link Chain}
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public String toSafe64() {
        byte[] d = toSafe();
        return new String(
            d, 0, 0, d.length
        );
    }

    /**
     * Returns a {@code RFC4648_SAFE|URL/Filename Safe} decoded byte array of {@link Chain}
     */
    @NotNull
    public byte[] fromSafe() {
        return Base64.RFC4648_SAFE.INS.decode(
            value, 0, count
        );
    }

    /**
     * Returns a {@code RFC2045|Mime} encoded byte array of {@link Chain}
     */
    @NotNull
    public byte[] toMime() {
        return Base64.RFC2045.INS.encode(
            value, 0, count
        );
    }

    /**
     * Returns a {@code RFC2045|Mime} encoded String of {@link Chain}
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public String toMime64() {
        byte[] d = toMime();
        return new String(
            d, 0, 0, d.length
        );
    }

    /**
     * Returns a {@code RFC2045|Mime} decoded byte array of {@link Chain}
     */
    @NotNull
    public byte[] fromMime() {
        return Base64.RFC2045.INS.decode(
            value, 0, count
        );
    }

    /**
     * Returns a {@link Reader} of this {@link Chain}
     *
     * @see Reader
     */
    @NotNull
    public Reader reader() {
        return new Reader(
            this, 0, count
        );
    }

    /**
     * Returns a {@link Reader} of this {@link Chain}
     *
     * @throws IllegalStateException if the {@code index} argument is negative or the length out of range
     * @see Reader
     */
    @NotNull
    public Reader reader(
        int index, int length
    ) {
        if (index < 0) {
            throw new IllegalStateException(
                "The 'index' argument is negative"
            );
        }

        int offset = index + length;
        if (offset > count) {
            throw new IllegalStateException(
                "The 'length' argument is ouf of range"
            );
        }

        return new Reader(
            this, index, offset
        );
    }

    /**
     * Returns an ASCII {@link String} of this {@link Chain}
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public String string() {
        if (count == 0) {
            return "";
        }
        return new String(
            value, 0, 0, count
        );
    }

    /**
     * Returns an ASCII {@link String} of this {@link Chain}
     *
     * @param b the beginning index, inclusive
     * @throws IndexOutOfBoundsException if the beginIndex is negative
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public String string(int b) {
        int l = count - b;
        if (l <= 0) {
            return "";
        }
        return new String(
            value, 0, b, l
        );
    }

    /**
     * Returns an ASCII {@link String} of this {@link Chain}
     *
     * @param b the beginning index, inclusive
     * @param e the ending index, exclusive
     * @throws IndexOutOfBoundsException if the beginIndex is negative
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public String string(int b, int e) {
        int l = e - b;
        if (l <= 0 || e > count) {
            return "";
        }
        return new String(
            value, 0, b, l
        );
    }

    /**
     * Returns the value of this {@link Chain} as a {@link String}
     */
    @NotNull
    @Override
    public String toString() {
        if (count == 0) {
            return "";
        }

        return new String(
            value, 0, count, UTF_8
        );
    }

    /**
     * Returns the value of this {@link Chain} as a {@link String}
     *
     * @param b the beginning index, inclusive
     * @param e the ending index, exclusive
     * @throws IndexOutOfBoundsException if the beginIndex is negative
     */
    @NotNull
    public String toString(
        int b, int e
    ) {
        int l = e - b;
        if (l <= 0 || e > count) {
            return "";
        }

        return new String(
            value, b, l, UTF_8
        );
    }

    /**
     * Returns the value of this {@link Chain} as a {@link String}
     *
     * @param c charset
     */
    @NotNull
    public String toString(
        @NotNull Charset c
    ) {
        if (count == 0) {
            return "";
        }

        return new String(
            value, 0, count, c
        );
    }

    /**
     * Returns the value of this {@link Chain} as a {@link String}
     *
     * @param c charset
     * @param b the beginning index, inclusive
     * @param e the ending index, exclusive
     * @throws IndexOutOfBoundsException if the beginIndex is negative
     */
    @NotNull
    public String toString(
        @NotNull Charset c, int b, int e
    ) {
        int l = e - b;
        if (l <= 0 || e > count) {
            return "";
        }

        return new String(
            value, b, l, c
        );
    }

    /**
     * Parses this {@code UTF8} {@link Chain} as a {@code char}
     *
     * @return the specified {@code char}, {@code '\0'} on error
     * @see Convert#toChar(byte[], int, char)
     * @since 0.0.3
     */
    public char toChar() {
        return Convert.toChar(
            value, count, '\0'
        );
    }

    /**
     * Parses this {@code UTF8} {@link Chain} as a {@code char}
     *
     * @return the specified {@code char}, {@code def} value on error
     * @see Convert#toChar(byte[], int, char)
     * @since 0.0.3
     */
    public char toChar(
        char def
    ) {
        return Convert.toChar(
            value, count, def
        );
    }

    /**
     * Parses this {@link Chain} as a signed decimal {@code int}
     *
     * @return the specified {@code int}, {@code '0'} on error
     * @see Convert#toInt(byte[], int, int, int)
     * @since 0.0.3
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
     * @since 0.0.3
     */
    public int toInt(
        int def
    ) {
        return Convert.toInt(
            value, count, 10, def
        );
    }

    /**
     * Parses this {@link Chain} as a signed decimal {@code int}
     *
     * @param radix the radix to be used while parsing {@link Chain}
     * @return the specified {@code int}, {@code def} value on error
     * @see Convert#toInt(byte[], int, int, int)
     * @since 0.0.3
     */
    public int toInt(
        int def, int radix
    ) {
        if (radix < 2 || radix > 36) {
            return def;
        }
        return Convert.toInt(
            value, count, radix, def
        );
    }

    /**
     * Parses this {@link Chain} as a signed decimal {@code long}
     *
     * @return the specified {@code long}, {@code '0L'} on error
     * @see Convert#toLong(byte[], int, long, long)
     * @since 0.0.3
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
     * @since 0.0.3
     */
    public long toLong(
        long def
    ) {
        return Convert.toLong(
            value, count, 10L, def
        );
    }

    /**
     * Parses this {@link Chain} as a signed decimal {@code long}
     *
     * @param radix the radix to be used while parsing {@link Chain}
     * @return the specified {@code long}, {@code def} value on error
     * @see Convert#toLong(byte[], int, long, long)
     * @since 0.0.3
     */
    public long toLong(
        long def, long radix
    ) {
        if (radix < 2L || radix > 36L) {
            return def;
        }
        return Convert.toLong(
            value, count, radix, def
        );
    }

    /**
     * Parses this {@link Chain} as a {@code float}
     *
     * @return the specified {@code float}, {@code '0F'} on error
     * @see Convert#toFloat(byte[], int, float)
     * @since 0.0.3
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
     * @since 0.0.3
     */
    public float toFloat(
        float def
    ) {
        return Convert.toFloat(
            value, count, def
        );
    }

    /**
     * Parses this {@link Chain} as a {@code double}
     *
     * @return the specified {@code double}, {@code '0D'} on error
     * @see Convert#toDouble(byte[], int, double)
     * @since 0.0.3
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
     * @since 0.0.3
     */
    public double toDouble(
        double def
    ) {
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
     * @since 0.0.3
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
     * @since 0.0.3
     */
    @Nullable
    public Number toNumber(
        @Nullable Number def
    ) {
        return Convert.toNumber(
            value, count, def
        );
    }

    /**
     * Parses this {@link Chain} as a {@code boolean}
     *
     * @return the specified {@code boolean}, {@code 'false'} on error
     * @see Convert#toBoolean(byte[], int, boolean)
     * @since 0.0.3
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
     * @since 0.0.3
     */
    public boolean toBoolean(
        boolean def
    ) {
        return Convert.toBoolean(
            value, count, def
        );
    }

    /**
     * @param c the specified {@link Chain}
     */
    protected void chain(
        @NotNull Chain c
    ) {
        int d = count;
        int l = c.count;
        if (l == 1) {
            grow(d + 1);
            hash = 0;
            value[count++] = c.value[0];
        } else if (l != 0) {
            grow(d + l);
            hash = 0;
            count += l;
            System.arraycopy(
                c.value, 0, value, d, l
            );
        }
    }

    /**
     * @param c the specified chain
     * @param i the specified index
     * @param l the specified length
     * @since 0.0.4
     */
    protected void chain(
        @NotNull Chain c, int i, int l
    ) {
        if (l != 0) {
            int d = count;
            grow(d + l);
            hash = 0;
            count += l;
            System.arraycopy(
                c.value, i, value, d, l
            );
        }
    }

    /**
     * @param in the specified {@link InputStream}
     * @since 0.0.3
     */
    protected void chain(
        @NotNull InputStream in
    ) {
        try {
            chain(in, 128);
        } catch (Exception e) {
            // Nothing
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                // Nothing
            }
        }
    }

    /**
     * @param in the specified {@link InputStream}
     * @throws IOException If an I/O error occurs
     * @since 0.0.3
     */
    protected void chain(
        @NotNull InputStream in, int range
    ) throws IOException {
        int cap, length;
        byte[] it = value;

        while (true) {
            cap = it.length - count;
            if (cap < range) {
                grow(count + range);
                it = value;
                cap = it.length - count;
            }

            length = in.read(
                it, count, cap
            );

            if (length == -1) {
                break;
            }
            count += length;
        }
    }

    /**
     * @param b the specified byte value
     */
    protected void chain(
        byte b
    ) {
        byte[] it = value;
        if (count != it.length) {
            hash = 0;
            it[count++] = b;
        } else {
            grow(count + 1);
            hash = 0;
            value[count++] = b;
        }
    }

    /**
     * @param num the specified int value
     * @since 0.0.4
     */
    protected void chain(
        int num
    ) {
        if (num < 0) {
            grow(count + 1);
            value[count++] = '-';
        } else {
            num = -num;
        }

        if (num > -10) {
            grow(count + 1);
            hash = 0;
            value[count++] = lower(-num);
        } else {
            int mark = count;
            do {
                grow(count + 1);
                value[count++] = lower(-(num % 10));
                num /= 10;
            } while (num < 0);
            swop(mark, count - 1);
        }
    }

    /**
     * @param num the specified long value
     * @since 0.0.4
     */
    protected void chain(
        long num
    ) {
        if (num < 0) {
            grow(count + 1);
            value[count++] = '-';
        } else {
            num = -num;
        }

        if (num > -10L) {
            grow(count + 1);
            hash = 0;
            value[count++] = lower((int) -num);
        } else {
            int mark = count;
            do {
                grow(count + 1);
                value[count++] = lower((int) -(num % 10L));
                num /= 10L;
            } while (num < 0L);
            swop(mark, count - 1);
        }
    }

    /**
     * @param b the specified byte array
     * @param i the specified index
     * @param l the specified length
     */
    protected void chain(
        @NotNull byte[] b, int i, int l
    ) {
        if (l != 0) {
            int d = count;
            grow(d + l);
            hash = 0;
            count += l;
            System.arraycopy(
                b, i, value, d, l
            );
        }
    }

    /**
     * @param c the specified char value
     */
    protected void chain(
        char c
    ) {
        // U+0000 ~ U+007F
        if (c < 0x80) {
            grow(count + 1);
            hash = 0;
            value[count++] = (byte) c;
        }

        // U+0080 ~ U+07FF
        else if (c < 0x800) {
            grow(count + 2);
            hash = 0;
            value[count++] = (byte) ((c >> 6) | 0xC0);
            value[count++] = (byte) ((c & 0x3F) | 0x80);
        }

        // U+10000 ~ U+10FFFF
        // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
        else if (c >= 0xD800 && c <= 0xDFFF) {
            // crippled surrogate pair
            grow(count + 1);
            hash = 0;
            value[count++] = '?';
        }

        // U+0800 ~ U+FFFF
        else {
            grow(count + 3);
            hash = 0;
            value[count++] = (byte) ((c >> 12) | 0xE0);
            value[count++] = (byte) (((c >> 6) & 0x3F) | 0x80);
            value[count++] = (byte) ((c & 0x3F) | 0x80);
        }
    }

    /**
     * @param c the specified char array
     * @param i the specified index
     * @param l the specified length
     */
    protected void chain(
        @NotNull char[] c, int i, int l
    ) {
        int k = i + l;
        grow(count + l);

        while (i < k) {
            // get char
            char d = c[i++];

            // U+0000 ~ U+007F
            if (d < 0x80) {
                grow(count + 1);
                hash = 0;
                value[count++] = (byte) d;
            }

            // U+0080 ~ U+07FF
            else if (d < 0x800) {
                grow(count + 2);
                hash = 0;
                value[count++] = (byte) ((d >> 6) | 0xC0);
                value[count++] = (byte) ((d & 0x3F) | 0x80);
            }

            // U+10000 ~ U+10FFFF
            // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
            else if (d >= 0xD800 && d <= 0xDFFF) {
                if (i >= k) {
                    grow(count + 1);
                    hash = 0;
                    value[count++] = '?';
                    break;
                }

                char f = c[i++];
                if (f < 0xDC00 || f > 0xDFFF) {
                    grow(count + 1);
                    hash = 0;
                    value[count++] = '?';
                    continue;
                }

                grow(count + 4);
                hash = 0;
                int u = (d << 10) + f - 0x35F_DC00;
                value[count++] = (byte) ((u >> 18) | 0xF0);
                value[count++] = (byte) (((u >> 12) & 0x3F) | 0x80);
                value[count++] = (byte) (((u >> 6) & 0x3F) | 0x80);
                value[count++] = (byte) ((u & 0x3F) | 0x80);
            }

            // U+0800 ~ U+FFFF
            else {
                grow(count + 3);
                hash = 0;
                value[count++] = (byte) ((d >> 12) | 0xE0);
                value[count++] = (byte) (((d >> 6) & 0x3F) | 0x80);
                value[count++] = (byte) ((d & 0x3F) | 0x80);
            }
        }
    }

    /**
     * @param c the specified char array
     * @param i the specified index
     * @param l the specified length
     */
    protected void chain(
        @NotNull CharSequence c, int i, int l
    ) {
        int k = i + l;
        grow(count + l);

        while (i < k) {
            // get char
            char d = c.charAt(i++);

            // U+0000 ~ U+007F
            if (d < 0x80) {
                grow(count + 1);
                hash = 0;
                value[count++] = (byte) d;
            }

            // U+0080 ~ U+07FF
            else if (d < 0x800) {
                grow(count + 2);
                hash = 0;
                value[count++] = (byte) ((d >> 6) | 0xC0);
                value[count++] = (byte) ((d & 0x3F) | 0x80);
            }

            // U+10000 ~ U+10FFFF
            // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
            else if (d >= 0xD800 && d <= 0xDFFF) {
                if (i >= k) {
                    grow(count + 1);
                    hash = 0;
                    value[count++] = '?';
                    break;
                }

                char f = c.charAt(i++);
                if (f < 0xDC00 || f > 0xDFFF) {
                    grow(count + 1);
                    hash = 0;
                    value[count++] = '?';
                    continue;
                }

                grow(count + 4);
                hash = 0;
                int u = (d << 10) + f - 0x35F_DC00;
                value[count++] = (byte) ((u >> 18) | 0xF0);
                value[count++] = (byte) (((u >> 12) & 0x3F) | 0x80);
                value[count++] = (byte) (((u >> 6) & 0x3F) | 0x80);
                value[count++] = (byte) ((u & 0x3F) | 0x80);
            }

            // U+0800 ~ U+FFFF
            else {
                grow(count + 3);
                hash = 0;
                value[count++] = (byte) ((d >> 12) | 0xE0);
                value[count++] = (byte) (((d >> 6) & 0x3F) | 0x80);
                value[count++] = (byte) ((d & 0x3F) | 0x80);
            }
        }
    }

    /**
     * Unsafe method
     *
     * @param b the beginning index
     * @param e the ending index
     */
    protected void swop(
        int b, int e
    ) {
        byte v;
        hash = 0;

        while (b < e) {
            v = value[e];
            value[e--] = value[b];
            value[b++] = v;
        }
    }

    /**
     * @param min the specified minimum size
     */
    protected void grow(
        int min
    ) {
        if (min > value.length) {
            if (bucket == null) {
                int cap = value.length +
                    (value.length >> 1);
                if (cap < min) cap = min;

                byte[] result = new byte[cap];
                System.arraycopy(
                    value, 0, result, 0, count
                );
                value = result;
            } else {
                value = bucket.alloc(
                    value, count, min
                );
            }
        }
    }

    /**
     * Clean this {@link Chain}
     */
    protected void clean() {
        hash = 0;
        type = null;
        count = 0;
    }

    /**
     * Clear this {@link Chain}
     */
    protected void clear() {
        this.clean();
        Bucket bt = bucket;
        if (bt == null) {
            value = EMPTY_BYTES;
        } else {
            byte[] it = bt.revert(value);
            value = it != null ? it : EMPTY_BYTES;
        }
    }

    /**
     * Close this {@link Chain}
     */
    protected void close() {
        this.clean();
        Bucket bt = bucket;
        if (bt != null) {
            bucket = null;
            byte[] it = value;
            if (it.length != 0) {
                bt.push(it);
            }
        }
        value = EMPTY_BYTES;
    }

    /**
     * @author Kraity
     * @since 0.0.1
     */
    public static class Reader implements plus.kat.stream.Reader {

        private int i, l;
        private byte[] b;

        /**
         * @param c the specified {@link Chain}
         * @param i the start index of the {@link Chain}
         */
        private Reader(
            @NotNull Chain c, int i, int l
        ) {
            this.i = i;
            this.l = l;
            this.b = c.value;
        }

        /**
         * Check {@link Reader} for readable bytes
         *
         * @throws NullPointerException If this has been closed
         */
        @Override
        public boolean also() {
            return i < l;
        }

        /**
         * Read a byte and cursor switch to next
         *
         * @throws NullPointerException If this has been closed
         */
        @Override
        public byte read() {
            return b[i++];
        }

        /**
         * Reads a byte if {@link Reader} has readable bytes, otherwise raise IOException
         *
         * @throws IOException If this has been closed
         */
        @Override
        public byte next() throws IOException {
            if (i < l) {
                return b[i++];
            }

            throw new UnexpectedCrash(
                "Unexpectedly, no readable byte"
            );
        }

        /**
         * @throws IllegalStateException if the {@code offset} argument is negative
         */
        public void slip(
            int index
        ) {
            if (index < 0) {
                throw new IllegalStateException(
                    "The 'offset' argument is negative"
                );
            }
            this.i = index;
        }

        /**
         * Close this {@link Reader}
         */
        @Override
        public void close() {
            l = 0;
            b = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Buffer implements Bucket {

        public static final int SIZE, SCALE;

        static {
            SIZE = Config.get(
                "kat.buffer.size", 4
            );
            SCALE = Config.get(
                "kat.buffer.scale", 1024 * 4
            );
        }

        public static final Buffer
            INS = new Buffer();

        private final byte[][]
            bucket = new byte[SIZE][];

        @NotNull
        public byte[] alloc() {
            Thread th = Thread.currentThread();
            int tr = th.hashCode() & 0xFFFFFF;

            byte[] it;
            int ix = tr % SIZE;

            synchronized (this) {
                it = bucket[ix];
                bucket[ix] = null;
            }

            if (it != null &&
                SCALE <= it.length) {
                return it;
            }

            return new byte[SCALE];
        }

        @Override
        public byte[] alloc(
            @NotNull byte[] it, int len, int min
        ) {
            Thread th = Thread.currentThread();
            int ix = (th.hashCode() & 0xFFFFFF) % SIZE;

            byte[] data;
            if (min <= SCALE) {
                synchronized (this) {
                    data = bucket[ix];
                    bucket[ix] = null;
                }
                if (data == null ||
                    SCALE > it.length) {
                    data = new byte[SCALE];
                }
                if (it.length != 0) {
                    System.arraycopy(
                        it, 0, data, 0, len
                    );
                }
            } else {
                int cap = it.length +
                    (it.length >> 1);
                if (cap < min) {
                    cap = min;
                }
                data = new byte[cap];
                if (it.length != 0) {
                    System.arraycopy(
                        it, 0, data, 0, len
                    );

                    if (SCALE == it.length) {
                        synchronized (this) {
                            bucket[ix] = it;
                        }
                    }
                }
            }

            return data;
        }

        @Override
        public void push(
            @Nullable byte[] it
        ) {
            if (it != null && SCALE == it.length) {
                Thread th = Thread.currentThread();
                int ix = (th.hashCode() & 0xFFFFFF) % SIZE;
                synchronized (this) {
                    bucket[ix] = it;
                }
            }
        }

        @Override
        public byte[] revert(
            @Nullable byte[] it
        ) {
            this.push(it);
            return EMPTY_BYTES;
        }
    }
}
