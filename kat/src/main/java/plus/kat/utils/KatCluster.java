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
package plus.kat.utils;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import java.util.Arrays;

/**
 * @author kraity
 * @since 0.0.4
 */
@SuppressWarnings("unchecked")
public abstract class KatCluster<T> {

    protected final int size;
    protected final boolean block;

    protected int grow;
    protected volatile int count;
    protected final Object[] group;

    /**
     * @param size the specified size of pool
     */
    public KatCluster(
        int size
    ) {
        this(size, true);
    }

    /**
     * @param size  the specified size of pool
     * @param block the specified blocked mode
     */
    public KatCluster(
        int size, boolean block
    ) {
        this.size = size;
        this.block = block;
        this.group = new Object[size];
    }

    /**
     * Returns a new instance
     *
     * @return {@link T}, it is not null
     */
    @NotNull
    public abstract T make();

    /**
     * Releases the {@code target}
     *
     * @param target the specified {@link T}
     */
    public abstract boolean stop(
        @NotNull T target
    );

    /**
     * Verify lock the {@code target}
     *
     * @param target the specified {@link T}
     */
    public abstract boolean lock(
        @NotNull T target
    );

    /**
     * Verify unlock the {@code target}
     *
     * @param target the specified {@link T}
     */
    public abstract boolean unlock(
        @NotNull T target
    );

    /**
     * Returns a borrowed instance
     *
     * @return {@link T}, it is not null
     */
    @NotNull
    public T borrow() {
        synchronized (this) {
            while (true) {
                if (count != 0) {
                    T f = (T)
                        group[--count];
                    group[count] = null;

                    if (f != null) {
                        if (lock(f)) {
                            return f;
                        } else {
                            continue;
                        }
                    }
                }

                if (!block) {
                    break;
                }

                if (grow < size) {
                    grow++;
                    break;
                } else try {
                    wait(1000);
                } catch (Throwable e) {
                    break;
                }
            }
        }

        return make();
    }

    /**
     * Returns a specified instance
     *
     * @param it the specified {@link T}, maybe is null
     */
    public boolean retreat(
        @Nullable T it
    ) {
        if (it == null) {
            return false;
        }

        if (unlock(it)) {
            synchronized (this) {
                if (count < size) {
                    group[count++] = it;
                    if (block) {
                        notify();
                    }
                    return true;
                }
            }
        }

        return stop(it);
    }

    /**
     * Close this {@link KatCluster}
     */
    public void close() {
        grow = 0;
        count = 0;
        Arrays.fill(group, null);
    }
}
