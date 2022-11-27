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
import plus.kat.utils.*;
import plus.kat.stream.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Value extends Chain {
    /**
     * Constructs a mutable value
     */
    public Value() {
        super();
    }

    /**
     * Constructs a mutable value
     *
     * @param size the initial capacity
     */
    public Value(
        int size
    ) {
        super(size);
    }

    /**
     * Constructs a mutable value
     *
     * @param data the initial byte array
     */
    public Value(
        @NotNull byte[] data
    ) {
        super(data);
    }

    /**
     * Constructs a mutable value
     *
     * @param data the specified chain to be used
     */
    public Value(
        @NotNull Chain data
    ) {
        super(data);
    }

    /**
     * Constructs a mutable value
     *
     * @param bucket the specified bucket to be used
     */
    public Value(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * Constructs a mutable value
     *
     * @param sequence the specified sequence to be used
     */
    public Value(
        @Nullable CharSequence sequence
    ) {
        super(sequence);
    }

    /**
     * Returns a {@link Value} that
     * is a subsequence of this {@link Value}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public Value subSequence(
        int start, int end
    ) {
        return new Value(
            toBytes(start, end)
        );
    }

    /**
     * Returns an empty value with default bucket
     */
    public static Value apply() {
        return new Value(
            Buffer.INS
        );
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Buffer implements Bucket {

        private static final int SIZE, LIMIT, SCALE, VALVE;

        static {
            SIZE = Config.get(
                "kat.value.size", 8
            );
            LIMIT = Config.get(
                "kat.value.limit", 16
            );

            if (LIMIT < SIZE) {
                throw new Error(
                    "Bucket's size(" + SIZE + ") cannot be greater than the limit(" + LIMIT + ")"
                );
            }

            SCALE = Config.get(
                "kat.value.scale", 1024
            );
            VALVE = SCALE - 1;
        }

        public static final Buffer
            INS = new Buffer();

        private final byte[][]
            bucket = new byte[SIZE][];

        @Override
        public boolean join(
            @NotNull byte[] it
        ) {
            int i = it.length;
            if (VALVE <= i && (i /= SCALE) < SIZE) {
                synchronized (this) {
                    bucket[i] = it;
                    return true;
                }
            }
            return false;
        }

        @Override
        public byte[] swap(
            @NotNull byte[] it
        ) {
            int i = it.length / SCALE;
            if (i == 0) {
                return it;
            }

            byte[] data;
            synchronized (this) {
                if (i < SIZE) {
                    bucket[i] = it;
                }
                data = bucket[0];
                bucket[0] = null;
            }
            return data == null ? EMPTY_BYTES : data;
        }

        @Override
        public byte[] alloc(
            @NotNull byte[] it, int len, int size
        ) {
            byte[] data;
            int i = size / SCALE;

            if (i < SIZE) {
                synchronized (this) {
                    data = bucket[i];
                    bucket[i] = null;
                }
                if (data == null ||
                    data.length < size) {
                    data = new byte[i * SCALE + VALVE];
                }
            } else {
                if (i < LIMIT) {
                    data = new byte[i * SCALE + VALVE];
                } else {
                    throw new FatalCrash(
                        "Exceeding range '" + LIMIT * SCALE + "' in value"
                    );
                }
            }

            if ((i = it.length) != 0) {
                System.arraycopy(
                    it, 0, data, 0, len
                );

                if (VALVE <= i && (i /= SCALE) < SIZE) {
                    synchronized (this) {
                        bucket[i] = it;
                    }
                }
            }

            return data;
        }
    }
}
