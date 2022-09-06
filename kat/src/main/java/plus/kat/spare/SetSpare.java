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
public class SetSpare implements Spare<Set> {

    public static final SetSpare
        INSTANCE = new SetSpare();

    protected final Class<Set> klass;

    public SetSpare() {
        this(
            HashSet.class
        );
    }

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
    public Set cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (klass.isInstance(data)) {
            return (Set) data;
        }

        if (data instanceof Collection) {
            Set set = apply();
            set.addAll(
                (Set) data
            );
            return set;
        }

        if (data instanceof Map) {
            Set set = apply();
            set.addAll(
                ((Map) data).values()
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

        if (data.getClass().isArray()) {
            Set set = apply();
            int size = Array.getLength(data);

            for (int i = 0; i < size; i++) {
                set.add(
                    Array.get(data, i)
                );
            }
            return set;
        }

        Spoiler spoiler = supplier.flat(data);
        if (spoiler != null) {
            Set set = apply();
            while (spoiler.hasNext()) {
                set.add(
                    spoiler.getValue()
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
            type == Object.class ||
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

        throw new CallCrash(
            "Unable to create 'Set' instance of '" + type + "'"
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
        return new ListSpare.Builder$<Set>(
            klass, type
        ) {
            @Override
            public Set onCreate(
                @NotNull Type type
            ) {
                return apply(type);
            }
        };
    }
}
