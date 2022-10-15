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
import java.time.LocalTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

/**
 * @author kraity
 * @since 0.0.2
 */
public class LocalTimeSpare extends TemporalSpare<LocalTime> {

    public static final LocalTimeSpare
        INSTANCE = new LocalTimeSpare();

    public LocalTimeSpare() {
        super(LocalTime.class,
            ISO_LOCAL_TIME
        );
    }

    public LocalTimeSpare(
        @NotNull Format format
    ) {
        super(LocalTime.class, format);
    }

    @Override
    public String getSpace() {
        return "LocalTime";
    }

    @Override
    public LocalTime cast(
        @NotNull String value
    ) throws IOException {
        return LocalTime.from(
            formatter.parse(value)
        );
    }
}
