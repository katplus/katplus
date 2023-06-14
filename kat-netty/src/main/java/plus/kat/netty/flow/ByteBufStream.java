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
import static plus.kat.stream.Toolkit.Streams.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class ByteBufStream extends UnpooledHeapByteBuf {

    static final ByteBufAllocator
        ALLOC = ByteBufAllocator.DEFAULT;

    final boolean recycle;

    public ByteBufStream(
        byte[] flow
    ) {
        this(flow, false);
    }

    public ByteBufStream(
        byte[] flow,
        boolean recycle
    ) {
        super(
            ALLOC,
            flow, flow.length
        );
        this.recycle = recycle;
    }

    /**
     * Returns a {@link ByteBuf} of the chan
     *
     * <pre>{@code
     *  try(Chan chan = ...) {
     *      // Use buffer before calling Chan#close
     *      ByteBuf buffer = ByteBufStream.of(chan);
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
     * @param space the specified space for reading
     */
    @NotNull
    public static ByteBuf of(
        @NotNull Space space
    ) {
        byte[] buffer = space.flow();
        if (buffer != null) {
            return new ByteBufStream(
                buffer, false
            ).writerIndex(
                space.size()
            );
        } else {
            return new ByteBufStream(
                valueOf(space), false
            );
        }
    }

    /**
     * Returns a {@link ByteBuf} of the stream
     *
     * <pre>{@code
     *  try(Stream stream = ...) {
     *      // Use buffer before calling Stream#close
     *      ByteBuf buffer = ByteBufStream.of(stream);
     *  }
     * }</pre>
     *
     * @param stream the specified stream for reading
     */
    @NotNull
    public static ByteBuf of(
        @NotNull Stream stream
    ) {
        byte[] buffer = sourceOf(stream);
        if (buffer != null) {
            return new ByteBufStream(
                buffer, true
            ).writerIndex(
                stream.size()
            );
        } else {
            return new ByteBufStream(
                stream.toBinary(), false
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
        if (recycle) {
            STREAMS.store(array);
        }
    }

    /**
     * Borrows a array of the capacity from the default bucket
     *
     * @param capacity the specified minimum length of buffer array
     */
    @Override
    protected byte[] allocateArray(int capacity) {
        return STREAMS.apply(capacity);
    }
}
