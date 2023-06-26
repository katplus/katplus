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
            int z,
            byte[] val
        ) throws IOException {
            int c = 0;
            for (int k = 0; k != 4; k++) {
                int b = t.next();
                if (b > 0x2F) {
                    if (b < 0x3A) {
                        c = c << 4 | (b - 0x30);
                        continue;
                    }
                    if (b > 0x60 && b < 0x67) {
                        c = c << 4 | (b - 0x57);
                        continue;
                    }
                    if (b > 0x40 && b < 0x47) {
                        c = c << 4 | (b - 0x37);
                        continue;
                    }
                }
                throw new IOException(
                    "Non-hexadecimal number: " + b
                );
            }

            if (c < 0x800) {
                if (c < 0x80) {
                    val[z++] = (byte) c;
                } else {
                    val[z++] = (byte) (c >> 6 | 0xC0);
                    val[z++] = (byte) (c & 0x3F | 0x80);
                }
            } else if (c < 0xD800 || 0xDFFF < c) {
                val[z++] = (byte) (c >> 12 | 0xE0);
                val[z++] = (byte) (c >> 6 & 0x3F | 0x80);
                val[z++] = (byte) (c & 0x3F | 0x80);
            } else {
                if (c > 0xDBFF) {
                    throw new IOException(
                        "Illegal agent pair: "
                            + Integer.toHexString(c)
                    );
                }

                byte ec = t.next();
                byte em = t.next();
                if (ec != 0x5C || em != 0x75) {
                    throw new IOException(
                        "Illegal escape char: "
                            + (char) ec + (char) em
                    );
                }

                int g = 0;
                for (int k = 0; k != 4; k++) {
                    int b = t.next();
                    if (b > 0x2F) {
                        if (b < 0x3A) {
                            g = g << 4 | (b - 0x30);
                            continue;
                        }
                        if (b > 0x60 && b < 0x67) {
                            g = g << 4 | (b - 0x57);
                            continue;
                        }
                        if (b > 0x40 && b < 0x47) {
                            g = g << 4 | (b - 0x37);
                            continue;
                        }
                    }
                    throw new IOException(
                        "Non-hexadecimal number: " + b
                    );
                }

                if (0xDBFF < g && g < 0xE000) {
                    val[z++] = (byte) ((c += 0x40) >> 8 & 0x07 | 0xF0);
                    val[z++] = (byte) (c >> 2 & 0x3F | 0x80);
                    val[z++] = (byte) (c << 4 & 0x30 | g >> 6 & 0x0F | 0x80);
                    val[z++] = (byte) (g & 0x3F | 0x80);
                } else {
                    throw new IOException(
                        "Illegal agent pair: " + Integer.toHexString(g)
                    );
                }
            }
            return z;
        }

        /**
         * Unsafe, may be deleted later
         */
        public static int text(
            Flow t,
            byte[] val
        ) throws IOException {
            int x = 0;
            while (true) {
                int i = t.index;
                int l = t.limit;

                byte[] v = t.value;
                while (i < l) {
                    byte w = v[i++];
                    switch (w) {
                        case 0x22: {
                            t.index = i;
                            return x;
                        }
                        case 0x5C: {
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
                            switch (w = v[i++]) {
                                case 's': {
                                    val[x++] = ' ';
                                    continue;
                                }
                                case 'b': {
                                    val[x++] = '\b';
                                    continue;
                                }
                                case 'f': {
                                    val[x++] = '\f';
                                    continue;
                                }
                                case 't': {
                                    val[x++] = '\t';
                                    continue;
                                }
                                case 'r': {
                                    val[x++] = '\r';
                                    continue;
                                }
                                case 'n': {
                                    val[x++] = '\n';
                                    continue;
                                }
                                case 'u': {
                                    int c = 0;
                                    for (int n = 0; n != 4; n++) {
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
                                        int m = v[i++];
                                        if (m > 0x2F) {
                                            if (m < 0x3A) {
                                                c = c << 4 | (m - 0x30);
                                                continue;
                                            }
                                            if (m > 0x60 && m < 0x67) {
                                                c = c << 4 | (m - 0x57);
                                                continue;
                                            }
                                            if (m > 0x40 && m < 0x47) {
                                                c = c << 4 | (m - 0x37);
                                                continue;
                                            }
                                        }
                                        throw new IOException(
                                            "Non-hexadecimal number: " + m
                                        );
                                    }

                                    if (c < 0x800) {
                                        if (c < 0x80) {
                                            val[x++] = (byte) c;
                                        } else {
                                            val[x++] = (byte) (
                                                c >> 6 | 0xC0
                                            );
                                            val[x++] = (byte) (
                                                c & 0x3F | 0x80
                                            );
                                        }
                                        continue;
                                    }

                                    if (c < 0xD800 || 0xDFFF < c) {
                                        val[x++] = (byte) (
                                            c >> 12 | 0xE0
                                        );
                                        val[x++] = (byte) (
                                            c >> 6 & 0x3F | 0x80
                                        );
                                        val[x++] = (byte) (c & 0x3F | 0x80);
                                        continue;
                                    }

                                    if (c > 0xDBFF) {
                                        throw new IOException(
                                            "Illegal agent pair: "
                                                + Integer.toHexString(c)
                                        );
                                    }

                                    int d = 0;
                                    for (int n = 0; n != 6; n++) {
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
                                        switch (n) {
                                            case 0: {
                                                if (v[i++] == 0x5C) {
                                                    continue;
                                                } else {
                                                    throw new IOException(
                                                        "Illegal escape char"
                                                    );
                                                }
                                            }
                                            case 1: {
                                                if (v[i++] == 0x75) {
                                                    continue;
                                                } else {
                                                    throw new IOException(
                                                        "Illegal escape char"
                                                    );
                                                }
                                            }
                                            default: {
                                                int m = v[i++];
                                                if (m > 0x2F) {
                                                    if (m < 0x3A) {
                                                        d = d << 4 | (m - 0x30);
                                                        continue;
                                                    }
                                                    if (m > 0x60 && m < 0x67) {
                                                        d = d << 4 | (m - 0x57);
                                                        continue;
                                                    }
                                                    if (m > 0x40 && m < 0x47) {
                                                        d = d << 4 | (m - 0x37);
                                                        continue;
                                                    }
                                                }
                                                throw new IOException(
                                                    "Non-hexadecimal number: " + m
                                                );
                                            }
                                        }
                                    }

                                    if (0xDBFF < d && d < 0xE000) {
                                        val[x++] = (byte) (
                                            (c += 0x40) >> 8 & 0x07 | 0xF0
                                        );
                                        val[x++] = (byte) (
                                            c >> 2 & 0x3F | 0x80
                                        );
                                        val[x++] = (byte) (
                                            c << 4 & 0x30 |
                                                d >> 6 & 0x0F | 0x80
                                        );
                                        val[x++] = (byte) (d & 0x3F | 0x80);
                                        continue;
                                    } else {
                                        throw new IOException(
                                            "Illegal agent pair: " + Integer.toHexString(d)
                                        );
                                    }
                                }
                            }
                        }
                        default: {
                            val[x++] = w;
                        }
                    }
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
