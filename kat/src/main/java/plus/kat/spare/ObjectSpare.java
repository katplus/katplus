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
import plus.kat.crash.*;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ObjectSpare extends Property<Object> {

    public static final ObjectSpare
        INSTANCE = new ObjectSpare();

    public static final Object
        EMPTY_OBJECT = new Object();

    public ObjectSpare() {
        super(Object.class);
    }

    @Override
    public Object apply() {
        return EMPTY_OBJECT;
    }

    @Override
    public Object apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == Object.class) {
            return EMPTY_OBJECT;
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return "$";
    }

    @Override
    public Object read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) throws IOException {
        int length = chain.length();

        if (length == 0) {
            return null;
        }

        byte b = chain.at(0);

        if (b < 0x3A) {
            Number num = chain.toNumber();
            if (num != null) {
                return num;
            }
            return chain.toString();
        }

        switch (length) {
            case 4: {
                // true
                if (b == 't') {
                    if (chain.at(1) == 'r' &&
                        chain.at(2) == 'u' &&
                        chain.at(3) == 'e') {
                        return Boolean.TRUE;
                    }
                }

                // TRUE/True
                else if (b == 'T') {
                    byte c = chain.at(1);
                    if (c == 'R') {
                        if (chain.at(2) == 'U' &&
                            chain.at(3) == 'E') {
                            return Boolean.TRUE;
                        }
                    }

                    // True
                    else if (c == 'r') {
                        if (chain.at(2) == 'u' &&
                            chain.at(3) == 'e') {
                            return Boolean.TRUE;
                        }
                    }
                }
                return chain.toString();
            }
            case 5: {
                // false
                if (b == 'f') {
                    if (chain.at(1) == 'a' &&
                        chain.at(2) == 'l' &&
                        chain.at(3) == 's' &&
                        chain.at(4) == 'e') {
                        return Boolean.FALSE;
                    }
                }

                // FALSE/False
                else if (b == 'F') {
                    byte c = chain.at(1);
                    if (c == 'A') {
                        if (chain.at(2) == 'L' &&
                            chain.at(3) == 'S' &&
                            chain.at(4) == 'E') {
                            return Boolean.FALSE;
                        }
                    }

                    // False
                    else if (c == 'a') {
                        if (chain.at(2) == 'l' &&
                            chain.at(3) == 's' &&
                            chain.at(4) == 'e') {
                            return Boolean.FALSE;
                        }
                    }
                }
                return chain.toString();
            }
        }

        return chain.toString();
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        Class<?> clazz = value.getClass();
        if (clazz != Object.class) {
            Spare<?> spare = supplier.lookup(clazz);
            if (spare != null &&
                spare != this) {
                spare.write(chan, value);
            }
        }
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        Class<?> clazz = value.getClass();
        if (clazz != Object.class) {
            Spare<?> spare = supplier.lookup(clazz);
            if (spare != null &&
                spare != this) {
                spare.write(flow, value);
            }
        }
    }

    @Override
    public Object cast(
        @Nullable Object object
    ) {
        return object;
    }

    @Override
    public Object cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        return object;
    }

    @Override
    public Factory getFactory(
        @Nullable Type type
    ) {
        if (type != null &&
            type != Object.class) {
            Spare<?> spare =
                supplier.search(type);
            if (spare == null) {
                return null;
            }
            if (spare != this) {
                return spare.getFactory(type);
            }
        }
        return MapSpare.INSTANCE.getFactory(type);
    }
}
