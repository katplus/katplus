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

import java.io.IOException;
import java.nio.ByteBuffer;

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
    public ByteBuffer cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof ByteBuffer) {
            return (ByteBuffer) data;
        }

        return null;
    }

    @Override
    public ByteBuffer read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return ByteBuffer.wrap(
            value.copyBytes()
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        ByteBuffer buf = (ByteBuffer) value;
        if (buf.hasArray()) {
            flow.emit(
                buf.array()
            );
        } else {
            int i = buf.position();
            int o = i + buf.limit();
            while (i < o) {
                flow.emit(
                    buf.get(i++)
                );
            }
        }
    }
}
