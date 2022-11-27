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
 * @since 0.0.5
 */
@SuppressWarnings("unchecked")
public class ChainSpare extends Property<Chain> {

    public static final ChainSpare
        INSTANCE = new ChainSpare(Chain.class);

    public ChainSpare(
        @NotNull Class<?> clazz
    ) {
        super((Class<Chain>) clazz);
    }

    @Override
    public Chain apply() {
        return apply(klass);
    }

    @Override
    public Chain apply(
        @Nullable Type type
    ) {
        if (type == null) {
            type = klass;
        }
        if (type == Chain.class) {
            return new Chain();
        }
        if (type == Alias.class) {
            return new Alias();
        }
        if (type == Space.class) {
            return new Space();
        }
        if (type == Value.class) {
            return new Value();
        }
        if (type instanceof Class) {
            Class<?> cls = (Class<?>) type;
            if (Chain.class.isAssignableFrom(cls)) {
                try {
                    return (Chain) cls.newInstance();
                } catch (Exception e) {
                    throw new Collapse(
                        "Failed to apply", e
                    );
                }
            }
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return "s";
    }

    @Override
    public Chain read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) throws IOException {
        Chain value = apply();
        value.join(chain);
        return value;
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
    public Chain cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (klass.isInstance(object)) {
            return (Chain) object;
        }

        if (object instanceof char[]) {
            Chain chain = apply();
            chain.join(
                (char[]) object
            );
            return chain;
        }

        if (object instanceof byte[]) {
            Chain chain = apply();
            chain.join(
                (byte[]) object
            );
            return chain;
        }

        if (object instanceof Chain) {
            Chain chain = apply();
            chain.join(
                (Chain) object
            );
            return chain;
        }

        if (object instanceof CharSequence) {
            Chain chain = apply();
            chain.join(
                (CharSequence) object
            );
            return chain;
        }

        throw new IllegalStateException(
            object + " cannot be converted to " + klass
        );
    }
}
