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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.*;
import java.lang.reflect.*;

import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public class ArraySpare extends BeanSpare<Object> {

    public static final ArraySpare
        INSTANCE = new ArraySpare(
        Object[].class, Supplier.ins()
    );

    public static final Object[]
        EMPTY_ARRAY = new Object[0];

    protected Object EMPTY;
    protected final Class<?> elem;

    public ArraySpare(
        @NotNull Class<?> clazz,
        @NotNull Context context
    ) {
        super((Class<Object>) clazz, context);
        if ((elem = clazz.getComponentType()) == null) {
            throw new IllegalStateException(
                "Specified `" + clazz + "` is not an array type"
            );
        }
    }

    @Override
    public Object apply() {
        Object array = EMPTY;
        if (array != null) {
            return array;
        }

        Class<?> e = elem;
        if (e == Object.class) {
            return EMPTY = EMPTY_ARRAY;
        } else if (e == byte[].class) {
            return EMPTY = EMPTY_BYTES;
        } else if (e == char[].class) {
            return EMPTY = EMPTY_CHARS;
        } else {
            return EMPTY = Array.newInstance(e, 0);
        }
    }

    @Override
    public String getSpace() {
        return "Array";
    }

    @Override
    public Boolean getScope() {
        return Boolean.FALSE;
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.BRACKET;
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        if (value instanceof Object[]) {
            for (Object val : (Object[]) value) {
                chan.set(
                    null, val
                );
            }
        } else {
            int size = Array.getLength(value);
            for (int i = 0; i < size; i++) {
                chan.set(
                    null, Array.get(value, i)
                );
            }
        }
    }

    @Override
    public Factory getFactory(
        @Nullable Type type
    ) {
        if (type == null) {
            Class<?> e = elem;
            if (e.isPrimitive()) {
                return new Builder0(e);
            } else {
                return new Builder1(e);
            }
        }

        if (type instanceof Class) {
            Class<?> k = (Class<?>) type;
            k = k.getComponentType();
            if (k == null) {
                k = elem;
            }
            if (k.isPrimitive()) {
                return new Builder0(k);
            } else {
                return new Builder1(k);
            }
        }

        if (type instanceof GenericArrayType) {
            GenericArrayType g = (GenericArrayType) type;
            return new Builder1(
                g.getGenericComponentType(), elem
            );
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            return new Builder2(
                p.getActualTypeArguments()
            );
        }

        return null;
    }

    public static class Builder0 extends Builder<Object> {

        protected int size;
        protected int mark;

        protected int length;
        protected Object bean;

        protected Class<?> elem;
        protected Spare<?> spare;

        public Builder0(
            Class<?> elem
        ) {
            this.elem = elem;
        }

        @Override
        public void onOpen() throws IOException {
            spare = context.assign(elem);
            if (spare != null) {
                size = 0;
                mark = 1;
                bean = Array.newInstance(
                    elem, length = 1
                );
            } else {
                throw new IOException(
                    "Can't lookup the Spare of '" + elem + "'"
                );
            }
        }

        @Override
        public void onEach(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            if (length == size) {
                enlarge();
            }
            Array.set(
                bean, size++, spare.read(this, value)
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

            Object make = Array.newInstance(
                elem, length = capacity
            );

            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(
                bean, 0, bean = make, 0, mark = size
            );
        }

        @Override
        public Object build() {
            if (length == size) {
                return bean;
            }

            Object data = Array
                .newInstance(elem, size);
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(
                bean, 0, data, 0, size
            );
            return data;
        }

        @Override
        public void onClose() {
            bean = null;
        }
    }

    public static class Builder1 extends Builder0 {

        protected Type visa;

        public Builder1(
            Class<?> elem
        ) {
            super(elem);
            this.visa = elem;
        }

        public Builder1(
            Type visa,
            Class<?> elem
        ) {
            super(elem);
            this.visa = visa;
        }

        @Override
        public void onOpen() {
            Type v = visa;
            Class<?> e = elem;
            if (e != v) {
                int i = 0;
                for (; v instanceof GenericArrayType; i++) {
                    v = ((GenericArrayType) v).getGenericComponentType();
                }
                v = holder.solve(v);
                if (v instanceof Class) {
                    if (i == 0) {
                        e = (Class<?>) v;
                    } else {
                        e = Array.newInstance(
                            (Class<?>) v, new int[i]
                        ).getClass();
                    }
                    visa = elem = e;
                } else {
                    if (i == 0) {
                        elem = e = classOf(visa = v);
                    } else {
                        elem = e = Array.newInstance(
                            classOf(v), new int[i]
                        ).getClass();
                    }
                }
            }

            if (e != Object.class) {
                spare = context.assign(e);
            }

            size = 0;
            mark = 1;
            bean = Array.newInstance(
                e, length = 1
            );
        }

        @Override
        public Spider onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Spare<?> spare0 = spare;
            if (spare0 == null) {
                spare0 = context.assign(elem, space);
                if (spare0 == null) {
                    throw new IOException(
                        "Not found the spare of " + space
                    );
                }
            }

            Factory child =
                spare0.getFactory(visa);

            if (child == null) {
                return null;
            }

            return child.init(
                this, context
            );
        }

        @Override
        public void onEach(
            @Nullable Object value
        ) throws IOException {
            if (length == size) {
                enlarge();
            }
            Array.set(
                bean, size++, value
            );
        }

        @Override
        public void onEach(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Spare<?> spare0 = spare;
            if (spare0 == null) {
                spare0 = context.assign(elem, space);
                if (spare0 == null) {
                    throw new IOException(
                        "Not found the spare of " + space
                    );
                }
            }

            if (length == size) {
                enlarge();
            }

            Array.set(
                bean, size++, spare0.read(this, value)
            );
        }

        @Override
        public void onClose() {
            bean = null;
        }
    }

    public static class Builder2 extends Builder<Object> {

        protected int index;
        protected Type[] elems;
        protected Object[] bean;

        public Builder2(
            Type[] visa
        ) {
            elems = visa;
        }

        @Override
        public void onOpen() {
            index = -1;
            bean = new Object[elems.length];
        }

        @Override
        public Spider onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Type[] types = elems;
            if (++index < types.length) {
                Type type = types[index];
                Spare<?> spare = context.assign(type);

                if (spare != null) {
                    Factory child =
                        spare.getFactory(type);

                    if (child == null) {
                        return null;
                    }

                    return child.init(
                        this, context
                    );
                } else {
                    throw new IOException(
                        "Not found the spare of " + space
                    );
                }
            } else {
                throw new IOException(
                    "The number of elements exceeds the range: " + types.length
                );
            }
        }

        @Override
        public void onEach(
            @Nullable Object value
        ) throws IOException {
            bean[index] = value;
        }

        @Override
        public void onEach(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Type[] types = elems;
            if (++index < types.length) {
                Spare<?> spare =
                    context.assign(
                        types[index]
                    );

                if (spare != null) {
                    bean[index] =
                        spare.read(
                            this, value
                        );
                } else {
                    throw new IOException(
                        "Not found the spare of " + space
                    );
                }
            } else {
                throw new IOException(
                    "The number of elements exceeds the range: " + types.length
                );
            }
        }

        @Override
        public Object[] build() {
            return bean;
        }

        @Override
        public void onClose() {
            bean = null;
        }
    }
}
