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

import plus.kat.anno.Embed;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.stream.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public class EnumSpare<T extends Enum<T>> extends Property<T> {

    protected String[] spaces;
    private T[] enums;
    private final String space;

    public EnumSpare(
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        this(
            klass.getAnnotation(Embed.class), klass, supplier
        );
    }

    public EnumSpare(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        this(embed, null, klass, supplier);
    }

    public EnumSpare(
        @Nullable Embed embed,
        @Nullable T[] enums,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        super(klass, supplier);
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

        if (embed == null) {
            space = klass.getSimpleName();
        } else {
            String[] names = embed.value();
            if (names.length != 0) {
                space = (spaces = names)[0];
            } else {
                space = klass.getSimpleName();
            }
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
            throw new Collapse(
                "Failed to apply"
            );
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return space;
    }

    @Override
    public Boolean getBorder(
        @NotNull Flag flag
    ) {
        if (flag.isFlag(Flag.ENUM_AS_INDEX)) {
            return Boolean.FALSE;
        }
        return null;
    }

    @Override
    public Spare<T> join(
        @NotNull Supplier supplier
    ) {
        supplier.embed(
            klass, this
        );
        if (spaces != null) {
            for (String space : spaces) {
                if (space.indexOf('.', 1) != -1) {
                    supplier.embed(space, this);
                }
            }
        }
        return this;
    }

    @Override
    public Spare<T> drop(
        @NotNull Supplier supplier
    ) {
        supplier.revoke(
            klass, this
        );
        if (spaces != null) {
            for (String space : spaces) {
                if (space.indexOf('.', 1) != -1) {
                    supplier.revoke(space, this);
                }
            }
        }
        return this;
    }

    @Override
    public T read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) {
        T[] e = enums;
        if (e != null && !chain.isBlank()) {
            if (flag.isFlag(Flag.INDEX_AS_ENUM)) {
                int i = chain.toInt(-1);
                if (-1 < i && i < e.length) {
                    return e[i];
                }
            }
            for (T em : e) {
                if (chain.equals(em.name())) {
                    return em;
                }
            }
        }
        return null;
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        Enum<?> e = (Enum<?>) value;
        if (flow.isFlag(Flag.ENUM_AS_INDEX)) {
            flow.emit(
                e.ordinal()
            );
        } else {
            flow.emit(e.name());
        }
    }

    @Override
    public T cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (klass.isInstance(object)) {
            return (T) object;
        }

        if (object instanceof Number) {
            T[] e = enums;
            if (e != null) {
                Number num = (Number) object;
                int index = num.intValue();
                if (index > -1 &&
                    index < e.length) {
                    return e[index];
                }
            }
            return null;
        }

        if (object instanceof CharSequence) {
            T[] e = enums;
            if (e != null) {
                CharSequence key =
                    (CharSequence) object;
                int l = key.length();
                if (l != 0) {
                    int i = Convert.toInt(
                        key, l, 10, -1
                    );
                    if (-1 < i && i < e.length) {
                        return e[i];
                    }

                    for (T em : e) {
                        if (key.equals(em.name())) {
                            return em;
                        }
                    }
                }
            }
            return null;
        }

        throw new IllegalStateException(
            object + " cannot be converted to " + klass
        );
    }
}
