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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.stream.*;

import java.io.IOException;
import java.lang.reflect.Type;

import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ByteArraySpare extends BaseSpare<byte[]> {

    public static final ByteArraySpare
        INSTANCE = new ByteArraySpare();

    private final byte[]
        table = RFC4648_ENCODE;

    private final Base64
        base64 = Base64.mime();

    public ByteArraySpare() {
        super(byte[].class);
    }

    @Override
    public byte[] apply() {
        return EMPTY_BYTES;
    }

    @Override
    public byte[] apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == byte[].class) {
            return EMPTY_BYTES;
        }

        throw new IllegalStateException(
            "Failed to build this " + type
        );
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
    public byte[] read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return base64.decode(value);
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        byte[] data =
            (byte[]) value;
        int size = data.length;

        if (size != 0) {
            int t1 = size % 3;
            int t2 = size / 3;

            int b1, b2, b3;
            int i = 0, c = t2 * 3;

            byte[] tab = table;
            while (i < c) {
                b1 = data[i++] & 0xFF;
                b2 = data[i++] & 0xFF;
                b3 = data[i++] & 0xFF;

                flux.emit(tab[b1 >>> 2]);
                flux.emit(tab[((b1 & 0x3) << 4) | (b2 >>> 4)]);
                flux.emit(tab[((b2 & 0xF) << 2) | (b3 >>> 6)]);
                flux.emit(tab[b3 & 0x3F]);
            }

            if (t1 != 0) {
                b1 = data[i++] & 0xFF;
                if (t1 == 1) {
                    flux.emit(tab[b1 >>> 2]);
                    flux.emit(tab[(b1 & 0x3) << 4]);
                    flux.emit((byte) '=');
                } else {
                    b2 = data[i] & 0xFF;
                    flux.emit(tab[b1 >>> 2]);
                    flux.emit(tab[((b1 & 0x3) << 4) | (b2 >>> 4)]);
                    flux.emit(tab[(b2 & 0xF) << 2]);
                }
                flux.emit((byte) '=');
            }
        }
    }
}
