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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentNavigableMap;

import static plus.kat.spare.Parser.*;
import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MapSpare extends BaseSpare<Map> {

    public static final MapSpare
        INSTANCE = new MapSpare(Map.class);

    final int mode;

    public MapSpare(
        @NotNull Class<?> klass
    ) {
        super(
            (Class<Map>) klass
        );
        if (klass == Map.class ||
            klass == LinkedHashMap.class) {
            mode = 6;
        } else if (klass == HashMap.class ||
            klass == AbstractMap.class) {
            mode = 1;
        } else if (klass == TreeMap.class ||
            klass == SortedMap.class ||
            klass == NavigableMap.class) {
            mode = 2;
        } else if (klass == Hashtable.class) {
            mode = 3;
        } else if (klass == Properties.class) {
            mode = 4;
        } else if (klass == WeakHashMap.class) {
            mode = 5;
        } else if (klass == IdentityHashMap.class) {
            mode = 7;
        } else if (klass == ConcurrentMap.class ||
            klass == ConcurrentHashMap.class) {
            mode = 8;
        } else if (klass == ConcurrentSkipListMap.class ||
            klass == ConcurrentNavigableMap.class) {
            mode = 9;
        } else {
            mode = -1;
        }
    }

    @Override
    public Map apply() {
        switch (mode) {
            case 1: {
                return new HashMap();
            }
            case 2: {
                return new TreeMap();
            }
            case 3: {
                return new Hashtable();
            }
            case 4: {
                return new Properties();
            }
            case 5: {
                return new WeakHashMap();
            }
            case 6: {
                return new LinkedHashMap();
            }
            case 7: {
                return new IdentityHashMap();
            }
            case 8: {
                return new ConcurrentHashMap();
            }
            case 9: {
                return new ConcurrentSkipListMap();
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
    public Map apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return apply();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof Map) {
                    Map map = apply();
                    if (map != null) {
                        map.putAll(
                            (Map) arg
                        );
                    }
                    return map;
                }
            }
        }

        throw new IllegalStateException(
            "No matching constructor found"
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
        return new Builder0(type, this);
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
        protected Spare<Map> rawSpare;

        protected Type actual;
        protected Type key, val;
        protected Spare<?> keySpace, valSpace;

        public Builder0(
            Type type,
            Spare<Map> spare
        ) {
            actual = type;
            rawSpare = spare;
            elemType = Object.class;
        }

        @Override
        public void onOpen() throws IOException {
            Type type = actual;
            if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) type;
                Type[] args = p.getActualTypeArguments();

                type = args[0];
                if (type != Object.class &&
                    type != String.class) {
                    Class<?> cls = classOf(
                        key = holder.solve(type)
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
                        val = holder.solve(type)
                    );
                    if (cls != null &&
                        cls != Object.class) {
                        elemType = cls;
                        valSpace = context.assign(cls);
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

            return child.init(this, context);
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
                    s1.read(this, value)
                );
            } else {
                bean.put(
                    s0.read(this, alias),
                    s1.read(this, value)
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
            actual = null;
        }
    }
}
