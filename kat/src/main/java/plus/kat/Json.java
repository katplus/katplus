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
public class Json extends Stream implements Chan {

    private static final byte
        LB = '{', RB = '}',
        LP = '[', RP = ']';

    protected boolean blank;
    protected final Context context;

    /**
     * Constructs a json with the default context
     */
    public Json() {
        this(0L, INS);
    }

    /**
     * Constructs a json with the flags and default context
     *
     * @param flags the specified flags
     */
    public Json(
        long flags
    ) {
        this(flags, INS);
    }

    /**
     * Constructs a json with the plan and default context
     *
     * @param plan the specified plan
     */
    public Json(
        @NotNull Plan plan
    ) {
        this(plan.writeFlags);
    }

    /**
     * Constructs a json with the plan and specified context
     *
     * @param plan    the specified plan
     * @param context the specified context
     */
    public Json(
        @NotNull Plan plan,
        @NotNull Context context
    ) {
        this(plan.writeFlags, context);
    }

    /**
     * Constructs a json with the flags and specified context
     *
     * @param flags   the specified flags
     * @param context the specified context
     */
    public Json(
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

        int dep = depth;
        if (dep > 1) {
            int i = dep;
            byte[] it = grow(
                size + i * 2
            );
            it[size++] = '\n';
            while (--i != 0) {
                it[size++] = ' ';
                it[size++] = ' ';
            }
        }

        if (alias != null) {
            join((byte) '"');
            emit(alias);
            join((byte) '"');
            join((byte) ':');
            if (dep != 0) {
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
            return true;
        }

        join(LB);
        blank = true;
        if (dep == 0) {
            value.accept(this);
        } else {
            ++depth;
            value.accept(this);
            --depth;
            if (dep == 1) {
                byte[] it = grow(
                    size + 2
                );
                it[size++] = '\n';
            } else {
                int i = dep;
                byte[] it = grow(
                    size + i * 2
                );
                it[size++] = '\n';
                while (--i != 0) {
                    it[size++] = ' ';
                    it[size++] = ' ';
                }
            }
        }
        join(RB);
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

        if (blank) {
            blank = false;
        } else {
            join(
                (byte) ','
            );
        }

        int dep = depth;
        if (dep > 1) {
            int i = dep;
            byte[] it = grow(
                size + i * 2
            );
            it[size++] = '\n';
            while (--i != 0) {
                it[size++] = ' ';
                it[size++] = ' ';
            }
        }

        if (alias != null) {
            join((byte) '"');
            emit(alias);
            join((byte) '"');
            join((byte) ':');
            if (dep != 0) {
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
                if (dep == 0) {
                    coder.write(
                        (Chan) this, value
                    );
                } else {
                    ++depth;
                    coder.write(
                        (Chan) this, value
                    );
                    --depth;
                    if (dep == 1) {
                        byte[] it = grow(
                            size + 2
                        );
                        it[size++] = '\n';
                    } else {
                        int i = dep;
                        byte[] it = grow(
                            size + i * 2
                        );
                        it[size++] = '\n';
                        while (--i != 0) {
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
     * Serializes to pretty json String
     *
     * @param value the specified value to serialized
     * @throws IllegalArgumentException If an error occurs in serialization
     */
    @NotNull
    public static String pretty(
        @Nullable Object value
    ) {
        return encode(
            value, PRETTY | DEF.writeFlags
        );
    }

    /**
     * Serializes to json String
     *
     * @param value the specified value to serialized
     * @throws IllegalArgumentException If an error occurs in serialization
     */
    @NotNull
    public static String encode(
        @Nullable Object value
    ) {
        return encode(
            value, DEF.writeFlags
        );
    }

    /**
     * Serializes to json String
     *
     * @param value the specified value to serialized
     * @throws IllegalArgumentException If an error occurs in serialization
     * @since 0.0.2
     */
    @NotNull
    public static String encode(
        @Nullable Object value, long flags
    ) {
        try (Json chan = new Json(flags)) {
            chan.set(
                null, value
            );
            return chan.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Failed to serialize into json", e
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

        return INS.parse(
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

        return INS.parse(
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

        return INS.parse(
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

        return INS.parse(
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

        return INS.parse(
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

        return INS.parse(
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

        return INS.parse(
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

        return INS.parse(
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

        return INS.parse(
            type, Flow.of(stream).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the json task and converts the result to {@link T}
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

        return INS.parse(
            type, flow.with(DEF.readFlags)
        );
    }
}
