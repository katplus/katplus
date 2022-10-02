package plus.kat.caller;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.crash.*;
import plus.kat.spare.*;
import plus.kat.kernel.*;

import java.io.*;
import java.net.*;
import java.util.*;

import static plus.kat.stream.Binary.digit;
import static plus.kat.stream.Binary.upper;

/**
 * @author kraity
 * @since 0.0.4
 */
@SuppressWarnings("deprecation")
public class Query extends Chain {

    protected int offset;

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
        if (offset() == 0) offset = -1;
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
        if (offset() == 0) offset = -1;
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
            toBytes(start, end)
        );
    }

    /**
     * Returns the starting index of the parameter
     */
    public int offset() {
        int o = offset;
        if (o > 0) {
            return o;
        }
        int max = count;
        byte[] it = value;
        while (o < max) {
            if (it[o++] == '?') {
                return offset = o;
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
        grow(count + 3);
        star = 0;
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
        if (count > 0) {
            if (offset != -1) {
                super.chain(
                    (byte) '&'
                );
            } else {
                super.chain(
                    (byte) '?'
                );
                offset = count;
            }
        }

        this.add(key);
        byte[] it = value;

        if (count != it.length) {
            star = 0;
            it[count++] = '=';
        } else {
            grow(count + 1);
            star = 0;
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
        chain(num);
        return this;
    }

    /**
     * @param num the specified data
     */
    public Query add(
        long num
    ) {
        chain(num);
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

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Parser
        extends Chain implements Spoiler {

        private Query query;
        private String string;

        private int i, k;
        private final boolean b;

        public Parser(
            @NotNull Query it
        ) {
            query = it;
            b = false;
            i = it.offset();
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
                    chain(
                        (byte) 0x20
                    );
                    continue;
                }

                if (b != '%') {
                    chain(b);
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
                        chain(d);
                    } catch (IOException e) {
                        throw new UnsupportedCrash(e);
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
                    chain(
                        (byte) 0x20
                    );
                    continue;
                }

                if (b != '%') {
                    chain(b);
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
                        chain(d);
                    } catch (IOException e) {
                        throw new UnsupportedCrash(e);
                    }
                }
            }
        }
    }
}
