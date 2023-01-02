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

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.lang.reflect.*;

import plus.kat.chain.*;
import plus.kat.spare.*;
import plus.kat.stream.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Event<T> implements Flag {

    protected Flag flag;
    protected Type type;
    protected long flags;

    protected Paper paper;
    protected Coder<?> coder;
    protected Supplier supplier;

    /**
     * Constructs an empty event and gets
     * the generic type of this {@link Event}
     */
    public Event() {
        Class<?> clazz = getClass();
        if (clazz != Event.class) {
            Type visa = clazz.getGenericSuperclass();
            if (visa instanceof ParameterizedType) {
                type = ((ParameterizedType) visa).getActualTypeArguments()[0];
            }
        }
    }

    /**
     * For example
     * <pre>{@code
     *   Paper paper = ...;
     *   Event<User> event = new Event<>(paper);
     * }</pre>
     *
     * @param paper the specified {@link Paper} to be used
     */
    public Event(
        @Nullable Paper paper
    ) {
        this();
        this.paper = paper;
    }

    /**
     * For example
     * <pre>{@code
     *   Flag flag = ...;
     *   Paper paper = ...;
     *   Event<User> event = new Event<>(flag, paper);
     * }</pre>
     *
     * @param flag  the specified {@link Flag} to be used
     * @param paper the specified {@link Paper} to be used
     * @since 0.0.2
     */
    public Event(
        @Nullable Flag flag,
        @Nullable Paper paper
    ) {
        this();
        this.flag = flag;
        this.paper = paper;
    }

    /**
     * For example
     * <pre>{@code
     *   byte[] data = ...;
     *   Event<User> event = new Event<>(data);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} is null
     * @see BytePaper#BytePaper(byte[])
     */
    public Event(
        @NotNull byte[] data
    ) {
        this();
        paper = new BytePaper(data);
    }

    /**
     * For example
     * <pre>{@code
     *   char[] data = ...;
     *   Event<User> event = new Event<>(data);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} is null
     * @see CharPaper#CharPaper(char[])
     */
    public Event(
        @NotNull char[] data
    ) {
        this();
        paper = new CharPaper(data);
    }

    /**
     * For example
     * <pre>{@code
     *   Chain data = ...;
     *   Event<User> event = new Event<>(data);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} is null
     * @see BytePaper#BytePaper(Chain)
     */
    public Event(
        @NotNull Chain data
    ) {
        this();
        paper = new BytePaper(data);
    }

    /**
     * For example
     * <pre>{@code
     *   String data = ...;
     *   Event<User> event = new Event<>(data);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} is null
     * @see CharSequencePaper#CharSequencePaper(CharSequence)
     */
    public Event(
        @NotNull String data
    ) {
        this();
        paper = new CharSequencePaper(data);
    }

    /**
     * Constructs an {@link Event} where
     * calling {@link Reader#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *   try (Reader reader = ...) {
     *      Event<User> event = new Event<>(reader);
     *   }
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code reader} is null
     * @see ReaderPaper#ReaderPaper(Reader)
     */
    public Event(
        @NotNull Reader reader
    ) {
        this();
        paper = new ReaderPaper(reader);
    }

    /**
     * For example
     * <pre>{@code
     *   CharSequence data = ...;
     *   Event<User> event = new Event<>(data);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} is null
     * @see BytePaper#BytePaper(Chain)
     * @see CharSequencePaper#CharSequencePaper(CharSequence)
     */
    public Event(
        @NotNull CharSequence data
    ) {
        this();
        if (data instanceof Chain) {
            paper = new BytePaper(
                (Chain) data
            );
        } else {
            paper = new CharSequencePaper(data);
        }
    }

    /**
     * For example
     * <pre>{@code
     *   ByteBuffer buffer = ...;
     *   Event<User> event = new Event<>(buffer);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code buffer} is null
     * @see ByteBufferPaper#ByteBufferPaper(ByteBuffer)
     */
    public Event(
        @NotNull ByteBuffer buffer
    ) {
        this();
        paper = new ByteBufferPaper(buffer);
    }

    /**
     * Constructs an {@link Event} where
     * calling {@link InputStream#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *   try (InputStream stream = ...) {
     *      Event<User> event = new Event<>(stream);
     *   }
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code stream} is null
     * @see InputStreamPaper#InputStreamPaper(InputStream)
     */
    public Event(
        @NotNull InputStream stream
    ) {
        this();
        paper = new InputStreamPaper(stream);
    }

    /**
     * Constructs an {@link Event} where
     * calling {@link InputStream#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *   Charset charset = ...
     *   try (InputStream stream = ...) {
     *      Event<User> event = new Event<>(stream, charset);
     *   }
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code stream} is null
     * @see ReaderPaper#ReaderPaper(InputStream, Charset)
     * @see InputStreamPaper#InputStreamPaper(InputStream)
     */
    public Event(
        @NotNull InputStream stream,
        @Nullable Charset charset
    ) {
        this();
        if (charset != null) {
            switch (charset.name()) {
                case "UTF-8":
                case "US-ASCII":
                case "ISO-8859-1": {
                    break;
                }
                default: {
                    paper = new ReaderPaper(
                        stream, charset
                    );
                    return;
                }
            }
        }
        paper = new InputStreamPaper(stream);
    }

    /**
     * For example
     * <pre>{@code
     *   byte[] data = ...;
     *   Event<User> event = new Event<>(data, 0, 6);
     * }</pre>
     *
     * @throws NullPointerException      If the specified {@code data} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     * @see BytePaper#BytePaper(byte[], int, int)
     */
    public Event(
        @NotNull byte[] data, int index, int length
    ) {
        this();
        paper = new BytePaper(
            data, index, length
        );
    }

    /**
     * For example
     * <pre>{@code
     *   char[] data = ...;
     *   Event<User> event = new Event<>(data, 0, 6);
     * }</pre>
     *
     * @throws NullPointerException      If the specified {@code data} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     * @see CharPaper#CharPaper(char[], int, int)
     */
    public Event(
        @NotNull char[] data, int index, int length
    ) {
        this();
        paper = new CharPaper(
            data, index, length
        );
    }

    /**
     * For example
     * <pre>{@code
     *   Chain data = ...;
     *   Event<User> event = new Event<>(data, 0, 6);
     * }</pre>
     *
     * @throws NullPointerException      If the specified {@code data} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     * @see BytePaper#BytePaper(Chain, int, int)
     */
    public Event(
        @NotNull Chain data, int index, int length
    ) {
        this();
        paper = new BytePaper(
            data, index, length
        );
    }

    /**
     * For example
     * <pre>{@code
     *   String data = ...;
     *   Event<User> event = new Event<>(data, 0, 6);
     * }</pre>
     *
     * @throws NullPointerException      If the specified {@code data} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     * @see CharSequencePaper#CharSequencePaper(CharSequence, int, int)
     */
    public Event(
        @NotNull String data, int index, int length
    ) {
        this();
        paper = new CharSequencePaper(
            data, index, length
        );
    }

    /**
     * For example
     * <pre>{@code
     *   CharSequence data = ...;
     *   Event<User> event = new Event<>(data, 0, 6);
     * }</pre>
     *
     * @throws NullPointerException      If the specified {@code data} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     * @see BytePaper#BytePaper(Chain, int, int)
     * @see CharSequencePaper#CharSequencePaper(CharSequence, int, int)
     */
    public Event(
        @NotNull CharSequence data, int index, int length
    ) {
        this();
        if (data instanceof Chain) {
            paper = new BytePaper(
                (Chain) data, index, length
            );
        } else {
            paper = new CharSequencePaper(
                data, index, length
            );
        }
    }

    /**
     * Check if this {@link Event} use the {@code flag}
     *
     * @param flag the specified {@code flag}
     * @see Flag#isFlag(long)
     */
    @Override
    public boolean isFlag(
        long flag
    ) {
        if ((flags & flag) == flag) {
            return true;
        }

        Flag f = this.flag;
        return f != null && f.isFlag(flag);
    }

    /**
     * Check if this {@link Object} use the {@code flag}.
     * The method is to extend {@link Flag#isFlag(long)} to derive custom {@code flags}.
     *
     * @param flag the specified {@code flag}
     * @param code the specified {@code code}
     * @see Flag#isFlag(long, int)
     * @since 0.0.3
     */
    @Override
    public boolean isFlag(
        long flag, int code
    ) {
        if (code == 0) {
            return isFlag(flag);
        }

        Flag f = this.flag;
        return f != null && f.isFlag(flag, code);
    }

    /**
     * Uses the specified feature {@code flag}
     *
     * <pre>{@code
     *  Event<User> event = ...
     *  event.with(Flag.INDEX_AS_ENUM);
     *  event.with(Flag.INDEX_AS_ENUM | Flag.VALUE_AS_BEAN);
     * }</pre>
     *
     * @param flag the specified {@code flag}
     */
    public Event<T> with(
        long flag
    ) {
        flags |= flag;
        return this;
    }

    /**
     * Uses the specified feature {@link Plan}
     *
     * <pre>{@code
     *  Plan plan = ...
     *  Event<User> event = ...
     *  event.with(plan);
     *  event.with(Plan.DEF);
     * }</pre>
     *
     * @param plan the specified {@code plan}
     * @throws NullPointerException If the {@code plan} is null
     * @since 0.0.3
     */
    public Event<T> with(
        @NotNull Plan plan
    ) {
        flags |= plan.readFlags;
        return this;
    }

    /**
     * Uses the specified feature {@link Flag}
     *
     * <pre>{@code
     *  Event<User> event = ...
     *  event.with(other.getFlag());
     * }</pre>
     *
     * @param flag the specified {@link Flag}
     */
    public Event<T> with(
        @Nullable Flag flag
    ) {
        this.flag = flag;
        return this;
    }

    /**
     * Uses the specified {@link Type}
     *
     * <pre>{@code
     *  Event<User> event = ...
     *  event.with(User.class);
     *  event.with(ArrayType.of(method)) // Method method = ...
     *
     *  event.with(new ComplexType<Map<Long, User>>().getType());
     *  // You can use this:
     *  // Event<Map<Long, User>> event = new Event<Map<Long, User>>() {};
     * }</pre>
     *
     * @param type the specified type
     */
    public Event<T> with(
        @Nullable Type type
    ) {
        this.type = type;
        return this;
    }

    /**
     * Uses the specified {@link Paper}
     *
     * <pre>{@code
     *  Paper paper = ...
     *  Event<User> event = ...
     *  event.with(paper);
     * }</pre>
     *
     * @param paper the specified {@link Paper} to be read
     */
    public Event<T> with(
        @Nullable Paper paper
    ) {
        this.paper = paper;
        return this;
    }

    /**
     * Uses the specified {@link Coder}
     *
     * <pre>{@code
     *  Coder<User> coder = ...
     *  Event<User> event = ...
     *  event.with(coder);
     * }</pre>
     *
     * @param coder the specified coder
     */
    public Event<T> with(
        @Nullable Coder<?> coder
    ) {
        this.coder = coder;
        return this;
    }

    /**
     * Use the specified {@link Supplier}
     *
     * <pre>{@code
     *  Supplier<User> supplier = ...
     *  Event<User> event = ...
     *  event.with(supplier);
     * }</pre>
     *
     * @param supplier the specified supplier
     */
    public Event<T> with(
        @Nullable Supplier supplier
    ) {
        this.supplier = supplier;
        return this;
    }

    /**
     * Seeks the {@link Coder} by space and alias
     */
    @Nullable
    public Coder<?> seek(
        @NotNull Space space,
        @NotNull Alias alias
    ) {
        return coder;
    }

    /**
     * If no type is set for this event,
     * the candidate {@link Type} will be used
     *
     * @param candidate the specified candidate type
     */
    public Event<T> setup(
        @Nullable Type candidate
    ) {
        if (type == null) {
            type = candidate;
        }
        return this;
    }

    /**
     * If no coder is set for this event,
     * the candidate {@link Coder} will be used
     *
     * @param candidate the specified candidate type
     */
    public Event<T> setup(
        @Nullable Coder<?> candidate
    ) {
        if (coder == null) {
            coder = candidate;
        }
        return this;
    }

    /**
     * If no supplier is set for this event,
     * the candidate {@link Supplier} will be used
     *
     * @param candidate the specified candidate supplier
     */
    public Event<T> setup(
        @Nullable Supplier candidate
    ) {
        if (supplier == null) {
            supplier = candidate;
        }
        return this;
    }

    /**
     * Returns the specified {@link Flag} being used
     */
    @Nullable
    public Flag getFlag() {
        return flag;
    }

    /**
     * Returns the specified {@link Type} being used
     */
    @Nullable
    public Type getType() {
        return type;
    }

    /**
     * Returns the {@link Paper} of this {@link Event}
     */
    @Nullable
    public Paper getPaper() {
        return paper;
    }

    /**
     * Returns the {@link Coder} of this {@link Event}
     */
    @Nullable
    public Coder<?> getCoder() {
        return coder;
    }

    /**
     * Returns the {@link Supplier} of this {@link Event}
     */
    @Nullable
    public Supplier getSupplier() {
        return supplier;
    }
}
