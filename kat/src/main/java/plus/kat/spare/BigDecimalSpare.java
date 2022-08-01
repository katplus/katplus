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
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author kraity
 * @since 0.0.1
 */
public class BigDecimalSpare implements Spare<BigDecimal> {

    public static final BigDecimalSpare
        INSTANCE = new BigDecimalSpare();

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$D;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == BigDecimal.class
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
    public Class<BigDecimal> getType() {
        return BigDecimal.class;
    }

    @Nullable
    @Override
    public Builder<BigDecimal> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @NotNull
    @Override
    public BigDecimal cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof BigDecimal) {
            return (BigDecimal) data;
        }

        if (data instanceof Number) {
            if (data instanceof BigInteger) {
                return new BigDecimal(
                    (BigInteger) data
                );
            }

            if (data instanceof Float
                || data instanceof Double) {
                return new BigDecimal(
                    data.toString()
                );
            }

            return BigDecimal.valueOf(
                ((Number) data).longValue()
            );
        }

        if (data instanceof Boolean) {
            return ((boolean) data) ? BigDecimal.ONE : BigDecimal.ZERO;
        }

        if (data instanceof String) {
            try {
                return new BigDecimal(
                    (String) data
                );
            } catch (Exception e) {
                // nothing
            }
        }

        return BigDecimal.ZERO;
    }

    @NotNull
    @Override
    public BigDecimal read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toBigDecimal();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOCrash {
        flow.addChars(
            value.toString()
        );
    }
}
