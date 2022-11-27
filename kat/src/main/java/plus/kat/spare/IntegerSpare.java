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
public class IntegerSpare extends Property<Integer> {

    public static final IntegerSpare
        INSTANCE = new IntegerSpare();

    public IntegerSpare() {
        super(Integer.class);
    }

    @Override
    public Integer apply() {
        return 0;
    }

    @Override
    public Integer apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == int.class ||
            type == Integer.class) {
            return 0;
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return "i";
    }

    @Override
    public Boolean getBorder(
        @NotNull Flag flag
    ) {
        return Boolean.FALSE;
    }

    @Override
    public Integer read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) {
        return chain.toInt();
    }

    @Override
    public Integer read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toInt();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.emit((int) value);
    }


    @Override
    public Integer cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return 0;
        }

        if (object instanceof Integer) {
            return (Integer) object;
        }

        if (object instanceof Number) {
            return ((Number) object).intValue();
        }

        if (object instanceof Boolean) {
            return ((boolean) object) ? 1 : 0;
        }

        if (object instanceof Chain) {
            return ((Chain) object).toInt();
        }

        if (object instanceof CharSequence) {
            CharSequence num = (CharSequence) object;
            return Convert.toInt(
                num, num.length(), 10, 0
            );
        }

        throw new IllegalStateException(
            object + " cannot be converted to Integer"
        );
    }
}
