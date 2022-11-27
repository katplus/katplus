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
import plus.kat.utils.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Locale.getDefault;
import static java.util.Locale.Category.FORMAT;

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
        @NotNull String format,
        @NotNull String zone,
        @NotNull String locale
    ) {
        this(format, zone, LocaleSpare.lookup(locale));
    }

    public DateSpare(
        @NotNull String format,
        @NotNull String zone,
        @Nullable Locale locale
    ) {
        super(format, locale != null ? locale : getDefault(FORMAT));
        if (!zone.isEmpty()) {
            setTimeZone(
                TimeZone.getTimeZone(zone)
            );
        }
    }

    @Override
    public Date apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        Object obj = resultSet
            .getObject(1);
        Date value = cast(
            obj, supplier
        );

        if (value != null) {
            return value;
        }

        throw new SQLCrash(
            "Cannot convert the type from "
                + obj.getClass() + " to " + Date.class
        );
    }

    @Override
    public Date apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) throws Collapse {
        if (spoiler.hasNext()) {
            Object obj = spoiler
                .getValue();
            Date value = cast(
                obj, supplier
            );

            if (value != null) {
                return value;
            }

            throw new Collapse(
                "Cannot convert the type from "
                    + obj + " to " + Date.class
            );
        } else {
            throw new Collapse(
                "The spoiler doesn't have a next data value"
            );
        }
    }

    @Override
    public String getSpace() {
        return "Date";
    }

    @Override
    public Boolean getFlag() {
        return null;
    }

    @Override
    public Class<Date> getType() {
        return Date.class;
    }

    @Override
    public Boolean getBorder(
        @NotNull Flag flag
    ) {
        if (flag.isFlag(Flag.DATE_AS_DIGIT)) {
            return Boolean.FALSE;
        }
        return null;
    }

    @Override
    public Supplier getSupplier() {
        return Supplier.ins();
    }

    @Override
    public Builder<Date> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @Override
    public Date read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) throws IOException {
        int len = chain.length();
        if (len == 0) {
            return null;
        }

        if (flag.isFlag(Flag.DIGIT_AS_DATE)) {
            long mil = chain.toLong(-1);
            if (mil > -1) {
                return new Date(mil);
            }
        }

        String text = chain.toString();
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
    ) throws IOException {
        Date date = (Date) value;
        if (flow.isFlag(Flag.DATE_AS_DIGIT)) {
            flow.emit(
                date.getTime()
            );
        } else {
            String time;
            synchronized (this) {
                time = format(date);
            }
            flow.emit(time);
        }
    }

    @Override
    public Date cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object != null) {
            if (object instanceof Date) {
                return (Date) object;
            }

            if (object instanceof Long) {
                // as millisecond
                return new Date(
                    (long) object
                );
            }

            if (object instanceof Integer) {
                // as seconds
                return new Date(
                    (int) object * 1000L
                );
            }

            if (object instanceof CharSequence) {
                String d = object.toString();
                if (d.isEmpty()) {
                    return null;
                }
                synchronized (this) {
                    try {
                        return parse(d);
                    } catch (Exception e) {
                        throw new FatalCrash(e);
                    }
                }
            }

            if (object instanceof AtomicLong) {
                // as millisecond
                return new Date(
                    ((AtomicLong) object).get()
                );
            }

            if (object instanceof AtomicInteger) {
                // as seconds
                return new Date(
                    ((AtomicInteger) object).get() * 1000L
                );
            }
        }

        throw new FatalCrash(
            object + " cannot be converted to " + getType()
        );
    }
}
