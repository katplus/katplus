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

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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
        return klass.isAssignableFrom(HashSet.class);
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return Boolean.FALSE;
    }

    @NotNull
    @Override
    public Class<HashSet> getType() {
        return HashSet.class;
    }

    @Override
    public Set read(
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
        private Spare<?> v;

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
                v = Reflex.lookup(
                    type = p.getActualTypeArguments()[0], supplier
                );
            }

            // hash
            if (raw == null ||
                raw == Set.class ||
                raw == HashSet.class) {
                entity = new HashSet<>();
            }

            // lined
            else if (raw == LinkedHashSet.class) {
                entity = new LinkedHashSet<>();
            }

            // tree
            else if (raw == TreeSet.class) {
                entity = new TreeSet<>();
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
                entity.add(
                    v.read(
                        flag, value
                    )
                );
            } else {
                Spare<?> spare = supplier
                    .lookup(space);

                if (spare != null) {
                    entity.add(
                        spare.read(
                            flag, value
                        )
                    );
                }
            }
        }

        @Nullable
        @Override
        public Set bundle() {
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
            entity.add(
                child.bundle()
            );
        }

        @Override
        public void close() {
            type = null;
            v = null;
            entity = null;
        }
    }
}
