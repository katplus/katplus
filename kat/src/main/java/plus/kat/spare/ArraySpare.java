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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.reflex.*;
import plus.kat.utils.Casting;
import plus.kat.utils.Reflect;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ArraySpare implements Spare<Object> {

    public static final ArraySpare
        INSTANCE = new ArraySpare();

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$A;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass.isArray();
    }

    @Nullable
    @Override
    public Object cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data.getClass().isArray()) {
            return data;
        }

        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, null, supplier
            );
        }

        return null;
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return Boolean.FALSE;
    }

    @NotNull
    @Override
    public Class<Object> getType() {
        return Object.class;
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOCrash {
        int l = Array.getLength(value);
        for (int i = 0; i < l; i++) {
            chan.set(
                null, Array.get(value, i)
            );
        }
    }

    @Nullable
    @Override
    public Builder<Object> getBuilder(
        @Nullable Type type
    ) {
        if (type instanceof Class) {
            Class<?> k = (Class<?>) type;
            k = k.getComponentType();
            if (k == null) {
                return null;
            }
            if (k.isPrimitive()) {
                return new Builder0(k);
            }
            return new Builder1(k);
        }

        if (type instanceof ArrayType) {
            return new Builder2(
                (ArrayType) type
            );
        }

        if (type instanceof GenericArrayType) {
            GenericArrayType g = (GenericArrayType) type;
            return new Builder1(
                g.getGenericComponentType()
            );
        }

        return new Builder1(Object.class);
    }

    public static class Builder0 extends Builder<Object> {

        protected Spare<?> v;
        protected Class<?> klass;
        protected Object list;
        protected int size, mark, length;

        public Builder0(
            @NotNull Class<?> klass
        ) {
            this.klass = klass;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOCrash {
            v = supplier.lookup(klass);
            if (v == null) {
                throw new Crash(
                    "Can't lookup the Spare of '" + klass + "'", false
                );
            }

            size = 0;
            mark = 1;
            list = Array.newInstance(
                klass, length = 1
            );
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
            if (length == size) {
                enlarge();
            }
            value.setType(klass);
            Array.set(
                list, size++, v.read(
                    event, value
                )
            );
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOCrash {
            throw new UnexpectedCrash(
                "Unexpectedly, operation not supported"
            );
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOCrash {
            throw new UnexpectedCrash(
                "Unexpectedly, operation not supported"
            );
        }

        /**
         * capacity expansion
         */
        protected void enlarge() {
            int capacity;
            if (Integer.MAX_VALUE - mark > size) {
                // fibonacci
                capacity = mark + size;
            } else {
                if (Integer.MAX_VALUE - 8 < size) {
                    throw new OutOfMemoryError();
                }
                capacity = size + 8;
            }

            mark = size;
            Object make = Array.newInstance(
                klass, length = capacity
            );

            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(
                list, 0, make, 0, size
            );
            list = make;
        }

        @Nullable
        @Override
        public Object getResult() {
            if (length == size) {
                return list;
            }

            Object ary = Array.newInstance(klass, size);
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(
                list, 0, ary, 0, size
            );
            return ary;
        }

        @Override
        public void onDestroy() {
            list = null;
            v = null;
            klass = null;
        }
    }

    public static class Builder1 extends Builder0 {

        protected Type type;

        public Builder1(
            @NotNull Type type
        ) {
            super(Object.class);
            this.type = type;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOCrash {
            if (type instanceof Class) {
                if (type == Object.class) {
                    type = null;
                } else {
                    klass = (Class<?>) type;
                    v = supplier.lookup(klass);
                }
            } else if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) type;
                klass = (Class<?>) p.getRawType();
                v = supplier.lookup(klass);
            } else {
                throw new Crash(
                    "Can't lookup the Spare of '" + type + "'", false
                );
            }

            size = 0;
            mark = 1;
            list = Array.newInstance(
                klass, length = 1
            );
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
            Object data = null;
            if (v != null) {
                data = v.read(
                    event, value
                );
            } else {
                Spare<?> spare = supplier
                    .lookup(space);
                if (spare != null) {
                    value.setType(type);
                    data = spare.read(
                        event, value
                    );
                }
            }

            if (length == size) {
                enlarge();
            }
            Array.set(
                list, size++, data
            );
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) {
            if (length == size) {
                enlarge();
            }
            Array.set(
                list, size++, child.getResult()
            );
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) {
            if (v != null) {
                return v.getBuilder(type);
            }

            Spare<?> spare = supplier
                .lookup(space);

            if (spare == null) {
                return null;
            }

            return spare.getBuilder(type);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            type = null;
        }
    }

    public static class Builder2 extends Builder<Object> {

        protected Object[] list;
        protected int index;
        protected ArrayType types;

        public Builder2(
            @NotNull ArrayType types
        ) {
            this.types = types;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOCrash {
            index = -1;
            list = new Object[types.size()];
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
            if (++index < list.length) {
                Type type = types
                    .getType(index);
                Spare<?> spare = lookup(
                    type, space
                );

                if (spare != null) {
                    value.setType(type);
                    list[index] = spare.read(
                        event, value
                    );
                }
            }
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) {
            list[index] = child.getResult();
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) {
            if (++index >= list.length) {
                return null;
            }

            Type type = types
                .getType(index);
            Spare<?> spare = lookup(
                type, space
            );

            if (spare == null) {
                return null;
            }

            return spare.getBuilder(type);
        }

        /**
         * Lookup spare
         */
        protected Spare<?> lookup(
            @NotNull Type type,
            @NotNull Space space
        ) {
            if (type instanceof Class) {
                if (type == Object.class) {
                    return supplier.lookup(space);
                } else {
                    return supplier.lookup(
                        (Class<?>) type
                    );
                }
            } else {
                return Reflect.lookup(
                    type, supplier
                );
            }
        }

        @Override
        public Object[] getResult() {
            return list;
        }

        @Override
        public void onDestroy() {
            list = null;
            types = null;
        }
    }
}
