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

import java.lang.reflect.*;
import java.math.*;
import java.util.*;

import plus.kat.crash.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;
import plus.kat.utils.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public final class Space extends Chain implements Type {
    /**
     * empty space
     */
    public static final Space
        EMPTY = new Space();

    /**
     * cached spaces
     */
    public static final Space $ = new Space(new byte[]{'$'}, Object.class);
    public static final Space $M = new Space(new byte[]{'M'}, Map.class);
    public static final Space $L = new Space(new byte[]{'L'}, List.class);
    public static final Space $A = new Space(new byte[]{'A'}, Object[].class);
    public static final Space $S = new Space(new byte[]{'S'}, Set.class);
    public static final Space $E = new Space(new byte[]{'E'}, Crash.class);
    public static final Space $s = new Space(new byte[]{'s'}, String.class);
    public static final Space $n = new Space(new byte[]{'n'}, Number.class);
    public static final Space $i = new Space(new byte[]{'i'}, int.class);
    public static final Space $l = new Space(new byte[]{'l'}, long.class);
    public static final Space $f = new Space(new byte[]{'f'}, float.class);
    public static final Space $d = new Space(new byte[]{'d'}, double.class);
    public static final Space $b = new Space(new byte[]{'b'}, boolean.class);
    public static final Space $c = new Space(new byte[]{'c'}, char.class);
    public static final Space $o = new Space(new byte[]{'o'}, byte.class);
    public static final Space $u = new Space(new byte[]{'u'}, short.class);
    public static final Space $B = new Space(new byte[]{'B'}, byte[].class);
    public static final Space $I = new Space(new byte[]{'I'}, BigInteger.class);
    public static final Space $D = new Space(new byte[]{'D'}, BigDecimal.class);

    /**
     * actual type
     */
    private Type actual;
    private String cache;

    /**
     * default
     */
    private Space() {
        super();
    }

    /**
     * @param b specify the {@code byte[]} to be mirrored
     * @param t specify the {@link Type} associated with this {@link Space}
     */
    private Space(
        byte[] b, Type t
    ) {
        this(b);
        actual = t;
    }

    /**
     * @param data the initial byte array
     */
    private Space(
        byte[] data
    ) {
        super(data);
        count = data.length;
    }

    /**
     * @param bucket the specified {@link Bucket} to be used
     */
    public Space(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * @param type specify the {@link Class} associated with this {@link Space}
     * @throws NullPointerException If the {@code type} is null
     */
    public Space(
        @NotNull Class<?> type
    ) {
        this(type.getName());
        this.actual = type;
    }

    /**
     * @param space specify the {@link String} to be mirrored
     * @param type  specify the {@link Type} associated with this {@link Space}
     * @throws NullPointerException If the {@code space} is null
     */
    public Space(
        @NotNull String space,
        @Nullable Type type
    ) {
        this(space);
        this.actual = type;
    }

    /**
     * @param space specify the {@link String} to be mirrored
     * @throws NullPointerException If the {@code space} is null
     */
    public Space(
        @NotNull String space
    ) {
        super(space.length());
        int i = 0;
        while (i < value.length) {
            char c = space.charAt(i++);
            if (c <= 0x20) {
                continue;
            }
            byte b = (byte) c;
            if (esc(b)) {
                continue;
            }
            value[count++] = b;
        }

        if (i == count) {
            cache = space;
        } else {
            cache = string();
        }
    }

    /**
     * Returns a {@link Space} of this {@link Space}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @NotNull
    @Override
    public Space subSequence(
        int start, int end
    ) {
        return new Space(
            copyBytes(start, end)
        );
    }

    /**
     * Check if it is the correct Class name
     */
    public boolean isClass() {
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
     * Check if it is the correct Class package
     */
    public boolean isPackage() {
        if (count == 0) {
            return false;
        }

        int i = 0, m = 0;
        boolean c = false;

        for (; i < count; i++) {
            byte b = value[i];
            if (b > 0x60) {   // a-z
                if (b < 0x7B) {
                    continue;
                }
                return false;
            }

            if (b == 0x2E) {   // .
                if (c) {
                    return false;
                }

                if (m != i) {
                    m = i + 1;
                    if (m != count) {
                        continue;
                    }
                }
                return false;
            }

            if (b > 0x40) {   // A-Z
                if (b < 0x5B) {
                    continue;
                }

                if (b == 0x5F) {  // _
                    c = true;
                    continue;
                }
                return false;
            }

            if (b > 0x2F) {   // 0-9
                if (b < 0x3A
                    && i != m) {
                    continue;
                }
                return false;
            }

            if (b == 0x24) {  // $
                c = true;
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * @param space the space to be compared
     */
    @Override
    public boolean is(
        byte space
    ) {
        return count == 1 &&
            value[0] == space;
    }

    /**
     * @param space the space to be compared
     */
    @Override
    public boolean is(
        char space
    ) {
        return count == 1 &&
            value[0] == (byte) space;
    }

    /**
     * Returns a {@code boolean}
     * {@code true} only if {@link Space} is {@code $}
     */
    public boolean isAny() {
        return count == 1 &&
            value[0] == '$';
    }

    /**
     * Returns a {@code boolean}
     * {@code true} only if {@link Space} is {@code M}
     */
    public boolean isMap() {
        return count == 1 &&
            value[0] == 'M';
    }

    /**
     * Returns a {@code boolean}
     * {@code true} only if {@link Space} is {@code S}
     */
    public boolean isSet() {
        return count == 1 &&
            value[0] == 'S';
    }

    /**
     * Returns a {@code boolean}
     * {@code true} only if {@link Space} is {@code L}
     */
    public boolean isList() {
        return count == 1 &&
            value[0] == 'L';
    }

    /**
     * Returns a {@code boolean}
     * {@code true} only if {@link Space} is {@code A}
     */
    public boolean isArray() {
        return count == 1 &&
            value[0] == 'A';
    }

    /**
     * @param c the specified space
     */
    @NotNull
    public static Space of(char c) {
        if (c >= 0x80 || c <= 0x20) {
            return EMPTY;
        }

        byte b = (byte) c;
        if (esc(b)) {
            return EMPTY;
        }

        return new Space(
            new byte[]{b}
        );
    }

    /**
     * @param b the specified space
     */
    @NotNull
    public static Space of(byte b) {
        if (b <= 0x20 || esc(b)) {
            return EMPTY;
        }
        return new Space(
            new byte[]{b}
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
     * Returns a {@code Type} representing the specified actual type
     */
    @Nullable
    @Override
    public Type getType() {
        return actual;
    }

    /**
     * @throws CallCrash Not support
     */
    @Override
    public void setType(
        @Nullable Type type
    ) {
        throw new CallCrash(
            "Unexpectedly, not support"
        );
    }

    /**
     * Returns a string describing this {@link Space}
     */
    @Override
    public String getTypeName() {
        return toString();
    }

    /**
     * Returns the {@code byte[]} of this {@link Space} as a {@link String} cache
     */
    @Override
    @SuppressWarnings("deprecation")
    public String toString() {
        if (count == 0) {
            return "$";
        }

        String ca = cache;
        if (ca != null) {
            return ca;
        }

        return new String(
            value, 0, 0, count
        );
    }

    /**
     * Returns the {@code byte[]} of this {@link Space} as a {@link String}
     *
     * @param b the beginning index, inclusive
     * @param e the ending index, exclusive
     * @throws IndexOutOfBoundsException if the beginIndex is negative
     */
    @Override
    @SuppressWarnings("deprecation")
    public String toString(
        int b, int e
    ) {
        int l = e - b;
        if (l <= 0 || e > count) {
            return "$";
        }

        return new String(
            value, 0, b, l
        );
    }

    /**
     * @see Space#Space(Bucket)
     */
    public static Space apply() {
        return new Space(
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
                "kat.space.range", 256
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

            throw new CallCrash(
                "Unexpectedly, Exceeding range '" + RANGE + "' in space"
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
