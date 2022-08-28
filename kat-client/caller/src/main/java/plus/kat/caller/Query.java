package plus.kat.caller;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.kernel.*;

import java.io.IOException;
import java.net.*;

import static plus.kat.stream.Binary.lower;
import static plus.kat.stream.Binary.upper;

/**
 * @author kraity
 * @since 0.0.4
 */
@SuppressWarnings("deprecation")
public class Query extends Chain {

    public Query() {
        super(16);
    }

    /**
     * @param src the specified src
     */
    public Query(
        @NotNull byte[] src
    ) {
        super(src);
        count = value.length;
        if (!contains((byte) '?')) {
            super.chain((byte) '?');
        }
    }

    /**
     * @param url the specified url
     */
    public Query(
        @NotNull CharSequence url
    ) {
        super();
        super.chain(
            url, 0, url.length()
        );
        if (!contains((byte) '?')) {
            super.chain((byte) '?');
        }
    }

    /**
     * Returns a {@link Query} of this {@link Query}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public Query subSequence(
        int start, int end
    ) {
        return new Query(
            copyBytes(start, end)
        );
    }

    /**
     * @param key   the specified key
     * @param value the specified value
     */
    public Query set(
        @NotNull String key,
        @Nullable int value
    ) {
        return set(key).add(value);
    }

    /**
     * @param key   the specified key
     * @param value the specified value
     */
    public Query set(
        @NotNull String key,
        @Nullable long value
    ) {
        return set(key).add(value);
    }

    /**
     * @param key   the specified key
     * @param value the specified value
     */
    public Query set(
        @NotNull String key,
        @Nullable String value
    ) {
        return set(key).add(value);
    }

    /**
     * @param key the specified key
     * @param val the specified value
     */
    public Query set(
        @NotNull String key,
        @Nullable Object val
    ) {
        return set(key).add(
            val.toString()
        );
    }

    /**
     * @param b the specified data
     */
    public Query set(
        byte b
    ) {
        grow(count + 3);
        hash = 0;
        value[count++] = '%';
        value[count++] = upper((b & 0xF0) >> 4);
        value[count++] = upper(b & 0x0F);
        return this;
    }

    /**
     * @param key the specified key
     */
    public Query set(
        @NotNull String key
    ) {
        int i = count - 1;
        if (i > 0 &&
            value[i] != '?') {
            super.chain(
                (byte) '&'
            );
        }

        this.add(key);
        byte[] it = value;

        if (count != it.length) {
            hash = 0;
            it[count++] = '=';
        } else {
            grow(count + 1);
            hash = 0;
            value[count++] = '=';
        }
        return this;
    }

    /**
     * @param b the specified data
     */
    public Query add(
        byte b
    ) {
        if ((0x60 < b && b < 0x7b) ||
            (0x40 < b && b < 0x5b) ||
            (0x2F < b && b < 0x3A)) {
            chain(b);
        } else {
            if (b == ' ') {
                chain(
                    (byte) '+'
                );
            } else if (b == '.' ||
                b == '_' ||
                b == '-' ||
                b == '*') {
                chain(b);
            } else {
                return set(b);
            }
        }
        return this;
    }

    /**
     * @param data the specified data
     */
    public Query add(
        @NotNull byte[] data
    ) {
        grow(count + data.length);
        for (byte b : data) add(b);
        return this;
    }

    /**
     * @param data the specified data
     * @param i    the start index
     * @param l    the specified length
     */
    public Query add(
        @NotNull byte[] data, int i, int l
    ) {
        grow(count + l);
        int k = i + l;
        while (i < k) {
            add(data[i++]);
        }
        return this;
    }

    /**
     * @param num the specified data
     */
    public Query add(
        int num
    ) {
        if (num < 0) {
            grow(count + 1);
            value[count++] = '-';
        } else {
            num = -num;
        }

        if (num > -10) {
            grow(count + 1);
            hash = 0;
            value[count++] = lower(-num);
        } else {
            int mark = count;
            do {
                grow(count + 1);
                value[count++] = lower(-(num % 10));
                num /= 10;
            } while (num < 0);
            swop(mark, count - 1);
        }
        return this;
    }

    /**
     * @param num the specified data
     */
    public Query add(
        long num
    ) {
        if (num < 0) {
            grow(count + 1);
            value[count++] = '-';
        } else {
            num = -num;
        }

        if (num > -10L) {
            grow(count + 1);
            hash = 0;
            value[count++] = lower((int) -num);
        } else {
            int mark = count;
            do {
                grow(count + 1);
                value[count++] = lower((int) -(num % 10L));
                num /= 10L;
            } while (num < 0L);
            swop(mark, count - 1);
        }
        return this;
    }

    /**
     * @param c the specified data
     */
    public Query add(
        @NotNull CharSequence c
    ) {
        return add(
            c, 0, c.length()
        );
    }

    /**
     * @param c the specified data
     * @param i the start index
     * @param l the specified length
     */
    public Query add(
        @NotNull CharSequence c, int i, int l
    ) {
        int k = i + l;
        grow(count + l);

        while (i < k) {
            // get char
            char d = c.charAt(i++);

            // U+0000 ~ U+007F
            if (d < 0x80) {
                add((byte) d);
            }

            // U+0080 ~ U+07FF
            else if (d < 0x800) {
                set((byte) ((d >> 6) | 0xC0));
                set((byte) ((d & 0x3F) | 0x80));
            }

            // U+10000 ~ U+10FFFF
            // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
            else if (d >= 0xD800 && d <= 0xDFFF) {
                if (i >= k) {
                    set((byte) '?');
                    break;
                }

                char f = c.charAt(i++);
                if (f < 0xDC00 || f > 0xDFFF) {
                    set((byte) '?');
                    continue;
                }

                int u = (d << 10) + f - 0x35F_DC00;
                set((byte) ((u >> 18) | 0xF0));
                set((byte) (((u >> 12) & 0x3F) | 0x80));
                set((byte) (((u >> 6) & 0x3F) | 0x80));
                set((byte) ((u & 0x3F) | 0x80));
            }

            // U+0800 ~ U+FFFF
            else {
                set((byte) ((d >> 12) | 0xE0));
                set((byte) (((d >> 6) & 0x3F) | 0x80));
                set((byte) ((d & 0x3F) | 0x80));
            }
        }

        return this;
    }

    /**
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    public Client get()
        throws IOException {
        return client().get();
    }

    /**
     * Returns this {@link Query} as a {@link Client}
     *
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    public Client client()
        throws IOException {
        return new Client(
            new URL(
                toString()
            )
        );
    }

    /**
     * Returns this {@link Query} as a {@link URL}
     *
     * @throws MalformedURLException If no protocol is specified, or unknown protocol
     */
    @NotNull
    public URL toUrl()
        throws MalformedURLException {
        return new URL(
            toString()
        );
    }

    /**
     * Returns the {@code byte[]} of this {@link Query} as a {@link String}
     */
    @NotNull
    @Override
    public String toString() {
        if (count == 0) {
            return "";
        }

        return new String(
            value, 0, 0, count
        );
    }

    /**
     * Returns the {@code byte[]} of this {@link Query} as a {@link String}
     *
     * @param b the beginning index, inclusive
     * @param e the ending index, exclusive
     * @throws IndexOutOfBoundsException if the beginIndex is negative
     */
    @NotNull
    @Override
    public String toString(
        int b, int e
    ) {
        int l = e - b;
        if (l <= 0 || e > count) {
            return "";
        }

        return new String(
            value, 0, b, l
        );
    }
}
