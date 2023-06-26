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
    private byte[] names;

    public EnumSpare(
        @Nilable String space,
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super(
            space, (Class<T>) klass, context
        );
        try {
            Method method = klass
                .getMethod("values");
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            T[] beans = (T[]) method.invoke(
                null, (Object[]) null
            );
            int size = beans.length;
            if (size == 0) {
                return;
            }
            this.enums = beans;
            this.names = new byte[size];
            String name;
            for (int i = 0; i < size; i++) {
                name = beans[i].name();
                names[i] = (byte) name.charAt(0);
            }
        } catch (ReflectiveOperationException e) {
            // Ignore this exception
        }
    }

    @Override
    public T apply() {
        T[] e = enums;
        return e == null ? null : e[0];
    }

    @Override
    public T apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return apply();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof String) {
                    T[] e = enums;
                    if (e != null) {
                        for (T t : e) {
                            if (t.name().equals(arg)) {
                                return t;
                            }
                        }
                    }
                    return null;
                }

                if (arg instanceof Integer) {
                    T[] e = enums;
                    if (e == null) {
                        return null;
                    }
                    int i = (int) arg;
                    return -1 < i && i < e.length ? e[i] : null;
                }
            }
        }

        throw new IllegalStateException(
            "No matching constructor found"
        );
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

    @Override
    public T read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        T[] e = enums;
        if (e != null && value.isAnything()) {
            if (flag.isFlag(Flag.INDEX_AS_ENUM)) {
                if (value.isDigits()) {
                    int i = value.toInt();
                    if (i < e.length) {
                        return e[i];
                    } else {
                        return null;
                    }
                }
            }

            int m = value.size();
            byte[] v = value.flow();

            byte x = v[0];
            byte[] ns = names;

            int max = e.length;
            for (int i = 0; i < max; i++) {
                check:
                if (x == ns[i]) {
                    T t = e[i];
                    String n = t.name();
                    if (m == n.length()) {
                        for (int j = 0; j < m; j++) {
                            if (n.charAt(j) !=
                                (char) (v[j] & 0xFF)) {
                                break check;
                            }
                        }
                        return t;
                    }
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
        if (flux.isFlag(Flag.ENUM_AS_INDEX)) {
            flux.emit(
                ((Enum<?>) value).ordinal()
            );
        } else {
            flux.emit(
                ((Enum<?>) value).name()
            );
        }
    }
}
