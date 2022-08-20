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

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public class EnumSpare<K extends Enum<K>> extends Property<K> implements Serializable {

    private K[] enums;
    private final String space;

    public EnumSpare(
        @NotNull Class<K> klass,
        @NotNull Supplier supplier
    ) {
        this(
            klass.getAnnotation(Embed.class),
            klass, supplier, null
        );
    }

    public EnumSpare(
        @Nullable Embed embed,
        @NotNull Class<K> klass,
        @NotNull Supplier supplier,
        @Nullable Provider provider
    ) {
        super(klass, provider);
        try {
            Method values = klass
                .getMethod("values");
            values.setAccessible(true);

            enums = (K[]) values.invoke(null);
        } catch (Exception e) {
            // NOOP
        }

        space = supplier.register(
            embed, klass, this
        );
    }

    public EnumSpare(
        @NotNull Class<K> klass,
        @Nullable K[] enums,
        @Nullable Embed embed,
        @NotNull Supplier supplier
    ) {
        super(klass);
        this.enums = enums;
        this.space = supplier.register(
            embed, klass, this
        );
    }

    @Override
    public K apply() {
        K[] e = enums;
        if (e != null) {
            return e[0];
        }
        return null;
    }

    @Override
    public String getSpace() {
        return space;
    }

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
            K[] e = enums;
            if (e != null) {
                String key = (String) data;
                for (K em : e) {
                    if (key.equals(em.name())) {
                        return em;
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

        return null;
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

            for (K em : e) {
                if (alias.is(em.name())) {
                    return em;
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

            for (K em : e) {
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
    ) throws IOException {
        Enum<?> e = (Enum<?>) value;
        if (flow.isFlag(Flag.ENUM_AS_INDEX)) {
            flow.addInt(
                e.ordinal()
            );
        } else {
            if (flow.getJob() != Job.JSON) {
                flow.emit(e.name());
            } else {
                flow.addByte((byte) '"');
                flow.emit(e.name());
                flow.addByte((byte) '"');
            }
        }
    }
}
