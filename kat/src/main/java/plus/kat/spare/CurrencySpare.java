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
import java.util.Locale;
import java.util.Currency;

import static java.util.Currency.*;

/**
 * @author kraity
 * @since 0.0.2
 */
public class CurrencySpare extends BaseSpare<Currency> {

    public static final CurrencySpare
        INSTANCE = new CurrencySpare();

    public CurrencySpare() {
        super(Currency.class);
    }

    @Override
    public Currency apply() {
        return getInstance(
            Locale.getDefault()
        );
    }

    @Override
    public String getSpace() {
        return "Currency";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public Currency read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (value.isNothing()) {
            return null;
        }

        if (value.size() == 3) {
            return getInstance(
                value.toLatin()
            );
        }

        throw new IOException(
            "Received `" + value
                + "` is not a currency code"
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            ((Currency) value).getCurrencyCode()
        );
    }
}
