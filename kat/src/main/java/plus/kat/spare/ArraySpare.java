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
import plus.kat.stream.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("rawtypes")
public class ArraySpare implements Spare<Object> {

    public static final ArraySpare
        INSTANCE = new ArraySpare(Object[].class);

    public static final Object[]
        EMPTY_ARRAY = new Object[0];

    protected final Class<?> klass;
    protected final Class<?> element;

    public ArraySpare(
        @NotNull Class<?> clazz
    ) {
        klass = clazz;
        element = clazz.getComponentType();
    }

    @Override
    public Space getSpace() {
        return Space.$A;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz.isAssignableFrom(klass);
    }

    @Override
    public Boolean getFlag() {
        return Boolean.FALSE;
    }

    @Override
    public Class<?> getType() {
        return klass;
    }

    @Override
    public Supplier getSupplier() {
        return Supplier.ins();
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        if (element == Object.class) {
            for (Object val : (Object[]) value) {
                chan.set(null, val);
            }
        } else {
            int l = Array.getLength(value);
            for (int i = 0; i < l; i++) {
                chan.set(
                    null, Array.get(value, i)
                );
            }
        }
    }

    @Override
    public Object apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) {
        Class<?> elem = element;
        if (elem == Object.class) {
            if (!spoiler.hasNext()) {
                return EMPTY_ARRAY;
            }
            Object[] data = new Object[]{
                spoiler.getValue()
            };
            while (spoiler.hasNext()) {
                Object[] copy = new Object[data.length + 1];
                System.arraycopy(
                    data, 0, copy, 0, data.length
                );
                copy[data.length] = spoiler.getValue();
                data = copy;
            }
            return data;
        } else {
            if (!spoiler.hasNext()) {
                return Array.newInstance(elem, 0);
            }

            Object data = null;
            Spare<?> spare = null;

            int size = 0;
            do {
                Object copy;
                if (size == 0) {
                    copy = Array.newInstance(elem, 1);
                } else {
                    copy = Array.newInstance(elem, size + 1);
                    //noinspection SuspiciousSystemArraycopy
                    System.arraycopy(
                        data, 0, copy, 0, size
                    );
                }

                Object val = spoiler.getValue();
                if (!elem.isInstance(val)) {
                    if (spare == null) {
                        spare = supplier.lookup(elem);
                    }
                    val = spare.cast(val, supplier);
                }
                Array.set(copy, size++, val);
                data = copy;
            } while (
                spoiler.hasNext()
            );
            return data;
        }
    }

    @Override
    public Object apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        Class<?> elem = element;
        ResultSetMetaData meta =
            resultSet.getMetaData();

        int count = meta.getColumnCount();
        if (elem == Object.class) {
            Object[] data = new Object[count];
            for (int i = 0; i < count; ) {
                data[i++] = resultSet.getObject(i);
            }
            return data;
        } else {
            Spare<?> spare = null;
            Object data = Array.newInstance(elem, count);
            for (int i = 0; i < count; ) {
                int k = i++;
                Object val = resultSet.getObject(i);
                if (!elem.isInstance(val)) {
                    if (spare == null) {
                        spare = supplier.lookup(elem);
                    }
                    val = spare.cast(val, supplier);
                }
                Array.set(data, k, val);
            }
            return data;
        }
    }

    @Override
    public Object cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data == null) {
            return null;
        }

        if (klass.isInstance(data)) {
            return data;
        }

        if (data instanceof Collection) {
            Class<?> elem = element;
            Collection col = (Collection) data;

            int size = col.size();
            if (elem == Object.class) {
                if (size == 0) {
                    return EMPTY_ARRAY;
                } else {
                    return col.toArray();
                }
            }

            Spare<?> spare = null;
            Object array = Array.newInstance(elem, size);
            int i = 0;
            for (Object val : col) {
                if (!elem.isInstance(val)) {
                    if (spare == null) {
                        spare = supplier.lookup(elem);
                    }
                    val = spare.cast(val, supplier);
                }
                Array.set(array, i++, val);
            }
            return array;
        }

        if (data instanceof Map) {
            Map map = (Map) data;
            Class<?> elem = element;

            int size = map.size();
            if (elem == Object.class) {
                if (size == 0) {
                    return EMPTY_ARRAY;
                }
                return map.values().toArray();
            }

            Spare<?> spare = null;
            Object array = Array.newInstance(elem, size);
            int i = 0;
            for (Object val : map.values()) {
                if (!elem.isInstance(val)) {
                    if (spare == null) {
                        spare = supplier.lookup(elem);
                    }
                    val = spare.cast(val, supplier);
                }
                Array.set(array, i++, val);
            }
            return array;
        }

        if (data instanceof CharSequence) {
            return Convert.toObject(
                this, (CharSequence) data, null, supplier
            );
        }

        if (data.getClass().isArray()) {
            Class<?> elem = element;
            int size = Array.getLength(data);

            if (elem == Object.class) {
                if (size == 0) {
                    return EMPTY_ARRAY;
                } else {
                    Object[] array = new Object[size];
                    for (int i = 0; i < size; i++) {
                        array[i] = Array.get(data, i);
                    }
                    return array;
                }
            }

            Spare<?> spare = null;
            Object array = Array.newInstance(elem, size);
            for (int i = 0; i < size; i++) {
                Object val = Array.get(data, i);
                if (!elem.isInstance(val)) {
                    if (spare == null) {
                        spare = supplier.lookup(elem);
                    }
                    val = spare.cast(val, supplier);
                }
                Array.set(array, i, val);
            }
            return array;
        }

        if (data instanceof Spoiler) {
            return apply(
                (Spoiler) supplier, supplier
            );
        }

        if (data instanceof ResultSet) {
            try {
                return apply(
                    supplier, (ResultSet) data
                );
            } catch (Exception e) {
                return null;
            }
        }

        Spoiler spoiler =
            supplier.flat(data);
        if (spoiler == null) {
            return null;
        }

        return apply(spoiler, supplier);
    }

    @Override
    public Builder<Object> getBuilder(
        @Nullable Type type
    ) {
        if (type == null) {
            Class<?> e = element;
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
                k = element;
            }
            if (k.isPrimitive()) {
                return new Builder0(k);
            } else {
                return new Builder1(k);
            }
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

        return null;
    }

    public static class Builder0 extends Builder<Object> {

        protected int size;
        protected int mark;

        protected int length;
        protected Object entity;

        protected Class<?> elem;
        protected Spare<?> spare;

        public Builder0(
            @NotNull Class<?> tag
        ) {
            elem = tag;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash {
            spare = supplier.lookup(elem);
            if (spare == null) {
                throw new Crash(
                    "Can't lookup the Spare of '" + elem + "'", false
                );
            }

            size = 0;
            mark = 1;
            entity = Array.newInstance(
                elem, length = 1
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
            value.setType(elem);
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
                elem, length = capacity
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

            Object ary = Array.newInstance(elem, size);
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
            @NotNull Type component
        ) {
            super(Object.class);
            type = component;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash {
            Type raw = type;
            if (raw instanceof Class) {
                if (raw != Object.class) {
                    elem = (Class<?>) raw;
                    spare = supplier.lookup(elem);
                }
            } else if (raw instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) raw;
                elem = (Class<?>) p.getRawType();
                spare = supplier.lookup(elem);
            } else {
                throw new Crash(
                    "Can't lookup the Spare of '" + raw + "'", false
                );
            }

            size = 0;
            mark = 1;
            entity = Array.newInstance(
                elem, length = 1
            );
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            Type type0 = type;
            Spare<?> spare0 = spare;

            Object data = null;
            if (spare0 != null) {
                value.setType(type0);
                data = spare0.read(
                    event, value
                );
            } else {
                spare0 = supplier.lookup(
                    type0, space
                );
                if (spare0 != null) {
                    value.setType(type0);
                    data = spare0.read(event, value);
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
            Type type = this.type;
            Spare<?> coder = spare;

            if (coder != null) {
                return coder.getBuilder(type);
            }

            coder = supplier.lookup(
                type, space
            );
            if (coder == null) {
                return null;
            }

            return coder.getBuilder(type);
        }
    }

    public static class Builder2 extends Builder<Object> {

        protected ArrayType tag;
        protected int index;
        protected Object[] entity;

        public Builder2(
            @NotNull ArrayType type
        ) {
            tag = type;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) {
            index = -1;
            entity = new Object[tag.size()];
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            if (++index < entity.length) {
                Type type = tag.getType(index);
                Spare<?> spare = supplier.lookup(
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

            Type type = tag.getType(index);
            Spare<?> spare = supplier.lookup(
                type, space
            );

            if (spare == null) {
                return null;
            }

            return spare.getBuilder(type);
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
