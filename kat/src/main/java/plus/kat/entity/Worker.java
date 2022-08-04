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

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Worker<K> extends Spare<K>, Maker<K> {
    /**
     * @param alias the alias of entity
     * @throws Crash If a failure occurs
     */
    @Nullable
    K apply(
        @NotNull Alias alias
    ) throws Crash;

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
     */
    @Nullable
    default Setter<K, ?> setter(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return null;
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
        ) throws Crash, IOCrash {
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
        ) throws IOCrash {
            setter.onAccept(
                entity, value
            );
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOCrash {
            setter.onAccept(
                entity, child.getResult()
            );
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
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
        ) throws IOCrash {
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
        ) throws Crash, IOCrash {
            // Nothing
        }

        @Override
        public void onAccept(
            @NotNull Target tag,
            @NotNull Object value
        ) throws IOCrash {
            int i = tag.getIndex();
            data[i] = value;
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOCrash {
            int i = target.getIndex();
            data[i] = child.getResult();
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
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
        ) throws IOCrash {
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
        ) throws IOCrash {
            // Nothing
        }

        public void onAccept(
            @NotNull Space space,
            @NotNull Value value,
            @NotNull Target target
        ) throws IOCrash {
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
        ) throws IOCrash {
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
