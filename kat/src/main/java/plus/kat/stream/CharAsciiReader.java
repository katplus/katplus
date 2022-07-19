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

/**
 * @author kraity
 * @since 0.0.1
 */
public class CharAsciiReader implements Reader {

    private int index;
    private int offset;
    private CharSequence value;

    public CharAsciiReader(
        @NotNull CharSequence data
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        this.value = data;
        this.offset = data.length();
    }

    /**
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     */
    public CharAsciiReader(
        @NotNull CharSequence data, int index, int length
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        int offset = index + length;
        if (index < 0 ||
            offset <= index ||
            offset > data.length()
        ) {
            throw new IndexOutOfBoundsException();
        }

        this.value = data;
        this.index = index;
        this.offset = offset;
    }

    @Override
    public byte read() {
        return (byte) value.charAt(index++);
    }

    @Override
    public boolean also() {
        return index < offset;
    }

    @Override
    public void close() {
        offset = 0;
        value = null;
    }
}
