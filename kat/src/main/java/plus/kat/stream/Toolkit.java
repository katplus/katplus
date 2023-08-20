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
import static java.nio.charset.StandardCharsets.*;

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
    public static byte stateOf(
        @NotNull Binary binary
    ) {
        return binary.state;
    }

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
    public static boolean isNull(
        @NotNull Binary bin
    ) {
        if (bin.size == 4 &&
            bin.state == 0) {
            byte[] v = bin.value;
            return v[0] == 'n'
                && v[1] == 'u'
                && v[2] == 'l'
                && v[3] == 'l';
        }

        return false;
    }

    /**
     * Unsafe, may be deleted later
     */
    public static byte[] copyOf(
        @NotNull Binary bin
    ) {
        int l = bin.size;
        if (l == 0) {
            return EMPTY_BYTES;
        }

        byte[] v = bin.value;
        if (l == 4 && bin.state == 0) {
            if (v[0] == 'n' &&
                v[1] == 'u' &&
                v[2] == 'l' &&
                v[3] == 'l') {
                return null;
            }
        }

        byte[] buffer = new byte[l];
        System.arraycopy(
            v, 0, buffer, 0, l
        );
        return buffer;
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
    @SuppressWarnings("deprecation")
    public static String latin(
        @NotNull Binary bin
    ) {
        int l = bin.size;
        if (l == 0) {
            return "";
        }

        return new String(
            bin.value, 0, 0, l
        );
    }

    /**
     * Unsafe, may be deleted later
     */
    public static Binary binary(
        @NotNull Binary bin
    ) {
        int l = bin.size;
        if (l == 0) {
            return new Binary();
        }

        byte[] v = bin.value;
        if (l == 4 && bin.state == 0) {
            if (v[0] == 'n' &&
                v[1] == 'u' &&
                v[2] == 'l' &&
                v[3] == 'l') {
                return null;
            }
        }

        byte[] buffer = new byte[l];
        System.arraycopy(
            v, 0, buffer, 0, l
        );
        return new Binary(buffer, l);
    }

    /**
     * Unsafe, may be deleted later
     */
    public static String string(
        @NotNull Binary bin
    ) {
        int l = bin.size;
        if (l == 0) {
            return "";
        }

        byte[] v = bin.value;
        if (l == 4 && bin.state == 0) {
            if (v[0] == 'n' &&
                v[1] == 'u' &&
                v[2] == 'l' &&
                v[3] == 'l') {
                return null;
            }
        }

        return new String(v, 0, l, UTF_8);
    }

    public static final long FNV_PRIME = 0x100000001B3L;
    public static final long FNV_BASIS = 0xCBF29CE484222325L;

    /**
     * Unsafe, may be deleted later
     */
    public static long hash1(
        @NotNull Object name
    ) {
        if (name instanceof Binary) {
            Binary n = (Binary) name;

            int l = n.size;
            long h = FNV_BASIS;

            byte[] v = n.value;
            for (int i = 0; i < l; i++) {
                h = (v[i] ^ h) * FNV_PRIME;
            }
            return h;
        }

        if (name instanceof String) {
            String n = (String) name;

            int l = n.length();
            long h = FNV_BASIS;

            for (int i = 0; i < l; i++) {
                h = (n.charAt(i) ^ h) * FNV_PRIME;
            }
            return h;
        }

        throw new IllegalStateException(
            "Received name(" + (name == null ?
                "null" : name.getClass()) + ") is not supported"
        );
    }

    /**
     * Unsafe, may be deleted later
     */
    public static long hash2(
        @NotNull Object name
    ) {
        if (name instanceof Binary) {
            Binary n = (Binary) name;

            int i = 0,
                l = n.size;
            long h = FNV_BASIS;

            byte[] v = n.value;
            boolean flag = true;

            while (i < l) {
                long w = v[i++];
                if (w != '_') {
                    if (w > 0x40 &&
                        w < 0x5B) {
                        if (flag) {
                            w += 32;
                        }
                    } else {
                        flag = false;
                    }
                } else {
                    if (i == 1 ||
                        i == l) {
                        return 0;
                    }
                    w = v[i++];
                    if (w == '_') {
                        return 0;
                    }
                    if (w < 0x61 ||
                        w > 0x7A) {
                        flag = true;
                    } else {
                        w -= 32;
                        flag = false;
                    }
                }
                h = (w ^ h) * FNV_PRIME;
            }
            return h;
        }

        if (name instanceof String) {
            String n = (String) name;

            int i = 0,
                l = n.length();
            long h = FNV_BASIS;

            boolean flag = true;
            while (i < l) {
                long w = n.charAt(i++);
                if (w != '_') {
                    if (w > 0x40 &&
                        w < 0x5B) {
                        if (flag) {
                            w += 32;
                        }
                    } else {
                        flag = false;
                    }
                } else {
                    if (i == 1 ||
                        i == l) {
                        return 0;
                    }
                    w = n.charAt(i++);
                    if (w == '_') {
                        return 0;
                    }
                    if (w < 0x61 ||
                        w > 0x7A) {
                        flag = true;
                    } else {
                        w -= 32;
                        flag = false;
                    }
                }
                h = (w ^ h) * FNV_PRIME;
            }
            return h;
        }

        throw new IllegalStateException(
            "Received name(" + (name == null ?
                "null" : name.getClass()) + ") is not supported"
        );
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

        if (value instanceof CharSequence) {
            return chan.set(
                alias, StringifySpare.INSTANCE, value
            );
        }

        if (value instanceof ByteSequence) {
            return chan.set(
                alias, BinaryifySpare.INSTANCE, value
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

        if (value instanceof Calendar) {
            return chan.set(
                alias, CalendarSpare.INSTANCE, value
            );
        }

        if (value instanceof Throwable) {
            Throwable o = (Throwable) value;
            return chan.set(
                alias, StringSpare.INSTANCE, o.getMessage()
            );
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
