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
import java.sql.SQLException;

/**
 * @author kraity
 * @since 0.0.3
 */
public abstract class Property<T> implements Spare<T> {

    protected final Class<T> klass;
    protected final Supplier supplier;

    protected Property(
        @NotNull Class<T> klass
    ) {
        this.klass = klass;
        this.supplier = Supplier.ins();
    }

    protected Property(
        @NotNull Class<T> klass,
        @NotNull Supplier supplier
    ) {
        this.klass = klass;
        this.supplier = supplier;
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
    public void embed(
        @NotNull Supplier supplier
    ) {
        supplier.embed(klass, this);
    }

    @Override
    public T apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) throws Collapse {
        if (!spoiler.hasNext()) {
            throw new Collapse(
                "No data source"
            );
        }

        Object obj = spoiler
            .getValue();
        T value = cast(
            obj, supplier
        );

        if (value != null) {
            return value;
        }

        throw new Collapse(
            "Cannot convert the type from "
                + obj + " to " + klass
        );
    }

    @Override
    public T apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        Object obj = resultSet
            .getObject(1);
        T value = cast(
            obj, supplier
        );

        if (value != null) {
            return value;
        }

        throw new SQLCrash(
            "Cannot convert the type from "
                + obj + " to " + klass
        );
    }

    @Override
    public Supplier getSupplier() {
        return supplier;
    }

    @Override
    public Builder<? extends T> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }
}
