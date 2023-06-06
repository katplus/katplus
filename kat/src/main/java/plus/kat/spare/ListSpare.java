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

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static plus.kat.spare.Parser.*;
import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ListSpare extends BaseSpare<List> {

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

        throw new IllegalStateException(
            "Failed to build this " + type
        );
    }

    @Override
    public String getSpace() {
        return "List";
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
    public Factory getFactory(
        @Nullable Type type
    ) {
        return new Builder0(
            type, klass, this
        );
    }

    @Override
    public List read(
        @NotNull Flag flag,
        @NotNull Value data
    ) throws IOException {
        if (data.isNothing()) {
            return null;
        }

        if (flag.isFlag(Flag.VALUE_AS_BEAN)) {
            Algo algo = algoOf(data);
            if (algo == null) {
                return null;
            }
            try (Parser op = with(this)) {
                return op.solve(
                    algo, Flow.of(data)
                );
            }
        }

        throw new IOException(
            "Failed to parse the value to `" + klass
                + "` unless `Flag.VALUE_AS_BEAN` is enabled"
        );
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        for (Object elem : (Iterable<?>) value) {
            chan.set(
                null, elem
            );
        }
    }

    public static class Builder0<T extends Collection> extends Builder<T> {

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
            elemType = Object.class;
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
                        Class<?> cls = classOf(
                            elem = holder.swap(type)
                        );
                        if (cls != null &&
                            cls != Object.class) {
                            elemType = cls;
                            elemSpare = context.assign(cls);
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
        public Spider onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Spare<?> spare = elemSpare;
            if (spare == null) {
                spare = context.assign(elemType, space);
                if (spare == null) {
                    throw new IOException(
                        "Not found the spare of " + space
                    );
                }
            }

            Factory child =
                spare.getFactory(elem);

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
            bean.add(value);
        }

        @Override
        public void onEach(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Spare<?> spare = elemSpare;
            if (spare == null) {
                spare = context.assign(elemType, space);
                if (spare == null) {
                    throw new IOException(
                        "Not found the spare of " + space
                    );
                }
            }

            bean.add(
                spare.read(
                    this, value
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
