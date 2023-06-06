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
package plus.kat.netty.buffer;

import plus.kat.*;
import plus.kat.actor.*;
import plus.kat.stream.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ChanBuf {
    /**
     * @see ChanBuf#wrappedBuffer(Binary)
     * @since 0.0.2
     */
    @NotNull
    public static ByteBuf wrappedBuffer(
        @NotNull Chan chan
    ) {
        Flux flux = chan.getFlux();
        if (flux instanceof Binary) {
            return wrappedBuffer(
                (Binary) flux
            );
        } else {
            byte[] stream = chan.toBinary();
            if (stream.length == 0) {
                return Unpooled.EMPTY_BUFFER;
            }
            return Unpooled.wrappedBuffer(stream);
        }
    }

    /**
     * @see Unpooled#wrappedBuffer(byte[])
     * @since 0.0.2
     */
    @NotNull
    public static ByteBuf wrappedBuffer(
        @NotNull Binary binary
    ) {
        int size = binary.size();
        if (size == 0) {
            return Unpooled.EMPTY_BUFFER;
        }

        byte[] value = valueOf(binary);
        if (size == value.length) {
            return Unpooled.wrappedBuffer(value);
        }

        return Unpooled.wrappedBuffer(value).slice(0, size);
    }
}
