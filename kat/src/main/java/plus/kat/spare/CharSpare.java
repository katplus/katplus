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
import plus.kat.stream.*;

import java.io.IOException;
import java.lang.reflect.Type;

import static plus.kat.chain.Chain.Unsafe.value;
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
        @Nullable Type type
    ) {
        if (type == null ||
            type == char.class ||
            type == Character.class) {
            return '\0';
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return "c";
    }

    @Override
    public Character read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) {
        int len = chain.length();
        if (chain.charset() != UTF_8) {
            if (len != 1) {
                return '\0';
            }
            return chain.charAt(0);
        } else {
            return Convert.toChar(
                value(chain), len, '\0'
            );
        }
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.emit((char) value);
    }

    @Override
    public Character cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return '\0';
        }

        if (object instanceof Character) {
            return (Character) object;
        }

        if (object instanceof Number) {
            return (char) ((Number) object).intValue();
        }

        if (object instanceof Boolean) {
            return ((boolean) object) ? '1' : '0';
        }

        if (object instanceof Chain) {
            return read(
                null, (Chain) object
            );
        }

        if (object instanceof CharSequence) {
            CharSequence ch = (CharSequence) object;
            if (ch.length() != 1) {
                return '\0';
            } else {
                return ch.charAt(0);
            }
        }

        throw new IllegalStateException(
            object + " cannot be converted to Character"
        );
    }
}
