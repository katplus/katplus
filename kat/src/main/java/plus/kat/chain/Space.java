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

import plus.kat.crash.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;
import plus.kat.utils.*;

import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Space extends Alpha implements Type {
    /**
     * Constructs an empty space
     */
    public Space() {
        super();
    }

    /**
     * Constructs a mutable space
     *
     * @param data the specified array to be used
     */
    public Space(
        @NotNull byte[] data
    ) {
        super(data);
    }

    /**
     * Constructs a mutable space
     *
     * @param chain the specified chain to be used
     */
    public Space(
        @NotNull Chain chain
    ) {
        super(chain);
    }

    /**
     * Constructs a mutable space
     *
     * @param space the specified space to be used
     */
    public Space(
        @NotNull Space space
    ) {
        super(space);
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
     * @param sequence the specified sequence to be used
     */
    public Space(
        @Nullable CharSequence sequence
    ) {
        super(sequence);
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
     * Uses this space directly
     * as the specified name {@link Space}
     *
     * <pre>{@code
     *   Space space = ...
     *   space.as((byte) 'M'); // the space: M
     *   space.as((byte) 'L'); // the space: L
     * }</pre>
     *
     * @param name the specified name
     */
    public Space as(byte name) {
        count = 0;
        join(name);
        return this;
    }

    /**
     * Uses this space directly
     * as the specified name {@link Space}
     *
     * <pre>{@code
     *   Space space = ...
     *   space.as('M'); // the space: M
     *   space.as('L'); // the space: L
     * }</pre>
     *
     * @param name the specified name
     */
    public Space as(char name) {
        count = 0;
        join(name);
        return this;
    }

    /**
     * Uses this space directly
     * as the specified name {@link Space}
     *
     * <pre>{@code
     *   Space space = ...
     *   space.as("plus.kat.User"); // the space: plus.kat.User
     *   space.as("plus.kat.Entity"); // the space: plus.kat.Entity
     * }</pre>
     *
     * @param name the specified name
     */
    public Space as(CharSequence name) {
        count = 0;
        join(
            name, 0, name.length()
        );
        return this;
    }

    /**
     * Returns a true only
     * if this chain is {@code '$'}
     */
    public boolean isAny() {
        return count == 1 && value[0] == '$';
    }

    /**
     * Returns a true only
     * if this chain is {@code 'M'}
     */
    public boolean isMap() {
        return count == 1 && value[0] == 'M';
    }

    /**
     * Returns a true only
     * if this chain is {@code 'S'}
     */
    public boolean isSet() {
        return count == 1 && value[0] == 'S';
    }

    /**
     * Returns a true only
     * if this chain is {@code 'L'}
     */
    public boolean isList() {
        return count == 1 && value[0] == 'L';
    }

    /**
     * Returns a true only
     * if this chain is {@code 'A'}
     */
    public boolean isArray() {
        return count == 1 && value[0] == 'A';
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
        public boolean join(
            @NotNull byte[] it
        ) {
            return false;
        }

        @Override
        public byte[] swap(
            @NotNull byte[] it
        ) {
            return it;
        }

        @Override
        public byte[] alloc(
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
