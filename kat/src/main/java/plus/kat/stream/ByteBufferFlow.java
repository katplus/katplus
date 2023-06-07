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
package plus.kat.stream;

import plus.kat.*;
import plus.kat.actor.*;

import java.nio.ByteBuffer;

import static plus.kat.stream.Toolkit.Streams.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class ByteBufferFlow extends Flow {

    private ByteBuffer source;

    /**
     * Constructs this flow for the specified text
     *
     * @throws NullPointerException If the specified text is null
     */
    public ByteBufferFlow(
        @NotNull ByteBuffer text
    ) {
        if (text != null) {
            source = text;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public int load() {
        ByteBuffer in = source;
        int m = in.limit(),
            n = in.position();

        index = 0;
        int size = m - n;
        if (size <= 0) {
            return limit = -1;
        }

        byte[] buf = value;
        if (buf == null) {
            if (size > 1023) {
                buf = STREAMS.apply(2048);
            } else if (size > 511) {
                buf = new byte[256];
            } else {
                buf = new byte[Math.min(256, size)];
            }
            value = buf;
        }

        if (size > buf.length) {
            size = buf.length;
        }
        in.get(
            buf, 0, size
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
