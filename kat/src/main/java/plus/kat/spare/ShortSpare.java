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
 * @since 0.0.1
 */
public class ShortSpare extends BaseSpare<Short> {

    public static final Short
        ZERO_SHORT = 0;

    public static final ShortSpare
        INSTANCE = new ShortSpare();

    public ShortSpare() {
        super(Short.class);
    }

    @Override
    public Short apply() {
        return ZERO_SHORT;
    }

    @Override
    public Short apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return ZERO_SHORT;
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof Short) {
                    return (Short) arg;
                }
            }
        }

        throw new IllegalStateException(
            "No matching constructor found"
        );
    }

    @Override
    public String getSpace() {
        return "Short";
    }

    @Override
    public Short read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        if (value.isNothing()) {
            return null;
        }

        int i = value.toInt();
        if (Short.MAX_VALUE >= i &&
            Short.MIN_VALUE <= i) {
            return (short) i;
        }

        throw new IllegalArgumentException(
            "Failed to convert the value to Short, " +
                "where this value is literally `" + value + '`'
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            ((Short) value).intValue()
        );
    }
}
