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

import plus.kat.anno.Format;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;

import java.lang.reflect.Type;
import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * @author kraity
 * @since 0.0.2
 */
public class LocalDateSpare implements Spare<LocalDate> {

    public static final LocalDateSpare
        INSTANCE = new LocalDateSpare();

    private final DateTimeFormatter fmt;

    public LocalDateSpare() {
        fmt = ISO_LOCAL_DATE;
    }

    public LocalDateSpare(
        @NotNull DateTimeFormatter format
    ) {
        fmt = format;
    }

    @Override
    public CharSequence getSpace() {
        return "LocalDate";
    }

    @Override
    public Boolean getFlag() {
        return null;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass.isAssignableFrom(LocalDate.class);
    }

    @Override
    public Class<LocalDate> getType() {
        return LocalDate.class;
    }

    @Override
    public LocalDate read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOCrash {
        return LocalDate.parse(
            value.toString(), fmt
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOCrash {
        flow.addData(
            fmt.format(
                (LocalDate) value
            )
        );
    }

    @Override
    public Builder<LocalDate> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @NotNull
    public static LocalDateSpare of(
        @NotNull Format format
    ) {
        String lang = format.lang();
        String pattern = format.value();

        if ("yyyy-MM-dd".equals(pattern)) {
            if (lang.isEmpty()) {
                return INSTANCE;
            }

            return new LocalDateSpare(
                ISO_LOCAL_DATE.withLocale(
                    new Locale(lang)
                )
            );
        }

        DateTimeFormatterBuilder builder =
            new DateTimeFormatterBuilder()
                .appendPattern(pattern);

        if (lang.isEmpty()) {
            return new LocalDateSpare(
                builder.toFormatter()
            );
        }

        return new LocalDateSpare(
            builder.toFormatter(
                new Locale(lang)
            )
        );
    }
}
