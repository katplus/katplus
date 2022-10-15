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

import plus.kat.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;
import plus.kat.utils.Config;

import java.io.Closeable;

import static plus.kat.Algo.KAT;
import static plus.kat.stream.Binary.*;

/**
 * @author kraity
 * @since 0.0.5
 */
public abstract class Steam extends Chain implements Flow, Closeable {

    protected long flags;
    protected short depth;

    /**
     * Constructs a steam with the specified flags
     *
     * @param flags the specified flags of {@link Flag}
     */
    public Steam(
        long flags
    ) {
        super(Buffer.INS);
        this.flags = flags;
        if ((flags & 1) == 0) {
            this.depth = Short.MIN_VALUE;
        }
    }

    /**
     * Check if this {@link Steam} use the {@code flag}
     *
     * @param flag the specified {@code flag}
     */
    @Override
    public boolean isFlag(
        long flag
    ) {
        return (flags & flag) == flag;
    }

    /**
     * Concatenates the byte value to this {@link Steam},
     * which will be escaped if it is a special character
     *
     * @param b the specified byte value to be appended
     */
    @Override
    public void emit(
        byte b
    ) {
        concat(b);
    }

    /**
     * Concatenates the string representation
     * of the integer value to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        int num
    ) {
        concat(num);
    }

    /**
     * Concatenates the format an integer value
     * (treated as unsigned) to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        int num, int shift
    ) {
        if (0 < shift && shift < 6) {
            int mark = count;
            int mask = (1 << shift) - 1;
            do {
                concat(lower(
                    (num & mask)
                ));
                num >>>= shift;
            } while (num != 0);
            swop(mark, count);
        }
    }

    /**
     * Concatenates the format an integer value
     * (treated as unsigned) to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        int num, int shift, int length
    ) {
        if (0 < shift && shift < 6) {
            int mark = count;
            int mask = (1 << shift) - 1;
            while (--length != -1) {
                concat(lower(
                    (num & mask)
                ));
                num >>>= shift;
            }
            swop(mark, count);
        }
    }

    /**
     * Concatenates the string representation
     * of the long value to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        long num
    ) {
        concat(num);
    }

    /**
     * Concatenates the format a long value
     * (treated as unsigned) to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        long num, int shift
    ) {
        if (0 < shift && shift < 6) {
            int mark = count;
            long mask = (1L << shift) - 1L;
            do {
                concat(lower(
                    (int) (num & mask)
                ));
                num >>>= shift;
            } while (num != 0L);
            swop(mark, count);
        }
    }

    /**
     * Concatenates the format a long value
     * (treated as unsigned) to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        long num, int shift, int length
    ) {
        if (0 < shift && shift < 6) {
            int mark = count;
            long mask = (1L << shift) - 1L;
            while (--length != -1) {
                concat(lower(
                    (int) (num & mask)
                ));
                num >>>= shift;
            }
            swop(mark, count);
        }
    }

    /**
     * Concatenates the string representation
     * of the short value to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        short num
    ) {
        concat(num);
    }

    /**
     * Concatenates the string representation
     * of the float value to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        float num
    ) {
        concat(num);
    }

    /**
     * Concatenates the hexadecimal format of float
     * value (treated as unsigned) to this {@link Flow}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        float num, boolean boot
    ) {
        asset = 0;
        byte[] it;
        if (boot) {
            grow(count + 10);
            it = value;
            it[count++] = '0';
            it[count++] = 'x';
        } else {
            grow(count + 8);
            it = value;
        }

        int mark = count;
        int data = Float.floatToIntBits(num);
        for (int i = 0; i < 8; i++) {
            it[count++] = upper(
                data & 0xF
            );
            data >>>= 4;
        }
        swop(mark, count);
    }

    /**
     * Concatenates the string representation
     * of the double value to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        double num
    ) {
        concat(num);
    }

    /**
     * Concatenates the hexadecimal format of double
     * value (treated as unsigned) to this {@link Flow}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        double num, boolean boot
    ) {
        asset = 0;
        byte[] it;
        if (boot) {
            grow(count + 18);
            it = value;
            it[count++] = '0';
            it[count++] = 'x';
        } else {
            grow(count + 16);
            it = value;
        }

        int mark = count;
        long data = Double.doubleToLongBits(num);
        for (int i = 0; i < 16; i++) {
            it[count++] = upper(
                (int) (data & 0xF)
            );
            data >>>= 4;
        }
        swop(mark, count);
    }

    /**
     * Concatenates the string representation
     * of the boolean value to this {@link Steam}
     *
     * @param bool the specified boolean to be appended
     */
    @Override
    public void emit(
        boolean bool
    ) {
        if (algo() != KAT) {
            concat(bool);
        } else {
            concat(
                bool ? (byte) '1' : (byte) '0'
            );
        }
    }

    /**
     * Concatenates the char value to this {@link Steam},
     * which will be escaped if it is a special character
     *
     * @param ch the specified char value to be appended
     */
    @Override
    public void emit(
        char ch
    ) {
        if ((flags & 2) == 0) {
            concat(ch);
        } else {
            if (algo() == KAT) {
                concat(
                    ch, (byte) '^'
                );
            } else {
                concat(
                    ch, (byte) '\\'
                );
            }
        }
    }

    /**
     * Concatenates the byte array to this {@link Steam},
     * which will be escaped if it contains special characters
     *
     * @param data the specified source to be appended
     */
    @Override
    public void emit(
        @NotNull byte[] data
    ) {
        for (byte b : data) emit(b);
    }

