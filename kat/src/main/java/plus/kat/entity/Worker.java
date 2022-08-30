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

/**
 * @author kraity
 * @see Workman
 * @since 0.0.1
 */
public interface Worker<K> extends Spare<K>, Maker<K> {
    /**
     * If this {@link Worker} can create an instance,
     * it returns it, otherwise it will return {@code null}
     *
     * @return {@link K} or {@code null}
     * @since 0.0.3
     */
    @Nullable
    @Override
    default K apply() {
        try {
            return apply(
                Alias.EMPTY
            );
        } catch (Exception e) {
            return null;
        }
    }

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
        throw new Crash(
            "Unsupported method"
        );
    }

    /**
     * @param alias the alias of target
     * @see Workman
     * @since 0.0.4
     */
    @Nullable
    default Target tag(
        @NotNull Object alias
    ) {
        return null;
    }

    /**
     * @param alias the alias of target
     * @see Workman
     * @since 0.0.4
     */
    @Nullable
    default Target tag(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return null;
    }

    /**
     * @param alias the alias of setter
     * @see Workman
     * @since 0.0.4
     */
    @Nullable
    default Setter<K, ?> set(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return null;
    }

    /**
     * @param alias the alias of getter
     * @see Workman
     * @since 0.0.4
     */
    @Nullable
    default Getter<K, ?> get(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return null;
    }

    /**
     * Returns a {@link Builder} of {@link K}
     *
     * @see Builder0
     * @see Builder1
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

        /**
         * Prepare before parsing
         *
         * @throws IOException If an I/O error occurs
         */
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

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Target tag,
            @NotNull Object value
        ) throws IOException {
            setter.call(
                entity, value
            );
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            setter.call(
                entity, child.getResult()
            );
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            setter = worker.set(
                index++, alias
            );

            if (setter != null) {
                onAccept(
                    space, value, setter
                );
            }
        }

        /**
         * Create a branch of this {@link Builder}
         *
         * @throws IOException If an I/O error occurs
         */
        @Nullable
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            setter = worker.set(
                index++, alias
            );

            if (setter == null) {
                return null;
            }

            return getBuilder(space, setter);
        }

        /**
         * Returns the result of building {@link K}
         */
        @Nullable
        @Override
        public K getResult() {
            return entity;
        }

        /**
         * Close the resources of this {@link Builder}
         */
        @Override
        public void onDestroy() {
            index = 0;
            setter = null;
            entity = null;
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

        /**
         * Prepare before parsing
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOException {
            // Nothing
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Target tag,
            @NotNull Object value
        ) throws IOException {
            int i = tag.getIndex();
            data[i] = value;
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            int i = target.getIndex();
            data[i] = child.getResult();
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            target = worker.tag(
                index++, alias
            );

            if (target != null) {
                onAccept(
                    space, value, target
                );
            }
        }

        /**
         * Create a branch of this {@link Builder}
         *
         * @throws IOException If an I/O error occurs
         */
        @Nullable
        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            target = worker.tag(
                index++, alias
            );

            if (target == null) {
                return null;
            }

            return getBuilder(space, target);
        }

        /**
         * Returns the result of building {@link K}
         *
         * @throws IOException If a packaging error or IO error
         */
        @Nullable
        @Override
        public K getResult()
            throws IOException {
            if (entity == null) {
                try {
                    entity = worker.apply(
                        getAlias(), data
                    );
                } catch (Crash e) {
                    throw new UnexpectedCrash(
                        "Error creating entity", e
                    );
                }
            }
            return entity;
        }

        /**
         * Close the resources of this {@link Builder}
         */
        @Override
        public void onDestroy() {
            index = 0;
            data = null;
            target = null;
            entity = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.3
     */
    class Builder2<K> extends Builder$<K> {

        protected K entity;
        protected int index;

        protected Object[] data;
        protected Class<?> master;

        protected Target target;
        protected Setter<K, ?> setter;

        protected Cache<K> cache;
        protected Worker<K> worker;

        public Builder2(
            @NotNull Worker<K> worker,
            @NotNull Object[] data
        ) {
            this(
                worker, data, null
            );
        }

        public Builder2(
            @NotNull Worker<K> worker,
            @NotNull Object[] data,
            @Nullable Class<?> master
        ) {
            this.data = data;
            this.master = master;
            this.worker = worker;
        }

        /**
         * Prepare before parsing
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOException {
            Class<?> o = master;
            if (o != null) {
                Object res = getParent().getResult();
                if (res == null) {
                    throw new UnexpectedCrash(
                        "Unexpectedly, the parent is is null"
                    );
                } else {
                    if (o.isInstance(res)) {
                        data[0] = res;
                    } else {
                        throw new UnexpectedCrash(
                            "Unexpectedly, the parent is not " + o
                        );
                    }
                }
            }
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Target tag,
            @NotNull Object value
        ) throws IOException {
            if (target == null) {
                Cache<K> c = new Cache<>();
                c.value = value;
                c.setter = setter;

                setter = null;
                if (cache == null) {
                    cache = c;
                } else {
                    cache.next = c;
                }
            } else {
                int r = data.length;
                int i = target.getIndex();
                if (i < r) {
                    target = null;
                    data[i] = value;
                } else {
                    throw new UnexpectedCrash(
                        "Unexpectedly, (" + i + ") out of range(" + r + ")"
                    );
                }
            }
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            onAccept(
                null, child.getResult()
            );
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            int i = index++;
            target = worker.tag(
                i, alias
            );
            if (target != null) {
                onAccept(
                    space, value, target
                );
            } else {
                setter = worker.set(
                    i, alias
                );
                if (setter != null) {
                    onAccept(
                        space, value, setter
                    );
                }
            }
        }

        /**
         * Create a branch of this {@link Builder}
         *
         * @throws IOException If an I/O error occurs
         */
        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            int i = index++;
            target = worker.tag(
                i, alias
            );
            if (target != null) {
                return getBuilder(
                    space, target
                );
            } else {
                setter = worker.set(
                    i, alias
                );
                if (setter != null) {
                    return getBuilder(
                        space, setter
                    );
                }
            }

            return null;
        }

        /**
         * @author kraity
         * @since 0.0.3
         */
        static class Cache<K> {
            Object value;
            Cache<K> next;
            Setter<K, ?> setter;
        }

        /**
         * Returns the result of building {@link K}
         *
         * @throws IOException If a packaging error or IO error
         */
        @Override
        public K getResult()
            throws IOException {
            if (entity == null) {
                try {
                    entity = worker.apply(
                        getAlias(), data
                    );
                } catch (Crash e) {
                    throw new UnexpectedCrash(
                        "Error creating entity", e
                    );
                }
            }

            while (cache != null) {
                cache.setter.call(
                    entity, cache.value
                );
                cache = cache.next;
            }
            return entity;
        }

        /**
         * Close the resources of this {@link Builder}
         */
        @Override
        public void onDestroy() {
            index = 0;
            data = null;
            cache = null;
            master = null;
            setter = null;
            target = null;
            entity = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    abstract class Builder$<K> extends Builder<K> {
        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        public void onAccept(
            @NotNull Target tag,
            @Nullable Object value
        ) throws IOException {
            // Nothing
        }

        /**
         * Receive according to requirements and then parse
         *
         * @throws IOException If an I/O error occurs
         */
        public void onAccept(
            @NotNull Space space,
            @NotNull Value value,
            @NotNull Target target
        ) throws IOException {
            // specified coder
            Coder<?> coder = target.getCoder();

            if (coder != null) {
                value.setType(
                    target.getRawType()
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
                        target.getRawType()
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
                        target.getRawType()
                    );
                    onAccept(
                        target, spare.read(
                            event, value
                        )
                    );
                    return;
                }

                // specified spare
                spare = supplier.lookup(space, klass);

                // skip if null
                if (spare != null &&
                    spare.accept(klass)) {
                    value.setType(
                        target.getRawType()
                    );
                    onAccept(
                        target, spare.read(
                            event, value
                        )
                    );
                }
            }
        }

        /**
         * Create a branch of this {@link Builder}
         *
         * @throws IOException If an I/O error occurs
         */
        @Nullable
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Target target
        ) throws IOException {
            // specified coder
            Coder<?> coder = target.getCoder();

            if (coder != null) {
                return coder.getBuilder(
                    target.getRawType()
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
                        target.getRawType()
                    );
                }
            } else {
                // specified spare
                spare = supplier.lookup(klass);

                // skip if null
                if (spare != null) {
                    return spare.getBuilder(
                        target.getRawType()
                    );
                }

                // specified spare
                spare = supplier.lookup(space, klass);

                // skip if null
                if (spare != null &&
                    spare.accept(klass)) {
                    return spare.getBuilder(
                        target.getRawType()
                    );
                }
            }

            return null;
        }
    }
}
