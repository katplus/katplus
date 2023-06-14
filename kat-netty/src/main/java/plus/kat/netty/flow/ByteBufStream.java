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
package plus.kat.netty.flow;

import plus.kat.*;
import plus.kat.actor.*;
import plus.kat.chain.*;
import plus.kat.stream.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;

import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class ByteBufStream extends UnpooledHeapByteBuf {

    static final ByteBufAllocator
        ALLOC = ByteBufAllocator.DEFAULT;

    final Bucket bucket;

    /**
     * Constructs a ByteBufStream for this array
     *
     * @param buf the specified buffer
     * @throws NullPointerException If the buffer is null
     */
    public ByteBufStream(
        byte[] buf
    ) {
        this(buf, null);
    }

    /**
     * Constructs a ByteBufStream for this array
     *
     * @param buf    the specified buffer
     * @param bucket the specified bucket for release
     * @throws NullPointerException If the buffer is null
     */
    public ByteBufStream(
        byte[] buf,
        Bucket bucket
    ) {
        super(
            ALLOC,
            buf, buf.length
        );
        this.bucket = bucket;
    }

    /**
     * Returns a {@link ByteBuf} of the chan
     *
     * <pre>{@code
     *  Chan chan = ...
     *  ByteBuf buffer = ByteBufStream.of(chan);
     *  chan.close();
     *  // use buffer here
     *  buffer.release(); // finally, call #release
     *
     *  try(Chan chan = ...) {
     *     ByteBuf buffer = ByteBufStream.of(chan);
     *     // use buffer here
     *     buffer.release(); // finally, call #release
     *  }
     * }</pre>
     *
     * @param chan the specified chan for reading
     */
    @NotNull
    public static ByteBuf of(
        @NotNull Chan chan
    ) {
        if (chan instanceof Stream) {
            return of(
                (Stream) chan
            );
        } else {
            return new ByteBufStream(
                chan.toBinary()
            );
        }
    }

    /**
     * Returns a {@link ByteBuf} of the space
     *
     * <pre>{@code
     *  Space space = ...
     *  ByteBuf buffer = ByteBufStream.of(space);
     *  // use buffer here
     *  // finally, can not call buffer.release()
     * }</pre>
     *
     * @param space the specified space for reading
     */
    @NotNull
    public static ByteBuf of(
        @NotNull Space space
    ) {
        byte[] buffer = space.flow();
        if (buffer != null) {
            return new ByteBufStream(
                buffer, null
            ).writerIndex(
                space.size()
            );
        } else {
            return new ByteBufStream(
                valueOf(space), null
            );
        }
    }

    /**
     * Returns a {@link ByteBuf} of the stream
     *
     * <pre>{@code
     *  Stream stream = ...
     *  ByteBuf buffer = ByteBufStream.of(chan);
     *  stream.close();
     *  // use buffer here
     *  buffer.release(); // finally, call #release
     *
     *  try(Stream stream = ...) {
     *     ByteBuf buffer = ByteBufStream.of(stream);
     *     // use buffer here
     *     buffer.release(); // finally, call #release
     *  }
     * }</pre>
     *
     * @param stream the specified stream for reading
     */
    @NotNull
    public static ByteBuf of(
        @NotNull Stream stream
    ) {
        byte[] buffer = valueOf(stream);
        if (buffer != null) {
            return new ByteBufStream(
                buffer, isolate(stream)
            ).writerIndex(
                stream.size()
            );
        } else {
            return new ByteBufStream(
                stream.toBinary(), null
            );
        }
    }

    /**
     * Resumes the specified old array to the default bucket
     *
     * @param array the specified array that will be released
     */
    @Override
    protected void freeArray(byte[] array) {
        if (bucket != null) {
            bucket.store(array);
        }
    }

    /**
     * Borrows an array of the capacity from the default bucket
     *
     * @param capacity the specified minimum length of buffer array
     */
    @Override
    protected byte[] allocateArray(int capacity) {
        if (bucket == null) {
            return new byte[capacity];
        } else {
            return bucket.apply(
                EMPTY_BYTES, 0, capacity
            );
        }
    }
}
