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

import static plus.kat.flow.Stream.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class ByteBufFlow extends Flow {

    private ByteBuf flow;

    /**
     * Constructs this flow for the specified text
     *
     * @throws NullPointerException If the specified text is null
     */
    public ByteBufFlow(
        @NotNull ByteBuf text
    ) {
        if (text != null) {
            flow = text;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public int load() {
        int n = flow.readerIndex(),
            m = flow.writerIndex();

        i = 0;
        int size = m - n;
        if (size <= 0) {
            return l = -1;
        }

        if (v == null) {
            if (size > 1023) {
                v = BUCKET.apply(
                    null, 0, 2048
                );
            } else if (size > 511) {
                v = new byte[256];
            } else {
                v = new byte[Math.min(size, 256)];
            }
        }

        if (size > v.length) {
            size = v.length;
        }

        flow.readBytes(
            v, 0, size
        );
        return l = size;
    }

    @Override
    public void close() {
        BUCKET.store(v);
        flow = null;
        super.close();
    }
}
