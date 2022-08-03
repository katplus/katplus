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
import plus.kat.entity.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kraity
 * @since 0.0.2
 */
public class AtomicLongSpare implements Spare<AtomicLong>, Serializable {

    public static final AtomicLongSpare
        INSTANCE = new AtomicLongSpare();

    @NotNull
    @Override
    public String getSpace() {
        return "AtomicLong";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == AtomicLong.class
            || klass == Number.class
            || klass == Object.class;
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return null;
    }

    @NotNull
    @Override
    public Class<AtomicLong> getType() {
        return AtomicLong.class;
    }

    @Nullable
    @Override
    public Builder<AtomicLong> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @Nullable
    @Override
    public AtomicLong cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data instanceof AtomicLong) {
            return (AtomicLong) data;
        }

        return new AtomicLong(
            LongSpare.INSTANCE.cast(
                supplier, data
            )
        );
    }

    @NotNull
    @Override
    public AtomicLong read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return new AtomicLong(
            alias.toLong()
        );
    }

    @NotNull
    @Override
    public AtomicLong read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return new AtomicLong(
            value.toLong()
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOCrash {
        flow.addLong(
            ((AtomicLong) value).get()
        );
    }
}
