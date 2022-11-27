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
        @Nullable Type type
    ) {
        if (type == null ||
            type == float.class ||
            type == Float.class) {
            return 0F;
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return "f";
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
        @NotNull Chain chain
    ) {
        return chain.toFloat();
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
        flow.emit((float) value);
    }

    @Override
    public Float cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return 0F;
        }

        if (object instanceof Float) {
            return (Float) object;
        }

        if (object instanceof Number) {
            return ((Number) object).floatValue();
        }

        if (object instanceof Boolean) {
            return ((boolean) object) ? 1F : 0F;
        }

        if (object instanceof Chain) {
            return ((Chain) object).toFloat();
        }

        if (object instanceof CharSequence) {
            String s = object.toString();
            if (s.isEmpty() ||
                "null".equalsIgnoreCase(s)) {
                return 0F;
            }
            try {
                return Float.parseFloat(s);
            } catch (Exception e) {
                return 0F;
            }
        }

        throw new IllegalStateException(
            object + " cannot be converted to Float"
        );
    }
}
