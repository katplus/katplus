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

import java.io.IOException;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

/**
 * @author kraity
 * @since 0.0.3
 */
public class ZonedDateTimeSpare extends TemporalSpare<ZonedDateTime> {

    public static final ZonedDateTimeSpare
        INSTANCE = new ZonedDateTimeSpare();

    public ZonedDateTimeSpare() {
        super(ZonedDateTime.class,
            ISO_ZONED_DATE_TIME
        );
    }

    public ZonedDateTimeSpare(
        @NotNull Format format
    ) {
        super(ZonedDateTime.class, format);
    }

    @Override
    public CharSequence getSpace() {
        return "ZonedDateTime";
    }

    @Override
    public ZonedDateTime cast(
        @NotNull String value
    ) throws IOException {
        return ZonedDateTime.from(
            formatter.parse(value)
        );
    }
}
