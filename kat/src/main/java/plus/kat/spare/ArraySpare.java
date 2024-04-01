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

import static plus.kat.lang.Uniform.*;
import static plus.kat.spare.ClassSpare.*;
import static plus.kat.spare.Supplier.Vendor.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("unchecked")
public class ArraySpare extends BeanSpare<Object> {

    public static final ArraySpare
        INSTANCE = new ArraySpare(Object[].class, INS);

    protected Object empty;
    protected final Class<?> elem;

    public ArraySpare(
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super((Class<Object>) klass, context);
        if ((elem = klass.getComponentType()) == null) {
            throw new IllegalStateException(
                "Specified `" + klass + "` is not an array type"
            );
        }
    }

    @Override
    public Object apply() {
        Object array = empty;
        if (array != null) {
            return array;
        }

        Class<?> e = elem;
        if (e == byte[].class) {
            return empty = EMPTY_BYTES;
        } else if (e == char[].class) {
            return empty = EMPTY_CHARS;
        } else {
            return empty = Array.newInstance(e, 0);
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
            for (Object elem : (Object[]) value) {
                chan.set(null, elem);
            }
        } else {
            for (int i = 0, m = Array.getLength(value); i < m; i++) {
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
            return new Builder1(
                ((GenericArrayType) type).getGenericComponentType()
            );
        }

        if (type instanceof ParameterizedType) {
            return new Builder2(
                ((ParameterizedType) type).getActualTypeArguments()
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

        public Builder0(Class<?> mold) {
            elem = mold;
        }

        @Override
        public void onCreate()
            throws IOException {
            if ((spare = context.assign(elem)) == null) {
                throw new IOException(
                    "Not found the spare of " + elem
                );
            }
        }

        @Override
        public void onNext(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Object data = spare.read(this, value);
            if (length == size) {
                enlarge();
            }
            Array.set(bean, size++, data);
        }

        /**
         * capacity expansion
         */
        protected void enlarge() {
            Object data = bean;
            if (data == null) {
                mark = 1;
                bean = Array.newInstance(
                    elem, length = 1
                );
                return;
            }

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

            bean = Array.newInstance(
                elem, length = capacity
            );

            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(
                data, 0, bean, 0, mark = size
            );
        }

        @Override
        public Object build() {
            Object data = bean;
            if (data == null) {
                return bean = Array
                    .newInstance(elem, 0);
            }

            if (length == size) {
                return data;
            }

            Object make = Array
                .newInstance(elem, length = size);
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(data, 0, make, 0, size);
            return bean = make;
        }

        @Override
        public Type getType() {
            return elem;
        }

        @Override
        public void onDestroy() {
            bean = null;
            size = length = 0;
        }
    }

    public static class Builder1 extends Builder0 {

        protected int step;
        protected Type root;
        protected Factory next;

        public Builder1(
            Type mold
        ) {
            super(null);
            step = -2;
            root = mold;
        }

        private Builder1(
            Class<?> mold,
            Builder1 head
        ) {
            super(mold);
            root = head.root;
            step = head.step - 1;
        }

        public Builder1(
            Class<?> mold
        ) {
            super(mold);
            root = mold;
            step = mold.isArray() ||
                mold == Object.class ? -1 : -3;
        }

        @Override
        public void onCreate() {
            switch (step) {
                case -3: {
                    step = -1;
                    spare = context.assign(elem);
                    break;
                }
                case -2: {
                    int i = 0;
                    while (root instanceof GenericArrayType) {
                        i++;
                        root = ((GenericArrayType) root)
                            .getGenericComponentType();
                    }

                    if (root instanceof Class) {
                        elem = (Class<?>) root;
                    } else {
                        elem = classOf(
                            root = getType(root)
                        );
                    }

                    if ((step = i) == 0) {
                        if (elem != Object.class) {
                            spare = context.assign(root);
                        }
                    } else {
                        elem = Array.newInstance(elem, new int[i]).getClass();
                    }
                }
            }
        }

        @Override
        public Pipe onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Factory child = next;
            if (child == null) {
                switch (step) {
                    case -1: {
                        Class<?> e = elem
                            .getComponentType();
                        if (e != null) {
                            child = next = new Builder1(e);
                            break;
                        }
                    }
                    case +0: {
                        Spare<?> coder =
                            context.assign(root, space);
                        if (coder == null) {
                            throw new IOException(
                                "Not found the spare of " + root
                            );
                        }
                        child = coder.getFactory(root);
                        if (child != null) {
                            break;
                        } else {
                            return null;
                        }
                    }
                    case -2:
                    case -3: {
                        throw new IOException();
                    }
                    default: {
                        child = next = new Builder1(
                            elem.getComponentType(), this
                        );
                    }
                }
            }
            return child.attach(this);
        }

        @Override
        public void onNext(
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
        public void onNext(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Spare<?> coder = spare;
            if (coder == null) {
                coder = context.assign(elem, space);
                if (coder == null) {
                    throw new IOException(
                        "Not found the spare of " + elem
                    );
                }
            }

            Object data = coder.read(this, value);
            if (length == size) {
                enlarge();
            }
            Array.set(bean, size++, data);
        }

        @Override
        public Type getType() {
            return root;
        }
    }

    public static class Builder2 extends Builder<Object> {

        protected int index;
        protected Type[] elems;
        protected Object[] target;

        public Builder2(
            Type[] mold
        ) {
            elems = mold;
        }

        @Override
        public void onCreate() {
            index = -1;
            target = new Object[elems.length];
        }

        @Override
        public Pipe onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Type[] types = elems;
            if (++index < types.length) {
                Type type = types[index];
                Spare<?> spare = context.assign(type);

                if (spare != null) {
                    Factory member = spare.getFactory(type);
                    return member == null ? null : member.attach(this);
                }
                throw new IOException(
                    "Not found the spare of " + space
                );
            } else {
                throw new IOException(
                    "The number of elements exceeds the range: " + types.length
                );
            }
        }

        @Override
        public void onNext(
            @Nullable Object value
        ) throws IOException {
            target[index] = value;
        }

        @Override
        public void onNext(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Type[] types = elems;
            if (++index < types.length) {
                Type type = types[index];
                Spare<?> spare = context.assign(type);

                if (spare != null) {
                    target[index] = spare.read(this, value);
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
            return target;
        }

        @Override
        public Type getType() {
            return Object[].class;
        }

        @Override
        public void onDestroy() {
            target = null;
        }
    }
}
