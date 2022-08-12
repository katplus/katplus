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

import plus.kat.stream.*;

import io.netty.buffer.ByteBuf;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ByteBufReader extends AbstractReader {

    private ByteBuf value;
    private int begin;
    private final int end;

    /**
     * @since 0.0.2
     */
    public ByteBufReader(
        @NotNull ByteBuf data
    ) {
        value = data;
        begin = value.readerIndex();
        end = value.writerIndex();
    }

    @Override
    protected int load() {
        int cap = end - begin;
        if (cap <= 0) {
            return -1;
        }

        byte[] tmp = cache;
        if (tmp == null) {
            int r = range;
            if (r == 0) {
                r = 128;
            }
            if (cap < r) {
                r = cap;
            }
            cache = tmp = new byte[r];
        }

        if (cap > tmp.length) {
            cap = tmp.length;
        }

        value.getBytes(
            begin, tmp, 0, cap
        );
        begin += cap;
        return cap;
    }

    @Override
    public void close() {
        value = null;
        cache = null;
        offset = -1;
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
