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
package plus.kat.utils;

import plus.kat.anno.Expose;
import plus.kat.anno.Format;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.spare.*;
import plus.kat.chain.*;
import plus.kat.entity.*;
import plus.kat.reflex.*;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author kraity
 * @since 0.0.2
 */
public class Reflect {
    /**
     * @since 0.0.2
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T apply(
        @NotNull Class<?> klass
    ) {
        try {
            Constructor<?> c = klass
                .getDeclaredConstructor();
            c.setAccessible(true);
            return (T) c.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @since 0.0.1
     */
    @Nullable
    public static Spare<?> lookup(
        @Nullable Type type,
        @NotNull Supplier supplier
    ) {
        if (type instanceof Class) {
            if (type == Object.class) {
                return null;
            }

            return supplier.lookup(
                (Class<?>) type
            );
        }

        if (type instanceof Space) {
            Space s = (Space) type;
            type = s.getType();
            if (type != null) {
                return lookup(
                    type, supplier
                );
            }
            return supplier.lookup(s);
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            return supplier.lookup(
                (Class<?>) p.getRawType()
            );
        }

        if (type instanceof WildcardType) {
            WildcardType w = (WildcardType) type;
            type = w.getUpperBounds()[0];
            if (type != Object.class) {
                return supplier.lookup(
                    (Class<?>) type
                );
            }
            Type[] bounds = w.getLowerBounds();
            if (bounds.length != 0) {
                type = bounds[0];
                if (type != Object.class) {
                    return supplier.lookup(
                        (Class<?>) type
                    );
                }
            }
            return null;
        }

        if (type instanceof TypeVariable) {
            TypeVariable<?> v = (TypeVariable<?>) type;
            Type[] bounds = v.getBounds();
            if (bounds.length != 0) {
                type = bounds[0];
                if (type != Object.class) {
                    return supplier.lookup(
                        (Class<?>) type
                    );
                }
            }
            return null;
        }

        if (type instanceof ArrayType) {
            return supplier.lookup(
                Object[].class
            );
        }

        if (type instanceof GenericArrayType) {
            return supplier.lookup(
                Object[].class
            );
        }

        return null;
    }

    /**
     * @since 0.0.2
     */
    @Nullable
    public static Coder<?> activate(
        @NotNull Class<?> klass,
        @Nullable Expose expose,
        @Nullable Format format,
        @NotNull Supplier supplier
    ) {
        if (format != null) {
            if (klass == Date.class) {
                return new DateSpare(format);
            } else if (klass == LocalDate.class) {
                return LocalDateSpare.of(format);
            }
        } else if (expose != null) {
            Class<?> with = expose.with();
            if (with != Coder.class) {
                return supplier.activate(with);
            }
        }

        return null;
    }
}
