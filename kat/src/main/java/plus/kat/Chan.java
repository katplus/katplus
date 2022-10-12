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
import plus.kat.spare.*;
import plus.kat.stream.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

import static plus.kat.chain.Space.*;
import static plus.kat.Supplier.Impl.INS;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Chan implements Flag, Closeable {

    protected Flow flow;
    protected Supplier supplier;

    /**
     * default
     */
    public Chan() {
        this.flow = new Flow();
        this.supplier = INS;
    }

    /**
     * @param flags the specified {@code flags}
     */
    public Chan(
        long flags
    ) {
        this.flow = new Flow(flags);
        this.supplier = INS;
    }

    /**
     * @param plan the specified {@code plan}
     */
    public Chan(
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
    public Chan(
        @NotNull long flags,
        @Nullable Supplier supplier
    ) {
        this(new Flow(flags), supplier);
    }

    /**
     * @param plan     the specified {@code plan}
     * @param supplier the specified {@code supplier}
     */
    public Chan(
        @NotNull Plan plan,
        @Nullable Supplier supplier
    ) {
        this(plan.writeFlags, supplier);
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
     * Serializes the specified {@code alias} and {@code value} at the current hierarchy
     *
     * <pre>{@code
     *  Chan chan = ...
     *  chan.set("id", 1);
     *  chan.set("name", "kraity");
     * }</pre>
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable Object value
    ) throws IOException {
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
        } else {
            return coding(
                alias, spare, value
            );
        }
    }

    /**
     * Serializes the specified {@code alias} and {@code kat} at the current hierarchy
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
     * @throws IOException If an I/O error occurs
     */
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable Kat kat
    ) throws IOException {
        if (kat != null) {
            flow.addSpace(
                kat.space()
            );
            flow.addAlias(alias);
            flow.leftBrace();
            kat.coding(this);
            flow.rightBrace();
            return true;
        }
        return coding(alias);
    }

    /**
     * Serializes the specified {@code alias}, {@code space} and {@code kat} at the current hierarchy
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
     * @throws IOException If an I/O error occurs
     */
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable CharSequence space,
        @Nullable Kat kat
    ) throws IOException {
        if (kat != null) {
            if (space == null) {
                space = kat.space();
            }
            flow.addSpace(space);
            flow.addAlias(alias);
            flow.leftBrace();
            kat.coding(this);
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
     * @throws IOException If an I/O error occurs
     */
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable Coder<?> coder,
        @Nullable Object value
    ) throws IOException {
        if (coder == null) {
            return set(
                alias, value
            );
        }

        if (value == null) {
            return coding(alias);
        } else {
            return coding(
                alias, coder, value
            );
        }
    }

    /**
     * Serializes the specified {@code alias}, {@code klass} and {@code value} at the current hierarchy
     *
     * <pre>{@code
     *  Chan chan = ...
     *  chan.set("id", Integer.class, 1);
     *  chan.set("name", CharSequence.class, "kraity");
     * }</pre>
     *
     * @return {@code true} if successful
     * @throws IOException        If an I/O error occurs
     * @throws ClassCastException If the {@code value} is not an instance of {@code klass}
     * @since 0.0.3
     */
    public boolean set(
        @Nullable CharSequence alias,
        @Nullable Class<?> klass,
        @Nullable Object value
    ) throws IOException {
        if (value == null) {
            return coding(alias);
        }

        // get class specified
        if (klass == null) {
            klass = value.getClass();
        }

        // get spare specified
        Spare<?> spare = supplier.lookup(klass);

        if (spare == null) {
            return coding(
                alias, value
            );
        } else {
            return coding(
                alias, spare, value
            );
        }
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
     * @throws IOException If an I/O error occurs
     */
    protected boolean coding(
        @Nullable CharSequence alias,
        @NotNull Object value
    ) throws IOException {
        if (value instanceof Kat) {
            return set(
                alias, (Kat) value
            );
        }

        if (value instanceof Map) {
            return coding(
                alias, MapSpare.INSTANCE, value
            );
        }

        if (value instanceof Set) {
            return coding(
                alias, SetSpare.INSTANCE, value
            );
        }

        if (value instanceof Iterable) {
            return coding(
                alias, ListSpare.INSTANCE, value
            );
        }

        if (value instanceof Optional) {
            Optional<?> o = (Optional<?>) value;
            return set(
                alias, o.orElse(null)
            );
        }

        if (value instanceof Exception) {
            return coding(
                alias, ErrorSpare.INSTANCE, value
            );
        }

        return coding(alias);
    }

    /**
     * Writes the specified {@code alias} and {@code value} by specified {@link Coder}
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    protected boolean coding(
        @Nullable CharSequence alias,
        @NotNull Coder<?> coder,
        @NotNull Object value
    ) throws IOException {
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
     *   chan.close() // flow.close();
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
     * Close this {@link Chan}
     *
     * <pre>{@code
     *   try (Chan chan = new Chan()) {
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
     * {@link Chan} as a serialized {@code byte[]}
     *
     * <pre>{@code
     *   Chan chan = ...
     *   byte[] data = chan.toBytes();
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
     * {@link Chan} as a serialized {@link String}
     *
     * <pre>{@code
     *   Chan chan = ...
     *   String text = chan.toString();
     * }</pre>
     *
     * @see Flow#toString()
     */
    @Override
    public String toString() {
        return flow.toString();
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
            return Algo.KAT;
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
                star = 0;
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
                        star = 0;
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
            star = 0;
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
            star = 0;
            value[count++] = '^';
            value[count++] = b;
        }

        @Override
        protected void escape(
            int min
        ) {
            byte[] it = value;
            if (min < it.length) {
                star = 0;
                it[count++] = '^';
            } else {
                grow(min + 1);
                star = 0;
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
                    star = 0;
                    value[count++] = '^';
                    value[count++] = data;
                    return false;
                }
            }
            return true;
        }
    }
}
