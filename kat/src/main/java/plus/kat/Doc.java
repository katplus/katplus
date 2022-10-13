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

import java.io.IOException;

import static plus.kat.Plan.DEF;
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
        this.flow = new Flow();
        this.supplier = INS;
    }

    /**
     * @param flags the specified {@code flags}
     */
    public Doc(
        long flags
    ) {
        this.flow = new Flow(flags);
        this.supplier = INS;
    }

    /**
     * @param plan the specified {@code plan}
     */
    public Doc(
        @NotNull Plan plan
    ) {
        this.flow = new Flow(
            plan.writeFlags
        );
        this.supplier = INS;
    }

    /**
     * @param flags    the specified {@code flags}
     * @param supplier the specified {@code supplier}
     */
    public Doc(
        @NotNull long flags,
        @Nullable Supplier supplier
    ) {
        this(new Flow(flags), supplier);
    }

    /**
     * @param plan     the specified {@code plan}
     * @param supplier the specified {@code supplier}
     */
    public Doc(
        @NotNull Plan plan,
        @Nullable Supplier supplier
    ) {
        this(plan.writeFlags, supplier);
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
     * Serializes the specified {@code alias} and {@code kat} at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable Kat kat
    ) throws IOException {
        if (kat == null) {
            return coding(alias);
        } else {
            if (alias == null) {
                alias = kat.space();
            }
            flow.leftAlias(
                alias, Boolean.TRUE
            );
            kat.coding(this);
            flow.rightAlias(
                alias, Boolean.TRUE
            );
        }
        return true;
    }

    /**
     * Serializes the specified {@code alias}, {@code space} and {@code kat} at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable CharSequence space,
        @Nullable Kat kat
    ) throws IOException {
        if (alias == null) {
            if (space != null) {
                alias = space;
            } else {
                if (kat != null) {
                    alias = kat.space();
                } else {
                    return false;
                }
            }
        }
        flow.leftAlias(
            alias, Boolean.TRUE
        );
        if (kat != null) {
            kat.coding(this);
        }
        flow.rightAlias(
            alias, Boolean.TRUE
        );
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
     * Writes the specified {@code alias} and {@code value} by specified {@link Coder}
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected boolean coding(
        @Nullable CharSequence alias,
        @NotNull Coder<?> coder,
        @NotNull Object value
    ) throws IOException {
        if (alias == null) {
            alias = coder.getSpace();
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
     * Returns the internal {@link Paper}
     */
    @NotNull
    @Override
    public Paper getFlow() {
        return flow;
    }

    /**
     * Close this {@link Chan}
     *
     * <pre>{@code
     *   try (Chan chan = new Doc()) {
     *       chan.set("id", 1);
     *       chan.set("name", "kraity");
     *   }
     * }</pre>
     *
     * @see Paper#close()
     * @since 0.0.4
     */
    @Override
    public void close() {
        flow.close();
    }

    /**
     * Returns the {@link Flow} of this
     * {@link Doc} as a serialized {@code byte[]}
     *
     * <pre>{@code
     *   Doc doc = ...
     *   byte[] data = doc.toBytes();
     * }</pre>
     *
     * @see Flow#toBytes()
     */
    @NotNull
    public byte[] toBytes() {
        return flow.toBytes();
    }

    /**
     * Returns the {@link Flow} of this
     * {@link Doc} as a serialized {@link String}
     *
     * <pre>{@code
     *   Doc doc = ...
     *   String text = doc.toString();
     * }</pre>
     *
     * @see Flow#toString()
     */
    @Override
    public String toString() {
        return flow.toString();
    }

    /**
     * Serialize to pretty {@link Doc} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     */
    @NotNull
    public static String pretty(
        @Nullable Object value
    ) {
        return encode(
            null, value, Flag.PRETTY | DEF.writeFlags
        );
    }

    /**
     * Serialize to {@link Doc} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     */
    @NotNull
    public static String encode(
        @Nullable Object value
    ) {
        return encode(
            null, value, DEF.writeFlags
        );
    }

    /**
     * Serialize to {@link Doc} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     * @since 0.0.2
     */
    @NotNull
    public static String encode(
        @Nullable Object value, long flags
    ) {
        return encode(
            null, value, flags
        );
    }

    /**
     * Serialize to {@link Doc} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     */
    @NotNull
    public static String encode(
        @Nullable CharSequence alias,
        @Nullable Object value
    ) {
        return encode(
            alias, value, DEF.writeFlags
        );
    }

    /**
     * Serialize to {@link Doc} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     * @since 0.0.3
     */
    @NotNull
    public static String encode(
        @Nullable CharSequence alias,
        @Nullable Object value, long flags
    ) {
        try (Doc chan = new Doc(flags)) {
            chan.set(
                alias, value
            );
            return chan.toString();
        } catch (Exception e) {
            throw new FatalCrash(
                "Unexpectedly, error serializing to xml", e
            );
        }
    }

    /**
     * Parse {@link Doc} {@link CharSequence}
     *
     * @param text the specified text to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     * @see Spare#solve(Algo, Event)
     */
    @Nullable
    public static Object decode(
        @Nullable CharSequence text
    ) {
        if (text == null) {
            return null;
        }
        return INS.solve(
            Algo.DOC, new Event<>(text)
        );
    }

    /**
     * Parse {@link Doc} {@link CharSequence}
     *
     * @param event the specified event to be handled
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     * @see Spare#solve(Algo, Event)
     */
    @Nullable
    public static <T> T decode(
        @Nullable Event<T> event
    ) {
        if (event == null) {
            return null;
        }

        return INS.solve(
            Algo.DOC, event
        );
    }

    /**
     * Parse {@link Doc} byte array
     *
     * @param text the specified text to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     * @see Supplier#down(Class, Event)
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
     * @param text the specified text to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     * @see Supplier#down(Class, Event)
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
     * @param event the specified event to be handled
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     * @see Supplier#down(Class, Event)
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
         * Returns the algo of flow
         */
        @Override
        public Algo algo() {
            return Algo.DOC;
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

            star = 0;
            value[count++] = '<';

            while (i < l) {
                char d = c.charAt(i++);
                if (d >= 0x80) {
                    addChar(d);
                } else {
                    emit((byte) d);
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

            star = 0;
            value[count++] = '<';
            value[count++] = '/';

            while (i < l) {
                char d = c.charAt(i++);
                if (d >= 0x80) {
                    addChar(d);
                } else {
                    emit((byte) d);
                }
            }

            value[count++] = '>';
        }

        @Override
        protected boolean record(
            byte data
        ) {
            switch (data) {
                case '<': {
                    grow(count + 4);
                    star = 0;
                    value[count++] = '&';
                    value[count++] = 'l';
                    value[count++] = 't';
                    value[count++] = ';';
                    return false;
                }
                case '>': {
                    grow(count + 4);
                    star = 0;
                    value[count++] = '&';
                    value[count++] = 'g';
                    value[count++] = 't';
                    value[count++] = ';';
                    return false;
                }
                case '&': {
                    grow(count + 5);
                    star = 0;
                    value[count++] = '&';
                    value[count++] = 'a';
                    value[count++] = 'm';
                    value[count++] = 'p';
                    value[count++] = ';';
                    return false;
                }
                default: {
                    return true;
                }
            }
        }
    }
}
