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

import java.util.concurrent.atomic.*;

/**
 * @author kraity
 * @since 0.0.6
 */
@SuppressWarnings("unchecked")
public class KatBuffer<Bean>
    extends AtomicReference<Bean> {

    private int size;
    private Object[] table;

    /**
     * Constructs an empty buffer
     */
    public KatBuffer() {
        // Nothing
    }

    /**
     * Constructs a buffer with the capacity
     *
     * @param size the specified capacity
     */
    public KatBuffer(int size) {
        if (size > 0) {
            table = new Object[size];
        } else {
            throw new IllegalArgumentException(
                "Received " + size + " is less than 1"
            );
        }
    }

    /**
     * Retrieves and removes the top of the table,
     * or returns {@code null} if the table is empty
     */
    public Bean acquire() {
        synchronized (this) {
            Object bean;
            Object[] zone = table;
            while (size != 0) {
                bean = zone[--size];
                if (bean != null) {
                    zone[size] = null;
                    return (Bean) bean;
                }
            }
        }
        return null;
    }

    /**
     * Inserts the specified bean into the table if it is possible
     * to do so immediately without violating capacity restrictions
     *
     * @param bean the specified bean to release
     */
    public boolean release(Bean bean) {
        synchronized (this) {
            Object[] zone = table;
            if (zone == null) {
                table = zone =
                    new Object[8];
            }

            if (size < zone.length) {
                zone[size++] = bean;
                return true;
            }
        }
        return false;
    }

    /**
     * Borrows a bean from the queue of {@link KatBuffer}
     */
    public Bean borrow() {
        Bean bean = getAndSet(null);
        return bean != null ? bean : acquire();
    }

    /**
     * Resumes the bean to the queue of {@link KatBuffer}
     *
     * @param bean the specified bean to resume
     */
    public boolean resume(Bean bean) {
        return compareAndSet(null, bean) || release(bean);
    }

    /**
     * Release resources related to this {@link KatBuffer}
     */
    public void close() {
        lazySet(null);
        synchronized (this) {
            Object[] zone = table;
            while (size != 0) {
                zone[--size] = null;
            }
        }
    }
}
