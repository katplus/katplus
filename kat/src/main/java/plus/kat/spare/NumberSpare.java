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

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.3
 */
public class NumberSpare extends Property<Number> {

    public static final NumberSpare
        INSTANCE = new NumberSpare();

    public NumberSpare() {
        super(Number.class);
    }

    @Override
    public String getSpace() {
        return "n";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == Number.class
            || clazz == Object.class;
    }

    @Override
    public Boolean getBorder(
        @NotNull Flag flag
    ) {
        return Boolean.FALSE;
    }

    @Override
    public Number read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toNumber();
    }

    @Override
    public Number read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toNumber();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        if (value instanceof Integer) {
            flow.emit(
                (int) value
            );
        } else if (value instanceof Long) {
            flow.emit(
                (long) value
            );
        } else if (value instanceof Float) {
            flow.emit(
                (float) value
            );
        } else if (value instanceof Double) {
            flow.emit(
                (double) value
            );
        } else if (value instanceof Byte) {
            flow.emit(
                ((Byte) value).intValue()
            );
        } else if (value instanceof Short) {
            flow.emit(
                ((Short) value).intValue()
            );
        } else if (value instanceof Number) {
            flow.emit(
                value.toString()
            );
        }
    }

    @Override
    public Number cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
            if (data instanceof Number) {
                return (Number) data;
            }

            if (data instanceof Boolean) {
                return ((boolean) data) ? 1 : 0;
            }
        }
        return null;
    }
}
