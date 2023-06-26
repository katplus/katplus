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

import static plus.kat.stream.Transfer.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class DoubleSpare extends BaseSpare<Double> {

    public static final Double
        ZERO_DOUBLE = 0D;

    public static final DoubleSpare
        INSTANCE = new DoubleSpare();

    public DoubleSpare() {
        super(Double.class);
    }

    @Override
    public Double apply() {
        return ZERO_DOUBLE;
    }

    @Override
    public Double apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return ZERO_DOUBLE;
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof Double) {
                    return (Double) arg;
                }
            }
        }

        throw new IllegalStateException(
            "No matching constructor found"
        );
    }

    @Override
    public String getSpace() {
        return "Double";
    }

    @Override
    public Double read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return toDouble(
            value, null
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            (Double) value
        );
    }
}
