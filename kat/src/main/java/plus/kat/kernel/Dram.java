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
package plus.kat.kernel;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.crash.*;
import plus.kat.stream.*;
import plus.kat.utils.*;

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.4
 */
public class Dram extends Chain {

    protected Type type;

    /**
     * Constructs an empty dram
     */
    public Dram() {
        super();
    }

    /**
     * Constructs a dram with the specified size
     *
     * @param size the initial capacity
     */
    public Dram(
        int size
    ) {
        super(size);
    }

    /**
     * Constructs an empty dram
     *
     * @param fixed the specified state
     */
    public Dram(
        boolean fixed
    ) {
        super(fixed);
    }

    /**
     * Constructs a dram with the specified data
     *
     * @param data the initial byte array
     */
    public Dram(
        @NotNull byte[] data
    ) {
        super(data);
        count = data.length;
    }

    /**
     * Constructs a dram with the specified chain
     *
     * @param chain the specified chain to be used
     */
    public Dram(
        @NotNull Chain chain
    ) {
        super(chain);
    }

    /**
     * Constructs an empty dram with the specified chain
     *
     * @param bucket the specified bucket to be used
     */
    public Dram(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * Constructs a dram with the specified chain and bucket
     *
     * @param chain  the specified chain to be used
     * @param bucket the specified bucket to be used
     */
    public Dram(
        @NotNull Chain chain,
        @Nullable Bucket bucket
    ) {
        super(chain);
        this.bucket = bucket;
    }

    /**
     * Constructs a chain with the specified sequence
     *
     * @param sequence the specified sequence to be used
     */
    public Dram(
        @Nullable CharSequence sequence
    ) {
        super();
        if (sequence != null) {
            int len = sequence.length();
            if (len != 0) {
                chain(
                    sequence, 0, len
                );
                star |= 2;
                backup = sequence.toString();
            }
        }
    }

    /**
     * Returns a {@link Dram} that
     * is a subsequence of this {@link Dram}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public Dram subSequence(
        int start, int end
    ) {
        return new Dram(
            toBytes(start, end)
        );
    }

    /**
     * Adds the specified byte value
     *
     * @param data the specified byte value
     * @throws Collapse If the chain is finally fixed
     * @see Chain#isFixed()
     * @since 0.0.5
     */
    public void add(
        byte data
    ) {
        if (0 <= star) {
            byte[] it = value;
            if (count != it.length) {
                star = 0;
                it[count++] = data;
            } else {
                grow(count + 1);
                star = 0;
                value[count++] = data;
            }
        } else {
            throw new Collapse(
                "Unexpectedly, the chain is finally fixed"
            );
        }
    }

    /**
     * Sets the value of the specified location
     *
     * @param i    the specified index
     * @param data the specified value
     * @throws Collapse                       If the chain is finally fixed
     * @throws ArrayIndexOutOfBoundsException if the index argument is negative
     * @see Chain#isFixed()
     * @since 0.0.5
     */
    public void set(
        int i, byte data
    ) {
        if (0 <= star) {
            byte[] it = value;
            if (i < it.length) {
                star = 0;
                it[i] = data;
            }
        } else {
            throw new Collapse(
                "Unexpectedly, the chain is finally fixed"
            );
        }
    }

    /**
     * Sets the specified length of this chain
     *
     * <pre>{@code
     *  Dram dram = ..
     *  dram.add("plus.kat");
     *  dram.slip(3);
     *  int length = value.length(); // 3
     * }</pre>
     *
     * @param length the specified length
     * @throws Collapse                       If the chain is finally fixed
     * @throws ArrayIndexOutOfBoundsException if the index argument is negative or out of range
     * @see Chain#isFixed()
     * @since 0.0.5
     */
    public void slip(
        int length
    ) {
        if (0 <= star) {
            if (length == 0) {
                star = 0;
                count = 0;
            } else {
                if (length < 0 || length > value.length) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                star = 0;
                count = length;
            }
        } else {
            throw new Collapse(
                "Unexpectedly, the chain is finally fixed"
            );
        }
    }

    /**
     * Returns the modifier type
     */
    @Nullable
    public Type getType() {
        return type;
    }

    /**
     * Sets the modifier type of {@link Dram}
     *
     * @param type the specified type
     * @throws Collapse If the chain is finally fixed
     * @see Chain#isFixed()
     */
    public void setType(
        @Nullable Type type
    ) {
        if (0 <= star) {
            this.type = type;
        } else {
            throw new Collapse(
                "Unexpectedly, the chain is finally fixed"
            );
        }
    }

    /**
     * Clean this {@link Dram}
     *
     * @throws Collapse If the chain is finally fixed
     * @see Chain#isFixed()
     * @since 0.0.5
     */
    public void clean() {
        if (0 <= star) {
            hash = 0;
            star = 0;
            count = 0;
            type = null;
            backup = null;
        } else {
            throw new Collapse(
                "Unexpectedly, the chain is finally fixed"
            );
        }
    }

    /**
     * Clear this {@link Dram}
     *
     * @throws Collapse If the chain is finally fixed
     * @see Chain#isFixed()
     * @since 0.0.5
     */
    public void clear() {
        this.clean();
        byte[] it = value;
        if (it.length != 0) {
            Bucket bt = bucket;
            if (bt != null) {
                value = bt.swop(it);
            }
        }
    }

    /**
     * Close this {@link Dram}
     *
     * @throws Collapse If the chain is finally fixed
     * @see Chain#isFixed()
     * @since 0.0.5
     */
    public void close() {
        this.clean();
        byte[] it = value;
        if (it.length != 0) {
            Bucket bt = bucket;
            if (bt != null) {
                bt.share(it);
                value = EMPTY_BYTES;
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.5
     */
    public static class Memory implements Bucket {

        public static final int SIZE, SCALE;

        static {
            SIZE = Config.get(
                "kat.memory.size", 4
            );
            SCALE = Config.get(
                "kat.memory.scale", 1024 * 4
            );
        }

        public static final Memory
            INS = new Memory();

        private final byte[][]
            bucket = new byte[SIZE][];

        @NotNull
        public byte[] alloc() {
            Thread th = Thread.currentThread();
            int tr = th.hashCode() & 0xFFFFFF;

            byte[] it;
            int ix = tr % SIZE;

            synchronized (this) {
                it = bucket[ix];
                bucket[ix] = null;
            }

            if (it != null &&
                SCALE <= it.length) {
                return it;
            }

            return new byte[SCALE];
        }

        @Override
        public boolean share(
            @Nullable byte[] it
        ) {
            if (it != null && SCALE == it.length) {
                Thread th = Thread.currentThread();
                int ix = (th.hashCode() & 0xFFFFFF) % SIZE;
                synchronized (this) {
                    bucket[ix] = it;
                }
                return true;
            }
            return false;
        }

        @Override
        public byte[] swop(
            @Nullable byte[] it
        ) {
            this.share(it);
            return EMPTY_BYTES;
        }

        @Override
        public byte[] apply(
            @NotNull byte[] it, int len, int size
        ) {
            Thread th = Thread.currentThread();
            int ix = (th.hashCode() & 0xFFFFFF) % SIZE;

            byte[] data;
            if (size <= SCALE) {
                synchronized (this) {
                    data = bucket[ix];
                    bucket[ix] = null;
                }
                if (data == null ||
                    SCALE > it.length) {
                    data = new byte[SCALE];
                }
                if (it.length != 0) {
                    System.arraycopy(
                        it, 0, data, 0, len
                    );
                }
            } else {
                int cap = it.length +
                    (it.length >> 1);
                if (cap < size) {
                    cap = size;
                }
                data = new byte[cap];
                if (it.length != 0) {
                    System.arraycopy(
                        it, 0, data, 0, len
                    );

                    if (SCALE == it.length) {
                        synchronized (this) {
                            bucket[ix] = it;
                        }
                    }
                }
            }

            return data;
        }
    }
}
