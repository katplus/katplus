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

/**
 * @author kraity
 * @since 0.0.1
 */
public class Json extends Steam implements Chan {

    private static final byte
        LP = '[', RP = ']',
        LB = '{', RB = '}';

    protected boolean blank;
    protected Supplier supplier;

    /**
     * Constructs a default json
     */
    public Json() {
        this(0L, INS);
    }

    /**
     * Constructs a json with the specified flags
     *
     * @param flags the specified flags
     */
    public Json(
        long flags
    ) {
        this(flags, INS);
    }

    /**
     * Constructs a json with the specified plan
     *
     * @param plan the specified plan
     */
    public Json(
        @NotNull Plan plan
    ) {
        this(plan.writeFlags);
    }

    /**
     * Constructs a json with the specified flags and supplier
     *
     * @param flags    the specified flags
     * @param supplier the specified supplier
     */
    public Json(
        @NotNull long flags,
        @NotNull Supplier supplier
    ) {
        super(flags);
        this.blank = true;
        this.supplier = supplier;
    }

    /**
     * Constructs a json with the specified plan and supplier
     *
     * @param plan     the specified plan
     * @param supplier the specified supplier
     */
    public Json(
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
        return Algo.JSON;
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
        if (blank) {
            blank = false;
        } else {
            concat(
                (byte) ','
            );
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

        if (alias != null) {
            concat((byte) '"');
            emit(alias);
            concat((byte) '"');
            concat((byte) ':');
        }

        if (fitter == null) {
            grow(count + 4);
            asset = 0;
            value[count++] = 'n';
            value[count++] = 'u';
            value[count++] = 'l';
            value[count++] = 'l';
            return true;
        }

        concat(LB);
        blank = true;
        if (d < 0) {
            fitter.accept(this);
        } else {
            ++depth;
            fitter.accept(this);
            --depth;
            if (d == 0) {
                grow(count + 1);
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

        if (blank) {
            blank = false;
        } else {
            concat(
                (byte) ','
            );
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

        if (alias != null) {
            concat((byte) '"');
            emit(alias);
            concat((byte) '"');
            concat((byte) ':');
        }

        Boolean flag = coder.getFlag();
        if (flag == null) {
            if (Boolean.FALSE ==
                coder.getBorder(this)) {
                coder.write(
                    (Flow) this, object
                );
            } else {
                concat((byte) '"');
                coder.write(
                    (Flow) this, object
                );
                concat((byte) '"');
            }
        } else {
            concat(
                flag ? LB : LP
            );
            blank = true;
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
                    grow(count + 1);
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
            concat(
                flag ? RB : RP
            );
        }
        return true;
    }

    /**
     * Returns the internal {@link Steam}
     */
    @NotNull
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
     * Concatenates the byte value to this {@link Json},
     * which will be escaped if it is a special character
     *
     * @param bt the specified byte value to be appended
     */
    @Override
    public void emit(
        byte bt
    ) throws IOException {
        if (bt < 0x5D) {
            if (bt > 0x1F) {
                if (bt == '"' ||
                    bt == '\\') {
                    concat(
                        (byte) '\\'
                    );
                }
            } else {
                switch (bt) {
                    case '\b': {
                        bt = 'b';
                        break;
                    }
                    case '\f': {
                        bt = 'f';
                        break;
                    }
                    case '\t': {
                        bt = 't';
                        break;
                    }
                    case '\r': {
                        bt = 'r';
                        break;
                    }
                    case '\n': {
                        bt = 'n';
                        break;
                    }
                    default: {
                        concat(
                            (char) bt
                        );
                        return;
                    }
                }
                concat(
                    (byte) '\\'
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
     * Serialize to pretty {@link Json} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     */
    @NotNull
    public static String pretty(
        @Nullable Object value
    ) {
        return encode(
            value, Flag.PRETTY | DEF.writeFlags
        );
    }

    /**
     * Serialize to {@link Json} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     */
    @NotNull
    public static String encode(
        @Nullable Object value
    ) {
        return encode(
            value, DEF.writeFlags
        );
    }

    /**
     * Serialize to {@link Json} String
     *
     * @param value the specified value to serialized
     * @throws FatalCrash If an error occurs in serialization
     * @since 0.0.2
     */
    @NotNull
    public static String encode(
        @Nullable Object value, long flags
    ) {
        try (Json chan = new Json(flags)) {
            chan.set(
                null, value
            );
            return chan.toString();
        } catch (Exception e) {
            throw new FatalCrash(
                "Unexpectedly, error serializing to json", e
            );
        }
    }

    /**
     * Parse {@link Json} {@link CharSequence}
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
            Algo.JSON, new Event<>(text)
        );
    }

    /**
     * Parse {@link Json} {@link CharSequence}
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
            Algo.JSON, event
        );
    }

    /**
     * Parse {@link Json} byte array
     *
     * @param text the specified text to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     * @see Supplier#parse(Class, Event)
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
     * @param text the specified text to be parsed
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     * @see Supplier#parse(Class, Event)
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
     * @param event the specified event to be handled
     * @throws Collapse   If parsing fails or the result is null
     * @throws FatalCrash If no spare available for klass is found
     * @see Supplier#parse(Class, Event)
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
}
