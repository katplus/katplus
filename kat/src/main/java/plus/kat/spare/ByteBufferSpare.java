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

import plus.kat.*;
import plus.kat.actor.*;
import plus.kat.chain.*;
import plus.kat.stream.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.2
 */
@SuppressWarnings("unchecked")
public class ByteBufferSpare extends BaseSpare<ByteBuffer> {

    public static final ByteBufferSpare
        INSTANCE = new ByteBufferSpare();

    private int mode;
    private final byte[]
        table = RFC4648_ENCODE;

    private final Base64
        base64 = Base64.mime();

    public ByteBufferSpare() {
        super(ByteBuffer.class);
    }

    public ByteBufferSpare(
        @NotNull Class<?> clazz
    ) {
        super(
            (Class<ByteBuffer>) clazz
        );
        mode = MappedByteBuffer.class
            .isAssignableFrom(clazz) ? 1 : 0;
    }

    @Override
    public String getSpace() {
        return "ByteArray";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public ByteBuffer read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        switch (mode) {
            case 0: {
                return ByteBuffer.wrap(
                    base64.decode(value)
                );
            }
            case 1: {
                byte[] flow =
                    base64.decode(value);
                ByteBuffer buffer = ByteBuffer
                    .allocateDirect(flow.length);
                return buffer.put(flow);
            }
        }

        throw new IOException(
            "Unsupported subtype: " + klass
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        ByteBuffer buf =
            (ByteBuffer) value;
        int e = buf.remaining();
        if (e > 0) {
            int t1 = e % 3;
            int t2 = e / 3;

            int b1, b2, b3;
            byte[] tab = table;

            for (int i = 0; i < t2; i++) {
                b1 = buf.get() & 0xFF;
                b2 = buf.get() & 0xFF;
                b3 = buf.get() & 0xFF;
                flux.emit(tab[b1 >>> 2]);
                flux.emit(tab[((b1 & 0x3) << 4) | (b2 >>> 4)]);
                flux.emit(tab[((b2 & 0xF) << 2) | (b3 >>> 6)]);
                flux.emit(tab[b3 & 0x3F]);
            }

            if (t1 != 0) {
                b1 = buf.get() & 0xFF;
                if (t1 == 1) {
                    flux.emit(tab[b1 >>> 2]);
                    flux.emit(tab[(b1 & 0x3) << 4]);
                    flux.emit((byte) '=');
                } else {
                    b2 = buf.get() & 0xFF;
                    flux.emit(tab[b1 >>> 2]);
                    flux.emit(tab[((b1 & 0x3) << 4) | (b2 >>> 4)]);
                    flux.emit(tab[(b2 & 0xF) << 2]);
                }
                flux.emit((byte) '=');
            }
        }
    }
}
