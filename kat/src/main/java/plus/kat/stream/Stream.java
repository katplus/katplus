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
package plus.kat.stream;

import plus.kat.*;
import plus.kat.actor.*;

import java.io.IOException;

import static plus.kat.stream.Toolkit.*;
import static plus.kat.stream.Toolkit.Streams.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 * @since 0.0.5
 */
public class Stream extends Binary implements Flux {

    protected int depth;
    protected long flags;
    protected Bucket bucket;

    /**
     * Constructs a default stream
     */
    public Stream() {
        this(0);
    }

    /**
     * Constructs a stream with the specified flags
     *
     * @param flags the specified flags of {@link Flux}
     */
    public Stream(
        long flags
    ) {
        this(
            flags, STREAMS
        );
    }

    /**
     * Constructs a stream with the specified flags and bucket
     *
     * @param flags  the specified flags of {@link Flux}
     * @param bucket the specified bucket of {@link Flux}
     */
    public Stream(
        @NotNull long flags,
        @NotNull Bucket bucket
    ) {
        if (bucket != null) {
            this.flags = flags;
            this.depth = (int) (flags & 1);
            this.bucket = bucket;
        } else {
            throw new NullPointerException(
                "Received storage bucket is null"
            );
        }
    }

    /**
     * Returns the hashCode of this {@link Stream}
     */
    @Override
    public int hashCode() {
        int h = 0, l = size;
        if (l != 0) {
            int i = 0;
            byte[] v = value;
            while (i < l) {
                h = 31 * h + v[i++];
            }
        }
        return hash = h;
    }

    /**
     * Configure to use the feature
     *
     * @param flag the specified flag code
     */
    public void setFlag(
        @NotNull long flag
    ) {
        flags |= flag;
    }

    /**
     * Check if this uses the feature
     *
     * @param flag the specified flag code
     */
    public boolean isFlag(
        @NotNull long flag
    ) {
        return (flags & flag) == flag;
    }

