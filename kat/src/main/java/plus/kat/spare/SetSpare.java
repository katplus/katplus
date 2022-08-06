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
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
public class SetSpare implements Spare<Set> {

    public static final SetSpare
        INSTANCE = new SetSpare();

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$S;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return Set.class.isAssignableFrom(klass);
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return Boolean.FALSE;
    }

    @NotNull
    @Override
    public Class<Set> getType() {
        return Set.class;
    }

    @Override
    public Set read(
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
        for (Object entry : (Set<?>) value) {
            chan.set(
                null, entry
            );
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public Set cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data instanceof Set) {
            return (Set) data;
        }

        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, null, supplier
            );
        }

        if (data instanceof Collection) {
            return new HashSet(
                (Collection) data
            );
        }

        if (data instanceof Iterable) {
            Set set = new HashSet();
            for (Object o : (Iterable) data) {
                set.add(o);
            }
            return set;
        }

        if (data.getClass().isArray()) {
            int size = Array.getLength(data);
            Set set = new HashSet(size);

            for (int i = 0; i < size; i++) {
                set.add(
                    Array.get(data, i)
                );
            }
            return set;
        }

        return null;
    }

    @Nullable
    @Override
    public Builder<Set> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0(type);
    }

    public static class Builder0 extends Builder<Set> {

        private Set entity;
        private Type type;
        private Type param;
        private Spare<?> v;

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
                param = p.getActualTypeArguments()[0];
                v = Reflect.lookup(
                    param, supplier
                );
            }

            // hash
            if (raw == null ||
                raw == Set.class ||
                raw == HashSet.class) {
                entity = new HashSet<>();
            }

            // linked
            else if (raw == LinkedHashSet.class) {
                entity = new LinkedHashSet<>();
            }

            // tree
            else if (raw == TreeSet.class ||
                raw == SortedSet.class ||
                raw == NavigableSet.class) {
                entity = new TreeSet<>();
            }

            // concurrent
            else if (raw == ConcurrentSkipListSet.class) {
                entity = new ConcurrentSkipListSet<>();
            }

            // abstract
            else if (raw == AbstractSet.class) {
                entity = new HashSet<>();
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
                value.setType(param);
                entity.add(
                    v.read(
                        event, value
                    )
                );
            } else {
                Spare<?> spare = supplier
                    .lookup(space);

                if (spare != null) {
                    value.setType(param);
                    entity.add(
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
            entity.add(
                child.getResult()
            );
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) {
            if (v != null) {
                return v.getBuilder(param);
            }

            Spare<?> spare = supplier
                .lookup(space);

            if (spare == null) {
                return null;
            }

            return spare.getBuilder(param);
        }

        @Nullable
        @Override
        public Set getResult() {
            return entity;
        }

        @Override
        public void onDestroy() {
            type = null;
            v = null;
            param = null;
            entity = null;
        }
    }
}
