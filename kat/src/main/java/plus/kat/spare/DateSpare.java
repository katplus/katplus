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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author kraity
 * @since 0.0.1
 */
public class DateSpare extends SimpleDateFormat implements Spare<Date>, Serializer {

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
        @NotNull String language
    ) {
        super(pattern, LocaleSpare.lookup(language, Locale.Category.FORMAT));
        if (!zone.isEmpty()) {
            super.setTimeZone(
                TimeZone.getTimeZone(zone)
            );
        }
    }

    @Override
    public String getSpace() {
        return "Date";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == Date.class
            || klass == Object.class;
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
    public Builder<Date> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @Override
    public Date apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        ResultSetMetaData meta =
            resultSet.getMetaData();
        int count = meta.getColumnCount();
        if (count != 1) {
            throw new SQLCrash(
                "Expected 1, actual " + count
            );
        }

        Object val = resultSet.getObject(1);
        if (val == null) {
            return null;
        }

        if (val instanceof Date) {
            return (Date) val;
        }

        Date var = cast(
            supplier, val
        );
        if (var != null) {
            return var;
        }

        throw new SQLCrash(
            "Cannot convert the type from " + val.getClass() + " to " + Date.class
        );
    }

    @Override
    public Date cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data instanceof Date) {
            return (Date) data;
        }

        if (data instanceof Long) {
            return new Date(
                (long) data
            );
        }

        if (data instanceof Integer) {
            return new Date(
                (int) data * 1000L
            );
        }

        if (data instanceof CharSequence) {
            String d = data.toString();
            if (d.isEmpty()) {
                return null;
            }
            synchronized (this) {
                try {
                    return parse(d);
                } catch (Exception e) {
                    return null;
                }
            }
        }

        return null;
    }

    @Override
    public Date read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOCrash {
        int len = value.length();
        if (len == 0) {
            return null;
        }

        long mil = value.toLong(-1);
        if (mil >= 0) {
            return new Date(mil);
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
    ) throws IOException {
        Date date = (Date) value;
        if (flow.isFlag(Flag.DATE_AS_TIMESTAMP)) {
            flow.addLong(
                date.getTime()
            );
        } else {
            String s;
            synchronized (this) {
                s = format(date);
            }
            if (flow.getJob() != Job.JSON) {
                flow.emit(s);
            } else {
                flow.addByte((byte) '"');
                flow.emit(s);
                flow.addByte((byte) '"');
            }
        }
    }
}
