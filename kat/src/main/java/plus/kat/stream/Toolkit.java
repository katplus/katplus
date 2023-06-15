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
     * Unsafe and may be deleted
     */
    public static final byte[]
        EMPTY_BYTES = {};

    /**
     * empty char array
     */
    public static final char[]
        EMPTY_CHARS = {};

    /**
     * Unsafe and may be deleted
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
     * Unsafe and may be deleted
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
     * Unsafe and may be deleted
     */
    public static final byte[] RFC4648_ENCODE = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };
    /**
     * Unsafe and may be deleted
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
     * Unsafe and may be deleted
     */
    static final byte[] RFC4648_SAFE_ENCODE = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };
    /**
     * Unsafe and may be deleted
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
     * Unsafe and may be deleted
     */
    public static byte[] valueOf(
        @NotNull Binary binary
    ) {
        return binary.value;
    }

    /**
     * Unsafe and may be deleted
     */
    public static Bucket isolate(
        @NotNull Stream stream
    ) {
        stream.isolate = true;
        return stream.bucket;
    }

    /**
     * Unsafe and may be deleted
     */
    public static boolean isIsolate(
        @NotNull Stream stream
    ) {
        return stream.isolate;
    }

    /**
     * Unsafe and may be deleted
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
     * Unsafe and may be deleted
     */
    public static boolean set(
        Chan chan, String alias, Object value
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
            TimeZone o = (TimeZone) value;
            return chan.set(
                alias, StringSpare.INSTANCE, o.getID()
            );
        }

        if (value instanceof Optional) {
            Optional<?> o = (Optional<?>) value;
            return chan.set(
                alias, null, o.orElse(null)
            );
        }

        if (value instanceof OptionalInt) {
            OptionalInt o = (OptionalInt) value;
            return chan.set(
                alias, IntSpare.INSTANCE, o.orElse(0)
            );
        }

        if (value instanceof OptionalLong) {
            OptionalLong o = (OptionalLong) value;
            return chan.set(
                alias, LongSpare.INSTANCE, o.orElse(0L)
            );
        }

        if (value instanceof OptionalDouble) {
            OptionalDouble o = (OptionalDouble) value;
            return chan.set(
                alias, DoubleSpare.INSTANCE, o.orElse(0D)
            );
        }

        throw new IOException(
            "No available coder for `"
                + value.getClass() + "` was found"
        );
    }

    /**
     * Unsafe and may be deleted
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
     * Unsafe and may be deleted
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
     * Unsafe and may be deleted
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
     * Unsafe and may be deleted
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
