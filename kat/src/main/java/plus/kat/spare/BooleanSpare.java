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
import plus.kat.kernel.*;
import plus.kat.stream.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public class BooleanSpare extends Property<Boolean> implements Serializer {

    public static final BooleanSpare
        INSTANCE = new BooleanSpare();

    public BooleanSpare() {
        super(Boolean.class);
    }

    @Override
    public Boolean apply() {
        return Boolean.FALSE;
    }

    @Override
    public Space getSpace() {
        return Space.$b;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == boolean.class
            || klass == Boolean.class
            || klass == Object.class;
    }

    @Override
    public Boolean cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data instanceof Boolean) {
            return (Boolean) data;
        }

        if (data instanceof Number) {
            return ((Number) data).intValue() != 0;
        }

        if (data instanceof Chain) {
            return ((Chain) data).toBoolean();
        }

        if (data instanceof CharSequence) {
            CharSequence val = (CharSequence) data;
            return Convert.toBoolean(
                val, val.length(), false
            );
        }

        return Boolean.FALSE;
    }

    @Override
    public Boolean read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toBoolean();
    }

    @Override
    public Boolean read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toBoolean();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.addBoolean(
            (boolean) value
        );
    }
}
