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

import java.io.Reader;
import java.io.IOException;

import static plus.kat.stream.Stream.Buffer.INS;

/**
 * @author kraity
 * @since 0.0.6
 */
public class ReaderPaper extends TransferPaper {

    protected char[] buffer;
    protected Reader source;

    /**
     * Constructs a {@link Paper} where
     * calling {@link Reader#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *   try (Reader reader = ...) {
     *      Paper paper = new ReaderPaper(stream);
     *   }
     * }</pre>
     */
    public ReaderPaper(
        @NotNull Reader data
    ) {
        if (data != null) {
            source = data;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    protected int load()
        throws IOException {
        char[] ch = buffer;
        Reader in = source;

        if (ch != null) {
            return load(
                ch, 0, 0, in.read(ch)
            );
        }

        int n = in.read();
        if (n < 0) {
            return -1;
        }

        int size = buflen;
        queue = INS.alloc(
            size > 31 ? size : 1024
        );
        buffer = ch = new char[256];

        ch[0] = (char) n;
        size = in.read(
            ch, 1, 255
        );
        return load(
            ch, 0, 0, size > 0 ? size + 1 : size
        );
    }

    @Override
    public void close() {
        INS.join(queue);
        limit = -1;
        queue = null;
        buffer = null;
        source = null;
    }
}
