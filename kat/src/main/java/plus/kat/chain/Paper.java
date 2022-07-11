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

import plus.kat.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;

import static plus.kat.stream.Binary.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Paper extends Value implements Flow {

    protected long flags;

    /**
     * default
     */
    public Paper() {
        super();
    }

    /**
     * @param size the initial capacity
     */
    public Paper(
        int size
    ) {
        super(size);
    }

    /**
     * @param flags the specified {@code flags}
     */
    public Paper(
        long flags
    ) {
        super();
        this.flags = flags;
    }

    /**
     * @param data the initial byte array
     */
    public Paper(
        @NotNull byte[] data
    ) {
        super(data);
    }

    /**
     * @param data specify the {@link Chain} to be mirrored
     */
    public Paper(
        @NotNull Chain data
    ) {
        super(data);
    }

    /**
     * @param data specify the {@link CharSequence} to be mirrored
     */
    public Paper(
        @Nullable CharSequence data
    ) {
        super();
        if (data != null) {
            addChars(data);
        }
    }

    /**
     * @param bucket the specified {@link Bucket} to be used
     */
    public Paper(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * Check if this {@link Paper} use the {@code flag}
     *
     * @param flag the specified {@code flag}
     */
    @Override
    public boolean isFlag(
        long flag
    ) {
        return (flags & flag) != 0;
    }

    /**
     * clean this {@link Paper}
     *
     * @see Chain#clean()
     */
    public void clean() {
        super.clean();
    }

    /**
     * clear this {@link Paper}
     *
     * @see Chain#clear()
     */
    @Override
    public void clear() {
        super.clear();
    }

    /**
     * close this {@link Paper}
     *
     * @see Chain#close()
     */
    @Override
    public void close() {
        super.close();
    }

    /**
     * Set the specified source
     */
    public void setSource(
        @Nullable byte[] src
    ) {
        hash = 0;
        count = 0;
        value = src != null ? src : EMPTY_BYTES;
    }

    /**
     * Returns the internal byte array of {@link Paper}
     */
    @NotNull
    public byte[] getSource() {
        return value;
    }

    /**
     * Returns a {@link Paper} of this {@link Paper}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @NotNull
    @Override
    public Paper subSequence(
        int start, int end
    ) {
        return new Paper(
            copyBytes(start, end)
        );
    }

    /**
     * @param b the specified byte value
     */
    @Override
    public void addByte(
        byte b
    ) {
        grow(count + 1);
        hash = 0;
        value[count++] = b;
    }

    /**
     * @param c the specified char value
     */
    @Override
    public void addChar(
        char c
    ) {
        super.chain(c);
    }

    /**
     * @param num the specified short value
     * @see Paper#addInt(int)
     */
    @Override
    public void addShort(
        short num
    ) {
        addInt(num);
    }

    /**
     * @param num the specified int value
     */
    @Override
    public void addInt(
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
     * @param num   the specified int value
     * @param shift the log2 of the base to format
     */
    @Override
    public void addInt(
        int num, int shift
    ) {
        if (shift > 0 && shift < 6) {
            int mark = count;
            int mask = (1 << shift) - 1;
            do {
                grow(count + 1);
                value[count++] = lower(num & mask);
                num >>>= shift;
            } while (num != 0);
            swop(mark, count - 1);
        }
    }

    /**
     * @param num    the specified int value
     * @param shift  the log2 of the base to format
     * @param length the length of the output bits
     * @since 0.0.2
     */
    @Override
    public void addInt(
        int num, int shift, int length
    ) {
        if (shift > 0 && shift < 6) {
            int mark = count;
            int mask = (1 << shift) - 1;
            while (--length != -1) {
                grow(count + 1);
                value[count++] = lower((num & mask));
                num >>>= shift;
            }
            swop(mark, count - 1);
        }
    }

    /**
     * @param num the specified long value
     */
    @Override
    public void addLong(
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
     * @param num   the specified long value
     * @param shift the log2 of the base to format
     */
    @Override
    public void addLong(
        long num, int shift
    ) {
        if (shift > 0 && shift < 6) {
            int mark = count;
            long mask = (1L << shift) - 1L;
            do {
                grow(count + 1);
                value[count++] = lower((int) (num & mask));
                num >>>= shift;
            } while (num != 0L);
            swop(mark, count - 1);
        }
    }

    /**
     * @param num    the specified long value
     * @param shift  the log2 of the base to format
     * @param length the length of the output bits
     * @since 0.0.2
     */
    @Override
    public void addLong(
        long num, int shift, int length
    ) {
        if (shift > 0 && shift < 6) {
            int mark = count;
            long mask = (1L << shift) - 1L;
            while (--length != -1) {
                grow(count + 1);
                value[count++] = lower((int) (num & mask));
                num >>>= shift;
            }
            swop(mark, count - 1);
        }
    }

    /**
     * @param num the specified double value
     */
    @Override
    public void addFloat(
        float num
    ) {
        String data = Float
            .toString(num);

        int i = 0, l =
            data.length();
        hash = 0;
        grow(count + l);

        while (i < l) {
            value[count++] = (byte) data.charAt(i++);
        }
    }

    /**
     * @param num the specified float value
     */
    @Override
    public void addFloat(
        float num, boolean hint
    ) {
        hash = 0;
        if (hint) {
            grow(count + 10);
            value[count++] = '0';
            value[count++] = 'x';
        } else {
            grow(count + 8);
        }

        int mark = count;
        int data = Float.floatToIntBits(num);
        for (int i = 0; i < 8; i++) {
            grow(count + 1);
            value[count++] = upper(data & 0xF);
            data >>>= 4;
        }
        swop(mark, count - 1);
    }

    /**
     * @param num the specified double value
     */
    @Override
    public void addDouble(
        double num
    ) {
        String data = Double
            .toString(num);

        int i = 0, l =
            data.length();
        hash = 0;
        grow(count + l);

        while (i < l) {
            value[count++] = (byte) data.charAt(i++);
        }
    }

    /**
     * @param num the specified double value
     */
    @Override
    public void addDouble(
        double num, boolean hint
    ) {
        hash = 0;
        if (hint) {
            grow(count + 18);
            value[count++] = '0';
            value[count++] = 'x';
        } else {
            grow(count + 16);
        }

        int mark = count;
        long data = Double.doubleToLongBits(num);
        for (int i = 0; i < 16; i++) {
            grow(count + 1);
            value[count++] = upper((int) (data & 0xF));
            data >>>= 4;
        }
        swop(mark, count - 1);
    }

    /**
     * @param bool the specified boolean value
     */
    @Override
    public void addBoolean(
        boolean bool
    ) {
        grow(count + 1);
        if (bool) {
            value[count++] = '1';
        } else {
            value[count++] = '0';
        }
    }

    /**
     * @param data the specified byte array
     */
    @Override
    public void addBytes(
        @NotNull byte[] data
    ) {
        super.chain(
            data, 0, data.length
        );
    }

    /**
     * @param data the specified byte array
     */
    @Override
    public void addBytes(
        @NotNull byte[] data, int i, int l
    ) {
        super.chain(
            data, i, l
        );
    }

    /**
     * @param data the specified char array
     */
    @Override
    public void addChars(
        @NotNull char[] data
    ) {
        super.chain(
            data, 0, data.length
        );
    }

    @Override
    public void addChars(
        @NotNull char[] data, int i, int l
    ) {
        super.chain(
            data, i, l
        );
    }

    /**
     * @param data the specified {@link CharSequence}
     */
    @Override
    public void addChars(
        @NotNull CharSequence data
    ) {
        super.chain(
            data, 0, data.length()
        );
    }

    /**
     * @param data the specified {@link CharSequence}
     */
    @Override
    public void addChars(
        @NotNull CharSequence data, int i, int l
    ) {
        super.chain(
            data, i, l
        );
    }

    /**
     * @see Paper#addByte(byte)
     */
    @Override
    public void addData(
        byte b
    ) {
        addByte(b);
    }

    /**
     * @see Paper#addChar(char)
     */
    @Override
    public void addData(
        char c
    ) {
        addChar(c);
    }

    /**
     * @see Paper#addByte(byte)
     */
    @Override
    public void addData(
        @NotNull byte[] data
    ) {
        int l = data.length;
        grow(count + l);

        for (byte b : data) {
            addData(b);
        }
    }

    /**
     * @see Paper#addByte(byte)
     */
    @Override
    public void addData(
        @NotNull byte[] data, int i, int l
    ) {
        int k = i + l;
        grow(count + l);

        for (int o = i; o < k; o++) {
            addData(data[o]);
        }
    }

    /**
     * @see Paper#addData(CharSequence, int, int)
     */
    @Override
    public void addData(
        @NotNull CharSequence data
    ) {
        addData(
            data, 0, data.length()
        );
    }

    /**
     * @see Paper#addByte(byte)
     */
    @Override
    public void addData(
        @NotNull CharSequence data, int i, int l
    ) {
        grow(count + l);

        while (i < l) {
            // get char
            char c = data.charAt(i++);

            // U+0000 ~ U+007F
            if (c < 0x80) {
                addData(
                    (byte) c
                );
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
                if (i >= l) {
                    grow(count + 1);
                    hash = 0;
                    value[count++] = '?';
                    break;
                }

                char d = data.charAt(i++);
                if (d < 0xDC00 || d > 0xDFFF) {
                    grow(count + 1);
                    hash = 0;
                    value[count++] = '?';
                    continue;
                }

                grow(count + 4);
                hash = 0;
                int u = (c << 10) + d - 0x35F_DC00;
                value[count++] = (byte) ((u >> 18) | 0xF0);
                value[count++] = (byte) (((u >> 12) & 0x3F) | 0x80);
                value[count++] = (byte) (((u >> 6) & 0x3F) | 0x80);
                value[count++] = (byte) ((u & 0x3F) | 0x80);
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
    }

    /**
     * @see Paper#addText(byte[], int, int)
     */
    @Override
    public void addText(
        @NotNull byte[] data
    ) {
        addText(
            data, 0, data.length
        );
    }

    /**
     * @see Paper#addByte(byte)
     */
    @Override
    public void addText(
        @NotNull byte[] data, int i, int l
    ) {
        grow(count + l);

        byte b1, b2, b3, b4;
        while (i < l) {
            // get byte
            b1 = data[i++];

            // U+0000 ~ U+007F
            // 0xxxxxxx
            if (b1 >= 0) {
                addData(b1);
                continue;
            }

            // U+0080 ~ U+07FF
            // 110xxxxx 10xxxxxx
            if ((b1 >> 5) == -2) {
                if (i < l) {
                    b2 = data[i++];

                    // 110xxx xx : 10xx xxxx
                    grow(count + 6);
                    hash = 0;
                    value[count++] = '^';
                    value[count++] = 'u';
                    value[count++] = '0';
                    value[count++] = upper((b1 >> 4) & 0x01);
                    value[count++] = upper((b1 & 0x03) << 2 | (b2 >> 4) & 0x03);
                    value[count++] = upper(b2 & 0x0F);
                }
                continue;
            }

            // U+0800 ~ U+FFFF
            // 1110xxxx 10xxxxxx 10xxxxxx
            if ((b1 >> 4) == -2) {
                if (i + 1 < l) {
                    b2 = data[i++];
                    b3 = data[i++];

                    // xxxx : 10xxxx xx : 10xx xxxx
                    grow(count + 6);
                    hash = 0;
                    value[count++] = '^';
                    value[count++] = 'u';
                    value[count++] = upper(b1 & 0x0F);
                    value[count++] = upper((b2 >> 2) & 0x0F);
                    value[count++] = upper((b2 & 0x03) << 2 | (b3 >> 4) & 0x03);
                    value[count++] = upper(b3 & 0x0F);
                }
            }

            // U+10000 ~ U+10FFFF
            // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
            // 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
            if ((b1 >> 3) == -2) {
                if (i + 2 < l) {
                    b2 = data[i++];
                    b3 = data[i++];
                    b4 = data[i++];

                    // 11110x xx : 10xxxx xx : 10xx xx xx : 10xx xxxx
                    // 11110x xx : 10x100 00
                    // 1101 10xx xxxx xxxx 1101 11xx xxxx xxxx
                    grow(count + 12);
                    hash = 0;
                    value[count++] = '^';
                    value[count++] = 'u';
                    value[count++] = 'd';
                    value[count++] = upper(0x08 | (b1 & 0x03));
                    value[count++] = upper(((b2 - 0x10 >> 2)) & 0x0F);
                    value[count++] = upper(((b2 & 0x03) << 2) | ((b3 >> 4) & 0x03));
                    value[count++] = '^';
                    value[count++] = 'u';
                    value[count++] = 'd';
                    value[count++] = upper(0x0C | ((b3 >> 2) & 0x03));
                    value[count++] = upper(((b3 & 0x3) << 2) | ((b4 >> 4) & 0x03));
                    value[count++] = upper(b4 & 0x0F);
                }
            }
        }
    }

    /**
     * @see Paper#addText(CharSequence, int, int)
     */
    @Override
    public void addText(
        @NotNull CharSequence data
    ) {
        addText(
            data, 0, data.length()
        );
    }

    /**
     * @see Paper#addByte(byte)
     */
    @Override
    public void addText(
        @NotNull CharSequence data, int i, int l
    ) {
        grow(count + l);

        while (i < l) {
            // get char
            char c = data.charAt(i++);

            // U+0000 ~ U+007F
            if (c < 0x80) {
                addData(
                    (byte) c
                );
            } else {
                // xxxx xxxx : xxxx xxxx
                grow(count + 6);
                hash = 0;
                value[count++] = '^';
                value[count++] = 'u';
                value[count++] = upper((c >> 12) & 0x0F);
                value[count++] = upper((c >> 8) & 0x0F);
                value[count++] = upper((c >> 4) & 0x0F);
                value[count++] = upper(c & 0x0F);
            }
        }
    }
}
