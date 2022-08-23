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
import plus.kat.utils.Reflect;

import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import static plus.kat.reflex.ReflectSpare.Task;

/**
 * @author kraity
 * @since 0.0.2
 */
public final class RecordSpare<T> extends Workman<T> {

    private int width;
    private Constructor<T> ctor;

    /**
     * @throws RunCrash If an error occurs in the build
     */
    public RecordSpare(
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        super(klass, supplier);
    }

    /**
     * @throws RunCrash If an error occurs in the build
     */
    public RecordSpare(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier,
        @Nullable Provider provider
    ) {
        super(embed, klass, supplier, provider);
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
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return super.apply(
            supplier, new Object[width], resultSet
        );
    }

    @Override
    public T apply(
        @NotNull Object result,
        @NotNull Supplier supplier
    ) {
        return super.apply(
            result, width, supplier
        );
    }

    @Override
    public Target target(
        Object alias
    ) {
        return (Target) get(alias);
    }

    @Override
    public Target target(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return (Target) get(
            alias.isEmpty() ? index : alias
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

    @Override
    @SuppressWarnings("unchecked")
    protected void initialize() {
        for (Field field : klass.getDeclaredFields()) {
            try {
                int mod = field.getModifiers();
                if ((mod & Modifier.STATIC) != 0) {
                    continue;
                }

                Expose e1 = field
                    .getAnnotation(
                        Expose.class
                    );

                Coder<?> coder;
                Class<?> type = field.getType();
                if (type.isPrimitive()) {
                    type = Reflect.wrap(type);
                    coder = Reflect.activate(e1, supplier);
                } else {
                    Format f1 = field
                        .getAnnotation(
                            Format.class
                        );

                    if (f1 != null) {
                        coder = Reflect.activate(type, f1);
                    } else {
                        coder = Reflect.activate(e1, supplier);
                    }
                }

                Item item = new Item(
                    width++, type,
                    field.getGenericType(), coder
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
                    if (!e1.export()) {
                        continue;
                    }
                }

                Task<T> node;
                Method method = klass.getMethod(
                    field.getName()
                );

                Expose e2 = method
                    .getAnnotation(
                        Expose.class
                    );
                if (e2 == null) {
                    node = new Task<>(
                        method, e1, supplier
                    );
                    setup(name, node);
                } else if (e2.export()) {
                    node = new Task<>(
                        method, e2, supplier
                    );
                    String[] keys = e2.value();
                    if (keys.length == 0) {
                        setup(name, node);
                    } else {
                        for (int i = 0; i < keys.length; i++) {
                            setup(
                                keys[i], i == 0 ? node : new Task<>(node)
                            );
                        }
                    }
                }
            } catch (Exception e) {
                throw new RunCrash(e);
            }
        }

        Constructor<T> b = null;
        for (Constructor<?> c : klass.getDeclaredConstructors()) {
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
            throw new RunCrash(
                "Unexpectedly, the Constructor of '" + klass + "' is null"
            );
        }

        if (width == b.getParameterCount()) {
            ctor = b;
            b.setAccessible(true);
        } else {
            throw new RunCrash(
                "Unexpectedly, the number of actual and formal parameters differ"
            );
        }
    }
}
