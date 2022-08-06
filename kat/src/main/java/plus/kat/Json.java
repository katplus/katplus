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
import plus.kat.spare.*;
import plus.kat.stream.*;

import java.io.Serializable;

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
     * @param action the specified {@code action}
     */
    public Json(
        @Nullable Action action
    ) {
        this();
        try {
            set(null, action);
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
     * Serializes the specified {@code alias} and {@code action} at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable Action action
    ) throws IOCrash {
        if (action != null) {
            flow.addComma();
            if (alias != null) {
                flow.addAlias(alias);
            }
            flow.leftBrace();
            action.accept(this);
            flow.rightBrace();
            return true;
        }
        return coding(alias);
    }

    /**
     * Serializes the specified {@code alias}, {@code space} and {@code action} at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable CharSequence space,
        @Nullable Action action
    ) throws IOCrash {
        return set(
            alias, action
        );
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
            if (value instanceof Serializable) {
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
     * Writes the specified {@code alias} and {@code value} by specified {@link Coder}
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    @Override
    protected boolean coding(
        @Nullable CharSequence alias,
        @NotNull Coder<?> coder,
        @NotNull Object value
    ) throws IOCrash {
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
            if (coder instanceof Serializable) {
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
    @NotNull
    @Override
    public Job getJob() {
        return Job.JSON;
    }

    /**
     * Returns the internal {@link Paper}
     */
    @NotNull
    @Override
    public Paper getFlow() {
        return flow;
    }

    /**
     * close the internal {@link Paper}
     *
     * @see Paper#close()
     * @since 0.0.2
     */
    public void closeFlow() {
        flow.close();
    }

    /**
     * Returns a copy of {@link Flow}.
     * Automatically close this {@link Flow} when calling
     *
     * <pre>{@code
     *   Json json = ...
     *   byte[] data = json.toBytes();
     * }</pre>
     *
     * @see Paper#closeFlow()
     * @since 0.0.3
     */
    public byte[] toBytes() {
        return flow.closeFlow();
    }

    /**
     * Returns a serialized string of {@link Flow}.
     * Automatically close this {@link Flow} when calling
     *
     * <pre>{@code
     *   Json json = ...
     *   String text = json.toString();
     * }</pre>
     *
     * @see Paper#closePaper()
     */
    @Override
    public String toString() {
        return flow.closePaper();
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
     * Serialize to {@link Json} String
     *
     * @param value specify serialized value
     * @since 0.0.2
     */
    @NotNull
    public static String encode(
        @Nullable Object value, long flags
    ) {
        return new Json(value, flags).toString();
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

        protected boolean comma;

        /**
         * default
         */
        public Flow() {
            super();
        }

        /**
         * @param flags the specified {@code flags}
         */
        public Flow(
            long flags
        ) {
            super(flags);
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
         * Returns the job
         */
        @Override
        public Job getJob() {
            return Job.JSON;
        }

        /**
         * Writers left brace
         */
        public void leftBrace() {
            addByte(
                (byte) '{'
            );
            comma = false;
            if (depth != 0) ++depth;
        }

        /**
         * Writers right brace
         */
        public void rightBrace() {
            if (depth == 0) {
                addByte(
                    (byte) '}'
                );
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
                value[count++] = '}';
            }
        }

        /**
         * Writers left bracket
         */
        public void leftBracket() {
            addByte(
                (byte) '['
            );
            comma = false;
            if (depth != 0) ++depth;
        }

        /**
         * Writers right bracket
         */
        public void rightBracket() {
            if (depth == 0) {
                addByte(
                    (byte) ']'
                );
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
                value[count++] = ']';
            }
        }

        /**
         * Writes null
         */
        public void addNull() {
            grow(count + 4);
            hash = 0;
            value[count++] = 'n';
            value[count++] = 'u';
            value[count++] = 'l';
            value[count++] = 'l';
        }

        /**
         * Writes quote
         */
        public void addQuote() {
            addByte(
                (byte) '"'
            );
        }

        /**
         * Writes comma
         */
        public void addComma() {
            if (comma) {
                addByte(
                    (byte) ','
                );
            } else {
                comma = true;
            }
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
                    emit((byte) d);
                }
            }

            value[count++] = '"';
            value[count++] = ':';
        }
    }
}
