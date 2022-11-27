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
import plus.kat.crash.*;
import plus.kat.stream.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import static plus.kat.chain.Chain.EMPTY_BYTES;
import static plus.kat.stream.Binary.Unsafe.RFC4648_ENCODE;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ByteArraySpare extends Property<byte[]> {

    public static final ByteArraySpare
        INSTANCE = new ByteArraySpare();

    private final byte[]
        table = RFC4648_ENCODE;

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

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return "B";
    }

    @Override
    public byte[] read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) {
        return Base64.mime().decode(chain);
    }

    @Override
    public void write(
        @NotNull Flow flow,
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

                flow.emit(tab[b1 >>> 2]);
                flow.emit(tab[((b1 & 0x3) << 4) | (b2 >>> 4)]);
                flow.emit(tab[((b2 & 0xF) << 2) | (b3 >>> 6)]);
                flow.emit(tab[b3 & 0x3F]);
            }

            if (t1 != 0) {
                b1 = data[i++] & 0xFF;
                if (t1 == 1) {
                    flow.emit(tab[b1 >>> 2]);
                    flow.emit(tab[(b1 & 0x3) << 4]);
                    flow.emit((byte) '=');
                } else {
                    b2 = data[i] & 0xFF;
                    flow.emit(tab[b1 >>> 2]);
                    flow.emit(tab[((b1 & 0x3) << 4) | (b2 >>> 4)]);
                    flow.emit(tab[(b2 & 0xF) << 2]);
                }
                flow.emit((byte) '=');
            }
        }
    }

    @Override
    public byte[] cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (object instanceof byte[]) {
            return (byte[]) object;
        }

        if (object instanceof Chain) {
            return ((Chain) object).toBytes();
        }

        if (object instanceof String) {
            return Base64.mime().decode(
                ((String) object).getBytes(
                    StandardCharsets.US_ASCII
                )
            );
        }

        throw new IllegalStateException(
            object + " cannot be converted to ByteArray"
        );
    }
}
