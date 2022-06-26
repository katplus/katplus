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

import plus.kat.entity.Coder;
import plus.kat.entity.Getter;
import plus.kat.entity.Setter;
import plus.kat.spare.DateSpare;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ReflexMethod<K> implements Setter<K, Object>, Getter<K, Object> {

    private final Method method;
    private Coder<?> coder;
    private final Type type;
    private final Class<?> klass;

    /**
     * @throws NullPointerException If the parameter length of {@code method} is greater than 1
     */
    @SuppressWarnings("unchecked")
    public ReflexMethod(
        @NotNull Method method,
        @NotNull Expose expose,
        @NotNull Supplier supplier
    ) {
        this.method = method;
        switch (method.getParameterCount()) {
            case 0: {
                this.klass = method.getReturnType();
                this.type = klass;
                break;
            }
            case 1: {
                this.klass = method.getParameterTypes()[0];
                this.type = method.getGenericParameterTypes()[0];
                break;
            }
            default: {
                throw new NullPointerException(
                    "Unexpectedly the parameter length of '" + method.getName() + "' is greater than '1'"
                );
            }
        }

        Format format = method
            .getAnnotation(Format.class);
        if (format != null) {
            if (klass == Date.class) {
                coder = new DateSpare(format);
            }
        } else {
            Class<?> with = expose.with();
            if (with != Coder.class) {
                coder = supplier.activate(
                    (Class<Coder<K>>) with
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
            return method.invoke(it);
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
            return method.invoke(it);
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
        try {
            method.invoke(it, val);
        } catch (Exception e) {
            // nothing
        }
    }

    @Override
    public void onAccept(
        @NotNull K it,
        @Nullable Object val
    ) {
        try {
            method.invoke(it, val);
        } catch (Exception e) {
            // nothing
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
}
