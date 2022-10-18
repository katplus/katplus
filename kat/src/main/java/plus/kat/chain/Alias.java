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
public final class Alias extends Alpha {
    /**
     * empty alias
     */
    public static final Alias
        EMPTY = new Alias();

    /**
     * For internal use
     */
    private Alias() {
        super(true);
    }

    /**
     * Constructs a final fixed alias
     *
     * @param data the initial byte array
     */
    public Alias(
        @NotNull byte[] data
    ) {
        super(data);
        asset |= Integer.MIN_VALUE;
    }

    /**
     * Constructs a final fixed alias
     *
     * @param chain the specified chain to be used
     */
    public Alias(
        @NotNull Chain chain
    ) {
        super(chain);
        asset |= Integer.MIN_VALUE;
    }

    /**
     * Constructs a mutable alias
     *
     * @param bucket the specified bucket to be used
     */
    public Alias(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * Constructs a mutable alias
     *
     * @param chain  the specified chain to be used
     * @param bucket the specified bucket to be used
     */
    public Alias(
        @NotNull Chain chain,
        @Nullable Bucket bucket
    ) {
        super(
            chain, bucket
        );
    }

    /**
     * Constructs a final fixed alias
     *
     * @param sequence the specified sequence to be used
     */
    public Alias(
        @Nullable CharSequence sequence
    ) {
        super(sequence);
        asset |= Integer.MIN_VALUE;
    }

    /**
     * Returns a {@link Alias} that
     * is a subsequence of this {@link Alias}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public Alias subSequence(
        int start, int end
    ) {
        return new Alias(
            toBytes(start, end)
        );
    }

    /**
     * Returns true if this alias is a simple
     * validated method name (Camel-Case). If alias contains an
     * element that is not in {@code [A-Za-z0-9]}, it must return false
     *
     * <pre>{@code
     *  isMethod() -> true
     *  // kat
     *  // v2
     *  // void // illegal
     *  // isEmpty
     *  // hashCode
     *
     *  isMethod() -> false
     *  // $
     *  // .
     *  // _
     *  // 3q
     *  // _get
     *  // Kat
     *  // Get
     *  // get_name
     *  // get$name
     * }</pre>
     *
     * @see Space#isClass()
     */
    public boolean isMethod() {
        int size = count;
        if (size == 0) {
            return false;
        }

        int i = 0;
        byte[] it = value;

        byte m = it[i++];
        if (m < 0x61 ||
            m > 0x7A) {
            return false;
        }

        for (; i < size; i++) {
            byte b = it[i];
            if (b > 0x60) {   // a-z
                if (b < 0x7B) {
                    continue;
                }
                return false;
            }

            if (b > 0x40) {   // A-Z
                if (b < 0x5B) {
                    continue;
                }
                return false;
            }

            if (b > 0x2F) {   // 0-9
                if (b < 0x3A) {
                    continue;
                }
                return false;
            }

            return false;
        }

        return true;
    }

    /**
     * Returns a fixed alias of clone this {@link Alias}
     */
    @NotNull
    public Alias copy() {
        return count == 0 ? EMPTY : new Alias(this);
    }

    /**
     * @see Alias#Alias(Bucket)
     */
    public static Alias apply() {
        return new Alias(
            Buffer.INS
        );
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    public static class Buffer implements Bucket {

        private static final int RANGE;

        static {
            RANGE = Config.get(
                "kat.alias.range", 512
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
                "Unexpectedly, Exceeding range '" + RANGE + "' in alias"
            );
        }
    }
}
