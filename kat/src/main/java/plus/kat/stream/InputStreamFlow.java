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

import plus.kat.*;
import plus.kat.actor.*;

import java.io.IOException;
import java.io.InputStream;

import static plus.kat.stream.Toolkit.Streams.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class InputStreamFlow extends Flow {

    private InputStream source;

    /**
     * Constructs this flow where
     * calling {@link InputStream#close()} has no effect
     * <p>
     * For example
     * <pre>{@code
     *  try (InputStream stream = ...) {
     *     Flow flow = new InputStreamFlow(stream);
     *  }
     * }</pre>
     *
     * @throws NullPointerException If the specified text is null
     */
    public InputStreamFlow(
        @NotNull InputStream text
    ) {
        if (text != null) {
            source = text;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public int load()
        throws IOException {
        byte[] buf = value;
        InputStream in = source;

        index = 0;
        if (buf != null) {
            return limit = in.read(buf);
        }

        int m = in.available();
        if (m < 1 || m > 1023) {
            return limit = in.read(
                value = STREAMS.apply(2048)
            );
        } else if (m > 511) {
            return limit = in.read(
                value = new byte[256]
            );
        } else {
            return limit = in.read(
                value = new byte[Math.min(256, m)]
            );
        }
    }

    @Override
    public void close() {
        STREAMS.store(value);
        limit = -1;
        value = null;

        source = null;
        // Don't call InputStream#close,
        // waiting for the user to call it
    }
}
