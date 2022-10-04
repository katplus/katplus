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

import plus.kat.crash.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;
import plus.kat.utils.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Value extends Dram {
    /**
     * Constructs a mutable value
     */
    public Value() {
        super();
    }

    /**
     * Constructs a mutable value
     *
     * @param size the initial capacity
     */
    public Value(
        int size
    ) {
        super(size);
    }

    /**
     * Constructs a mutable value
     *
     * @param data the initial byte array
     */
    public Value(
        @NotNull byte[] data
    ) {
        super(data);
    }

    /**
     * Constructs a mutable value
     *
     * @param data the specified chain to be used
     */
    public Value(
        @NotNull Chain data
    ) {
        super(data);
    }

    /**
     * Constructs a mutable value
     *
     * @param bucket the specified bucket to be used
     */
    public Value(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * Constructs a mutable value
     *
     * @param chain  the specified chain to be used
     * @param bucket the specified bucket to be used
     */
    public Value(
        @NotNull Chain chain,
        @Nullable Bucket bucket
    ) {
        super(
            chain, bucket
        );
    }

    /**
     * Constructs a mutable value
     *
     * @param sequence the specified sequence to be used
     */
    public Value(
        @Nullable CharSequence sequence
    ) {
        super(sequence);
    }

    /**
     * Returns a {@link Value} that
     * is a subsequence of this {@link Value}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public Value subSequence(
        int start, int end
    ) {
        return new Value(
            toBytes(start, end)
        );
    }

    /**
     * Appends the char value to this {@link Value}
     *
     * <pre>{@code
     *   Value value = ...
     *   value.add('k');
     * }</pre>
     *
     * @param c the specified char value
     */
    public void add(
        char c
    ) {
        chain(c);
    }

    /**
     * Appends the number to this {@link Value}
     *
     * <pre>{@code
     *   Value value = ...
     *   value.add(1024);
     * }</pre>
     *
     * @param num the specified int value
     * @since 0.0.4
     */
    public void add(
        int num
    ) {
        chain(num);
    }

    /**
     * Appends the number to this {@link Value}
     *
     * <pre>{@code
     *   Value value = ...
     *   value.add(1024L);
     * }</pre>
     *
     * @param num the specified long value
     * @since 0.0.4
     */
    public void add(
        long num
    ) {
        chain(num);
    }

    /**
     * Appends the byte array to this {@link Value}
     *
     * @param b the specified byte array
     */
    public void add(
        byte[] b
    ) {
        if (b != null) {
            chain(
                b, 0, b.length
            );
        }
    }

    /**
     * Appends the byte array to this {@link Value}
     *
     * @param b the specified byte array
     * @param i the specified index
     * @param l the specified length
     * @throws ArrayIndexOutOfBoundsException If the {@code index} or {@code length} ou of range
     * @since 0.0.4
     */
    public void add(
        byte[] b, int i, int l
    ) {
        if (b != null && l != 0) {
            if (i >= 0 && i + l <= b.length) {
                chain(
                    b, i, l
                );
            } else {
                throw new ArrayIndexOutOfBoundsException(
                    "Out of bounds, i:" + i + " l:" + l + " length:" + b.length
                );
            }
        }
    }

    /**
     * Appends the char array to this {@link Value}
     *
     * @param c the specified char array
     */
    public void add(
        char[] c
    ) {
        if (c != null) {
            int len = c.length;
            if (len != 0) {
                chain(
                    c, 0, len
                );
            }
        }
    }

    /**
     * Appends the char array to this {@link Value}
     *
     * @param c the specified byte array
     * @param i the specified index
     * @param l the specified length
     * @throws ArrayIndexOutOfBoundsException If the {@code index} or {@code length} ou of range
     * @since 0.0.4
     */
    public void add(
        char[] c, int i, int l
    ) {
        if (c != null && l != 0) {
            if (i >= 0 && i + l <= c.length) {
                chain(
                    c, i, l
                );
            } else {
                throw new ArrayIndexOutOfBoundsException(
                    "Out of bounds, i:" + i + " l:" + l + " length:" + c.length
                );
            }
        }
    }

    /**
     * Appends the {@link InputStream} to this {@link Value}
     *
     * <pre>{@code
     *  Value value = ...
     *  InputStream in = ...
     *  value.add(in); // auto close
     * }</pre>
     *
     * @param in the specified {@link InputStream} will be used and closed
     * @since 0.0.3
     */
    public void add(
        InputStream in
    ) {
        if (in != null) {
            chain(in);
        }
    }

    /**
     * Appends the {@link InputStream} to this {@link Value}
     *
     * <pre>{@code
     *  Value value = ...
     *  InputStream in = ...
     *  value.add(in, 512);
     *  in.close(); // close it
     *
     *  // or
     *  try (InputStream in = ...) {
     *      value.add(in, 512);
     *  }
     * }</pre>
     *
     * @param range the specified range
     * @param in    the specified {@link InputStream} will be used but will not be closed
     * @throws IOException If an I/O error occurs
     * @since 0.0.5
     */
    public void add(
        InputStream in, int range
    ) throws IOException {
        if (in != null) {
            if (range > 0) {
                chain(
                    in, range
                );
            } else {
                throw new UnexpectedCrash(
                    "Unexpectedly, the range is not a positive number"
                );
            }
        }
    }

    /**
     * Appends the {@link CharSequence} to this {@link Value}
     *
     * @param c the specified char array
     */
    public void add(
        CharSequence c
    ) {
        if (c != null) {
            int len = c.length();
            if (len != 0) {
                chain(
                    c, 0, len
                );
            }
        }
    }

    /**
     * Appends the {@link CharSequence} to this {@link Value}
     *
     * @param c the specified byte array
     * @param i the specified index
     * @param l the specified length
     * @throws ArrayIndexOutOfBoundsException If the {@code index} or {@code length} ou of range
     * @since 0.0.4
     */
    public void add(
        CharSequence c, int i, int l
    ) {
        if (c != null && l != 0) {
            if (i >= 0 && i + l <= c.length()) {
                chain(
                    c, i, l
                );
            } else {
                throw new ArrayIndexOutOfBoundsException(
                    "Out of bounds, i:" + i + " l:" + l + " length:" + c.length()
                );
            }
        }
    }

    /**
     * Adds the data to uppercase hexadecimal
     *
     * <pre>{@code
     *   Value value = ...
     *   value.upper(new byte[]{1, 11, 111}); // 010B6F
     * }</pre>
     *
     * @param data the specified data to be encoded
     * @see Value#lower(byte[])
     * @since 0.0.4
     */
    public void upper(
        byte[] data
    ) {
        if (data != null) {
            int size = data.length;
            if (size != 0) {
                grow(count * size * 2);
                star = 0;
                int i = 0;
                byte[] it = value;
                while (i < size) {
                    int o = data[i++] & 0xFF;
                    it[count++] = Binary.upper(o >> 4);
                    it[count++] = Binary.upper(o & 0xF);
                }
            }
        }
    }

    /**
     * Adds the data to lowercase hexadecimal
     *
     * <pre>{@code
     *   Value value = ...
     *   value.lower(new byte[]{1, 11, 111}); // 010b6f
     * }</pre>
     *
     * @param data the specified data to be encoded
     * @see Value#upper(byte[])
     * @since 0.0.4
     */
    public void lower(
        byte[] data
    ) {
        if (data != null) {
            int size = data.length;
            if (size != 0) {
                grow(count * size * 2);
                star = 0;
                int i = 0;
                byte[] it = value;
                while (i < size) {
                    int o = data[i++] & 0xFF;
                    it[count++] = Binary.lower(o >> 4);
                    it[count++] = Binary.lower(o & 0xF);
                }
            }
        }
    }

    /**
     * Sets the length of this value
     *
     * <pre>{@code
     *  Value value = ..
     *  value.add("plus.kat");
     *  value.slip(3);
     *  int length = value.length(); // 3
     * }</pre>
     *
     * @param length the specified length
     * @throws ArrayIndexOutOfBoundsException if the index argument is negative or out of range
     * @see Value#length()
     */
    public void slip(
        int length
    ) {
        if (length == 0) {
            star = 0;
            count = 0;
        } else {
            if (length < 0 || length > value.length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            star = 0;
            count = length;
        }
    }

    /**
     * Returns a mutable value of clone this {@link Value}
     */
    @NotNull
    public Value copy() {
        return new Value(this);
    }

    /**
     * @see Value#Value(Bucket)
     */
    public static Value apply() {
        return new Value(
            Buffer.INS
        );
    }

    /**
     * @param b the {@code byte} to be compared
     */
    public static boolean esc(byte b) {
        switch (b) {
            case '^':
            case '(':
            case ')': {
                return true;
            }
        }
        return false;
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Buffer implements Bucket {

        private static final int SIZE, LIMIT, SCALE;

        static {
            SIZE = Config.get(
                "kat.value.size", 8
            );
            LIMIT = Config.get(
                "kat.value.limit", 16
            );

            if (LIMIT < SIZE) {
                throw new Error(
                    "Bucket's size(" + SIZE + ") cannot be greater than the limit(" + LIMIT + ")"
                );
            }

            SCALE = Config.get(
                "kat.value.scale", 1024
            );
        }

        public static final Buffer
            INS = new Buffer();

        private final byte[][]
            bucket = new byte[SIZE][];

        @Override
        public boolean share(
            @NotNull byte[] it
        ) {
            int i = it.length / SCALE;
            if (i < SIZE) {
                synchronized (this) {
                    bucket[i] = it;
                }
                return true;
            }
            return false;
        }

        @Override
        public byte[] swop(
            @NotNull byte[] it
        ) {
            int i = it.length / SCALE;
            if (i == 0) {
                return it;
            }

            byte[] data;
            synchronized (this) {
                if (i < SIZE) {
                    bucket[i] = it;
                }
                data = bucket[i];
                bucket[i] = null;
            }
            return data == null ? EMPTY_BYTES : data;
        }

        @Override
        public byte[] apply(
            @NotNull byte[] it, int len, int size
        ) {
            byte[] data;
            int i = size / SCALE;

            if (i < SIZE) {
                synchronized (this) {
                    data = bucket[i];
                    bucket[i] = null;
                }
                if (data == null ||
                    data.length < size) {
                    data = new byte[(i + 1) * SCALE - 1];
                }
            } else {
                if (i < LIMIT) {
                    data = new byte[(i + 1) * SCALE - 1];
                } else {
                    throw new Collapse(
                        "Unexpectedly, Exceeding range '" + LIMIT * SCALE + "' in value"
                    );
                }
            }

            if (it.length != 0) {
                System.arraycopy(
                    it, 0, data, 0, len
                );

                int k = it.length / SCALE;
                if (k < SIZE) {
                    synchronized (this) {
                        bucket[k] = it;
                    }
                }
            }

            return data;
        }
    }
}
