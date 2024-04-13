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

import plus.kat.*;
import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.IOException;
import java.lang.reflect.*;

/**
 * @author kraity
 * @since 0.0.6
 */
@SuppressWarnings("rawtypes")
public class ClassSpare extends BaseSpare<Class> {

    public static final ClassSpare
        INSTANCE = new ClassSpare();

    public ClassSpare() {
        super(Class.class);
    }

    @Override
    public String getSpace() {
        return "Class";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public Class read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (value.isClass()) {
            ClassLoader cl = null;
            try {
                cl = Thread.currentThread()
                    .getContextClassLoader();
            } catch (Throwable e) {
                // Cannot access thread ClassLoader
            }

            if (cl == null) {
                try {
                    cl = flag.getClass().getClassLoader();
                } catch (Throwable e) {
                    // Cannot access caller ClassLoader
                }

                if (cl == null) {
                    try {
                        cl = ClassLoader.getSystemClassLoader();
                    } catch (Throwable e) {
                        // Cannot access system ClassLoader
                    }
                }
            }

            try {
                return Class.forName(
                    value.toString(), false, cl
                );
            } catch (ClassNotFoundException e) {
                throw new IOException(
                    "Failed to resolve to class", e
                );
            }
        }

        if (value.isNothing()) {
            return null;
        }

        throw new IOException(
            "Received `" + value + "` is " +
                "not a secure fully qualified name"
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            ((Class) value).getName()
        );
    }

    @Nullable
    public static Type typeOf(
        @Nullable Type type
    ) {
        if (type instanceof Klass) {
            type = ((ParameterizedType)
                type.getClass()
                    .getGenericSuperclass())
                .getActualTypeArguments()[0];
        }
        return type;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> Class<T> classOf(
        @Nullable Type type
    ) {
        type = typeOf(type);
        if (type == null) {
            return null;
        }

        if (type instanceof Class) {
            return (Class<T>) type;
        }

        if (type instanceof ParameterizedType) {
            return classOf(
                ((ParameterizedType) type).getRawType()
            );
        }

        if (type instanceof TypeVariable) {
            return null;
        }

        if (type instanceof WildcardType) {
            return classOf(
                ((WildcardType) type).getUpperBounds()[0]
            );
        }

        if (type instanceof GenericArrayType) {
            GenericArrayType g = (GenericArrayType) type;
            Class<?> cls = classOf(
                g.getGenericComponentType()
            );
            if (cls != null) {
                if (cls == Object.class) {
                    cls = Object[].class;
                } else if (cls == String.class) {
                    cls = String[].class;
                } else if (cls.isPrimitive()) {
                    if (cls == int.class) {
                        cls = int[].class;
                    } else if (cls == long.class) {
                        cls = long[].class;
                    } else if (cls == float.class) {
                        cls = float[].class;
                    } else if (cls == double.class) {
                        cls = double[].class;
                    } else if (cls == byte.class) {
                        cls = byte[].class;
                    } else if (cls == short.class) {
                        cls = short[].class;
                    } else if (cls == char.class) {
                        cls = char[].class;
                    } else if (cls == boolean.class) {
                        cls = boolean[].class;
                    } else {
                        return null;
                    }
                } else {
                    cls = Array.newInstance(cls, 0).getClass();
                }
                return (Class<T>) cls;
            }
        }

        return null;
    }
}
