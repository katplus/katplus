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

import plus.kat.actor.*;

import java.lang.reflect.*;

import static plus.kat.spare.ArraySpare.*;
import static plus.kat.spare.ReflectSpare.*;

/**
 * @author kraity
 * @since 0.0.2
 */
@SuppressWarnings("unchecked")
public class RecordSpare<T> extends BeanSpare<T> {

    private int width;
    private Constructor<T> builder;

    public RecordSpare(
        @Nilable String space,
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super(
            space, (Class<T>) klass, context
        );
        onFields(
            klass.getDeclaredFields()
        );
        onConstructors(
            klass.getDeclaredConstructors()
        );
    }

    @NotNull
    public T apply(
        @Nullable Object[] data
    ) {
        Constructor<T> maker = builder;
        if (maker == null) {
            throw new IllegalStateException(
                "Not supported"
            );
        }

        try {
            return maker.newInstance(
                data != null ? data : EMPTY_ARRAY
            );
        } catch (Throwable e) {
            throw new IllegalStateException(
                "Failed to apply", e
            );
        }
    }

    @Override
    public Factory getFactory(
        @Nullable Type type
    ) {
        return new Builder1<>(
            type, new Object[width], this
        );
    }

    protected void onFields(
        @NotNull Field[] fields
    ) {
        for (Field field : fields) {
            int mo = field.getModifiers();
            if ((mo & Modifier.STATIC) != 0 ||
                (mo & Modifier.TRANSIENT) != 0) {
                continue;
            }

            Magic m1 = field.getAnnotation(Magic.class);
            ParamMember arg
                = new ParamMember(
                width++, field, m1, context
            );

            String[] keys = null;
            String name = field.getName();

            if (m1 == null) {
                setParameter(
                    name, arg
                );
            } else {
                String[] ks = m1.value();
                if (ks.length == 0) {
                    setParameter(
                        name, arg
                    );
                } else {
                    for (String key : (keys = ks)) {
                        if (!addParameter(key, arg)) {
                            throw new IllegalStateException(
                                "Parameter for " + key + " has been setup"
                            );
                        }
                    }
                }
            }

            Method method;
            try {
                method = klass.getMethod(
                    field.getName()
                );
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(
                    "Can't find the `" + field.getName() + "` method", e
                );
            }

            Class<?>[] params = method.getParameterTypes();
            if (params.length > 1) {
                continue;
            }

            Magic m2 = method.getAnnotation(Magic.class);
            Member member;
            if (m2 == null) {
                member = new MethodMember(
                    m1, method, params, context
                );
                if (keys == null) {
                    setWriter(
                        name, member
                    );
                } else {
                    for (String key : keys) {
                        setWriter(
                            key, member
                        );
                    }
                }
            } else {
                member = new MethodMember(
                    m2, method, params, context
                );
                keys = m2.value();
                if (keys.length == 0) {
                    setWriter(
                        name, member
                    );
                } else {
                    for (String key : keys) {
                        setWriter(
                            key, member
                        );
                    }
                }
            }
        }
    }

    protected void onConstructors(
        @NotNull Constructor<?>[] cs
    ) {
        Constructor<?> buffer,
            latest = cs[0];
        Class<?>[] bufferType,
            latestType = latest.getParameterTypes();
        for (int i = 1; i < cs.length; i++) {
            buffer = cs[i];
            bufferType = buffer.getParameterTypes();
            if (latestType.length <=
                bufferType.length) {
                latest = buffer;
                latestType = bufferType;
            }
        }

        if (width == latestType.length) {
            builder = (Constructor<T>) latest;
            if (!latest.isAccessible()) {
                latest.setAccessible(true);
            }
        } else {
            throw new IllegalArgumentException(
                "The number of actual and formal arguments differ"
            );
        }
    }
}
