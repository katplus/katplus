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
    public Boolean getBorder(
        @NotNull Flag flag
    ) {
        return Boolean.FALSE;
    }

    @Override
    public BigDecimal read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) {
        if (!chain.isBlank()) {
            try {
                return new BigDecimal(
                    chain.toString()
                );
            } catch (Exception e) {
                // Nothing
            }
        }
        return BigDecimal.ZERO;
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
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return BigDecimal.ZERO;
        }

        if (object instanceof BigDecimal) {
            return (BigDecimal) object;
        }

        if (object instanceof Number) {
            if (object instanceof BigInteger) {
                return new BigDecimal(
                    (BigInteger) object
                );
            }

            if (object instanceof Float
                || object instanceof Double) {
                return new BigDecimal(
                    object.toString()
                );
            }

            return BigDecimal.valueOf(
                ((Number) object).longValue()
            );
        }

        if (object instanceof Boolean) {
            return ((boolean) object) ? BigDecimal.ONE : BigDecimal.ZERO;
        }

        if (object instanceof Chain) {
            return read(
                null, (Chain) object
            );
        }

        if (object instanceof CharSequence) {
            try {
                return new BigDecimal(
                    object.toString()
                );
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }

        throw new IllegalStateException(
            object + " cannot be converted to " + klass
        );
    }
}
