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

import plus.kat.actor.*;

import static plus.kat.stream.Toolkit.Streams.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class CharFlow extends TransferFlow {

    private char[] source;
    private int left;
    private final int right;

    public CharFlow(
        @NotNull char[] text
    ) {
        if (text == null) {
            throw new NullPointerException();
        }

        this.source = text;
        this.right = text.length;
    }

    public CharFlow(
        @NotNull char[] text, int index, int length
    ) {
        if (text == null) {
            throw new NullPointerException();
        }

        int right = index + length;
        if (index < 0 ||
            right <= index ||
            right > text.length
        ) {
            throw new IndexOutOfBoundsException();
        }

        this.source = text;
        this.left = index;
        this.right = right;
    }

    @Override
    public int load() {
        int i = left;
        int size = right - i;

        if (size <= 0) {
            index = 0;
            return limit = -1;
        }

        byte[] it = value;
        if (it == null) {
            value = it =
                STREAMS.apply(2048);
        }

        int v = it.length / 3;
        if (size > v) size = v;

        return load(
            source, 0, i, left += size
        );
    }

    @Override
    public void close() {
        STREAMS.store(value);
        limit = -1;
        value = null;
        source = null;
    }
}