    /**
     * Appends this byte to the current content
     *
     * @param bitty the specified byte value
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void emit(
        byte bitty
    ) throws IOException {
        byte[] it = value;
        escape:
        {
            switch (bitty) {
                case 0x5C:
                case 0x22: {
                    break;
                }
                case 0x08: {
                    bitty = 'b';
                    break;
                }
                case 0x09: {
                    bitty = 't';
                    break;
                }
                case 0x0A: {
                    bitty = 'n';
                    break;
                }
                case 0x0C: {
                    bitty = 'f';
                    break;
                }
                case 0x0D: {
                    bitty = 'r';
                    break;
                }
                case 0x20:
                case 0x21:
                case 0x23:
                case 0x24:
                case 0x25:
                case 0x26:
                case 0x27:
                case 0x28:
                case 0x29:
                case 0x2A:
                case 0x2B:
                case 0x2C:
                case 0x2D:
                case 0x2E:
                case 0x2F:
                case 0x30:
                case 0x31:
                case 0x32:
                case 0x33:
                case 0x34:
                case 0x35:
                case 0x36:
                case 0x37:
                case 0x38:
                case 0x39:
                case 0x3A:
                case 0x3B:
                case 0x3C:
                case 0x3D:
                case 0x3E:
                case 0x3F:
                case 0x40:
                case 0x41:
                case 0x42:
                case 0x43:
                case 0x44:
                case 0x45:
                case 0x46:
                case 0x47:
                case 0x48:
                case 0x49:
                case 0x4A:
                case 0x4B:
                case 0x4C:
                case 0x4D:
                case 0x4E:
                case 0x4F:
                case 0x50:
                case 0x51:
                case 0x52:
                case 0x53:
                case 0x54:
                case 0x55:
                case 0x56:
                case 0x57:
                case 0x58:
                case 0x59:
                case 0x5A:
                case 0x5B:
                case 0x5D:
                case 0x5E:
                case 0x5F:
                case 0x60:
                case 0x61:
                case 0x62:
                case 0x63:
                case 0x64:
                case 0x65:
                case 0x66:
                case 0x67:
                case 0x68:
                case 0x69:
                case 0x6A:
                case 0x6B:
                case 0x6C:
                case 0x6D:
                case 0x6E:
                case 0x6F:
                case 0x70:
                case 0x71:
                case 0x72:
                case 0x73:
                case 0x74:
                case 0x75:
                case 0x76:
                case 0x77:
                case 0x78:
                case 0x79:
                case 0x7A:
                case 0x7B:
                case 0x7C:
                case 0x7D:
                case 0x7E:
                default: {
                    break escape;
                }
                case 0x00:
                case 0x01:
                case 0x02:
                case 0x03:
                case 0x04:
                case 0x05:
                case 0x06:
                case 0x07:
                case 0x0B:
                case 0x0E:
                case 0x0F:
                case 0x10:
                case 0x11:
                case 0x12:
                case 0x13:
                case 0x14:
                case 0x15:
                case 0x16:
                case 0x17:
                case 0x18:
                case 0x19:
                case 0x1A:
                case 0x1B:
                case 0x1C:
                case 0x1D:
                case 0x1E:
                case 0x1F:
                case 0x7F: {
                    int min = size + 6;
                    if (min > it.length) {
                        value = it
                            = bucket.apply(
                            it, size, min
                        );
                    }
                    it[size++] = '\\';
                    it[size++] = 'u';
                    it[size++] = '0';
                    it[size++] = '0';
                    it[size++] = HEX_UPPER[(bitty >> 4) & 0x0F];
                    it[size++] = HEX_UPPER[bitty & 0x0F];
                    return;
                }
            }

            if (size == it.length) {
                value = it
                    = bucket.apply(
                    it, size, size + 2
                );
            }
            it[size++] = (byte) '\\';
        }

        if (size != it.length) {
            it[size++] = bitty;
        } else {
            grow(size + 1)[size++] = bitty;
        }
    }

    /**
     * Appends this char to the current content
     *
     * @param cutty the specified char value
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void emit(
        char cutty
    ) throws IOException {
        if (cutty < 0x80) {
            emit(
                (byte) cutty
            );
        } else {
            byte[] it = value;
            if ((flags & UNICODE) != 0) {
                int min = size + 6;
                if (min > it.length) {
                    value = it
                        = bucket.apply(
                        it, size, min
                    );
                }
                byte[] hex = HEX_UPPER;
                it[size++] = '\\';
                it[size++] = 'u';
                it[size++] = hex[cutty >> 12 & 0x0F];
                it[size++] = hex[cutty >> 8 & 0x0F];
                it[size++] = hex[cutty >> 4 & 0x0F];
                it[size++] = hex[cutty & 0x0F];
            } else {
                // U+0080 ~ U+07FF
                if (cutty < 0x800) {
                    int min = size + 2;
                    if (min > it.length) {
                        value = it
                            = bucket.apply(
                            it, size, min
                        );
                    }
                    it[size++] = (byte) (cutty >> 6 | 0xC0);
                    it[size++] = (byte) (cutty & 0x3F | 0x80);
                }

                // U+0800 ~ U+D7FF
                // U+E000 ~ U+FFFF
                else if (cutty < 0xD800 || 0xDFFF < cutty) {
                    int min = size + 3;
                    if (min > it.length) {
                        value = it
                            = bucket.apply(
                            it, size, min
                        );
                    }
                    it[size++] = (byte) (cutty >> 12 | 0xE0);
                    it[size++] = (byte) (cutty >> 6 & 0x3F | 0x80);
                    it[size++] = (byte) (cutty & 0x3F | 0x80);
                }

                // U+10000 ~ U+10FFFF
                else {
                    // crippled surrogate pair
                    if (size != it.length) {
                        it[size++] = '?';
                    } else {
                        grow(size + 1)[size++] = '?';
                    }
                }
            }
        }
    }

    /**
     * Appends the literal representation
     * of the int value to the current content
     */
    @Override
    public void emit(int val) {
        int iv;
        byte[] it = value;
        if (val < 0) {
            if (-9 <= val) {
                iv = size + 2;
            } else if (-99 <= val) {
                iv = size + 3;
            } else if (-999 <= val) {
                iv = size + 4;
            } else if (-9999 <= val) {
                iv = size + 5;
            } else if (-99999 <= val) {
                iv = size + 6;
            } else if (-999999 <= val) {
                iv = size + 7;
            } else if (-9999999 <= val) {
                iv = size + 8;
            } else if (-99999999 <= val) {
                iv = size + 9;
            } else if (-999999999 <= val) {
                iv = size + 10;
            } else {
                iv = size + 11;
            }

            if (iv > it.length) {
                value = it
                    = bucket.apply(
                    it, size, iv
                );
            }
            it[size] = '-';
            size = iv;
            do {
                it[--iv] = (byte) (
                    0x3A + (val % 10)
                );
            } while ((val /= 10) != 0);
        } else {
            if (val <= 9) {
                iv = size + 1;
            } else if (val <= 99) {
                iv = size + 2;
            } else if (val <= 999) {
                iv = size + 3;
            } else if (val <= 9999) {
                iv = size + 4;
            } else if (val <= 99999) {
                iv = size + 5;
            } else if (val <= 999999) {
                iv = size + 6;
            } else if (val <= 9999999) {
                iv = size + 7;
            } else if (val <= 99999999) {
                iv = size + 8;
            } else if (val <= 999999999) {
                iv = size + 9;
            } else {
                iv = size + 10;
            }

            if (iv > it.length) {
                value = it
                    = bucket.apply(
                    it, size, iv
                );
            }
            size = iv;
            do {
                it[--iv] = (byte) (
                    0x30 + (val % 10)
                );
            } while ((val /= 10) != 0);
        }
    }

