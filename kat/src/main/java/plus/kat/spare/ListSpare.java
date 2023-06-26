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
        INSTANCE = new ListSpare(List.class);

    final int mode;

    public ListSpare(
        @NotNull Class<?> klass
    ) {
        super(
            (Class<List>) klass
        );
        if (klass == List.class ||
            klass == ArrayList.class ||
            klass == Collection.class) {
            mode = 3;
        } else if (klass == Queue.class ||
            klass == Deque.class ||
            klass == LinkedList.class) {
            mode = 4;
        } else if (klass == Stack.class) {
            mode = 1;
        } else if (klass == Vector.class) {
            mode = 2;
        } else if (klass == AbstractList.class ||
            klass == AbstractCollection.class) {
            mode = 3;
        } else if (klass == CopyOnWriteArrayList.class) {
            mode = 5;
        } else {
            mode = -1;
        }
    }

    @Override
    public List apply() {
        switch (mode) {
            case 1: {
                return new Stack();
            }
            case 2: {
                return new Vector<>();
            }
            case 3: {
                return new ArrayList<>();
            }
            case 4: {
                return new LinkedList<>();
            }
            case 5: {
                return new CopyOnWriteArrayList<>();
            }
            case 0: {
                return null;
            }
            case -1: {
                try {
                    return klass.newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException(
                        "Failed to build " + klass, e
                    );
                }
            }
        }

        throw new IllegalStateException(
            "Failed to build " + klass
        );
    }

    @Override
    public List apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return apply();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof Collection) {
                    List list = apply();
                    if (list != null) {
                        list.addAll(
                            (Collection) arg
                        );
                    }
                    return list;
                }
            }
        }

        throw new IllegalStateException(
            "No matching constructor found"
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
        return new Builder0(type, this);
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
        if (value instanceof RandomAccess) {
            List<?> list = (List<?>) value;
            int size = list.size();
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < size; i++) {
                chan.set(
                    null,
                    list.get(i)
                );
            }
        } else {
            if (value instanceof Iterable) {
                for (Object element : (Iterable<?>) value) {
                    chan.set(
                        null, element
                    );
                }
            } else {
                Iterator<?> iterator = (Iterator<?>) value;
                while (iterator.hasNext()) {
                    chan.set(
                        null, iterator.next()
                    );
                }
            }
        }
    }

    public static class Builder0<T extends Collection> extends Builder<T> {

        protected Type elem;
        protected Type actual;

        protected T bean;
        protected Class<?> elemType;

        protected Spare<T> rawSpare;
        protected Spare<?> elemSpare;

        public Builder0(
            Type type,
            Spare<T> spare
        ) {
            actual = type;
            rawSpare = spare;
            elemType = Object.class;
        }

        @Override
        public void onOpen() {
            Type type = actual;
            if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) type;
                type = p.getActualTypeArguments()[0];
                if (type != Object.class) {
                    Class<?> cls = classOf(
                        elem = holder.solve(type)
                    );
                    if (cls != null &&
                        cls != Object.class) {
                        elemType = cls;
                        elemSpare = context.assign(cls);
                    }
                }
            }
            bean = rawSpare.apply();
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
