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
import plus.kat.stream.*;

import java.io.IOException;

import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public class BinarySpare extends BaseSpare<Binary> {

    public static final BinarySpare
        INSTANCE = new BinarySpare();

    public BinarySpare() {
        super(Binary.class);
    }

    @Override
    public String getSpace() {
        return "Binary";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public Binary read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return binary(value);
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            (Binary) value
        );
    }
}
