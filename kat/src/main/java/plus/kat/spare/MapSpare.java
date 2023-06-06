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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static plus.kat.spare.Parser.*;
import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MapSpare extends BaseSpare<Map> {

    public static final MapSpare
        INSTANCE = new MapSpare(LinkedHashMap.class);

    public MapSpare(
        @NotNull Class<?> klass
    ) {
        super((Class<Map>) klass);
    }

    @Override
    public Map apply() {
        return apply(klass);
    }

    @NotNull
    public Map apply(
        @Nullable Type type
    ) {
        if (type == null) {
            type = klass;
        }

        if (type == Map.class) {
            return new LinkedHashMap<>();
        }

        if (type == HashMap.class) {
            return new HashMap<>();
        }

        if (type == LinkedHashMap.class) {
            return new LinkedHashMap<>();
        }

        if (type == ConcurrentHashMap.class ||
            type == ConcurrentMap.class) {
            return new ConcurrentHashMap<>();
        }

        if (type == Properties.class) {
            return new Properties();
        }

        if (type == TreeMap.class) {
            return new TreeMap<>();
        }

        if (type == Hashtable.class) {
            return new Hashtable<>();
        }

        if (type == WeakHashMap.class) {
            return new WeakHashMap<>();
        }

        if (type == SortedMap.class ||
            type == NavigableMap.class) {
            return new TreeMap<>();
        }

        if (type == AbstractMap.class) {
            return new HashMap<>();
        }

        if (type == ConcurrentSkipListMap.class ||
            type == ConcurrentNavigableMap.class) {
            return new ConcurrentSkipListMap<>();
        }

        throw new IllegalStateException(
            "Failed to build this " + type
        );
    }

    @Override
    public String getSpace() {
        return "Map";
    }

    @Override
    public Boolean getScope() {
        return Boolean.TRUE;
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.BRACE;
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
    public Map read(
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
        for (Map.Entry<?, ?> item : ((Map<?, ?>) value).entrySet()) {
            Object name = item.getKey();
            if (name instanceof String) {
                chan.set(
                    (String) name, item.getValue()
                );
            } else {
                chan.set(
                    name == null ? "null" : name.toString(), item.getValue()
                );
            }
        }
    }

    public static class Builder0 extends Builder<Map> {

        protected Map bean;
        protected Object name;
        protected Class<?> elemType;

        protected Type actual;
        protected Type key, val, raw;

        protected Spare<Map> rawSpare;
        protected Spare<?> keySpace, valSpace;

        public Builder0(
            Type type,
            Class<?> kind,
            Spare<Map> spare
        ) {
            raw = kind;
            actual = type;
            rawSpare = spare;
            elemType = Object.class;
        }

        @Override
        public void onOpen() throws IOException {
            Type type = actual;
            if (type != null) {
                if (type instanceof Class) {
                    if (type != Object.class) {
                        raw = type;
                    }
                } else if (type instanceof ParameterizedType) {
                    ParameterizedType p = (ParameterizedType) type;
                    raw = p.getRawType();
                    Type[] args = p.getActualTypeArguments();

                    type = args[0];
                    if (type != Object.class &&
                        type != String.class) {
                        Class<?> cls = classOf(
                            key = holder.swap(type)
                        );
                        if (cls != Object.class &&
                            cls != String.class) {
                            keySpace = context.assign(cls);
                            if (keySpace == null) {
                                throw new IOException(
                                    type + "'s spare does not exist"
                                );
                            }
                        }
                    }

                    type = args[1];
                    if (type != Object.class) {
                        Class<?> cls = classOf(
                            val = holder.swap(type)
                        );
                        if (cls != null &&
                            cls != Object.class) {
                            elemType = cls;
                            valSpace = context.assign(cls);
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
            Spare<?> s1 = valSpace;
            if (s1 == null) {
                s1 = context.assign(elemType, space);
                if (s1 == null) {
                    throw new IOException(
                        "Not found the spare of " + space
                    );
                }
            }

            Factory child =
                s1.getFactory(val);

            if (child == null) {
                return null;
            }

            Spare<?> s0 = keySpace;
            if (s0 == null) {
                name = alias.toString();
            } else {
                name = s0.read(this, alias);
            }

            return child.init(
                this, context
            );
        }

        @Override
        public void onEach(
            @Nullable Object value
        ) throws IOException {
            bean.put(
                name, value
            );
        }

        @Override
        public void onEach(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Spare<?> s1 = valSpace;
            if (s1 == null) {
                s1 = context.assign(elemType, space);
                if (s1 == null) {
                    throw new IOException(
                        "Not found the spare of " + space
                    );
                }
            }

            Spare<?> s0 = keySpace;
            if (s0 == null) {
                bean.put(
                    alias.toString(),
                    s1.read(
                        this, value
                    )
                );
            } else {
                bean.put(
                    s0.read(
                        this, alias
                    ),
                    s1.read(
                        this, value
                    )
                );
            }
        }

        @Override
        public Map build() {
            return bean;
        }

        @Override
        public void onClose() {
            name = null;
            bean = null;
        }
    }
}
