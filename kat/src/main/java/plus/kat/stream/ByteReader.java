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
package plus.kat.stream;

import plus.kat.anno.NotNull;

import plus.kat.chain.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ByteReader implements Reader {

    private int index;
    private int length;
    private byte[] value;

    /**
     * @throws NullPointerException If the specified {@code data} is null
     */
    public ByteReader(
        @NotNull byte[] data
    ) {
        this.value = data;
        this.length = data.length;
    }

    /**
     * @throws NullPointerException      If the specified {@code data} is null
     * @throws IndexOutOfBoundsException If the index and the range are out of range
     */
    public ByteReader(
        @NotNull Paper data
    ) {
        this(data.getSource(), 0, data.length());
    }

    /**
     * @throws NullPointerException      If the specified {@code data} is null
     * @throws IndexOutOfBoundsException If the index and the range are out of range
     */
    public ByteReader(
        @NotNull byte[] data, int index, int length
    ) {
        if (index < 0 ||
            index >= data.length ||
            length <= index ||
            length > data.length
        ) {
            throw new IndexOutOfBoundsException();
        }

        this.value = data;
        this.index = index;
        this.length = length;
    }

    @Override
    public byte read() {
        return value[index++];
    }

    @Override
    public boolean also() {
        return index < length;
    }

    @Override
    public void close() {
        value = null;
        length = 0;
    }
}
