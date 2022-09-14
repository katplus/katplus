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
import plus.kat.utils.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MapSpare implements Spare<Map> {

    public static final MapSpare
        INSTANCE = new MapSpare(LinkedHashMap.class);

    protected final Class<Map> klass;

    public MapSpare(
        @NotNull Class<?> klass
    ) {
        this.klass = (Class<Map>) klass;
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
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz.isAssignableFrom(klass)
            || klass.isAssignableFrom(clazz);
    }

    @Override
    public Boolean getFlag() {
        return Boolean.TRUE;
    }

    @Override
    public Class<Map> getType() {
        return klass;
    }

    @Override
    public Map read(
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
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            chan.set(
                entry.getKey().toString(),
                entry.getValue()
            );
        }
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
            String key = meta.getColumnLabel(i);
            if (key == null) {
                key = meta.getColumnName(i);
            }
            map.put(
                key, resultSet.getObject(i)
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
            return Casting.cast(
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
        return new Spoiler0(bean);
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

    @NotNull
    public static Map apply(
        @Nullable Type type
    ) {
        if (type == Map.class) {
            return new LinkedHashMap<>();
        }

        if (type == HashMap.class) {
            return new HashMap<>();
        }

        if (type == Object.class ||
            type == LinkedHashMap.class) {
            return new LinkedHashMap<>();
        }

        if (type == ConcurrentHashMap.class ||
            type == ConcurrentMap.class) {
            return new ConcurrentHashMap<>();
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

        if (type == Properties.class) {
            return new Properties();
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

    public static Spare<Map> of(
        @NotNull Class<?> type
    ) {
        if (type == Map.class ||
            type == LinkedHashMap.class) {
            return INSTANCE;
        }

        return new MapSpare(type);
    }

    @Override
    public Builder<Map> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0(
            klass, type
        );
    }

    public static class Spoiler0 implements Spoiler {

        private Map.Entry entry;
        private final Iterator<Map.Entry> it;

        public Spoiler0(
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
            Object key = entry.getKey();
            if (key == null) {
                return "";
            }
            return key.toString();
        }

        @Override
        public Object getValue() {
            return entry.getValue();
        }
    }

    public static class Builder0 extends Builder<Map> {

        protected Type tag;
        protected Type type;
        protected Type key, val;

        protected Map entity;
        protected Spare spare0;

        public Builder0(
            @NotNull Type tag,
            @Nullable Type type
        ) {
            this.tag = tag;
            this.type = type;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) {
            Type raw = type;
            if (raw instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) raw;
                raw = p.getRawType();
                Type[] actual = p.getActualTypeArguments();
                val = actual[1];
                if (actual[0] != String.class) {
                    spare0 = supplier.lookup(
                        key = actual[0], null
                    );
                    if (spare0 == null) {
                        throw new Collapse(
                            "Key's spare does not exist"
                        );
                    }
                }
            }

            entity = apply(
                raw == null ? tag : raw
            );
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            Type type1 = val;
            Spare<?> spare1 =
                supplier.lookup(type1, space);

            if (spare1 != null) {
                Type type0 = key;
                value.setType(type1);
                if (type0 == null) {
                    entity.put(
                        alias.toString(),
                        spare1.read(
                            event, value
                        )
                    );
                } else {
                    alias.setType(type0);
                    entity.put(
                        spare0.read(
                            event, alias
                        ),
                        spare1.read(
                            event, value
                        )
                    );
                }
            }
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            Type type = key;
            if (type == null) {
                entity.put(
                    alias.toString(),
                    child.getResult()
                );
            } else {
                alias.setType(type);
                entity.put(
                    spare0.read(
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
            Type type = val;
            Spare<?> spare =
                supplier.lookup(type, space);

            if (spare == null) {
                return null;
            }

            return spare.getBuilder(type);
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
