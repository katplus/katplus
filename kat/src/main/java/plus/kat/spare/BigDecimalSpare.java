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
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author kraity
 * @since 0.0.1
 */
public class BigDecimalSpare extends Property<BigDecimal> {

    public static final BigDecimalSpare
        INSTANCE = new BigDecimalSpare();

    public BigDecimalSpare() {
        super(BigDecimal.class);
    }

    @Override
    public BigDecimal apply() {
        return BigDecimal.ZERO;
    }

    @Override
    public String getSpace() {
        return "D";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == BigDecimal.class
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
    ) throws IOException {
        flow.emit(
            value.toString()
        );
    }

    @Override
    public BigDecimal cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
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

            if (data instanceof Value) {
                return ((Value) data).toBigDecimal();
            }

            if (data instanceof CharSequence) {
                try {
                    return new BigDecimal(
                        data.toString()
                    );
                } catch (Exception e) {
                    // Nothing
                }
            }
        }
        return BigDecimal.ZERO;
    }
}
