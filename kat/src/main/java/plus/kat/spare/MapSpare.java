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
import java.sql.*;
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
public class MapSpare extends Property<Map> {

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

    @Override
    public String getSpace() {
        return "M";
    }

    @Override
    public Boolean getFlag() {
        return Boolean.TRUE;
    }

    @Override
    public Map read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (flag.isFlag(Flag.VALUE_AS_BEAN)) {
            Algo algo = Algo.of(value);
            if (algo == null) {
                return null;
            }
            return solve(
                algo, new Event<Map>(value).with(flag)
            );
        }
        return null;
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            Object key = entry.getKey();
            if (key instanceof String) {
                chan.set(
                    (String) key, entry.getValue()
                );
            } else {
                chan.set(
                    String.valueOf(key), entry.getValue()
                );
            }
        }
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

        throw new Collapse(
            "Unable to create 'Map' instance of '" + type + "'"
        );
    }

    @Override
    public Map apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) {
        Map map = apply();
        while (spoiler.hasNext()) {
            map.put(
                spoiler.getKey(),
                spoiler.getValue()
            );
        }
        return map;
    }

    @Override
    public Map apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        Map map = apply();
        ResultSetMetaData meta =
            resultSet.getMetaData();

        int count = meta.getColumnCount();
        for (int i = 1; i <= count; i++) {
            String name = meta.getColumnLabel(i);
            if (name == null) {
                name = meta.getColumnName(i);
            }
            map.put(
                name, resultSet.getObject(i)
            );
        }

        return map;
    }

    @Override
    public Map cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (klass.isInstance(object)) {
            return (Map) object;
        }

        if (object instanceof Map) {
            Map map = apply();
            map.putAll(
                (Map) object
            );
            return map;
        }

        if (object instanceof CharSequence) {
            CharSequence cs =
                (CharSequence) object;
            Algo algo = Algo.of(cs);
            if (algo == null) {
                return null;
            }
            return solve(
                algo, new Event<Map>(cs).with(supplier)
            );
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
    public Spoiler flat(
        @NotNull Map bean
    ) {
        return new Folder(bean);
    }

    @Override
    public boolean flat(
        @NotNull Map bean,
        @NotNull Visitor visitor
    ) {
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) bean).entrySet()) {
            visitor.accept(
                entry.getKey(),
                entry.getValue()
            );
        }
        return true;
    }

    @Override
    public Builder<Map> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0(
            type, klass, this
        );
    }

    public static class Folder
        implements Spoiler {

        private Map.Entry entry;
        private final Iterator<Map.Entry> it;

        public Folder(
            @NotNull Map map
        ) {
            it = map.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            if (it.hasNext()) {
                entry = it.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getKey() {
            return String.valueOf(
                entry.getKey()
            );
        }

        @Override
        public Object getValue() {
            return entry.getValue();
        }
    }

    public static class Builder0 extends Builder<Map> implements Callback {

        protected Map bean;
        protected Object name;
        protected Class<?> valType;

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
                        Class<?> cls = Space.wipe(
                            key = trace(type)
                        );
                        if (cls != Object.class &&
                            cls != String.class) {
                            keySpace = supplier.lookup(cls);
                            if (keySpace == null) {
                                throw new IOException(
                                    type + "'s spare does not exist"
                                );
                            }
                        }
                    }

                    type = args[1];
                    if (type != Object.class) {
                        Class<?> cls = Space.wipe(
                            val = trace(type)
                        );
                        if (cls != null &&
                            cls != Object.class) {
                            valType = cls;
                            valSpace = supplier.lookup(cls);
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
            Spare<?> s1 = valSpace;
            if (s1 == null) {
                s1 = supplier.search(
                    valType, space
                );
                if (s1 == null) {
                    return null;
                }
            }

            Builder<?> child =
                s1.getBuilder(val);

            if (child == null) {
                return null;
            }

            Spare<?> s0 = keySpace;
            if (s0 == null) {
                name = alias.toString();
            } else {
                name = s0.read(flag, alias);
            }

            return child.init(this, this);
        }

        @Override
        public void onEmit(
            @NotNull Pipage pipage,
            @Nullable Object result
        ) throws IOException {
            bean.put(
                name, result
            );
        }

        @Override
        public void onEmit(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            Spare<?> s1 = valSpace;
            if (s1 == null) {
                s1 = supplier.search(
                    valType, space
                );
                if (s1 == null) {
                    return;
                }
            }

            Spare<?> s0 = keySpace;
            if (s0 == null) {
                bean.put(
                    alias.toString(),
                    s1.read(
                        flag, value
                    )
                );
            } else {
                bean.put(
                    s0.read(
                        flag, alias
                    ),
                    s1.read(
                        flag, value
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
