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

import java.nio.CharBuffer;

import static plus.kat.stream.Toolkit.Streams.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class CharBufferFlow extends TransferFlow {

    private char[] buffer;
    private CharBuffer source;

    /**
     * Constructs this flow for the specified text
     *
     * @throws NullPointerException If the specified text is null
     */
    public CharBufferFlow(
        @NotNull CharBuffer text
    ) {
        if (text != null) {
            source = text;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public int load() {
        CharBuffer in = source;
        int m = in.limit(),
            n = in.position();

        int size = m - n;
        if (size <= 0) {
            return limit = -1;
        }

        char[] ch = buffer;
        if (ch == null) {
            value = STREAMS.apply(2048);
            buffer = ch = new char[256];
        }

        if (size > ch.length) {
            size = ch.length;
        }
        in.get(
            ch, 0, size
        );
        return load(
            ch, 0, 0, size
        );
    }

    @Override
    public void close() {
        STREAMS.store(value);
        limit = -1;
        value = null;
        buffer = null;
        source = null;
    }
}
