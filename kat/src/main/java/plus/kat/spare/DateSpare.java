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
import plus.kat.utils.*;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author kraity
 * @since 0.0.1
 */
public class DateSpare extends SimpleDateFormat implements Spare<Date> {

    public static final DateSpare
        INSTANCE = new DateSpare();

    public DateSpare() {
        super(Config.get(
            "kat.spare.date.format", "yyyy-MM-dd HH:mm:ss"
        ));
    }

    public DateSpare(
        @NotNull Format format
    ) {
        this(format.value(), format.zone(), format.lang());
    }

    public DateSpare(
        @NotNull String pattern,
        @NotNull String zone,
        @NotNull String locale
    ) {
        super(pattern, locale.isEmpty() ? Locale.getDefault(Locale.Category.FORMAT) : new Locale(locale));
        if (!zone.isEmpty()) {
            setTimeZone(
                TimeZone.getTimeZone(zone)
            );
        }
    }

    @NotNull
    @Override
    public String getSpace() {
        return "Date";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass.isAssignableFrom(Date.class);
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return null;
    }

    @NotNull
    @Override
    public Class<Date> getType() {
        return Date.class;
    }

    @Nullable
    @Override
    public Builder<Date> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @NotNull
    @Override
    public Date cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof Date) {
            return (Date) data;
        }

        if (data instanceof String) {
            synchronized (this) {
                try {
                    return parse(
                        (String) data
                    );
                } catch (Exception e) {
                    return null;
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Date read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOCrash {
        int len = value.length();
        if (len == 0) {
            return null;
        }

        if (len == 10) {
            long sec = value.toLong();
            if (sec > 0) {
                return new Date(
                    sec * 1000
                );
            }
        } else if (len == 13) {
            long mil = value.toLong();
            if (mil > 0) {
                return new Date(mil);
            }
        }

        String text = value.toString();
        synchronized (this) {
            try {
                return parse(text);
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOCrash {
        String result;
        synchronized (this) {
            result = format(
                (Date) value
            );
        }
        flow.addData(result);
    }
}
