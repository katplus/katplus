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

import plus.kat.anno.*;

import plus.kat.*;
import plus.kat.crash.*;

import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author kraity
 * @since 0.0.2
 */
@SuppressWarnings("unchecked")
public class RecordSpare<T> extends AbstractSpare<T> {

    private int width;
    private Constructor<T> ctor;

    public RecordSpare(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        super(embed, klass, supplier);
        onFields(
            klass.getDeclaredFields()
        );
        onConstructors(
            klass.getDeclaredConstructors()
        );
    }

    @NotNull
    public T apply(
        @NotNull Object[] data
    ) {
        Constructor<T> con = ctor;
        if (con == null) {
            throw new Collapse(
                "Not supported"
            );
        }
        try {
            return con.newInstance(data);
        } catch (Throwable e) {
            throw new Collapse(
                "Failed to apply", e
            );
        }
    }

    @Override
    public T apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) throws Collapse {
        try {
            Object[] group = new Object[width];
            update(
                group, spoiler, supplier
            );
            return apply(group);
        } catch (Collapse e) {
            throw e;
        } catch (Throwable e) {
            throw new Collapse(
                "Error creating " + getType(), e
            );
        }
    }

    @Override
    public T apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        try {
            Object[] group = new Object[width];
            update(
                group, supplier, resultSet
            );
            return apply(group);
        } catch (SQLException e) {
            throw e;
        } catch (Throwable e) {
            throw new SQLCrash(
                "Error creating " + getType(), e
            );
        }
    }

    @Override
    public Builder<T> getBuilder(
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
            if ((mo & Modifier.STATIC) != 0) {
                continue;
            }

            Expose e1 = field
                .getAnnotation(Expose.class);

            Argument arg = new Argument(
                width++, e1, field, this
            );

            String[] keys = null;
            String name = field.getName();
            if (e1 == null) {
                setParameter(
                    name, arg
                );
            } else {
                String[] ks = e1.value();
                if (ks.length == 0) {
                    setParameter(
                        name, arg
                    );
                } else {
                    for (String key : (keys = ks)) {
                        setParameter(
                            key, arg
                        );
                    }
                }
                if ((e1.require() & Flag.INTERNAL) != 0) {
                    continue;
                }
            }

            Method method;
            try {
                method = klass.getMethod(
                    field.getName()
                );
            } catch (NoSuchMethodException e) {
                throw new FatalCrash(
                    "Can't find the `" + field.getName() + "` method", e
                );
            }

            Expose e2 = method
                .getAnnotation(Expose.class);

            Callable<T> accessor;
            if (e2 == null) {
                accessor = new Callable<>(
                    e1, method, this
                );
                if (keys == null) {
                    setWriter(
                        name, accessor
                    );
                } else {
                    for (String key : keys) {
                        setWriter(
                            key, accessor
                        );
                    }
                }
                continue;
            }

            if ((e2.require() & Flag.INTERNAL) == 0) {
                accessor = new Callable<>(
                    e2, method, this
                );
                keys = e2.value();
                if (keys.length == 0) {
                    setWriter(
                        name, accessor
                    );
                } else {
                    for (String key : keys) {
                        setWriter(
                            key, accessor
                        );
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void onConstructors(
        @NotNull Constructor<?>[] constructors
    ) {
        Constructor<T> b = null;
        for (Constructor<?> c : constructors) {
            if (b == null) {
                b = (Constructor<T>) c;
            } else {
                if (b.getParameterCount() <=
                    c.getParameterCount()) {
                    b = (Constructor<T>) c;
                }
            }
        }

        if (b == null) {
            throw new FatalCrash(
                "The Constructor of '" + klass + "' is null"
            );
        }

        if (width == b.getParameterCount()) {
            ctor = b;
            if (!b.isAccessible()) {
                b.setAccessible(true);
            }
        } else {
            throw new FatalCrash(
                "The number of actual and formal parameters differ"
            );
        }
    }
}
