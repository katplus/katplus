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
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import plus.kat.crash.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;
import plus.kat.utils.*;

import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public final class Space extends Alpha implements Type {
    /**
     * empty space
     */
    public static final Space
        EMPTY = new Space();

    public static final Space $ = new Space(Object.class, "$");
    public static final Space $s = new Space(String.class, "s");
    public static final Space $M = new Space(Map.class, "M");
    public static final Space $L = new Space(List.class, "L");

    /**
     * For internal use
     */
    private Space() {
        super(true);
    }

    /**
     * Constructs a final fixed space
     *
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
     * Constructs a final fixed space
     *
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
        asset |= 0x80000002;
        this.type = type;
        this.count = value.length;
    }

    /**
     * Constructs a final fixed space
     *
     * @param data the initial byte array
     */
    public Space(
        @NotNull byte[] data
    ) {
        super(data);
        asset |= Integer.MIN_VALUE;
    }

    /**
     * Constructs a final fixed space
     *
     * @param chain the specified chain to be used
     */
    public Space(
        @NotNull Chain chain
    ) {
        super(chain);
        asset |= Integer.MIN_VALUE;
    }

    /**
     * Constructs a final fixed space
     *
     * @param space the specified space to be used
     */
    public Space(
        @NotNull Space space
    ) {
        super(space);
        asset |= Integer.MIN_VALUE;
    }

    /**
     * Constructs a mutable space
     *
     * @param bucket the specified bucket to be used
     */
    public Space(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * Constructs a mutable space
     *
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
     * Constructs a final fixed space
     *
     * @param sequence the specified sequence to be used
     */
    public Space(
        @Nullable CharSequence sequence
    ) {
        super(sequence);
        asset |= Integer.MIN_VALUE;
    }

    /**
     * Returns the charset of this {@link Space}
     */
    @Override
    public Charset charset() {
        return ISO_8859_1;
    }

    /**
     * Returns a {@link Space} that
     * is a subsequence of this {@link Space}
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
     * Returns true if and only if
     * the chain is describing class
     *
     * @since 0.0.5
     */
    @Override
    public boolean isSpace() {
        return true;
    }

    /**
     * Returns true if this space is a simple
     * validated class name (Camel-Case). If space contains an
     * element that is not in {@code [A-Za-z0-9.$]}, it must return false
     *
     * <pre>{@code
     *  isClass() -> true
     *  // kat.User
     *  // void.User // illegal
     *  // plus.kat.User
     *  // plus.kat.v2.User
     *  // plus.kat.v2.UserName
     *
     *  isClass() -> false
     *  // $
     *  // .
     *  // plus.$a
     *  // plus.kat.1v
     *  // plus.kat.$User
     *  // plus.kat.User$
     *  // plus.kat_v2.User
     *  // plus.kat.I_O
     *  // plus.kat.v2.3Q
     *  // plus.kat.User-Name
     * }</pre>
     *
     * @see Space#isPackage()
     */
    public boolean isClass() {
        int size = count;
        if (size == 0) {
            return false;
        }

        int i = 0, m = 0;
        byte[] it = value;

        for (; i < size; i++) {
            byte b = it[i];
            if (b > 0x60) {   // a-z
                if (b < 0x7B) {
                    continue;
                }
                return false;
            }

            if (b == 0x2E) {   // .
                if (m != i) {
                    m = i + 1;
                    if (m != size) {
                        continue;
                    }
                }
                return false;
            }

            if (b < 0x3A) {   // 0-9
                if (b > 0x2F
                    && i != m) {
                    continue;
                }
                return false;
            }

            if (size - i > 255 ||  // max-len
                b < 0x41 || b > 0x5A) {   // A-Z
                return false;
            }

            for (++i; i < size; i++) {
                byte c = it[i];
                if (c > 0x60) {   // a-z
                    if (c < 0x7B) {
                        continue;
                    }
                    return false;
                }

                if (c > 0x40) {   // A-Z
                    if (c < 0x5B) {
                        continue;
                    }
                    return false;
                }

                if (c < 0x3A) {   // 0-9
                    if (c > 0x2F ||
                        (c == 0x24 &&
                            i + 1 != size)) { // $
                        continue;
                    }
                    return false;
                }

                return false;
            }

            return true;
        }

        return false;
    }

    /**
     * Returns true if this space is a simple
     * validated class package (Lower-Case). If space contains an
     * element that is not in {@code [a-z0-9.]}, it must return false
     *
     * <pre>{@code
     *  isPackage() -> true
     *  // kat
     *  // void // illegal
     *  // plus.kat
     *  // plus.kat.v2
     *
     *  isPackage() -> false
     *  // $
     *  // .
     *  // plus.$a
     *  // plus.kat.1v
     *  // plus.kat_v2
     *  // plus.kat.3q
     *  // plus.kat.V2
     *  // plus.kat.User
     * }</pre>
     *
     * @see Space#isClass()
     */
    public boolean isPackage() {
        int size = count;
        if (size == 0) {
            return false;
        }

        int i = 0, m = 0;
        byte[] it = value;

        for (; i < size; i++) {
            byte b = it[i];
            if (b > 0x60) {   // a-z
                if (b < 0x7B) {
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

            if (b == 0x2E && m != i) {   // .
                m = i + 1;
                if (m != size) {
                    continue;
                }
            }

            return false;
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
     * Returns a fixed space of clone this {@link Space}
     */
    @NotNull
    public Space copy() {
        return count == 0 ? EMPTY : new Space(this);
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

            throw new FatalCrash(
                "Unexpectedly, Exceeding range '" + RANGE + "' in space"
            );
        }
    }
}
