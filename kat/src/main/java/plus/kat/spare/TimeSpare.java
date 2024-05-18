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

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.GregorianCalendar;

import static java.util.Calendar.*;
import static plus.kat.lang.Uniform.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public abstract class TimeSpare<T> extends BaseSpare<T> {

    public static final ThreadLocal<Calendar>
        CALENDAR = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return new GregorianCalendar();
        }
    };

    public TimeSpare(
        Class<T> klass
    ) {
        super(klass);
    }

    public TimeSpare(
        Class<T> klass,
        Context context
    ) {
        super(klass, context);
    }

    public abstract T apply(
        @NotNull long time
    );

    public T apply(
        @NotNull Calendar calendar
    ) {
        return apply(
            calendar.getTimeInMillis()
        );
    }

    public Border getBorder(
        @NotNull Flag flag
    ) {
        if (flag.isFlag(Flag.TIME_AS_DIGIT)) {
            return null;
        }
        return Border.QUOTE;
    }

    public T read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (value.isNothing()) {
            return null;
        }

        int i = 0, x = 7;
        int l = value.size();

        scope:
        {
            byte w;
            byte[] v = value.flow();

            check:
            {
                if (l > 9) {
                    switch (w = v[4]) {
                        case '.':
                        case '-':
                        case '/': {
                            if (w == v[7]) {
                                break check;
                            } else {
                                break scope;
                            }
                        }
                        default: {
                            switch (v[8]) {
                                case ' ':
                                case 'T': {
                                    x = 6;
                                    break check;
                                }
                            }
                        }
                    }
                }
                if (stateOf(value) == 0 ||
                    flag.isFlag(Flag.DIGIT_AS_TIME)) {
                    return apply(
                        value.toLong()
                    );
                }
                break scope;
            }

            int e = 4, n = 0, a;
            int y = 0, M = 0, d;

            check:
            while (true) {
                a = v[i++] - 0x30;
                if (a < 0 ||
                    a > 9) {
                    break scope;
                } else {
                    n = a + n * 10;
                }

                if (i == e) {
                    switch (e) {
                        case 4: {
                            y = n;
                            n = 0;
                            i = x - 2;
                            e = x;
                            break;
                        }
                        case 6: {
                            M = n;
                            n = 0;
                            e = 8;
                            break;
                        }
                        case 7: {
                            M = n;
                            n = 0;
                            i = 8;
                            e = 10;
                            break;
                        }
                        case 8:
                        case 10: {
                            d = n;
                            n = 0;
                            e = e + 3;
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
                calendar.setTimeZone(
                    TimeZone.getDefault()
                );
                calendar.set(YEAR, y);
                calendar.set(MONTH, M - 1);
                calendar.set(DAY_OF_MONTH, d);
                return apply(calendar);
            }

            switch (v[i++]) {
                case ' ':
                case 'T': {
                    if (l > e + 2 &&
                        v[e] == ':') {
                        break;
                    }
                }
                default: {
                    break scope;
                }
            }

            int H = 0, s = 0;
            int m = 0, ms = 0;

            check:
            while (true) {
                a = v[i++] - 0x30;
                if (a < 0 ||
                    a > 9) {
                    break scope;
                } else {
                    n = a + n * 10;
                }

                if (i == e) {
                    switch (e) {
                        case 11:
                        case 13: {
                            H = n;
                            n = 0;
                            i += 1;
                            e += 3;
                            break;
                        }
                        case 14:
                        case 16: {
                            m = n;
                            if (i < l && v[i] == ':') {
                                if (l > e + 2) {
                                    n = 0;
                                    i += 1;
                                    e += 3;
                                    break;
                                } else {
                                    break scope;
                                }
                            }
                            break check;
                        }
                        case 17:
                        case 19: {
                            s = n;
                            if (i < l && v[i] == '.') {
                                if (l > e + 1) {
                                    n = 1000;
                                    while (++i < l) {
                                        a = v[i] - 0x30;
                                        if (a < 0 ||
                                            a > 9) {
                                            break;
                                        }
                                        if (n != 0) {
                                            n /= 10;
                                            ms += a * n;
                                        }
                                    }
                                } else {
                                    break scope;
                                }
                            }
                            break check;
                        }
                    }
                }
            }

            int scale = 60000;
            if (i == l) {
                scale = 1;
            } else {
                check:
                switch (v[i++]) {
                    case 'Z': {
                        if (i == l) {
                            scale = 0;
                            break;
                        } else {
                            break scope;
                        }
                    }
                    case '-': {
                        scale = -scale;
                    }
                    case '+': {
                        switch (l - i) {
                            case 5: {
                                if (v[i + 2] != ':') {
                                    break scope;
                                }
                            }
                            case 4: {
                                int m0 = v[l - 2] - '0';
                                int m1 = v[l - 1] - '0';
                                if (m0 < 0 || m0 > 5 ||
                                    m1 < 0 || m1 > 9) {
                                    break scope;
                                }

                                int h0 = v[i] - '0';
                                int h1 = v[i + 1] - '0';
                                if (h0 < 0 || h0 > 3 ||
                                    h1 < 0 || h1 > 9 ||
                                    (h0 = h0 * 10 + h1) > 23) {
                                    break scope;
                                } else {
                                    scale *= h0 * 60 + m0 * 10 + m1;
                                    break check;
                                }
                            }
                        }
                    }
                    default: {
                        break scope;
                    }
                }
            }

            calendar = CALENDAR.get();
            calendar.clear();
            calendar.setLenient(false);
            calendar.set(YEAR, y);
            calendar.set(MONTH, M - 1);
            calendar.set(DAY_OF_MONTH, d);
            calendar.set(HOUR_OF_DAY, H);
            calendar.set(MINUTE, m);
            calendar.set(SECOND, s);
            calendar.set(MILLISECOND, ms);
            if (scale == 1) {
                calendar.setTimeZone(
                    TimeZone.getDefault()
                );
            } else {
                calendar.set(DST_OFFSET, 0);
                calendar.set(ZONE_OFFSET, scale);
            }
            return apply(calendar);
        }

        throw new IOException(
            "Failed to parse time [" +
                value + "], starting at position: " + i
        );
    }

    public void write(
        @NotNull Flux flux,
        @NotNull long value
    ) throws IOException {
        Calendar calendar = CALENDAR.get();
        calendar.clear();
        calendar.setLenient(false);
        calendar.setTimeZone(
            TimeZone.getDefault()
        );
        calendar.setTimeInMillis(value);
        write(flux, calendar);
    }

    public void write(
        @NotNull Flux flux,
        @NotNull Calendar value
    ) throws IOException {
        int num = value.get(YEAR);
        flux.emit((byte) (num / 1000 + 0x30));
        flux.emit((byte) (num / 100 % 10 + 0x30));
        flux.emit((byte) (num / 10 % 10 + 0x30));
        flux.emit((byte) (num % 10 + 0x30));

        flux.emit((byte) '-');
        num = value.get(MONTH) + 1;
        flux.emit((byte) (num / 10 + 0x30));
        flux.emit((byte) (num % 10 + 0x30));

        flux.emit((byte) '-');
        num = value.get(DAY_OF_MONTH);
        flux.emit((byte) (num / 10 + 0x30));
        flux.emit((byte) (num % 10 + 0x30));

        flux.emit((byte) 'T');
        num = value.get(HOUR_OF_DAY);
        flux.emit((byte) (num / 10 + 0x30));
        flux.emit((byte) (num % 10 + 0x30));

        flux.emit((byte) ':');
        num = value.get(MINUTE);
        flux.emit((byte) (num / 10 + 0x30));
        flux.emit((byte) (num % 10 + 0x30));

        flux.emit((byte) ':');
        num = value.get(SECOND);
        flux.emit((byte) (num / 10 + 0x30));
        flux.emit((byte) (num % 10 + 0x30));

        flux.emit((byte) '.');
        num = value.get(MILLISECOND);
        flux.emit((byte) (num / 100 + 0x30));
        flux.emit((byte) (num / 10 % 10 + 0x30));
        flux.emit((byte) (num % 10 + 0x30));

        int zone = value.get(ZONE_OFFSET);
        if (zone == 0) {
            flux.emit((byte) 'Z');
            return;
        }

        if (zone > 0) {
            flux.emit((byte) '+');
        } else {
            zone = -zone;
            flux.emit((byte) '-');
        }

        num = zone / 3600000;
        flux.emit((byte) (num / 10 + 0x30));
        flux.emit((byte) (num % 10 + 0x30));

        flux.emit((byte) ':');
        num = (zone % 3600000) / 60000;
        flux.emit((byte) (num / 10 + 0x30));
        flux.emit((byte) (num % 10 + 0x30));
    }
}
