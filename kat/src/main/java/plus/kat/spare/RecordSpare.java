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
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;

import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author kraity
 * @since 0.0.2
 */
public class RecordSpare<T> extends Workman<T> {

    private int width;
    private Constructor<T> ctor;

    public RecordSpare(
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        super(klass, supplier);
    }

    public RecordSpare(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        super(embed, klass, supplier);
    }

    @Override
    protected void initialize() {
        onFields(
            klass.getDeclaredFields()
        );
        onConstructors(
            klass.getDeclaredConstructors()
        );
    }

    @Override
    public T apply(
        @NotNull Alias alias
    ) throws Crash {
        throw new Crash(
            "Unsupported method"
        );
    }

    @Override
    public T apply(
        @NotNull Alias alias,
        @NotNull Object... params
    ) throws Crash {
        Constructor<T> b = ctor;
        if (ctor == null) {
            throw new Crash(
                "Not supported"
            );
        }
        try {
            return b.newInstance(params);
        } catch (Throwable e) {
            throw new Crash(
                "Failed to create", e
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
            return apply(
                Alias.EMPTY, group
            );
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
            return apply(
                Alias.EMPTY, group
            );
        } catch (SQLException e) {
            throw e;
        } catch (Throwable e) {
            throw new SQLCrash(
                "Error creating " + getType(), e
            );
        }
    }

    @Override
    public Target tag(
        Object alias
    ) {
        return (Target) getOrDefault(alias, null);
    }

    @Override
    public Target tag(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return (Target) getOrDefault(
            alias.isEmpty() ? index : alias, null
        );
    }

    @Override
    public Builder<T> getBuilder(
        @Nullable Type type
    ) {
        return new Builder1<>(
            this, new Object[width]
        );
    }

    protected void onFields(
        @NotNull Field[] fields
    ) {
        for (Field field : fields) {
            try {
                int mod = field.getModifiers();
                if ((mod & Modifier.STATIC) != 0) {
                    continue;
                }

                Expose e1 = field
                    .getAnnotation(Expose.class);

                Item item = new Item(width++);
                item.setField(field);
                item.setCoder(
                    supplier.assign(e1, item)
                );

                String name = field.getName();
                if (e1 == null) {
                    super.put(
                        name, item
                    );
                } else {
                    String[] keys = e1.value();
                    if (keys.length == 0) {
                        super.put(
                            name, item
                        );
                    } else {
                        name = keys[0];
                        for (String key : keys) {
                            super.put(
                                key, item
                            );
                        }
                    }
                    if ((e1.mode() &
                        Expose.HIDDEN) != 0) {
                        continue;
                    }
                }

                Edge<T> node;
                Method method = klass.getMethod(
                    field.getName()
                );

                Expose e2 = method
                    .getAnnotation(Expose.class);

                if (e2 == null) {
                    node = new Edge<>(
                        e1, method, supplier
                    );
                    display(
                        name, node
                    );
                } else if ((e2.mode() &
                    Expose.HIDDEN) == 0) {
                    node = new Edge<>(
                        e2, method, supplier
                    );
                    String[] keys = e2.value();
                    if (keys.length == 0) {
                        display(
                            name, node
                        );
                    } else {
                        for (int i = 0; i < keys.length; i++) {
                            display(
                                keys[i], i == 0 ? node : new Edge<>(node)
                            );
                        }
                    }
                }
            } catch (Exception e) {
                throw new Collapse(e);
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
            throw new Collapse(
                "Unexpectedly, the Constructor of '" + klass + "' is null"
            );
        }

        if (width == b.getParameterCount()) {
            ctor = b;
            if (!b.isAccessible()) {
                b.setAccessible(true);
            }
        } else {
            throw new Collapse(
                "Unexpectedly, the number of actual and formal parameters differ"
            );
        }
    }
}
