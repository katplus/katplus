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
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.spare.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Worker<K> extends Spare<K>, Maker<K> {
    /**
     * @param alias the alias of entity
     * @throws Crash If a failure occurs
     */
    @NotNull
    K apply(
        @NotNull Alias alias
    ) throws Crash;

    /**
     * @param alias the alias of entity
     * @throws Crash If a failure occurs
     * @since 0.0.2
     */
    @NotNull
    @Override
    default K apply(
        @NotNull Alias alias,
        @NotNull Object... params
    ) throws Crash {
        throw new Crash();
    }

    /**
     * @param supplier  the specified supplier
     * @param resultSet the specified resultSet
     * @since 0.0.3
     */
    @NotNull
    @Override
    default K apply(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        return compose(
            supplier, resultSet
        );
    }

    /**
     * @param alias the alias of target
     * @since 0.0.3
     */
    @Nullable
    default Target target(
        @NotNull Object alias
    ) {
        return null;
    }

    /**
     * @param alias the alias of target
     */
    @Nullable
    default Target target(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return null;
    }

    /**
     * @param alias the alias of setter
     * @since 0.0.3
     */
    @Nullable
    default Setter<K, ?> setter(
        @NotNull Object alias
    ) {
        return null;
    }

    /**
     * @param alias the alias of setter
     */
    @Nullable
    default Setter<K, ?> setter(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return null;
    }

    /**
     * @param supplier  the specified supplier
     * @param resultSet the specified resultSet
     * @since 0.0.3
     */
    @NotNull
    default K compose(
        @NotNull Supplier supplier,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        K entity;
        try {
            entity = apply(
                Alias.EMPTY
            );
        } catch (Throwable e) {
            throw new SQLCrash(
                "Error creating " + getType(), e
            );
        }

        ResultSetMetaData meta =
            resultSet.getMetaData();
        int count = meta.getColumnCount();

        // update fields
        for (int i = 1; i <= count; i++) {
            // get its key
            String key = meta.getColumnName(i);

            // try lookup
            Setter<K, ?> setter = setter(key);
            if (setter == null) {
                throw new SQLCrash(
                    "Can't find the Setter of " + key
                );
            }

            // get the value
            Object val = resultSet.getObject(i);

            // skip if null
            if (val == null) {
                continue;
            }

            // get class specified
            Class<?> klass = setter.getType();

            // check type
            if (klass == null) {
                setter.onAccept(
                    entity, val
                );
                continue;
            }

            // update field
            Class<?> type = val.getClass();
            if (klass.isAssignableFrom(type)) {
                setter.onAccept(
                    entity, val
                );
                continue;
            }

            // get spare specified
            Spare<?> spare = supplier.lookup(klass);

            // update field
            if (spare != null) {
                val = spare.cast(
                    supplier, val
                );
                if (val != null) {
                    setter.onAccept(
                        entity, val
                    );
                    continue;
                }
            }

            throw new SQLCrash(
                "Cannot convert the type of " + key + " from " + type + " to " + klass
            );
        }

        return entity;
    }

    /**
     * @param supplier  the specified supplier
     * @param data      the specified params
     * @param resultSet the specified resultSet
     * @since 0.0.3
     */
    @NotNull
    default K compose(
        @NotNull Supplier supplier,
        @NotNull Object[] data,
        @NotNull ResultSet resultSet
    ) throws SQLException {
        ResultSetMetaData meta =
            resultSet.getMetaData();
        int count = meta.getColumnCount();

        // update params
        for (int i = 1; i <= count; i++) {
            // get its key
            String key = meta.getColumnName(i);

            // try lookup
            Target target = target(key);
            if (target == null) {
                throw new SQLCrash(
                    "Can't find the Target of " + key
                );
            }

            // check index
            int k = target.getIndex();
            if (k < 0 || k >= data.length) {
                throw new SQLCrash(
                    "'" + k + "' out of range"
                );
            }

            // get the value
            Object val = resultSet.getObject(i);

            // skip if null
            if (val == null) {
                continue;
            }

            // get class specified
            Class<?> klass = target.getType();

            // check type
            if (klass == null) {
                data[k] = val;
                continue;
            }

            // update field
            Class<?> type = val.getClass();
            if (klass.isAssignableFrom(type)) {
                data[k] = val;
                continue;
            }

            // get spare specified
            Spare<?> spare = supplier.lookup(klass);

            // update field
            if (spare != null) {
                val = spare.cast(
                    supplier, val
                );
                if (val != null) {
                    data[k] = val;
                    continue;
                }
            }

            throw new SQLCrash(
                "Cannot convert the type of " + key + " from " + type + " to " + klass
            );
        }

        try {
            return apply(
                Alias.EMPTY, data
            );
        } catch (Throwable e) {
            throw new SQLCrash(
                "Error creating " + getType(), e
            );
        }
    }

    /**
     * Returns a {@link Builder} of {@link K}
     */
    @Nullable
    @Override
    default Builder<K> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0<>(this);
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    class Builder0<K> extends Builder$<K> {

        protected K entity;
        protected int index;

        protected Worker<K> worker;
        protected Setter<K, ?> setter;

        /**
         * default
         */
        public Builder0(
            @NotNull Worker<K> worker
        ) {
            this.worker = worker;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOException {
            // get an instance
            entity = worker.apply(alias);

            // check this instance
            if (entity == null) {
                throw new Crash(
                    "Entity created through Worker is null", false
                );
            }
        }

        @Override
        public void onAccept(
            @NotNull Target tag,
            @NotNull Object value
        ) throws IOException {
            setter.onAccept(
                entity, value
            );
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            setter.onAccept(
                entity, child.getResult()
            );
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            setter = worker.setter(
                index++, alias
            );

            if (setter != null) {
                onAccept(
                    space, value, setter
                );
            }
        }

        @Nullable
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            setter = worker.setter(
                index++, alias
            );

            if (setter == null) {
                return null;
            }

            return getBuilder(space, setter);
        }

        @Nullable
        @Override
        public K getResult() {
            return entity;
        }

        @Override
        public void onDestroy() {
            index = 0;
            entity = null;
            setter = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    class Builder1<K> extends Builder$<K> {

        protected K entity;
        protected int index;

        protected Target target;
        protected Object[] data;
        protected Worker<K> worker;

        public Builder1(
            @NotNull Worker<K> worker,
            @NotNull Object[] data
        ) {
            this.data = data;
            this.worker = worker;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOException {
            // Nothing
        }

        @Override
        public void onAccept(
            @NotNull Target tag,
            @NotNull Object value
        ) throws IOException {
            int i = tag.getIndex();
            data[i] = value;
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            int i = target.getIndex();
            data[i] = child.getResult();
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            target = worker.target(
                index++, alias
            );

            if (target != null) {
                onAccept(
                    space, value, target
                );
            }
        }

        @Nullable
        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            target = worker.target(
                index++, alias
            );

            if (target == null) {
                return null;
            }

            return getBuilder(space, target);
        }

        @Nullable
        @Override
        public K getResult() {
            if (entity == null) {
                try {
                    entity = worker.apply(
                        getAlias(), data
                    );
                } catch (Crash e) {
                    return null;
                }
            }
            return entity;
        }

        @Override
        public void onDestroy() {
            index = 0;
            entity = null;
            target = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    abstract class Builder$<K> extends Builder<K> {

        public void onAccept(
            @NotNull Target tag,
            @Nullable Object value
        ) throws IOException {
            // Nothing
        }

        public void onAccept(
            @NotNull Space space,
            @NotNull Value value,
            @NotNull Target target
        ) throws IOException {
            // specified coder
            Coder<?> coder = target.getCoder();

            if (coder != null) {
                value.setType(
                    target.getActualType()
                );
                onAccept(
                    target, coder.read(
                        event, value
                    )
                );
                return;
            }

            Spare<?> spare;
            Class<?> klass = target.getType();

            // lookup
            if (klass == null) {
                // specified spare
                spare = supplier.lookup(space);

                if (spare != null) {
                    value.setType(
                        target.getActualType()
                    );
                    onAccept(
                        target, spare.read(
                            event, value
                        )
                    );
                }
            } else {
                // specified spare
                spare = supplier.lookup(klass);

                // skip if null
                if (spare != null) {
                    value.setType(
                        target.getActualType()
                    );
                    onAccept(
                        target, spare.read(
                            event, value
                        )
                    );
                    return;
                }

                // specified spare
                spare = supplier.lookup(space);

                // skip if null
                if (spare != null &&
                    spare.accept(klass)) {
                    value.setType(
                        target.getActualType()
                    );
                    onAccept(
                        target, spare.read(
                            event, value
                        )
                    );
                }
            }
        }

        @Nullable
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Target target
        ) throws IOException {
            // specified coder
            Coder<?> coder = target.getCoder();

            if (coder != null) {
                return coder.getBuilder(
                    target.getActualType()
                );
            }

            Spare<?> spare;
            Class<?> klass = target.getType();

            // lookup
            if (klass == null) {
                // specified spare
                spare = supplier.lookup(space);

                // skip if null
                if (spare != null) {
                    return spare.getBuilder(
                        target.getActualType()
                    );
                }
            } else {
                // specified spare
                spare = supplier.lookup(klass);

                // skip if null
                if (spare != null) {
                    return spare.getBuilder(
                        target.getActualType()
                    );
                }

                // specified spare
                spare = supplier.lookup(space);

                // skip if null
                if (spare != null &&
                    spare.accept(klass)) {
                    return spare.getBuilder(
                        target.getActualType()
                    );
                }
            }

            return null;
        }
    }
}
