package plus.kat.entity;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Clutter<K> extends Coder<K> {
    /**
     * @param alias the alias of entity
     * @throws Crash If a failure occurs
     */
    @Nullable
    K apply(
        @NotNull Alias alias
    ) throws Crash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void accept(
        @NotNull K entity,
        @NotNull int index,
        @NotNull Space space,
        @NotNull Alias alias,
        @NotNull Value value
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    default Builder<?> channel(
        @NotNull K entity,
        @NotNull int index,
        @NotNull Space space,
        @NotNull Alias alias,
        @NotNull Supplier supplier
    ) throws IOCrash {
        return null;
    }

    /**
     * @throws IOCrash If an I/O error occurs
     */
    default void receive(
        @NotNull K entity,
        @NotNull int index,
        @NotNull Builder<?> child
    ) throws IOCrash {
        // nothing
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
     * @since 0.0.1
     */
    class Builder0<K> extends Builder<K> {

        protected K entity;
        protected int index = -1;
        protected Clutter<K> clutter;

        /**
         * default
         */
        public Builder0(
            @NotNull Clutter<K> clutter
        ) {
            this.clutter = clutter;
        }

        @Override
        public void create(
            @NotNull Alias alias
        ) throws Crash, IOCrash {
            // get an instance
            entity = clutter.apply(alias);

            // check this instance
            if (entity == null) {
                throw new Crash(
                    "Entity created through Clutter is null", false
                );
            }
        }

        @Override
        public void accept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
            clutter.accept(
                entity, ++index,
                space, alias, value
            );
        }

        @Nullable
        @Override
        public Builder<?> observe(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOCrash {
            return clutter.channel(
                entity, ++index,
                space, alias, supplier
            );
        }

        @Override
        public void dispose(
            @NotNull Builder<?> child
        ) throws IOCrash {
            clutter.receive(
                entity, index, child
            );
        }

        @Nullable
        @Override
        public K bundle() {
            return entity;
        }

        @Override
        public void close() {
            entity = null;
            index = -1;
            clutter = null;
        }
    }
}
