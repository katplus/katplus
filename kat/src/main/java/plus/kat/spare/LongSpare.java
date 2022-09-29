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
import plus.kat.kernel.*;
import plus.kat.stream.*;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class LongSpare extends Property<Long> implements Serializer {

    public static final LongSpare
        INSTANCE = new LongSpare();

    public LongSpare() {
        super(Long.class);
    }

    @Override
    public Long apply() {
        return 0L;
    }

    @Override
    public Long apply(
        @NotNull Type type
    ) {
        if (type == long.class ||
            type == Long.class) {
            return 0L;
        }

        throw new Collapse(
            "Unable to create an instance of " + type
        );
    }

    @Override
    public Space getSpace() {
        return Space.$l;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == long.class
            || clazz == Long.class
            || clazz == Number.class
            || clazz == Object.class;
    }

    @Override
    public Long read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toLong();
    }

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

    @Override
    public Long cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
            if (data instanceof Long) {
                return (Long) data;
            }

            if (data instanceof Number) {
                return ((Number) data).longValue();
            }

            if (data instanceof Boolean) {
                return ((boolean) data) ? 1L : 0L;
            }

            if (data instanceof Chain) {
                return ((Chain) data).toLong();
            }

            if (data instanceof CharSequence) {
                CharSequence num = (CharSequence) data;
                return Convert.toLong(
                    num, num.length(), 10L, 0L
                );
            }
        }
        return 0L;
    }
}
