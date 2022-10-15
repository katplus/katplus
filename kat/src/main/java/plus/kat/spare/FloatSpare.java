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
public class FloatSpare extends Property<Float> {

    public static final FloatSpare
        INSTANCE = new FloatSpare();

    public FloatSpare() {
        super(Float.class);
    }

    @Override
    public Float apply() {
        return 0F;
    }

    @Override
    public Float apply(
        @NotNull Type type
    ) {
        if (type == float.class ||
            type == Float.class) {
            return 0F;
        }

        throw new Collapse(
            "Unable to create an instance of " + type
        );
    }

    @Override
    public String getSpace() {
        return "f";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == float.class
            || clazz == Float.class
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
    public Float read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toFloat();
    }

    @Override
    public Float read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toFloat();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        if (flow.isFlag(Flag.FLOAT_AS_BITMAP)) {
            flow.emit(
                (float) value, true
            );
        } else {
            flow.emit(
                (float) value
            );
        }
    }

    @Override
    public Float cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
            if (data instanceof Float) {
                return (Float) data;
            }

            if (data instanceof Number) {
                return ((Number) data).floatValue();
            }

            if (data instanceof Boolean) {
                return ((boolean) data) ? 1F : 0F;
            }

            if (data instanceof Chain) {
                return ((Chain) data).toFloat();
            }

            if (data instanceof CharSequence) {
                try {
                    return Float.parseFloat(
                        data.toString()
                    );
                } catch (Exception e) {
                    // Nothing
                }
            }
        }
        return 0F;
    }
}
