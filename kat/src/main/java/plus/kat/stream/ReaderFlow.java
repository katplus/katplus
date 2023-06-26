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

import java.io.Reader;
import java.io.IOException;

import static plus.kat.stream.Toolkit.Streams.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class ReaderFlow extends TransferFlow {

    private char[] buffer;
    private Reader source;

    /**
     * Constructs this flow where
     * calling {@link Reader#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *  try (Reader reader = ...) {
     *     Flow flow = new ReaderFlow(stream);
     *  }
     * }</pre>
     *
     * @throws NullPointerException If the specified text is null
     */
    public ReaderFlow(
        @NotNull Reader data
    ) {
        if (data != null) {
            source = data;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public int load()
        throws IOException {
        char[] ch = buffer;
        Reader in = source;

        if (ch != null) {
            return limit = load(
                ch, 0, 0, in.read(ch)
            );
        }

        int n = in.read();
        if (n < 0) {
            return limit = -1;
        }

        value = STREAMS.apply(2048);
        buffer = ch = new char[256];

        ch[0] = (char) n;
        int size = in.read(ch, 1, 255);
        return load(
            ch, 0, 0, size > 0 ? size + 1 : 1
        );
    }

    @Override
    public void close() {
        STREAMS.store(value);
        limit = -1;
        value = null;
        buffer = null;

        source = null;
        // Don't call Reader#close,
        // waiting for the user to call it
    }
}
