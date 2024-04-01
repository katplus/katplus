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
package plus.kat.flow;

import plus.kat.actor.*;

import static plus.kat.flow.Stream.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class CharFlow extends TransferFlow {

    private char[] flow;
    private int left;
    private final int right;

    /**
     * Constructs this flow for the specified text
     *
     * @throws NullPointerException If the specified text is null
     */
    public CharFlow(
        @NotNull char[] text
    ) {
        if (text == null) {
            throw new NullPointerException();
        }

        this.flow = text;
        this.right = text.length;
    }

    /**
     * Constructs this flow for the specified text
     *
     * @param index  the start index of the text
     * @param length the specified length of the text
     * @throws NullPointerException If the specified text is null
     */
    public CharFlow(
        @NotNull char[] text, int index, int length
    ) {
        if (text == null) {
            throw new NullPointerException();
        }

        int limit = index + length;
        if (limit <= text.length &&
            index >= 0 && length >= 0) {
            this.flow = text;
            this.left = index;
            this.right = limit;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public int load() {
        int size = right - left;
        if (size <= 0) {
            return l = -1;
        }

        if (v == null) {
            v = BUCKET.apply(
                null, 0, 2048
            );
        }

        int cap = v.length / 3;
        if (size > cap) size = cap;

        return load(
            flow, 0, left, left += size
        );
    }

    @Override
    public void close() {
        BUCKET.store(v);
        flow = null;
        super.close();
    }
}
