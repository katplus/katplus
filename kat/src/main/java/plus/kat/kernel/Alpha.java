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

import plus.kat.stream.*;
import plus.kat.utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author kraity
 * @since 0.0.5
 */
public class Alpha extends Chain {
    /**
     * Constructs an empty alpha
     */
    public Alpha() {
        super();
    }

    /**
     * Constructs an alpha with the specified size
     *
     * @param size the initial capacity
     */
    public Alpha(
        int size
    ) {
        super(size);
    }

    /**
     * Constructs an alpha with the specified data
     *
     * @param data the specified array to be used
     */
    public Alpha(
        @NotNull byte[] data
    ) {
        super(data);
        count = data.length;
    }

    /**
     * Constructs an alpha with the specified chain
     *
     * @param chain the specified chain to be used
     */
    public Alpha(
        @NotNull Chain chain
    ) {
        this(
            chain.toBytes()
        );
    }

    /**
     * Constructs an alpha with the specified bucket
     *
     * @param bucket the specified bucket to be used
     */
    public Alpha(
        @Nullable Bucket bucket
    ) {
        super();
        this.bucket = bucket;
    }

    /**
     * Constructs an alpha with the specified sequence
     *
     * @param chars the specified sequence to be used
     */
    public Alpha(
        @Nullable CharSequence chars
    ) {
        super();
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
     * Returns an {@link Alpha} that
     * is a subsequence of this {@link Alpha}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public Alpha subSequence(
        int start, int end
    ) {
        return new Alpha(
            toBytes(start, end)
        );
    }

    /**
     * Writes this {@link Alpha} to the specified {@link Alpha}
     *
     * @throws NullPointerException If the specified alpha is null
     */
    public void each(
        @NotNull Alpha alpha
    ) {
        alpha.join(
            value, 0, count
        );
    }

    /**
     * Writes this {@link Alpha} to the specified {@link OutputStream}
     *
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified steam is null
     */
    public void each(
        @NotNull OutputStream stream
    ) throws IOException {
        stream.write(
            value, 0, count
        );
    }

    /**
     * Concatenates the string representation
     * of the integer value to this {@link Alpha}
     *
     * @param num the specified number to be appended
     */
    public void emit(int num) {
        int arch = 0x30;
        byte[] it = value;

        asset = 0;
        if (num < 0) {
            int size = count + 2;
            if (size > it.length) {
                it = grow(size);
            }
            arch = 0x3A;
            it[count++] = '-';
        }

        int i = count;
        do {
            if (count == it.length) {
                it = grow(count + 1);
            }
            it[count++] = (byte) (arch + (num % 10));
        } while (
            (num /= 10) != 0
        );

        arch = count;
        for (byte j; i < --arch; it[i++] = j) {
            j = it[arch];
            it[arch] = it[i];
        }
    }

    /**
     * Concatenates the string representation
     * of the long value to this {@link Alpha}
     *
     * @param num the specified number to be appended
     */
    public void emit(long num) {
        long arch = 0x30L;
        byte[] it = value;

        asset = 0;
        if (num < 0) {
            int size = count + 2;
            if (size > it.length) {
                it = grow(size);
            }
            arch = 0x3AL;
            it[count++] = '-';
        }

        int i = count;
        do {
            if (count == it.length) {
                it = grow(count + 1);
            }
            it[count++] = (byte) (arch + (num % 10L));
        } while (
            (num /= 10L) != 0L
        );

        int apex = count;
        for (byte j; i < --apex; it[i++] = j) {
            j = it[apex];
            it[apex] = it[i];
        }
    }

    /**
     * Concatenates the stream to this
     * {@link Alpha}, copy it directly
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
     * {@link Alpha}, copy it directly
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
     * {@link Alpha}, copy it directly
     *
     * @param b the specified byte value to be joined
     */
    public void join(byte b) {
        byte[] it = value;
        if (count != it.length) {
            asset = 0;
            it[count++] = b;
        } else {
            asset = 0;
            grow(count + 1)[count++] = b;
        }
    }

    /**
     * Concatenates the array to this
     * {@link Alpha}, copy it directly
     *
     * @param b the specified source to be joined
     * @throws NullPointerException If the specified array is null
     */
    public void join(
        @NotNull byte[] b
    ) {
        byte[] it;
        int d = count;
        int l = b.length;
        if (l == 1) {
            it = grow(d + 1);
            asset = 0;
            it[count++] = b[0];
        } else if (l != 0) {
            it = grow(d + l);
            asset = 0;
            count += l;
            System.arraycopy(
                b, 0, it, d, l
            );
        }
    }

    /**
     * Concatenates the array to this
     * {@link Alpha}, copy it directly
     *
     * @param b the specified source to be joined
     * @param i the specified start index for array
     * @param l the specified length of bytes to join
     * @throws NullPointerException           If the specified array is null
     * @throws ArrayIndexOutOfBoundsException If the index or length out of range
     */
    public void join(
        @NotNull byte[] b, int i, int l
    ) {
        if (0 <= i && 0 <= l && i + l <= b.length) {
            if (l != 0) {
                System.arraycopy(
                    b, i, grow(count + l), count, l
                );
                asset = 0;
                count += l;
            }
        } else {
            throw new ArrayIndexOutOfBoundsException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + b.length
            );
        }
    }

