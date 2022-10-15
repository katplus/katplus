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
public class Doc extends Steam implements Chan {

    protected Supplier supplier;

    /**
     * Constructs a default xml
     */
    public Doc() {
        this(0L, INS);
    }

    /**
     * Constructs a xml with the specified flags
     *
     * @param flags the specified flags
     */
    public Doc(
        long flags
    ) {
        this(flags, INS);
    }

    /**
     * Constructs a xml with the specified plan
     *
     * @param plan the specified plan
     */
    public Doc(
        @NotNull Plan plan
    ) {
        this(plan.writeFlags);
    }

    /**
     * Constructs a xml with the specified flags and supplier
     *
     * @param flags    the specified flags
     * @param supplier the specified supplier
     */
    public Doc(
        @NotNull long flags,
        @NotNull Supplier supplier
    ) {
        super(flags);
        this.supplier = supplier;
    }

    /**
     * Constructs a xml with the specified plan and supplier
     *
     * @param plan     the specified plan
     * @param supplier the specified supplier
     */
    public Doc(
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
        return Algo.DOC;
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
            alias, "items", fitter
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
        if (alias == null) {
            if (space == null) {
                return false;
            } else {
                alias = space;
            }
        }

        int d = depth;
        if (0 <= d) {
            ++depth;
            if (d != 0) {
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

        concat((byte) '<');
        emit(alias);
        concat((byte) '>');

        if (fitter != null) {
            fitter.accept(this);
        }

        if (0 <= d) {
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

        concat((byte) '<');
        concat((byte) '/');
        emit(alias);
        concat((byte) '>');
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
                alias, "item", null
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
                        alias, "item", null
                    );
                }
            }
        }

        if (alias == null) {
            alias = coder.getSpace();
        }
        Boolean flag = coder.getFlag();

        int d = depth;
        if (0 <= d) {
            if (flag != null) ++depth;
            if (d != 0) {
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

        concat((byte) '<');
        emit(alias);
        concat((byte) '>');

        if (flag == null) {
            coder.write(
                (Flow) this, object
            );
        } else {
            coder.write(
                (Chan) this, object
            );
            if (0 <= d) {
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
        }

        concat((byte) '<');
        concat((byte) '/');
        emit(alias);
        concat((byte) '>');
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
     * Concatenates the byte value to this {@link Doc},
     * which will be escaped if it is a special character
     *
     * @param b the specified byte value to be appended
     */
    @Override
    public void emit(byte b) {
        asset = 0;
        switch (b) {
            case '<': {
                grow(count + 4);
                byte[] it = value;
                it[count++] = '&';
                it[count++] = 'l';
                it[count++] = 't';
                it[count++] = ';';
                return;
            }
            case '>': {
                grow(count + 4);
                byte[] it = value;
                it[count++] = '&';
                it[count++] = 'g';
                it[count++] = 't';
                it[count++] = ';';
                return;
            }
            case '&': {
                grow(count + 5);
                byte[] it = value;
                it[count++] = '&';
                it[count++] = 'a';
                it[count++] = 'm';
                it[count++] = 'p';
                it[count++] = ';';
                return;
            }
            default: {
                byte[] it = value;
                if (count != it.length) {
                    it[count++] = b;
                } else {
                    grow(count + 1);
                    value[count++] = b;
                }
            }
        }
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
        @Nullable String alias,
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
        @Nullable String alias,
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
}
