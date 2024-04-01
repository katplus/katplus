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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.flow.*;
import plus.kat.lang.*;
import plus.kat.spare.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.lang.reflect.Type;

import static plus.kat.spare.Supplier.Vendor.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Kat extends Stream implements Chan {

    public static final byte
        LC = '{', RC = '}',
        LB = '[', RB = ']';

    protected boolean head;
    protected Context context;

    /**
     * Constructs a kat with the default flags and context
     */
    public Kat() {
        this(0, INS);
    }

    /**
     * Constructs a kat with the flags and default context
     *
     * @param flags the specified flags
     */
    public Kat(
        long flags
    ) {
        this(flags, INS);
    }

    /**
     * Constructs a kat with the specified context and default plan
     *
     * @param context the specified context
     */
    public Kat(
        @NotNull Context context
    ) {
        this(0, context);
    }

    /**
     * Constructs a kat with the flags and specified context
     *
     * @param flags   the specified flags
     * @param context the specified context
     */
    public Kat(
        @NotNull long flags,
        @NotNull Context context
    ) {
        super(flags);
        if (context != null) {
            this.head = true;
            this.context = context;
        } else {
            throw new NullPointerException(
                "Received context is null"
            );
        }
    }

    /**
     * Encodes the specified alias
     * and value at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable Object alias,
        @Nullable Object value
    ) throws IOException {
        return set(
            alias, null, value
        );
    }

    /**
     * Encodes the specified alias
     * and value at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable Object alias,
        @Nullable Entity value
    ) throws IOException {
        return set(
            alias, "Map", value
        );
    }

    /**
     * Encodes the specified alias,
     * space and value at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable Object alias,
        @Nullable String space,
        @Nullable Entity value
    ) throws IOException {
        if (head) {
            head = false;
        } else {
            join((byte) ',');
        }

        int wide = depth;
        if (wide > 1) {
            int iv = wide;
            byte[] it = grow(
                size + iv * 2
            );
            it[size++] = '\n';
            while (--iv != 0) {
                it[size++] = ' ';
                it[size++] = ' ';
            }
        }

        if (alias != null) {
            state = 1;
            if (alias instanceof String) {
                emit((String) alias);
            } else if (alias instanceof Binary) {
                emit((Binary) alias);
            } else {
                Spare<?> spare = context.assign(
                    alias.getClass()
                );
                if (spare != null) {
                    spare.write((Flux) this, alias);
                } else {
                    throw new IOException(
                        "No spare of " + alias
                            .getClass() + " was found"
                    );
                }
            }
            state = 0;
        }

        if (flags < 0) {
            if (alias == null) {
                join((byte) '@');
            } else {
                join((byte) ':');
            }
            state = 1;
            if (value == null) {
                emit("Any");
            } else {
                if (space == null) {
                    emit("Map");
                } else {
                    emit(space);
                }
            }
            state = 0;
            if (alias == null && (
                wide != 0 ||
                    value == null)) {
                join((byte) ' ');
            }
        }

        if (alias != null) {
            if (wide == 0) {
                join((byte) '=');
            } else {
                join((byte) ' ');
                join((byte) '=');
                join((byte) ' ');
            }
        }

        if (value == null) {
            byte[] it = grow(
                size + 4
            );
            it[size++] = 'n';
            it[size++] = 'u';
            it[size++] = 'l';
            it[size++] = 'l';
        } else {
            join(LC);
            if (wide == 0) {
                head = true;
                value.accept(this);
            } else {
                ++depth;
                head = true;
                value.accept(this);
                --depth;
                if (wide == 1) {
                    byte[] it = grow(
                        size + 2
                    );
                    it[size++] = '\n';
                } else {
                    byte[] it = grow(
                        size + wide * 2
                    );
                    it[size++] = '\n';
                    while (--wide != 0) {
                        it[size++] = ' ';
                        it[size++] = ' ';
                    }
                }
            }
            join(RC);
        }
        return true;
    }

    /**
     * Encodes the specified alias,
     * coder and value at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable Object alias,
        @Nullable Coder<?> coder,
        @Nullable Object value
    ) throws IOException {
        if (value == null) {
            return set(
                alias, "Any", null
            );
        }

        if (coder == null) {
            // search for the spare of value
            coder = context.assign(
                value.getClass()
            );

            // solving the coder problem again
            if (coder == null) {
                if (value instanceof Map) {
                    coder = MapSpare.INSTANCE;
                } else if (value instanceof Set) {
                    coder = SetSpare.INSTANCE;
                } else if (value instanceof List) {
                    coder = ListSpare.INSTANCE;
                } else if (value instanceof Entity) {
                    return set(
                        alias, (Entity) value
                    );
                } else {
                    return ObjectSpare.write(
                        this, alias, value
                    );
                }
            }
        }

        if (head) {
            head = false;
        } else {
            join((byte) ',');
        }

        int wide = depth;
        if (wide > 1) {
            int iv = wide;
            byte[] it = grow(
                size + iv * 2
            );
            it[size++] = '\n';
            while (--iv != 0) {
                it[size++] = ' ';
                it[size++] = ' ';
            }
        }

        if (alias != null) {
            state = 1;
            if (alias instanceof String) {
                emit((String) alias);
            } else if (alias instanceof Binary) {
                emit((Binary) alias);
            } else {
                Spare<?> spare = context.assign(
                    alias.getClass()
                );
                if (spare != null) {
                    spare.write((Flux) this, alias);
                } else {
                    throw new IOException(
                        "No spare of " + alias
                            .getClass() + " was found"
                    );
                }
            }
            state = 0;
        }

        if (flags < 0) {
            if (alias == null) {
                join((byte) '@');
            } else {
                join((byte) ':');
            }
            String name = coder.getSpace();
            state = 1;
            if (name != null) {
                emit(name);
            } else {
                emit(value.getClass().getName());
            }
            state = 0;
            if (alias == null && (
                wide != 0 ||
                    coder.getScope() == null)) {
                join((byte) ' ');
            }
        }

        if (alias != null) {
            if (wide == 0) {
                join((byte) '=');
            } else {
                join((byte) ' ');
                join((byte) '=');
                join((byte) ' ');
            }
        }

        Border border =
            coder.getBorder(this);
        if (border == null) {
            state = 1;
            coder.write(
                (Flux) this, value
            );
            state = 0;
            return true;
        }

        byte left, right;
        switch (border) {
            case QUOTE: {
                join((byte) '"');
                coder.write(
                    (Flux) this, value
                );
                join((byte) '"');
                return true;
            }
            case BRACE: {
                left = LC;
                right = RC;
                break;
            }
            case BRACKET: {
                left = LB;
                right = RB;
                break;
            }
            default: {
                return false;
            }
        }

        head = true;
        join(left);
        if (wide == 0) {
            coder.write(
                (Chan) this, value
            );
        } else {
            ++depth;
            coder.write(
                (Chan) this, value
            );
            --depth;
            if (wide == 1) {
                byte[] it = grow(
                    size + 2
                );
                it[size++] = '\n';
            } else {
                byte[] it = grow(
                    size + wide * 2
                );
                it[size++] = '\n';
                while (--wide != 0) {
                    it[size++] = ' ';
                    it[size++] = ' ';
                }
            }
        }
        join(right);
        return true;
    }

    /**
     * Returns the {@link Flux} of chan
     */
    @Override
    public Flux getFlux() {
        return this;
    }

    /**
     * Returns the {@link Context} of chan
     */
    @Override
    public Context getContext() {
        return context;
    }

    /**
     * Encodes the object to kat {@link Chan}
     *
     * @param value the specified value to be encoded
     * @throws IOException If an I/O error or analysis error occurs
     */
    @NotNull
    public static Chan encode(
        @Nullable Object value
    ) throws IOException {
        return encode(
            value, 0L
        );
    }

    /**
     * Encodes the entity to kat {@link Chan}
     *
     * @param value the specified value to be encoded
     * @throws IOException If an I/O error or analysis error occurs
     */
    @NotNull
    public static Chan encode(
        @Nullable Entity value
    ) throws IOException {
        return encode(
            value, 0L
        );
    }

    /**
     * Encodes the object to kat {@link Chan}
     *
     * @param value the specified value to be encoded
     * @throws IOException If an I/O error or analysis error occurs
     */
    @NotNull
    public static Chan pretty(
        @Nullable Object value
    ) throws IOException {
        return encode(
            value, PRETTY
        );
    }

    /**
     * Encodes the entity to kat {@link Chan}
     *
     * @param value the specified value to be encoded
     * @throws IOException If an I/O error or analysis error occurs
     */
    @NotNull
    public static Chan pretty(
        @Nullable Entity value
    ) throws IOException {
        return encode(
            value, PRETTY
        );
    }

    /**
     * Encodes the object to kat {@link Chan}
     *
     * @param value the specified value to be encoded
     * @throws IOException If an I/O error or analysis error occurs
     */
    @NotNull
    public static Chan encode(
        @Nullable Object value, long flags
    ) throws IOException {
        return INS.write(
            value, flags
        );
    }

    /**
     * Encodes the entity to kat {@link Chan}
     *
     * @param value the specified value to be encoded
     * @throws IOException If an I/O error or analysis error occurs
     */
    @NotNull
    public static Chan encode(
        @Nullable Entity value, long flags
    ) throws IOException {
        return INS.write(
            value, flags
        );
    }

    /**
     * Decodes the {@link Flow} and converts the result to {@link T}
     *
     * @param flow the specified flow to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable Flow flow
    ) throws IOException {
        if (type == null ||
            flow == null) {
            return null;
        }

        return INS.read(type, flow);
    }

    /**
     * Decodes the byte array and converts the result to {@link T}
     *
     * @param text the specified text to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable byte[] text
    ) throws IOException {
        if (type == null ||
            text == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(text)
        );
    }

    /**
     * Decodes the char array and converts the result to {@link T}
     *
     * @param text the specified text to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable char[] text
    ) throws IOException {
        if (type == null ||
            text == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(text)
        );
    }

    /**
     * Decodes the {@link Binary} and converts the result to {@link T}
     *
     * @param text the specified text to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable Binary text
    ) throws IOException {
        if (type == null ||
            text == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(text)
        );
    }

    /**
     * Decodes the {@link String} and converts the result to {@link T}
     *
     * @param text the specified text to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable String text
    ) throws IOException {
        if (type == null ||
            text == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(text)
        );
    }

    /**
     * Decodes the {@link Reader} where
     * calling {@link Reader#close()} has no effect
     *
     * @param reader the specified reader to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable Reader reader
    ) throws IOException {
        if (type == null ||
            reader == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(reader)
        );
    }

    /**
     * Decodes the {@link ByteBuffer} and converts the result to {@link T}
     *
     * @param buffer the specified buffer to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable ByteBuffer buffer
    ) throws IOException {
        if (type == null ||
            buffer == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(buffer)
        );
    }

    /**
     * Decodes the {@link CharBuffer} and converts the result to {@link T}
     *
     * @param buffer the specified buffer to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable CharBuffer buffer
    ) throws IOException {
        if (type == null ||
            buffer == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(buffer)
        );
    }

    /**
     * Decodes the {@link InputStream} where
     * calling {@link InputStream#close()} has no effect
     *
     * @param stream the specified stream to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable InputStream stream
    ) throws IOException {
        if (type == null ||
            stream == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(stream)
        );
    }
}
