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

import plus.kat.crash.*;
import plus.kat.stream.*;

import io.netty.buffer.ByteBuf;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ByteBufReader implements Reader {

    private int index;
    private int offset;

    private int begin;
    private final int end;

    private byte[] cache;
    private ByteBuf value;

    /**
     * @since 0.0.2
     */
    public ByteBufReader(
        @NotNull ByteBuf data
    ) {
        value = data;
        cache = new byte[64];
        begin = value.readerIndex();
        end = value.writerIndex();
        index = cache.length;
        offset = cache.length;
    }

    @Override
    public boolean also() {
        if (index < offset) {
            return true;
        }

        if (offset > 0) {
            offset = read(cache);
            if (offset > 0) {
                index = 0;
                return true;
            }
        }

        return false;
    }

    @Override
    public byte read() {
        return cache[index++];
    }

    @Override
    public byte next() throws IOCrash {
        if (index < offset) {
            return cache[index++];
        }

        if (offset > 0) {
            offset = read(cache);
            if (offset > 0) {
                index = 0;
                return cache[index++];
            }
        }

        throw new UnexpectedCrash(
            "Unexpectedly, no readable byte"
        );
    }

    private int read(
        @NotNull byte[] buf
    ) {
        int length = end - begin;
        if (length > 0) {
            if (length > buf.length) {
                length = buf.length;
            }
            value.getBytes(
                begin, buf, 0, length
            );
            begin += length;
        }
        return length;
    }

    @Override
    public void close() {
        offset = 0;
        value = null;
        cache = null;
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
