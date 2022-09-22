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
import java.util.*;

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
    public Space getSpace() {
        return Space.$;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == Object.class;
    }

    @Override
    public Object cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        return data;
    }

    @Override
    public Object read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) throws IOException {
        Type type = alias.getType();
        if (type == null ||
            type == Object.class) {
            return solve(alias);
        } else {
            Spare<?> spare = supplier.lookup(type, null);
            if (spare == null) {
                return null;
            }
            if (spare == this) {
                return solve(alias);
            }
            return spare.read(flag, alias);
        }
    }

    @Override
    public Object read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        Type type = value.getType();
        if (type == null ||
            type == Object.class) {
            return solve(value);
        } else {
            Spare<?> spare = supplier.lookup(type, null);
            if (spare == null) {
                return null;
            }
            if (spare == this) {
                return solve(value);
            }
            return spare.read(flag, value);
        }
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
        @NotNull Chain chain
    ) {
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
                if (b == 't') {
                    if (chain.at(1) == 'r' &&
                        chain.at(2) == 'u' &&
                        chain.at(3) == 'e') {
                        return Boolean.TRUE;
                    }
                } else if (b == 'T') {
                    if (chain.at(1) == 'R' &&
                        chain.at(2) == 'U' &&
                        chain.at(3) == 'E') {
                        return Boolean.TRUE;
                    }
                }
                return chain.toString();
            }
            case 5: {
                if (b == 'f') {
                    if (chain.at(1) == 'a' &&
                        chain.at(2) == 'l' &&
                        chain.at(3) == 's' &&
                        chain.at(4) == 'e') {
                        return Boolean.FALSE;
                    }
                } else if (b == 'F') {
                    if (chain.at(1) == 'A' &&
                        chain.at(2) == 'L' &&
                        chain.at(3) == 'S' &&
                        chain.at(4) == 'E') {
                        return Boolean.FALSE;
                    }
                }
                return chain.toString();
            }
        }

        return chain.toString();
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
        return new MapSpare.Builder0(Map.class);
    }
}
