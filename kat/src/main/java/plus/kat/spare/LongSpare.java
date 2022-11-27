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
import plus.kat.stream.*;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class LongSpare extends Property<Long> {

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
        @Nullable Type type
    ) {
        if (type == null ||
            type == long.class ||
            type == Long.class) {
            return 0L;
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return "l";
    }

    @Override
    public Boolean getBorder(
        @NotNull Flag flag
    ) {
        return Boolean.FALSE;
    }

    @Override
    public Long read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) {
        return chain.toLong();
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
        flow.emit((long) value);
    }

    @Override
    public Long cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return 0L;
        }

        if (object instanceof Long) {
            return (Long) object;
        }

        if (object instanceof Number) {
            return ((Number) object).longValue();
        }

        if (object instanceof Boolean) {
            return ((boolean) object) ? 1L : 0L;
        }

        if (object instanceof Chain) {
            return ((Chain) object).toLong();
        }

        if (object instanceof CharSequence) {
            CharSequence num = (CharSequence) object;
            return Convert.toLong(
                num, num.length(), 10L, 0L
            );
        }

        throw new IllegalStateException(
            object + " cannot be converted to Long"
        );
    }
}
