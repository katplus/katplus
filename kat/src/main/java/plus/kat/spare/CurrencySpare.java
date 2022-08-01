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

import java.lang.reflect.Type;
import java.util.Currency;

import static java.util.Currency.getInstance;

/**
 * @author kraity
 * @since 0.0.2
 */
public class CurrencySpare implements Spare<Currency> {

    public static final CurrencySpare
        INSTANCE = new CurrencySpare();

    @NotNull
    @Override
    public String getSpace() {
        return "Currency";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == Currency.class
            || klass == Object.class;
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return null;
    }

    @NotNull
    @Override
    public Class<Currency> getType() {
        return Currency.class;
    }

    @Nullable
    @Override
    public Builder<Currency> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @Nullable
    @Override
    public Currency cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data instanceof Currency) {
            return (Currency) data;
        }

        if (data instanceof CharSequence) {
            CharSequence d = (CharSequence) data;
            if (d.length() != 3) {
                return null;
            }
            try {
                return getInstance(
                    data.toString()
                );
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Currency read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        if (alias.length() != 3) {
            return null;
        }

        return getInstance(
            alias.string()
        );
    }

    @Nullable
    @Override
    public Currency read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        if (value.length() != 3) {
            return null;
        }

        return getInstance(
            value.string()
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOCrash {
        flow.emit(
            value.toString()
        );
    }
}
