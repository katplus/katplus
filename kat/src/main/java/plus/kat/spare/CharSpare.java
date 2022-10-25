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
import plus.kat.kernel.*;
import plus.kat.stream.*;

import java.io.IOException;
import java.lang.reflect.Type;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kraity
 * @since 0.0.1
 */
public class CharSpare extends Property<Character> {

    public static final CharSpare
        INSTANCE = new CharSpare();

    public CharSpare() {
        super(Character.class);
    }

    @Override
    public Character apply() {
        return '\0';
    }

    @Override
    public Character apply(
        @NotNull Type type
    ) {
        if (type == char.class ||
            type == Character.class) {
            return '\0';
        }

        throw new Collapse(
            "Unable to create an instance of " + type
        );
    }

    @Override
    public String getSpace() {
        return "c";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == char.class
            || clazz == Character.class
            || clazz == Object.class;
    }

    @NotNull
    @Override
    public Character read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return Convert.toChar(
            Unsafe.value(alias), alias.length(), '\0'
        );
    }

    @NotNull
    @Override
    public Character read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return Convert.toChar(
            Unsafe.value(value), value.length(), '\0'
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.emit(
            (char) value
        );
    }

    @Override
    public Character cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
            if (data instanceof Character) {
                return (Character) data;
            }

            if (data instanceof Number) {
                return (char) ((Number) data).intValue();
            }

            if (data instanceof Boolean) {
                return ((boolean) data) ? '1' : '0';
            }

            if (data instanceof Chain) {
                Chain chain = (Chain) data;
                int len = chain.length();
                if (chain.charset() != UTF_8) {
                    if (len != 1) {
                        return '\0';
                    }
                    return chain.charAt(0);
                } else {
                    return Convert.toChar(
                        Unsafe.value(chain), len, '\0'
                    );
                }
            }

            if (data instanceof CharSequence) {
                CharSequence ch = (CharSequence) data;
                if (ch.length() == 1) {
                    return ch.charAt(0);
                }
            }
        }
        return '\0';
    }
}
