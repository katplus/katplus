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
 * @since 0.0.2
 */
@SuppressWarnings("rawtypes")
public class IterableSpare implements Spare<Iterable> {

    public static final IterableSpare
        INSTANCE = new IterableSpare();

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$L;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return Iterable.class.isAssignableFrom(klass);
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return Boolean.FALSE;
    }

    @NotNull
    @Override
    public Class<Iterable> getType() {
        return Iterable.class;
    }

    @Override
    public Iterable read(
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
        Iterable<?> val =
            (Iterable<?>) value;

        for (Object v : val) {
            chan.set(
                null, v
            );
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public Iterable cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof Iterable) {
            return (Iterable) data;
        }

        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, null, supplier
            );
        }

        if (data.getClass().isArray()) {
            int size = Array.getLength(data);
            List list = new ArrayList(size);

            for (int i = 0; i < size; i++) {
                list.add(
                    Array.get(data, i)
                );
            }
            return list;
        }

        return null;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public Builder<Iterable> getBuilder(
        @Nullable Type type
    ) {
        if (type == null) {
            return new Builder0(
                null, null
            );
        }

        Class<?> klass;
        ParameterizedType param = null;

        Builder<?> builder;
        if (type instanceof ParameterizedType) {
            param = (ParameterizedType) type;
            klass = (Class<?>) param.getRawType();
        }

        // other
        else if (type instanceof Class) {
            klass = (Class<?>) type;
        } else {
            return new Builder0(
                null, null
            );
        }

        if (Set.class.isAssignableFrom(klass)) {
            builder = new SetSpare.Builder0(type);
            return (Builder<Iterable>) builder;
        }

        if (List.class.isAssignableFrom(klass)) {
            builder = new ListSpare.Builder0(type);
            return (Builder<Iterable>) builder;
        }

        return new Builder0(klass, param);
    }

    public static class Builder0 extends Builder<Iterable> {

        private Class<?> raw;
        private Type param;
        private Spare<?> v;

        private Collection entity;
        private ParameterizedType actual;

        public Builder0(
            @Nullable Class<?> raw,
            @Nullable ParameterizedType actual
        ) {
            this.raw = raw;
            this.actual = actual;
        }

        @Override
        public void create(
            @NotNull Alias alias
        ) throws Crash, IOCrash {
            // array
            if (raw == null ||
                raw == Iterable.class ||
                raw == Collection.class) {
                entity = new ArrayList<>();
            }

            // abstract
            else if (raw == AbstractCollection.class) {
                entity = new ArrayList<>();
            }

            // crash
            else {
                throw new Crash(
                    "Can't create instance of '" + raw + "'", false
                );
            }

            // spare
            if (param != null) {
                v = Reflex.lookup(
                    param = actual.getActualTypeArguments()[0], supplier
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
        public Iterable bundle() {
            return entity;
        }

        @Override
        public Builder<?> observe(
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
            raw = null;
            v = null;
            actual = null;
            param = null;
            entity = null;
        }
    }
}
