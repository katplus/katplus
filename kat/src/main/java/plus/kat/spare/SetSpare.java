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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class SetSpare extends Property<Set> {

    public static final SetSpare
        INSTANCE = new SetSpare(HashSet.class);

    public SetSpare(
        @NotNull Class<?> klass
    ) {
        super((Class<Set>) klass);
    }

    @Override
    public Set apply() {
        return apply(klass);
    }

    @Override
    public String getSpace() {
        return "S";
    }

    @Override
    public Boolean getFlag() {
        return Boolean.FALSE;
    }

    @Override
    public Set read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (flag.isFlag(Flag.VALUE_AS_BEAN)) {
            Algo algo = Algo.of(value);
            if (algo == null) {
                return null;
            }
            return solve(
                algo, new Event<Set>(value).with(flag)
            );
        }
        throw new IOException(
            "Failed to parse the value to `" + klass
                + "` unless `Flag.VALUE_AS_BEAN` is enabled"
        );
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
    public Set apply(
        @Nullable Type type
    ) {
        if (type == null) {
            type = klass;
        }

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

        throw new Collapse(
            "Unable to create 'Set' instance of '" + type + "'"
        );
    }

    @Override
    public Set apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) {
        Set set = apply();
        while (spoiler.hasNext()) {
            set.add(
                spoiler.getValue()
            );
        }
        return set;
    }

    @Override
    public Set apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        Set set = apply();
        ResultSetMetaData meta =
            resultSet.getMetaData();

        int count = meta.getColumnCount();
        for (int i = 1; i <= count; i++) {
            set.add(
                resultSet.getObject(i)
            );
        }

        return set;
    }

    @Override
    public Set cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (klass.isInstance(object)) {
            return (Set) object;
        }

        if (object instanceof Collection) {
            Set set = apply();
            set.addAll(
                (Set) object
            );
            return set;
        }

        if (object instanceof Map) {
            Set set = apply();
            set.addAll(
                ((Map) object).values()
            );
            return set;
        }

        if (object instanceof Iterable) {
            Set set = apply();
            for (Object o : (Iterable) object) {
                set.add(o);
            }
            return set;
        }

        if (object instanceof CharSequence) {
            CharSequence cs =
                (CharSequence) object;
            Algo algo = Algo.of(cs);
            if (algo == null) {
                return null;
            }
            return solve(
                algo, new Event<Set>(cs).with(supplier)
            );
        }

        if (object.getClass().isArray()) {
            Set set = apply();
            int size = Array.getLength(object);

            for (int i = 0; i < size; i++) {
                set.add(
                    Array.get(object, i)
                );
            }
            return set;
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
    public Builder<Set> getBuilder(
        @Nullable Type type
    ) {
        return new ListSpare.Builder0(
            type, klass, this
        );
    }
}
