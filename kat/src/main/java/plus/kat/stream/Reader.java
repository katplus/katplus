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
package plus.kat.stream;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.crash.*;
import plus.kat.utils.Config;

import java.util.concurrent.atomic.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Reader {
    /**
     * Read a byte in {@link Reader} and cursor switch to next
     *
     * @throws IOCrash Unexpected crash if this {@link Reader} has been closed or I/O error occurs
     */
    byte read() throws IOCrash;

    /**
     * Check {@link Reader} for readable bytes
     *
     * @throws IOCrash Unexpected crash if this {@link Reader} has been closed or I/O error occurs
     */
    boolean also() throws IOCrash;

    /**
     * Read a byte if {@link Reader} has readable bytes, otherwise raise IOCrash
     *
     * @throws IOCrash Unexpected crash if this {@link Reader} has been closed or I/O error occurs
     */
    default byte next() throws IOCrash {
        if (also()) {
            return read();
        }

        throw new UnexpectedCrash(
            "Unexpectedly, no readable byte"
        );
    }

    /**
     * Close this {@link Reader}
     */
    default void close() {
        // nothing
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    class Bucket extends AtomicReferenceArray<byte[]> {

        static final int SIZE, SCALE;

        static {
            SIZE = Config.get(
                "kat.reader.size", 4
            );
            SCALE = Config.get(
                "kat.reader.scale", 1024 * 4
            );
        }

        static final Bucket
            INS = new Bucket();

        private Bucket() {
            super(SIZE);
        }

        @NotNull
        public byte[] alloc(
            @NotNull Reader r
        ) {
            return alloc(
                r, SCALE
            );
        }

        @NotNull
        public byte[] alloc(
            @NotNull Reader r,
            @NotNull int scale
        ) {
            int h = r.hashCode();
            h = h ^ (h >> 16);

            byte[] it = getAndSet(
                h % SIZE, null
            );

            if (it != null) {
                return it;
            }

            return new byte[scale];
        }

        public void revert(
            @NotNull Reader r,
            @Nullable byte[] it
        ) {
            if (it != null &&
                it.length >= SCALE) {
                int h = r.hashCode();
                h = h ^ (h >> 16);

                this.set(
                    h % SIZE, it
                );
            }
        }
    }
}
