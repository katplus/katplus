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
import plus.kat.stream.*;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author kraity
 * @since 0.0.1
 */
public class LongSpare extends DataSpare<Long> implements Serializable {

    public static final LongSpare
        INSTANCE = new LongSpare();

    public LongSpare() {
        super(Long.class);
    }

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$l;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == long.class
            || klass == Long.class
            || klass == Number.class
            || klass == Object.class;
    }

    @NotNull
    @Override
    public Long cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof Long) {
            return (Long) data;
        }

        if (data instanceof Number) {
            return ((Number) data).longValue();
        }

        if (data instanceof Boolean) {
            return ((boolean) data) ? 1L : 0L;
        }

        if (data instanceof CharSequence) {
            CharSequence num = (CharSequence) data;
            return Convert.toLong(
                num, num.length(), 10L, 0L
            );
        }

        return 0L;
    }

    @NotNull
    @Override
    public Long read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toLong();
    }

    @NotNull
    @Override
    public Long read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toLong();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.addLong(
            (long) value
        );
    }
}
