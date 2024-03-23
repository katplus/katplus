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
import java.util.Date;
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

    public T apply(
        @NotNull long time
    ) {
        return null;
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

        if (stateOf(value) == 0) {
            return apply(
                value.toLong()
            );
        }

        int i = 0;
        int l = value.size();

        scope:
        {
            byte w;
            byte[] v = value.flow();

            check:
            {
                if (9 < l && l < 30) {
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
                    }
                }
                if (flag.isFlag(Flag.DIGIT_AS_TIME)) {
                    return apply(
                        value.toLong()
                    );
                }
                break scope;
            }

            int e = 4, n = 0, a;
            int y = 0, m = 1, d;

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
                a = v[i++] - 0x30;
                if (a < 0 ||
                    a > 9) {
                    break scope;
                } else {
                    n = a + n * 10;
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

            int scale = 60000;
            if (i == l) {
                scale = 1;
                // truncate up to
                // 3 leap seconds
                if (s > 59 && s < 63) {
                    s = 59;
                }
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
            calendar.set(MONTH, m - 1);
            calendar.set(DAY_OF_MONTH, d);
            calendar.set(HOUR_OF_DAY, h);
            calendar.set(MINUTE, min);
            calendar.set(SECOND, s);
            calendar.set(MILLISECOND, mil);
            if (scale != 1) {
                calendar.set(DST_OFFSET, 0);
                calendar.set(ZONE_OFFSET, scale);
            }
            return apply(
                calendar.getTimeInMillis()
            );
        }

        throw new IOException(
            "Failed to parse time [" +
                value + "], starting at position: " + i
        );
    }

    protected void serialize(
        @NotNull Flux flux,
        @NotNull Date value
    ) throws IOException {
        Calendar calendar = CALENDAR.get();
        calendar.clear();
        calendar.setLenient(false);
        calendar.setTimeInMillis(
            value.getTime()
        );
        serialize(flux, calendar);
    }

    protected void serialize(
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

        flux.emit((byte) ' ');
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
    }
}
