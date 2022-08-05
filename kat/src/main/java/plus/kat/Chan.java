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

import java.util.*;

import static plus.kat.chain.Space.*;
import static plus.kat.Supplier.Impl.INS;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Chan implements Flag {

    protected Flow flow;
    protected Supplier supplier;

    /**
     * default
     */
    public Chan() {
        this(new Flow(), (Supplier) null);
    }

    /**
     * @param flags the specified {@code flags}
     */
    public Chan(
        long flags
    ) {
        this(flags, null);
    }

    /**
     * @param flow the specified {@code page}
     */
    public Chan(
        @NotNull Flow flow
    ) {
        this(flow, (Supplier) null);
    }

    /**
     * @param supplier the specified {@code supplier}
     */
    public Chan(
        @Nullable Supplier supplier
    ) {
        this(new Flow(), supplier);
    }

    /**
     * @param flags    the specified {@code flags}
     * @param supplier the specified {@code supplier}
     */
    public Chan(
        long flags,
        @Nullable Supplier supplier
    ) {
        this(new Flow(flags), supplier);
    }

    /**
     * @param flow     the specified {@code flow}
     * @param supplier the specified {@code supplier}
     */
    public Chan(
        @NotNull Flow flow,
        @Nullable Supplier supplier
    ) {
        this.flow = flow;
        this.supplier = supplier == null ? INS : supplier;
    }

    /**
     * @param value the specified {@code value}
     */
    public Chan(
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
    public Chan(
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
     * @param flags the flags
     * @param value the specified {@code value}
     */
    public Chan(
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
    public Chan(
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
     * @param action the specified {@code action}
     */
    public Chan(
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
     * @param space  the space
     * @param action the specified {@code action}
     */
    public Chan(
        @Nullable CharSequence space,
        @Nullable Action action
    ) {
        this();
        try {
            set(null, space, action);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param alias  the alias
     * @param space  the space
     * @param action the specified {@code action}
     */
    public Chan(
        @Nullable CharSequence space,
        @Nullable CharSequence alias,
        @Nullable Action action
    ) {
        this();
        try {
            set(alias, space, action);
        } catch (Exception e) {
            // Nothing
        }
    }

    /**
     * @param coder the specified {@code coder}
     * @param value the specified {@code value}
     */
    public Chan(
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
    public Chan(
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
    public Chan(
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
    public Chan(
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
     * @author kraity
     * @since 0.0.2
     */
    @FunctionalInterface
    public interface Action {
        /**
         * @throws IOCrash If an I/O error occurs
         */
        void accept(
            @NotNull Chan chan
        ) throws IOCrash;
    }

    /**
     * Serializes the specified {@code alias} and {@code value} at the current hierarchy
     *
     * <pre>{@code
     *  Chan chan = ...
     *  chan.set("id", 1);
     *  chan.set("name", "kraity");
     * }</pre>
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable Object value
    ) throws IOCrash {
        if (value == null) {
            return coding(alias);
        }

        // get class specified
        Class<?> klass = value.getClass();

        // get spare specified
        Spare<?> spare = supplier.lookup(klass);

        if (spare == null) {
            return coding(
                alias, value
            );
        }

        return coding(
            alias, spare, value
        );
    }

    /**
     * Serializes the specified {@code alias} and {@code action} at the current hierarchy
     *
     * <pre>{@code
     *  Chan chan = ...
     *  chan.set("user", c0 -> {
     *     c0.set("id", 1);
     *     c0.set("name", "kraity");
     *     c0.set("meta", c1 -> {
     *         c1.set("id", 1);
     *         c1.set("tag", "kat");
     *     });
     *  });
     * }</pre>
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable Action action
    ) throws IOCrash {
        if (action != null) {
            flow.addSpace($M);
            flow.addAlias(alias);
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
     * <pre>{@code
     *  Chan chan = ...
     *  chan.set("me", "User", c0 -> {
     *     c0.set("id", 1);
     *     c0.set("name", "kraity");
     *  });
     * }</pre>
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable CharSequence space,
        @Nullable Action action
    ) throws IOCrash {
        if (action != null) {
            flow.addSpace(
                space == null ? $M : space
            );
            flow.addAlias(alias);
            flow.leftBrace();
            action.accept(this);
            flow.rightBrace();
            return true;
        }
        return coding(alias);
    }

    /**
     * Serializes the specified {@code alias}, {@code coder} and {@code value} at the current hierarchy
     *
     * <pre>{@code
     *  Coder<Integer> c1 = ...
     *  Coder<String> c2 = ...
     *
     *  Chan chan = ...
     *  chan.set("id", c1, 1);
     *  chan.set("name", c2, "kraity");
     * }</pre>
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
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

        return coding(
            alias, coder, value
        );
    }

    /**
     * Writes the specified {@code alias}
     *
     * @return {@code true} if successful
     */
    protected boolean coding(
        @Nullable CharSequence alias
    ) {
        flow.addSpace($);
        flow.addAlias(alias);
        flow.leftParen();
        flow.rightParen();
        return true;
    }

    /**
     * Writes the specified {@code alias} and {@code value}
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    protected boolean coding(
        @Nullable CharSequence alias,
        @NotNull Object value
    ) throws IOCrash {
        if (value instanceof Kat) {
            return coding(
                alias, (Kat) value
            );
        }

        if (value instanceof Map) {
            return coding(
                alias, MapSpare.INSTANCE, value
            );
        }

        if (value instanceof List) {
            return coding(
                alias, ListSpare.INSTANCE, value
            );
        }

        if (value instanceof Set) {
            return coding(
                alias, SetSpare.INSTANCE, value
            );
        }

        if (value instanceof Iterable) {
            return coding(
                alias, IterableSpare.INSTANCE, value
            );
        }

        return coding(alias);
    }

    /**
     * Writes the specified {@code alias} and {@code value}
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    protected boolean coding(
        @Nullable CharSequence alias,
        @NotNull Kat value
    ) throws IOCrash {
        CharSequence space =
            value.getSpace();
        if (space == null) {
            return coding(alias);
        }

        flow.addSpace(space);
        flow.addAlias(alias);
        if (value.getFlag() != null) {
            flow.leftBrace();
            value.onCoding(this);
            flow.rightBrace();
        } else {
            flow.leftParen();
            value.onCoding(flow);
            flow.rightParen();
        }
        return true;
    }

    /**
     * Writes the specified {@code alias} and {@code value} by specified {@link Coder}
     *
     * @return {@code true} if successful
     * @throws IOCrash If an I/O error occurs
     */
    protected boolean coding(
        @Nullable CharSequence alias,
        @NotNull Coder<?> coder,
        @NotNull Object value
    ) throws IOCrash {
        flow.addSpace(
            coder.getSpace()
        );
        flow.addAlias(alias);
        if (coder.getFlag() != null) {
            flow.leftBrace();
            coder.write(this, value);
            flow.rightBrace();
        } else {
            flow.leftParen();
            coder.write(flow, value);
            flow.rightParen();
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
     * Returns the job of {@link Chan}
     */
    @NotNull
    public Job getJob() {
        return Job.KAT;
    }

    /**
     * Returns the internal {@link Paper}
     *
     * <pre>{@code
     *   Chan chan = ...
     *   Paper flow = chan.getFlow();
     *
     *   // case 1
     *   String text = flow.toString();
     *
     *   // case 2
     *   OutputStream out = ...
     *   flow.update(out);
     *
     *   // finally close the flow
     *   chan.closeFlow() // flow.close();
     * }</pre>
     */
    @NotNull
    public Paper getFlow() {
        return flow;
    }

    /**
     * Returns the internal {@link Supplier}
     *
     * @since 0.0.3
     */
    @NotNull
    public Supplier getSupplier() {
        return supplier;
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
     *   Chan chan = ...
     *   byte[] data = chan.toBytes();
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
     *   Chan chan = ...
     *   String text = chan.toString();
     * }</pre>
     *
     * @see Paper#closePaper()
     */
    @Override
    public String toString() {
        return flow.closePaper();
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
         * Returns the job
         */
        @Override
        public Job getJob() {
            return Job.KAT;
        }

        /**
         * Writes left paren
         */
        public void leftParen() {
            addByte(
                (byte) '('
            );
        }

        /**
         * Writes right paren
         */
        public void rightParen() {
            addByte(
                (byte) ')'
            );
        }

        /**
         * Writes left Brace
         */
        public void leftBrace() {
            addByte(
                (byte) '{'
            );
            if (depth != 0) ++depth;
        }

        /**
         * Writes right Brace
         */
        public void rightBrace() {
            if (depth == 0) {
                addByte(
                    (byte) '}'
                );
            } else {
                int range = --depth;
                if (range != 1) {
                    grow(count + range * 2);
                    value[count++] = '\n';
                    for (int i = 1; i < range; i++) {
                        value[count++] = ' ';
                        value[count++] = ' ';
                    }
                } else {
                    grow(count + 2);
                    value[count++] = '\n';
                }
                hash = 0;
                value[count++] = '}';
            }
        }

        /**
         * Writes the specified space
         */
        public void addSpace(
            @NotNull CharSequence c
        ) {
            int range = depth;
            if (range > 1) {
                grow(count + range * 2);
                value[count++] = '\n';
                for (int i = 1; i < range; i++) {
                    value[count++] = ' ';
                    value[count++] = ' ';
                }
            }

            if (c instanceof Space) {
                chain(
                    (Space) c
                );
            } else {
                int i = 0,
                    l = c.length();
                grow(count + l);

                while (i < l) {
                    char d = c.charAt(i++);
                    if (d >= 0x80) {
                        continue;
                    }

                    byte b = (byte) d;
                    if (b <= 0x20) {
                        slash(b);
                    } else {
                        if (Space.esc(b)) {
                            grow(count + 2);
                            value[count++] = '^';
                        } else {
                            grow(count + 1);
                        }
                        hash = 0;
                        value[count++] = b;
                    }
                }
            }
        }

        /**
         * Writes the specified alias
         */
        public void addAlias(
            @Nullable CharSequence c
        ) {
            if (c == null) return;

            int i = 0,
                l = c.length();
            grow(count + l + 1);
            hash = 0;
            value[count++] = ':';

            while (i < l) {
                char d = c.charAt(i++);
                if (d >= 0x80) {
                    continue;
                }

                byte b = (byte) d;
                if (b <= 0x20) {
                    slash(b);
                } else {
                    if (Alias.esc(b)) {
                        grow(count + 2);
                        value[count++] = '^';
                    } else {
                        grow(count + 1);
                    }
                    value[count++] = b;
                }
            }
        }

        @Override
        public void addBoolean(
            boolean bool
        ) {
            grow(count + 1);
            if (bool) {
                value[count++] = '1';
            } else {
                value[count++] = '0';
            }
        }

        /**
         * Writes special byte value
         */
        protected void slash(
            byte b
        ) {
            switch (b) {
                case ' ': {
                    b = 's';
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
                default: {
                    return;
                }
            }

            grow(count + 2);
            hash = 0;
            value[count++] = '^';
            value[count++] = b;
        }

        @Override
        protected void escape(
            int min
        ) {
            byte[] it = value;
            if (min < it.length) {
                hash = 0;
                it[count++] = '^';
            } else {
                grow(min + 1);
                hash = 0;
                value[count++] = '^';
            }
        }

        @Override
        protected boolean record(
            byte data
        ) {
            switch (data) {
                case '^':
                case '(':
                case ')': {
                    grow(count + 2);
                    hash = 0;
                    value[count++] = '^';
                    value[count++] = data;
                    return false;
                }
            }
            return true;
        }
    }
}
