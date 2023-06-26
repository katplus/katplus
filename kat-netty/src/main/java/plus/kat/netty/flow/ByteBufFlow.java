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

import io.netty.buffer.ByteBuf;

import static plus.kat.stream.Toolkit.Streams.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class ByteBufFlow extends Flow {

    private ByteBuf source;

    /**
     * Constructs this flow for the specified text
     *
     * @throws NullPointerException If the specified text is null
     */
    public ByteBufFlow(
        @NotNull ByteBuf text
    ) {
        if (text != null) {
            source = text;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public int load() {
        ByteBuf in = source;
        int n = in.readerIndex(),
            m = in.writerIndex();

        index = 0;
        int size = m - n;
        if (size <= 0) {
            return limit = -1;
        }

        byte[] it = value;
        if (it == null) {
            if (size > 1023) {
                it = STREAMS.apply(2048);
            } else if (size > 511) {
                it = new byte[256];
            } else {
                it = new byte[Math.min(size, 256)];
            }
            value = it;
        }

        if (size > it.length) {
            size = it.length;
        }

        in.readBytes(
            it, 0, size
        );
        return limit = size;
    }

    @Override
    public void close() {
        STREAMS.store(value);
        limit = -1;
        value = null;
        source = null;
    }
}
