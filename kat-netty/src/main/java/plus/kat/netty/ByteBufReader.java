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
package plus.kat.netty;

import plus.kat.anno.NotNull;

import plus.kat.stream.Reader;
import plus.kat.stream.ByteReader;

import io.netty.buffer.ByteBuf;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ByteBufReader implements Reader {

    private ByteBuf buf;
    private int index, length;

    /**
     * @since 0.0.2
     */
    public ByteBufReader(
        @NotNull ByteBuf buf
    ) {
        this.buf = buf;
        this.index = buf.readerIndex();
        this.length = buf.writerIndex();
    }

    @Override
    public byte read() {
        return buf.getByte(index++);
    }

    @Override
    public boolean also() {
        return index < length;
    }

    @Override
    public void close() {
        buf = null;
        length = 0;
    }

    /**
     * @since 0.0.2
     */
    @NotNull
    public static Reader of(
        @NotNull ByteBuf buf
    ) {
        if (buf.hasArray()) {
            return new ByteReader(
                buf.array()
            );
        }

        return new ByteBufReader(buf);
    }
}
