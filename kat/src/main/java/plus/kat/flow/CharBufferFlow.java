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

import java.nio.CharBuffer;

import static plus.kat.flow.Stream.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class CharBufferFlow extends TransferFlow {

    private char[] temp;
    private CharBuffer flow;

    /**
     * Constructs this flow for the specified text
     *
     * @throws NullPointerException If the specified text is null
     */
    public CharBufferFlow(
        @NotNull CharBuffer text
    ) {
        if (text != null) {
            flow = text;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public int load() {
        int m = flow.limit(),
            n = flow.position();

        int size = m - n;
        if (size <= 0) {
            return l = -1;
        }

        if (temp == null) {
            v = BUCKET.apply(
                null, 0, 2048
            );
            temp = new char[256];
        }

        if (size > temp.length) {
            size = temp.length;
        }
        flow.get(temp, 0, size);
        return load(temp, 0, 0, size);
    }

    @Override
    public void close() {
        BUCKET.store(v);
        flow = null;
        temp = null;
        super.close();
    }
}
