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
 * @since 0.0.5
 */
public interface Bridge {
    /**
     * Copy the property values of the given source bean into this {@link Bridge}
     *
     * <pre>{@code
     *  Object source = ...
     *  Bridge target = ...
     *  target.combine(source);
     * }</pre>
     *
     * @param source the specified source bean
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     * @see Supplier#mutate(Object, Object)
     */
    default int combine(
        @NotNull Object source
    ) {
        return Supplier.ins().mutate(source, this);
    }

    /**
     * Copy the property values of the specified spoiler into this {@link Bridge}
     *
     * <pre>{@code
     *  Spoiler spoiler = ...
     *  Bridge bridge = ...
     *  bridge.update(spoiler);
     * }</pre>
     *
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     * @see Supplier#update(Object, Spoiler)
     */
    default int update(
        @NotNull Spoiler spoiler
    ) {
        return Supplier.ins().update(this, spoiler);
    }

    /**
     * Copy the all values of the specified source into this {@link Bridge}
     *
     * <pre>{@code
     *  Map source = ...
     *  Bridge bridge = ...
     *  bridge.update(source);
     * }</pre>
     *
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     * @see Supplier#update(Object, Spoiler)
     */
    default int update(
        @NotNull Map<?, ?> source
    ) {
        return update(
            Spoiler.of(source)
        );
    }

    /**
     * Copy the property values of the specified spoiler into this {@link Bridge}
     *
     * <pre>{@code
     *  ResultSet resultSet = ...
     *  Bridge bridge = ...
     *  bridge.update(resultSet);
     * }</pre>
     *
     * @return the number of rows affected
     * @throws SQLException         If a database access error occurs
     * @throws NullPointerException If the parameters contains null
     * @see Supplier#update(Object, Spoiler)
     */
    default int update(
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return Supplier.ins().update(this, resultSet);
    }

    /**
     * Copy the property values of this {@link Bridge} into the given target bean
     *
     * <pre>{@code
     *  Bridge source = ...
     *  Object target = ...
     *  source.migrate(target);
     * }</pre>
     *
     * @param target the specified target bean
     * @return the number of rows affected
     * @throws NullPointerException If the parameters contains null
     * @see Supplier#mutate(Object, Object)
     */
    default int migrate(
        @NotNull Object target
    ) {
        return Supplier.ins().mutate(this, target);
    }
}
