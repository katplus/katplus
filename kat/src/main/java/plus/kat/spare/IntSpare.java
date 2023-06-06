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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.IOException;
import java.lang.reflect.Type;

import static plus.kat.stream.Transfer.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class IntSpare extends BaseSpare<Integer> {

    public static final Integer
        ZERO_INT = 0;

    public static final IntSpare
        INSTANCE = new IntSpare();

    public IntSpare() {
        super(Integer.class);
    }

    @Override
    public Integer apply() {
        return ZERO_INT;
    }

    @Override
    public Integer apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == int.class ||
            type == Integer.class) {
            return ZERO_INT;
        }

        throw new IllegalStateException(
            "Failed to build this " + type
        );
    }

    @Override
    public String getSpace() {
        return "Int";
    }

    @Override
    public Integer read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return toInteger(
            value, null
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            (Integer) value
        );
    }
}
