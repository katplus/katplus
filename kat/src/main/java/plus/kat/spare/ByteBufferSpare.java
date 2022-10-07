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
package plus.kat.spare;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static plus.kat.kernel.Dram.Memory.INS;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ByteBufferSpare extends Property<ByteBuffer> {

    public static final ByteBufferSpare
        INSTANCE = new ByteBufferSpare();

    public ByteBufferSpare() {
        super(ByteBuffer.class);
    }

    @Override
    public Space getSpace() {
        return Space.$s;
    }

    @Override
    public ByteBuffer read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return ByteBuffer.wrap(
            value.toBytes()
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        int rem;
        byte[] cache = null;
        ByteBuffer buf = (ByteBuffer) value;

        while ((rem = buf.remaining()) > 0) {
            if (cache == null) {
                if (rem > 1024) {
                    cache = INS.alloc();
                } else {
                    cache = new byte[Math.min(rem, 256)];
                }
            }
            int len = Math.min(
                rem, cache.length
            );
            buf.get(cache, 0, len);
            flow.emit(cache, 0, len);
        }
        INS.share(cache);
    }

    @Override
    public ByteBuffer cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
            if (data instanceof ByteBuffer) {
                return (ByteBuffer) data;
            }

            if (data instanceof byte[]) {
                return ByteBuffer.wrap(
                    (byte[]) data
                );
            }

            if (data instanceof Chain) {
                return ByteBuffer.wrap(
                    ((Chain) data).toBytes()
                );
            }

            if (data instanceof String) {
                return ByteBuffer.wrap(
                    Base64.mime().decode(
                        ((String) data).getBytes(
                            StandardCharsets.US_ASCII
                        )
                    )
                );
            }
        }
        return null;
    }
}
