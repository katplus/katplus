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
public class AtomicIntegerSpare extends Property<AtomicInteger> implements Serializer {

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
        return "AtomicInteger";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == AtomicInteger.class
            || klass == Number.class
            || klass == Object.class;
    }

    @Override
    public AtomicInteger cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data == null) {
            return apply();
        }

        if (data instanceof AtomicInteger) {
            return (AtomicInteger) data;
        }

        return new AtomicInteger(
            IntegerSpare.INSTANCE.cast(
                data, supplier
            )
        );
    }

    @Override
    public AtomicInteger read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return new AtomicInteger(
            alias.toInt()
        );
    }

    @Override
    public AtomicInteger read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return new AtomicInteger(
            value.toInt()
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.addInt(
            ((AtomicInteger) value).get()
        );
    }
}
