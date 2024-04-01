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
import plus.kat.lang.*;
import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ObjectSpare extends BaseSpare<Object> {

    public static final ObjectSpare
        INSTANCE = new ObjectSpare();

    public ObjectSpare() {
        super(Object.class);
    }

    @Override
    public String getSpace() {
        return "Any";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public Factory getFactory(
        @Nullable Type type
    ) {
        if (type != null &&
            type != Object.class) {
            Spare<?> spare =
                context.assign(type);
            if (spare == null) {
                return null;
            }
            if (spare != this) {
                return spare.getFactory(type);
            }
        }
        return MapSpare.INSTANCE.getFactory(type);
    }

    @Override
    public Object read(
        @NotNull Flag flag,
        @NotNull Value data
    ) throws IOException {
        int size = data.size();
        if (size == 0) {
            return null;
        }

        byte w;
        byte[] flow = data.flow();

        switch (w = flow[0]) {
            case 0x2B:
            case 0x2E:
            case 0x2D:
            case 0x30:
            case 0x31:
            case 0x32:
            case 0x33:
            case 0x34:
            case 0x35:
            case 0x36:
            case 0x37:
            case 0x38:
            case 0x39: {
                Number num = data.toNumber(null);
                if (num == null) {
                    break;
                } else {
                    return num;
                }
            }
            default: {
                switch (size) {
                    case 4: {
                        // true
                        if (w == 't') {
                            if (flow[1] == 'r' &&
                                flow[2] == 'u' &&
                                flow[3] == 'e') {
                                return Boolean.TRUE;
                            }
                        }
                        // null
                        else if (w == 'n') {
                            if (flow[1] == 'u' &&
                                flow[2] == 'l' &&
                                flow[3] == 'l') {
                                return null;
                            }
                        }
                        break;
                    }
                    case 5: {
                        // false
                        if (w == 'f') {
                            if (flow[1] == 'a' &&
                                flow[2] == 'l' &&
                                flow[3] == 's' &&
                                flow[4] == 'e') {
                                return Boolean.FALSE;
                            }
                        }
                        break;
                    }
                }
            }
        }

        return data.toString();
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        Class<?> clazz = value.getClass();
        if (clazz != Object.class) {
            Spare<?> spare = context.assign(clazz);
            if (spare != null &&
                spare != this) {
                spare.write(chan, value);
            }
        }
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        Class<?> clazz = value.getClass();
        if (clazz != Object.class) {
            Spare<?> spare = context.assign(clazz);
            if (spare != null &&
                spare != this) {
                spare.write(flux, value);
            }
        }
    }

    public static boolean write(
        Chan chan, Object alias, Object value
    ) throws IOException {
        if (value instanceof Iterable ||
            value instanceof Iterator) {
            return chan.set(
                alias, ListSpare.INSTANCE, value
            );
        }

        if (value instanceof CharSequence) {
            return chan.set(
                alias, StringifySpare.INSTANCE, value
            );
        }

        if (value instanceof ByteSequence) {
            return chan.set(
                alias, BinaryifySpare.INSTANCE, value
            );
        }

        // Subclass of Date
        // java.sql.Date
        // java.sql.Time
        // java.sql.Timestamp
        if (value instanceof Date) {
            return chan.set(
                alias, DateSpare.INSTANCE, value
            );
        }

        // Subclass of File
        if (value instanceof File) {
            return chan.set(
                alias, FileSpare.INSTANCE, value
            );
        }

        // Subclass of Number
        if (value instanceof Number) {
            return chan.set(
                alias, NumberSpare.INSTANCE, value
            );
        }

        // Subclass of Charset
        if (value instanceof Charset) {
            Charset o = (Charset) value;
            return chan.set(
                alias, StringSpare.INSTANCE, o.name()
            );
        }

        // Subclass of TimeZone
        // java.util.SimpleTimeZone
        // sun.util.calendar.ZoneInfo
        if (value instanceof TimeZone) {
            return chan.set(
                alias, TimeZoneSpare.INSTANCE, value
            );
        }

        if (value instanceof Calendar) {
            return chan.set(
                alias, CalendarSpare.INSTANCE, value
            );
        }

        if (value instanceof UUID ||
            value instanceof Throwable) {
            return chan.set(
                alias, StringSpare.INSTANCE, value.toString()
            );
        }

        throw new IOException(
            "No available coder for `"
                + value.getClass() + "` was found"
        );
    }
}
