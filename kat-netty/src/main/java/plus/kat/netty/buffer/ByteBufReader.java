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

import static plus.kat.stream.Stream.Buffer.INS;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ByteBufReader extends AbstractReader {

    private ByteBuf value;

    public ByteBufReader(
        @NotNull ByteBuf data
    ) {
        if (data == null) {
            throw new NullPointerException();
        }
        value = data;
    }

    @Override
    protected int load() {
        int m = value.readerIndex();
        int n = value.writerIndex();

        int cap = n - m;
        if (cap <= 0) {
            return -1;
        }

        byte[] buf = cache;
        if (buf == null) {
            if (cap > 1024) {
                cache = buf = INS.alloc(range);
            } else if (cap > 512) {
                cache = buf = new byte[256];
            } else {
                cache = buf = new byte[Math.min(cap, 256)];
            }
        }

        if (cap > buf.length) {
            cap = buf.length;
        }

        value.readBytes(
            buf, 0, cap
        );
        return cap;
    }

    @Override
    public void close() {
        INS.join(cache);
        limit = -1;
        value = null;
        cache = null;
    }
}
