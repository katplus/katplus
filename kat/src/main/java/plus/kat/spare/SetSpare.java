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

    protected final Class<Set> klass;

    public SetSpare() {
        this(
            HashSet.class
        );
    }

    @SuppressWarnings("unchecked")
    public SetSpare(
        @NotNull Class<?> klass
    ) {
        this.klass = (Class<Set>) klass;
    }

    @Override
    public Set apply() {
        return apply(klass);
    }

    @Override
    public Space getSpace() {
        return Space.$S;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz.isAssignableFrom(klass);
    }

    @Override
    public Boolean getFlag() {
        return Boolean.FALSE;
    }

    @Override
    public Class<Set> getType() {
        return klass;
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

    @Override
    @SuppressWarnings("unchecked")
    public Set cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        Class<?> clazz = data.getClass();
        if (clazz == klass) {
            return (Set) data;
        }

        if (data instanceof Collection) {
            Set set = apply();
            set.addAll(
                (Set) data
            );
            return set;
        }

        if (data instanceof Iterable) {
            Set set = apply();
            for (Object o : (Iterable) data) {
                set.add(o);
            }
            return set;
        }

        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, null, supplier
            );
        }

        if (clazz.isArray()) {
            int size = Array.getLength(data);
            Set set = apply();

            for (int i = 0; i < size; i++) {
                set.add(
                    Array.get(data, i)
                );
            }
            return set;
        }

        return null;
    }

    @NotNull
    public static Set apply(
        @Nullable Type type
    ) {
        if (type == Set.class ||
            type == HashSet.class) {
            return new HashSet<>();
        }

        if (type == LinkedHashSet.class) {
            return new LinkedHashSet<>();
        }

        if (type == TreeSet.class ||
            type == SortedSet.class ||
            type == NavigableSet.class) {
            return new TreeSet<>();
        }

        if (type == ConcurrentSkipListSet.class) {
            return new ConcurrentSkipListSet<>();
        }

        if (type == AbstractSet.class) {
            return new HashSet<>();
        }

        throw new RunCrash(
            "Can't create instance of '" + type + "'", false
        );
    }

    public static Spare<Set> of(
        @NotNull Class<?> type
    ) {
        if (type == Set.class ||
            type == HashSet.class) {
            return INSTANCE;
        }
        return new SetSpare(type);
    }

    @Override
    public Builder<Set> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0(
            klass, type
        );
    }

    public static class Builder0 extends Builder<Set> {

        protected Type type;
        protected Type tag;
        protected Set entity;

        protected Type param;
        protected Spare<?> v;

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
        ) throws Crash, IOException {
            Type raw = type;
            if (raw instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) raw;
                raw = p.getRawType();
                param = p.getActualTypeArguments()[0];
                v = Reflect.lookup(
                    param, supplier
                );
            }
            entity = apply(
                raw == null ? tag : raw
            );
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
            entity = null;
        }
    }
}
