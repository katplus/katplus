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
import java.util.concurrent.atomic.*;

/**
 * @author kraity
 * @since 0.0.2
 */
public class AtomicIntegerSpare extends BaseSpare<AtomicInteger> {

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
    public AtomicInteger apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return apply();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof Integer) {
                    return new AtomicInteger(
                        (Integer) arg
                    );
                }
            }
        }

        throw new IllegalStateException(
            "No matching constructor found"
        );
    }

    @Override
    public String getSpace() {
        return "Int";
    }

    @Override
    public AtomicInteger read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        if (value.isNothing()) {
            return null;
        }
        return new AtomicInteger(
            value.toInt()
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            ((AtomicInteger) value).get()
        );
    }
}
