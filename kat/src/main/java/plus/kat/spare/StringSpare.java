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
public class StringSpare implements Spare<String> {

    public static final StringSpare
        INSTANCE = new StringSpare();

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$s;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == String.class
            || klass == Object.class
            || klass == CharSequence.class;
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return null;
    }

    @NotNull
    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Nullable
    @Override
    public Builder<String> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @NotNull
    @Override
    public String cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        return data == null ? "" : data.toString();
    }

    @Nullable
    @Override
    public String read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return alias.toString();
    }

    @Nullable
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
    ) throws IOCrash {
        if (flow.isFlag(Flag.UNICODE)) {
            flow.addText(
                value.toString()
            );
        } else {
            flow.addData(
                value.toString()
            );
        }
    }
}
