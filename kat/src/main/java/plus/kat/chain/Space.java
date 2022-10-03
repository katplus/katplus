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

import java.math.*;
import java.util.*;
import java.lang.reflect.*;
import java.nio.charset.Charset;

import plus.kat.crash.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;
import plus.kat.utils.*;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author kraity
 * @since 0.0.1
 */
public final class Space extends Dram implements Type {
    /**
     * empty space
     */
    public static final Space
        EMPTY = new Space();

    public static final Space $ = new Space(Object.class, "$");
    public static final Space $M = new Space(Map.class, "M");
    public static final Space $L = new Space(List.class, "L");
    public static final Space $A = new Space(Object[].class, "A");
    public static final Space $S = new Space(Set.class, "S");
    public static final Space $E = new Space(Crash.class, "E");
    public static final Space $s = new Space(String.class, "s");
    public static final Space $n = new Space(Number.class, "n");
    public static final Space $i = new Space(int.class, "i");
    public static final Space $l = new Space(long.class, "l");
    public static final Space $f = new Space(float.class, "f");
    public static final Space $d = new Space(double.class, "d");
    public static final Space $b = new Space(boolean.class, "b");
    public static final Space $c = new Space(char.class, "c");
    public static final Space $o = new Space(byte.class, "o");
    public static final Space $u = new Space(short.class, "u");
    public static final Space $B = new Space(byte[].class, "B");
    public static final Space $I = new Space(BigInteger.class, "I");
    public static final Space $D = new Space(BigDecimal.class, "D");

    /**
     * For internal use
     */
    private Space() {
        super();
    }

    /**
     * @param type the specified type
     */
    public Space(
        @NotNull Type type
    ) {
        this(
            type, type.getTypeName()
        );
    }

    /**
     * @param type the specified type
     * @param name the specified name of type
     */
    public Space(
        @NotNull Type type,
        @NotNull String name
    ) {
        super(
            Binary.latin(name)
        );
        backup = name;
        this.star |= 2;
        this.type = type;
        this.count = value.length;
    }

    /**
     * @param data the initial byte array
     */
    public Space(
        @NotNull byte[] data
    ) {
        super(data);
    }

    /**
     * @param chain the specified chain to be used
     */
    public Space(
        @NotNull Chain chain
    ) {
        super(chain);
    }

    /**
     * @param bucket the specified bucket to be used
     */
    public Space(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * @param chain  the specified chain to be used
     * @param bucket the specified bucket to be used
     */
    public Space(
        @NotNull Chain chain,
        @Nullable Bucket bucket
    ) {
        super(
            chain, bucket
        );
    }

    /**
     * @param sequence the specified sequence to be used
     */
    public Space(
        @Nullable CharSequence sequence
    ) {
        super(sequence);
    }

    /**
     * Returns the modifier type
     */
    @Override
    public Type getType() {
        return type;
    }

    /**
     * Sets the modifier type of {@link Space}
     *
     * @param type the specified type
     * @throws Collapse If the dram is read-only
     */
    @Override
    public void setType(
        @Nullable Type type
    ) {
        if (bucket != null) {
            this.type = type;
        } else {
            throw new Collapse(
                "Unexpectedly, the dram is read-only"
            );
        }
    }

    /**
     * Returns a {@link Space} of this {@link Space}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public Space subSequence(
        int start, int end
    ) {
        return new Space(
            toBytes(start, end)
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
     * Returns a true only
     * if this chain is the name
     *
     * @param name the name to be compared
     */
    @Override
    public boolean is(
        byte name
    ) {
        return count == 1 &&
            value[0] == name;
    }

    /**
     * Returns a true only
     * if this chain is the name
     *
     * @param name the name to be compared
     */
    @Override
    public boolean is(
        char name
    ) {
        return count == 1 &&
            value[0] == name;
    }

    /**
     * Returns a true only
     * if this chain is {@code '$'}
     */
    public boolean isAny() {
        return count == 1 &&
            value[0] == '$';
    }

    /**
     * Returns a true only
     * if this chain is {@code 'M'}
     */
    public boolean isMap() {
        return count == 1 &&
            value[0] == 'M';
    }

    /**
     * Returns a true only
     * if this chain is {@code 'S'}
     */
    public boolean isSet() {
        return count == 1 &&
            value[0] == 'S';
    }

    /**
     * Returns a true only
     * if this chain is {@code 'L'}
     */
    public boolean isList() {
        return count == 1 &&
            value[0] == 'L';
    }

    /**
     * Returns a true only
     * if this chain is {@code 'A'}
     */
    public boolean isArray() {
        return count == 1 &&
            value[0] == 'A';
    }

    /**
     * Returns the ASCII {@link String} for this {@link Space}
     */
    @Override
    public String string() {
        return toString();
    }

    /**
     * Returns the charset of this {@link Space}
     *
     * @since 0.0.5
     */
    @Override
    public Charset charset() {
        return US_ASCII;
    }

    /**
     * @see Space#Space(Bucket)
     */
    public static Space apply() {
        return new Space(
            Buffer.INS
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
     * @author kraity
     * @since 0.0.5
     */
    public static class Buffer implements Bucket {

        private static final int RANGE;

        static {
            RANGE = Config.get(
                "kat.space.range", 256
            );
        }

        public static final Buffer
            INS = new Buffer();

        @Override
        public boolean share(
            @NotNull byte[] it
        ) {
            return false;
        }

        @Override
        public byte[] swop(
            @NotNull byte[] it
        ) {
            return it;
        }

        @Override
        public byte[] apply(
            @NotNull byte[] it, int len, int size
        ) {
            if (size <= RANGE) {
                int cap = it.length;
                if (cap == 0) {
                    return new byte[0x80];
                } else do {
                    cap <<= 1;
                } while (cap < size);

                byte[] result = new byte[cap];
                System.arraycopy(
                    it, 0, result, 0, len
                );

                return result;
            }

            throw new Collapse(
                "Unexpectedly, Exceeding range '" + RANGE + "' in space"
            );
        }
    }
}
