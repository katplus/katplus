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
        @Nullable Type type
    ) {
        if (type == null ||
            type == double.class ||
            type == Double.class) {
            return 0D;
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return "d";
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
        @NotNull Chain chain
    ) {
        return chain.toDouble();
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
        flow.emit((double) value);
    }

    @Override
    public Double cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return 0D;
        }

        if (object instanceof Double) {
            return (Double) object;
        }

        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }

        if (object instanceof Boolean) {
            return ((boolean) object) ? 1D : 0D;
        }

        if (object instanceof Chain) {
            return ((Chain) object).toDouble();
        }

        if (object instanceof CharSequence) {
            String s = object.toString();
            if (s.isEmpty() ||
                "null".equalsIgnoreCase(s)) {
                return 0D;
            }
            try {
                return Double.parseDouble(s);
            } catch (Exception e) {
                return 0D;
            }
        }

        throw new IllegalStateException(
            object + " cannot be converted to Double"
        );
    }
}
