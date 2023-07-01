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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.*;
import plus.kat.spare.*;

import plus.kat.utils.Config;
import plus.kat.utils.KatBuffer;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.lang.reflect.*;

import static plus.kat.Algo.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class Toolkit {

    private Toolkit() {
        throw new IllegalStateException();
    }

    /**
     * Unsafe, may be deleted later
     */
    public static final byte[]
        EMPTY_BYTES = {};

    /**
     * empty char array
     */
    public static final char[]
        EMPTY_CHARS = {};

    /**
     * Unsafe, may be deleted later
     */
    public static final byte[] HEX_LOWER = {
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f', 'g', 'h',
        'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    };
    /**
     * Unsafe, may be deleted later
     */
    public static final byte[] HEX_UPPER = {
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F', 'G', 'H',
        'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    /**
     * Unsafe, may be deleted later
     */
    public static final byte[] RFC4648_ENCODE = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };
    /**
     * Unsafe, may be deleted later
     */
    static final byte[] RFC4648_DECODE = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, // 20-2f + /
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, // 30-3f 0-9
        -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,           // 40-4f A-O
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, // 50-5f P-Z
        -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 60-6f a-o
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51                      // 70-7a p-z
    };
    /**
     * Unsafe, may be deleted later
     */
    static final byte[] RFC4648_SAFE_ENCODE = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };
    /**
     * Unsafe, may be deleted later
     */
    static final byte[] RFC4648_SAFE_DECODE = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, // 20-2f -
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, // 30-3f 0-9
        -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,           // 40-4f A-O
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, // 50-5f P-Z _
        -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 60-6f a-o
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51                      // 70-7a p-z
    };

    /**
     * Unsafe, may be deleted later
     */
    public static byte[] valueOf(
        @NotNull Binary binary
    ) {
        return binary.value;
    }

    /**
     * Unsafe, may be deleted later
     */
    public static Bucket isolate(
        @NotNull Stream stream
    ) {
        stream.isolate = true;
        return stream.bucket;
    }

    /**
     * Unsafe, may be deleted later
     */
    public static boolean isIsolate(
        @NotNull Stream stream
    ) {
        return stream.isolate;
    }

    /**
     * Unsafe, may be deleted later
     */
    public static void writeTo(
        @NotNull Binary binary,
        @NotNull OutputStream stream
    ) throws IOException {
        stream.write(
            binary.value, 0, binary.size
        );
        stream.flush();
    }

    /**
     * Unsafe, may be deleted later
     */
    public static void write(
        @NotNull Binary data,
        @NotNull Stream heap
    ) {
        int l = data.size;
        int size = heap.size;

        byte[] v = data.value;
        byte[] it = heap.value;

        for (int i = 0; i < l; i++) {
            byte elem = v[i];
            switch (elem) {
                case 0x22:
                case 0x23:
                case 0x3A:
                case 0x40:
                case 0x5B:
                case 0x5D:
                case 0x7B:
                case 0x7D: {
                    break;
                }
                case 0x08: {
                    elem = 'b';
                    break;
                }
                case 0x09: {
                    elem = 't';
                    break;
                }
                case 0x0A: {
                    elem = 'n';
                    break;
                }
                case 0x0C: {
                    elem = 'f';
                    break;
                }
                case 0x0D: {
                    elem = 'r';
                    break;
                }
                case 0x20: {
                    elem = 's';
                    break;
                }
                case 0x21:
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
                case 0x3B:
                case 0x3C:
                case 0x3D:
                case 0x3E:
                case 0x3F:
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
                case 0x5C:
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
                case 0x7C:
                case 0x7E:
                default: {
                    if (size == it.length) {
                        heap.value = it =
                            heap.bucket.apply(
                                it, size, size + 1
                            );
                    }
                    it[size++] = elem;
                    continue;
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
                        heap.value = it =
                            heap.bucket.apply(
                                it, size, min
                            );
                    }
                    it[size++] = '\\';
                    it[size++] = 'u';
                    it[size++] = '0';
                    it[size++] = '0';
                    it[size++] = HEX_UPPER[(elem >> 4) & 0x0F];
                    it[size++] = HEX_UPPER[elem & 0x0F];
                    continue;
                }
            }

            int min = size + 2;
            if (min > it.length) {
                heap.value = it =
                    heap.bucket.apply(
                        it, size, min
                    );
            }
            it[size++] = '\\';
            it[size++] = elem;
        }
        heap.size = size;
    }

    /**
     * Unsafe, may be deleted later
     */
    public static void write(
        @NotNull String data,
        @NotNull Stream heap
    ) throws IOException {
        int size = heap.size;
        byte[] it = heap.value;

        int l = data.length();
        for (int i = 0; i < l; i++) {
            char elem = data.charAt(i);
            switch (elem) {
                case 0x22:
                case 0x23:
                case 0x3A:
                case 0x40:
                case 0x5B:
                case 0x5D:
                case 0x7B:
                case 0x7D: {
                    break;
                }
                case 0x08: {
                    elem = 'b';
                    break;
                }
                case 0x09: {
                    elem = 't';
                    break;
                }
                case 0x0A: {
                    elem = 'n';
                    break;
                }
                case 0x0C: {
                    elem = 'f';
                    break;
                }
                case 0x0D: {
                    elem = 'r';
                    break;
                }
                case 0x20: {
                    elem = 's';
                    break;
                }
                case 0x21:
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
                case 0x3B:
                case 0x3C:
                case 0x3D:
                case 0x3E:
                case 0x3F:
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
                case 0x5C:
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
                case 0x7C:
                case 0x7E: {
                    if (size == it.length) {
                        heap.value = it =
                            heap.bucket.apply(
                                it, size, size + 1
                            );
                    }
                    it[size++] = (byte) elem;
                    continue;
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
                        heap.value = it =
                            heap.bucket.apply(
                                it, size, min
                            );
                    }
                    it[size++] = '\\';
                    it[size++] = 'u';
                    it[size++] = '0';
                    it[size++] = '0';
                    it[size++] = HEX_UPPER[(elem >> 4) & 0x0F];
                    it[size++] = HEX_UPPER[elem & 0x0F];
                    continue;
                }
                default: {
                    heap.size = size;
                    heap.emit(elem);
                    it = heap.value;
                    size = heap.size;
                    continue;
                }
            }

            int min = size + 2;
            if (min > it.length) {
                heap.value = it =
                    heap.bucket.apply(
                        it, size, min
                    );
            }
            it[size++] = '\\';
            it[size++] = (byte) elem;
        }
        heap.size = size;
    }

    /**
     * Unsafe, may be deleted later
     */
    public static boolean set(
        Chan chan, Object alias, Object value
    ) throws IOException {
        if (value instanceof Entity) {
            return chan.set(
                alias, (Entity) value
            );
        }

        if (value instanceof Iterable ||
            value instanceof Iterator) {
            return chan.set(
                alias, ListSpare.INSTANCE, value
            );
        }

        if (value instanceof Throwable) {
            Throwable o = (Throwable) value;
            return chan.set(
                alias, StringSpare.INSTANCE, o.getMessage()
            );
        }

        if (value instanceof CharSequence) {
            return chan.set(
                alias, StringSpare.INSTANCE, value.toString()
            );
        }

        // Subclass of Date
        // java.sql.Date
        // java.sql.Time
        // java.sql.Timestamp
        if (value instanceof Date) {
            return chan.set(
                alias, DateSpare.INSTANCE, value
            );
        }

        // Subclass of File
        if (value instanceof File) {
            return chan.set(
                alias, FileSpare.INSTANCE, value
            );
        }

        // Subclass of Number
        if (value instanceof Number) {
            return chan.set(
                alias, NumberSpare.INSTANCE, value
            );
        }

        // Subclass of Charset
        if (value instanceof Charset) {
            Charset o = (Charset) value;
            return chan.set(
                alias, StringSpare.INSTANCE, o.name()
            );
        }

        // Subclass of TimeZone
        // java.util.SimpleTimeZone
        // sun.util.calendar.ZoneInfo
        if (value instanceof TimeZone) {
            return chan.set(
                alias, TimeZoneSpare.INSTANCE, value
            );
        }

        switch (value.getClass().getName()) {
            case "java.util.Optional": {
                do {
                    value = ((Optional<?>) value)
                        .orElse(null);
                } while (value instanceof Optional);
                return chan.set(
                    alias, null, value
                );
            }
            case "java.util.OptionalInt": {
                OptionalInt o = (OptionalInt) value;
                return chan.set(
                    alias, IntSpare.INSTANCE, o.orElse(0)
                );
            }
            case "java.util.OptionalLong": {
                OptionalLong o = (OptionalLong) value;
                return chan.set(
                    alias, LongSpare.INSTANCE, o.orElse(0L)
                );
            }
            case "java.util.OptionalDouble": {
                OptionalDouble o = (OptionalDouble) value;
                return chan.set(
                    alias, DoubleSpare.INSTANCE, o.orElse(0D)
                );
            }
        }

        throw new IOException(
            "No available coder for `"
                + value.getClass() + "` was found"
        );
    }

    /**
     * Unsafe, may be deleted later
     */
    @Nullable
    public static Algo algoOf(
        @NotNull Binary text
    ) {
        int e = text.size;
        if (e < 2) {
            return null;
        }

        byte c1, c2;
        byte[] v = text.value;

        int i = 0;
        do {
            c1 = v[i];
        } while (
            c1 <= 0x20 && ++i < e
        );

        do {
            c2 = v[e - 1];
        } while (
            c2 <= 0x20 && --e > i
        );

        if (c2 != '}') {
            // []
            if (c2 == ']') {
                if (c1 == '[') {
                    return JSON;
                } else {
                    return null;
                }
            }

            // <>
            if (c2 == '>') {
                if (c1 == '<' && e > 6) {
                    return DOC;
                } else {
                    return null;
                }
            }
        } else {
            // @{}
            if (c1 != '{') {
                return KAT;
            }

            int n = i + 1;
            while (true) {
                c1 = v[n++];
                switch (c1) {
                    case '"':
                    case '\'':
                    case '\\': {
                        return JSON;
                    }
                    default: {
                        if (e <= n || c1 > 0x20)
                            return KAT;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Unsafe, may be deleted later
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> Class<T> classOf(
        @Nullable Type type
    ) {
        if (type == null) {
            return null;
        }

        if (type instanceof Class) {
            return (Class<T>) type;
        }

        if (type instanceof ParameterizedType) {
            return classOf(
                ((ParameterizedType) type).getRawType()
            );
        }

        if (type instanceof TypeVariable) {
            return null;
        }

        if (type instanceof WildcardType) {
            return classOf(
                ((WildcardType) type).getUpperBounds()[0]
            );
        }

        if (type instanceof GenericArrayType) {
            GenericArrayType g = (GenericArrayType) type;
            Class<?> cls = classOf(
                g.getGenericComponentType()
            );
            if (cls != null) {
                if (cls == Object.class) {
                    cls = Object[].class;
                } else if (cls == String.class) {
                    cls = String[].class;
                } else if (cls.isPrimitive()) {
                    if (cls == int.class) {
                        cls = int[].class;
                    } else if (cls == long.class) {
                        cls = long[].class;
                    } else if (cls == float.class) {
                        cls = float[].class;
                    } else if (cls == double.class) {
                        cls = double[].class;
                    } else if (cls == byte.class) {
                        cls = byte[].class;
                    } else if (cls == short.class) {
                        cls = short[].class;
                    } else if (cls == char.class) {
                        cls = char[].class;
                    } else if (cls == boolean.class) {
                        cls = boolean[].class;
                    } else {
                        return null;
                    }
                } else {
                    cls = Array.newInstance(cls, 0).getClass();
                }
                return (Class<T>) cls;
            }
        }

        return null;
    }

    /**
     * Unsafe, may be deleted later
     */
    @SuppressWarnings("deprecation")
    public static String print(
        byte[] it, int index, int limit
    ) {
        int i = 0, n = 22;
        if (limit < 1) {
            limit = index;
        }

        if (index > 0) {
            i = index - 1;
            for (int m = 10; n < 31; n++) {
                if (i < m) {
                    break;
                } else {
                    m = 10 * m;
                }
            }
        }

        // `6` with index 36 in `AB`
        final int e = i;
        final int m = n + limit;

        byte[] v = new byte[m];
        System.arraycopy(
            it, 0, v, --n, limit
        );

        v[--n] = '`';
        v[--n] = ' ';
        v[--n] = 'n';
        v[--n] = 'i';
        v[--n] = ' ';

        do {
            v[--n] = (byte) (
                0x30 + (i % 10)
            );
        } while ((i /= 10) != 0);

        v[--n] = ' ';
        v[--n] = 'x';
        v[--n] = 'e';
        v[--n] = 'd';
        v[--n] = 'n';
        v[--n] = 'i';
        v[--n] = ' ';
        v[--n] = 'h';
        v[--n] = 't';
        v[--n] = 'i';
        v[--n] = 'w';
        v[--n] = ' ';
        v[--n] = '`';
        v[--n] = it[e];
        v[--n] = '`';
        v[m - 1] = '`';
        return new String(v, 0, 0, m);
    }

    /**
     * Unsafe, may be deleted later
     */
    @SuppressWarnings("unchecked")
    public static class Streams implements Bucket {

        private final int mask, valve, scale;
        private final KatBuffer<byte[]>[] table;

        public static final Streams
            STREAMS = new Streams();

        private Streams() {
            int l = Config.get(
                "kat.stream.length", 8192
            );
            if ((l & (valve = l - 1)) == 0) {
                scale = l;
            } else {
                throw new IllegalArgumentException(
                    "Received " + l + " is not a power of two"
                );
            }

            int g = Config.get(
                "kat.stream.group", 8
            );
            if ((g & (mask = g - 1)) == 0) {
                table = new KatBuffer[g];
                do {
                    table[--g] = new KatBuffer<>();
                } while (g > 0);
            } else {
                throw new IllegalArgumentException(
                    "Received " + g + " is not a power of two"
                );
            }
        }

        @NotNull
        public byte[] apply(int min) {
            if (min * 8 < valve) {
                if (min <= 0) {
                    return EMPTY_BYTES;
                } else {
                    return new byte[min];
                }
            }

            int i = min / scale;
            if (i < 2) {
                int v = mask & Thread
                    .currentThread().hashCode();
                byte[] it = table[i ^ v].borrow();
                if (it != null) {
                    return it;
                }
            }
            return new byte[i * scale + valve];
        }

        @Override
        public byte[] store(
            byte[] flow
        ) {
            if (flow != null) {
                int i = flow.length;
                if (i % scale == valve && (i /= scale) < 2) {
                    int v = mask & Thread
                        .currentThread().hashCode();
                    if (table[i ^ v].resume(flow)) {
                        return EMPTY_BYTES;
                    }
                }
            }
            return flow;
        }

        @Override
        public byte[] apply(
            byte[] flow, int size, int capacity
        ) {
            int i = capacity / scale;
            int v = mask & Thread
                .currentThread().hashCode();

            byte[] data;
            if (i > 1) {
                data = new byte[i * scale + valve];
            } else {
                KatBuffer<byte[]> node = table[i ^ v];
                data = node.getAndSet(null);
                if (data == null) {
                    data = node.acquire();
                    if (data == null) {
                        data = new byte[i * scale + valve];
                    }
                }
            }

            if ((i = flow.length) != 0) {
                System.arraycopy(
                    flow, 0, data, 0, size
                );

                if (i % scale == valve && (i /= scale) < 2) {
                    table[i ^ v].resume(flow);
                }
            }

            return data;
        }
    }
}
