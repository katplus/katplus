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

/**
 * @author kraity
 * @since 0.0.1
 */
public class ByteSpare extends Property<Byte> {

    public static final ByteSpare
        INSTANCE = new ByteSpare();

    public ByteSpare() {
        super(Byte.class);
    }

    @Override
    public Byte apply() {
        return (byte) 0;
    }

    @Override
    public Byte apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == byte.class ||
            type == Byte.class) {
            return (byte) 0;
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return "o";
    }

    @Override
    public Boolean getBorder(
        @NotNull Flag flag
    ) {
        return Boolean.FALSE;
    }

    @Override
    public Byte read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) {
        int i = chain.toInt();
        return i < Byte.MIN_VALUE
            || i > Byte.MAX_VALUE ? (byte) 0 : (byte) i;
    }

    @Override
    public Byte read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        int i = value.toInt();
        return i < Byte.MIN_VALUE
            || i > Byte.MAX_VALUE ? (byte) 0 : (byte) i;
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.emit(
            ((Byte) value).intValue()
        );
    }

    @Override
    public Byte cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return (byte) 0;
        }

        if (object instanceof Byte) {
            return (Byte) object;
        }

        if (object instanceof Number) {
            return ((Number) object).byteValue();
        }

        if (object instanceof Boolean) {
            return ((boolean) object) ? (byte) 1 : (byte) 0;
        }

        int i;
        if (object instanceof Chain) {
            i = ((Chain) object).toInt();
        } else if (object instanceof CharSequence) {
            CharSequence num = (CharSequence) object;
            i = Convert.toInt(
                num, num.length(), 10, 0
            );
        } else {
            throw new IllegalStateException(
                object + " cannot be converted to Byte"
            );
        }

        return i < Byte.MIN_VALUE
            || i > Byte.MAX_VALUE ? (byte) 0 : (byte) i;
    }
}
