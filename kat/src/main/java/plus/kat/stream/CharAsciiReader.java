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

import plus.kat.kernel.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class CharAsciiReader extends CharReader {
    /**
     * @throws NullPointerException If the data is null
     */
    public CharAsciiReader(
        @NotNull CharSequence data
    ) {
        super(data);
    }

    /**
     * @throws IndexOutOfBoundsException If the index and the length are out of range
     */
    public CharAsciiReader(
        @NotNull CharSequence data, int index, int length
    ) {
        super(data, index, length);
    }

    @Override
    protected int load() {
        int cap = end - begin;
        if (cap <= 0) {
            return -1;
        }

        byte[] tmp = cache;
        if (tmp == null) {
            int r = range;
            if (r == 0) {
                r = 128;
            }
            if (cap < r) {
                r = cap;
            }
            cache = tmp = new byte[r];
        }

        if (cap > tmp.length) {
            cap = tmp.length;
        }

        if (value instanceof String) {
            String s = (String) value;
            s.getBytes(
                begin, begin += cap, tmp, 0
            );
        } else if (value instanceof Chain) {
            Chain c = (Chain) value;
            cap = c.getBytes(
                begin, tmp, 0, cap
            );
            if (cap > 0) {
                begin += cap;
            }
        } else {
            for (int i = 0; i < cap; i++) {
                tmp[i] = (byte) value.charAt(begin++);
            }
        }

        return cap;
    }
}
