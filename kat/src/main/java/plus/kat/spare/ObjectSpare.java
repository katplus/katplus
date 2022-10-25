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
import plus.kat.kernel.*;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ObjectSpare extends Property<Object> {

    public static final ObjectSpare
        INSTANCE = new ObjectSpare();

    public ObjectSpare() {
        super(Object.class);
    }

    @Override
    public String getSpace() {
        return "$";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == Object.class;
    }

    @Override
    public Object read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) throws IOException {
        return solve(alias);
    }

    @Override
    public Object read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        return solve(value);
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        Spare<?> spare = supplier.lookup(
            value.getClass()
        );
        if (spare != null &&
            spare != this) {
            spare.write(chan, value);
        }
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        Spare<?> spare = supplier.lookup(
            value.getClass()
        );
        if (spare != null &&
            spare != this) {
            spare.write(flow, value);
        }
    }

    @Nullable
    private Object solve(
        @NotNull Chain ch
    ) {
        int length = ch.length();

        if (length == 0) {
            return null;
        }

        byte b = ch.at(0);

        if (b < 0x3A) {
            Number num = ch.toNumber();
            if (num != null) {
                return num;
            }
            return ch.toString();
        }

        switch (length) {
            case 4: {
                // true
                if (b == 't') {
                    if (ch.at(1) == 'r' &&
                        ch.at(2) == 'u' &&
                        ch.at(3) == 'e') {
                        return Boolean.TRUE;
                    }
                }

                // TRUE/True
                else if (b == 'T') {
                    byte c = ch.at(1);
                    if (c == 'R') {
                        if (ch.at(2) == 'U' &&
                            ch.at(3) == 'E') {
                            return Boolean.TRUE;
                        }
                    }

                    // True
                    else if (c == 'r') {
                        if (ch.at(2) == 'u' &&
                            ch.at(3) == 'e') {
                            return Boolean.TRUE;
                        }
                    }
                }
                return ch.toString();
            }
            case 5: {
                // false
                if (b == 'f') {
                    if (ch.at(1) == 'a' &&
                        ch.at(2) == 'l' &&
                        ch.at(3) == 's' &&
                        ch.at(4) == 'e') {
                        return Boolean.FALSE;
                    }
                }

                // FALSE/False
                else if (b == 'F') {
                    byte c = ch.at(1);
                    if (c == 'A') {
                        if (ch.at(2) == 'L' &&
                            ch.at(3) == 'S' &&
                            ch.at(4) == 'E') {
                            return Boolean.FALSE;
                        }
                    }

                    // False
                    else if (c == 'a') {
                        if (ch.at(2) == 'l' &&
                            ch.at(3) == 's' &&
                            ch.at(4) == 'e') {
                            return Boolean.FALSE;
                        }
                    }
                }
                return ch.toString();
            }
        }

        return ch.toString();
    }

    @Override
    public Object cast(
        @Nullable Object data
    ) {
        return data;
    }

    @Override
    public Object cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        return data;
    }

    @Override
    public Builder<?> getBuilder(
        @Nullable Type type
    ) {
        if (type != null &&
            type != Object.class) {
            Spare<?> spare = supplier.lookup(type, null);
            if (spare == null) {
                return null;
            }
            if (spare != this) {
                return spare.getBuilder(type);
            }
        }
        return MapSpare.INSTANCE.getBuilder(type);
    }
}
