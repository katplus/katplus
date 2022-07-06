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

import plus.kat.anno.Expose;
import plus.kat.anno.Format;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.spare.*;
import plus.kat.entity.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ReflexField<K> extends ReflexSketch.Node<K> implements Setter<K, Object>, Getter<K, Object> {

    private final Field field;
    private Coder<?> coder;
    private final Type type;
    private final Class<?> klass;
    private final boolean nullable;

    /**
     * @since 0.0.2
     */
    public ReflexField(
        @NotNull ReflexField<?> ref
    ) {
        this.field = ref.field;
        this.coder = ref.coder;
        this.type = ref.type;
        this.klass = ref.klass;
        this.nullable = ref.nullable;
    }

    /**
     * @since 0.0.2
     */
    public ReflexField(
        @NotNull Field field
    ) {
        this(-1, field);
    }

    /**
     * @since 0.0.2
     */
    public ReflexField(
        int hash, @NotNull Field field
    ) {
        super(hash);
        this.field = field;
        this.type = field.getGenericType();
        this.klass = field.getType();
        this.nullable = field.getAnnotation(NotNull.class) == null;
    }

    @SuppressWarnings("unchecked")
    public ReflexField(
        @NotNull Field field,
        @NotNull Expose expose,
        @NotNull Supplier supplier
    ) {
        this(expose.index(), field);
        Format format = field.getAnnotation(Format.class);
        if (format != null) {
            if (klass == Date.class) {
                coder = new DateSpare(format);
            } else if (klass == LocalDate.class) {
                coder = LocalDateSpare.of(format);
            }
        } else {
            Class<?> with = expose.with();
            if (with != Coder.class) {
                coder = supplier.activate(
                    (Class<Coder<Object>>) with
                );
            }
        }
    }

    @Override
    @Nullable
    public Object apply(
        @NotNull K it
    ) {
        try {
            return field.get(it);
        } catch (Exception e) {
            // nothing
        }
        return null;
    }

    @Override
    @Nullable
    public Object onApply(
        @NotNull Object it
    ) {
        try {
            return field.get(it);
        } catch (Exception e) {
            // nothing
        }
        return null;
    }

    @Override
    public void accept(
        @NotNull K it,
        @Nullable Object val
    ) {
        if (val != null || nullable) {
            try {
                field.set(it, val);
            } catch (Exception e) {
                // nothing
            }
        }
    }

    @Override
    public void onAccept(
        @NotNull K it,
        @Nullable Object val
    ) {
        if (val != null || nullable) {
            try {
                field.set(it, val);
            } catch (Exception e) {
                // nothing
            }
        }
    }

    @Override
    public Coder<?> getCoder() {
        return coder;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Class<?> getKlass() {
        return klass;
    }

    /**
     * @since 0.0.2
     */
    @Override
    public ReflexField<K> clone() {
        return new ReflexField<>(this);
    }
}
