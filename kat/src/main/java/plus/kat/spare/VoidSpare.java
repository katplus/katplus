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
import plus.kat.chain.*;
import plus.kat.crash.*;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author kraity
 * @since 0.0.4
 */
public class VoidSpare extends Property<Void> {

    public static final VoidSpare
        INSTANCE = new VoidSpare();

    public VoidSpare() {
        super(Void.class);
    }

    @Override
    public Void apply() {
        return null;
    }

    @Override
    public Space getSpace() {
        return Space.$;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == void.class
            || clazz == Void.class
            || clazz == Object.class;
    }

    @Override
    public Void apply(
        @NotNull Spoiler spoiler,
        @NotNull Supplier supplier
    ) throws Collapse {
        throw new Collapse(
            "Unsupported"
        );
    }

    @Override
    public Void apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        throw new Collapse(
            "Unsupported"
        );
    }

    @Override
    public Void cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        return null;
    }

    @Override
    public Void read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return null;
    }

    @Override
    public Void read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return null;
    }
}
