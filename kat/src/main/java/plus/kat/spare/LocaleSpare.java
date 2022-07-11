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
import plus.kat.kernel.*;

import java.lang.reflect.Type;
import java.util.Locale;

/**
 * @author kraity
 * @since 0.0.1
 */
public class LocaleSpare implements Spare<Locale> {

    public static final LocaleSpare
        INSTANCE = new LocaleSpare();

    @NotNull
    @Override
    public String getSpace() {
        return "Locale";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == Locale.class
            || klass == Object.class;
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return null;
    }

    @NotNull
    @Override
    public Class<Locale> getType() {
        return Locale.class;
    }

    @Nullable
    @Override
    public Builder<Locale> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @Nullable
    @Override
    public Locale cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data instanceof Locale) {
            return (Locale) data;
        }

        if (data instanceof String) {
            try {
                return parse(
                    (String) data
                );
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Locale read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return parse(alias);
    }

    @Nullable
    @Override
    public Locale read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return parse(value);
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOCrash {
        flow.addText(
            value.toString()
        );
    }

    @Nullable
    public static Locale parse(
        @NotNull Chain c
    ) {
        int len = c.length();
        if (len == 0) {
            return null;
        }

        int d1 = c.indexOf(
            (byte) '_'
        );

        if (d1 < 0) {
            return new Locale(
                c.toString()
            );
        }

        int d2 = c.indexOf(
            (byte) '_', d1 + 1
        );

        if (d2 < 0) {
            return new Locale(
                c.toString(0, d1),
                c.toString(d1 + 1, len)
            );
        }

        return new Locale(
            c.toString(0, d1),
            c.toString(d1 + 1, d2),
            c.toString(d2 + 1, len)
        );
    }

    @Nullable
    public static Locale parse(
        @NotNull String c
    ) {
        int len = c.length();
        if (len == 0) {
            return null;
        }

        int d1 = c.indexOf('_');

        if (d1 < 0) {
            return new Locale(c);
        }

        int d2 = c.indexOf(
            '_', d1 + 1
        );

        if (d2 < 0) {
            return new Locale(
                c.substring(0, d1),
                c.substring(d1 + 1, len)
            );
        }

        return new Locale(
            c.substring(0, d1),
            c.substring(d1 + 1, d2),
            c.substring(d2 + 1, len)
        );
    }
}
