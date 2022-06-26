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
package plus.kat.chain;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.crash.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;
import plus.kat.utils.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public final class Alias extends Chain {
    /**
     * empty alias
     */
    public static final Alias
        EMPTY = new Alias();

    /**
     * default
     */
    private Alias() {
        super();
    }

    /**
     * @param size the initial capacity
     */
    public Alias(
        int size
    ) {
        super(size);
    }

    /**
     * @param data the initial byte array
     */
    public Alias(
        @NotNull byte[] data
    ) {
        super(data);
        count = data.length;
    }

    /**
     * @param data specify the {@link Alias} to be mirrored
     */
    public Alias(
        @NotNull Alias data
    ) {
        super(data);
    }

    /**
     * @param bucket the specified {@link Bucket} to be used
     */
    public Alias(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * @param data specify the {@link CharSequence} to be mirrored
     * @see String#getBytes()
     */
    public Alias(
        @Nullable CharSequence data
    ) {
        super();
        if (data != null) chain(
            data, 0, data.length()
        );
    }

    /**
     * Returns a {@link Alias} of this {@link Alias}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @NotNull
    @Override
    public Alias subSequence(
        int start, int end
    ) {
        return new Alias(
            copyBytes(start, end)
        );
    }

    /**
     * Check if it is the correct method name
     */
    public boolean isMethod() {
        if (count == 0) {
            return false;
        }

        for (int i = 0; i < count; i++) {
            byte b = value[i];
            if (b > 0x60) {   // a-z
                if (b < 0x7B) {
                    continue;
                }
                return false;
            }

            if (b > 0x40) {   // A-Z
                if (b < 0x5B
                    || b == 0x5F) {  // _
                    continue;
                }
                return false;
            }

            if (b > 0x2F) {   // 0-9
                if (b < 0x3A
                    && i != 0) {
                    continue;
                }
                return false;
            }

            if (b != 0x24) {  // $
                return false;
            }
        }

        return true;
    }

    /**
     * Creates and returns a copy of this {@link Alias}
     */
    @NotNull
    public Alias copy() {
        return count == 0 ? EMPTY : new Alias(this);
    }

    /**
     * Parses this {@code UTF8} {@link Alias} as a {@code char}
     */
    public char toChar() {
        return Convert.toChar(
            value, count, '?'
        );
    }

    /**
     * Parses this {@link Alias} as a signed decimal {@code byte}
     */
    public byte toByte() {
        return (byte) Convert.toInt(
            value, count, 10, 0
        );
    }

    /**
     * Parses this {@link Alias} as a signed decimal {@code short}
     */
    public short toShort() {
        return (short) Convert.toInt(
            value, count, 10, 0
        );
    }

    /**
     * Parses this {@link Alias} as a signed decimal {@code int}
     */
    public int toInt() {
        return Convert.toInt(
            value, count, 10, 0
        );
    }

    /**
     * Parses this {@link Alias} as a signed decimal {@code int}
     */
    public int toInt(
        int def
    ) {
        return Convert.toInt(
            value, count, 10, def
        );
    }

    /**
     * Parses this {@link Alias} as a signed decimal {@code int}
     *
     * @param radix the radix to be used while parsing {@link Value}
     */
    public int toInt(
        int def, int radix
    ) {
        if (radix < 2 || radix > 36) {
            return def;
        }
        return Convert.toInt(
            value, count, radix, def
        );
    }

    /**
     * Parses this {@link Alias} as a signed decimal {@code long}
     */
    public long toLong() {
        return Convert.toLong(
            value, count, 10L, 0L
        );
    }

    /**
     * Parses this {@link Alias} as a signed decimal {@code long}
     */
    public long toLong(
        long def
    ) {
        return Convert.toLong(
            value, count, 10L, def
        );
    }

    /**
     * Parses this {@link Alias} as a signed decimal {@code long}
     *
     * @param radix the radix to be used while parsing {@link Value}
     */
    public long toLong(
        long def, long radix
    ) {
        if (radix < 2L || radix > 36L) {
            return def;
        }
        return Convert.toLong(
            value, count, radix, def
        );
    }

    /**
     * Parses this {@link Alias} as a {@code float}
     */
    public float toFloat() {
        return Convert.toFloat(
            value, count, 0F
        );
    }

    /**
     * Parses this {@link Alias} as a {@code float}
     */
    public float toFloat(
        float def
    ) {
        return Convert.toFloat(
            value, count, def
        );
    }

    /**
     * Parses this {@link Alias} as a {@code double}
     */
    public double toDouble() {
        return Convert.toDouble(
            value, count, 0D
        );
    }

    /**
     * Parses this {@link Alias} as a {@code double}
     */
    public double toDouble(
        double def
    ) {
        return Convert.toDouble(
            value, count, def
        );
    }

    /**
     * Parses this {@link Alias} as a {@code boolean}
     */
    public boolean toBoolean() {
        return Convert.toBoolean(
            value, count, false
        );
    }

    /**
     * Parses this {@link Alias} as a {@code boolean}
     */
    public boolean toBoolean(
        boolean def
    ) {
        return Convert.toBoolean(
            value, count, def
        );
    }

    /**
     * @param b the {@code byte} to be compared
     */
    public static boolean esc(byte b) {
        switch (b) {
            case '^':
            case '#':
            case ':':
            case '(':
            case ')':
            case '{':
            case '}': {
                return true;
            }
        }
        return false;
    }

    /**
     * @see Alias#Alias(Bucket)
     */
    public static Alias apply() {
        return new Alias(
            $Bucket.INS
        );
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    private static class $Bucket implements Bucket {

        private static final int RANGE;

        static {
            RANGE = Config.get(
                "kat.alias.range", 512
            );
        }

        private static final $Bucket
            INS = new $Bucket();

        @NotNull
        @Override
        public byte[] alloc(
            @NotNull byte[] it, int len, int min
        ) {
            if (min <= RANGE) {
                int cap = it.length;
                if (cap == 0) {
                    return new byte[0x80];
                } else do {
                    cap <<= 1;
                } while (cap < min);

                byte[] result = new byte[cap];
                System.arraycopy(
                    it, 0, result, 0, len
                );

                return result;
            }

            throw new RunCrash(
                "Unexpectedly, Exceeding range '" + RANGE + "' in alias"
            );
        }

        @Override
        public void push(
            @NotNull byte[] it
        ) {
            // NOOP
        }

        @NotNull
        @Override
        public byte[] revert(
            @NotNull byte[] it
        ) {
            return it;
        }
    }
}
