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
import java.nio.file.*;
import javax.crypto.Cipher;

import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.stream.*;
import plus.kat.stream.Reader;
import plus.kat.stream.InputStreamReader;

import static plus.kat.Supplier.Impl.INS;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Event<T> implements Flag {

    protected int range;
    protected long flags;

    protected Flag flag;
    protected Type type;
    protected Spare<?> spare;

    protected Reader reader;
    protected Supplier supplier;

    /**
     * For example
     * <pre>{@code
     *   Event<User> event = new Event<>();
     *   Reader reader = ...;
     *   event.with(reader);
     * }</pre>
     */
    public Event() {
        this(Event.class);
    }

    /**
     * @param impl the specified {@link Class} that needs to be compared
     */
    public Event(
        @NotNull Class<?> impl
    ) {
        Class<?> klass = getClass();
        if (klass != impl) {
            type = ((ParameterizedType) klass.getGenericSuperclass()).getActualTypeArguments()[0];
        }
    }

    /**
     * @param reader the specified {@link Reader} to be used
     */
    public Event(
        @Nullable Reader reader
    ) {
        this(Event.class);
        this.reader = reader;
    }

    /**
     * @param flag   the specified {@link Flag} to be used
     * @param reader the specified {@link Reader} to be used
     * @since 0.0.2
     */
    public Event(
        @Nullable Flag flag,
        @Nullable Reader reader
    ) {
        this(Event.class);
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
        this(Event.class);
        reader = new ByteReader(data);
    }

    /**
     * For example
     * <pre>{@code
     *   byte[] data = ...;
     *   Cipher cipher = ...;
     *   Event<User> event = new Event<>(data, cipher);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} or {@code cipher} is null
     * @see CipherByteReader#CipherByteReader(byte[], Cipher)
     */
    public Event(
        @NotNull byte[] data,
        @NotNull Cipher cipher
    ) {
        this(Event.class);
        reader = new CipherByteReader(
            data, cipher
        );
    }

    /**
     * For example
     * <pre>{@code
     *   byte[] data = ...;
     *   Event<User> event = new Event<>(data);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} is null
     * @see CharReader#CharReader(CharSequence)
     */
    public Event(
        @NotNull CharSequence data
    ) {
        this(Event.class);
        reader = new CharReader(data);
    }

    /**
     * @throws NullPointerException      If the specified {@code data} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     * @see ByteReader#ByteReader(byte[], int, int)
     */
    public Event(
        @NotNull byte[] data, int index, int length
    ) {
        this(Event.class);
        reader = new ByteReader(
            data, index, length
        );
    }

    /**
     * @throws NullPointerException      If the specified {@code data} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     * @see CharReader#CharReader(CharSequence, int, int)
     */
    public Event(
        @NotNull CharSequence data, int index, int length
    ) {
        this(Event.class);
        reader = new CharReader(
            data, index, length
        );
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
        this(Event.class);
        reader = new InputStreamReader(
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
        this(Event.class);
        reader = new InputStreamReader(
            Files.newInputStream(
                file.toPath()
            )
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
        this(Event.class);
        reader = new InputStreamReader(
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
        this(Event.class);
        reader = new InputStreamReader(
            conn.getInputStream()
        );
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
        this(Event.class);
        reader = new InputStreamReader(stream);
    }

    /**
     * For example
     * <pre>{@code
     *   cipher.init(
     *      Cipher.DECRYPT_MODE,
     *      new SecretKeySpec(
     *         "key".getBytes(), "AES"
     *      ),
     *      new IvParameterSpec(
     *         "iv".getBytes()
     *      )
     *   );
     *   InputStream stream = ...;
     *   Event<User> event = new Event<>(stream, cipher);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code stream} is null
     * @see CipherStreamReader#CipherStreamReader(InputStream, Cipher)
     */
    public Event(
        @NotNull InputStream stream,
        @NotNull Cipher cipher
    ) {
        this(Event.class);
        reader = new CipherStreamReader(
            stream, cipher
        );
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
     * Use the specified feature {@code flag}
     *
     * <pre>{@code
     *  Event<User> event = ...
     *  event.with(Flag.INDEX_AS_ENUM);
     *  event.with(Flag.INDEX_AS_ENUM | Flag.STRING_AS_OBJECT);
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
     * Use the specified feature {@link Plan}
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
     * Use the specified {@link Type}
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
     * Use the specified {@link Reader}
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
     * Use the specified {@link Spare}
     *
     * <pre>{@code
     *  Spare<User> spare = ...
     *  Event<User> event = ...
     *  event.with(spare);
     * }</pre>
     *
     * @param spare the specified spare
     */
    public Event<T> with(
        @Nullable Spare<?> spare
    ) {
        this.spare = spare;
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
     * Use the specified {@link Type}
     * If this {@link Event} does not have {@code type}
     *
     * @param expected the specified type may be used
     * @since 0.0.2
     */
    public void prepare(
        @Nullable Type expected
    ) {
        if (type == null) {
            type = expected;
        }
    }

    /**
     * Use the specified {@link Supplier}
     * If this {@link Event} does not have {@code supplier}
     *
     * @param expected the specified supplier
     * @since 0.0.2
     */
    public void prepare(
        @Nullable Supplier expected
    ) {
        if (supplier == null) {
            supplier = expected;
        }
    }

    /**
     * Returns the specified {@link Spare} being used
     *
     * @throws IOException If the specified coder was not found
     * @since 0.0.4
     */
    @NotNull
    public Spare<?> accept(
        @NotNull Space space,
        @NotNull Alias alias
    ) throws IOException {
        return assign(
            space, alias
        );
    }

    /**
     * Returns the specified {@link Spare} being used
     *
     * @throws IOException If the specified coder was not found
     * @since 0.0.4
     */
    @NotNull
    public Spare<?> assign(
        @NotNull Space space,
        @NotNull Alias alias
    ) throws IOException {
        Spare<?> coder = spare;
        if (coder != null) {
            return coder;
        }

        Supplier supplier = getSupplier();
        coder = supplier.lookup(type, space);

        if (coder != null) {
            return coder;
        }

        throw new ProxyCrash(
            "Unexpectedly, the spare of " + alias + " was not found"
        );
    }

    /**
     * Returns the specified range
     */
    public int getRange() {
        int r = range;
        return r > 0 ? r : 8;
    }

    /**
     * @param range the specified range
     */
    public void setRange(
        int range
    ) {
        this.range = range;
    }

    /**
     * Returns the specified {@link Flag} being used
     */
    @NotNull
    public Flag getFlag() {
        Flag f = flag;
        return f != null ? f : this;
    }

    /**
     * @param flag the specified {@link Flag} to be used
     */
    public void setFlag(
        Flag flag
    ) {
        if (flag != this) {
            this.flag = flag;
        }
    }

    /**
     * Returns the specified {@link Type} being used
     *
     * @since 0.0.2
     */
    @Nullable
    public Type getType() {
        return type;
    }

    /**
     * Returns the specified {@link Spare} being used
     *
     * @since 0.0.2
     */
    @Nullable
    public Spare<?> getSpare() {
        return spare;
    }

    /**
     * Returns the specified {@link Reader} being read
     *
     * @return {@link Reader} or null
     */
    @Nullable
    public Reader getReader() {
        return reader;
    }

    /**
     * Returns the specified {@link Supplier} being used
     *
     * @since 0.0.2
     */
    @NotNull
    public Supplier getSupplier() {
        Supplier s = supplier;
        return s != null ? s : INS;
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
