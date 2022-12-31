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

import static plus.kat.stream.Stream.Buffer.INS;

/**
 * @author kraity
 * @since 0.0.1
 */
public class CharSequencePaper extends TransferPaper {

    protected int left;
    protected final int right;
    protected CharSequence source;

    public CharSequencePaper(
        @NotNull CharSequence data
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        this.source = data;
        this.right = data.length();
    }

    public CharSequencePaper(
        @NotNull CharSequence data, int index, int length
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        int right = index + length;
        if (index < 0 ||
            right <= index ||
            right > data.length()
        ) {
            throw new IndexOutOfBoundsException();
        }

        this.source = data;
        this.left = index;
        this.right = right;
    }

    @Override
    protected int load() {
        int i = left;
        int size = right - i;

        if (size <= 0) {
            return -1;
        }

        byte[] it = queue;
        if (it == null) {
            int n = buflen;
            if (n > 31) {
                queue = it = INS.alloc(n);
            } else {
                n = size * 3;
                if (n > 1024) {
                    queue = it = INS.alloc(1024);
                } else if (n > 512) {
                    queue = it = new byte[256];
                } else {
                    queue = it = new byte[Math.min(256, n)];
                }
            }
        }

        int k = it.length / 3;
        if (size > k) size = k;

        return load(
            source, 0, i, left += size
        );
    }

    @Override
    public void close() {
        INS.join(queue);
        limit = -1;
        queue = null;
        source = null;
    }
}
