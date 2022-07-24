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

/**
 * @author kraity
 * @since 0.0.2
 */
public class RecordSpare<T> extends SuperSpare<T, Param> implements Sketch<T> {

    private int width;
    private Constructor<T> ctor;

    public RecordSpare(
        @Nullable Embed embed,
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        super(embed, klass, supplier);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initialize() {
        for (Field field : klass.getDeclaredFields()) {
            // its modifier
            int mod = field.getModifiers();

            // check its modifier
            if ((mod & Modifier.STATIC) != 0) {
                continue;
            }

            Expose e1, e2;
            e1 = field.getAnnotation(
                Expose.class
            );

            Handle<T> handle;
            String name = field.getName();
            try {
                Method method = klass.getMethod(name);
                e2 = method.getAnnotation(Expose.class);
                handle = new Handle<>(
                    method, e2 == null ? e1 : e2, supplier
                );
            } catch (Exception e) {
                continue;
            }

            Edge edge = new Edge(width++);
            edge.setCoder(handle.getCoder());
            edge.setType(field.getGenericType());
            edge.setKlass(field.getType());

            if (e2 != null) {
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
                if (e1 == null) {
                    put(name, edge);
                } else {
                    keys = e1.value();
                    if (keys.length == 0) {
                        put(name, edge);
                    } else {
                        put(keys[0], edge);
                    }
                }
            } else if (e1 != null) {
                String[] keys = e1.value();
                if (keys.length == 0) {
                    put(name, edge);
                    getter(name, handle);
                } else {
                    put(keys[0], edge);
                    getter(keys[0], handle);
                }
            } else {
                put(name, edge);
                getter(name, handle);
            }
        }

        for (Constructor<?> c : klass.getDeclaredConstructors()) {
            if (ctor == null) {
                ctor = (Constructor<T>) c;
            } else {
                if (ctor.getParameterCount() <
                    c.getParameterCount()) {
                    ctor = (Constructor<T>) c;
                }
            }
        }

        if (width == ctor.getParameterCount()) {
            ctor.setAccessible(true);
        } else {
            throw new RunCrash();
        }
    }

    @Override
    public T apply(
        @NotNull Alias alias
    ) throws Crash {
        return null;
    }

    @Override
    public T apply(
        @NotNull Alias alias,
        @NotNull Object... params
    ) throws Crash {
        try {
            return ctor.newInstance(params);
        } catch (Exception e) {
            throw new Crash(e);
        }
    }

    @Override
    public Param param(
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

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Handle<K> extends Node<K> {

        final Method method;
        final Class<?> klass;

        public Handle(
            Handle<?> handle
        ) {
            this.klass = handle.klass;
            this.coder = handle.coder;
            this.method = handle.method;
            this.nullable = handle.nullable;
        }

        public Handle(
            Method method,
            Expose expose,
            Supplier supplier
        ) {
            super(expose);
            klass = method.getReturnType();
            nullable = method.getAnnotation(NotNull.class) == null;

            this.method = method;
            method.setAccessible(true);

            Format format = method
                .getAnnotation(Format.class);
            coder = Reflect.activate(
                klass, expose, format, supplier
            );
        }

        @Override
        public Object apply(
            @NotNull K it
        ) {
            try {
                return method.invoke(it);
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
                return method.invoke(it);
            } catch (Throwable e) {
                // Nothing
            }
            return null;
        }

        public Class<?> getKlass() {
            return klass;
        }

        @Override
        public Handle<K> clone() {
            return new Handle<>(this);
        }
    }
}
