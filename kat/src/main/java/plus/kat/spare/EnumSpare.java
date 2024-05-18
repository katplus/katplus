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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.IdentityHashMap;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public class EnumSpare<T extends Enum<T>> extends BaseSpare<T> {

    private final T[] enums;
    private final String space;

    private Map<Object, T> read;
    private Map<T, String> write;

    public EnumSpare(
        @Nilable String space,
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super((Class<T>) klass, context);
        try {
            Method method = klass
                .getMethod("values");
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            this.space = space;
            this.enums = (T[]) method
                .invoke(null, (Object[]) null);
            onFields(
                klass.getFields()
            );
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private void onFields(
        @NotNull Field[] fields
    ) throws Throwable {
        for (Field item : fields) {
            if ((item.getType() != klass) ||
                (item.getModifiers() & 0x8) != 0x8 ||
                (item.getAnnotation(Magic.class) == null)) {
                continue;
            }

            read = new HashMap<>();
            write = new IdentityHashMap<>();

            for (Field field : fields) {
                if ((field.getType() != klass) ||
                    (field.getModifiers() & 0x8) != 0x8) {
                    continue;
                }

                T e = (T) field.get(null);
                Magic magic = field.getAnnotation(Magic.class);

                if (magic != null) {
                    String[] names = magic.value();
                    if (names.length > 0) {
                        for (String name : names) {
                            if (read.putIfAbsent(name, e) != null) {
                                throw new IllegalStateException(
                                    "Failed to set the enum<" + name + "> of `"
                                        + klass.getName() + "` because it already exists"
                                );
                            }
                        }
                        write.put(e, names[0]);
                        continue;
                    }
                }

                write.put(e, field.getName());
                if (read.put(field.getName(), e) != null) {
                    throw new IllegalStateException(
                        "Failed to set the enum<" + field + "> of `"
                            + klass.getName() + "` because it already exists"
                    );
                }
            }
            break;
        }
    }

    @NotNull
    public T apply() {
        return apply(0);
    }

    @NotNull
    public T apply(int i) {
        T[] e = enums;
        if (0 <= i && i < e.length) {
            return e[i];
        }
        throw new IllegalStateException(
            "Not found the enum for " + i
        );
    }

    @Override
    public String getSpace() {
        return space;
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
    public T read(
        @NotNull Flag flag,
        @NotNull Value name
    ) throws IOException {
        if (name.isNothing()) {
            return null;
        }

        if (name.isDigits() &&
            flag.isFlag(Flag.INDEX_AS_ENUM)) {
            return apply(
                name.toInt()
            );
        }

        Map<Object, T> tab = read;
        if (tab != null) {
            T e = tab.get(name);
            if (e != null) {
                return e;
            }
        } else {
            for (T e : enums) {
                if (name.equals(e.name())) {
                    return e;
                }
            }
        }

        throw new IOException(
            "No enum for " + name + " was found"
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        T e = (T) value;
        if (flux.isFlag(Flag.ENUM_AS_INDEX)) {
            flux.emit(
                e.ordinal()
            );
        } else {
            Map<T, String> tab = write;
            if (tab == null) {
                flux.emit(
                    e.name()
                );
            } else {
                String name = tab.get(e);
                if (name != null) {
                    flux.emit(name);
                } else {
                    throw new IOException(
                        "Not found the name for " + e
                    );
                }
            }
        }
    }
}
