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

import plus.kat.*;
import plus.kat.crash.*;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author kraity
 * @since 0.0.3
 */
public abstract class Property<T> implements Spare<T> {

    protected final Class<T> klass;

    protected Property(
        @NotNull Class<T> klass
    ) {
        this.klass = klass;
    }

    @Override
    public CharSequence getSpace() {
        return klass.getName();
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz.isAssignableFrom(klass);
    }

    @Override
    public Boolean getFlag() {
        return null;
    }

    @Override
    public Class<? extends T> getType() {
        return klass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        ResultSetMetaData meta =
            resultSet.getMetaData();
        int count = meta.getColumnCount();
        if (count != 1) {
            throw new SQLCrash(
                "Expected 1, actual " + count
            );
        }

        Object val = resultSet.getObject(1);
        if (val == null) {
            return null;
        }

        if (klass.isInstance(val)) {
            return (T) val;
        }

        T var = cast(
            supplier, val
        );
        if (var != null) {
            return var;
        }

        throw new SQLCrash(
            "Cannot convert the type from " + val.getClass() + " to " + klass
        );
    }

    @Override
    public Builder<? extends T> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }
}
