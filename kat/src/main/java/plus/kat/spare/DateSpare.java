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
import plus.kat.utils.*;

import java.io.*;
import java.text.*;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Locale.getDefault;
import static java.util.Locale.Category.FORMAT;

/**
 * @author kraity
 * @since 0.0.1
 */
public class DateSpare extends BaseSpare<Date> implements Spare<Date> {

    public static final DateSpare
        INSTANCE = new DateSpare();

    static {
        INSTANCE.style.format(null);
    }

    private final SimpleDateStyle style;

    public DateSpare() {
        super(Date.class);
        style = new SimpleDateStyle(
            Config.get(
                "kat.date.format",
                "yyyy-MM-dd HH:mm:ss"
            )
        );
    }

    public DateSpare(
        @NotNull String format,
        @NotNull String zone,
        @NotNull String locale
    ) {
        super(Date.class);
        style = new SimpleDateStyle(format, zone, locale);
    }

    @Override
    public String getSpace() {
        return "Date";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        if (flag.isFlag(Flag.DATE_AS_DIGIT)) {
            return null;
        }
        return Border.QUOTE;
    }

    @Override
    public Date read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (value.isNothing()) {
            return null;
        }

        if (flag.isFlag(Flag.DIGIT_AS_DATE)) {
            if (value.isDigits()) {
                return new Date(
                    value.toLong()
                );
            }
        }

        return style.read(
            value.toString()
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        Date date = (Date) value;
        if (flux.isFlag(Flag.DATE_AS_DIGIT)) {
            flux.emit(
                date.getTime()
            );
        } else {
            style.write(
                flux, date
            );
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    private static class SimpleDateStyle extends SimpleDateFormat {

        static FieldPosition FP;
        private StringBuffer SB;
        private ParsePosition PP;

        public SimpleDateStyle(
            @NotNull String format
        ) {
            super(
                format, getDefault(FORMAT)
            );
        }

        public SimpleDateStyle(
            @NotNull String format,
            @NotNull String zone,
            @NotNull String locale
        ) {
            super(format, locale.isEmpty() ?
                getDefault(FORMAT) : new Locale(locale)
            );
            if (!zone.isEmpty()) {
                setTimeZone(
                    TimeZone.getTimeZone(zone)
                );
            }
        }

        public Date read(
            @NotNull String text
        ) throws IOException {
            int errorIndex;
            synchronized (this) {
                ParsePosition pos = PP;
                if (pos != null) {
                    pos.setIndex(0);
                } else {
                    PP = pos = new ParsePosition(0);
                }

                Date date = super.parse(text, pos);
                if (date != null) {
                    return date;
                } else {
                    errorIndex = pos.getErrorIndex();
                }
            }

            throw new IOException(
                "Failed to parse this date: `" +
                    text + "`, error index is " + errorIndex
            );
        }

        public void write(
            @NotNull Flux flux,
            @NotNull Date date
        ) throws IOException {
            synchronized (this) {
                StringBuffer buf = SB;
                if (buf != null) {
                    buf.setLength(0);
                } else {
                    SB = buf = new StringBuffer();
                }
                super.format(
                    date, SB, FP
                );
                int size = buf.length();
                for (int i = 0; i < size; i++) {
                    flux.emit(
                        buf.charAt(i)
                    );
                }
            }
        }

        public StringBuffer format(
            Date date, StringBuffer buffer, FieldPosition position
        ) {
            if (FP == null) {
                FP = position;
                return SB = buffer;
            } else {
                return super.format(
                    date, buffer, position
                );
            }
        }
    }
}
