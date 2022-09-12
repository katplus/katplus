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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.reflex.*;

import java.lang.invoke.*;
import java.lang.reflect.*;

/**
 * @author kraity
 * @since 0.0.2
 */
public final class Reflect {

    public static final Object[]
        EMPTY = new Object[0];

    public static final MethodHandles.Lookup
        LOOKUP = MethodHandles.lookup();

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
            return (T) c.newInstance(EMPTY);
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
     * @since 0.0.3
     */
    @Nullable
    public static Object def(
        @NotNull Class<?> type
    ) {
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == float.class) {
            return 0F;
        }
        if (type == double.class) {
            return 0D;
        }
        if (type == void.class) {
            return null;
        }
        if (type == char.class) {
            return (char) 0;
        }

        throw new Collapse(
            "Not support type:" + type
        );
    }

    /**
     * @since 0.0.3
     */
    @NotNull
    public static Class<?> wrap(
        @NotNull Class<?> type
    ) {
        if (type == int.class) {
            return Integer.class;
        }
        if (type == long.class) {
            return Long.class;
        }
        if (type == boolean.class) {
            return Boolean.class;
        }
        if (type == byte.class) {
            return Byte.class;
        }
        if (type == short.class) {
            return Short.class;
        }
        if (type == float.class) {
            return Float.class;
        }
        if (type == double.class) {
            return Double.class;
        }
        if (type == void.class) {
            return Void.class;
        }
        if (type == char.class) {
            return Character.class;
        }
        return type;
    }

    /**
     * Convert name of method to alias
     *
     * <pre>{@code
     *   // getId() -> "id"
     *   // getId(Param) -> null
     *
     *   // getURL() -> "URL"
     *   // getUrl() -> "url"
     *
     *   // isEmpty() -> "empty"
     *   // isEmpty(Param) -> null
     *
     *   // setName() -> null
     *   // setName(Param) -> "name"
     *   // setName(Param, Param) -> null
     * }</pre>
     *
     * @see java.beans.Introspector#decapitalize(String)
     * @since 0.0.3
     */
    @Nullable
    @SuppressWarnings("deprecation")
    public static byte[] alias(
        @NotNull Method method
    ) {
        String name = method.getName();
        int i = 1, l = name.length();

        char ch = name.charAt(0);
        if (ch == 's') {
            if (method.getParameterCount() == 0 || l < 4 ||
                name.charAt(i++) != 'e' ||
                name.charAt(i++) != 't') {
                return null;
            }
        } else if (ch == 'g') {
            if (method.getParameterCount() != 0 || l < 4 ||
                name.charAt(i++) != 'e' ||
                name.charAt(i++) != 't') {
                return null;
            }
        } else if (ch == 'i') {
            if (method.getParameterCount() != 0 || l < 3 ||
                name.charAt(i++) != 's') {
                return null;
            }
            Class<?> cls = method.getReturnType();
            if (cls != boolean.class &&
                cls != Boolean.class) {
                return null;
            }
        } else {
            return null;
        }

        char c1 = name.charAt(i++);
        if (c1 < 'A' || 'Z' < c1) {
            return null;
        }

        byte[] alias;
        if (i == l) {
            alias = new byte[]{
                (byte) (c1 + 0x20)
            };
        } else {
            char c2 = name.charAt(i);
            if (c2 < 'A' || 'Z' < c2) {
                c1 += 0x20;
            }

            alias = new byte[l - i + 1];
            alias[0] = (byte) c1;
            name.getBytes(i, l, alias, 1);
        }
        return alias;
    }
}
