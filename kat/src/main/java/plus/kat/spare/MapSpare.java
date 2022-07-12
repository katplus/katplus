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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.reflex.*;
import plus.kat.utils.Casting;

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
    public Space getSpace() {
        return Space.$M;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass.isAssignableFrom(LinkedHashMap.class);
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return Boolean.TRUE;
    }

    @NotNull
    @Override
    public Class<LinkedHashMap> getType() {
        return LinkedHashMap.class;
    }

    @Override
    public Map read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOCrash {
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
    ) throws IOCrash {
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            chan.set(
                entry.getKey().toString(),
                entry.getValue()
            );
        }
    }

    @Nullable
    @Override
    public Map cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
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
        private Spare<?> k, v;

        public Builder0(
            @Nullable Type type
        ) {
            this.type = type;
        }

        @Override
        public void create(
            @NotNull Alias alias
        ) throws Crash, IOCrash {
            Type raw = type;
            if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) type;
                raw = p.getRawType();
                Type[] ary = p.getActualTypeArguments();
                k = Reflex.lookup(
                    ary[0], supplier
                );
                v = Reflex.lookup(
                    type = ary[1], supplier
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
        public void accept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
            if (v != null) {
                if (k == null) {
                    entity.put(
                        alias.toString(),
                        v.read(
                            flag, value
                        )
                    );
                } else {
                    entity.put(
                        k.read(
                            flag, alias
                        ),
                        v.read(
                            flag, value
                        )
                    );
                }
            } else {
                Spare<?> spare = supplier
                    .lookup(space);

                if (spare != null) {
                    entity.put(
                        alias.toString(),
                        spare.read(
                            flag, value
                        )
                    );
                }
            }
        }

        @Nullable
        @Override
        public Map bundle() {
            return entity;
        }

        @Override
        public Builder<?> observe(
            @NotNull Space space,
            @NotNull Alias alias
        ) {
            if (v != null) {
                return v.getBuilder(null);
            }

            Spare<?> spare = supplier
                .lookup(space);

            if (spare == null) {
                return null;
            }

            return spare.getBuilder(null);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void dispose(
            @NotNull Builder<?> child
        ) throws IOCrash {
            Alias alias =
                child.alias();
            if (k == null) {
                entity.put(
                    alias.toString(),
                    child.bundle()
                );
            } else {
                entity.put(
                    k.read(
                        flag, alias
                    ),
                    child.bundle()
                );
            }
        }

        @Override
        public void close() {
            type = null;
            k = null;
            v = null;
            entity = null;
        }
    }
}
