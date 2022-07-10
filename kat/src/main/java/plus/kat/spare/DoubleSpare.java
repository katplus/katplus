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
import plus.kat.entity.*;

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class DoubleSpare implements Spare<Double> {

    public static final DoubleSpare
        INSTANCE = new DoubleSpare();

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$d;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == double.class
            || klass == Double.class
            || klass == Number.class
            || klass == Object.class;
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return null;
    }

    @NotNull
    @Override
    public Class<Double> getType() {
        return double.class;
    }

    @Nullable
    @Override
    public Builder<Double> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @NotNull
    @Override
    public Double cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof Double) {
            return (Double) data;
        }

        if (data instanceof Number) {
            return ((Number) data).doubleValue();
        }

        if (data instanceof Boolean) {
            return ((boolean) data) ? 1D : 0D;
        }

        if (data instanceof CharSequence) {
            try {
                return Double.parseDouble(
                    data.toString()
                );
            } catch (Exception e) {
                // nothing
            }
        }

        return 0D;
    }

    @Nullable
    @Override
    public Double read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toDouble();
    }

    @Nullable
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
    ) throws IOCrash {
        if (flow.isFlag(Flag.FLOAT_AS_BITMAP)) {
            flow.addDouble(
                (double) value, true
            );
        } else {
            flow.addDouble(
                (double) value
            );
        }
    }
}
