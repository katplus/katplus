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
public class Doc extends Chan {

    protected Flow flow;

    /**
     * default
     */
    public Doc() {
        this(new Flow(), (Supplier) null);
    }

    /**
     * @param flow the specified {@code page}
     */
    public Doc(
        @NotNull Flow flow
    ) {
        this(flow, (Supplier) null);
    }

    /**
     * @param flags the specified {@code flags}
     */
    public Doc(long flags) {
        this(flags, null);
    }

    /**
     * @param supplier the specified {@code supplier}
     */
    public Doc(
        @Nullable Supplier supplier
    ) {
        this(new Flow(), supplier);
    }

    /**
     * @param flags    the specified {@code flags}
     * @param supplier the specified {@code supplier}
     */
    public Doc(
        long flags,
        @Nullable Supplier supplier
    ) {
        this(new Flow(flags), supplier);
    }

    /**
     * @param flow     the specified {@code flow}
     * @param supplier the specified {@code supplier}
     */
    public Doc(
        @NotNull Flow flow,
        @Nullable Supplier supplier
    ) {
        this.flow = flow;
        this.supplier = supplier == null ? INS : supplier;
    }

    /**
     * @param value the specified {@code value}
     */
    public Doc(
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
     * @param alias the alias
     * @param value the specified {@code value}
     */
    public Doc(
        @Nullable CharSequence alias,
        @Nullable Object value
    ) {
        this();
        try {
            set(alias, value);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param value the specified {@code value}
     */
    public Doc(
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
     * @param flags the flags
     * @param alias the alias
     * @param value the specified {@code value}
     */
    public Doc(
        @Nullable CharSequence alias,
        @Nullable Object value, long flags
    ) {
        this(flags);
        try {
            set(alias, value);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param alias  the alias
     * @param action the specified {@code action}
     */
    public Doc(
        @Nullable CharSequence alias,
        @Nullable Action action
    ) {
        this();
        try {
            set(alias, action);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param flags  the flags
     * @param alias  the alias
     * @param action the specified {@code action}
     */
    public Doc(
        @Nullable CharSequence alias,
        @Nullable Action action, long flags
    ) {
        this(flags);
        try {
            set(alias, action);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param coder the specified {@code coder}
     * @param value the specified {@code value}
     */
    public Doc(
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
    public Doc(
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
    public Doc(
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
    public Doc(
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
        if (alias != null) {
            flow.leftAlias(
                alias, Boolean.TRUE
            );
            if (action != null) {
                action.accept(this);
            }
            flow.rightAlias(
                alias, Boolean.TRUE
            );
            return true;
        }
        return false;
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
        if (alias == null)
            if ((alias = space) == null)
                return false;
        flow.leftAlias(
            alias, Boolean.TRUE
        );
        if (action != null) {
            action.accept(this);
        }
        flow.rightAlias(
            alias, Boolean.TRUE
        );
        return true;
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

        if (alias == null) {
            alias = coder.getSpace();
        }

        if (value == null) {
            return coding(alias);
        }

        Boolean flag = coder.getFlag();
        flow.leftAlias(alias, flag);
        if (flag != null) {
            coder.write(this, value);
        } else {
            coder.write(flow, value);
        }
        flow.rightAlias(alias, flag);
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
        if (alias != null) {
            flow.leftAlias(alias, null);
            flow.rightAlias(alias, null);
            return true;
        }
        return false;
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
        if (alias == null) {
            return false;
        }

        Boolean flag = value.getFlag();
        flow.leftAlias(alias, flag);
        if (space != null) {
            if (flag != null) {
                value.onCoding(this);
            } else {
                value.onCoding(flow);
            }
        }
        flow.rightAlias(alias, flag);
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
        if (alias == null) {
            alias = spare.getSpace();
        }

        Boolean flag = spare.getFlag();
        flow.leftAlias(alias, flag);
        if (flag != null) {
            spare.write(this, value);
        } else {
            spare.write(flow, value);
        }
        flow.rightAlias(alias, flag);
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
     * Returns the job of {@link Doc}
     */
    @Nullable
    @Override
    public Job getJob() {
        return Job.DOC;
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
     * close the internal {@link Paper}
     *
     * @see Paper#close()
     * @since 0.0.2
     */
    public void closeFlow() {
        flow.close();
    }

    /**
     * Returns a serialized string of {@link Flow}
     *
     * @see Paper#closePaper()
     */
    @Override
    public String toString() {
        return flow.closePaper();
    }

    /**
     * Serialize to pretty {@link Doc} String
     *
     * @param value specify serialized value
     */
    @NotNull
    public static String pretty(
        @Nullable Object value
    ) {
        return new Doc(value, Flag.PRETTY).toString();
    }

    /**
     * Serialize to {@link Doc} String
     *
     * @param value specify serialized value
     */
    @NotNull
    public static String encode(
        @Nullable Object value
    ) {
        return new Doc(value).toString();
    }

    /**
     * Serialize to {@link Doc} String
     *
     * @param value specify serialized value
     * @since 0.0.2
     */
    @NotNull
    public static String encode(
        @Nullable Object value, long flags
    ) {
        return new Doc(value, flags).toString();
    }

    /**
     * Serialize to {@link Doc} String
     *
     * @param value specify serialized value
     */
    @NotNull
    public static String encode(
        @Nullable CharSequence alias,
        @Nullable Object value
    ) {
        return new Doc(alias, value).toString();
    }

    /**
     * Parse {@link Doc} {@link CharSequence}
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
            Job.DOC, new Event<>(text)
        );
    }

    /**
     * Parse {@link Doc} {@link CharSequence}
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
            Job.DOC, event
        );
    }

    /**
     * Parse {@link Doc} byte array
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

        return INS.down(
            klass, new Event<>(text)
        );
    }

    /**
     * Parse {@link Doc} {@link CharSequence}
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

        return INS.down(
            klass, new Event<>(text)
        );
    }

    /**
     * Parse {@link Doc} {@link CharSequence}
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

        return INS.down(klass, event);
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    public static class Flow extends Paper {

        protected int depth;

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
         * Returns the job
         */
        @Override
        public Job getJob() {
            return Job.DOC;
        }

        @Override
        public void addData(
            byte b
        ) {
            switch (b) {
                case '<': {
                    grow(count + 4);
                    value[count++] = '&';
                    value[count++] = 'l';
                    value[count++] = 't';
                    value[count++] = ';';
                    break;
                }
                case '>': {
                    grow(count + 4);
                    value[count++] = '&';
                    value[count++] = 'g';
                    value[count++] = 't';
                    value[count++] = ';';
                    break;
                }
                case '&': {
                    grow(count + 5);
                    value[count++] = '&';
                    value[count++] = 'a';
                    value[count++] = 'm';
                    value[count++] = 'p';
                    value[count++] = ';';
                    break;
                }
                default: {
                    grow(count + 1);
                    value[count++] = b;
                }
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
            char c
        ) {
            switch (c) {
                case '<': {
                    grow(count + 4);
                    value[count++] = '&';
                    value[count++] = 'l';
                    value[count++] = 't';
                    value[count++] = ';';
                    break;
                }
                case '>': {
                    grow(count + 4);
                    value[count++] = '&';
                    value[count++] = 'g';
                    value[count++] = 't';
                    value[count++] = ';';
                    break;
                }
                case '&': {
                    grow(count + 5);
                    value[count++] = '&';
                    value[count++] = 'a';
                    value[count++] = 'm';
                    value[count++] = 'p';
                    value[count++] = ';';
                    break;
                }
                default: {
                    addChar(c);
                }
            }
        }

        /**
         * Writes left alias
         */
        public void leftAlias(
            @NotNull CharSequence c,
            @Nullable Boolean flag
        ) {
            int range = depth;
            if (range != 0) {
                if (flag != null)
                    ++depth;
                if (range > 1) {
                    grow(count + range * 2);
                    value[count++] = '\n';
                    for (int i = 1; i < range; i++) {
                        value[count++] = ' ';
                        value[count++] = ' ';
                    }
                }
            }

            int i = 0,
                l = c.length();
            grow(count + l + 2);

            hash = 0;
            value[count++] = '<';

            while (i < l) {
                char d = c.charAt(i++);
                if (d >= 0x80) {
                    addChar(d);
                } else {
                    addData((byte) d);
                }
            }

            value[count++] = '>';
        }

        /**
         * Writes right alias
         */
        public void rightAlias(
            @NotNull CharSequence c,
            @Nullable Boolean flag
        ) {
            if (depth != 0 && flag != null) {
                int range = --depth;
                if (range == 1) {
                    grow(count + 1);
                    value[count++] = '\n';
                } else {
                    grow(count + range * 2);
                    value[count++] = '\n';
                    for (int i = 1; i < range; i++) {
                        value[count++] = ' ';
                        value[count++] = ' ';
                    }
                }
            }

            int i = 0,
                l = c.length();
            grow(count + l + 3);

            hash = 0;
            value[count++] = '<';
            value[count++] = '/';

            while (i < l) {
                char d = c.charAt(i++);
                if (d >= 0x80) {
                    addChar(d);
                } else {
                    addData((byte) d);
                }
            }

            value[count++] = '>';
        }
    }
}
