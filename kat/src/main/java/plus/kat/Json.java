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

import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.stream.*;

import static plus.kat.Supplier.Impl.INS;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Json extends Chan {

    protected Flow flow;

    /**
     * default
     */
    public Json() {
        this(new Flow(), null);
    }

    /**
     * @param flow the specified {@code page}
     */
    public Json(
        @NotNull Flow flow
    ) {
        this(flow, null);
    }

    /**
     * @param flags the specified {@code flags}
     */
    public Json(long flags) {
        this(flags, null);
    }

    /**
     * @param supplier the specified {@code supplier}
     */
    public Json(
        @Nullable Supplier supplier
    ) {
        this(new Flow(), supplier);
    }

    /**
     * @param flags    the specified {@code flags}
     * @param supplier the specified {@code supplier}
     */
    public Json(
        long flags,
        @Nullable Supplier supplier
    ) {
        this(new Flow(flags), supplier);
    }

    /**
     * @param flow     the specified {@code flow}
     * @param supplier the specified {@code supplier}
     */
    public Json(
        @NotNull Flow flow,
        @Nullable Supplier supplier
    ) {
        this.flow = flow;
        this.supplier = supplier == null ? INS : supplier;
    }

    /**
     * @param value the specified {@code value}
     */
    public Json(
        @Nullable Object value
    ) {
        this();
        try {
            set(null, value);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param flags the flags
     * @param value the specified {@code value}
     */
    public Json(
        @Nullable Object value, long flags
    ) {
        this(flags);
        try {
            set(null, value);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param entry the specified {@code entry}
     */
    public Json(
        @Nullable Entry entry
    ) {
        this();
        try {
            set(null, entry);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param coder the specified {@code coder}
     * @param value the specified {@code value}
     */
    public Json(
        @Nullable Coder<?> coder,
        @Nullable Object value
    ) {
        this();
        try {
            set(null, coder, value);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param flags the flags
     * @param coder the specified {@code coder}
     * @param value the specified {@code value}
     */
    public Json(
        @Nullable Coder<?> coder,
        @Nullable Object value, long flags
    ) {
        this(flags);
        try {
            set(null, coder, value);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param value    the specified {@code value}
     * @param supplier the specified {@code supplier}
     */
    public Json(
        @Nullable Supplier supplier,
        @Nullable Object value
    ) {
        this(supplier);
        try {
            set(null, value);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param flags    the flags
     * @param value    the specified {@code value}
     * @param supplier the specified {@code supplier}
     */
    public Json(
        @Nullable Supplier supplier,
        @Nullable Object value, long flags
    ) {
        this(flags, supplier);
        try {
            set(null, value);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * Serializes the specified {@code alias} and {@code entry} at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable Entry entry
    ) throws IOCrash {
        if (entry != null) {
            flow.addComma();
            if (alias != null) {
                flow.addAlias(alias);
            }
            flow.leftBrace();
            entry.accept(this);
            flow.rightBrace();
            return true;
        }
        return coding(alias);
    }

    /**
     * Serializes the specified {@code alias}, {@code space} and {@code entry} at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable CharSequence space,
        @Nullable Entry entry
    ) throws IOCrash {
        return set(
            alias, entry
        );
    }

    /**
     * Serializes the specified {@code alias}, {@code coder} and {@code value} at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable Coder<?> coder,
        @Nullable Object value
    ) throws IOCrash {
        if (coder == null) {
            return set(
                alias, value
            );
        }

        if (value == null) {
            return coding(alias);
        }

        Boolean flag =
            coder.getFlag();

        flow.addComma();
        flow.addAlias(alias);
        if (flag != null) {
            if (flag) {
                flow.leftBrace();
                coder.write(this, value);
                flow.rightBrace();
            } else {
                flow.leftBracket();
                coder.write(this, value);
                flow.rightBracket();
            }
        } else {
            if (value instanceof CharSequence) {
                flow.addQuote();
                coder.write(flow, value);
                flow.addQuote();
            } else if (value instanceof Number || value instanceof Boolean) {
                coder.write(flow, value);
            } else {
                flow.addQuote();
                coder.write(flow, value);
                flow.addQuote();
            }
        }
        return true;
    }

    /**
     * Writes the specified {@code alias}
     *
     * @return {@code true} if successful
     */
    @Override
    protected boolean coding(
        @Nullable CharSequence alias
    ) {
        flow.addComma();
        flow.addAlias(alias);
        flow.addNull();
        return true;
    }

    /**
     * Writes the specified {@code alias} and {@code value}
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    @Override
    protected boolean coding(
        @Nullable CharSequence alias,
        @NotNull Kat value
    ) throws IOCrash {
        CharSequence space =
            value.getSpace();
        if (space == null) {
            return coding(alias);
        }

        flow.addComma();
        flow.addAlias(alias);

        Boolean flag =
            value.getFlag();
        if (flag != null) {
            if (flag) {
                flow.leftBrace();
                value.onCoding(this);
                flow.rightBrace();
            } else {
                flow.leftBracket();
                value.onCoding(this);
                flow.rightBracket();
            }
        } else {
            if (value instanceof Number) {
                value.onCoding(flow);
            } else {
                flow.addQuote();
                value.onCoding(flow);
                flow.addQuote();
            }
        }
        return true;
    }

    /**
     * Writes the specified {@code alias} and {@code value}
     *
     * @return {@code true} if successful
     */
    @Override
    protected boolean coding(
        @Nullable CharSequence alias,
        @NotNull Enum<?> value
    ) {
        flow.addComma();
        flow.addAlias(alias);
        flow.addQuote();
        if (flow.isFlag(Flag.ENUM_AS_ORDINAL)) {
            flow.addInt(
                value.ordinal()
            );
        } else {
            flow.addText(
                value.name()
            );
        }
        flow.addQuote();
        return true;
    }

    /**
     * Writes the specified {@code alias} and {@code value} by specified {@link Spare}
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    protected boolean coding(
        @Nullable CharSequence alias,
        @NotNull Spare<?> spare,
        @NotNull Object value
    ) throws IOCrash {
        Boolean flag =
            spare.getFlag();

        flow.addComma();
        flow.addAlias(alias);
        if (flag != null) {
            if (flag) {
                flow.leftBrace();
                spare.write(this, value);
                flow.rightBrace();
            } else {
                flow.leftBracket();
                spare.write(this, value);
                flow.rightBracket();
            }
        } else {
            if (value instanceof CharSequence) {
                flow.addQuote();
                spare.write(flow, value);
                flow.addQuote();
            } else if (value instanceof Number || value instanceof Boolean) {
                spare.write(flow, value);
            } else {
                flow.addQuote();
                spare.write(flow, value);
                flow.addQuote();
            }
        }
        return true;
    }

    /**
     * Check if this {@link Flow} use the {@code flag}
     *
     * @param flag the specified {@code flag}
     */
    @Override
    public boolean isFlag(
        long flag
    ) {
        return flow.isFlag(flag);
    }

    /**
     * Returns the job of {@link Json}
     */
    @Nullable
    @Override
    public Job getJob() {
        return Job.JSON;
    }

    /**
     * Returns the internal {@link Paper}
     */
    @Nullable
    @Override
    public Paper getFlow() {
        return flow;
    }

    /**
     * Returns a serialized string of {@link Flow}
     */
    @Override
    public String toString() {
        return flow.toString();
    }

    /**
     * Serialize to pretty {@link Json} String
     *
     * @param value specify serialized value
     */
    @NotNull
    public static String pretty(
        @Nullable Object value
    ) {
        return new Json(value, Flag.PRETTY).toString();
    }

    /**
     * Serialize to {@link Json} String
     *
     * @param value specify serialized value
     */
    @NotNull
    public static String encode(
        @Nullable Object value
    ) {
        return new Json(value).toString();
    }

    /**
     * Parse {@link Json} {@link CharSequence}
     *
     * @param text specify the {@code text} to be parsed
     */
    @Nullable
    public static Object decode(
        @Nullable CharSequence text
    ) {
        if (text == null) {
            return null;
        }
        return Parser.solve(
            Job.JSON, new Event<>(text)
        );
    }

    /**
     * Parse {@link Json} {@link CharSequence}
     *
     * @param event specify the {@code event} to be handled
     */
    @Nullable
    public static <T> T decode(
        @Nullable Event<T> event
    ) {
        if (event == null) {
            return null;
        }
        return Parser.solve(
            Job.JSON, event
        );
    }

    /**
     * Parse {@link Json} byte array
     *
     * @param text specify the {@code text} to be parsed
     */
    @Nullable
    public static <T> T decode(
        @Nullable Class<T> klass,
        @Nullable byte[] text
    ) {
        if (text == null |
            klass == null) {
            return null;
        }

        return INS.parse(
            klass, new Event<>(text)
        );
    }

    /**
     * Parse {@link Json} {@link CharSequence}
     *
     * @param text specify the {@code text} to be parsed
     */
    @Nullable
    public static <T> T decode(
        @Nullable Class<T> klass,
        @Nullable CharSequence text
    ) {
        if (text == null |
            klass == null) {
            return null;
        }

        return INS.parse(
            klass, new Event<>(text)
        );
    }

    /**
     * Parse {@link Json} {@link CharSequence}
     *
     * @param event specify the {@code event} to be handled
     */
    @Nullable
    public static <E, T extends E> T decode(
        @Nullable Class<E> klass,
        @Nullable Event<T> event
    ) {
        if (klass == null ||
            event == null) {
            return null;
        }

        return INS.parse(klass, event);
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    public static class Flow extends Paper {

        protected int depth;
        protected boolean comma;

        /**
         * default
         */
        public Flow() {
            super();
        }

        /**
         * @param size the initial capacity
         */
        public Flow(
            int size
        ) {
            super(size);
        }

        /**
         * @param flags the specified {@code flags}
         */
        public Flow(
            long flags
        ) {
            super(flags);
            if (isFlag(Flag.PRETTY)) ++depth;
        }

        /**
         * @param data the initial byte array
         */
        public Flow(
            @NotNull byte[] data
        ) {
            super(data);
        }

        /**
         * @param bucket the specified {@link Bucket} to be used
         */
        public Flow(
            @Nullable Bucket bucket
        ) {
            super(bucket);
        }

        /**
         * Returns a {@link Flow} of this {@link Flow}
         *
         * @param start the start index, inclusive
         * @param end   the end index, exclusive
         */
        @NotNull
        @Override
        public Flow subSequence(
            int start, int end
        ) {
            return new Flow(
                copyBytes(start, end)
            );
        }

        /**
         * Writers left brace
         */
        public void leftBrace() {
            grow(count + 1);
            value[count++] = '{';
            comma = false;
            if (depth != 0) ++depth;
        }

        /**
         * Writers right brace
         */
        public void rightBrace() {
            if (depth == 0) {
                grow(count + 1);
            } else {
                if (--depth != 1) {
                    grow(count + depth * 2);
                    value[count++] = '\n';
                    for (int i = 1; i < depth; i++) {
                        value[count++] = ' ';
                        value[count++] = ' ';
                    }
                } else {
                    grow(count + 2);
                    value[count++] = '\n';
                }
            }
            value[count++] = '}';
        }

        /**
         * Writers left bracket
         */
        public void leftBracket() {
            grow(count + 1);
            value[count++] = '[';
            comma = false;
            if (depth != 0) ++depth;
        }

        /**
         * Writers right bracket
         */
        public void rightBracket() {
            if (depth == 0) {
                grow(count + 1);
            } else {
                if (--depth != 1) {
                    grow(count + depth * 2);
                    value[count++] = '\n';
                    for (int i = 1; i < depth; i++) {
                        value[count++] = ' ';
                        value[count++] = ' ';
                    }
                } else {
                    grow(count + 2);
                    value[count++] = '\n';
                }
            }
            value[count++] = ']';
        }

        /**
         * Writes null
         */
        public void addNull() {
            grow(count + 5);
            hash = 0;
            value[count++] = 'n';
            value[count++] = 'u';
            value[count++] = 'l';
            value[count++] = 'l';
            value[count++] = ',';
        }

        /**
         * Writes quote
         */
        public void addQuote() {
            grow(count + 1);
            value[count++] = '"';
        }

        /**
         * Writes comma
         */
        public void addComma() {
            if (comma) {
                grow(count + 1);
                value[count++] = ',';
            } else {
                comma = true;
            }
        }

        @Override
        public void addBoolean(
            boolean bool
        ) {
            if (bool) {
                grow(count + 4);
                hash = 0;
                value[count++] = 't';
                value[count++] = 'r';
                value[count++] = 'u';
            } else {
                grow(count + 5);
                hash = 0;
                value[count++] = 'f';
                value[count++] = 'a';
                value[count++] = 'l';
                value[count++] = 's';
            }
            value[count++] = 'e';
        }

        @Override
        public void addData(
            byte b
        ) {
            switch (b) {
                case '\r': {
                    b = 'r';
                    break;
                }
                case '\n': {
                    b = 'n';
                    break;
                }
                case '\t': {
                    b = 't';
                    break;
                }
                case '"':
                case '\\': {
                    break;
                }
                default: {
                    addByte(b);
                    return;
                }
            }

            grow(count + 2);
            value[count++] = '\\';
            value[count++] = b;
        }

        @Override
        public void addData(
            char c
        ) {
            byte b;
            switch (c) {
                case '"': {
                    b = '"';
                    break;
                }
                case '\r': {
                    b = 'r';
                    break;
                }
                case '\n': {
                    b = 'n';
                    break;
                }
                case '\t': {
                    b = 't';
                    break;
                }
                case '\\': {
                    b = '\\';
                    break;
                }
                default: {
                    addChar(c);
                    return;
                }
            }

            grow(count + 2);
            value[count++] = '\\';
            value[count++] = b;
        }

        /**
         * Writes the specified alias
         */
        public void addAlias(
            @Nullable CharSequence c
        ) {
            if (depth > 1) {
                grow(count + depth * 2);
                value[count++] = '\n';
                for (int i = 1; i < depth; i++) {
                    value[count++] = ' ';
                    value[count++] = ' ';
                }
            }

            // skip if null
            if (c == null) return;

            int i = 0,
                l = c.length();
            grow(count + l + 3);

            hash = 0;
            value[count++] = '"';

            while (i < l) {
                char d = c.charAt(i++);
                if (d >= 0x80) {
                    addChar(d);
                } else {
                    addData((byte) d);
                }
            }

            value[count++] = '"';
            value[count++] = ':';
        }
    }
}
