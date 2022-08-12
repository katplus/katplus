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
import plus.kat.utils.Casting;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.BitSet;

/**
 * @author kraity
 * @since 0.0.2
 */
public class BitSetSpare implements Spare<BitSet> {

    public static final BitSetSpare
        INSTANCE = new BitSetSpare();

    @Override
    public String getSpace() {
        return "BitSet";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == BitSet.class
            || klass == Object.class;
    }

    @Override
    public Boolean getFlag() {
        return Boolean.FALSE;
    }

    @Override
    public Class<BitSet> getType() {
        return BitSet.class;
    }

    @Override
    public Builder<BitSet> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0();
    }

    @Override
    public BitSet cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data instanceof BitSet) {
            return (BitSet) data;
        }

        return null;
    }

    @Override
    public BitSet read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        if (flag.isFlag(Flag.STRING_AS_OBJECT)) {
            return Casting.cast(
                this, value, flag, null
            );
        }
        return null;
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        BitSet set = (BitSet) value;
        int len = set.length();
        for (int i = 0; i < len; i++) {
            chan.set(
                null, set.get(i) ? 1 : 0
            );
        }
    }

    public static class Builder0 extends Builder<BitSet> {

        protected int index;
        protected BitSet entity;

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) {
            entity = new BitSet();
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) {
            int i = index++;
            if (value.toBoolean()) {
                entity.set(i);
            }
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOCrash {
            // NOOP
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOCrash {
            throw new UnexpectedCrash(
                "Unexpectedly, invalid BitSet value type '" + space + "'"
            );
        }

        @Nullable
        @Override
        public BitSet getResult() {
            return entity;
        }

        @Override
        public void onDestroy() {
            entity = null;
        }
    }
}
