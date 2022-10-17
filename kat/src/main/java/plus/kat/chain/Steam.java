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

import java.io.IOException;

import static plus.kat.stream.Binary.*;

/**
 * @author kraity
 * @since 0.0.5
 */
public abstract class Steam extends Chain implements Flow {

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
     * Concatenates the string representation
     * of the integer value to this {@link Steam}
     *
     * @param num the specified number to be appended
     */
    @Override
    public void emit(
        int num
    ) throws IOException {
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
        int num, int s
    ) throws IOException {
        if (0 < s && s < 6) {
            int mark = count;
            int mask = (1 << s) - 1;
            do {
                concat(lower(
                    (num & mask)
                ));
                num >>>= s;
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
        int num, int s, int l
    ) throws IOException {
        if (0 < s && s < 6) {
            int mark = count;
            int mask = (1 << s) - 1;
            while (--l != -1) {
                concat(lower(
                    (num & mask)
                ));
                num >>>= s;
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
    ) throws IOException {
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
        long num, int s
    ) throws IOException {
        if (0 < s && s < 6) {
            int mark = count;
            long mask = (1L << s) - 1L;
            do {
                concat(lower(
                    (int) (num & mask)
                ));
                num >>>= s;
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
        long num, int s, int l
    ) throws IOException {
        if (0 < s && s < 6) {
            int mark = count;
            long mask = (1L << s) - 1L;
            while (--l != -1) {
                concat(lower(
                    (int) (num & mask)
                ));
                num >>>= s;
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
    ) throws IOException {
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
    ) throws IOException {
        concat(num);
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
    ) throws IOException {
        concat(num);
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
    ) throws IOException {
        concat(bool);
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
    ) throws IOException {
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
     * Concatenates the char value to this
     * {@link Steam}, conventing it to unicode first
     *
     * @param ch the specified char value to be appended
     */
    public void save(
        char ch
    ) {
        byte[] it = value;
        int size = count + 6;
        if (size > it.length) {
            grow(size);
            it = value;
        }
        it[count++] = '\\';
        it[count++] = 'u';
        it[count++] = upper((ch >> 12) & 0x0F);
        it[count++] = upper((ch >> 8) & 0x0F);
        it[count++] = upper((ch >> 4) & 0x0F);
        it[count++] = upper(ch & 0x0F);
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
            emit(
                (byte) ch
            );
        } else {
            if ((flags & 2) == 2) {
                save(ch);
            } else {
                concat(ch);
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
        if (0 < l && 0 <= i && k <= data.length) {
            grow(count + l);
            if ((flags & 2) == 2) {
                while (i < k) {
                    char c = data[i++];
                    if (c < 0x80) {
                        emit(
                            (byte) c
                        );
                    } else save(c);
                }
            } else {
                asset = 0;
                while (i < k) {
                    // next char
                    char c = data[i++];

                    // U+0000 ~ U+007F
                    if (c < 0x80) {
                        emit((byte) c);
                    }

                    // U+0080 ~ U+07FF
                    else if (c < 0x800) {
                        byte[] it = value;
                        int size = count + 2;
                        if (size > it.length) {
                            grow(size);
                            it = value;
                        }
                        it[count++] = (byte) ((c >> 6) | 0xC0);
                        it[count++] = (byte) ((c & 0x3F) | 0x80);
                    }

                    // U+10000 ~ U+10FFFF
                    // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
                    else if (0xD800 <= c && c <= 0xDFFF) {
                        if (k <= i) {
                            emit((byte) '?');
                            break;
                        }

                        char next = data[i++];
                        if (next < 0xDC00 ||
                            next > 0xDFFF) {
                            emit((byte) '?');
                            continue;
                        }

                        byte[] it = value;
                        int size = count + 4;
                        if (size > it.length) {
                            grow(size);
                            it = value;
                        }
                        int u = (c << 10) + next - 0x35F_DC00;
                        it[count++] = (byte) ((u >> 18) | 0xF0);
                        it[count++] = (byte) (((u >> 12) & 0x3F) | 0x80);
                        it[count++] = (byte) (((u >> 6) & 0x3F) | 0x80);
                        it[count++] = (byte) ((u & 0x3F) | 0x80);
                    }

                    // U+0800 ~ U+FFFF
                    else {
                        byte[] it = value;
                        int size = count + 3;
                        if (size > it.length) {
                            grow(size);
                            it = value;
                        }
                        it[count++] = (byte) ((c >> 12) | 0xE0);
                        it[count++] = (byte) (((c >> 6) & 0x3F) | 0x80);
                        it[count++] = (byte) ((c & 0x3F) | 0x80);
                    }
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
        if (0 < l && 0 <= i && k <= data.length()) {
            grow(count + l);
            if ((flags & 2) == 2) {
                while (i < k) {
                    char c = data.charAt(i++);
                    if (c < 0x80) {
                        emit(
                            (byte) c
                        );
                    } else save(c);
                }
            } else {
                asset = 0;
                while (i < k) {
                    // next char
                    char c = data.charAt(i++);

                    // U+0000 ~ U+007F
                    if (c < 0x80) {
                        emit((byte) c);
                    }

                    // U+0080 ~ U+07FF
                    else if (c < 0x800) {
                        byte[] it = value;
                        int size = count + 2;
                        if (size > it.length) {
                            grow(size);
                            it = value;
                        }
                        it[count++] = (byte) ((c >> 6) | 0xC0);
                        it[count++] = (byte) ((c & 0x3F) | 0x80);
                    }

                    // U+10000 ~ U+10FFFF
                    // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
                    else if (0xD800 <= c && c <= 0xDFFF) {
                        if (k <= i) {
                            emit((byte) '?');
                            break;
                        }

                        char next = data.charAt(i++);
                        if (next < 0xDC00 ||
                            next > 0xDFFF) {
                            emit((byte) '?');
                            continue;
                        }

                        byte[] it = value;
                        int size = count + 4;
                        if (size > it.length) {
                            grow(size);
                            it = value;
                        }
                        int u = (c << 10) + next - 0x35F_DC00;
                        it[count++] = (byte) ((u >> 18) | 0xF0);
                        it[count++] = (byte) (((u >> 12) & 0x3F) | 0x80);
                        it[count++] = (byte) (((u >> 6) & 0x3F) | 0x80);
                        it[count++] = (byte) ((u & 0x3F) | 0x80);
                    }

                    // U+0800 ~ U+FFFF
                    else {
                        byte[] it = value;
                        int size = count + 3;
                        if (size > it.length) {
                            grow(size);
                            it = value;
                        }
                        it[count++] = (byte) ((c >> 12) | 0xE0);
                        it[count++] = (byte) (((c >> 6) & 0x3F) | 0x80);
                        it[count++] = (byte) ((c & 0x3F) | 0x80);
                    }
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
