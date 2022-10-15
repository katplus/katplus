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
import plus.kat.kernel.*;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class DoubleSpare extends Property<Double> {

    public static final DoubleSpare
        INSTANCE = new DoubleSpare();

    public DoubleSpare() {
        super(Double.class);
    }

    @Override
    public Double apply() {
        return 0D;
    }

    @Override
    public Double apply(
        @NotNull Type type
    ) {
        if (type == double.class ||
            type == Double.class) {
            return 0D;
        }

        throw new Collapse(
            "Unable to create an instance of " + type
        );
    }

    @Override
    public String getSpace() {
        return "d";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == double.class
            || clazz == Double.class
            || clazz == Number.class
            || clazz == Object.class;
    }

    @Override
    public Boolean getBorder(
        @NotNull Flag flag
    ) {
        return Boolean.FALSE;
    }

    @Override
    public Double read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toDouble();
    }

    @Override
    public Double read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toDouble();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        if (flow.isFlag(Flag.FLOAT_AS_BITMAP)) {
            flow.emit(
                (double) value, true
            );
        } else {
            flow.emit(
                (double) value
            );
        }
    }

    @Override
    public Double cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
            if (data instanceof Double) {
                return (Double) data;
            }

            if (data instanceof Number) {
                return ((Number) data).doubleValue();
            }

            if (data instanceof Boolean) {
                return ((boolean) data) ? 1D : 0D;
            }

            if (data instanceof Chain) {
                return ((Chain) data).toDouble();
            }

            if (data instanceof CharSequence) {
                try {
                    return Double.parseDouble(
                        data.toString()
                    );
                } catch (Exception e) {
                    // Nothing
                }
            }
        }
        return 0D;
    }
}
