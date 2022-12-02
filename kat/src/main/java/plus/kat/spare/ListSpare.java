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

import java.io.IOException;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.stream.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ListSpare extends Property<List> {

    public static final ListSpare
        INSTANCE = new ListSpare(ArrayList.class);

    public ListSpare(
        @NotNull Class<?> klass
    ) {
        super((Class<List>) klass);
    }

    @Override
    public List apply() {
        return apply(klass);
    }

    @Override
    public String getSpace() {
        return "L";
    }

    @Override
    public Boolean getFlag() {
        return Boolean.FALSE;
    }

    @Override
    public List read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (flag.isFlag(Flag.VALUE_AS_BEAN)) {
            Algo algo = Algo.of(value);
            if (algo == null) {
                return null;
            }
            return solve(
                algo, new Event<List>(value).with(flag)
            );
        }
        return null;
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        Iterable<?> val =
            (Iterable<?>) value;

        for (Object v : val) {
            chan.set(null, v);
        }
    }

    @Override
    public List apply(
        @Nullable Type type
    ) {
        if (type == null) {
            type = klass;
        }

        if (type == List.class ||
            type == Iterable.class ||
            type == ArrayList.class ||
            type == Collection.class) {
            return new ArrayList<>();
        }

        if (type == Queue.class ||
            type == Deque.class ||
            type == LinkedList.class) {
            return new LinkedList<>();
        }

        if (type == Stack.class) {
            return new Stack<>();
        }

        if (type == Vector.class) {
            return new Vector<>();
        }

        if (type == AbstractList.class ||
            type == AbstractCollection.class) {
            return new ArrayList<>();
        }

        if (type == CopyOnWriteArrayList.class) {
            return new CopyOnWriteArrayList<>();
        }

        throw new Collapse(
            "Unable to create 'List' instance of '" + type + "'"
        );
    }

    @Override
    public List apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) {
        List list = apply();
        while (spoiler.hasNext()) {
            list.add(
                spoiler.getValue()
            );
        }
        return list;
    }

    @Override
    public List apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        List list = apply();
        ResultSetMetaData meta =
            resultSet.getMetaData();

        int count = meta.getColumnCount();
        for (int i = 1; i <= count; i++) {
            list.add(
                resultSet.getObject(i)
            );
        }

        return list;
    }

    @Override
    public List cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (klass.isInstance(object)) {
            return (List) object;
        }

        if (object instanceof Collection) {
            List list = apply();
            list.addAll(
                (Collection) object
            );
            return list;
        }

        if (object instanceof Map) {
            List list = apply();
            list.addAll(
                ((Map) object).values()
            );
            return list;
        }

        if (object instanceof Iterable) {
            List list = apply();
            for (Object o : (Iterable) object) {
                list.add(o);
            }
            return list;
        }

        if (object instanceof CharSequence) {
            CharSequence cs =
                (CharSequence) object;
            Algo algo = Algo.of(cs);
            if (algo == null) {
                return null;
            }
            return solve(
                algo, new Event<List>(cs).with(supplier)
            );
        }

        if (object.getClass().isArray()) {
            List list = apply();
            int size = Array.getLength(object);

            for (int i = 0; i < size; i++) {
                list.add(
                    Array.get(object, i)
                );
            }
            return list;
        }

        if (object instanceof Spoiler) {
            return apply(
                (Spoiler) supplier, supplier
            );
        }

        if (object instanceof ResultSet) {
            try {
                return apply(
                    supplier, (ResultSet) object
                );
            } catch (SQLException e) {
                throw new IllegalStateException(
                    object + " cannot be converted to " + klass, e
                );
            }
        }

        Spoiler spoiler =
            supplier.flat(object);
        if (spoiler != null) {
            return apply(
                spoiler, supplier
            );
        } else {
            throw new IllegalStateException(
                object + " cannot be converted to " + klass
            );
        }
    }

    @Override
    public Builder<List> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0(
            type, klass, this
        );
    }

    public static class Builder0<T extends Collection> extends Builder<T> implements Callback {

        protected Type actual;
        protected Type raw, elem;

        protected T bean;
        protected Class<?> elemType;

        protected Spare<T> rawSpare;
        protected Spare<?> elemSpare;

        public Builder0(
            Type type,
            Class<?> kind,
            Spare<T> spare
        ) {
            raw = kind;
            actual = type;
            rawSpare = spare;
        }

        @Override
        public void onOpen() {
            Type type = actual;
            if (type != null) {
                if (type instanceof Class) {
                    if (type != Object.class) {
                        raw = type;
                    }
                } else if (type instanceof ParameterizedType) {
                    ParameterizedType p = (ParameterizedType) type;
                    raw = p.getRawType();
                    type = p.getActualTypeArguments()[0];
                    if (type != Object.class) {
                        Class<?> cls = Space.wipe(
                            elem = locate(type)
                        );
                        if (cls != null &&
                            cls != Object.class) {
                            elemType = cls;
                            elemSpare = supplier.lookup(cls);
                        }
                    }
                } else {
                    throw new IllegalStateException(
                        "Failed to resolve this " + type
                    );
                }
            }
            bean = rawSpare.apply(raw);
        }

        @Override
        public Pipage onOpen(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            Spare<?> spare = elemSpare;
            if (spare == null) {
                spare = supplier.search(
                    elemType, space
                );
                if (spare == null) {
                    return null;
                }
            }

            Builder<?> child =
                spare.getBuilder(elem);

            if (child == null) {
                return null;
            }

            return child.init(this, this);
        }

        @Override
        public void onEmit(
            @NotNull Pipage pipage,
            @Nullable Object result
        ) throws IOException {
            bean.add(result);
        }

        @Override
        public void onEmit(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            Spare<?> spare = elemSpare;
            if (spare == null) {
                spare = supplier.search(
                    elemType, space
                );
                if (spare == null) {
                    return;
                }
            }

            bean.add(
                spare.read(
                    flag, value
                )
            );
        }

        @Override
        public T build() {
            return bean;
        }

        @Override
        public void onClose() {
            bean = null;
        }
    }
}
