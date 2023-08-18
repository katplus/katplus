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

import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MapSpare extends BeanSpare<Map> {

    public static final MapSpare
        INSTANCE = new MapSpare(
        Map.class, Supplier.ins()
    );

    final int type;

    public MapSpare(
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super(
            (Class<Map>) klass, context
        );
        if (klass == Map.class ||
            klass == LinkedHashMap.class) {
            type = 6;
        } else if (klass == HashMap.class ||
            klass == AbstractMap.class) {
            type = 1;
        } else if (klass == TreeMap.class ||
            klass == SortedMap.class ||
            klass == NavigableMap.class) {
            type = 2;
        } else if (klass == Hashtable.class) {
            type = 3;
        } else if (klass == Properties.class) {
            type = 4;
        } else if (klass == WeakHashMap.class) {
            type = 5;
        } else if (klass == IdentityHashMap.class) {
            type = 7;
        } else if (klass == ConcurrentMap.class ||
            klass == ConcurrentHashMap.class) {
            type = 8;
        } else if (klass == ConcurrentSkipListMap.class ||
            klass == ConcurrentNavigableMap.class) {
            type = 9;
        } else if (Map.class.isAssignableFrom(klass)) {
            type = -1;
        } else {
            throw new IllegalStateException(
                "Received unsupported: " + klass.getName()
            );
        }
    }

    @Override
    public Map apply() {
        switch (type) {
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
    public String getSpace() {
        return "Map";
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
        for (Map.Entry<?, ?> item : ((Map<?, ?>) value).entrySet()) {
            Object name = item.getKey();
            chan.set(
                name == null ? "null" : name, item.getValue()
            );
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
