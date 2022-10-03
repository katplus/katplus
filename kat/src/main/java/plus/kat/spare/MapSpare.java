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
import plus.kat.utils.*;

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
    public Space getSpace() {
        return Space.$M;
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
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            Object key = entry.getKey();
            if (key instanceof CharSequence) {
                chan.set(
                    (CharSequence) key, entry.getValue()
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
        @NotNull Type type
    ) {
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
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data == null) {
            return null;
        }

        if (klass.isInstance(data)) {
            return (Map) data;
        }

        if (data instanceof Map) {
            Map map = apply();
            map.putAll(
                (Map) data
            );
            return map;
        }

        if (data instanceof CharSequence) {
            return Convert.toObject(
                this, (CharSequence) data, null, supplier
            );
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

    public static class Builder0 extends Builder<Map> {

        protected Class<?> kind;
        protected Type key, val, raw;

        protected Spare<Map> owner;
        protected Map entity;
        protected Spare<?> spare0, spare1;

        public Builder0(
            Type type,
            Class<?> kind,
            Spare<Map> spare
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
                    key = act[0];
                }
                if (act[1] != Object.class) {
                    val = act[1];
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
        public void onCreate(
            @NotNull Alias alias
        ) {
            Type tv = val;
            if (tv != null) {
                Class<?> cls = Find.clazz(tv);
                if (cls != null &&
                    cls != Object.class) {
                    kind = cls;
                    spare1 = supplier.lookup(cls);
                }
            }
            Type tk = key;
            if (tk != null) {
                Class<?> cls = Find.clazz(tk);
                if (cls != Object.class &&
                    cls != String.class) {
                    spare0 = supplier.lookup(cls);
                    if (spare0 == null) {
                        throw new Collapse(
                            tk + "'s spare does not exist"
                        );
                    }
                }
            }
            entity = owner.apply(raw);
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            Spare<?> s1 = spare1;
            if (s1 == null) {
                s1 = supplier.search(
                    kind, space
                );
                if (s1 == null) {
                    return;
                }
            }

            value.setType(val);
            Spare<?> s0 = spare0;

            if (s0 == null) {
                entity.put(
                    alias.toString(),
                    s1.read(
                        event, value
                    )
                );
            } else {
                alias.setType(key);
                entity.put(
                    s0.read(
                        event, alias
                    ),
                    s1.read(
                        event, value
                    )
                );
            }
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            Spare<?> s0 = spare0;
            if (s0 == null) {
                entity.put(
                    alias.toString(),
                    child.getResult()
                );
            } else {
                entity.put(
                    s0.read(
                        event, alias
                    ),
                    child.getResult()
                );
            }
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) {
            Spare<?> s1 = spare1;
            if (s1 == null) {
                s1 = supplier.search(
                    kind, space
                );
                if (s1 == null) {
                    return null;
                }
            }

            return s1.getBuilder(val);
        }

        @Override
        public Map getResult() {
            return entity;
        }

        @Override
        public void onDestroy() {
            entity = null;
        }
    }
}
