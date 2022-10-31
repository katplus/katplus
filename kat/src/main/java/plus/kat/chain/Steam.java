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
import plus.kat.utils.*;

import java.io.IOException;

import static plus.kat.stream.Binary.*;

/**
 * @author kraity
 * @since 0.0.5
 */
public abstract class Steam extends Alpha implements Flow {

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
     * Concatenates the format an integer value
     * (treated as unsigned) to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        int num, int s
    ) throws IOException {
        if (0 < s && s < 6) {
            asset = 0;
            byte[] it = value;

            int i = count;
            int arch = (1 << s) - 1;
            do {
                if (count == it.length) {
                    it = grow(count + 1);
                }
                it[count++] = lower((num & arch));
            } while (
                (num >>>= s) != 0
            );

            arch = count;
            for (byte j; i < --arch; it[i++] = j) {
                j = it[arch];
                it[arch] = it[i];
            }
        } else {
            throw new IOException(
                "The specified shift is not in [0, 5]"
            );
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
        int num, int s, int l
    ) throws IOException {
        if (0 < s && s < 6) {
            asset = 0;
            byte[] it = value;

            int i = count;
            int arch = (1 << s) - 1;
            while (--l != -1) {
                if (count == it.length) {
                    it = grow(count + 1);
                }
                it[count++] = lower((num & arch));
                num >>>= s;
            }

            arch = count;
            for (byte j; i < --arch; it[i++] = j) {
                j = it[arch];
                it[arch] = it[i];
            }
        } else {
            throw new IOException(
                "The specified shift is not in [0, 5]"
            );
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
        long num, int s
    ) throws IOException {
        if (0 < s && s < 6) {
            asset = 0;
            byte[] it = value;

            int i = count;
            long arch = (1L << s) - 1L;
            do {
                if (count == it.length) {
                    it = grow(count + 1);
                }
                it[count++] = lower((int) (num & arch));
            } while (
                (num >>>= s) != 0L
            );

            int apex = count;
            for (byte j; i < --apex; it[i++] = j) {
                j = it[apex];
                it[apex] = it[i];
            }
        } else {
            throw new IOException(
                "The specified shift is not in [0, 5]"
            );
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
        long num, int s, int l
    ) throws IOException {
        if (0 < s && s < 6) {
            asset = 0;
            byte[] it = value;

            int i = count;
            long arch = (1L << s) - 1L;
            while (--l != -1) {
                if (count == it.length) {
                    it = grow(count + 1);
                }
                it[count++] = lower((int) (num & arch));
                num >>>= s;
            }

            int apex = count;
            for (byte j; i < --apex; it[i++] = j) {
                j = it[apex];
                it[apex] = it[i];
            }
        } else {
            throw new IOException(
                "The specified shift is not in [0, 5]"
            );
        }
    }

    /**
     * Concatenates the string representation
     * of the short value to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    public void emit(
        short num
    ) throws IOException {
        emit((int) num);
    }

    /**
     * Concatenates the string representation
     * of the float value to this {@link Alpha}
     *
     * @param num the specified number to be appended
     */
    @SuppressWarnings("deprecation")
    public void emit(
        float num
    ) {
        String data =
            Float.toString(num);
        int len = data.length();
        int now = count;
        int size = now + len;
        byte[] it = value;
        if (size > it.length) {
            it = grow(size);
        }
        asset = 0;
        count = size;
        data.getBytes(0, len, it, now);
    }

    /**
     * Concatenates the string representation
     * of the double value to this {@link Alpha}
     *
     * @param num the specified number to be appended
     */
    @SuppressWarnings("deprecation")
    public void emit(
        double num
    ) {
        String data =
            Double.toString(num);
        int len = data.length();
        int now = count;
        int size = now + len;
        byte[] it = value;
        if (size > it.length) {
            it = grow(size);
        }
        asset = 0;
        count = size;
        data.getBytes(0, len, it, now);
    }

    /**
     * Concatenates the string representation
     * of the boolean value to this {@link Alpha}
     *
     * @param bool the specified boolean to be appended
     */
    @Override
    public void emit(
        boolean bool
    ) throws IOException {
        asset = 0;
        byte[] it = value;
        if (bool) {
            int size = count + 4;
            if (size > it.length) {
                it = grow(size);
            }
            it[count++] = 't';
            it[count++] = 'r';
            it[count++] = 'u';
        } else {
            int size = count + 5;
            if (size > it.length) {
                it = grow(size);
            }
            it[count++] = 'f';
            it[count++] = 'a';
            it[count++] = 'l';
            it[count++] = 's';
        }
        it[count++] = 'e';
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
    ) throws IOException {
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
    ) throws IOException {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= data.length) {
            while (i < k) {
                emit(data[i++]);
            }
        } else {
            throw new IOException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + data.length
            );
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
    ) throws IOException {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= data.length) {
            if (l != 0) {
                if (t == 'B') {
                    System.arraycopy(
                        data, i, grow(count + l), count, l
                    );
                    asset = 0;
                    count += l;
                } else {
                    while (i < k) {
                        emit(data[i++]);
                    }
                }
            }
        } else {
            throw new IOException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + data.length
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
    ) throws IOException {
        if (ch < 0x80) {
            emit((byte) ch);
        } else {
            if ((flags & 2) == 0) {
                join(ch);
            } else {
                byte[] it = value;
                int size = count + 6;
                if (size > it.length) {
                    it = grow(size);
                }
                it[count++] = algo().esc();
                it[count++] = 'u';
                it[count++] = upper(ch >> 12 & 0x0F);
                it[count++] = upper(ch >> 8 & 0x0F);
                it[count++] = upper(ch >> 4 & 0x0F);
                it[count++] = upper(ch & 0x0F);
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
    ) throws IOException {
        emit(data, 0, data.length);
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
    ) throws IOException {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= data.length) {
            if (l != 0) {
                asset = 0;
                if ((flags & 2) == 2) {
                    byte esc = algo().esc();
                    do {
                        char ch = data[i++];
                        if (ch < 0x80) {
                            emit((byte) ch);
                        } else {
                            byte[] it = value;
                            int size = count + 6;
                            if (size > it.length) {
                                it = grow(size);
                            }
                            it[count++] = esc;
                            it[count++] = 'u';
                            it[count++] = upper(ch >> 12 & 0x0F);
                            it[count++] = upper(ch >> 8 & 0x0F);
                            it[count++] = upper(ch >> 4 & 0x0F);
                            it[count++] = upper(ch & 0x0F);
                        }
                    } while (i < k);
                } else {
                    do {
                        // next char
                        char code = data[i++];

                        // U+0000 ~ U+007F
                        if (code < 0x80) {
                            emit((byte) code);
                        }

                        // U+0080 ~ U+07FF
                        else if (code < 0x800) {
                            byte[] it = value;
                            int size = count + 2;
                            if (size > it.length) {
                                it = grow(size);
                            }
                            it[count++] = (byte) (code >> 6 | 0xC0);
                            it[count++] = (byte) (code & 0x3F | 0x80);
                        }

                        // U+10000 ~ U+10FFFF
                        else if (0xD7FF < code && code < 0xE000) {
                            if (code > 0xDBFF) {
                                emit((byte) '?');
                                continue;
                            }

                            if (k == i) {
                                emit((byte) '?');
                                break;
                            }

                            char arch = data[i];
                            if (arch < 0xDC00 ||
                                arch > 0xDFFF) {
                                emit((byte) '?');
                                continue;
                            }

                            int hi = code - 0xD7C0;
                            int lo = arch - 0xDC00;

                            byte[] it = value;
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
                            byte[] it = value;
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
            }
        } else {
            throw new IOException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + data.length
            );
        }
    }

    /**
     * Concatenates the chain to this {@link Steam},
     * which will be escaped if it contains special characters
     *
     * @param data the specified chain to be appended
     */
    @Override
    public void emit(
        @NotNull Chain data
    ) throws IOException {
        int size = data.length();
        if (size != 0) {
            int i = 0;
            byte[] it = Unsafe.value(data);
            while (i < size) {
                emit(it[i++]);
            }
        }
    }

    /**
     * Concatenates the chain to this {@link Steam},
     * which will be escaped if it contains special characters
     *
     * @param data the specified chain to be appended
     */
    @Override
    public void emit(
        @NotNull Chain data, int i, int l
    ) throws IOException {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= data.length()) {
            byte[] it = Unsafe.value(data);
            while (i < k) {
                emit(it[i++]);
            }
        } else {
            throw new IOException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + data.length()
            );
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
    ) throws IOException {
        emit(data, 0, data.length());
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
    ) throws IOException {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= data.length()) {
            if (l != 0) {
                asset = 0;
                if ((flags & 2) == 2) {
                    byte esc = algo().esc();
                    do {
                        char ch = data.charAt(i++);
                        if (ch < 0x80) {
                            emit((byte) ch);
                        } else {
                            byte[] it = value;
                            int size = count + 6;
                            if (size > it.length) {
                                it = grow(size);
                            }
                            it[count++] = esc;
                            it[count++] = 'u';
                            it[count++] = upper(ch >> 12 & 0x0F);
                            it[count++] = upper(ch >> 8 & 0x0F);
                            it[count++] = upper(ch >> 4 & 0x0F);
                            it[count++] = upper(ch & 0x0F);
                        }
                    } while (i < k);
                } else {
                    do {
                        // next char
                        char code = data.charAt(i++);

                        // U+0000 ~ U+007F
                        if (code < 0x80) {
                            emit((byte) code);
                        }

                        // U+0080 ~ U+07FF
                        else if (code < 0x800) {
                            byte[] it = value;
                            int size = count + 2;
                            if (size > it.length) {
                                it = grow(size);
                            }
                            it[count++] = (byte) (code >> 6 | 0xC0);
                            it[count++] = (byte) (code & 0x3F | 0x80);
                        }

                        // U+10000 ~ U+10FFFF
                        else if (0xD7FF < code && code < 0xE000) {
                            if (code > 0xDBFF) {
                                emit((byte) '?');
                                continue;
                            }

                            if (k == i) {
                                emit((byte) '?');
                                break;
                            }

                            char arch = data.charAt(i);
                            if (arch < 0xDC00 ||
                                arch > 0xDFFF) {
                                emit((byte) '?');
                                continue;
                            }

                            int hi = code - 0xD7C0;
                            int lo = arch - 0xDC00;

                            byte[] it = value;
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
                            byte[] it = value;
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
            }
        } else {
            throw new IOException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + data.length()
            );
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
        public boolean join(
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
        public byte[] swap(
            @NotNull byte[] it
        ) {
            this.join(it);
            return EMPTY_BYTES;
        }

        @Override
        public byte[] alloc(
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
