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
import plus.kat.stream.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public class StringSpare extends Property<String> {

    public static final StringSpare
        INSTANCE = new StringSpare();

    public StringSpare() {
        super(String.class);
    }

    @Override
    public String apply() {
        return "";
    }

    @Override
    public Space getSpace() {
        return Space.$s;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == String.class
            || clazz == Object.class
            || clazz == CharSequence.class;
    }

    @Override
    public String cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data instanceof String) {
            return (String) data;
        }

        if (data == null) {
            return "";
        }

        if (data instanceof char[]) {
            return new String(
                (char[]) data
            );
        }

        if (data instanceof byte[]) {
            return Binary.latin(
                Base64.REC4648.INS.encode(
                    (byte[]) data
                )
            );
        }

        return data.toString();
    }

    @Override
    public String read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toString();
    }

    @Override
    public String read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toString();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        if (flow.isFlag(Flag.UNICODE)) {
            flow.text(
                (CharSequence) value
            );
        } else {
            flow.emit(
                (CharSequence) value
            );
        }
    }
}
