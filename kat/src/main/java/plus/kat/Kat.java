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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.crash.*;
import plus.kat.spare.*;
import plus.kat.stream.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

import static plus.kat.Plan.DEF;
import static plus.kat.Supplier.Impl.INS;
import static plus.kat.stream.Binary.Unsafe.UPPER;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Kat extends Stream implements Chan {

    private static final byte
        LP = '(', RP = ')',
        LB = '{', RB = '}';

    protected Supplier supplier;

    /**
     * Constructs a default kat
     */
    public Kat() {
        this(0L, INS);
    }

    /**
     * Constructs a kat with the specified flags
     *
     * @param flags the specified flags
     */
    public Kat(
        long flags
    ) {
        this(flags, INS);
    }

    /**
     * Constructs a kat with the specified plan
     *
     * @param plan the specified plan
     */
    public Kat(
        @NotNull Plan plan
    ) {
        this(plan.writeFlags);
    }

    /**
     * Constructs a kat with the specified flags and supplier
     *
     * @param flags    the specified flags
     * @param supplier the specified supplier
     */
    public Kat(
        @NotNull long flags,
        @NotNull Supplier supplier
    ) {
        super(flags);
        this.supplier = supplier;
    }

    /**
     * Constructs a kat with the specified plan and supplier
     *
     * @param plan     the specified plan
     * @param supplier the specified supplier
     */
    public Kat(
        @NotNull Plan plan,
        @NotNull Supplier supplier
    ) {
        this(plan.writeFlags, supplier);
    }

    /**
     * Returns the algo of flow
     */
    @Override
    public Algo algo() {
        return Algo.KAT;
    }

    /**
     * Serializes the specified alias and value at the current hierarchy
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
     * Serializes the specified alias and value at the current hierarchy
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
            alias, "M", value
        );
    }

    /**
     * Serializes the specified alias, space and value at the current hierarchy
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
        short d = depth;
        if (d > 0) {
            int i = d + 1;
            byte[] it = grow(
                count + i * 2
            );
            it[count++] = '\n';
            while (--i != 0) {
                it[count++] = ' ';
                it[count++] = ' ';
            }
        }

        if (0 <= flags &&
            space != null) {
            emit(space);
        }

        if (alias != null) {
            join(
                (byte) ':'
            );
            emit(alias);
        }

        if (value == null) {
            join(LP);
            join(RP);
        } else {
            join(LB);
            if (d < 0) {
                value.serial(this);
            } else {
                ++depth;
                value.serial(this);
                --depth;
                if (d == 0) {
                    byte[] it = grow(
                        count + 2
                    );
                    it[count++] = '\n';
                } else {
                    int i = d + 1;
                    byte[] it = grow(
                        count + i * 2
                    );
                    it[count++] = '\n';
                    while (--i != 0) {
                        it[count++] = ' ';
                        it[count++] = ' ';
                    }
                }
            }
            join(RB);
        }
        return true;
    }

    /**
     * Serializes the specified alias, coder and value at the current hierarchy
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
                alias, "$", null
            );
        }

        if (coder == null) {
            // search for the spare of value
            coder = supplier.lookup(
                value.getClass()
            );

            // solving the coder problem again
            if (coder == null) {
                if (value instanceof Entity) {
                    return set(
                        alias, (Entity) value
                    );
                }

                if (value instanceof Optional) {
                    Optional<?> o = (Optional<?>) value;
                    return set(
                        alias, null, o.orElse(null)
                    );
                }

                if (value instanceof Throwable) {
                    coder = ErrorCoder.INSTANCE;
                } else if (value instanceof Map) {
                    coder = MapSpare.INSTANCE;
                } else if (value instanceof Set) {
                    coder = SetSpare.INSTANCE;
                } else if (value instanceof Iterable) {
                    coder = ListSpare.INSTANCE;
                } else {
                    return set(
                        alias, "$", null
                    );
                }
            }
        }

        short d = depth;
        if (d > 0) {
            int i = d + 1;
            byte[] it = grow(
                count + i * 2
            );
            it[count++] = '\n';
            while (--i != 0) {
                it[count++] = ' ';
                it[count++] = ' ';
            }
        }

        if (0 <= flags) {
            emit(
                coder.getSpace()
            );
        }

        if (alias != null) {
            join(
                (byte) ':'
            );
            emit(alias);
        }

        if (coder.getFlag() == null) {
            join(LP);
            coder.write(
                (Flow) this, value
            );
            join(RP);
        } else {
            join(LB);
            if (d < 0) {
                coder.write(
                    (Chan) this, value
                );
            } else {
                ++depth;
                coder.write(
                    (Chan) this, value
                );
                --depth;
                if (d == 0) {
                    byte[] it = grow(
                        count + 2
                    );
                    it[count++] = '\n';
                } else {
                    int i = d + 1;
                    byte[] it = grow(
                        count + i * 2
                    );
                    it[count++] = '\n';
                    while (--i != 0) {
                        it[count++] = ' ';
                        it[count++] = ' ';
                    }
                }
            }
            join(RB);
        }
        return true;
    }

    /**
     * Returns the internal {@link Flow}
     */
    @Override
    public Flow getFlow() {
        return this;
    }

    /**
     * Returns the internal {@link Supplier}
     */
    @Override
    public Supplier getSupplier() {
        return supplier;
    }

    /**
     * Concatenates the byte value to this {@link Kat},
     * which will be escaped if it is a special character
     *
     * @param bt the specified byte value to be appended
     */
    @Override
    public void emit(
        byte bt
    ) throws IOException {
        byte[] it = value;
        switch (bt) {
            case '^':
            case '(':
            case ')': {
                it = grow(
                    count + 2
                );
                it[count++] = '^';
            }
        }
        if (count != it.length) {
            asset = 0;
            it[count++] = bt;
        } else {
            asset = 0;
            grow(count + 1)[count++] = bt;
        }
    }

    /**
     * Concatenates the string representation
     * of the boolean value to this {@link Kat}
     *
     * @param bt the specified boolean to be appended
     */
    @Override
    public void emit(
        boolean bt
    ) throws IOException {
        byte[] it = value;
        if (count == it.length) {
            it = grow(count + 1);
        }
        asset = 0;
        it[count++] = bt ? (byte) '1' : (byte) '0';
    }

    /**
     * Concatenates the string to this {@link Kat},
     * which will be escaped if it is a special character
     *
     * @param data the specified string to be appended
     */
    private void emit(
        @NotNull String data
    ) throws IOException {
        asset = 0;
        byte[] it = value;

        int l = data.length();
        for (int i = 0; i < l; i++) {
            char ch = data.charAt(i);
            switch (ch) {
                case 0x23:
                case 0x28:
                case 0x29:
                case 0x3A:
                case 0x5E:
                case 0x7B:
                case 0x7D: {
                    break;
                }
                case 0x09: {
                    ch = 't';
                    break;
                }
                case 0x0A: {
                    ch = 'n';
                    break;
                }
                case 0x0D: {
                    ch = 'r';
                    break;
                }
                case 0x20: {
                    ch = 's';
                    break;
                }
                case 0x21:
                case 0x22:
                case 0x24:
                case 0x25:
                case 0x26:
                case 0x27:
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
                case 0x40:
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
                case 0x5B:
                case 0x5C:
                case 0x5D:
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
                    if (count == it.length)
                        it = grow(count + 1);
                    it[count++] = (byte) ch;
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
                case 0x08:
                case 0x0B:
                case 0x0C:
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
                    int size = count + 6;
                    if (size > it.length) {
                        it = grow(size);
                    }
                    it[count++] = '^';
                    it[count++] = 'u';
                    it[count++] = '0';
                    it[count++] = '0';
                    it[count++] = UPPER[(ch >> 4) & 0x0F];
                    it[count++] = UPPER[ch & 0x0F];
                    continue;
                }
                default: {
                    emit(ch);
                    it = value;
                    continue;
                }
            }

            int size = count + 2;
            if (size > it.length) {
                it = grow(size);
            }
            it[count++] = '^';
            it[count++] = (byte) ch;
        }
    }

    /**
     * Serializes to pure {@link Kat} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     * @since 0.0.5
     */
    @NotNull
    public static String pure(
        @Nullable Object value
    ) {
        return encode(
            null, value, Flag.PURE | DEF.writeFlags
        );
    }

    /**
     * Serializes to pretty {@link Kat} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     */
    @NotNull
    public static String pretty(
        @Nullable Object value
    ) {
        return encode(
            null, value, Flag.PRETTY | DEF.writeFlags
        );
    }

    /**
     * Serializes to {@link Kat} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
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
     * Serializes to {@link Kat} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
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
     * Serializes to {@link Kat} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
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
     * Serializes to {@link Kat} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
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
            throw new FatalCrash(
                "Unexpectedly, error serializing to kat", e
            );
        }
    }

    /**
     * Resolves the {@link Kat} byte array
     *
     * @param text the specified text to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Class<T> klass,
        @Nullable byte[] text
    ) {
        if (text == null ||
            klass == null) {
            return null;
        }

        return INS.read(
            klass, new Event<>(text)
        );
    }

    /**
     * Resolves the {@link Kat} char array
     *
     * @param text the specified text to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Class<T> klass,
        @Nullable char[] text
    ) {
        if (text == null ||
            klass == null) {
            return null;
        }

        return INS.read(
            klass, new Event<>(text)
        );
    }

    /**
     * Resolves the {@link Kat} {@link Paper}
     *
     * @param paper the specified paper to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Class<T> klass,
        @Nullable Paper paper
    ) {
        if (paper == null ||
            klass == null) {
            return null;
        }

        return INS.read(
            klass, new Event<>(paper)
        );
    }

    /**
     * Resolves the {@link Kat} {@link Reader}
     * where calling {@link Reader#close()} has no effect
     *
     * @param reader the specified reader to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Class<T> klass,
        @Nullable Reader reader
    ) {
        if (klass == null ||
            reader == null) {
            return null;
        }

        return INS.read(
            klass, new Event<>(reader)
        );
    }

    /**
     * Resolves the {@link Kat} {@link CharSequence}
     *
     * @param text the specified text to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Class<T> klass,
        @Nullable CharSequence text
    ) {
        if (text == null ||
            klass == null) {
            return null;
        }

        return INS.read(
            klass, new Event<>(text)
        );
    }

    /**
     * Resolves the {@link Kat} {@link ByteBuffer}
     *
     * @param buffer the specified buffer to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Class<T> klass,
        @Nullable ByteBuffer buffer
    ) {
        if (klass == null ||
            buffer == null) {
            return null;
        }

        return INS.read(
            klass, new Event<>(buffer)
        );
    }

    /**
     * Resolves the {@link Kat} {@link InputStream}
     * where calling {@link InputStream#close()} has no effect
     *
     * @param stream the specified stream to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Class<T> klass,
        @Nullable InputStream stream
    ) {
        if (klass == null ||
            stream == null) {
            return null;
        }

        return INS.read(
            klass, new Event<>(stream)
        );
    }

    /**
     * Resolves the {@link Kat} {@link Event}
     *
     * @param event the specified event to be handled
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     */
    @Nullable
    public static <E, T extends E> T decode(
        @Nullable Class<E> klass,
        @Nullable Event<T> event
    ) {
        if (klass == null ||
            event == null) {
            return null;
        } else {
            return INS.read(
                klass, event
            );
        }
    }
}
