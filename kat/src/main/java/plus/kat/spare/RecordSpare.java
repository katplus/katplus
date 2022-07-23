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
public class RecordSpare<T> extends SuperSpare<T, Param> implements Spare<T> {

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

            Edge edge = new Edge();
            edge.setIndex(width++);
            edge.setCoder(handle.getCoder());
            edge.setType(field.getGenericType());
            edge.setKlass(field.getType());

            if (e2 != null) {
                String[] keys = e2.value();
                if (keys.length == 0) {
                    addGetter(name, handle);
                } else {
                    for (int i = 0; i < keys.length; i++) {
                        addGetter(
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
                    addGetter(name, handle);
                } else {
                    put(keys[0], edge);
                    addGetter(keys[0], handle);
                }
            } else {
                put(name, edge);
                addGetter(name, handle);
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
    public Builder<T> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0<>(this);
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Handle<K> extends Node<K> {

        final Method method;
        final Class<?> klass;
        final Coder<?> coder;

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
            super(expose == null
                ? -1 : expose.index()
            );
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

        @Override
        public Coder<?> getCoder() {
            return coder;
        }

        public Class<?> getKlass() {
            return klass;
        }

        @Override
        public Handle<K> clone() {
            return new Handle<>(this);
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    public static class Builder0<K> extends Builder<K> {

        private K entity;
        private Object[] data;

        private Param param;
        private RecordSpare<K> spare;

        public Builder0(
            @NotNull RecordSpare<K> spare
        ) {
            this.spare = spare;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOCrash {
            data = new Object[spare.width];
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOCrash {
            int i = param.getIndex();
            param = null;
            data[i] = child.getResult();
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
            Param param = spare.get(alias);
            if (param != null) {
                // specified coder
                int i = param.getIndex();
                Coder<?> coder = param.getCoder();

                if (coder != null) {
                    data[i] = coder.read(
                        flag, value
                    );
                } else {
                    // specified spare
                    coder = supplier.lookup(
                        param.getKlass()
                    );

                    // skip if null
                    if (coder != null) {
                        data[i] = coder.read(
                            flag, value
                        );
                    }
                }
            }
        }

        @Nullable
        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOCrash {
            param = spare.get(alias);
            if (param == null) {
                return null;
            }

            // specified coder
            Coder<?> coder = param.getCoder();

            // skip if null
            if (coder == null) {
                // specified spare
                coder = supplier.lookup(
                    param.getKlass()
                );

                // skip if null
                if (coder == null) {
                    return null;
                }
            }

            return coder.getBuilder(
                param.getType()
            );
        }

        @Nullable
        @Override
        public K getResult() {
            if (entity == null) {
                try {
                    entity = spare.ctor
                        .newInstance(data);
                } catch (Exception e) {
                    return null;
                }
            }
            return entity;
        }

        @Override
        public void onDestroy() {
            data = null;
            param = null;
            spare = null;
        }
    }
}
