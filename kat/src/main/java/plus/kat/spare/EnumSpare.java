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
import plus.kat.kernel.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public class EnumSpare<K extends Enum<K>> extends Property<K> {

    protected String[] spaces;
    private K[] enums;
    private final String space;

    public EnumSpare(
        @NotNull Class<K> klass,
        @NotNull Supplier supplier
    ) {
        this(
            klass.getAnnotation(Embed.class), klass, supplier
        );
    }

    public EnumSpare(
        @Nullable Embed embed,
        @NotNull Class<K> klass,
        @NotNull Supplier supplier
    ) {
        this(embed, null, klass, supplier);
    }

    public EnumSpare(
        @Nullable Embed embed,
        @Nullable K[] enums,
        @NotNull Class<K> klass,
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
            this.enums = (K[]) method.invoke(null);
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
    public K apply() {
        K[] e = enums;
        if (e != null &&
            e.length != 0) {
            return e[0];
        }
        return null;
    }

    @Override
    public K apply(
        @NotNull Type type
    ) {
        if (type == klass) {
            K[] e = enums;
            if (e != null &&
                e.length != 0) {
                return e[0];
            }
            throw new Collapse(
                "Failed to create"
            );
        }

        throw new Collapse(
            "Unable to create an instance of " + type
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
    public void embed(
        @NotNull Supplier supplier
    ) {
        supplier.embed(klass, this);
        if (spaces != null) {
            for (String space : spaces) {
                if (space.indexOf('.', 1) != -1) {
                    supplier.embed(space, this);
                }
            }
        }
    }

    @Override
    public K read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        K[] e = enums;
        if (e != null) {
            if (flag.isFlag(Flag.INDEX_AS_ENUM)) {
                int i = alias.toInt(-1);
                if (i >= 0 && i < e.length) {
                    return e[i];
                }
            }

            if (alias.isNotBlank()) {
                for (K em : e) {
                    if (alias.equals(em.name())) {
                        return em;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public K read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        K[] e = enums;
        if (e != null) {
            if (flag.isFlag(Flag.INDEX_AS_ENUM)) {
                int i = value.toInt(-1);
                if (i >= 0 && i < e.length) {
                    return e[i];
                }
            }

            if (value.isNotBlank()) {
                for (K em : e) {
                    if (value.equals(em.name())) {
                        return em;
                    }
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
            flow.emit(
                e.name()
            );
        }
    }

    @Override
    public K cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
            if (klass.isInstance(data)) {
                return (K) data;
            }

            if (data instanceof String) {
                K[] e = enums;
                if (e != null) {
                    String key = (String) data;
                    if (!key.isEmpty()) {
                        for (K em : e) {
                            if (key.equals(em.name())) {
                                return em;
                            }
                        }
                    }
                }
                return null;
            }

            if (data instanceof Chain) {
                K[] e = enums;
                if (e != null) {
                    Chain c = (Chain) data;
                    if (c.isNotBlank()) {
                        for (K em : e) {
                            if (c.equals(em.name())) {
                                return em;
                            }
                        }
                    }
                }
                return null;
            }

            if (data instanceof Number) {
                K[] e = enums;
                if (e != null) {
                    Number num = (Number) data;
                    int index = num.intValue();
                    if (index >= 0 &&
                        index < e.length) {
                        return e[index];
                    }
                }
                return null;
            }
        }
        return null;
    }
}
