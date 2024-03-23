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

import plus.kat.lang.*;
import plus.kat.flow.*;
import plus.kat.actor.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public abstract class Flow implements Flag {

    public int i;
    public int l;

    public long f;
    public byte[] v;

    /**
     * Loads the remaining flow to the {@link #v} and update
     * {@link #i} and {@link #l}, returns the current length
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    public int load()
        throws IOException {
        return l = -1;
    }

    /**
     * Reads a byte from this flow. if this flow has readable data,
     * the {@link #i} switches to next, otherwise returns {@code '0'}
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    public byte read()
        throws IOException {
        if (i < l) {
            return v[i++];
        } else {
            if (load() < 1) {
                return 0;
            } else {
                return v[i++];
            }
        }
    }

    /**
     * Reads a byte from this flow. if this flow has readable data,
     * the {@link #i} switches to next, otherwise raise {@link IOException}
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    public byte next()
        throws IOException {
        if (i < l) {
            return v[i++];
        } else {
            if (load() > 0) {
                return v[i++];
            }
        }

        throw new IOException(
            "No more readable bytes, please " +
                "check whether this flow is damaged"
        );
    }

    /**
     * Check if this still has readable bytes,
     * generally used with {@link Flow#read()}
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    public boolean also()
        throws IOException {
        if (i < l) {
            return true;
        } else {
            return load() > 0;
        }
    }

    /**
     * Reads the hex-code of the specified step into bytes
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    public int code(int step)
        throws IOException {
        int n = 0;
        for (; step > 0; step--) {
            int w = i < l ?
                v[i++] : next();
            if (w > 0x2F) {
                if (w < 0x3A) {
                    n = n << 4 | (w - 0x30);
                    continue;
                }
                if (w > 0x60 && w < 0x67) {
                    n = n << 4 | (w - 0x57);
                    continue;
                }
                if (w > 0x40 && w < 0x47) {
                    n = n << 4 | (w - 0x37);
                    continue;
                }
            }
            throw new IOException(
                "Illegal hex-code: " + w
            );
        }
        return n;
    }

    /**
     * Skip over and discards exactly bytes of the specified step from this {@link Flow}
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    public boolean skip(int step)
        throws IOException {
        if (step > 0) {
            int valve = l - i;
            while (step > valve) {
                if (load() > 0) {
                    if (valve > 0) {
                        step -= valve;
                    }
                    valve = l - i;
                } else {
                    throw new EOFException(
                        "Failed to skip exactly"
                    );
                }
            }
            i += step;
            return true;
        } else return false;
    }

    /**
     * Enable the specified feature
     *
     * @param flag the specified flag code
     * @see Flag
     */
    public Flow and(
        @NotNull long flag
    ) {
        f |= flag;
        return this;
    }

    /**
     * Disable the specified feature
     *
     * @param flag the specified flag code
     * @see Flag
     */
    public Flow not(
        @NotNull long flag
    ) {
        f &= ~flag;
        return this;
    }

    /**
     * Check if this uses the feature
     *
     * @param flag the specified flag code
     */
    public boolean isFlag(
        @NotNull long flag
    ) {
        return (f & flag) == flag;
    }

    /**
     * Closes this flow and releases
     * the resources associated with it
     */
    public void close() {
        l = -1;
        v = null;
    }

    /**
     * Returns the literal value of this
     * {@link Flow} and marks the current index
     */
    @Override
    @SuppressWarnings("deprecation")
    public String toString() {
        int index = i;
        int limit = l;

        int r = 0, n = 22;
        if (limit < 1) {
            limit = index;
        }

        if (index > 0) {
            int m = 10;
            r = index - 1;
            while (n < 31) {
                if (r < m) {
                    break;
                } else {
                    n++;
                    m = 10 * m;
                }
            }
        }

        final int e = r;
        final int m = n + limit;

        byte[] t = new byte[m];
        try {
            System.arraycopy(
                v, 0, t, --n, limit
            );

            t[--n] = '`';
            t[--n] = ' ';
            t[--n] = 'n';
            t[--n] = 'i';
            t[--n] = ' ';

            do {
                t[--n] = (byte) (
                    0x30 + (r % 10)
                );
            } while ((r /= 10) != 0);

            t[--n] = ' ';
            t[--n] = 'x';
            t[--n] = 'e';
            t[--n] = 'd';
            t[--n] = 'n';
            t[--n] = 'i';
            t[--n] = ' ';
            t[--n] = 'h';
            t[--n] = 't';
            t[--n] = 'i';
            t[--n] = 'w';
            t[--n] = ' ';
            t[--n] = '`';
            t[--n] = v[e];
            t[--n] = '`';
            t[m - 1] = '`';
        } catch (Throwable th) {
            // Nothing
        }
        return new String(t, 0, 0, m);
    }

    /**
     * Returns the {@link Flow} of the specified byte array
     *
     * @throws NullPointerException If the specified array is null
     */
    public static Flow of(
        @NotNull byte[] text
    ) {
        return new ByteFlow(text);
    }

    /**
     * Returns the {@link Flow} of the specified byte array
     *
     * @throws NullPointerException      If the specified array is null
     * @throws IndexOutOfBoundsException If received index is out of bounds
     */
    public static Flow of(
        @NotNull byte[] text, int index, int length
    ) {
        return new ByteFlow(
            text, index, length
        );
    }

    /**
     * Returns the {@link Flow} of the specified char array
     *
     * @throws NullPointerException If the specified array is null
     */
    public static Flow of(
        @NotNull char[] text
    ) {
        return new CharFlow(text);
    }

    /**
     * Returns the {@link Flow} of the specified char array
     *
     * @throws NullPointerException      If the specified array is null
     * @throws IndexOutOfBoundsException If received index is out of bounds
     */
    public static Flow of(
        @NotNull char[] text, int index, int length
    ) {
        return new CharFlow(
            text, index, length
        );
    }

    /**
     * Returns the {@link Flow} of the specified {@link Binary}
     *
     * @throws NullPointerException If the specified binary is null
     */
    public static Flow of(
        @NotNull Binary text
    ) {
        return new ByteFlow(text);
    }

    /**
     * Returns the {@link Flow} of the specified {@link Binary}
     *
     * @throws NullPointerException      If the specified binary is null
     * @throws IndexOutOfBoundsException If received index is out of bounds
     */
    public static Flow of(
        @NotNull Binary text, int index, int length
    ) {
        return new ByteFlow(
            text, index, length
        );
    }

    /**
     * Returns the {@link Flow} of the specified {@link String}
     *
     * @throws NullPointerException If the specified string is null
     */
    public static Flow of(
        @NotNull String text
    ) {
        return new StringFlow(text);
    }

    /**
     * Returns the {@link Flow} of the specified {@link String}
     *
     * @throws NullPointerException      If the specified string is null
     * @throws IndexOutOfBoundsException If received index is out of bounds
     */
    public static Flow of(
        @NotNull String text, int index, int length
    ) {
        return new StringFlow(
            text, index, length
        );
    }

    /**
     * Returns a {@link Flow} where
     * calling {@link Reader#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *  try (Reader reader = ...) {
     *     Flow flow = Flow.of(reader);
     *  }
     * }</pre>
     *
     * @throws NullPointerException If the specified reader is null
     */
    public static Flow of(
        @NotNull Reader text
    ) {
        return new ReaderFlow(text);
    }

    /**
     * Returns a {@link Flow} where
     * calling {@link InputStream#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *  try (InputStream stream = ...) {
     *     Flow flow = Flow.of(stream);
     *  }
     * }</pre>
     *
     * @throws NullPointerException If the specified stream is null
     */
    public static Flow of(
        @NotNull InputStream text
    ) {
        return new InputStreamFlow(text);
    }

    /**
     * Returns the {@link Flow} of the specified {@link ByteBuffer}
     *
     * @throws NullPointerException If the specified buffer is null
     */
    public static Flow of(
        @NotNull ByteBuffer text
    ) {
        if (text.hasArray()) {
            int m = text.limit(),
                n = text.position();
            text.position(m);
            return new ByteFlow(
                text.array(), n +
                text.arrayOffset(), m
            );
        }
        return new ByteBufferFlow(text);
    }

    /**
     * Returns the {@link Flow} of the specified {@link CharBuffer}
     *
     * @throws NullPointerException If the specified buffer is null
     */
    public static Flow of(
        @NotNull CharBuffer text
    ) {
        if (text.hasArray()) {
            int m = text.limit(),
                n = text.position();
            text.position(m);
            return new CharFlow(
                text.array(), n +
                text.arrayOffset(), m
            );
        }
        return new CharBufferFlow(text);
    }

    /**
     * Returns the {@link Flow} of the specified {@link String}
     *
     * @throws NullPointerException If the specified buffer is null
     */
    public static Flow of(
        @NotNull String text, Charset charset
    ) {
        if (charset != null) {
            return new ByteFlow(
                text.getBytes(charset)
            );
        } else {
            return new StringFlow(text);
        }
    }

    /**
     * Returns a {@link Flow} where
     * calling {@link InputStream#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *  Charset charset = ...
     *  try (InputStream stream = ...) {
     *     Flow flow = Flow.of(
     *         stream, charset
     *     );
     *  }
     * }</pre>
     *
     * @throws NullPointerException If the specified stream is null
     */
    public static Flow of(
        @NotNull InputStream text, Charset charset
    ) {
        match:
        if (charset != null) {
            switch (charset.name()) {
                case "UTF-8":
                case "US-ASCII":
                case "ISO-8859-1": {
                    break match;
                }
            }
            return new ReaderFlow(
                new InputStreamReader(
                    text, charset
                )
            );
        }

        return new InputStreamFlow(text);
    }
}
