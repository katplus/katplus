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

import static plus.kat.Plan.*;
import static plus.kat.Draft.Unsafe.*;
import static plus.kat.Supplier.Vendor.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Doc extends Stream implements Chan {

    protected Context context;

    /**
     * Constructs a xml with the default flags and context
     */
    public Doc() {
        this(DEF);
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
     * Serializes the object to xml {@link Chan}
     *
     * @param value the specified value to serialized
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
     * Serializes the entity to xml {@link Chan}
     *
     * @param value the specified value to serialized
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
     * Serializes the object to xml {@link Chan}
     *
     * @param value the specified value to serialized
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
     * Serializes the entity to xml {@link Chan}
     *
     * @param value the specified value to serialized
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
     * Serializes the object to xml {@link Chan}
     *
     * @param value the specified value to serialized
     * @throws IOException If an I/O error or analysis error occurs
     */
    @NotNull
    public static Chan encode(
        @Nullable Object value, long flags
    ) throws IOException {
        Chan chan = new Doc(
            DEF.writeFlags | flags
        );
        try {
            chan.set(
                null, value
            );
        } catch (Throwable alas) {
            try {
                chan.close();
            } catch (Throwable e) {
                alas.addSuppressed(e);
            }
            throw alas;
        }
        return chan;
    }

    /**
     * Serializes the entity to xml {@link Chan}
     *
     * @param value the specified value to serialized
     * @throws IOException If an I/O error or analysis error occurs
     */
    @NotNull
    public static Chan encode(
        @Nullable Entity value, long flags
    ) throws IOException {
        Chan chan = new Doc(
            DEF.writeFlags | flags
        );
        try {
            chan.set(
                null, value
            );
        } catch (Throwable alas) {
            try {
                chan.close();
            } catch (Throwable e) {
                alas.addSuppressed(e);
            }
            throw alas;
        }
        return chan;
    }

    /**
     * Resolves the draft and converts the result to {@link T}
     *
     * @param draft the specified draft to be handled
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the draft is found
     */
    @Nullable
    public static <T> T decode(
        @Nullable Draft<T> draft
    ) throws IOException {
        if (draft == null) {
            return null;
        }

        Flow flow = draft.getFlow();
        Type type = draft.getType();

        if (flow == null ||
            type == null) {
            return null;
        }

        return INS.down(
            type, flow.with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link Flow} and converts the result to {@link T}
     *
     * @param flow the specified flow to be parsed
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

        return INS.down(
            type, flow.with(DEF.readFlags)
        );
    }

    /**
     * Resolves the byte array and converts the result to {@link T}
     *
     * @param text the specified text to be parsed
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
            type, Flow.of(text).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the char array and converts the result to {@link T}
     *
     * @param text the specified text to be parsed
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
            type, Flow.of(text).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link Binary} and converts the result to {@link T}
     *
     * @param text the specified text to be parsed
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
            type, Flow.of(text).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link String} and converts the result to {@link T}
     *
     * @param text the specified text to be parsed
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
            type, Flow.of(text).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link Reader} where
     * calling {@link Reader#close()} has no effect
     *
     * @param reader the specified reader to be parsed
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
            type, Flow.of(reader).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link ByteBuffer} and converts the result to {@link T}
     *
     * @param buffer the specified buffer to be parsed
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
            type, Flow.of(buffer).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link CharBuffer} and converts the result to {@link T}
     *
     * @param buffer the specified buffer to be parsed
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
            type, Flow.of(buffer).with(DEF.readFlags)
        );
    }

    /**
     * Resolves the {@link InputStream} where
     * calling {@link InputStream#close()} has no effect
     *
     * @param stream the specified stream to be parsed
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
            type, Flow.of(stream).with(DEF.readFlags)
        );
    }
}
