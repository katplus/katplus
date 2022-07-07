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
package plus.kat.reflex;

import plus.kat.Supplier;
import plus.kat.anno.Expose;
import plus.kat.anno.Format;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.entity.*;
import plus.kat.spare.*;
import plus.kat.utils.KatMap;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ReflexParam extends KatMap.Entry<String, ReflexParam> {

    protected final int index;
    protected Coder<?> coder;
    protected final Type type;
    protected final Class<?> klass;

    public ReflexParam(
        @NotNull ReflexParam ref
    ) {
        super(0);
        this.index = ref.index;
        this.coder = ref.coder;
        this.type = ref.type;
        this.klass = ref.klass;
    }

    @SuppressWarnings("unchecked")
    public ReflexParam(
        int index,
        @Nullable Type type,
        @NotNull Class<?> klass,
        @Nullable Format format,
        @Nullable Expose expose,
        @NotNull Supplier supplier
    ) {
        super(0);
        this.index = index;
        this.type = type;
        this.klass = klass;

        if (format != null) {
            if (klass == Date.class) {
                coder = new DateSpare(format);
            } else if (klass == LocalDate.class) {
                coder = LocalDateSpare.of(format);
            }
        } else if (expose != null) {
            Class<?> with = expose.with();
            if (with != Coder.class) {
                coder = supplier.activate(
                    (Class<Coder<Object>>) with
                );
            }
        }
    }

    @Override
    protected ReflexParam clone() {
        return new ReflexParam(this);
    }
}