    /**
     * Concatenates the chain to this
     * {@link Alpha}, copy it directly
     *
     * @param c the specified chain to be joined
     * @throws NullPointerException If the specified chain is null
     */
    public void join(
        @NotNull Chain c
    ) {
        byte[] it;
        int d = count;
        int l = c.count;
        if (l == 1) {
            it = grow(d + 1);
            asset = 0;
            it[count++] = c.value[0];
        } else if (l != 0) {
            it = grow(d + l);
            asset = 0;
            count += l;
            System.arraycopy(
                c.value, 0, it, d, l
            );
        }
    }

    /**
     * Concatenates the chain to this
     * {@link Alpha}, copy it directly
     *
     * @param c the specified chain to be joined
     * @param i the specified start index for chain
     * @param l the specified length of chain to join
     * @throws NullPointerException           If the specified chain is null
     * @throws ArrayIndexOutOfBoundsException If the index or length out of range
     */
    public void join(
        @Nullable Chain c, int i, int l
    ) {
        if (0 <= i && 0 <= l && i + l <= c.count) {
            if (l != 0) {
                System.arraycopy(
                    c.value, i, grow(count + l), count, l
                );
                asset = 0;
                count += l;
            }
        } else {
            throw new ArrayIndexOutOfBoundsException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + c.count
            );
        }
    }

    /**
     * Concatenates the char to this
     * {@link Alpha}, converting it to UTF-8 first
     *
     * @param c the specified character to be joined
     */
    public void join(char c) {
        asset = 0;
        byte[] it = value;

        // U+0000 ~ U+007F
        if (c < 0x80) {
            if (count != it.length) {
                it[count++] = (byte) c;
            } else {
                grow(count + 1)[count++] = (byte) c;
            }
        }

        // U+0080 ~ U+07FF
        else if (c < 0x800) {
            int size = count + 2;
            if (size > it.length) {
                it = grow(size);
            }
            it[count++] = (byte) (c >> 6 | 0xC0);
            it[count++] = (byte) (c & 0x3F | 0x80);
        }

        // U+10000 ~ U+10FFFF
        else if (0xD7FF < c && c < 0xE000) {
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
            it[count++] = (byte) (c >> 12 | 0xE0);
            it[count++] = (byte) (c >> 6 & 0x3F | 0x80);
            it[count++] = (byte) (c & 0x3F | 0x80);
        }
    }

    /**
     * Concatenates the array to this
     * {@link Alpha}, converting it to UTF-8 first
     *
     * @param ch the specified array to be joined
     * @throws NullPointerException If the specified array is null
     */
    public void join(
        @NotNull char[] ch
    ) {
        join(
            ch, 0, ch.length
        );
    }

    /**
     * Concatenates the array to this
     * {@link Alpha}, converting it to UTF-8 first
     *
     * @param ch the specified array to be joined
     * @param i  the specified start index for array
     * @param l  the specified length of array to join
     * @throws NullPointerException           If the specified array is null
     * @throws ArrayIndexOutOfBoundsException If the index or length out of range
     */
    public void join(
        @NotNull char[] ch, int i, int l
    ) {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= ch.length) {
            if (l != 0) {
                asset = 0;
                byte[] it = grow(
                    count + l
                );
                do {
                    // next char
                    char code = ch[i++];

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
                            char arch = ch[i];
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
                "Out of bounds, i:" + i + " l:" + l + " length:" + ch.length
            );
        }
    }

    /**
     * Concatenates the char sequence to this
     * {@link Alpha}, converting it to UTF-8 first
     *
     * @param ch the specified sequence to be joined
     * @throws NullPointerException If the specified sequence is null
     */
    public void join(
        @NotNull CharSequence ch
    ) {
        join(
            ch, 0, ch.length()
        );
    }

    /**
     * Concatenates the char sequence to this
     * {@link Alpha}, converting it to UTF-8 first
     *
     * @param ch the specified sequence to be joined
     * @param i  the specified start index for sequence
     * @param l  the specified length of sequence to join
     * @throws NullPointerException           If the specified sequence is null
     * @throws ArrayIndexOutOfBoundsException If the index or length out of range
     */
    public void join(
        @NotNull CharSequence ch, int i, int l
    ) {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= ch.length()) {
            if (l != 0) {
                asset = 0;
                byte[] it = grow(
                    count + l
                );
                do {
                    // next char
                    char code = ch.charAt(i++);

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
                            char arch = ch.charAt(i);
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
                "Out of bounds, i:" + i + " l:" + l + " length:" + ch.length()
            );
        }
    }

    /**
     * Sets the specified length of this chain
     *
     * <pre>{@code
     *  Alpha alpha = ..
     *  alpha.join("plus.kat");
     *  alpha.slip(3);
     *  int length = value.length(); // 3
     * }</pre>
     *
     * @param length the specified length
     * @throws ArrayIndexOutOfBoundsException if the index argument is negative or out of range
     */
    public void slip(int length) {
        if (length == 0) {
            asset = 0;
            count = 0;
        } else {
            if (length < 0 || length > value.length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            asset = 0;
            count = length;
        }
    }

    /**
     * Reset this {@link Alpha}
     */
    public void reset() {
        asset = 0;
        count = 0;
        backup = null;
    }

    /**
     * Clear this {@link Alpha}
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
     * Close this {@link Alpha}
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
    public static class Memory implements Bucket {

        public static final int SIZE, SCALE;

        static {
            SIZE = Config.get(
                "kat.memory.size", 4
            );
            SCALE = Config.get(
                "kat.memory.scale", 1024 * 4
            );
        }

        public static final Memory
            INS = new Memory();

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
        public boolean join(
            @Nullable byte[] it
        ) {
            if (it != null && SCALE == it.length) {
                Thread th = Thread.currentThread();
                int ix = (th.hashCode() & 0xFFFFFF) % SIZE;
                synchronized (this) {
                    bucket[ix] = it;
                }
                return true;
            }
            return false;
        }

        @Override
        public byte[] swap(
            @Nullable byte[] it
        ) {
            this.join(it);
            return EMPTY_BYTES;
        }

        @Override
        public byte[] alloc(
            @NotNull byte[] it, int len, int size
        ) {
            Thread th = Thread.currentThread();
            int ix = (th.hashCode() & 0xFFFFFF) % SIZE;

            byte[] data;
            if (size <= SCALE) {
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
                if (cap < size) {
                    cap = size;
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
    }
}
