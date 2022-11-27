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
public class ShortSpare extends Property<Short> {

    public static final ShortSpare
        INSTANCE = new ShortSpare();

    public ShortSpare() {
        super(Short.class);
    }

    @Override
    public Short apply() {
        return (short) 0;
    }

    @Override
    public Short apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == short.class ||
            type == Short.class) {
            return (short) 0;
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return "u";
    }

    @Override
    public Boolean getBorder(
        @NotNull Flag flag
    ) {
        return Boolean.FALSE;
    }

    @Override
    public Short read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) {
        int i = chain.toInt();
        return i < Short.MIN_VALUE
            || i > Short.MAX_VALUE ? (short) 0 : (short) i;
    }

    @Override
    public Short read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        int i = value.toInt();
        return i < Short.MIN_VALUE
            || i > Short.MAX_VALUE ? (short) 0 : (short) i;
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.emit(
            ((Short) value).intValue()
        );
    }

    @Override
    public Short cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return (short) 0;
        }

        if (object instanceof Short) {
            return (Short) object;
        }

        if (object instanceof Number) {
            return ((Number) object).shortValue();
        }

        if (object instanceof Boolean) {
            return ((boolean) object) ? (short) 1 : (short) 0;
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
                object + " cannot be converted to Short"
            );
        }

        return i < Short.MIN_VALUE
            || i > Short.MAX_VALUE ? (short) 0 : (short) i;
    }
}
