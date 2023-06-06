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
import java.nio.charset.*;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * @author kraity
 * @since 0.0.6
 */
public abstract class Flow implements Flag {

    protected int index;
    protected int limit;

    protected long flags;
    protected byte[] value;

    /**
     * Loads the remaining stream to the {@link #value} and update
     * {@link #index} and {@link #limit}, returns the current length
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    public int load()
        throws IOException {
        index = 0;
        return limit = -1;
    }

    /**
     * Reads a byte from this flow. if this flow has readable data,
     * the {@link #index} switches to next, otherwise returns {@code '0'}
     *
     * @throws IOException               If this has been closed or I/O error occurs
     * @throws IndexOutOfBoundsException If the index exceeds the length of internal buffer
     */
    public byte read()
        throws IOException {
        if (index < limit) {
            return value[index++];
        } else {
            if (load() < 1) {
                return 0;
            } else {
                return value[index++];
            }
        }
    }

    /**
     * Reads a byte from this flow. if this flow has readable data,
     * the {@link #index} switches to next, otherwise raise {@link IOException}
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    public byte next()
        throws IOException {
        if (index < limit) {
            return value[index++];
        } else {
            if (load() > 0) {
                return value[index++];
            }
        }

        throw new IOException(
            "No more readable bytes, please " +
                "check whether this flow is damaged"
        );
    }

    /**
     * Check if this flow still has readable bytes, generally used with {@link #read()}
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    public boolean also()
        throws IOException {
        if (index < limit) {
            return true;
        } else {
            return load() > 0;
        }
    }

    /**
     * Skip over and discards exactly bytes of the specified length from this {@link Flow}
     *
     * @throws IOException If this has been closed or I/O error occurs
     */
    public boolean skip(int i)
        throws IOException {
        if (i > 0) {
            int valve = limit - index;
            while (i > valve) {
                if (load() > 0) {
                    if (valve > 0) {
                        i -= valve;
                    }
                    valve = limit - index;
                } else {
                    throw new EOFException(
                        "Failed to skip exactly"
                    );
                }
            }
            index += i;
            return true;
        } else return false;
    }

    /**
     * Configure to use the feature
     *
     * @param flag the specified flag code
     */
    public Flow with(
        @NotNull long flag
    ) {
        flags |= flag;
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
        return (flags & flag) == flag;
    }

    /**
     * Closes this flow and releases
     * the resources associated with it
     */
    public void close() {
        limit = -1;
        value = null;
    }

    /**
     * Returns the literal value of this
     * {@link Flow} and marks the current index
     */
    @Override
    public String toString() {
        return Toolkit.print(
            value, index, limit
        );
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
     * Returns the {@link Flow} of the specified {@link ByteBuffer}
     *
     * @throws NullPointerException If the specified buffer is null
     */
    public static Flow of(
        @NotNull ByteBuffer text
    ) {
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
        return new CharBufferFlow(text);
    }

    /**
     * Returns a {@link Flow} where
     * calling {@link Reader#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *   try (Reader reader = ...) {
     *      Flow flow = Flow.of(reader);
     *   }
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
     *   try (InputStream stream = ...) {
     *      Flow flow = Flow.of(stream);
     *   }
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
     * Returns a {@link Flow} where
     * calling {@link InputStream#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *   Charset charset = ...
     *   try (InputStream stream = ...) {
     *      Flow flow = Flow.of(
     *          stream, charset
     *      );
     *   }
     * }</pre>
     *
     * @throws NullPointerException If the specified stream is null
     */
    public static Flow of(
        @NotNull InputStream text, @Nullable Charset charset
    ) {
        check:
        if (charset != null) {
            switch (charset.name()) {
                case "UTF-8":
                case "US-ASCII":
                case "ISO-8859-1": {
                    break check;
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
