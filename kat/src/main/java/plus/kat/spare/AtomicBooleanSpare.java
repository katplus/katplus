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
import plus.kat.crash.*;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author kraity
 * @since 0.0.2
 */
public class AtomicBooleanSpare extends DataSpare<AtomicBoolean> implements Serializable {

    public static final AtomicBooleanSpare
        INSTANCE = new AtomicBooleanSpare();

    public AtomicBooleanSpare() {
        super(AtomicBoolean.class);
    }

    @NotNull
    @Override
    public String getSpace() {
        return "AtomicBoolean";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == AtomicBoolean.class
            || klass == Object.class;
    }

    @Nullable
    @Override
    public AtomicBoolean cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data instanceof AtomicBoolean) {
            return (AtomicBoolean) data;
        }

        return new AtomicBoolean(
            BooleanSpare.INSTANCE.cast(
                supplier, data
            )
        );
    }

    @NotNull
    @Override
    public AtomicBoolean read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return new AtomicBoolean(
            alias.toBoolean()
        );
    }

    @NotNull
    @Override
    public AtomicBoolean read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return new AtomicBoolean(
            value.toBoolean()
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOCrash {
        flow.addBoolean(
            ((AtomicBoolean) value).get()
        );
    }
}
