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
import java.lang.reflect.*;
import java.net.*;
import java.nio.*;
import java.nio.file.*;

import plus.kat.chain.*;
import plus.kat.spare.*;
import plus.kat.stream.*;
import plus.kat.stream.Reader;
import plus.kat.stream.InputStreamReader;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Event<T> implements Flag {

    protected Flag flag;
    protected Type type;
    protected long flags;

    protected Reader reader;
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
     *   Reader reader = ...;
     *   Event<User> event = new Event<>(reader);
     * }</pre>
     *
     * @param reader the specified {@link Reader} to be used
     */
    public Event(
        @Nullable Reader reader
    ) {
        this();
        this.reader = reader;
    }

    /**
     * For example
     * <pre>{@code
     *   Flag flag = ...;
     *   Reader reader = ...;
     *   Event<User> event = new Event<>(flag, reader);
     * }</pre>
     *
     * @param flag   the specified {@link Flag} to be used
     * @param reader the specified {@link Reader} to be used
     * @since 0.0.2
     */
    public Event(
        @Nullable Flag flag,
        @Nullable Reader reader
    ) {
        this();
        this.flag = flag;
        this.reader = reader;
    }

    /**
     * For example
     * <pre>{@code
     *   byte[] data = ...;
     *   Event<User> event = new Event<>(data);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} is null
     * @see ByteReader#ByteReader(byte[])
     */
    public Event(
        @NotNull byte[] data
    ) {
        this();
        reader = new ByteReader(data);
    }

    /**
     * For example
     * <pre>{@code
     *   Chain data = ...;
     *   Event<User> event = new Event<>(data);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} is null
     * @see ByteReader#ByteReader(Chain)
     */
    public Event(
        @NotNull Chain data
    ) {
        this();
        reader = new ByteReader(data);
    }

    /**
     * For example
     * <pre>{@code
     *   String data = ...;
     *   Event<User> event = new Event<>(data);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} is null
     * @see CharReader#CharReader(CharSequence)
     */
    public Event(
        @NotNull String data
    ) {
        this();
        reader = new CharReader(data);
    }

    /**
     * For example
     * <pre>{@code
     *   CharSequence data = ...;
     *   Event<User> event = new Event<>(data);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} is null
     * @see ByteReader#ByteReader(Chain)
     * @see CharReader#CharReader(CharSequence)
     */
    public Event(
        @NotNull CharSequence data
    ) {
        this();
        if (data instanceof Chain) {
            reader = new ByteReader(
                (Chain) data
            );
        } else {
            reader = new CharReader(data);
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
     * @see ByteBufferReader#ByteBufferReader(ByteBuffer)
     */
    public Event(
        @NotNull ByteBuffer buffer
    ) {
        this();
        reader = new ByteBufferReader(buffer);
    }

    /**
     * For example
     * <pre>{@code
     *   InputStream stream = ...;
     *   Event<User> event = new Event<>(stream);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code stream} is null
     * @see InputStreamReader#InputStreamReader(InputStream)
     */
    public Event(
        @NotNull InputStream stream
    ) {
        this();
        reader = new InputStreamReader(stream);
    }

    /**
     * For example
     * <pre>{@code
     *   URL url = ...;
     *   Event<User> event = new Event<>(url);
     * }</pre>
     *
     * @throws IOException          If an I/O exception occurs
     * @throws NullPointerException If the specified {@code url} is null
     * @see URL#openStream()
     * @see InputStreamReader#InputStreamReader(InputStream)
     */
    public Event(
        @NotNull URL url
    ) throws IOException {
        this(
            url.openStream()
        );
    }

    /**
     * For example
     * <pre>{@code
     *   File file = ...;
     *   Event<User> event = new Event<>(file);
     * }</pre>
     *
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified {@code file} is null
     * @see Files#newInputStream(Path, OpenOption...)
     * @see InputStreamReader#InputStreamReader(InputStream)
     */
    public Event(
        @NotNull File file
    ) throws IOException {
        this(
            file.toPath()
        );
    }

    /**
     * For example
     * <pre>{@code
     *   Path path = ...;
     *   Event<User> event = new Event<>(path);
     * }</pre>
     *
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified {@code path} is null
     * @see Files#newInputStream(Path, OpenOption...)
     * @see InputStreamReader#InputStreamReader(InputStream)
     */
    public Event(
        @NotNull Path path
    ) throws IOException {
        this(
            Files.newInputStream(path)
        );
    }

    /**
     * For example
     * <pre>{@code
     *   URLConnection conn = ...;
     *   Event<User> event = new Event<>(conn);
     * }</pre>
     *
     * @throws NullPointerException    If the specified {@code conn} is null
     * @throws IOException             If an I/O error occurs while creating the input stream
     * @throws UnknownServiceException If the protocol does not support input
     * @see URLConnection#getInputStream()
     * @see InputStreamReader#InputStreamReader(InputStream)
     */
    public Event(
        @NotNull URLConnection conn
    ) throws IOException {
        this(
            conn.getInputStream()
        );
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
     * @see ByteReader#ByteReader(byte[], int, int)
     */
    public Event(
        @NotNull byte[] data, int index, int length
    ) {
        this();
        reader = new ByteReader(
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
     * @see ByteReader#ByteReader(Chain, int, int)
     */
    public Event(
        @NotNull Chain data, int index, int length
    ) {
        this();
        reader = new ByteReader(
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
     * @see CharReader#CharReader(CharSequence, int, int)
     */
    public Event(
        @NotNull String data, int index, int length
    ) {
        this();
        reader = new CharReader(
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
     * @see ByteReader#ByteReader(Chain, int, int)
     * @see CharReader#CharReader(CharSequence, int, int)
     */
    public Event(
        @NotNull CharSequence data, int index, int length
    ) {
        this();
        if (data instanceof Chain) {
            reader = new ByteReader(
                (Chain) data, index, length
            );
        } else {
            reader = new CharReader(
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
     * Uses the specified {@link Reader}
     *
     * <pre>{@code
     *  Reader reader = ...
     *  Event<User> event = ...
     *  event.with(reader);
     * }</pre>
     *
     * @param reader the specified {@link Reader} to be read
     */
    public Event<T> with(
        @Nullable Reader reader
    ) {
        this.reader = reader;
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
     * Returns the {@link Coder} of this {@link Event}
     */
    @Nullable
    public Coder<?> getCoder() {
        return coder;
    }

    /**
     * Returns the {@link Reader} of this {@link Event}
     */
    @Nullable
    public Reader getReader() {
        return reader;
    }

    /**
     * Returns the {@link Supplier} of this {@link Event}
     */
    @Nullable
    public Supplier getSupplier() {
        return supplier;
    }

    /**
     * @see CharLatinReader#CharLatinReader(CharSequence)
     */
    @NotNull
    public static <T> Event<T> latin(
        @NotNull CharSequence data
    ) {
        return new Event<>(
            new CharLatinReader(data)
        );
    }

    /**
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     * @see CharLatinReader#CharLatinReader(CharSequence, int, int)
     */
    @NotNull
    public static <T> Event<T> latin(
        @NotNull CharSequence data, int index, int length
    ) {
        return new Event<>(
            new CharLatinReader(
                data, index, length
            )
        );
    }

    /**
     * For example
     * <pre>{@code
     *   Event.file("./test/entity/user.kat");
     * }</pre>
     *
     * @param path the file path
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    public static <T> Event<T> file(
        @NotNull String path
    ) throws IOException {
        return new Event<>(
            Paths.get(path)
        );
    }

    /**
     * For example
     * <pre>{@code
     *   Event.file(getClass(), "/entity/user.kat");
     * }</pre>
     *
     * @param path the file path
     * @throws NullPointerException  If the specified {@code path} or {@code klass} is null
     * @throws FileNotFoundException If the file does not exist or is not a regular file or for some other reason cannot be opened for reading.
     * @see Class#getResourceAsStream(String)
     * @see InputStreamReader#InputStreamReader(InputStream)
     */
    @NotNull
    public static <T> Event<T> file(
        @NotNull Class<?> klass,
        @NotNull String path
    ) throws FileNotFoundException {
        InputStream in = klass
            .getResourceAsStream(path);

        if (in == null) {
            throw new FileNotFoundException(
                "the file does not exist"
            );
        }

        return new Event<>(
            new InputStreamReader(in)
        );
    }

    /**
     * For example
     * <pre>{@code
     *   Event.remote("https://kat.plus/test/entity/user.kat");
     * }</pre>
     *
     * @param url the String to parse as a URL.
     * @throws IOException             If an I/O error occurs while creating the input stream
     * @throws UnknownServiceException If the protocol does not support input
     * @throws MalformedURLException   If the url is {@code null} or no protocol is specified or an unknown protocol is found
     * @see URL#URL(String)
     * @see URL#openConnection()
     * @see URLConnection#getInputStream()
     * @see InputStreamReader#InputStreamReader(InputStream)
     */
    @NotNull
    public static <T> Event<T> remote(
        @NotNull String url
    ) throws IOException {
        URL $url = new URL(url);
        URLConnection conn = $url.openConnection();

        conn.setReadTimeout(6000);
        conn.setConnectTimeout(3000);

        conn.setRequestProperty(
            "User-Agent", "kat/0.0.1"
        );
        conn.setRequestProperty(
            "Accept", "text/kat,text/xml,text/plain,application/kat,application/xml,application/json"
        );

        return new Event<>(
            new InputStreamReader(
                conn.getInputStream()
            )
        );
    }
}
