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

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static plus.kat.Plan.DEF;
import static plus.kat.Supplier.Impl.INS;
import static plus.kat.stream.Binary.upper;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Kat extends Steam implements Chan {

    private static final byte
        LP = '(', RP = ')',
        LB = '{', RB = '}';

    protected Supplier supplier;

    /**
     * Constructs a default kat
     */
    public Kat() {
        this(0L, INS);
    }

    /**
     * Constructs a kat with the specified flags
     *
     * @param flags the specified flags
     */
    public Kat(
        long flags
    ) {
        this(flags, INS);
    }

    /**
     * Constructs a kat with the specified plan
     *
     * @param plan the specified plan
     */
    public Kat(
        @NotNull Plan plan
    ) {
        this(plan.writeFlags);
    }

    /**
     * Constructs a kat with the specified flags and supplier
     *
     * @param flags    the specified flags
     * @param supplier the specified supplier
     */
    public Kat(
        @NotNull long flags,
        @NotNull Supplier supplier
    ) {
        super(flags);
        this.supplier = supplier;
    }

    /**
     * Constructs a kat with the specified plan and supplier
     *
     * @param plan     the specified plan
     * @param supplier the specified supplier
     */
    public Kat(
        @NotNull Plan plan,
        @NotNull Supplier supplier
    ) {
        this(plan.writeFlags, supplier);
    }

    /**
     * Returns the algo of flow
     */
    @Override
    public Algo algo() {
        return Algo.KAT;
    }

    /**
     * Serializes the specified alias and value at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable String alias,
        @Nullable Object value
    ) throws IOException {
        return set(
            alias, null, value
        );
    }

    /**
     * Serializes the specified alias and fitter at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable String alias,
        @Nullable Fitter fitter
    ) throws IOException {
        return set(
            alias, "M", fitter
        );
    }

    /**
     * Serializes the specified alias, space and fitter at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable String alias,
        @Nullable String space,
        @Nullable Fitter fitter
    ) throws IOException {
        short d = depth;
        if (d > 0) {
            int i = d + 1;
            grow(count + i * 2);
            byte[] it = value;
            it[count++] = '\n';
            while (--i != 0) {
                it[count++] = ' ';
                it[count++] = ' ';
            }
        }

        if (space != null) {
            escape(space);
        }
        if (alias != null) {
            concat(
                (byte) ':'
            );
            escape(alias);
        }

        if (fitter == null) {
            concat(LP);
            concat(RP);
        } else {
            concat(LB);
            if (d < 0) {
                fitter.accept(this);
            } else {
                ++depth;
                fitter.accept(this);
                --depth;
                if (d == 0) {
                    grow(count + 2);
                    value[count++] = '\n';
                } else {
                    int i = d + 1;
                    grow(count + i * 2);
                    byte[] it = value;
                    it[count++] = '\n';
                    while (--i != 0) {
                        it[count++] = ' ';
                        it[count++] = ' ';
                    }
                }
            }
            concat(RB);
        }
        return true;
    }

    /**
     * Serializes the specified alias, coder and object at the current hierarchy
     *
     * @return {@code true} if successful
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean set(
        @Nullable String alias,
        @Nullable Coder<?> coder,
        @Nullable Object object
    ) throws IOException {
        if (object == null) {
            return set(
                alias, "$", null
            );
        }

        if (coder == null) {
            // search for the spare of object
            coder = supplier.lookup(
                object.getClass()
            );

            // solving the coder problem again
            if (coder == null) {
                if (object instanceof Fitter) {
                    return set(
                        alias, (Fitter) object
                    );
                }

                if (object instanceof Optional) {
                    Optional<?> o = (Optional<?>) object;
                    return set(
                        alias, null, o.orElse(null)
                    );
                }

                if (object instanceof Exception) {
                    coder = ErrorSpare.INSTANCE;
                } else if (object instanceof Map) {
                    coder = MapSpare.INSTANCE;
                } else if (object instanceof Set) {
                    coder = SetSpare.INSTANCE;
                } else if (object instanceof Iterable) {
                    coder = ListSpare.INSTANCE;
                } else {
                    return set(
                        alias, "$", null
                    );
                }
            }
        }

        short d = depth;
        if (d > 0) {
            int i = d + 1;
            grow(count + i * 2);
            byte[] it = value;
            it[count++] = '\n';
            while (--i != 0) {
                it[count++] = ' ';
                it[count++] = ' ';
            }
        }

        escape(
            coder.getSpace()
        );
        if (alias != null) {
            concat(
                (byte) ':'
            );
            escape(alias);
        }

        if (coder.getFlag() == null) {
            concat(LP);
            coder.write(
                (Flow) this, object
            );
            concat(RP);
        } else {
            concat(LB);
            if (d < 0) {
                coder.write(
                    (Chan) this, object
                );
            } else {
                ++depth;
                coder.write(
                    (Chan) this, object
                );
                --depth;
                if (d == 0) {
                    grow(count + 2);
                    value[count++] = '\n';
                } else {
                    int i = d + 1;
                    grow(count + i * 2);
                    byte[] it = value;
                    it[count++] = '\n';
                    while (--i != 0) {
                        it[count++] = ' ';
                        it[count++] = ' ';
                    }
                }
            }
            concat(RB);
        }
        return true;
    }

    /**
     * Returns the internal {@link Steam}
     */
    @Override
    public Steam getSteam() {
        return this;
    }

    /**
     * Returns the internal {@link Supplier}
     */
    @Override
    public Supplier getSupplier() {
        return supplier;
    }

    /**
     * Concatenates the byte value to this {@link Kat},
     * which will be escaped if it is a special character
     *
     * @param bt the specified byte value to be appended
     */
    @Override
    public void emit(
        byte bt
    ) throws IOException {
        switch (bt) {
            case '^':
            case '(':
            case ')': {
                concat(
                    (byte) '^'
                );
            }
        }
        byte[] it = value;
        if (count != it.length) {
            asset = 0;
            it[count++] = bt;
        } else {
            grow(count + 1);
            asset = 0;
            value[count++] = bt;
        }
    }

    /**
     * Concatenates the string representation
     * of the boolean value to this {@link Kat}
     *
     * @param bt the specified boolean to be appended
     */
    @Override
    public void emit(
        boolean bt
    ) throws IOException {
        byte[] it = value;
        int size = count + 1;
        if (size > it.length) {
            grow(size);
            it = value;
        }
        asset = 0;
        it[count++] = bt ? (byte) '1' : (byte) '0';
    }

    /**
     * Concatenates the string to this {@link Kat},
     * which will be escaped if it is a special character
     *
     * @param data the specified string to be appended
     */
    private void escape(
        @NotNull String data
    ) throws IOException {
        int i = 0,
            l = data.length();
        grow(count + l);
        for (; i < l; i++) {
            char bit = data.charAt(i);
            if (bit > 0x7A) {
                switch (bit) {
                    case '{':
                    case '}': {
                        concat(
                            (byte) '^'
                        );
                        break;
                    }
                    case 0x7C:
                    case 0x7E: {
                        break;
                    }
                    case 0x7F: {
                        concat(
                            bit, (byte) '^'
                        );
                        continue;
                    }
                    default: {
                        if ((flags & 2) == 0) {
                            concat(bit);
                        } else {
                            concat(
                                bit, (byte) '^'
                            );
                        }
                        continue;
                    }
                }
            } else if (bit < 0x5F) {
                if (bit > 0x20) {
                    switch (bit) {
                        case '^':
                        case '#':
                        case ':':
                        case '(':
                        case ')': {
                            concat(
                                (byte) '^'
                            );
                        }
                    }
                } else {
                    switch (bit) {
                        case ' ': {
                            bit = 's';
                            break;
                        }
                        case '\t': {
                            bit = 't';
                            break;
                        }
                        case '\r': {
                            bit = 'r';
                            break;
                        }
                        case '\n': {
                            bit = 'n';
                            break;
                        }
                        default: {
                            concat(
                                bit, (byte) '^'
                            );
                            continue;
                        }
                    }
                    concat(
                        (byte) '^'
                    );
                }
            }
            byte[] it = value;
            if (count != it.length) {
                asset = 0;
                it[count++] = (byte) bit;
            } else {
                grow(count + 1);
                asset = 0;
                value[count++] = (byte) bit;
            }
        }
    }

    /**
     * Serialize to pretty {@link Kat} String
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
     * Serialize to {@link Kat} String
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
     * Serialize to {@link Kat} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
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
     * Serialize to {@link Kat} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     */
    @NotNull
    public static String encode(
        @Nullable String alias,
        @Nullable Object value
    ) {
        return encode(
            alias, value, DEF.writeFlags
        );
    }

    /**
     * Serialize to {@link Kat} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     */
    @NotNull
    public static String encode(
        @Nullable String alias,
        @Nullable Object value, long flags
    ) {
        try (Chan chan = new Kat(flags)) {
            chan.set(
                alias, value
            );
            return chan.toString();
        } catch (Exception e) {
            throw new FatalCrash(
                "Unexpectedly, error serializing to kat", e
            );
        }
    }

    /**
     * Parse {@link Kat} {@link CharSequence}
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
            Algo.KAT, new Event<>(text)
        );
    }

    /**
     * Parse {@link Kat} {@link CharSequence}
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
            Algo.KAT, event
        );
    }

    /**
     * Parse {@link Kat} byte array
     *
     * @param text the specified text to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     * @see Supplier#read(Class, Event)
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

        return INS.read(
            klass, new Event<>(text)
        );
    }

    /**
     * Parse {@link Kat} {@link CharSequence}
     *
     * @param text the specified text to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     * @see Supplier#read(Class, Event)
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

        return INS.read(
            klass, new Event<>(text)
        );
    }

    /**
     * Parse {@link Kat} {@link Event}
     *
     * @param event the specified event to be handled
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     * @see Supplier#read(Class, Event)
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

        return INS.read(klass, event);
    }
}
