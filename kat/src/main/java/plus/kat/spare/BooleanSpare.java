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
import java.io.Serializable;

/**
 * @author kraity
 * @since 0.0.1
 */
public class BooleanSpare extends Property<Boolean> implements Serializable {

    public static final BooleanSpare
        INSTANCE = new BooleanSpare();

    public BooleanSpare() {
        super(Boolean.class);
    }

    @Override
    public Boolean apply() {
        return false;
    }

    @NotNull
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

    @NotNull
    @Override
    public Boolean cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof Boolean) {
            return (Boolean) data;
        }

        if (data instanceof Number) {
            return ((Number) data).intValue() == 1;
        }

        if (data instanceof CharSequence) {
            CharSequence val = (CharSequence) data;
            switch (val.length()) {
                case 1: {
                    return val.charAt(0) == '1';
                }
                case 4: {
                    return val.charAt(0) == 't'
                        && val.charAt(1) == 'r'
                        && val.charAt(2) == 'u'
                        && val.charAt(3) == 'e';
                }
            }
        }

        return Boolean.FALSE;
    }

    @NotNull
    @Override
    public Boolean read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toBoolean();
    }

    @NotNull
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
