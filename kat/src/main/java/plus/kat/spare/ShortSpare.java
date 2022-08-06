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
public class ShortSpare extends DataSpare<Short> implements Serializable {

    public static final ShortSpare
        INSTANCE = new ShortSpare();

    public ShortSpare() {
        super(Short.class);
    }

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$u;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == short.class
            || klass == Short.class
            || klass == Number.class
            || klass == Object.class;
    }

    @NotNull
    @Override
    public Short cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof Short) {
            return (Short) data;
        }

        if (data instanceof Number) {
            return ((Number) data).shortValue();
        }

        if (data instanceof Boolean) {
            return ((boolean) data) ? (short) 1 : (short) 0;
        }

        if (data instanceof CharSequence) {
            CharSequence num = (CharSequence) data;
            int i = Convert.toInt(
                num, num.length(), 10, 0
            );

            if (i < Short.MIN_VALUE ||
                i > Short.MAX_VALUE) {
                return (short) 0;
            }

            return (short) i;
        }

        return (short) 0;
    }

    @NotNull
    @Override
    public Short read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toShort();
    }

    @NotNull
    @Override
    public Short read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toShort();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.addShort(
            (short) value
        );
    }
}
