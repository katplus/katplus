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

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ByteSpare extends BaseSpare<Byte> {

    public static final Byte
        ZERO_BYTE = 0;

    public static final ByteSpare
        INSTANCE = new ByteSpare();

    public ByteSpare() {
        super(Byte.class);
    }

    @Override
    public Byte apply() {
        return ZERO_BYTE;
    }

    @Override
    public Byte apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == byte.class ||
            type == Byte.class) {
            return ZERO_BYTE;
        }

        throw new IllegalStateException(
            "Failed to build this " + type
        );
    }

    @Override
    public String getSpace() {
        return "Byte";
    }

    @Override
    public Byte read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        if (value.isNothing()) {
            return null;
        }

        int i = value.toInt();
        if (Byte.MAX_VALUE >= i &&
            Byte.MIN_VALUE <= i) {
            return (byte) i;
        }

        throw new IllegalArgumentException(
            "Failed to convert the value to Byte, " +
                "where this value is literally `" + value + '`'
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            ((Byte) value).intValue()
        );
    }
}
