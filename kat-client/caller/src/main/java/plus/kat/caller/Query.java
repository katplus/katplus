package plus.kat.caller;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.crash.*;
import plus.kat.spare.*;
import plus.kat.kernel.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.Charset;

import static plus.kat.stream.Binary.digit;
import static plus.kat.stream.Binary.upper;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

/**
 * @author kraity
 * @since 0.0.4
 */
public class Query extends Alpha {

    protected int apex;

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
        if (apex() == 0) apex = -1;
    }

    /**
     * @param url the specified url
     */
    public Query(
        @NotNull CharSequence url
    ) {
        super();
        int len = url.length();
        if (len != 0) {
            join(url, 0, len);
            if (apex() == 0) apex = -1;
        }
    }

    /**
     * @param map the specified params
     */
    public Query(
        @NotNull Map<?, ?> map
    ) {
        super(32);
        for (Map.Entry<?, ?> it : map.entrySet()) {
            Object key = it.getKey();
            if (key == null) {
                continue;
            }
            Object val = it.getValue();
            if (val == null) {
                set(key.toString(), null);
            } else if (val instanceof String) {
                set(key.toString(), (String) val);
            } else if (val instanceof Integer) {
                set(key.toString(), (int) val);
            } else if (val instanceof Long) {
                set(key.toString(), (long) val);
            } else {
                set(key.toString(), val.toString());
            }
        }
    }

    /**
     * Returns the charset of this {@link Query}
     */
    @Override
    public Charset charset() {
        return ISO_8859_1;
    }

    /**
     * Returns a {@link Query} that
     * is a subsequence of this {@link Query}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public Query subSequence(
        int start, int end
    ) {
        return new Query(
            toBytes(start, end)
        );
    }

    /**
     * Returns the starting index of the parameter
     */
    public int apex() {
        int i = apex;
        if (i > 0) {
            return i;
        }
        int max = count;
        byte[] it = value;
        while (i < max) {
            if (it[i++] == '?') {
                return apex = i;
            }
        }
        return 0;
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
        asset = 0;
        byte[] it = grow(
            count + 3
        );
        it[count++] = '%';
        it[count++] = upper((b & 0xF0) >> 4);
        it[count++] = upper(b & 0x0F);
        return this;
    }

    /**
     * @param key the specified key
     */
    public Query set(
        @NotNull String key
    ) {
        if (count > 0) {
            if (apex != -1) {
                join(
                    (byte) '&'
                );
            } else {
                join(
                    (byte) '?'
                );
                apex = count;
            }
        }

        add(key);
        join(
            (byte) '='
        );
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
            join(b);
        } else {
            if (b == ' ') {
                join(
                    (byte) '+'
                );
            } else if (b == '.' ||
                b == '_' ||
                b == '-' ||
                b == '*') {
                join(b);
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
        @Nullable byte[] data
    ) {
        if (data != null) {
            grow(count + data.length);
            for (byte b : data) add(b);
        }
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
        emit(num);
        return this;
    }

    /**
     * @param num the specified data
     */
    public Query add(
        long num
    ) {
        emit(num);
        return this;
    }

    /**
     * @param c the specified data
     */
    public Query add(
        @Nullable CharSequence c
    ) {
        if (c == null) {
            return this;
        }
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
            char d = c.charAt(i++);

            if (d < 0x80) {
                add((byte) d);
            }

            // U+0080 ~ U+07FF
            else if (d < 0x800) {
                set((byte) (d >> 6 | 0xC0));
                set((byte) (d & 0x3F | 0x80));
            }

            // U+10000 ~ U+10FFFF
            else if (0xD7FF < d && d < 0xE000) {
                if (d > 0xDBFF) {
                    set((byte) '?');
                    continue;
                }

                if (k == i) {
                    set((byte) '?');
                    break;
                }

                char a = c.charAt(i);
                if (a < 0xDC00 ||
                    a > 0xDFFF) {
                    set((byte) '?');
                    continue;
                }

                int hi = d - 0xD7C0;
                int lo = a - 0xDC00;

                i++; // 2 chars
                set((byte) (hi >> 8 | 0xF0));
                set((byte) (hi >> 2 & 0x3F | 0x80));
                set((byte) (lo >> 6 | hi << 4 & 0x30 | 0x80));
                set((byte) (lo & 0x3F | 0x80));
            }

            // U+0800 ~ U+FFFF
            else {
                set((byte) (d >> 12 | 0xE0));
                set((byte) (d >> 6 & 0x3F | 0x80));
                set((byte) (d & 0x3F | 0x80));
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
     * Returns a {@link Spoiler} of {@link Query}
     */
    @NotNull
    public Spoiler spoiler() {
        return new Parser(this);
    }

    /**
     * Returns a {@link Spoiler} of {@link String}
     */
    @NotNull
    public static Spoiler spoiler(
        @NotNull String spec
    ) {
        return new Parser(spec);
    }

    /**
     * Returns this {@link Query} as a {@link HashMap}
     */
    @NotNull
    public Map<String, String> toMap() {
        return toMap(
            new HashMap<>()
        );
    }

    /**
     * Returns this {@link Query} as a {@link HashMap}
     */
    @NotNull
    public Map<String, String> toMap(
        @NotNull Map<String, String> map
    ) {
        Spoiler spoiler = spoiler();
        while (spoiler.hasNext()) {
            map.put(
                spoiler.getKey(),
                spoiler.getValue().toString()
            );
        }
        return map;
    }

    /**
     * Returns the {@code url} as a {@link HashMap}
     */
    @NotNull
    public static Map<String, String> toMap(
        @NotNull String url
    ) {
        return toMap(
            url, new HashMap<>()
        );
    }

    /**
     * Returns the {@code url} as a {@link HashMap}
     */
    @NotNull
    public static Map<String, String> toMap(
        @NotNull String spec,
        @NotNull Map<String, String> map
    ) {
        Spoiler spoiler = spoiler(spec);
        while (spoiler.hasNext()) {
            map.put(
                spoiler.getKey(),
                spoiler.getValue().toString()
            );
        }
        return map;
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
    @Override
    @SuppressWarnings("deprecation")
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
    @Override
    @SuppressWarnings("deprecation")
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

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Parser
        extends Alpha implements Spoiler {

        private Query query;
        private String string;

        private int i, k;
        private final boolean b;

        public Parser(
            @NotNull Query it
        ) {
            query = it;
            b = false;
            i = it.apex();
        }

        public Parser(
            @NotNull String it
        ) {
            string = it;
            b = true;
            i = it.indexOf('?') + 1;
        }

        @Override
        public boolean hasNext() {
            if (b) {
                String s = string;
                if (i < k) {
                    int v = s.indexOf(
                        '&', ++k
                    );
                    if (v != -1) {
                        i = v + 1;
                    } else {
                        return false;
                    }
                }
                k = s.indexOf('=', i);
            } else {
                Query q = query;
                if (i < k) {
                    int v = q.indexOf(
                        (byte) '&', ++k
                    );
                    if (v != -1) {
                        i = v + 1;
                    } else {
                        return false;
                    }
                }
                k = q.indexOf(
                    (byte) '=', i
                );
            }
            return k > 0;
        }

        @Override
        public String getKey() {
            if (i < k) {
                count = 0;
                if (b) {
                    decode(
                        string, i, k
                    );
                } else {
                    decode(
                        query.value, i, k
                    );
                }
            }
            return toString();
        }

        @Override
        public Object getValue() {
            if (i < k) {
                count = 0;
                if (b) {
                    String s = string;
                    int v = s.indexOf(
                        '&', ++k
                    );
                    if (v == -1) {
                        v = s.length();
                    }
                    i = v + 1;
                    decode(s, k, v);
                } else {
                    Query q = query;
                    int v = q.indexOf(
                        (byte) '&', ++k
                    );
                    if (v == -1) {
                        v = q.count;
                    }
                    i = v + 1;
                    decode(
                        q.value, k, v
                    );
                }
            }
            return this;
        }

        public void decode(
            byte[] data, int i, int o
        ) {
            while (i < o) {
                byte b = data[i++];

                if (b == '+') {
                    join(
                        (byte) 0x20
                    );
                    continue;
                }

                if (b != '%') {
                    join(b);
                    continue;
                }

                if (i + 1 < o) {
                    try {
                        byte d;
                        d = (byte) digit(
                            data[i++]
                        );
                        d <<= 4;
                        d |= (byte) digit(
                            data[i++]
                        );
                        join(d);
                    } catch (IOException e) {
                        throw new FatalCrash(e);
                    }
                }
            }
        }

        public void decode(
            CharSequence c, int i, int o
        ) {
            while (i < o) {
                char b = c.charAt(i++);

                if (b == '+') {
                    join(
                        (byte) 0x20
                    );
                    continue;
                }

                if (b != '%') {
                    join(b);
                    continue;
                }

                if (i + 1 < o) {
                    try {
                        byte d;
                        d = (byte) digit(
                            (byte) c.charAt(i++)
                        );
                        d <<= 4;
                        d |= (byte) digit(
                            (byte) c.charAt(i++)
                        );
                        join(d);
                    } catch (IOException e) {
                        throw new FatalCrash(e);
                    }
                }
            }
        }
    }
}
