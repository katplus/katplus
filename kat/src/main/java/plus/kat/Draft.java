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
package plus.kat;

import plus.kat.actor.*;
import plus.kat.stream.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.lang.reflect.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public abstract class Draft<T> {

    protected Flow flow;
    protected Type type;

    /**
     * Constructs an empty draft and gets
     * the generic type of this {@link Draft}
     */
    public Draft() {
        Type t = getClass()
            .getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            type = ((ParameterizedType) t)
                .getActualTypeArguments()[0];
        }
    }

    /**
     * For example
     * <pre>{@code
     *  Flow text = ...
     *  Draft<Response<User>> draft =
     *      new Draft<Response<User>>(text){};
     * }</pre>
     *
     * @param text the specified {@link Flow} to be used
     */
    public Draft(
        @Nilable Flow text
    ) {
        this();
        flow = text;
    }

    /**
     * For example
     * <pre>{@code
     *  byte[] text = ...
     *  Draft<Response<User>> draft =
     *      new Draft<Response<User>>(text){};
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code text} is null
     */
    public Draft(
        @NotNull byte[] text
    ) {
        this();
        flow = Flow.of(text);
    }

    /**
     * For example
     * <pre>{@code
     *  char[] text = ...
     *  Draft<Response<User>> draft =
     *      new Draft<Response<User>>(text){};
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code text} is null
     */
    public Draft(
        @NotNull char[] text
    ) {
        this();
        flow = Flow.of(text);
    }

    /**
     * For example
     * <pre>{@code
     *  Binary text = ...
     *  Draft<Response<User>> draft =
     *      new Draft<Response<User>>(text){};
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code text} is null
     */
    public Draft(
        @NotNull Binary text
    ) {
        this();
        flow = Flow.of(text);
    }

    /**
     * Constructs an {@link Draft} where
     * calling {@link Reader#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *  try (Reader text = ... ) {
     *      Draft<Response<User>> draft =
     *          new Draft<Response<User>>(text){};
     *  }
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code text} is null
     */
    public Draft(
        @NotNull Reader text
    ) {
        this();
        flow = Flow.of(text);
    }

    /**
     * For example
     * <pre>{@code
     *  String text = ...
     *  Draft<Response<User>> draft =
     *      new Draft<Response<User>>(text){};
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code text} is null
     */
    public Draft(
        @NotNull String text
    ) {
        this();
        flow = Flow.of(text);
    }

    /**
     * For example
     * <pre>{@code
     *  ByteBuffer text = ...
     *  Draft<Response<User>> draft =
     *      new Draft<Response<User>>(text){};
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code text} is null
     */
    public Draft(
        @NotNull ByteBuffer text
    ) {
        this();
        flow = Flow.of(text);
    }

    /**
     * For example
     * <pre>{@code
     *  CharBuffer text = ...
     *  Draft<Response<User>> draft =
     *      new Draft<Response<User>>(text){};
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code text} is null
     */
    public Draft(
        @NotNull CharBuffer text
    ) {
        this();
        flow = Flow.of(text);
    }

    /**
     * Constructs an {@link Draft} where
     * calling {@link InputStream#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *  try (InputStream text = ... ) {
     *      Draft<Response<User>> draft =
     *          new Draft<Response<User>>(text){};
     *  }
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code text} is null
     */
    public Draft(
        @NotNull InputStream text
    ) {
        this();
        flow = Flow.of(text);
    }

    /**
     * Constructs an {@link Draft} where
     * calling {@link InputStream#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *  Charset charset = ...
     *  try (InputStream text = ... ) {
     *     Draft<Response<User>> draft =
     *        new Draft<Response<User>>(text, charset){};
     *  }
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code text} is null
     */
    public Draft(
        @NotNull InputStream text,
        @Nullable Charset charset
    ) {
        this();
        flow = Flow.of(
            text, charset
        );
    }

    /**
     * For example
     * <pre>{@code
     *  byte[] text = ...
     *  Draft<Response<User>> draft =
     *      new Draft<Response<User>>(text, 0, 6){};
     * }</pre>
     *
     * @throws NullPointerException      If the specified {@code text} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     */
    public Draft(
        @NotNull byte[] text, int index, int length
    ) {
        this();
        flow = Flow.of(
            text, index, length
        );
    }

    /**
     * For example
     * <pre>{@code
     *  char[] text = ...
     *  Draft<Response<User>> draft =
     *      new Draft<Response<User>>(text, 0, 6){};
     * }</pre>
     *
     * @throws NullPointerException      If the specified {@code text} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     */
    public Draft(
        @NotNull char[] text, int index, int length
    ) {
        this();
        flow = Flow.of(
            text, index, length
        );
    }

    /**
     * For example
     * <pre>{@code
     *  Binary text = ...
     *  Draft<Response<User>> draft =
     *      new Draft<Response<User>>(text, 0, 6){};
     * }</pre>
     *
     * @throws NullPointerException      If the specified {@code text} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     */
    public Draft(
        @NotNull Binary text, int index, int length
    ) {
        this();
        flow = Flow.of(
            text, index, length
        );
    }

    /**
     * For example
     * <pre>{@code
     *  String text = ...
     *  Draft<Response<User>> draft =
     *      new Draft<Response<User>>(text, 0, 6){};
     * }</pre>
     *
     * @throws NullPointerException      If the specified {@code text} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     */
    public Draft(
        @NotNull String text, int index, int length
    ) {
        this();
        flow = Flow.of(
            text, index, length
        );
    }

    /**
     * Uses the specified {@code flag}
     */
    public Draft<T> with(
        long flag
    ) {
        flow.with(flag);
        return this;
    }

    /**
     * Uses the specified {@link Plan}
     */
    public Draft<T> with(
        @NotNull Plan plan
    ) {
        flow.with(plan);
        return this;
    }

    /**
     * Gets the {@link Flow} of this {@link Draft}
     */
    @Nullable
    public Flow getFlow() {
        return flow;
    }

    /**
     * Sets the {@link Flow} of this {@link Draft}
     *
     * @param flow the specified flow
     */
    public void setFlow(
        @Nullable Flow flow
    ) {
        if (flow != null) {
            this.flow = flow;
        }
    }

    /**
     * Gets the {@link Type} of this {@link Draft}
     */
    @Nullable
    public Type getType() {
        return type;
    }

    /**
     * Sets the {@link Type} of this {@link Draft}
     *
     * @param type the specified type
     */
    public void setType(
        @Nullable Type type
    ) {
        if (type != null) {
            this.type = type;
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    public static final class Unsafe {
        /**
         * Unsafe, may be deleted later
         */
        public static final byte
            LT = '<', GT = '>',
            AMP = '&', QUOT = '"',
            APOS = '\'', SLASH = '/';

        private Unsafe() {
            throw new IllegalStateException();
        }

        /**
         * Unsafe, may be deleted later
         */
        public static int word(
            Flow t,
            int n,
            byte[] val
        ) throws IOException {
            int z, w = 0;
            for (z = 0; z != 4; z++) {
                int u = t.next();
                if (u > 0x2F) {
                    if (u < 0x3A) {
                        w = w << 4 | (u - 0x30);
                        continue;
                    }
                    if (u > 0x60 && u < 0x67) {
                        w = w << 4 | (u - 0x57);
                        continue;
                    }
                    if (u > 0x40 && u < 0x47) {
                        w = w << 4 | (u - 0x37);
                        continue;
                    }
                }
                throw new IOException(
                    "Non-hexadecimal number: " + u
                );
            }

            if (w < 0x800) {
                if (w < 0x80) {
                    val[n++] = (byte) w;
                } else {
                    val[n++] = (byte) (
                        w >> 6 | 0xC0
                    );
                    val[n++] = (byte) (
                        w & 0x3F | 0x80
                    );
                }
            } else if (w < 0xD800 || 0xDFFF < w) {
                val[n++] = (byte) (
                    w >> 12 | 0xE0
                );
                val[n++] = (byte) (
                    w >> 6 & 0x3F | 0x80
                );
                val[n++] = (byte) (w & 0x3F | 0x80);
            } else {
                if (w > 0xDBFF) {
                    throw new IOException(
                        "Illegal agent pair: "
                            + Integer.toHexString(w)
                    );
                }

                if (t.next() != 0x5C ||
                    t.next() != 0x75) {
                    throw new IOException(
                        "Illegal escape char"
                    );
                }

                int m = 0;
                for (z = 0; z != 4; z++) {
                    int u = t.next();
                    if (u > 0x2F) {
                        if (u < 0x3A) {
                            m = m << 4 | (u - 0x30);
                            continue;
                        }
                        if (u > 0x60 && u < 0x67) {
                            m = m << 4 | (u - 0x57);
                            continue;
                        }
                        if (u > 0x40 && u < 0x47) {
                            m = m << 4 | (u - 0x37);
                            continue;
                        }
                    }
                    throw new IOException(
                        "Non-hexadecimal number: " + u
                    );
                }

                if (0xDBFF < m && m < 0xE000) {
                    val[n++] = (byte) (
                        (w += 0x40) >> 8 & 0x07 | 0xF0
                    );
                    val[n++] = (byte) (
                        w >> 2 & 0x3F | 0x80
                    );
                    val[n++] = (byte) (
                        w << 4 & 0x30 |
                            m >> 6 & 0x0F | 0x80
                    );
                    val[n++] = (byte) (m & 0x3F | 0x80);
                } else {
                    throw new IOException(
                        "Illegal agent pair: " + Integer.toHexString(m)
                    );
                }
            }
            return n;
        }

        /**
         * Unsafe, may be deleted later
         */
        public static int text(
            Flow t,
            byte[] val
        ) throws IOException {
            int n = 0;
            while (true) {
                int i = t.index;
                int l = t.limit;

                int z, e = i;
                byte[] v = t.value;

                check:
                while (i < l) {
                    switch (v[i++]) {
                        case 0x22: {
                            if ((z = i - e - 1) > 0) {
                                System.arraycopy(
                                    v, e, val, n, z
                                );
                                n += z;
                            }
                            t.index = i;
                            return n;
                        }
                        case 0x5C: {
                            if ((z = i - e - 1) > 0) {
                                System.arraycopy(
                                    v, e, val, n, z
                                );
                                n += z;
                            }
                            while (true) {
                                if (i == l) {
                                    if (t.load() > 0) {
                                        i = t.index;
                                        l = t.limit;
                                        v = t.value;
                                    } else {
                                        throw new IOException(
                                            "No more readable bytes"
                                        );
                                    }
                                }

                                byte a;
                                switch (a = v[i++]) {
                                    case 's': {
                                        val[n++] = ' ';
                                        break;
                                    }
                                    case 'b': {
                                        val[n++] = '\b';
                                        break;
                                    }
                                    case 'f': {
                                        val[n++] = '\f';
                                        break;
                                    }
                                    case 't': {
                                        val[n++] = '\t';
                                        break;
                                    }
                                    case 'r': {
                                        val[n++] = '\r';
                                        break;
                                    }
                                    case 'n': {
                                        val[n++] = '\n';
                                        break;
                                    }
                                    case 'u': {
                                        int w = 0;
                                        for (z = 0; z != 4; z++) {
                                            if (i == l) {
                                                if (t.load() > 0) {
                                                    i = t.index;
                                                    l = t.limit;
                                                    v = t.value;
                                                } else {
                                                    throw new IOException(
                                                        "No more readable bytes"
                                                    );
                                                }
                                            }
                                            int u = v[i++];
                                            if (u > 0x2F) {
                                                if (u < 0x3A) {
                                                    w = w << 4 | (u - 0x30);
                                                    continue;
                                                }
                                                if (u > 0x60 && u < 0x67) {
                                                    w = w << 4 | (u - 0x57);
                                                    continue;
                                                }
                                                if (u > 0x40 && u < 0x47) {
                                                    w = w << 4 | (u - 0x37);
                                                    continue;
                                                }
                                            }
                                            throw new IOException(
                                                "Non-hexadecimal number: " + u
                                            );
                                        }

                                        if (w < 0x800) {
                                            if (w < 0x80) {
                                                val[n++] = (byte) w;
                                            } else {
                                                val[n++] = (byte) (
                                                    w >> 6 | 0xC0
                                                );
                                                val[n++] = (byte) (
                                                    w & 0x3F | 0x80
                                                );
                                            }
                                            break;
                                        }

                                        if (w < 0xD800 || 0xDFFF < w) {
                                            val[n++] = (byte) (
                                                w >> 12 | 0xE0
                                            );
                                            val[n++] = (byte) (
                                                w >> 6 & 0x3F | 0x80
                                            );
                                            val[n++] = (byte) (w & 0x3F | 0x80);
                                            break;
                                        }

                                        if (w > 0xDBFF) {
                                            throw new IOException(
                                                "Illegal agent pair: "
                                                    + Integer.toHexString(w)
                                            );
                                        }

                                        int m = 0;
                                        for (z = 0; z != 6; z++) {
                                            if (i == l) {
                                                if (t.load() > 0) {
                                                    i = t.index;
                                                    l = t.limit;
                                                    v = t.value;
                                                } else {
                                                    throw new IOException(
                                                        "No more readable bytes"
                                                    );
                                                }
                                            }
                                            switch (z) {
                                                case 0: {
                                                    if (v[i++] == 0x5C) {
                                                        break;
                                                    } else {
                                                        throw new IOException(
                                                            "Illegal escape char"
                                                        );
                                                    }
                                                }
                                                case 1: {
                                                    if (v[i++] == 0x75) {
                                                        break;
                                                    } else {
                                                        throw new IOException(
                                                            "Illegal escape char"
                                                        );
                                                    }
                                                }
                                                default: {
                                                    int u = v[i++];
                                                    if (u > 0x2F) {
                                                        if (u < 0x3A) {
                                                            m = m << 4 | (u - 0x30);
                                                            continue;
                                                        }
                                                        if (u > 0x60 && u < 0x67) {
                                                            m = m << 4 | (u - 0x57);
                                                            continue;
                                                        }
                                                        if (u > 0x40 && u < 0x47) {
                                                            m = m << 4 | (u - 0x37);
                                                            continue;
                                                        }
                                                    }
                                                    throw new IOException(
                                                        "Non-hexadecimal number: " + u
                                                    );
                                                }
                                            }
                                        }

                                        if (0xDBFF < m && m < 0xE000) {
                                            val[n++] = (byte) (
                                                (w += 0x40) >> 8 & 0x07 | 0xF0
                                            );
                                            val[n++] = (byte) (
                                                w >> 2 & 0x3F | 0x80
                                            );
                                            val[n++] = (byte) (
                                                w << 4 & 0x30 |
                                                    m >> 6 & 0x0F | 0x80
                                            );
                                            val[n++] = (byte) (m & 0x3F | 0x80);
                                            break;
                                        } else {
                                            throw new IOException(
                                                "Illegal agent pair: " + Integer.toHexString(m)
                                            );
                                        }
                                    }
                                    default: {
                                        val[n++] = a;
                                    }
                                }

                                if (i == l) {
                                    if (t.load() > 0) {
                                        i = t.index;
                                        l = t.limit;
                                        v = t.value;
                                    } else {
                                        throw new IOException(
                                            "No more readable bytes, please " +
                                                "check whether this nodus is damaged"
                                        );
                                    }
                                }

                                switch (v[i++]) {
                                    case 0x22: {
                                        t.index = i;
                                        return n;
                                    }
                                    case 0x5C: {
                                        continue;
                                    }
                                    default: {
                                        e = i - 1;
                                        continue check;
                                    }
                                }
                            }
                        }
                    }
                }

                if ((z = i - e) > 0) {
                    System.arraycopy(
                        v, e, val, n, z
                    );
                    n += z;
                }

                if (t.load() <= 0) {
                    throw new IOException(
                        "No more readable bytes, please " +
                            "check whether this nodus is damaged"
                    );
                }
            }
        }
    }
}
