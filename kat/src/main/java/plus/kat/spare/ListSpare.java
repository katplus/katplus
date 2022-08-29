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
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.utils.Casting;
import plus.kat.utils.Reflect;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("rawtypes")
public class ListSpare implements Spare<List> {

    public static final ListSpare
        INSTANCE = new ListSpare();

    protected final Class<List> klass;

    public ListSpare() {
        this(
            ArrayList.class
        );
    }

    @SuppressWarnings("unchecked")
    public ListSpare(
        @NotNull Class<?> klass
    ) {
        this.klass = (Class<List>) klass;
    }

    @Override
    public List apply() {
        return apply(klass);
    }

    @Override
    public Space getSpace() {
        return Space.$L;
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
    public Class<List> getType() {
        return klass;
    }

    @Override
    public List read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (flag.isFlag(Flag.STRING_AS_OBJECT)) {
            return Casting.cast(
                this, value, flag, null
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
    @SuppressWarnings("unchecked")
    public List cast(
        @NotNull Supplier supplier,
        @Nullable Object data
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
            return Casting.cast(
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

        Spoiler spoiler = supplier.flat(data);
        if (spoiler != null) {
            List list = apply();
            while (spoiler.hasNext()) {
                list.add(
                    spoiler.getValue()
                );
            }
            return list;
        }

        return null;
    }

    @NotNull
    public static List apply(
        @Nullable Type type
    ) {
        if (type == List.class ||
            type == Object.class ||
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

        throw new CallCrash(
            "Unable to create 'List' instance of '" + type + "'"
        );
    }

    public static Spare<List> of(
        @NotNull Class<?> type
    ) {
        if (type == List.class ||
            type == ArrayList.class) {
            return INSTANCE;
        }

        return new ListSpare(type);
    }

    @Override
    public Builder<List> getBuilder(
        @Nullable Type type
    ) {
        return new Builder$<List>(
            klass, type
        ) {
            @Override
            public List onCreate(
                @NotNull Type type
            ) {
                return apply(type);
            }
        };
    }

    public static class Builder$<T extends Collection> extends Builder<T> {

        protected Type tag;
        protected Type type;

        protected Type param;
        protected T entity;
        protected Spare<?> spare;

        public Builder$(
            @NotNull Type tag,
            @Nullable Type type
        ) {
            this.tag = tag;
            this.type = type;
        }

        @NotNull
        public T onCreate(
            @NotNull Type type
        ) {
            throw new CallCrash(
                "Failed to create"
            );
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) {
            Type raw = type;
            if (raw instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) raw;
                raw = p.getRawType();
                param = p.getActualTypeArguments()[0];
                spare = Reflect.lookup(
                    param, supplier
                );
            }
            entity = onCreate(
                raw == null ? tag : raw
            );
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            if (spare != null) {
                value.setType(param);
                entity.add(
                    spare.read(
                        event, value
                    )
                );
            } else {
                Spare<?> spare =
                    supplier.lookup(space);

                if (spare != null) {
                    value.setType(param);
                    entity.add(
                        spare.read(
                            event, value
                        )
                    );
                }
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            entity.add(
                child.getResult()
            );
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) {
            if (spare != null) {
                return spare.getBuilder(param);
            }

            Spare<?> spare =
                supplier.lookup(space);

            if (spare == null) {
                return null;
            }

            return spare.getBuilder(param);
        }

        @Nullable
        @Override
        public T getResult() {
            return entity;
        }

        @Override
        public void onDestroy() {
            entity = null;
        }
    }
}
