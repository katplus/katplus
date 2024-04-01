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
public class LongSpare extends BaseSpare<Long> {

    static final Long ZERO = 0L;

    public static final LongSpare
        INSTANCE = new LongSpare();

    public LongSpare() {
        super(Long.class);
    }

    @Override
    public Long apply() {
        return ZERO;
    }

    @Override
    public String getSpace() {
        return "Long";
    }

    @Override
    public Long read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toLong(null);
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            (Long) value
        );
    }
}
