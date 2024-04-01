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

import static plus.kat.spare.Supplier.Vendor.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ListSpare extends BeanSpare<List> {

    public static final ListSpare
        INSTANCE = new ListSpare(List.class, INS);

    final int type;

    public ListSpare(
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super((Class<List>) klass, context);
        if (klass == List.class ||
            klass == ArrayList.class ||
            klass == Collection.class) {
            type = 3;
        } else if (klass == Queue.class ||
            klass == Deque.class ||
            klass == LinkedList.class) {
            type = 4;
        } else if (klass == Stack.class) {
            type = 1;
        } else if (klass == Vector.class) {
            type = 2;
        } else if (klass == AbstractList.class ||
            klass == AbstractCollection.class) {
            type = 3;
        } else if (klass == CopyOnWriteArrayList.class) {
            type = 5;
        } else if (List.class.isAssignableFrom(klass)) {
            type = -1;
        } else {
            throw new IllegalStateException(
                "Received unsupported: " + klass.getName()
            );
        }
    }

    @Override
    public List apply() {
        switch (type) {
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
                    null, list.get(i)
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

        protected T bean;
        protected Type actual;
        protected Type valType;

        protected Spare<T> subject;
        protected Spare<Object> valSpare;

        public Builder0(
            Type type,
            Spare<T> spare
        ) {
            actual = type;
            subject = spare;
        }

        @Override
        public void onCreate() {
            Type type = actual;
            if (type instanceof ParameterizedType) {
                type = ((ParameterizedType)
                    type).getActualTypeArguments()[0];
                if (type != Object.class) {
                    type = getType(type);
                    if (type != Object.class) {
                        valType = type;
                        valSpare = context.assign(type);
                    }
                }
            }
            bean = subject.apply();
        }

        @Override
        public Pipe onOpen(
            @NotNull Alias alias,
            @NotNull Space space
        ) throws IOException {
            Type type = valType;
            Spare<?> spare = valSpare;

            if (spare == null) {
                spare = context.assign(type, space);
                if (spare == null) {
                    throw new IOException(
                        "Not found the spare of " + space
                    );
                }
            }

            Factory member = spare.getFactory(type);
            return member == null ? null : member.attach(this);
        }

        @Override
        public void onNext(
            @Nullable Object value
        ) throws IOException {
            bean.add(value);
        }

        @Override
        public void onNext(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Spare<?> spare = valSpare;
            if (spare == null) {
                spare = context.assign(
                    valType, space
                );
                if (spare == null) {
                    throw new IOException(
                        "Not found the spare of " + space
                    );
                }
            }

            bean.add(
                spare.read(this, value)
            );
        }

        @Override
        public T build() {
            return bean;
        }

        @Override
        public Type getType() {
            return actual;
        }

        @Override
        public void onDestroy() {
            bean = null;
        }
    }
}
