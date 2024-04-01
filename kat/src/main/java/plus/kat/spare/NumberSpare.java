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

/**
 * @author kraity
 * @since 0.0.3
 */
public class NumberSpare extends BaseSpare<Number> {

    public static final NumberSpare
        INSTANCE = new NumberSpare();

    public NumberSpare() {
        super(Number.class);
    }

    @Override
    public String getSpace() {
        return "Number";
    }

    @Override
    public Number read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toNumber(null);
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        if (value instanceof Integer) {
            flux.emit(
                (int) value
            );
        } else if (value instanceof Long) {
            flux.emit(
                (long) value
            );
        } else if (value instanceof Float) {
            flux.emit(
                (float) value
            );
        } else if (value instanceof Double) {
            flux.emit(
                (double) value
            );
        } else if (value instanceof Byte) {
            flux.emit(
                ((Byte) value).intValue()
            );
        } else if (value instanceof Short) {
            flux.emit(
                ((Short) value).intValue()
            );
        } else if (value instanceof Number) {
            flux.emit(
                value.toString()
            );
        }
    }
}
