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

import plus.kat.anno.NotNull;

import plus.kat.*;
import plus.kat.chain.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static plus.kat.chain.Chain.Unsafe.value;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ChanBuf {
    /**
     * @see ChanBuf#wrappedBuffer(Chain)
     * @since 0.0.2
     */
    @NotNull
    public static ByteBuf wrappedBuffer(
        @NotNull Chan chan
    ) {
        Flow flow = chan.getFlow();
        if (flow instanceof Chain) {
            return wrappedBuffer(
                (Chain) flow
            );
        } else {
            byte[] stream = chan.toBytes();
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
        @NotNull Chain chain
    ) {
        int length = chain.length();
        if (length == 0) {
            return Unpooled.EMPTY_BUFFER;
        }

        byte[] value = value(chain);
        if (value == null) {
            return Unpooled.wrappedBuffer(
                chain.toBytes()
            );
        }

        if (length == value.length) {
            return Unpooled.wrappedBuffer(value);
        }

        return Unpooled.wrappedBuffer(value).slice(0, length);
    }
}
