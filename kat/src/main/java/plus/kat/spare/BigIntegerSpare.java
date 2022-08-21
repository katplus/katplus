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
public class BigIntegerSpare extends Property<BigInteger> implements Serializer {

    public static final BigIntegerSpare
        INSTANCE = new BigIntegerSpare();

    public BigIntegerSpare() {
        super(BigInteger.class);
    }

    @Override
    public BigInteger apply() {
        return BigInteger.ZERO;
    }

    @Override
    public Space getSpace() {
        return Space.$I;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == BigInteger.class
            || klass == Number.class
            || klass == Object.class;
    }

    @Override
    public BigInteger cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof BigInteger) {
            return (BigInteger) data;
        }

        if (data instanceof Number) {
            if (data instanceof BigDecimal) {
                return ((BigDecimal) data).toBigInteger();
            }

            return BigInteger.valueOf(
                ((Number) data).longValue()
            );
        }

        if (data instanceof Boolean) {
            return ((boolean) data) ? BigInteger.ONE : BigInteger.ZERO;
        }

        if (data instanceof String) {
            try {
                return new BigInteger(
                    data.toString()
                );
            } catch (Exception e) {
                // nothing
            }
        }

        return BigInteger.ZERO;
    }

    @Override
    public BigInteger read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toBigInteger();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.addChars(
            value.toString()
        );
    }
}
