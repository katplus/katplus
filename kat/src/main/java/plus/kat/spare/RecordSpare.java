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

import java.lang.invoke.MethodHandle;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static plus.kat.utils.Reflect.lookup;

/**
 * @author kraity
 * @since 0.0.2
 */
public class RecordSpare<T> extends Workman<T, Target> implements Worker<T> {

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

    @NotNull
    @Override
    public T apply(
        @NotNull Alias alias
    ) throws Crash {
        throw new Crash();
    }

    @NotNull
    @Override
    public T apply(
        @NotNull Alias alias,
        @NotNull Object... params
    ) throws Crash {
        try {
            return ctor.newInstance(params);
        } catch (Throwable e) {
            throw new Crash(e);
        }
    }

    @NotNull
    @Override
    public T apply(
        @NotNull Supplier supplier,
        @NotNull Map<?, ?> data
    ) throws Crash {
        return compose(
            supplier, new Object[width], data
        );
    }

    @NotNull
    @Override
    public T apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return compose(
            supplier, new Object[width], resultSet
        );
    }

    @Override
    public Target target(
        Object alias
    ) {
        return get(alias);
    }

    @Override
    public Target target(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return get(alias);
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
                        for (int i = 0; i < keys.length; i++) {
                            super.put(
                                keys[i], i == 0 ? item : item.clone()
                            );
                        }
                    }
                    if (!e1.export()) {
                        continue;
                    }
                }

                Handle<T> handle;
                Method method = klass.getMethod(
                    field.getName()
                );

                Expose e2 = method
                    .getAnnotation(
                        Expose.class
                    );
                if (e2 == null) {
                    handle = new Handle<>(
                        method, e1, supplier
                    );
                    getter(name, handle);
                } else if (e2.export()) {
                    handle = new Handle<>(
                        method, e2, supplier
                    );
                    String[] keys = e2.value();
                    if (keys.length == 0) {
                        getter(name, handle);
                    } else {
                        for (int i = 0; i < keys.length; i++) {
                            getter(
                                keys[i], i == 0 ? handle : handle.clone()
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

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Handle<K> extends Node<K> {

        final Class<?> klass;
        final MethodHandle getter;

        public Handle(
            Handle<?> handle
        ) {
            super(handle);
            this.klass = handle.klass;
            this.getter = handle.getter;
        }

        public Handle(
            Method method,
            Expose expose,
            Supplier supplier
        ) throws IllegalAccessException {
            super(expose);
            method.setAccessible(true);
            getter = lookup.unreflect(method);

            Class<?> clazz = method.getReturnType();
            if (clazz.isPrimitive()) {
                klass = Reflect.wrap(clazz);
                coder = Reflect.activate(expose, supplier);
            } else {
                klass = clazz;
                nullable = method.getAnnotation(NotNull.class) == null;
                unwrapped = method.getAnnotation(Unwrapped.class) != null;

                Format format = method
                    .getAnnotation(
                        Format.class
                    );
                if (format != null) {
                    coder = Reflect.activate(klass, format);
                } else {
                    coder = Reflect.activate(expose, supplier);
                }
            }
        }

        @Override
        public Object apply(
            @NotNull K it
        ) {
            try {
                return getter.invoke(it);
            } catch (Throwable e) {
                // Nothing
            }
            return null;
        }

        @Override
        public Object onApply(
            @NotNull Object it
        ) {
            try {
                return getter.invoke(it);
            } catch (Throwable e) {
                // Nothing
            }
            return null;
        }

        @Override
        public Handle<K> clone() {
            return new Handle<>(this);
        }
    }
}
