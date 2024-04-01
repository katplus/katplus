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
public class Doc extends Stream implements Chan {

    public static final byte
        LT = '<', GT = '>',
        SOL = '/', NUM = '#',
        AMP = '&', QUOT = '"',
        APOS = '\'', EQUAL = '=';

    protected Context context;

    /**
     * Constructs a xml with the default flags and context
     */
    public Doc() {
        this(0, INS);
    }

    /**
     * Constructs a xml with the flags and default context
     *
     * @param flags the specified flags
     */
    public Doc(
        long flags
    ) {
        this(flags, INS);
    }

    /**
     * Constructs a xml with the specified context and default plan
     *
     * @param context the specified context
     */
    public Doc(
        @NotNull Context context
    ) {
        this(0, context);
    }

    /**
     * Constructs a xml with the flags and specified context
     *
     * @param flags   the specified flags
     * @param context the specified context
     */
    public Doc(
        @NotNull long flags,
        @NotNull Context context
    ) {
        super(flags);
        if (context != null) {
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
        if (alias == null) {
            if (space != null) {
                alias = space;
            } else {
                if (value == null) {
                    return false;
                }
                alias = value.getClass().getName();
            }
        }

        int width = depth;
        if (width != 0) {
            ++depth;
            if (width != 1) {
                int len = width;
                byte[] it = grow(
                    size + len * 2
                );
                it[size++] = '\n';
                while (--len != 0) {
                    it[size++] = ' ';
                    it[size++] = ' ';
                }
            }
        }

        join(LT);
        state = 1;
        int mark1 = size;
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
        int mark2 = size - mark1;
        state = 0;
        join(GT);

        if (value != null) {
            value.accept(this);
        }

        if (width != 0) {
            --depth;
            if (width == 1 ||
                value == null) {
                grow(size + 1)
                    [size++] = '\n';
            } else {
                byte[] it = grow(
                    size + width * 2
                );
                it[size++] = '\n';
                while (--width != 0) {
                    it[size++] = ' ';
                    it[size++] = ' ';
                }
            }
        }

        join(LT);
        join(SOL);
        byte[] it = grow(
            size + mark2
        );
        System.arraycopy(
            it, mark1, it, size, mark2
        );
        size += mark2;
        join(GT);
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

        Boolean scope = coder.getScope();
        if (alias == null) {
            alias = coder.getSpace();
            if (alias == null) {
                alias = value.getClass().getName();
            }
        }

        int width = depth;
        if (width != 0) {
            if (scope != null) {
                ++depth;
            }
            if (width != 1) {
                int len = width;
                byte[] it = grow(
                    size + len * 2
                );
                it[size++] = '\n';
                while (--len != 0) {
                    it[size++] = ' ';
                    it[size++] = ' ';
                }
            }
        }

        join(LT);
        state = 1;
        int mark1 = size;
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
        int mark2 = size - mark1;
        state = 0;
        join(GT);

        if (scope == null) {
            coder.write(
                (Flux) this, value
            );
        } else {
            coder.write(
                (Chan) this, value
            );
            if (width != 0) {
                --depth;
                if (width == 1) {
                    grow(size + 1)
                        [size++] = '\n';
                } else {
                    byte[] it = grow(
                        size + width * 2
                    );
                    it[size++] = '\n';
                    while (--width != 0) {
                        it[size++] = ' ';
                        it[size++] = ' ';
                    }
                }
            }
        }

        join(LT);
        join(SOL);
        byte[] it = grow(
            size + mark2
        );
        System.arraycopy(
            it, mark1, it, size, mark2
        );
        size += mark2;
        join(GT);
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
     * Concatenates the value to this flux
     *
     * @param val the specified byte value
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void emit(byte val)
        throws IOException {
        switch (val) {
            case '<': {
                byte[] it = grow(
                    size + 4
                );
                it[size++] = '&';
                it[size++] = 'l';
                it[size++] = 't';
                it[size++] = ';';
                return;
            }
            case '>': {
                byte[] it = grow(
                    size + 4
                );
                it[size++] = '&';
                it[size++] = 'g';
                it[size++] = 't';
                it[size++] = ';';
                return;
            }
            case '&': {
                byte[] it = grow(
                    size + 5
                );
                it[size++] = '&';
                it[size++] = 'a';
                it[size++] = 'm';
                it[size++] = 'p';
                it[size++] = ';';
                return;
            }
            case '"': {
                if (state == 0) {
                    break;
                }
                byte[] it = grow(
                    size + 6
                );
                it[size++] = '&';
                it[size++] = 'q';
                it[size++] = 'u';
                it[size++] = 'o';
                it[size++] = 't';
                it[size++] = ';';
                return;
            }
            case '/': {
                if (state == 0) {
                    break;
                }
                byte[] it = grow(
                    size + 5
                );
                it[size++] = '&';
                it[size++] = 's';
                it[size++] = 'o';
                it[size++] = 'l';
                it[size++] = ';';
                return;
            }
            case ' ': {
                if (state == 0) {
                    break;
                }
                byte[] it = grow(
                    size + 6
                );
                it[size++] = '&';
                it[size++] = 'n';
                it[size++] = 'b';
                it[size++] = 's';
                it[size++] = 'p';
                it[size++] = ';';
                return;
            }
            case '=': {
                if (state == 0) {
                    break;
                }
                byte[] it = grow(
                    size + 7
                );
                it[size++] = '&';
                it[size++] = 'e';
                it[size++] = 'q';
                it[size++] = 'u';
                it[size++] = 'a';
                it[size++] = 'l';
                it[size++] = ';';
                return;
            }
        }
        byte[] it = value;
        if (size != it.length) {
            it[size++] = val;
        } else {
            grow(size + 1)[size++] = val;
        }
    }

    /**
     * Encodes the object to xml {@link Chan}
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
     * Encodes the entity to xml {@link Chan}
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
     * Encodes the object to xml {@link Chan}
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
     * Encodes the entity to xml {@link Chan}
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
     * Encodes the object to xml {@link Chan}
     *
     * @param flags the specified flags
     * @param value the specified value to be encoded
     * @throws IOException If an I/O error or analysis error occurs
     */
    @NotNull
    public static Chan encode(
        @Nullable Object value, long flags
    ) throws IOException {
        return INS.mark(
            value, flags
        );
    }

    /**
     * Encodes the entity to xml {@link Chan}
     *
     * @param flags the specified flags
     * @param value the specified value to be encoded
     * @throws IOException If an I/O error or analysis error occurs
     */
    @NotNull
    public static Chan encode(
        @Nullable Entity value, long flags
    ) throws IOException {
        return INS.mark(
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

        return INS.down(type, flow);
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
            type, Flow.of(stream)
        );
    }
}
