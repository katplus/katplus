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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.utils.Casting;
import plus.kat.utils.Reflect;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings("rawtypes")
public class MapSpare implements Spare<Map> {

    public static final MapSpare
        INSTANCE = new MapSpare();

    @NotNull
    @Override
    public Map apply() {
        return new LinkedHashMap<>();
    }

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$M;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return Map.class.isAssignableFrom(klass);
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return Boolean.TRUE;
    }

    @NotNull
    @Override
    public Class<Map> getType() {
        return Map.class;
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

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Map apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet data
    ) throws SQLException {
        Map entity = apply();
        ResultSetMetaData meta =
            data.getMetaData();

        int count = meta.getColumnCount();
        for (int i = 1; i <= count; i++) {
            String key = meta.getColumnLabel(i);
            if (key == null) {
                key = meta.getColumnName(i);
            }
            entity.put(
                key, data.getObject(i)
            );
        }

        return entity;
    }

    @Nullable
    @Override
    public Map cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data instanceof Map) {
            return (Map) data;
        }

        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, null, supplier
            );
        }

        return null;
    }

    @Nullable
    @Override
    public Builder<Map> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0(type);
    }

    public static class Builder0 extends Builder<Map> {

        private Map entity;
        private Type type;
        private Type tk, tv;
        private Spare<?> k, v;

        public Builder0(
            @Nullable Type type
        ) {
            this.type = type;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOException {
            Type raw = type;
            if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) type;
                raw = p.getRawType();
                Type[] ary = p.getActualTypeArguments();
                k = Reflect.lookup(
                    tk = ary[0], supplier
                );
                v = Reflect.lookup(
                    tv = ary[1], supplier
                );
            }

            // linked
            if (raw == null ||
                raw == Map.class) {
                entity = new LinkedHashMap<>();
            }

            // hash
            else if (raw == HashMap.class) {
                entity = new HashMap<>();
            }

            // linked
            else if (raw == LinkedHashMap.class) {
                entity = new LinkedHashMap<>();
            }

            // concurrent
            else if (raw == ConcurrentHashMap.class ||
                raw == ConcurrentMap.class) {
                entity = new ConcurrentHashMap<>();
            }

            // tree
            else if (raw == TreeMap.class) {
                entity = new TreeMap<>();
            }

            // table
            else if (raw == Hashtable.class) {
                entity = new Hashtable<>();
            }

            // weak
            else if (raw == WeakHashMap.class) {
                entity = new WeakHashMap<>();
            }

            // sorted
            else if (raw == SortedMap.class ||
                raw == NavigableMap.class) {
                entity = new TreeMap<>();
            }

            // property
            else if (raw == Properties.class) {
                entity = new Properties();
            }

            // abstract
            else if (raw == AbstractMap.class) {
                entity = new HashMap<>();
            }

            // concurrent
            else if (raw == ConcurrentSkipListMap.class ||
                raw == ConcurrentNavigableMap.class) {
                entity = new ConcurrentSkipListMap<>();
            }

            // crash
            else {
                throw new Crash(
                    "Can't create instance of '" + raw + "'", false
                );
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            if (v != null) {
                alias.setType(tk);
                value.setType(tv);
                if (k == null) {
                    entity.put(
                        alias.toString(),
                        v.read(
                            event, value
                        )
                    );
                } else {
                    entity.put(
                        k.read(
                            event, alias
                        ),
                        v.read(
                            event, value
                        )
                    );
                }
            } else {
                Spare<?> spare = supplier
                    .lookup(space);

                if (spare != null) {
                    value.setType(tv);
                    entity.put(
                        alias.toString(),
                        spare.read(
                            event, value
                        )
                    );
                }
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            if (k == null) {
                entity.put(
                    alias.toString(),
                    child.getResult()
                );
            } else {
                entity.put(
                    k.read(
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
            if (v != null) {
                return v.getBuilder(tv);
            }

            Spare<?> spare = supplier
                .lookup(space);

            if (spare == null) {
                return null;
            }

            return spare.getBuilder(tv);
        }

        @Nullable
        @Override
        public Map getResult() {
            return entity;
        }

        @Override
        public void onDestroy() {
            type = null;
            k = null;
            v = null;
            tv = null;
            tk = null;
            entity = null;
        }
    }
}
