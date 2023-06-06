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

import plus.kat.spare.*;
import plus.kat.stream.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

import static plus.kat.Plan.DEF;
import static plus.kat.Supplier.Vendor.INS;
import static plus.kat.stream.Toolkit.HEX_UPPER;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Kat extends Stream implements Chan {

    private static final byte
        LB = '{', RB = '}',
        LP = '[', RP = ']';

    protected boolean blank;
    protected final Context context;

    /**
     * Constructs a kat with the default context
     */
    public Kat() {
        this(0L, INS);
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
     * Constructs a kat with the plan and default context
     *
     * @param plan the specified plan
     */
    public Kat(
        @NotNull Plan plan
    ) {
        this(plan.writeFlags);
    }

    /**
     * Constructs a kat with the plan and specified context
     *
     * @param plan    the specified plan
     * @param context the specified context
     */
    public Kat(
        @NotNull Plan plan,
        @NotNull Context context
    ) {
        this(plan.writeFlags, context);
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
            this.blank = true;
            this.context = context;
        } else {
            throw new NullPointerException(
                "Received context is null"
            );
        }
    }

    /**
     * Serializes the specified alias
     * and value at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable String alias,
        @Nullable Object value
    ) throws IOException {
        return set(
            alias, null, value
        );
    }

    /**
     * Serializes the specified alias
     * and value at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable String alias,
        @Nullable Entity value
    ) throws IOException {
        return set(
            alias, "Map", value
        );
    }

    /**
     * Serializes the specified alias,
     * space and value at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable String alias,
        @Nullable String space,
        @Nullable Entity value
    ) throws IOException {
        if (blank) {
            blank = false;
        } else {
            join(
                (byte) ','
            );
        }

        int width = depth;
        if (width > 1) {
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

        if (alias != null) {
            join(alias);
        }

        if (flags < 0 &&
            space != null) {
            if (alias == null) {
                join((byte) '@');
            } else {
                join((byte) ':');
            }
            join(space);
            if (width != 0 ||
                alias == null) {
                join((byte) ' ');
            }
        }

        if (alias != null) {
            if (width == 0) {
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
            join(LB);
            if (width == 0) {
                blank = true;
                value.accept(this);
            } else {
                ++depth;
                blank = true;
                value.accept(this);
                --depth;
                if (width == 1) {
                    byte[] it = grow(
                        size + 2
                    );
                    it[size++] = '\n';
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
            join(RB);
        }
        return true;
    }

    /**
     * Serializes the specified alias,
     * coder and value at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable String alias,
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
                if (value instanceof Entity) {
                    return set(
                        alias, (Entity) value
                    );
                } else if (value instanceof Map) {
                    coder = MapSpare.INSTANCE;
                } else if (value instanceof Set) {
                    coder = SetSpare.INSTANCE;
                } else if (value instanceof Iterable) {
                    coder = ListSpare.INSTANCE;
                } else if (value instanceof Optional) {
                    Optional<?> o = (Optional<?>) value;
                    return set(
                        alias, null, o.orElse(null)
                    );
                } else if (value instanceof Throwable) {
                    coder = FaultCoder.INSTANCE;
                } else {
                    throw new IOException(
                        "No available coder for `"
                            + value.getClass() + "` was found"
                    );
                }
            }
        }

        if (blank) {
            blank = false;
        } else {
            join(
                (byte) ','
            );
        }

        int width = depth;
        if (width > 1) {
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

        if (alias != null) {
            join(alias);
        }

        if (flags < 0) {
            if (alias == null) {
                join((byte) '@');
            } else {
                join((byte) ':');
            }
            String name = coder.getSpace();
            if (name != null) {
                join(name);
            } else {
                join(value.getClass().getName());
            }
            if (alias == null &&
                (width != 0 || coder.getScope() == null)) {
                join((byte) ' ');
            }
        }

        if (alias != null) {
            if (width == 0) {
                join((byte) '=');
            } else {
                join((byte) ' ');
                join((byte) '=');
                join((byte) ' ');
            }
        }

        Border border = coder.getBorder(this);
        if (border == null) {
            coder.write(
                (Flux) this, value
            );
            return true;
        }

        boolean brace = false;
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
                brace = true;
            }
            case BRACKET: {
                blank = true;
                join(brace ? LB : LP);
                if (width == 0) {
                    coder.write(
                        (Chan) this, value
                    );
                } else {
                    ++depth;
                    coder.write(
                        (Chan) this, value
                    );
                    --depth;
                    if (width == 1) {
                        byte[] it = grow(
                            size + 2
                        );
                        it[size++] = '\n';
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
                join(brace ? RB : RP);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the internal {@link Flux}
     */
    @Override
    public Flux getFlux() {
        return this;
    }

    /**
     * Returns the internal {@link Context}
     */
    @Override
    public Context getContext() {
        return context;
    }

    /**
     * Concatenates the string to the current content
     *
     * @param data the specified string to be appended
     */
    private void join(
        @NotNull String data
    ) throws IOException {
        int l = data.length();
        byte[] it = value;

        for (int i = 0; i < l; i++) {
            char cutty = data.charAt(i);
            switch (cutty) {
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
                    cutty = 'b';
                    break;
                }
                case 0x09: {
                    cutty = 't';
                    break;
                }
                case 0x0A: {
                    cutty = 'n';
                    break;
                }
                case 0x0C: {
                    cutty = 'f';
                    break;
                }
                case 0x0D: {
                    cutty = 'r';
                    break;
                }
                case 0x20: {
                    cutty = 's';
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
                        it = grow(size + 1);
                    }
                    it[size++] = (byte) cutty;
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
                        value = it
                            = bucket.apply(
                            it, size, min
                        );
                    }
                    it[size++] = '\\';
                    it[size++] = 'u';
                    it[size++] = '0';
                    it[size++] = '0';
                    it[size++] = HEX_UPPER[(cutty >> 4) & 0x0F];
                    it[size++] = HEX_UPPER[cutty & 0x0F];
                    continue;
                }
                default: {
                    emit(cutty);
                    it = value;
                    continue;
                }
            }

            int min = size + 2;
            if (min > it.length) {
                value = it
                    = bucket.apply(
                    it, size, min
                );
            }
            it[size++] = (byte) '\\';
            it[size++] = (byte) cutty;
        }
    }

    /**
     * Serializes to pretty kat String
     *
     * @param value the specified value to serialized
     * @throws IllegalArgumentException If an error occurs in serialization
     */
    @NotNull
    public static String pretty(
        @Nullable Object value
    ) {
        return encode(
            null, value, PRETTY | DEF.writeFlags
        );
    }

    /**
     * Serializes to kat String
     *
     * @param value the specified value to serialized
     * @throws IllegalArgumentException If an error occurs in serialization
     */
    @NotNull
    public static String encode(
        @Nullable Object value
    ) {
        return encode(
            null, value, DEF.writeFlags
        );
    }

    /**
     * Serializes to kat String
     *
     * @param value the specified value to serialized
     * @throws IllegalArgumentException If an error occurs in serialization
     */
    @NotNull
    public static String encode(
        @Nullable Object value, long flags
    ) {
        return encode(
            null, value, flags
        );
    }

    /**
     * Serializes to kat String
     *
     * @param value the specified value to serialized
     * @throws IllegalArgumentException If an error occurs in serialization
     */
    @NotNull
    public static String encode(
        @Nullable String alias,
        @Nullable Object value
    ) {
        return encode(
            alias, value, DEF.writeFlags
        );
    }

    /**
     * Serializes to kat String
     *
     * @param value the specified value to serialized
     * @throws IllegalArgumentException If an error occurs in serialization
     */
    @NotNull
    public static String encode(
        @Nullable String alias,
        @Nullable Object value, long flags
    ) {
        try (Chan chan = new Kat(flags)) {
            chan.set(
                alias, value
            );
            return chan.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Failed to serialize into kat", e
            );
        }
    }

    /**
     * Resolves the {@link Flow} and converts the result to {@link T}
     *
     * @param flow the specified flow to be parsed
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable Flow flow
    ) {
        if (type == null ||
            flow == null) {
            return null;
        }

        return INS.read(
            type, flow.with(DEF.readFlags)
        );
    }

    /**
     * Resolves the byte array and converts the result to {@link T}
     *
     * @param text the specified text to be parsed
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable byte[] text
    ) {
        if (type == null ||
            text == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(text).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the char array and converts the result to {@link T}
     *
     * @param text the specified text to be parsed
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable char[] text
    ) {
        if (type == null ||
            text == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(text).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link Binary} and converts the result to {@link T}
     *
     * @param text the specified text to be parsed
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable Binary text
    ) {
        if (type == null ||
            text == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(text).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link String} and converts the result to {@link T}
     *
     * @param text the specified text to be parsed
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable String text
    ) {
        if (type == null ||
            text == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(text).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link Reader} where
     * calling {@link Reader#close()} has no effect
     *
     * @param reader the specified reader to be parsed
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable Reader reader
    ) {
        if (type == null ||
            reader == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(reader).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link ByteBuffer} and converts the result to {@link T}
     *
     * @param buffer the specified buffer to be parsed
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable ByteBuffer buffer
    ) {
        if (type == null ||
            buffer == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(buffer).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link CharBuffer} and converts the result to {@link T}
     *
     * @param buffer the specified buffer to be parsed
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable CharBuffer buffer
    ) {
        if (type == null ||
            buffer == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(buffer).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link InputStream} where
     * calling {@link InputStream#close()} has no effect
     *
     * @param stream the specified stream to be parsed
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Type type,
        @Nullable InputStream stream
    ) {
        if (type == null ||
            stream == null) {
            return null;
        }

        return INS.read(
            type, Flow.of(stream).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the kat task and converts the result to {@link T}
     *
     * @param event the specified event to be handled
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the event is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Event<T> event
    ) {
        if (event == null) {
            return null;
        }

        Type type = event.type;
        Flow flow = event.flow;

        if (type == null ||
            flow == null) {
            return null;
        }

        return INS.read(
            type, flow.with(DEF.readFlags)
        );
    }
}
