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

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.2
 */
public interface Sketch<K> extends Spare<K> {
    /**
     * @param alias the alias of entity
     * @throws Crash If a failure occurs
     */
    @Nullable
    K apply(
        @NotNull Alias alias
    ) throws Crash;

    /**
     * @param alias the alias of setter
     */
    @Nullable
    Setter<K, ?> setter(
        @NotNull int index,
        @NotNull Alias alias
    );

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
    class Builder0<K> extends Builder<K> {

        protected K entity;
        protected int index = -1;

        protected Sketch<K> sketch;
        protected Setter<K, ?> setter;

        /**
         * default
         */
        public Builder0(
            @NotNull Sketch<K> sketch
        ) {
            this.sketch = sketch;
        }

        @Override
        public void create(
            @NotNull Alias alias
        ) throws Crash, IOCrash {
            // get an instance
            entity = sketch.apply(alias);

            // check this instance
            if (entity == null) {
                throw new Crash(
                    "Entity created through Sketch is null", false
                );
            }
        }

        @Override
        public void accept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
            setter = sketch.setter(
                ++index, alias
            );

            if (setter == null) {
                return;
            }

            // specified coder
            Coder<?> coder = setter.getCoder();

            if (coder != null) {
                dispose(
                    coder.read(
                        flag, value
                    )
                );
                return;
            }

            Spare<?> spare;
            Class<?> klass = setter.getKlass();

            // lookup
            if (klass == null) {
                // specified spare
                spare = supplier.lookup(space);

                if (spare != null) {
                    dispose(
                        spare.read(
                            flag, value
                        )
                    );
                }
            } else {
                // specified spare
                spare = supplier.embed(klass);

                // skip if null
                if (spare != null) {
                    dispose(
                        spare.read(
                            flag, value
                        )
                    );
                    return;
                }

                // specified spare
                spare = supplier.lookup(space);

                // skip if null
                if (spare != null &&
                    spare.accept(klass)) {
                    dispose(
                        spare.read(
                            flag, value
                        )
                    );
                }
            }
        }

        @Nullable
        @Override
        public K bundle() {
            return entity;
        }

        @Nullable
        public Builder<?> observe(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOCrash {
            setter = sketch.setter(
                ++index, alias
            );

            if (setter == null) {
                return null;
            }

            // specified coder
            Coder<?> coder = setter.getCoder();

            if (coder != null) {
                return coder.getBuilder(
                    setter.getType()
                );
            }

            Spare<?> spare;
            Class<?> klass = setter.getKlass();

            // lookup
            if (klass == null) {
                // specified spare
                spare = supplier.lookup(space);

                // skip if null
                if (spare != null) {
                    return spare.getBuilder(
                        setter.getType()
                    );
                }
            } else {
                // specified spare
                spare = supplier.embed(klass);

                // skip if null
                if (spare != null) {
                    return spare.getBuilder(
                        setter.getType()
                    );
                }

                // specified spare
                spare = supplier.lookup(space);

                // skip if null
                if (spare != null &&
                    spare.accept(klass)) {
                    return spare.getBuilder(
                        setter.getType()
                    );
                }
            }

            return null;
        }

        public void dispose(
            @Nullable Object value
        ) throws IOCrash {
            setter.onAccept(
                entity, value
            );
        }

        @Override
        public void dispose(
            @NotNull Builder<?> child
        ) throws IOCrash {
            setter.onAccept(
                entity, child.bundle()
            );
        }

        @Override
        public void close() {
            entity = null;
            index = -1;
            sketch = null;
            setter = null;
        }
    }
}
