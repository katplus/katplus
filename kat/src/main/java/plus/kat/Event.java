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
import java.nio.file.Path;
import javax.crypto.Cipher;

import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.stream.*;
import plus.kat.stream.Reader;
import plus.kat.stream.InputStreamReader;
import plus.kat.utils.Reflect;

import static plus.kat.Supplier.Impl.INS;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Event<T> implements Flag {

    protected int range;
    protected long flags;

    protected Flag flag;
    protected Alias alias;

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
        @NotNull Reader reader
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
        @NotNull Reader reader
    ) {
        this(Event.class);
        this.setFlag(flag);
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
     * For example
     * <pre>{@code
     *   CharSequence data = ...;
     *   Cipher cipher = ...;
     *   Event<User> event = new Event<>(data, cipher);
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code data} is null
     * @see CipherCharReader#CipherCharReader(CharSequence, Cipher)
     */
    public Event(
        @NotNull CharSequence data,
        @NotNull Cipher cipher
    ) {
        this(Event.class);
        reader = new CipherCharReader(data, cipher);
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
     * @throws NullPointerException  If the specified {@code file} is null
     * @throws FileNotFoundException If the file does not exist or is not a regular file or for some other reason cannot be opened for reading.
     * @see FileInputStream#FileInputStream(String)
     * @see InputStreamReader#InputStreamReader(InputStream)
     */
    public Event(
        @NotNull File file
    ) throws FileNotFoundException {
        this(Event.class);
        reader = new InputStreamReader(
            new FileInputStream(file)
        );
    }

    /**
     * For example
     * <pre>{@code
     *   Path path = ...;
     *   Event<User> event = new Event<>(path);
     * }</pre>
     *
     * @throws NullPointerException          If the specified {@code path} is null
     * @throws FileNotFoundException         If the file does not exist or is not a regular file or for some other reason cannot be opened for reading.
     * @throws UnsupportedOperationException If this Path is not associated with the default provider
     * @see Path#toFile()
     * @see FileInputStream#FileInputStream(File)
     * @see InputStreamReader#InputStreamReader(InputStream)
     */
    public Event(
        @NotNull Path path
    ) throws FileNotFoundException {
        this(Event.class);
        reader = new InputStreamReader(
            new FileInputStream(
                path.toFile()
            )
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
     */
    @Override
    public boolean isFlag(
        long flag
    ) {
        return (flags & flag) != 0;
    }

    /**
     * Use the specified feature {@code flag}
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
     * Use the specified {@link Type}
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
     * @param spare the specified spare
     */
    public Event<T> with(
        @Nullable Spare<? super T> spare
    ) {
        this.spare = spare;
        return this;
    }

    /**
     * Use the specified {@link Supplier}
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
     * @param type the specified type may be used
     * @since 0.0.2
     */
    public void prepare(
        @Nullable Type type
    ) {
        if (this.type == null) {
            this.type = type;
        }
    }

    /**
     * Use the specified {@link Supplier}
     * If this {@link Event} does not have {@code supplier}
     *
     * @param supplier the specified supplier
     * @since 0.0.2
     */
    public void prepare(
        @Nullable Supplier supplier
    ) {
        if (this.supplier == null) {
            this.supplier = supplier;
        }
    }

    /**
     * Captures exception
     *
     * @param e the crash
     */
    public void onError(
        @NotNull Exception e
    ) {
        // Nothing
    }

    /**
     * Returns the specified {@link Coder} being used
     *
     * @throws IOCrash If the specified coder was not found
     * @since 0.0.2
     */
    @NotNull
    public Coder<?> getCoder(
        @NotNull Space space,
        @NotNull Alias alias
    ) throws IOCrash {
        this.alias = alias;
        if (spare != null) {
            return spare;
        }

        Coder<?> coder;
        Supplier supplier = getSupplier();

        if (type == null) {
            coder = supplier.lookup(space);
        } else {
            return Reflect.lookup(
                type, supplier
            );
        }

        if (coder != null) {
            return coder;
        }

        throw new UnexpectedCrash(
            "Unexpectedly, the specified coder was not found"
        );
    }

    /**
     * Sets the alias of this {@link Event}
     *
     * @param alias the specified alias
     * @since 0.0.2
     */
    public void setAlias(
        @Nullable Alias alias
    ) {
        this.alias = alias;
    }

    /**
     * Returns the {@link Alias} of this {@link Event}
     *
     * @since 0.0.2
     */
    @Nullable
    public Alias getAlias() {
        return alias;
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
        this.flag = flag;
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
     * @see CharAsciiReader#CharAsciiReader(CharSequence)
     */
    @NotNull
    public static <T> Event<T> ascii(
        @NotNull CharSequence data
    ) {
        return new Event<>(
            new CharAsciiReader(data)
        );
    }

    /**
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     * @see CharAsciiReader#CharAsciiReader(CharSequence, int, int)
     */
    @NotNull
    public static <T> Event<T> ascii(
        @NotNull CharSequence data, int index, int length
    ) {
        return new Event<>(
            new CharAsciiReader(
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
     * @throws FileNotFoundException If the file does not exist or is not a regular file or for some other reason cannot be opened for reading.
     * @see FileInputStream#FileInputStream(String)
     * @see InputStreamReader#InputStreamReader(InputStream)
     */
    @NotNull
    public static <T> Event<T> file(
        @NotNull String path
    ) throws FileNotFoundException {
        return new Event<>(
            new InputStreamReader(
                new FileInputStream(path)
            )
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
