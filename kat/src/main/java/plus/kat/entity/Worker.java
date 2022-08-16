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
     * @see Workman
     */
    @Nullable
    default Target target(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return null;
    }

    /**
     * @param alias the alias of getter
     * @see Workman
     * @since 0.0.3
     */
    @Nullable
    default Getter<K, ?> getter(
        @NotNull Object alias
    ) {
        return null;
    }

    /**
     * @param alias the alias of getter
     * @see Workman
     * @since 0.0.3
     */
    @Nullable
    default Getter<K, ?> getter(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return null;
    }

    /**
     * @param alias the alias of setter
     * @see Workman
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
     * @see Workman
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
     * @since 0.0.3
     */
    class Builder2<K> extends Builder$<K> {

        protected K entity;
        protected int index;

        protected int count;
        protected Object[] data;

        protected int range;
        protected Class<?> owner;

        protected Target target;
        protected Setter<K, ?> setter;

        protected Cache<K> cache;
        protected Worker<K> worker;

        public Builder2(
            @NotNull Worker<K> worker,
            @NotNull Object[] data
        ) {
            this(
                worker, data,
                data.length, null
            );
        }

        public Builder2(
            @NotNull Worker<K> worker,
            @NotNull Object[] data,
            @NotNull int range,
            @Nullable Class<?> owner
        ) {
            this.data = data;
            this.owner = owner;
            this.range = range;
            this.worker = worker;
        }

        @Override
        public void onCreate(
            @NotNull Alias alias
        ) throws Crash, IOException {
            Class<?> o = owner;
            if (o != null) {
                Object res = getParent().getResult();
                if (res == null) {
                    throw new UnexpectedCrash(
                        "Unexpectedly, getParent().getResult() is null"
                    );
                } else {
                    if (o.isInstance(res)) {
                        data[count++] = res;
                        if (range == 1) {
                            onApply();
                        }
                    } else {
                        throw new UnexpectedCrash(
                            "Unexpectedly, getParent().getResult() is not " + o
                        );
                    }
                }
            }
        }

        @Override
        public void onAccept(
            @NotNull Target tag,
            @NotNull Object value
        ) throws IOException {
            if (entity != null) {
                setter.onAccept(
                    entity, value
                );
            } else if (target == null) {
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
                int r = range;
                int i = target.getIndex();
                if (i < r) {
                    target = null;
                    if (data[i] != null) {
                        data[i] = value;
                    } else {
                        data[i] = value;
                        if (r == ++count) try {
                            onApply();
                        } catch (Crash e) {
                            throw new IOCrash(e);
                        }
                    }
                } else {
                    throw new UnexpectedCrash(
                        "Unexpectedly, (" + i + ") out of range(" + r + ")"
                    );
                }
            }
        }

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) throws IOException {
            onAccept(
                null, child.getResult()
            );
        }

        @Override
        public void onAccept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOException {
            int i = index++;
            if (entity != null) {
                setter = worker.setter(
                    i, alias
                );
                if (setter != null) {
                    onAccept(
                        space, value, setter
                    );
                }
            } else {
                target = worker.target(
                    i, alias
                );
                if (target != null) {
                    onAccept(
                        space, value, target
                    );
                } else {
                    setter = worker.setter(
                        i, alias
                    );
                    if (setter != null) {
                        onAccept(
                            space, value, setter
                        );
                    }
                }
            }
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOException {
            int i = index++;
            if (entity != null) {
                setter = worker.setter(
                    i, alias
                );
                if (setter != null) {
                    return getBuilder(
                        space, setter
                    );
                }
            } else {
                target = worker.target(
                    i, alias
                );
                if (target != null) {
                    return getBuilder(
                        space, target
                    );
                } else {
                    setter = worker.setter(
                        i, alias
                    );
                    if (setter != null) {
                        return getBuilder(
                            space, setter
                        );
                    }
                }
            }

            return null;
        }

        @Override
        public K getResult() {
            if (entity == null &&
                range != data.length) {
                try {
                    onApply();
                } catch (Exception e) {
                    return null;
                }
            }
            return entity;
        }

        @Override
        public void onDestroy() {
            index = 0;
            count = 0;
            range = 0;

            data = null;
            cache = null;

            setter = null;
            target = null;
            entity = null;
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
         * Apply for it
         */
        protected void onApply()
            throws Crash {
            entity = worker.apply(
                getAlias(), data
            );
            while (cache != null) {
                cache.setter.onAccept(
                    entity, cache.value
                );
                cache = cache.next;
            }
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
