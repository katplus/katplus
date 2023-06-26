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
package plus.kat.chain;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Alias extends Value {
    /**
     * Constructs an alias with the size
     *
     * @param size the specified capacity
     */
    public Alias(
        int size
    ) {
        super(size);
    }

    /**
     * Constructs an alias with the flow
     *
     * @param flow the specified buffer stream
     */
    public Alias(
        byte[] flow
    ) {
        super(flow);
    }

    /**
     * Constructs an alias with the flow and size
     *
     * @param flow the specified buffer stream
     * @param size the specified size of stream
     */
    public Alias(
        int size,
        byte[] flow
    ) {
        super(size, flow);
    }

    /**
     * Sets the specified length of this {@link Alias}
     *
     * @param i the specified length
     * @return this {@link Alias} itself
     * @throws IndexOutOfBoundsException If index is out of bounds
     */
    public Alias slip(int i) {
        if (i == 0) {
            size = 0;
            hash = 0;
        } else {
            if (0 < i && i <= value.length) {
                size = i;
                hash = 0;
            } else {
                throw new IndexOutOfBoundsException(
                    "Index<" + i + "> is out of bounds"
                );
            }
        }
        return this;
    }

    /**
     * Sets the specified length of this {@link Value}
     *
     * @param i the specified length
     * @param v the specified default value
     * @return this {@link Value} itself
     * @throws IndexOutOfBoundsException If index is out of bounds
     */
    public Value slip(int i, byte v) {
        if (i == 0) {
            size = 1;
            hash = value[0] = v;
        } else {
            if (0 < i && i <= value.length) {
                size = i;
                hash = 0;
            } else {
                throw new IndexOutOfBoundsException(
                    "Index<" + i + "> is out of bounds"
                );
            }
        }
        return this;
    }
}
