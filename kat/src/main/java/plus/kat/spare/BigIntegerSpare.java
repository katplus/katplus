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
import plus.kat.kernel.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author kraity
 * @since 0.0.1
 */
public class BigIntegerSpare extends Property<BigInteger> {

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
    public String getSpace() {
        return "I";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == BigInteger.class
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
    public BigInteger read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        if (!value.isBlank()) {
            long num = value.toLong();
            if (num != 0) {
                return BigInteger.valueOf(num);
            }
            try {
                return new BigInteger(
                    value.toString()
                );
            } catch (Exception e) {
                // Nothing
            }
        }
        return BigInteger.ZERO;
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
    public BigInteger cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
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

            if (data instanceof Value) {
                Chain chain = (Chain) data;
                if (!chain.isBlank()) {
                    long num = chain.toLong();
                    if (num != 0) {
                        return BigInteger.valueOf(num);
                    }
                    try {
                        return new BigInteger(
                            chain.toString()
                        );
                    } catch (Exception e) {
                        // Nothing
                    }
                }
                return BigInteger.ZERO;
            }

            if (data instanceof CharSequence) {
                try {
                    return new BigInteger(
                        data.toString()
                    );
                } catch (Exception e) {
                    // Nothing
                }
            }
        }
        return BigInteger.ZERO;
    }
}
