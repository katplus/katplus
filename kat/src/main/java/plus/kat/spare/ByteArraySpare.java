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
import java.nio.charset.StandardCharsets;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ByteArraySpare extends Property<byte[]> {

    public static final ByteArraySpare
        INSTANCE = new ByteArraySpare();

    public ByteArraySpare() {
        super(byte[].class);
    }

    @Override
    public byte[] apply() {
        return Chain.EMPTY_BYTES;
    }

    @Override
    public String getSpace() {
        return "B";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == byte[].class
            || clazz == Object.class;
    }

    @Override
    public byte[] read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.decode(
            Base64.mime()
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        byte[] b = Base64.base()
            .encode(
                (byte[]) value
            );
        flow.emit(
            b, 'B', 0, b.length
        );
    }

    @Override
    public byte[] cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
            if (data instanceof byte[]) {
                return (byte[]) data;
            }

            if (data instanceof Chain) {
                return ((Chain) data).toBytes();
            }

            if (data instanceof String) {
                return Base64.mime().decode(
                    ((String) data).getBytes(
                        StandardCharsets.US_ASCII
                    )
                );
            }
        }
        return Chain.EMPTY_BYTES;
    }
}
