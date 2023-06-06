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

import plus.kat.*;
import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.Method;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public class EnumSpare<T extends Enum<T>> extends BeanSpare<T> {

    private T[] enums;

    public EnumSpare(
        @Nilable String space,
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        this(
            space, null, klass, context
        );
    }

    public EnumSpare(
        @Nullable String space,
        @Nullable T[] enums,
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super(
            space, (Class<T>) klass, context
        );
        if (enums != null) {
            this.enums = enums;
        } else try {
            Method method = klass
                .getMethod("values");
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            this.enums = (T[]) method.invoke(null);
        } catch (Exception e) {
            // Nothing
        }
    }

    @Override
    public T apply() {
        T[] e = enums;
        if (e != null &&
            e.length != 0) {
            return e[0];
        }
        return null;
    }

    @Override
    public T apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == klass) {
            T[] e = enums;
            if (e != null &&
                e.length != 0) {
                return e[0];
            }
            throw new IllegalStateException(
                "Failed to apply " + type
            );
        }

        throw new IllegalStateException(
            "Failed to build this " + type
        );
    }

    @Override
    public T read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        T[] e = enums;
        if (e != null && value.isAnything()) {
            if (flag.isFlag(Flag.INDEX_AS_ENUM)) {
                if (value.isDigits()) {
                    int i = value.toInt();
                    if (-1 < i && i < e.length) {
                        return e[i];
                    }
                }
            }
            for (T em : e) {
                if (value.equals(em.name())) {
                    return em;
                }
            }
        }
        return null;
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        Enum<?> e = (Enum<?>) value;
        if (flux.isFlag(Flag.ENUM_AS_INDEX)) {
            flux.emit(
                e.ordinal()
            );
        } else {
            flux.emit(e.name());
        }
    }

    @Override
    public String getSpace() {
        return space;
    }

    @Override
    public Boolean getScope() {
        return null;
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        if (flag.isFlag(Flag.ENUM_AS_INDEX)) {
            return null;
        }
        return Border.QUOTE;
    }

    @Override
    public Factory getFactory(
        @Nullable Type type
    ) {
        return null;
    }
}
