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
import plus.kat.utils.*;

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
        if (flag.isFlag(Flag.STRING_AS_OBJECT)) {
            return Convert.toObject(
                this, flag, value
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
        @NotNull Type type
    ) {
        if (type == List.class ||
            type == ArrayList.class) {
            return new ArrayList<>();
        }

        if (type == Stack.class) {
            return new Stack<>();
        }

        if (type == Vector.class) {
            return new Vector<>();
        }

        if (type == LinkedList.class ||
            type == AbstractSequentialList.class) {
            return new LinkedList<>();
        }

        if (type == CopyOnWriteArrayList.class) {
            return new CopyOnWriteArrayList<>();
        }

        if (type == AbstractList.class) {
            return new ArrayList<>();
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
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data == null) {
            return null;
        }

        if (klass.isInstance(data)) {
            return (List) data;
        }

        if (data instanceof Collection) {
            List list = apply();
            list.addAll(
                (Collection) data
            );
            return list;
        }

        if (data instanceof Map) {
            List list = apply();
            list.addAll(
                ((Map) data).values()
            );
            return list;
        }

        if (data instanceof Iterable) {
            List list = apply();
            for (Object o : (Iterable) data) {
                list.add(o);
            }
            return list;
        }

        if (data instanceof CharSequence) {
            return Convert.toObject(
                this, (CharSequence) data, null, supplier
            );
        }

        if (data.getClass().isArray()) {
            List list = apply();
            int size = Array.getLength(data);

            for (int i = 0; i < size; i++) {
                list.add(
                    Array.get(data, i)
                );
            }
            return list;
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
    public Builder<List> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0(
            type, klass, this
        );
    }

    public static class Builder0<T extends Collection> extends Builder<T> {

        protected Type tag, raw;
        protected Class<?> kind;

        protected T bundle;
        protected Spare<T> owner;
        protected Spare<?> spare0;

        public Builder0(
            Type type,
            Class<?> kind,
            Spare<T> spare
        ) {
            owner = spare;
            if (type == null) {
                raw = kind;
            }

            // class
            else if (type instanceof Class) {
                if (type != Object.class) {
                    raw = type;
                } else {
                    raw = kind;
                }
            }

            // param
            else if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) type;
                Type[] act = p.getActualTypeArguments();
                raw = p.getRawType();
                if (act[0] != Object.class) {
                    tag = act[0];
                }
            }

            // other
            else {
                Class<?> cls = Find.clazz(type);
                if (cls != null &&
                    cls != Object.class) {
                    raw = cls;
                } else {
                    raw = kind;
                }
            }
        }

        @Override
        public void onCreate() {
            Type tv = tag;
            if (tv != null) {
                Class<?> cls = Find.clazz(tv);
                if (cls != null &&
                    cls != Object.class) {
                    kind = cls;
                    spare0 = supplier.lookup(cls);
                }
            }
            bundle = owner.apply(raw);
        }

        @Override
        public void onAttain(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            Spare<?> spare = spare0;
            if (spare == null) {
                spare = supplier.search(
                    kind, space
                );
                if (spare == null) {
                    return;
                }
            }

            bundle.add(
                spare.read(
                    event, value
                )
            );
        }

        @Override
        public void onDetain(
            @NotNull Builder<?> child
        ) throws IOException {
            bundle.add(
                child.onPacket()
            );
        }

        @Override
        public Builder<?> onAttain(
            @NotNull Space space,
            @NotNull Alias alias
        ) {
            Spare<?> spare = spare0;
            if (spare == null) {
                spare = supplier.search(
                    kind, space
                );
                if (spare == null) {
                    return null;
                }
            }

            return spare.getBuilder(tag);
        }

        @Override
        public T onPacket() {
            return bundle;
        }

        @Override
        public void onDestroy() {
            bundle = null;
        }
    }
}