    /**
     * Appends the literal representation
     * of the long value to the current content
     */
    @Override
    public void emit(long val) {
        int iv;
        byte[] it = value;
        if (val < 0) {
            if (-9L <= val) {
                iv = size + 2;
            } else if (-99L <= val) {
                iv = size + 3;
            } else if (-999L <= val) {
                iv = size + 4;
            } else if (-9999L <= val) {
                iv = size + 5;
            } else if (-99999L <= val) {
                iv = size + 6;
            } else if (-999999L <= val) {
                iv = size + 7;
            } else if (-9999999L <= val) {
                iv = size + 8;
            } else if (-99999999L <= val) {
                iv = size + 9;
            } else if (-999999999L <= val) {
                iv = size + 10;
            } else if (-9999999999L <= val) {
                iv = size + 11;
            } else if (-99999999999L <= val) {
                iv = size + 12;
            } else if (-999999999999L <= val) {
                iv = size + 13;
            } else if (-9999999999999L <= val) {
                iv = size + 14;
            } else if (-99999999999999L <= val) {
                iv = size + 15;
            } else if (-999999999999999L <= val) {
                iv = size + 16;
            } else if (-9999999999999999L <= val) {
                iv = size + 17;
            } else if (-99999999999999999L <= val) {
                iv = size + 18;
            } else if (-999999999999999999L <= val) {
                iv = size + 19;
            } else {
                iv = size + 20;
            }

            if (iv > it.length) {
                value = it
                    = bucket.apply(
                    it, size, iv
                );
            }
            it[size] = '-';
            size = iv;
            do {
                it[--iv] = (byte) (
                    0x3AL + (val % 10L)
                );
            } while ((val /= 10L) != 0L);
        } else {
            if (val <= 9L) {
                iv = size + 1;
            } else if (val <= 99L) {
                iv = size + 2;
            } else if (val <= 999L) {
                iv = size + 3;
            } else if (val <= 9999L) {
                iv = size + 4;
            } else if (val <= 99999L) {
                iv = size + 5;
            } else if (val <= 999999L) {
                iv = size + 6;
            } else if (val <= 9999999L) {
                iv = size + 7;
            } else if (val <= 99999999L) {
                iv = size + 8;
            } else if (val <= 999999999L) {
                iv = size + 9;
            } else if (val <= 9999999999L) {
                iv = size + 10;
            } else if (val <= 99999999999L) {
                iv = size + 11;
            } else if (val <= 999999999999L) {
                iv = size + 12;
            } else if (val <= 9999999999999L) {
                iv = size + 13;
            } else if (val <= 99999999999999L) {
                iv = size + 14;
            } else if (val <= 999999999999999L) {
                iv = size + 15;
            } else if (val <= 9999999999999999L) {
                iv = size + 16;
            } else if (val <= 99999999999999999L) {
                iv = size + 17;
            } else if (val <= 999999999999999999L) {
                iv = size + 18;
            } else {
                iv = size + 19;
            }

            if (iv > it.length) {
                value = it
                    = bucket.apply(
                    it, size, iv
                );
            }
            size = iv;
            do {
                it[--iv] = (byte) (
                    0x30L + (val % 10L)
                );
            } while ((val /= 10L) != 0L);
        }
    }

