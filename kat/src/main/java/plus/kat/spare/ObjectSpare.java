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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.IOException;
import java.lang.reflect.Type;

import static plus.kat.stream.Transfer.*;

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
                Number num = toNumber(
                    data, null
                );
                if (num != null) {
                    return num;
                } else {
                    return data.toString();
                }
            }
        }

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

                // TRUE/True
                else if (w == 'T') {
                    w = flow[1];
                    if (w == 'r') {
                        if (flow[2] == 'u' &&
                            flow[3] == 'e') {
                            return Boolean.TRUE;
                        }
                    }
                    // TRUE
                    else if (w == 'R') {
                        if (flow[2] == 'U' &&
                            flow[3] == 'E') {
                            return Boolean.TRUE;
                        }
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

                // NULL/Null
                else if (w == 'N') {
                    w = flow[1];
                    if (w == 'u') {
                        if (flow[2] == 'l' &&
                            flow[3] == 'l') {
                            return null;
                        }
                    }
                    // NULL
                    else if (w == 'U') {
                        if (flow[2] == 'L' &&
                            flow[3] == 'L') {
                            return null;
                        }
                    }
                }
                return data.toString();
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

                // FALSE/False
                else if (w == 'F') {
                    byte c = flow[1];
                    if (c == 'a') {
                        if (flow[2] == 'l' &&
                            flow[3] == 's' &&
                            flow[4] == 'e') {
                            return Boolean.FALSE;
                        }
                    }
                    // FALSE
                    else if (c == 'A') {
                        if (flow[2] == 'L' &&
                            flow[3] == 'S' &&
                            flow[4] == 'E') {
                            return Boolean.FALSE;
                        }
                    }
                }
                return data.toString();
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
}
