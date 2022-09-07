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
package plus.kat.entity;

import plus.kat.anno.NotNull;

import plus.kat.*;
import plus.kat.spare.*;

import java.sql.*;
import java.util.*;

/**
 * @author kraity
 * @since 0.0.4
 */
public interface Agent {
    /**
     * Copy the property values of the given source bean into this {@link Agent}
     *
     * <pre>{@code
     *  Object source = ...
     *  Agent target = ...
     *  target.combine(source);
     * }</pre>
     *
     * @param source the specified source bean
     * @return {@code true} if successful update
     * @throws NullPointerException If the parameters contains null
     * @see Supplier#migrate(Object, Object)
     */
    default boolean combine(
        @NotNull Object source
    ) {
        Supplier supplier = Supplier.ins();
        return supplier.migrate(source, this);
    }

    /**
     * Copy the property values of the specified spoiler into this {@link Agent}
     *
     * <pre>{@code
     *  Spoiler spoiler = ...
     *  Agent agent = ...
     *  agent.update(spoiler);
     * }</pre>
     *
     * @return {@code true} if successful update
     * @throws NullPointerException If the parameters contains null
     * @see Supplier#update(Object, Spoiler)
     */
    default boolean update(
        @NotNull Spoiler spoiler
    ) {
        Supplier supplier = Supplier.ins();
        return supplier.update(this, spoiler);
    }

    /**
     * Copy the all values of the specified source into this {@link Agent}
     *
     * <pre>{@code
     *  Map source = ...
     *  Agent agent = ...
     *  agent.update(source);
     * }</pre>
     *
     * @return {@code true} if successful update
     * @throws NullPointerException If the parameters contains null
     * @see Supplier#update(Object, Spoiler)
     */
    default boolean update(
        @NotNull Map<?, ?> source
    ) {
        return update(
            Spoiler.of(source)
        );
    }

    /**
     * Copy the property values of the specified spoiler into this {@link Agent}
     *
     * <pre>{@code
     *  ResultSet resultSet = ...
     *  Agent agent = ...
     *  agent.update(resultSet);
     * }</pre>
     *
     * @return {@code true} if successful update
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the parameters contains null
     * @see Supplier#update(Object, Spoiler)
     */
    default boolean update(
        @NotNull ResultSet resultSet
    ) throws SQLException {
        Supplier supplier = Supplier.ins();
        return supplier.update(this, resultSet);
    }

    /**
     * Copy the property values of this {@link Agent} into the given target bean
     *
     * <pre>{@code
     *  Agent source = ...
     *  Object target = ...
     *  source.migrate(target);
     * }</pre>
     *
     * @param target the specified target bean
     * @return {@code true} if successful update
     * @throws NullPointerException If the parameters contains null
     * @see Supplier#migrate(Object, Object)
     */
    default boolean migrate(
        @NotNull Object target
    ) {
        Supplier supplier = Supplier.ins();
        return supplier.migrate(this, target);
    }
}
