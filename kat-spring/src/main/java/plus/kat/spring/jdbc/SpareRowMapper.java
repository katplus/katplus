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
package plus.kat.spring.jdbc;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import plus.kat.Spare;
import plus.kat.Supplier;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author kraity
 * @see RowMapper
 * @see Spare#apply(Supplier, ResultSet)
 * @since 0.0.3
 */
public class SpareRowMapper<T> implements RowMapper<T> {

    protected final Spare<T> spare;
    protected final Supplier supplier;

    public SpareRowMapper(
        Spare<T> spare
    ) {
        this(spare, null);
    }

    public SpareRowMapper(
        Class<T> klass
    ) {
        this(klass, null);
    }

    public SpareRowMapper(
        Class<T> klass,
        Supplier supplier
    ) {
        Assert.notNull(klass, "Class must not be null");
        this.supplier = supplier;
        if (supplier == null) {
            spare = Spare.lookup(klass);
        } else {
            spare = supplier.lookup(klass);
        }
        Assert.notNull(spare, "Can't find the Spare of " + klass);
    }

    public SpareRowMapper(
        Spare<T> spare,
        Supplier supplier
    ) {
        Assert.notNull(spare, "Spare must not be null");
        this.spare = spare;
        this.supplier = supplier;
    }

    @Override
    public T mapRow(
        ResultSet rs, int rowNum
    ) throws SQLException {
        Supplier $supplier = supplier;
        if ($supplier == null) {
            return spare.apply(rs);
        }
        return spare.apply($supplier, rs);
    }
}
