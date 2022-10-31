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

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.5
 */
@SuppressWarnings("unchecked")
public class AlphaSpare extends Property<Alpha> {

    public static final AlphaSpare
        INSTANCE = new AlphaSpare(Alpha.class);

    public AlphaSpare(
        @NotNull Class<?> clazz
    ) {
        super((Class<Alpha>) clazz);
    }

    @Override
    public Alpha apply() {
        Class<?> cls = klass;
        if (cls == Alpha.class) {
            return new Alpha();
        }
        if (cls == Alias.class) {
            return new Alias();
        }
        if (cls == Space.class) {
            return new Space();
        }
        if (cls == Value.class) {
            return new Value();
        }
        try {
            return (Alpha) cls.newInstance();
        } catch (Exception e) {
            throw new Collapse(
                "Unable to create 'Alpha' instance of '" + cls + "'"
            );
        }
    }

    @Override
    public String getSpace() {
        return "s";
    }

    @Override
    public Alpha read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) throws IOException {
        Alpha chain = apply();
        chain.join(alias);
        return chain;
    }

    @Override
    public Alpha read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        Alpha chain = apply();
        chain.join(value);
        return chain;
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.emit(
            (Chain) value
        );
    }

    @Override
    public Alpha cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data == null) {
            return apply();
        }

        if (klass.isInstance(data)) {
            return (Alpha) data;
        }

        if (data instanceof char[]) {
            Alpha chain = apply();
            chain.join(
                (char[]) data
            );
            return chain;
        }

        if (data instanceof byte[]) {
            Alpha chain = apply();
            chain.join(
                (byte[]) data
            );
            return chain;
        }

        if (data instanceof Chain) {
            Alpha chain = apply();
            chain.join(
                (Chain) data
            );
            return chain;
        }

        if (data instanceof CharSequence) {
            Alpha chain = apply();
            chain.join(
                (CharSequence) data
            );
            return chain;
        }

        return apply();
    }
}