    /**
     * Concatenates the byte array to this {@link Steam},
     * which will be escaped if it contains special characters
     *
     * @param data the specified source to be appended
     */
    @Override
    public void emit(
        @NotNull byte[] data, int i, int l
    ) {
        int k = i + l;
        if (0 < l && 0 <= i && k <= data.length) {
            grow(count + l);
            while (i < k) {
                emit(data[i++]);
            }
        }
    }

    /**
     * Concatenates the byte array to this {@link Steam}
     * and copy it directly if the specified type does not conflict with algo,
     * otherwise check to see if it contains special characters, concat it after escape
     */
    @Override
    public void emit(
        @NotNull byte[] data, char t, int i, int l
    ) {
        int k = i + l;
        if (0 < l && 0 <= i && k <= data.length) {
            if (t == 'B') {
                concat(
                    data, i, l
                );
            } else {
                grow(count + l);
                while (i < k) {
                    emit(data[i++]);
                }
            }
        }
    }

    /**
     * Concatenates the char array to this {@link Steam},
     * which will be escaped if it contains special characters
     *
     * @param data the specified source to be appended
     */
    @Override
    public void emit(
        @NotNull char[] data
    ) {
        int l = data.length;
        if (l != 0) {
            if ((flags & 2) == 0) {
                concat(
                    data, 0, l
                );
            } else {
                if (algo() == KAT) {
                    concat(
                        data, 0, l, (byte) '^'
                    );
                } else {
                    concat(
                        data, 0, l, (byte) '\\'
                    );
                }
            }
        }
    }

    /**
     * Concatenates the char array to this {@link Steam},
     * which will be escaped if it contains special characters
     *
     * @param data the specified source to be appended
     */
    @Override
    public void emit(
        @NotNull char[] data, int i, int l
    ) {
        if (0 < l && 0 <= i && i + l <= data.length) {
            if ((flags & 2) == 0) {
                concat(
                    data, i, l
                );
            } else {
                if (algo() == KAT) {
                    concat(
                        data, i, l, (byte) '^'
                    );
                } else {
                    concat(
                        data, i, l, (byte) '\\'
                    );
                }
            }
        }
    }

    /**
     * Concatenates the sequence to this {@link Steam},
     * which will be escaped if it contains special characters
     *
     * @param data the specified sequence to be appended
     */
    @Override
    public void emit(
        @NotNull CharSequence data
    ) {
        int l = data.length();
        if (l != 0) {
            if ((flags & 2) == 0) {
                concat(
                    data, 0, l
                );
            } else {
                if (algo() == KAT) {
                    concat(
                        data, 0, l, (byte) '^'
                    );
                } else {
                    concat(
                        data, 0, l, (byte) '\\'
                    );
                }
            }
        }
    }

    /**
     * Concatenates the sequence to this {@link Steam},
     * which will be escaped if it contains special characters
     *
     * @param data the specified sequence to be appended
     */
    @Override
    public void emit(
        @NotNull CharSequence data, int i, int l
    ) {
        if (0 < l && 0 <= i && i + l <= data.length()) {
            if ((flags & 2) == 0) {
                concat(
                    data, i, l
                );
            } else {
                if (algo() == KAT) {
                    concat(
                        data, i, l, (byte) '^'
                    );
                } else {
                    concat(
                        data, i, l, (byte) '\\'
                    );
                }
            }
        }
    }

    /**
     * Close this {@link Steam}
     */
    @Override
    public void close() {
        byte[] it = value;
        if (it.length != 0) {
            asset = 0;
            count = 0;
            backup = null;
            value = EMPTY_BYTES;
            Bucket bt = bucket;
            if (bt != null) bt.share(it);
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Buffer implements Bucket {

        private static final int SIZE, GROUP, SCALE;

        static {
            SIZE = Config.get(
                "kat.paper.size", 4
            );
            GROUP = Config.get(
                "kat.paper.group", 4
            );
            SCALE = Config.get(
                "kat.paper.scale", 1024
            );
        }

        public static final Buffer
            INS = new Buffer();

        private final byte[][]
            bucket = new byte[SIZE * GROUP][];

        @Override
        public boolean share(
            @NotNull byte[] it
        ) {
            int i = it.length / SCALE;
            if (i < GROUP) {
                Thread th = Thread.currentThread();
                int tr = th.hashCode() & 0xFFFFFF;
                int ix = i * GROUP + tr % SIZE;
                synchronized (this) {
                    bucket[ix] = it;
                }
                return true;
            }
            return false;
        }

        @Override
        public byte[] swop(
            @NotNull byte[] it
        ) {
            this.share(it);
            return EMPTY_BYTES;
        }

        @Override
        public byte[] apply(
            @NotNull byte[] it, int len, int size
        ) {
            byte[] data;
            int i = size / SCALE;

            Thread th = Thread.currentThread();
            int tr = th.hashCode() & 0xFFFFFF;

            if (i >= GROUP) {
                data = new byte[(i + 1) * SCALE - 1];
            } else {
                int ix = i * GROUP + tr % SIZE;
                synchronized (this) {
                    data = bucket[ix];
                    bucket[ix] = null;
                }
                if (data == null ||
                    data.length < size) {
                    data = new byte[(i + 1) * SCALE - 1];
                }
            }

            if (it.length != 0) {
                System.arraycopy(
                    it, 0, data, 0, len
                );

                i = it.length / SCALE;
                if (i < GROUP) {
                    int ix = i * GROUP + tr % SIZE;
                    synchronized (this) {
                        bucket[ix] = it;
                    }
                }
            }

            return data;
        }
    }
}
