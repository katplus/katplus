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

import java.io.Reader;
import java.io.IOException;

import static plus.kat.flow.Stream.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class ReaderFlow extends TransferFlow {

    private char[] temp;
    private Reader flow;

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
            flow = data;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public int load()
        throws IOException {
        int size;
        if (temp != null) {
            size = flow.read(temp);
            if (size > 0) {
                return l = load(
                    temp, 0, 0, size
                );
            }
            return size;
        }

        int next = flow.read();
        if (next < 0) {
            return l = -1;
        }

        v = BUCKET.apply(
            null, 0, 2048
        );

        temp = new char[256];
        temp[0] = (char) next;

        size = flow.read(temp, 1, 255);
        return load(
            temp, 0, 0, size > 0 ? size + 1 : 1
        );
    }

    @Override
    public void close() {
        BUCKET.store(v);
        flow = null;
        temp = null;
        super.close();
        // Don't call Reader#close,
        // waiting for the user to call it
    }
}
