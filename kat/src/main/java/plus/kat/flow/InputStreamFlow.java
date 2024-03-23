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

import plus.kat.*;
import plus.kat.actor.*;

import java.io.IOException;
import java.io.InputStream;

import static plus.kat.flow.Stream.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class InputStreamFlow extends Flow {

    private InputStream flow;

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
            flow = text;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public int load()
        throws IOException {
        int size;
        if (v != null) {
            size = flow.read(v);
        } else {
            int m = flow.available();
            if (m < 1 || m > 1023) {
                size = flow.read(
                    v = BUCKET.apply(
                        null, 0, 2048
                    )
                );
            } else if (m > 511) {
                size = flow.read(
                    v = new byte[256]
                );
            } else {
                size = flow.read(
                    v = new byte[Math.min(256, m)]
                );
            }
        }
        if (size > 0) {
            i = 0;
        }
        return l = size;
    }

    @Override
    public void close() {
        BUCKET.store(v);
        l = -1;
        v = null;
        flow = null;
        // Don't call InputStream#close,
        // waiting for the user to call it
    }
}
