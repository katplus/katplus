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

import static plus.kat.kernel.Dram.Memory;
import static plus.kat.kernel.Dram.Memory.INS;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ByteBufReader extends AbstractReader {

    private ByteBuf value;

    /**
     * @since 0.0.2
     */
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

        byte[] tmp = cache;
        if (tmp == null) {
            int r = range;
            if (r == 0) {
                if (cap > 512) {
                    tmp = INS.alloc();
                } else {
                    tmp = new byte[Math.min(cap, 256)];
                }
            } else {
                int s = Memory.SCALE;
                if (r > s) {
                    tmp = new byte[r];
                } else {
                    tmp = INS.alloc();
                }
            }
            cache = tmp;
        }

        if (cap > tmp.length) {
            cap = tmp.length;
        }

        value.getBytes(
            m, tmp, 0, cap
        );
        return cap;
    }

    @Override
    public void close() {
        INS.share(cache);
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
