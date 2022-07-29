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

import plus.kat.crash.*;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

/**
 * @author kraity
 * @since 0.0.2
 */
public class LocalDateTimeSpare extends TemporalSpare<LocalDateTime> {

    public static final LocalDateTimeSpare
        INSTANCE = new LocalDateTimeSpare();

    public LocalDateTimeSpare() {
        super(LocalDateTime.class,
            ISO_LOCAL_DATE_TIME
        );
    }

    public LocalDateTimeSpare(
        @NotNull Format format
    ) {
        super(LocalDateTime.class, format);
    }

    @Override
    public CharSequence getSpace() {
        return "LocalDateTime";
    }

    @Override
    public LocalDateTime cast(
        @NotNull String value
    ) throws IOCrash {
        return LocalDateTime.from(
            formatter.parse(value)
        );
    }
}
