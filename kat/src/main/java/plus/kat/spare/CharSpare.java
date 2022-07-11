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
import plus.kat.entity.*;

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class CharSpare implements Spare<Character> {

    public static final CharSpare
        INSTANCE = new CharSpare();

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$c;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == char.class
            || klass == Character.class
            || klass == Object.class;
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return null;
    }

    @NotNull
    @Override
    public Class<Character> getType() {
        return char.class;
    }

    @Nullable
    @Override
    public Builder<Character> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @NotNull
    @Override
    public Character cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof Character) {
            return (Character) data;
        }

        if (data instanceof Number) {
            return (char) ((Number) data).intValue();
        }

        if (data instanceof Boolean) {
            return ((boolean) data) ? '1' : '0';
        }

        if (data instanceof CharSequence) {
            CharSequence ch = (CharSequence) data;
            if (ch.length() == 1) {
                return ch.charAt(0);
            }
        }

        return '?';
    }

    @NotNull
    @Override
    public Character read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toChar();
    }

    @NotNull
    @Override
    public Character read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toChar();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOCrash {
        flow.addData(
            (char) value
        );
    }
}
