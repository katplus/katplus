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
import plus.kat.utils.Config;

import java.util.concurrent.atomic.*;

import static plus.kat.stream.Binary.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kraity
 * @since 0.0.1
 */
public abstract class Paper extends Chain implements Flow {

    protected int depth;
    protected long flags;

    /**
     * default
     */
    public Paper() {
        super($Bucket.INS);
    }

    /**
     * @param flags the specified {@code flags}
     */
    public Paper(
        long flags
    ) {
        super($Bucket.INS);
        this.flags = flags;
        if ((flags & Flow.PRETTY) != 0) ++depth;
    }

    /**
     * @param bucket the specified {@link Bucket} to be used
     */
    public Paper(
        @Nullable Bucket bucket
    ) {
        super(bucket == null ? $Bucket.INS : bucket);
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
     * Returns {@code true} if, and only if, internal {@code byte[]} can be shared
     *
     * @see Chain#getValue()
     * @since 0.0.2
     */
    @Override
    public boolean isShared() {
        return bucket == null;
    }

    /**
     * Returns a {@link String} of this {@link Paper}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @NotNull
    @Override
    public String subSequence(
        int start, int end
    ) {
        return toString(
            start, end
        );
    }

    /**
     * @param b the specified byte value
     */
    @Override
    public void addByte(
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
     * @param c the specified char value
     */
    @Override
    public void addChar(
        char c
    ) {
        chain(c);
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
        if (bool) {
            grow(count + 4);
            hash = 0;
            value[count++] = 't';
            value[count++] = 'r';
            value[count++] = 'u';
        } else {
            grow(count + 5);
            hash = 0;
            value[count++] = 'f';
            value[count++] = 'a';
            value[count++] = 'l';
            value[count++] = 's';
        }
        value[count++] = 'e';
    }

    /**
     * @param data the specified byte array
     */
    @Override
    public void addBytes(
        @NotNull byte[] data
    ) {
        chain(data, 0, data.length);
    }

    /**
     * @param data the specified byte array
     */
    @Override
    public void addBytes(
        @NotNull byte[] data, int i, int l
    ) {
        chain(data, i, l);
    }

    /**
     * @param data the specified char array
     */
    @Override
    public void addChars(
        @NotNull char[] data
    ) {
        chain(data, 0, data.length);
    }

    @Override
    public void addChars(
        @NotNull char[] data, int i, int l
    ) {
        chain(data, i, l);
    }

    /**
     * @param data the specified {@link CharSequence}
     */
    @Override
    public void addChars(
        @NotNull CharSequence data
    ) {
        chain(data, 0, data.length());
    }

    /**
     * @param data the specified {@link CharSequence}
     */
    @Override
    public void addChars(
        @NotNull CharSequence data, int i, int l
    ) {
        chain(data, i, l);
    }

    /**
     * @see Paper#addByte(byte)
     */
    @Override
    public void addData(byte b) {
        if (record(b)) {
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
    }

    /**
     * @see Paper#addChar(char)
     */
    @Override
    public void addData(char c) {
        if (c >= 0x80) {
            chain(c);
        } else {
            byte b = (byte) c;
            if (record(b)) {
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
        }
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
            if (record(b)) {
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
            byte b = data[o];
            if (record(b)) {
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
                byte b = (byte) c;
                if (record(b)) {
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
                if (record(b1)) {
                    byte[] it = value;
                    if (count != it.length) {
                        hash = 0;
                        it[count++] = b1;
                    } else {
                        grow(count + 1);
                        hash = 0;
                        value[count++] = b1;
                    }
                }
                continue;
            }

            // U+0080 ~ U+07FF
            // 110xxxxx 10xxxxxx
            if ((b1 >> 5) == -2) {
                if (i < l) {
                    b2 = data[i++];

                    // 110xxx xx : 10xx xxxx
                    escape(count + 5);
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
                    escape(count + 5);
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
                    escape(count + 5);
                    value[count++] = 'u';
                    value[count++] = 'd';
                    value[count++] = upper(0x08 | (b1 & 0x03));
                    value[count++] = upper(((b2 - 0x10 >> 2)) & 0x0F);
                    value[count++] = upper(((b2 & 0x03) << 2) | ((b3 >> 4) & 0x03));
                    escape(count + 5);
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
                byte b = (byte) c;
                if (record(b)) {
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
            } else {
                // xxxx xxxx : xxxx xxxx
                escape(count + 5);
                value[count++] = 'u';
                value[count++] = upper((c >> 12) & 0x0F);
                value[count++] = upper((c >> 8) & 0x0F);
                value[count++] = upper((c >> 4) & 0x0F);
                value[count++] = upper(c & 0x0F);
            }
        }
    }

    /**
     * @see Paper#addData(byte)
     * @see Paper#addData(char)
     * @since 0.0.2
     */
    @Override
    public Appendable append(
        char c
    ) {
        if (isFlag(Flow.UNICODE)) {
            if (c < 0x80) {
                byte b = (byte) c;
                if (record(b)) chain(b);
            } else {
                // xxxx xxxx : xxxx xxxx
                escape(count + 5);
                value[count++] = 'u';
                value[count++] = upper((c >> 12) & 0x0F);
                value[count++] = upper((c >> 8) & 0x0F);
                value[count++] = upper((c >> 4) & 0x0F);
                value[count++] = upper(c & 0x0F);
            }
        } else {
            byte b = (byte) c;
            if (record(b)) chain(c);
        }
        return this;
    }

    /**
     * @see Flow#addData(CharSequence)
     * @see Flow#addText(CharSequence)
     * @since 0.0.2
     */
    @Override
    public Appendable append(
        CharSequence data
    ) {
        if (isFlag(Flow.UNICODE)) {
            addText(data);
        } else {
            addData(data);
        }
        return this;
    }

    /**
     * @see Flow#addData(CharSequence, int, int)
     * @see Flow#addText(CharSequence, int, int)
     * @since 0.0.2
     */
    @Override
    public Appendable append(
        CharSequence data, int start, int end
    ) {
        if (isFlag(Flow.UNICODE)) {
            addText(data, start, end - start);
        } else {
            addData(data, start, end - start);
        }
        return this;
    }

    /**
     * clean this {@link Paper}
     *
     * @since 0.0.2
     */
    public void clean() {
        hash = 0;
        count = 0;
    }

    /**
     * clear this {@link Paper}
     *
     * @since 0.0.2
     */
    public void clear() {
        this.clean();
        if (bucket == null) {
            value = EMPTY_BYTES;
        } else {
            value = bucket.revert(value);
        }
    }

    /**
     * close this {@link Paper}
     *
     * @since 0.0.2
     */
    @Override
    public void close() {
        this.clean();
        Bucket bt = bucket;
        if (bt != null) {
            if (value.length != 0) {
                bt.push(value);
            }
        }
        value = EMPTY_BYTES;
    }

    /**
     * Close this {@link Paper} and returns the {@code byte[]} of this {@link Paper} as a {@link String}
     *
     * @since 0.0.2
     */
    @NotNull
    public String closePaper() {
        String text;
        if (count == 0) {
            text = "";
        } else {
            text = new String(
                value, 0, count, UTF_8
            );
        }
        close();
        return text;
    }

    /**
     * @since 0.0.2
     */
    protected void escape(
        int min
    ) {
        byte[] it = value;
        if (min < it.length) {
            hash = 0;
            it[count++] = '\\';
        } else {
            grow(min + 1);
            hash = 0;
            value[count++] = '\\';
        }
    }

    /**
     * @since 0.0.2
     */
    protected boolean record(
        byte data
    ) {
        switch (data) {
            case '\r': {
                data = 'r';
                break;
            }
            case '\n': {
                data = 'n';
                break;
            }
            case '\t': {
                data = 't';
                break;
            }
            case '"':
            case '\\': {
                break;
            }
            default: {
                return true;
            }
        }

        grow(count + 2);
        hash = 0;
        value[count++] = '\\';
        value[count++] = data;
        return false;
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    private static class $Bucket extends AtomicReferenceArray<byte[]> implements Bucket {

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

        private static final $Bucket
            INS = new $Bucket();

        private $Bucket() {
            super(SIZE * GROUP);
        }

        @NotNull
        @Override
        public byte[] alloc(
            @NotNull byte[] it, int len, int min
        ) {
            byte[] data;
            int i = min / SCALE;

            Thread th = Thread.currentThread();
            int tr = th.hashCode() & 0xFFFFFF;

            if (i >= GROUP) {
                data = new byte[(i + 1) * SCALE - 1];
            } else {
                data = getAndSet(
                    i * GROUP + tr % SIZE, null
                );
                if (data == null ||
                    data.length < min) {
                    data = new byte[(i + 1) * SCALE - 1];
                }
            }

            if (it.length != 0) {
                System.arraycopy(
                    it, 0, data, 0, len
                );

                i = it.length / SCALE;
                if (i < GROUP) {
                    set(
                        i * GROUP + tr % SIZE, it
                    );
                }
            }

            return data;
        }

        @Override
        public void push(
            @NotNull byte[] it
        ) {
            int i = it.length / SCALE;
            if (i < GROUP) {
                Thread th = Thread.currentThread();
                int tr = th.hashCode() & 0xFFFFFF;

                set(
                    i * GROUP + tr % SIZE, it
                );
            }
        }

        @Nullable
        @Override
        public byte[] revert(
            @NotNull byte[] it
        ) {
            if (it.length != 0) {
                push(it);
            }
            return Chain.EMPTY_BYTES;
        }
    }
}
