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
public class ByteBufPaper extends AbstractPaper {

    protected ByteBuf source;

    public ByteBufPaper(
        @NotNull ByteBuf data
    ) {
        if (data != null) {
            source = data;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    protected int load() {
        ByteBuf in = source;
        int n = in.readerIndex(),
            m = in.writerIndex();

        int size = m - n;
        if (size <= 0) {
            return -1;
        }

        byte[] buf = queue;
        if (buf == null) {
            int l = buflen;
            if (l != 0) {
                buf = INS.alloc(l);
            } else if (size > 1024) {
                buf = INS.alloc(1024);
            } else if (size > 512) {
                buf = new byte[256];
            } else {
                buf = new byte[Math.min(size, 256)];
            }
            queue = buf;
        }

        if (size > buf.length) {
            size = buf.length;
        }

        in.readBytes(
            buf, 0, size
        );
        return size;
    }

    @Override
    public void close() {
        INS.join(queue);
        limit = -1;
        queue = null;
        source = null;
    }
}