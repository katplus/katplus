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

import plus.kat.crash.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ByteReader implements Reader {

    private int index;
    private int offset;
    private byte[] value;

    /**
     * @throws NullPointerException If the specified {@code data} is null
     */
    public ByteReader(
        @NotNull byte[] data
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        this.value = data;
        this.offset = data.length;
    }

    /**
     * @throws NullPointerException      If the specified {@code data} is null
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     */
    public ByteReader(
        @NotNull byte[] data, int index, int length
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        int offset = index + length;
        if (index < 0 ||
            offset <= index ||
            offset > data.length
        ) {
            throw new IndexOutOfBoundsException();
        }

        this.value = data;
        this.index = index;
        this.offset = offset;
    }

    @Override
    public boolean also() {
        return index < offset;
    }

    @Override
    public byte read() {
        return value[index++];
    }

    @Override
    public byte next() throws IOException {
        if (index < offset) {
            return value[index++];
        }

        throw new ReaderCrash(
            "Unexpectedly, no readable byte"
        );
    }

    @Override
    public void close() {
        value = null;
        offset = 0;
    }
}