    /**
     * Appends the literal representation
     * of the short value to the current content
     */
    @Override
    public void emit(short val)
        throws IOException {
        emit(val & 0xFFFF);
    }

    /**
     * Appends the literal representation
     * of the float value to the current content
     */
    @SuppressWarnings("deprecation")
    public void emit(float val) {
        String data = Float.toString(val);
        int now = size,
            add = data.length(),
            min = now + add;
        byte[] it = value;
        if (min > it.length) {
            value = it
                = bucket.apply(
                it, size, min
            );
        }
        size = min;
        data.getBytes(0, add, it, now);
    }

    /**
     * Appends the literal representation
     * of the double value to the current content
     *
     * @param val the specified number to be appended
     */
    @SuppressWarnings("deprecation")
    public void emit(double val) {
        String data = Double.toString(val);
        int now = size,
            add = data.length(),
            min = now + add;
        byte[] it = value;
        if (min > it.length) {
            value = it
                = bucket.apply(
                it, size, min
            );
        }
        size = min;
        data.getBytes(0, add, it, now);
    }

    /**
     * Appends the literal representation
     * of the boolean value to the current content
     */
    @Override
    public void emit(boolean val) {
        byte[] it = value;
        if (val) {
            int min = size + 4;
            if (min > it.length) {
                value = it
                    = bucket.apply(
                    it, size, min
                );
            }
            it[size++] = 't';
            it[size++] = 'r';
            it[size++] = 'u';
        } else {
            int min = size + 5;
            if (min > it.length) {
                value = it
                    = bucket.apply(
                    it, size, min
                );
            }
            it[size++] = 'f';
            it[size++] = 'a';
            it[size++] = 'l';
            it[size++] = 's';
        }
        it[size++] = 'e';
    }

    /**
     * Appends this byte array to the current content
     */
    @Override
    public void emit(
        @NotNull byte[] bin
    ) throws IOException {
        for (byte b : bin) emit(b);
    }

