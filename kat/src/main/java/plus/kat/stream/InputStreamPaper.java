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

import java.io.IOException;
import java.io.InputStream;

import static plus.kat.stream.Stream.Buffer.INS;

/**
 * @author kraity
 * @since 0.0.1
 */
public class InputStreamPaper extends AbstractPaper {

    protected InputStream source;

    /**
     * Constructs a {@link Paper} where
     * calling {@link InputStream#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *   try (InputStream stream = ...) {
     *      Paper paper = new InputStreamPaper(stream);
     *   }
     * }</pre>
     */
    public InputStreamPaper(
        @NotNull InputStream data
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
        byte[] buf = queue;
        InputStream in = source;

        if (buf != null) {
            return in.read(buf);
        }

        int m, n = buflen;
        if (n > 31) {
            return in.read(
                queue = INS.alloc(n)
            );
        }

        if ((m = in.available()) > 0) {
            if (m > 1024) {
                return in.read(
                    queue = INS.alloc(1024)
                );
            } else if (m > 512) {
                return in.read(
                    queue = new byte[256]
                );
            } else {
                return in.read(
                    queue = new byte[Math.min(256, m)]
                );
            }
        } else {
            if ((n = in.read()) < 0) {
                return -1;
            }

            m = in.available();
            if (m <= 0 || m > 1023) {
                buf = INS.alloc(1024);
            } else if (m > 511) {
                buf = new byte[256];
            } else {
                buf = new byte[Math.min(256, m + 1)];
            }

            (queue = buf)[0] = (byte) n;
            int size = in.read(
                buf, 1, buf.length - 1
            );
            return size > 0 ? size + 1 : size;
        }
    }

    @Override
    public void close() {
        INS.join(queue);
        limit = -1;
        queue = null;
        source = null;
    }
}