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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ObjectSpare implements Spare<Object> {

    public static final ObjectSpare
        INSTANCE = new ObjectSpare();

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == Object.class;
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return null;
    }

    @NotNull
    @Override
    public Class<Object> getType() {
        return Object.class;
    }

    @Nullable
    @Override
    @SuppressWarnings("rawtypes")
    public Builder<Map> getBuilder(
        @Nullable Type type
    ) {
        return new MapSpare.Builder0(type);
    }

    @Nullable
    @Override
    public Object cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        return data;
    }

    @Nullable
    @Override
    public Object read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        int length = value.length();

        if (length == 0) {
            return null;
        }

        byte b = value.at(0);

        if (b < 0x3A) {
            if (b > 0x2F) {
                if (length < 10) {
                    int num = value.toInt(-1);
                    if (num != -1) {
                        return num;
                    }
                } else {
                    long num = value.toLong(-1);
                    if (num > Integer.MAX_VALUE) {
                        return num;
                    } else if (num != -1) {
                        return (int) num;
                    }
                }
            } else if (b != 0x2D) {
                return value.toString();
            } else {
                if (length < 11) {
                    int num = value.toInt(1);
                    if (num != 1) {
                        return num;
                    }
                } else {
                    long num = value.toLong(1);
                    if (num < Integer.MIN_VALUE) {
                        return num;
                    } else if (num != 1) {
                        return (int) num;
                    }
                }
            }

            int i = 1, r = 0;
            while (i < length) {
                byte t = value.at(i++);
                if (t > 0x39) {
                    return value.toString();
                }

                if (t < 0x30) {
                    if (t != '.' ||
                        ++r == 2) {
                        return value.toString();
                    }
                }
            }

            try {
                return Double.parseDouble(
                    value.string()
                );
            } catch (Exception e) {
                return value.toString();
            }
        }

        switch (length) {
            case 4: {
                if (b == 't') {
                    if (value.at(1) == 'r' &&
                        value.at(2) == 'u' &&
                        value.at(3) == 'e') {
                        return Boolean.TRUE;
                    }
                } else if (b == 'T') {
                    if (value.at(1) == 'R' &&
                        value.at(2) == 'U' &&
                        value.at(3) == 'E') {
                        return Boolean.TRUE;
                    }
                }
                return value.toString();
            }
            case 5: {
                if (b == 'f') {
                    if (value.at(1) == 'a' &&
                        value.at(2) == 'l' &&
                        value.at(3) == 's' &&
                        value.at(4) == 'e') {
                        return Boolean.FALSE;
                    }
                } else if (b == 'F') {
                    if (value.at(1) == 'A' &&
                        value.at(2) == 'L' &&
                        value.at(3) == 'S' &&
                        value.at(4) == 'E') {
                        return Boolean.FALSE;
                    }
                }
                return value.toString();
            }
        }

        return value.toString();
    }

    @Override
    public void write(
        Chan chan,
        Object value
    ) throws IOException {
        if (value instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                chan.set(
                    entry.getKey().toString(),
                    entry.getValue()
                );
            }
        } else if (value instanceof Iterable) {
            for (Object entry : (Iterable<?>) value) {
                chan.set(
                    null, entry
                );
            }
        }
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        if (value instanceof Number) {
            if (value instanceof Integer) {
                flow.addInt(
                    (int) value
                );
            } else if (value instanceof Long) {
                flow.addLong(
                    (long) value
                );
            } else {
                flow.addChars(
                    value.toString()
                );
            }
        } else if (value instanceof Boolean) {
            if ((boolean) value) {
                flow.addByte((byte) 't');
                flow.addByte((byte) 'r');
                flow.addByte((byte) 'u');
                flow.addByte((byte) 'e');
            } else {
                flow.addByte((byte) 'f');
                flow.addByte((byte) 'a');
                flow.addByte((byte) 'l');
                flow.addByte((byte) 's');
                flow.addByte((byte) 'e');
            }
        } else {
            if (flow.isFlag(Flag.UNICODE)) {
                flow.text(
                    value.toString()
                );
            } else {
                flow.emit(
                    value.toString()
                );
            }
        }
    }
}
