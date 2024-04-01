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

import static plus.kat.spare.Supplier.Vendor.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MapSpare extends BeanSpare<Map> {

    public static final MapSpare
        INSTANCE = new MapSpare(Map.class, INS);

    final int type;

    public MapSpare(
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super((Class<Map>) klass, context);
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
        protected Map<Object, List> mark;
        protected Object name;

        protected Type actual;
        protected Type valType;

        protected Spare<Map> subject;
        protected Spare<Object> keySpace, valSpace;

        public Builder0(
            Type type,
            Spare<Map> spare
        ) {
            actual = type;
            subject = spare;
        }

        @Override
        public void onCreate() throws IOException {
            Type type = actual;
            if (type instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType)
                    type).getActualTypeArguments();

                type = args[1];
                if (type != Object.class) {
                    type = getType(type);
                    if (type != Object.class) {
                        valType = type;
                        valSpace = context.assign(type);
                    }
                }

                type = args[0];
                if (type != Object.class &&
                    type != String.class) {
                    type = getType(type);
                    if (type != Object.class &&
                        type != String.class) {
                        keySpace = context.assign(type);
                        if (keySpace == null) {
                            throw new IOException(
                                "Not found the spare of " + type
                            );
                        }
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
            Spare<?> spare = valSpace;

            if (spare == null) {
                spare = context.assign(type, space);
                if (spare == null) {
                    throw new IOException(
                        "Not found the spare of " + space
                    );
                }
            }

            Factory member = spare.getFactory(type);
            if (member == null) {
                return null;
            }

            if ((spare = keySpace) == null) {
                name = alias.toString();
            } else {
                name = spare.read(this, alias);
            }

            return member.attach(this);
        }

        @Override
        public void onNext(
            @Nullable Object value
        ) throws IOException {
            onNext(name, value);
        }

        public void onNext(
            @NotNull Object name,
            @Nullable Object value
        ) throws IOException {
            Object prev = bean.
                putIfAbsent(name, value);
            if (prev != null) {
                if (mark == null) {
                    if (valType == null) {
                        mark = new HashMap<>();
                    } else {
                        throw new IOException();
                    }
                }
                List list = mark.get(name);
                if (list != null) {
                    list.add(value);
                } else {
                    list = new ArrayList();
                    list.add(prev);
                    list.add(value);
                    mark.put(name, list);
                    bean.put(name, list);
                }
            }
        }

        @Override
        public void onNext(
            @NotNull Alias alias,
            @NotNull Space space,
            @NotNull Value value
        ) throws IOException {
            Spare<?> spare = valSpace;
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

            Object data = spare.read(this, value);
            if ((spare = keySpace) == null) {
                onNext(
                    alias.toString(), data
                );
            } else {
                onNext(
                    spare.read(this, alias), data
                );
            }
        }

        @Override
        public Map build() {
            return bean;
        }

        @Override
        public Type getType() {
            return actual;
        }

        @Override
        public void onDestroy() {
            name = null;
            bean = null;
            if (mark != null) {
                mark.clear();
            }
        }
    }
}
