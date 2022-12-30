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

/**
 * @author kraity
 * @since 0.0.6
 */
public abstract class TransferPaper extends AbstractPaper {
    /**
     * Returns the offset index of the {@link #queue}
     *
     * @param buffer the specified buffer
     * @param from   the index of {@link #queue}
     * @param index  the start index, inclusive
     * @param offset the offset index, exclusive
     * @throws IndexOutOfBoundsException If the queue capacity is insufficient
     */
    protected int load(
        byte[] buffer, int from, int index, int offset
    ) {
        int length = offset - index;
        System.arraycopy(
            buffer, index, queue, from, length
        );
        return from + length;
    }

    /**
     * Returns the offset index of the {@link #queue}
     *
     * @param buffer the specified buffer
     * @param from   the index of {@link #queue}
     * @param index  the start index, inclusive
     * @param offset the offset index, exclusive
     * @throws IndexOutOfBoundsException If the queue capacity is insufficient
     */
    protected int load(
        char[] buffer, int from, int index, int offset
    ) {
        byte[] it = queue;
        while (index < offset) {
            char code = buffer[index++];

            // U+0000 ~ U+007F
            if (code < 0x80) {
                it[from++] = (byte) code;
            }

            // U+0080 ~ U+07FF
            else if (code < 0x800) {
                it[from++] = (byte) (code >> 6 | 0xC0);
                it[from++] = (byte) (code & 0x3F | 0x80);
            }

            // U+10000 ~ U+10FFFF
            else if (0xD7FF < code && code < 0xE000) {
                if (code > 0xDBFF ||
                    index == offset) {
                    it[from++] = '?';
                    continue;
                }

                char arch = buffer[index++];
                if (arch < 0xDC00 ||
                    arch > 0xDFFF) {
                    it[from++] = '?';
                    continue;
                }

                int hi = code - 0xD7C0;
                int lo = arch - 0xDC00;

                it[from++] = (byte) (hi >> 8 | 0xF0);
                it[from++] = (byte) (hi >> 2 & 0x3F | 0x80);
                it[from++] = (byte) (lo >> 6 | hi << 4 & 0x30 | 0x80);
                it[from++] = (byte) (lo & 0x3F | 0x80);
            }

            // U+0800 ~ U+FFFF
            else {
                it[from++] = (byte) (code >> 12 | 0xE0);
                it[from++] = (byte) (code >> 6 & 0x3F | 0x80);
                it[from++] = (byte) (code & 0x3F | 0x80);
            }
        }

        return from;
    }

    /**
     * Returns the offset index of the {@link #queue}
     *
     * @param buffer the specified buffer
     * @param from   the index of {@link #queue}
     * @param index  the start index, inclusive
     * @param offset the offset index, exclusive
     * @throws IndexOutOfBoundsException If the queue capacity is insufficient
     */
    protected int load(
        CharSequence buffer, int from, int index, int offset
    ) {
        byte[] it = queue;
        while (index < offset) {
            char code = buffer.charAt(index++);

            // U+0000 ~ U+007F
            if (code < 0x80) {
                it[from++] = (byte) code;
            }

            // U+0080 ~ U+07FF
            else if (code < 0x800) {
                it[from++] = (byte) (code >> 6 | 0xC0);
                it[from++] = (byte) (code & 0x3F | 0x80);
            }

            // U+10000 ~ U+10FFFF
            else if (0xD7FF < code && code < 0xE000) {
                if (code > 0xDBFF ||
                    index == offset) {
                    it[from++] = '?';
                    continue;
                }

                char arch = buffer.charAt(index++);
                if (arch < 0xDC00 ||
                    arch > 0xDFFF) {
                    it[from++] = '?';
                    continue;
                }

                int hi = code - 0xD7C0;
                int lo = arch - 0xDC00;

                it[from++] = (byte) (hi >> 8 | 0xF0);
                it[from++] = (byte) (hi >> 2 & 0x3F | 0x80);
                it[from++] = (byte) (lo >> 6 | hi << 4 & 0x30 | 0x80);
                it[from++] = (byte) (lo & 0x3F | 0x80);
            }

            // U+0800 ~ U+FFFF
            else {
                it[from++] = (byte) (code >> 12 | 0xE0);
                it[from++] = (byte) (code >> 6 & 0x3F | 0x80);
                it[from++] = (byte) (code & 0x3F | 0x80);
            }
        }

        return from;
    }
}
