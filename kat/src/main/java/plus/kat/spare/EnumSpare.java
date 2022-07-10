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
import plus.kat.entity.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public class EnumSpare<K extends Enum<K>> implements Spare<Enum<K>> {

    private K[] enums;
    private final Class<K> klass;
    private final CharSequence space;

    public EnumSpare(
        @NotNull Class<K> klass,
        @Nullable Embed embed,
        @NotNull Supplier supplier
    ) {
        this.klass = klass;

        try {
            Method values = klass
                .getMethod("values");
            values.setAccessible(true);

            enums = (K[]) values.invoke(null);
        } catch (Exception e) {
            // NOOP
        }

        this.space = supplier.register(
            embed, klass, this
        );
    }

    public EnumSpare(
        @NotNull Class<K> klass,
        @Nullable K[] enums,
        @Nullable Embed embed,
        @NotNull Supplier supplier
    ) {
        this.klass = klass;
        this.enums = enums;
        this.space = supplier.register(
            embed, klass, this
        );
    }

    @Override
    public CharSequence getSpace() {
        return space;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return klass == clazz;
    }

    @Override
    public Boolean getFlag() {
        return null;
    }

    @Override
    public Class<K> getType() {
        return klass;
    }

    @Override
    public Builder<K> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @Nullable
    @Override
    public K cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data.getClass() == klass) {
            return (K) data;
        }

        if (data instanceof String) {
            if (enums != null) {
                String key = (String) data;
                for (K em : enums) {
                    if (key.equals(em.name())) {
                        return em;
                    }
                }
            }
            return null;
        }

        if (data instanceof Number) {
            if (enums != null) {
                Number num = (Number) data;
                int index = num.intValue();
                if (index >= 0 &&
                    index < enums.length) {
                    return enums[index];
                }
            }
            return null;
        }

        return null;
    }

    @Nullable
    @Override
    public K read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        if (enums != null) {
            if (flag.isFlag(Flag.INDEX_AS_ENUM)) {
                int i = alias.toInt(-1);
                if (i >= 0 && i < enums.length) {
                    return enums[i];
                }
            }

            for (K em : enums) {
                if (alias.is(em.name())) {
                    return em;
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public K read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        if (enums != null) {
            if (flag.isFlag(Flag.INDEX_AS_ENUM)) {
                int i = value.toInt(-1);
                if (i >= 0 && i < enums.length) {
                    return enums[i];
                }
            }

            for (K em : enums) {
                if (value.is(em.name())) {
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
    ) throws IOCrash {
        if (flow.isFlag(Flag.ENUM_AS_INDEX)) {
            flow.addInt(
                ((Enum<?>) value).ordinal()
            );
        } else {
            flow.addText(
                ((Enum<?>) value).name()
            );
        }
    }
}
