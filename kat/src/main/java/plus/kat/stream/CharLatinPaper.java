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
public class CharLatinPaper extends CharPaper {

    public CharLatinPaper(
        @NotNull CharSequence data
    ) {
        super(data);
    }

    public CharLatinPaper(
        @NotNull CharSequence data, int index, int length
    ) {
        super(data, index, length);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected int load() {
        int size = end - start;
        if (size <= 0) {
            return -1;
        }

        byte[] buf = queue;
        if (buf == null) {
            int l = buflen;
            if (l == 0) {
                l = 128;
            }
            if (size < l) {
                l = size;
            }
            queue = buf = new byte[l];
        }

        if (size > buf.length) {
            size = buf.length;
        }

        CharSequence ch = source;
        if (ch instanceof String) {
            String s = (String) ch;
            s.getBytes(
                start, start += size, buf, 0
            );
        } else {
            for (int i = 0; i < size; i++) {
                buf[i] = (byte) ch.charAt(start++);
            }
        }

        return size;
    }
}
