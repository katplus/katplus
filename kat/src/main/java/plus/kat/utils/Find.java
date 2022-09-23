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

import plus.kat.chain.*;
import plus.kat.reflex.*;

import java.lang.reflect.*;
import java.beans.Introspector;

/**
 * @author kraity
 * @since 0.0.4
 */
@SuppressWarnings("deprecation")
public final class Find {
    /**
     * Returns a wrapper class of primitive type
     *
     * <pre>{@code
     *  Class cls = Find.out(int.class); // Integer.class
     *  Class cls = Find.out(boolean.class); // Boolean.class
     * }</pre>
     *
     * @return {@link Class} or {@code itself}
     */
    @NotNull
    public static Class<?> kind(
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
     * Returns the default value of primitive type
     *
     * <pre>{@code
     *  Object obj = Find.value(int.class); // 0
     *  Object obj = Find.value(boolean.class); // false
     * }</pre>
     *
     * @return {@link Object} or {@code null}
     */
    @Nullable
    public static Object value(
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
        if (type == char.class) {
            return (char) 0;
        }
        return null;
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
     * @see Introspector#decapitalize(String)
     */
    @Nullable
    public static String name(
        @NotNull Method method
    ) {
        String name = method.getName();
        int i = 1, len = name.length();

        // setXXX
        char ch = name.charAt(0);
        if (ch == 's') {
            if (method.getParameterCount()
                == 0 || len < 4 ||
                name.charAt(i++) != 'e' ||
                name.charAt(i++) != 't') {
                return null;
            }
        }

        // getXXX
        else if (ch == 'g') {
            if (method.getParameterCount()
                != 0 || len < 4 ||
                name.charAt(i++) != 'e' ||
                name.charAt(i++) != 't') {
                return null;
            }
        }

        // isXXX
        else if (ch == 'i') {
            if (method.getParameterCount()
                != 0 || len < 3 ||
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

        if (i == len) {
            return String.valueOf(
                (char) (c1 + 0x20)
            );
        }

        char c2 = name.charAt(i);
        if (c2 < 'A' || 'Z' < c2) {
            c1 += 0x20;
        }

        byte[] it = new byte[len - i + 1];
        it[0] = (byte) c1;
        name.getBytes(
            i, len, it, 1
        );

        return new String(it, 0, 0, it.length);
    }

    /**
     * Returns the class corresponding to the type
     *
     * @return {@link Class} or {@code null}
     * @since 0.0.4
     */
    @Nullable
    public static Class<?> clazz(
        @Nullable Type type
    ) {
        if (type == null) {
            return null;
        }

        if (type instanceof Class) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            return clazz(
                p.getRawType()
            );
        }

        if (type instanceof Space) {
            Space s = (Space) type;
            return clazz(
                s.getType()
            );
        }

        if (type instanceof TypeVariable) {
            TypeVariable<?> v = (TypeVariable<?>) type;
            return clazz(
                v.getBounds()[0]
            );
        }

        if (type instanceof WildcardType) {
            WildcardType w = (WildcardType) type;
            type = w.getUpperBounds()[0];
            if (type == Object.class) {
                Type[] bounds = w.getLowerBounds();
                if (bounds.length != 0) {
                    type = bounds[0];
                }
            }
            return clazz(type);
        }

        if (type instanceof ArrayType) {
            return Object[].class;
        }

        if (type instanceof GenericArrayType) {
            GenericArrayType g = (GenericArrayType) type;
            Class<?> cls = clazz(
                g.getGenericComponentType()
            );
            if (cls == null ||
                cls == Object.class) {
                return Object[].class;
            } else {
                return Array.newInstance(cls, 0).getClass();
            }
        }

        return null;
    }
}
