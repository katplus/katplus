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

import plus.kat.*;
import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.IOException;
import java.math.BigInteger;

import static java.math.BigInteger.*;
import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class BigIntegerSpare extends BaseSpare<BigInteger> {

    public static final BigIntegerSpare
        INSTANCE = new BigIntegerSpare();

    public BigIntegerSpare() {
        super(BigInteger.class);
    }

    @Override
    public String getSpace() {
        return "BigInteger";
    }

    @Override
    public BigInteger apply() {
        return ZERO;
    }

    @Override
    public BigInteger read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        if (value.isNothing()) {
            return null;
        }

        if (value.size() < 20 &&
            value.isDigits()) {
            return valueOf(
                value.toLong()
            );
        } else {
            return new BigInteger(
                latin(value)
            );
        }
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            value.toString()
        );
    }
}
