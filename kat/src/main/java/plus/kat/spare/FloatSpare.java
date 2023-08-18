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
public class FloatSpare extends BaseSpare<Float> {

    static final Float ZERO = 0F;

    public static final FloatSpare
        INSTANCE = new FloatSpare();

    public FloatSpare() {
        super(Float.class);
    }

    @Override
    public Float apply() {
        return ZERO;
    }

    @Override
    public String getSpace() {
        return "Float";
    }

    @Override
    public Float read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return toFloat(
            value, null
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            (Float) value
        );
    }
}
