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
import plus.kat.stream.*;

import java.io.IOException;
import java.nio.ByteBuffer;

import static plus.kat.stream.Binary.Unsafe.RFC4648_ENCODE;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ByteBufferSpare extends Property<ByteBuffer> {

    public static final ByteBufferSpare
        INSTANCE = new ByteBufferSpare();

    private final byte[]
        table = RFC4648_ENCODE;

    public ByteBufferSpare() {
        super(ByteBuffer.class);
    }

    @Override
    public String getSpace() {
        return "B";
    }

    @Override
    public ByteBuffer read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) {
        return ByteBuffer.wrap(
            Base64.mime().decode(chain)
        );
    }

    @Override
    public ByteBuffer cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }
        return ByteBuffer.wrap(
            ByteArraySpare.INSTANCE.cast(
                object, supplier
            )
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        ByteBuffer buf =
            (ByteBuffer) value;
        int size = buf.remaining();

        if (size > 0) {
            int t1 = size % 3;
            int t2 = size / 3;

            int b1, b2, b3;
            int c = t2 * 3;

            byte[] tab = table;
            int i = buf.position();

            while (i < c) {
                b1 = buf.get(i++) & 0xFF;
                b2 = buf.get(i++) & 0xFF;
                b3 = buf.get(i++) & 0xFF;

                flow.emit(tab[b1 >>> 2]);
                flow.emit(tab[((b1 & 0x3) << 4) | (b2 >>> 4)]);
                flow.emit(tab[((b2 & 0xF) << 2) | (b3 >>> 6)]);
                flow.emit(tab[b3 & 0x3F]);
            }

            if (t1 != 0) {
                b1 = buf.get(i++) & 0xFF;
                if (t1 == 1) {
                    flow.emit(tab[b1 >>> 2]);
                    flow.emit(tab[(b1 & 0x3) << 4]);
                    flow.emit((byte) '=');
                } else {
                    b2 = buf.get(i) & 0xFF;
                    flow.emit(tab[b1 >>> 2]);
                    flow.emit(tab[((b1 & 0x3) << 4) | (b2 >>> 4)]);
                    flow.emit(tab[(b2 & 0xF) << 2]);
                }
                flow.emit((byte) '=');
            }
        }
    }
}
