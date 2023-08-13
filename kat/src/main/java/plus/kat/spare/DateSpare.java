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

import java.io.*;
import java.util.*;

import static java.util.Locale.*;
import static java.util.Calendar.*;
import static plus.kat.spare.TimeZoneSpare.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class DateSpare extends BaseSpare<Date> implements Spare<Date> {

    public static final DateSpare
        INSTANCE = new DateSpare();

    static final ThreadLocal<Calendar>
        CALENDAR = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return new GregorianCalendar(US);
        }
    };

    public DateSpare() {
        super(Date.class);
    }

    @Override
    public Date apply() {
        return new Date();
    }

    @NotNull
    public Date apply(
        @NotNull long msec
    ) {
        return new Date(msec);
    }

    @Override
    public Date apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return apply();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof Long) {
                    return apply(
                        (Long) arg
                    );
                }
                if (arg instanceof Integer) {
                    return apply(
                        (Integer) arg
                    );
                }
            }
        }

        throw new IllegalStateException(
            "No matching constructor found"
        );
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
        @NotNull Value data
    ) throws IOException {
        if (data.isNothing()) {
            return null;
        }

        if (flag.isFlag(Flag.DIGIT_AS_DATE)) {
            if (data.isDigits()) {
                return new Date(
                    data.toLong()
                );
            }
        }

        scope:
        {
            int l = data.size();
            if (l < 10 || l > 29) {
                break scope;
            }

            byte w;
            byte[] v = data.flow();

            switch (w = v[4]) {
                default: {
                    break scope;
                }
                case '.':
                case '-':
                case '/': {
                    if (w == v[7]) {
                        break;
                    } else {
                        break scope;
                    }
                }
            }

            int y = 0, m = 1, d;
            int i = 0, e = 4, n = 0;

            check:
            while (true) {
                int a = v[i++] - 0x30;
                if (-1 < a && a < 10) {
                    n = a + n * 10;
                } else {
                    throw new IOException(
                        "Invalid date: " + data
                    );
                }

                if (i == e) {
                    switch (i) {
                        case 4: {
                            y = n;
                            n = 0;
                            i = 5;
                            e = 7;
                            break;
                        }
                        case 7: {
                            m = n;
                            n = 0;
                            i = 8;
                            e = 10;
                            break;
                        }
                        case 10: {
                            d = n;
                            n = 0;
                            e = 13;
                            break check;
                        }
                    }
                }
            }

            Calendar calendar;
            if (i == l) {
                calendar = CALENDAR.get();
                calendar.clear();
                calendar.setLenient(false);
                calendar.setTimeZone(DEF);
                calendar.set(YEAR, y);
                calendar.set(MONTH, m - 1);
                calendar.set(DAY_OF_MONTH, d);
                return apply(
                    calendar.getTimeInMillis()
                );
            }

            switch (v[i++]) {
                case ' ':
                case 'T': {
                    if (l > 15 &&
                        v[13] == ':') {
                        break;
                    }
                }
                default: {
                    break scope;
                }
            }

            int h = 0, s = 0;
            int min = 0, mil = 0;

            check:
            while (true) {
                int a = v[i++] - 0x30;
                if (-1 < a && a < 10) {
                    n = a + n * 10;
                } else {
                    throw new IOException(
                        "Invalid date: " + data
                    );
                }

                if (i == e) {
                    switch (i) {
                        case 13: {
                            h = n;
                            n = 0;
                            i = 14;
                            e = 16;
                            break;
                        }
                        case 16: {
                            min = n;
                            if (i == l ||
                                v[i] != ':') {
                                break check;
                            } else {
                                if (l > 18) {
                                    n = 0;
                                    i = 17;
                                    e = 19;
                                    break;
                                } else {
                                    break scope;
                                }
                            }
                        }
                        case 19: {
                            s = n;
                            if (i == l ||
                                v[i] != '.') {
                                break check;
                            } else {
                                if (l > 22) {
                                    n = 0;
                                    i = 20;
                                    e = 23;
                                    break;
                                } else {
                                    break scope;
                                }
                            }
                        }
                        case 23: {
                            mil = n;
                            break check;
                        }
                    }
                }
            }

            TimeZone zone;
            if (i == l) {
                zone = DEF;
                // truncate up to 3 leap seconds
                if (s > 59 && s < 63) {
                    s = 59;
                }
            } else {
                switch (w = v[i++]) {
                    default: {
                        break scope;
                    }
                    case 'Z': {
                        if (i == l) {
                            zone = UTC;
                            break;
                        } else {
                            break scope;
                        }
                    }
                    case '+':
                    case '-': {
                        switch (l - i) {
                            default: {
                                break scope;
                            }
                            case 4: {
                                break;
                            }
                            case 5: {
                                if (v[i + 2] == ':') {
                                    break;
                                } else {
                                    break scope;
                                }
                            }
                        }

                        char c3 = (char) v[l - 1];
                        char c2 = (char) v[l - 2];

                        int m0 = c2 - '0';
                        int m1 = c3 - '0';
                        if (m0 < 0 || m0 > 5 ||
                            m1 < 0 || m1 > 9) {
                            break scope;
                        }

                        char c0 = (char) v[i];
                        char c1 = (char) v[i + 1];

                        int h0 = c0 - '0';
                        int h1 = c1 - '0';
                        if (h0 < 0 || h0 > 3 ||
                            h1 < 0 || h1 > 9 ||
                            (h0 = h0 * 10 + h1) > 23) {
                            break scope;
                        }

                        if (h0 == 0 &&
                            m0 == 0 &&
                            m1 == 0) {
                            zone = UTC;
                        } else {
                            zone = TimeZone.getTimeZone(
                                new String(
                                    new char[]{
                                        'G', 'M', 'T',
                                        (char) w,
                                        c0, c1, ':', c2, c3
                                    }
                                )
                            );
                        }
                    }
                }
            }

            calendar = CALENDAR.get();
            calendar.clear();
            calendar.setLenient(false);
            calendar.setTimeZone(zone);
            calendar.set(YEAR, y);
            calendar.set(MONTH, m - 1);
            calendar.set(DAY_OF_MONTH, d);
            calendar.set(HOUR_OF_DAY, h);
            calendar.set(MINUTE, min);
            calendar.set(SECOND, s);
            calendar.set(MILLISECOND, mil);
            return apply(
                calendar.getTimeInMillis()
            );
        }

        throw new IOException(
            "Failed to parse date [" + data + ']'
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
            Calendar calendar = CALENDAR.get();
            calendar.clear();
            calendar.setLenient(false);
            calendar.setTimeZone(DEF);

            calendar.setTime(date);
            int num = calendar.get(YEAR);

            flux.emit((byte) (num / 1000 + 0x30));
            flux.emit((byte) (num / 100 % 10 + 0x30));
            flux.emit((byte) (num / 10 % 10 + 0x30));
            flux.emit((byte) (num % 10 + 0x30));

            flux.emit((byte) '-');
            num = calendar.get(MONTH) + 1;
            flux.emit((byte) (num / 10 + 0x30));
            flux.emit((byte) (num % 10 + 0x30));

            flux.emit((byte) '-');
            num = calendar.get(DAY_OF_MONTH);
            flux.emit((byte) (num / 10 + 0x30));
            flux.emit((byte) (num % 10 + 0x30));

            flux.emit((byte) ' ');
            num = calendar.get(HOUR_OF_DAY);
            flux.emit((byte) (num / 10 + 0x30));
            flux.emit((byte) (num % 10 + 0x30));

            flux.emit((byte) ':');
            num = calendar.get(MINUTE);
            flux.emit((byte) (num / 10 + 0x30));
            flux.emit((byte) (num % 10 + 0x30));

            flux.emit((byte) ':');
            num = calendar.get(SECOND);
            flux.emit((byte) (num / 10 + 0x30));
            flux.emit((byte) (num % 10 + 0x30));

            flux.emit((byte) '.');
            num = calendar.get(MILLISECOND);
            flux.emit((byte) (num / 100 + 0x30));
            flux.emit((byte) (num / 10 % 10 + 0x30));
            flux.emit((byte) (num % 10 + 0x30));
        }
    }
}
