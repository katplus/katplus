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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.lang.reflect.Type;

import static plus.kat.Plan.DEF;
import static plus.kat.Supplier.Vendor.INS;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Doc extends Stream implements Chan {

    public static final byte
        LT = '<', GT = '>',
        AMP = '&', QUOT = '"',
        APOS = '\'', SLASH = '/';

    protected final Context context;

    /**
     * Constructs a xml with the default context
     */
    public Doc() {
        this(0L, INS);
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
     * Constructs a xml with the plan and default context
     *
     * @param plan the specified plan
     */
    public Doc(
        @NotNull Plan plan
    ) {
        this(plan.writeFlags);
    }

    /**
     * Constructs a xml with the plan and specified context
     *
     * @param plan    the specified plan
     * @param context the specified context
     */
    public Doc(
        @NotNull Plan plan,
        @NotNull Context context
    ) {
        this(plan.writeFlags, context);
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
            alias, "items", value
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
        if (alias == null) {
            if (space == null) {
                return false;
            } else {
                alias = space;
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
        emit(alias);
        join(GT);

        if (value != null) {
            value.accept(this);
        }

        if (width != 0) {
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

        join(LT);
        join(SLASH);
        emit(alias);
        join(GT);
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
                alias, "item", null
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
                } else {
                    return Toolkit.set(
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
        emit(alias);
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
        }

        join(LT);
        join(SLASH);
        emit(alias);
        join(GT);
        return true;
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
     * Appends this byte to the current content
     *
     * @param bin the specified byte value
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void emit(
        byte bin
    ) throws IOException {
        switch (bin) {
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
                    size + 4
                );
                it[size++] = '&';
                it[size++] = 'a';
                it[size++] = 'm';
                it[size++] = 'p';
                it[size++] = ';';
                return;
            }
            default: {
                byte[] it = value;
                if (size != it.length) {
                    it[size++] = bin;
                } else {
                    grow(size + 1)[size++] = bin;
                }
            }
        }
    }

    /**
     * Serializes to pretty xml String
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
     * Serializes to xml String
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
     * Serializes to xml String
     *
     * @param value the specified value to serialized
     * @throws IllegalArgumentException If an error occurs in serialization
     * @since 0.0.2
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
     * Serializes to xml String
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
     * Serializes to xml String
     *
     * @param value the specified value to serialized
     * @throws IllegalArgumentException If an error occurs in serialization
     * @since 0.0.3
     */
    @NotNull
    public static String encode(
        @Nullable String alias,
        @Nullable Object value, long flags
    ) {
        try (Doc chan = new Doc(flags)) {
            chan.set(
                alias, value
            );
            return chan.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Failed to serialize into xml", e
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
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

        return INS.down(
            type, Flow.of(stream).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the xml task and converts the result to {@link T}
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

        return INS.down(
            type, flow.with(DEF.readFlags)
        );
    }
}
