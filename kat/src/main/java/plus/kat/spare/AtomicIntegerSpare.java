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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author kraity
 * @since 0.0.2
 */
public class AtomicIntegerSpare extends Property<AtomicInteger> {

    public static final AtomicIntegerSpare
        INSTANCE = new AtomicIntegerSpare();

    public AtomicIntegerSpare() {
        super(AtomicInteger.class);
    }

    @Override
    public AtomicInteger apply() {
        return new AtomicInteger();
    }

    @Override
    public String getSpace() {
        return "i";
    }

    @Override
    public Boolean getBorder(
        @NotNull Flag flag
    ) {
        return Boolean.FALSE;
    }

    @Override
    public AtomicInteger read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) {
        return new AtomicInteger(
            chain.toInt()
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.emit(
            ((AtomicInteger) value).get()
        );
    }

    @Override
    public AtomicInteger cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (object instanceof AtomicInteger) {
            return (AtomicInteger) object;
        }

        return new AtomicInteger(
            IntegerSpare.INSTANCE.cast(
                object, supplier
            )
        );
    }
}
