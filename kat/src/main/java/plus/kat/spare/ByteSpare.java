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
import java.io.Serializable;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ByteSpare extends Property<Byte> implements Serializable {

    public static final ByteSpare
        INSTANCE = new ByteSpare();

    public ByteSpare() {
        super(Byte.class);
    }

    @Override
    public Byte apply() {
        return (byte) 0;
    }

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$o;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == byte.class
            || klass == Byte.class
            || klass == Number.class
            || klass == Object.class;
    }

    @NotNull
    @Override
    public Byte cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof Byte) {
            return (Byte) data;
        }

        if (data instanceof Number) {
            return ((Number) data).byteValue();
        }

        if (data instanceof Boolean) {
            return ((boolean) data) ? (byte) 1 : (byte) 0;
        }

        if (data instanceof CharSequence) {
            CharSequence num = (CharSequence) data;
            int i = Convert.toInt(
                num, num.length(), 10, 0
            );

            if (i < Byte.MIN_VALUE ||
                i > Byte.MAX_VALUE) {
                return (byte) 0;
            }

            return (byte) i;
        }

        return (byte) 0;
    }

    @NotNull
    @Override
    public Byte read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toByte();
    }

    @NotNull
    @Override
    public Byte read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toByte();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.addInt(
            (byte) value
        );
    }
}
