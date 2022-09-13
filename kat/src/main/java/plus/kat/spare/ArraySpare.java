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

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.reflex.*;
import plus.kat.utils.*;

import java.io.IOException;
import java.lang.reflect.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ArraySpare implements Spare<Object> {

    public static final ArraySpare
        INSTANCE = new ArraySpare();

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

    @Override
    public Boolean getFlag() {
        return Boolean.FALSE;
    }

    @Override
    public Class<Object> getType() {
        return Object.class;
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        int l = Array.getLength(value);
        for (int i = 0; i < l; i++) {
            chan.set(
                null, Array.get(value, i)
            );
        }
    }

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

        protected int size;
        protected int mark;

        protected int length;
        protected Object entity;

        protected Class<?> klass;
        protected Spare<?> spare;

        public Builder0(
            @NotNull Class<?> tag
        ) {
            klass = tag;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOException {
            spare = supplier.lookup(klass);
            if (spare == null) {
                throw new Crash(
                    "Can't lookup the Spare of '" + klass + "'", false
                );
            }

            size = 0;
            mark = 1;
            entity = Array.newInstance(
                klass, length = 1
            );
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            if (length == size) {
                enlarge();
            }
            value.setType(klass);
            Array.set(
                entity, size++, spare.read(
                    event, value
                )
            );
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            throw new UnexpectedCrash(
                "Unexpectedly, operation not supported"
            );
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
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
                entity, 0, make, 0, size
            );
            entity = make;
        }

        @Nullable
        @Override
        public Object getResult() {
            if (length == size) {
                return entity;
            }

            Object ary = Array.newInstance(klass, size);
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(
                entity, 0, ary, 0, size
            );
            return ary;
        }

        @Override
        public void onDestroy() {
            entity = null;
        }
    }

    public static class Builder1 extends Builder0 {

        protected Type type;

        public Builder1(
            @NotNull Type type
        ) {
            super(null);
            this.type = type;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOException {
            if (type instanceof Class) {
                klass = (Class<?>) type;
                if (type == Object.class) {
                    type = null;
                } else {
                    spare = supplier.lookup(klass);
                }
            } else if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) type;
                klass = (Class<?>) p.getRawType();
                spare = supplier.lookup(klass);
            } else {
                throw new Crash(
                    "Can't lookup the Spare of '" + type + "'", false
                );
            }

            size = 0;
            mark = 1;
            entity = Array.newInstance(
                klass, length = 1
            );
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            Object data = null;
            if (spare != null) {
                value.setType(type);
                data = spare.read(
                    event, value
                );
            } else {
                Spare<?> spare =
                    supplier.lookup(space);

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
                entity, size++, data
            );
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            if (length == size) {
                enlarge();
            }
            Array.set(
                entity, size++, child.getResult()
            );
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) {
            if (spare != null) {
                return spare.getBuilder(type);
            }

            Spare<?> spare =
                supplier.lookup(space);

            if (spare == null) {
                return null;
            }

            return spare.getBuilder(type);
        }
    }

    public static class Builder2 extends Builder<Object> {

        protected ArrayType types;
        protected int index;
        protected Object[] entity;

        public Builder2(
            @NotNull ArrayType tag
        ) {
            types = tag;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOException {
            index = -1;
            entity = new Object[types.size()];
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            if (++index < entity.length) {
                Type type = types
                    .getType(index);
                Spare<?> spare = lookup(
                    type, space
                );

                if (spare != null) {
                    value.setType(type);
                    entity[index] = spare.read(
                        event, value
                    );
                }
            }
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            entity[index] = child.getResult();
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) {
            if (++index >= entity.length) {
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
                return supplier.search(type);
            }
        }

        @Override
        public Object[] getResult() {
            return entity;
        }

        @Override
        public void onDestroy() {
            entity = null;
        }
    }
}