    /**
     * Appends this byte array where the
     * specified offset and length to the current content
     */
    @Override
    public void emit(
        @NotNull byte[] bin, int i, int l
    ) throws IOException {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= bin.length) {
            while (i < k) {
                emit(bin[i++]);
            }
        } else {
            throw new IOException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + bin.length
            );
        }
    }

    /**
     * Appends this char array to the current content
     */
    @Override
    public void emit(
        @NotNull char[] val
    ) throws IOException {
        emit(val, 0, val.length);
    }

    /**
     * Appends this char array where the
     * specified offset and length to the current content
     */
    @Override
    public void emit(
        @NotNull char[] val, int i, int l
    ) throws IOException {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= val.length) {
            if (l != 0) {
                if ((flags & UNICODE) != 0) {
                    byte[] hex = HEX_UPPER;
                    do {
                        char ch = val[i++];
                        if (ch < 0x80) {
                            emit((byte) ch);
                        } else {
                            byte[] it = value;
                            int min = size + 6;
                            if (min > it.length) {
                                value = it
                                    = bucket.apply(
                                    it, size, min
                                );
                            }
                            it[size++] = '\\';
                            it[size++] = 'u';
                            it[size++] = hex[ch >> 12 & 0x0F];
                            it[size++] = hex[ch >> 8 & 0x0F];
                            it[size++] = hex[ch >> 4 & 0x0F];
                            it[size++] = hex[ch & 0x0F];
                        }
                    } while (i < k);
                } else {
                    do {
                        char c1 = val[i++];

                        // U+0000 ~ U+007F
                        if (c1 < 0x80) {
                            emit((byte) c1);
                        }

                        // U+0080 ~ U+07FF
                        else if (c1 < 0x800) {
                            byte[] it = value;
                            int min = size + 2;
                            if (min > it.length) {
                                value = it
                                    = bucket.apply(
                                    it, size, min
                                );
                            }
                            it[size++] = (byte) (c1 >> 6 | 0xC0);
                            it[size++] = (byte) (c1 & 0x3F | 0x80);
                        }

                        // U+0800 ~ U+D7FF
                        // U+E000 ~ U+FFFF
                        else if (c1 < 0xD800 || 0xDFFF < c1) {
                            byte[] it = value;
                            int min = size + 3;
                            if (min > it.length) {
                                value = it
                                    = bucket.apply(
                                    it, size, min
                                );
                            }
                            it[size++] = (byte) (c1 >> 12 | 0xE0);
                            it[size++] = (byte) (c1 >> 6 & 0x3F | 0x80);
                            it[size++] = (byte) (c1 & 0x3F | 0x80);
                        }

                        // U+10000 ~ U+10FFFF
                        else {
                            if (c1 > 0xDBFF) {
                                emit((byte) '?');
                                continue;
                            }

                            if (k == i) {
                                emit((byte) '?');
                                break;
                            }

                            char c2 = val[i];
                            if (c2 < 0xDC00 ||
                                c2 > 0xDFFF) {
                                emit((byte) '?');
                                continue;
                            }

                            int hi = c1 - 0xD7C0;
                            int lo = c2 - 0xDC00;

                            byte[] it = value;
                            int min = size + 4;
                            if (min > it.length) {
                                value = it
                                    = bucket.apply(
                                    it, size, min
                                );
                            }
                            i++; // 2 chars
                            it[size++] = (byte) (hi >> 8 | 0xF0);
                            it[size++] = (byte) (hi >> 2 & 0x3F | 0x80);
                            it[size++] = (byte) (lo >> 6 | hi << 4 & 0x30 | 0x80);
                            it[size++] = (byte) (lo & 0x3F | 0x80);
                        }
                    } while (i < k);
                }
            }
        } else {
            throw new IOException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + val.length
            );
        }
    }

    /**
     * Appends this binary to the current content
     */
    @Override
    public void emit(
        @NotNull Binary bin
    ) throws IOException {
        int l = bin.size;
        if (l != 0) {
            int i = 0;
            byte[] it = bin.value;
            while (i < l) {
                emit(it[i++]);
            }
        }
    }

    /**
     * Appends this binary where the
     * specified offset and length to the current content
     */
    @Override
    public void emit(
        @NotNull Binary bin, int i, int l
    ) throws IOException {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= bin.size) {
            byte[] it = bin.value;
            while (i < k) {
                emit(it[i++]);
            }
        } else {
            throw new IOException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + bin.size
            );
        }
    }

    /**
     * Appends this string to the current content
     */
    @Override
    public void emit(
        @NotNull String val
    ) throws IOException {
        emit(val, 0, val.length());
    }

    /**
     * Appends this string where the
     * specified offset and length to the current content
     */
    @Override
    public void emit(
        @NotNull String val, int i, int l
    ) throws IOException {
        int k = i + l;
        if (0 <= i && 0 <= l && k <= val.length()) {
            if (l != 0) {
                if ((flags & UNICODE) != 0) {
                    byte[] hex = HEX_UPPER;
                    do {
                        char ch = val.charAt(i++);
                        if (ch < 0x80) {
                            emit((byte) ch);
                        } else {
                            byte[] it = value;
                            int min = size + 6;
                            if (min > it.length) {
                                value = it
                                    = bucket.apply(
                                    it, size, min
                                );
                            }
                            it[size++] = '\\';
                            it[size++] = 'u';
                            it[size++] = hex[ch >> 12 & 0x0F];
                            it[size++] = hex[ch >> 8 & 0x0F];
                            it[size++] = hex[ch >> 4 & 0x0F];
                            it[size++] = hex[ch & 0x0F];
                        }
                    } while (i < k);
                } else {
                    do {
                        char c1 = val.charAt(i++);

                        // U+0000 ~ U+007F
                        if (c1 < 0x80) {
                            emit((byte) c1);
                        }

                        // U+0080 ~ U+07FF
                        else if (c1 < 0x800) {
                            byte[] it = value;
                            int min = size + 2;
                            if (min > it.length) {
                                value = it
                                    = bucket.apply(
                                    it, size, min
                                );
                            }
                            it[size++] = (byte) (c1 >> 6 | 0xC0);
                            it[size++] = (byte) (c1 & 0x3F | 0x80);
                        }

                        // U+0800 ~ U+D7FF
                        // U+E000 ~ U+FFFF
                        else if (c1 < 0xD800 || 0xDFFF < c1) {
                            byte[] it = value;
                            int min = size + 3;
                            if (min > it.length) {
                                value = it
                                    = bucket.apply(
                                    it, size, min
                                );
                            }
                            it[size++] = (byte) (c1 >> 12 | 0xE0);
                            it[size++] = (byte) (c1 >> 6 & 0x3F | 0x80);
                            it[size++] = (byte) (c1 & 0x3F | 0x80);
                        }

                        // U+10000 ~ U+10FFFF
                        else {
                            if (c1 > 0xDBFF) {
                                emit((byte) '?');
                                continue;
                            }

                            if (k == i) {
                                emit((byte) '?');
                                break;
                            }

                            char c2 = val.charAt(i);
                            if (c2 < 0xDC00 ||
                                c2 > 0xDFFF) {
                                emit((byte) '?');
                                continue;
                            }

                            int hi = c1 - 0xD7C0;
                            int lo = c2 - 0xDC00;

                            byte[] it = value;
                            int min = size + 4;
                            if (min > it.length) {
                                value = it
                                    = bucket.apply(
                                    it, size, min
                                );
                            }
                            i++; // 2 chars
                            it[size++] = (byte) (hi >> 8 | 0xF0);
                            it[size++] = (byte) (hi >> 2 & 0x3F | 0x80);
                            it[size++] = (byte) (lo >> 6 | hi << 4 & 0x30 | 0x80);
                            it[size++] = (byte) (lo & 0x3F | 0x80);
                        }
                    } while (i < k);
                }
            }
        } else {
            throw new IOException(
                "Out of bounds, i:" + i + " l:" + l + " length:" + val.length()
            );
        }
    }

    /**
     * Appends this byte value to this {@link Stream}
     */
    public void join(
        @NotNull byte in
    ) {
        byte[] it = value;
        if (size != it.length) {
            it[size++] = in;
        } else {
            grow(size + 1)[size++] = in;
        }
    }

    /**
     * Appends this char value to this {@link Stream}
     */
    public void join(
        @NotNull char in
    ) {
        byte[] it = value;
        // U+0000 ~ U+007F
        if (in < 0x80) {
            if (size != it.length) {
                it[size++] = (byte) in;
            } else {
                grow(size + 1)[size++] = (byte) in;
            }
        }

        // U+0080 ~ U+07FF
        else if (in < 0x800) {
            int min = size + 2;
            if (min > it.length) {
                value = it
                    = bucket.apply(
                    it, size, min
                );
            }
            it[size++] = (byte) (in >> 6 | 0xC0);
            it[size++] = (byte) (in & 0x3F | 0x80);
        }

        // U+0800 ~ U+D7FF
        // U+E000 ~ U+FFFF
        else if (in < 0xD800 || 0xDFFF < in) {
            int min = size + 3;
            if (min > it.length) {
                value = it
                    = bucket.apply(
                    it, size, min
                );
            }
            it[size++] = (byte) (in >> 12 | 0xE0);
            it[size++] = (byte) (in >> 6 & 0x3F | 0x80);
            it[size++] = (byte) (in & 0x3F | 0x80);
        }

        // U+10000 ~ U+10FFFF
        else {
            // crippled surrogate pair
            if (size != it.length) {
                it[size++] = '?';
            } else {
                grow(size + 1)[size++] = '?';
            }
        }
    }

    /**
     * Returns a copy of the
     * value of this {@link Stream}
     */
    public byte[] toBinary() {
        if (size != 0) {
            byte[] v =
                new byte[size];
            System.arraycopy(
                value, 0, v, 0, size
            );
            return v;
        }
        return EMPTY_BYTES;
    }

    /**
     * Returns the value of this
     * {@link Stream} as a {@link String}
     */
    public String toString() {
        return size == 0 ? "" : (
            new String(
                value, 0, size, UTF_8
            )
        );
    }

    /**
     * Closes this stream and releases
     * the resources associated with it
     */
    public void close() {
        byte[] it = value;
        if (it.length != 0) {
            size = 0;
            Bucket bt = bucket;
            if (bt != null) {
                value = bt.store(it);
            }
        }
    }

    /**
     * Requires that the length of {@link #value} be at least
     * equal to the specified minimum length. If the current length is less
     * than the argument, then a new array is allocated with greater capacity
     *
     * @param min the specified minimum length
     */
    protected byte[] grow(int min) {
        byte[] val = value;
        return min <= val.length ? val : (
            value = bucket.apply(val, size, min)
        );
    }
}
