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
public class InputStreamReader extends AbstractReader {

    private InputStream value;

    public InputStreamReader(
        @NotNull InputStream data
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        this.value = data;
    }

    @Override
    protected int load()
        throws IOException {
        byte[] buf = cache;
        if (buf == null) {
            cache = buf = INS.alloc(range);
        }

        int s = scale;
        if (s == 0) {
            return value.read(buf);
        } else {
            if (s <= buf.length) {
                return value.read(
                    buf, 0, s
                );
            } else {
                throw new IOException(
                    "The specified scale<" + s + "> exceeds " +
                        "the buffer length<" + buf.length + ">"
                );
            }
        }
    }

    @Override
    public void close() {
        try {
            INS.join(
                cache
            );
            value.close();
        } catch (Exception e) {
            // Nothing
        } finally {
            limit = -1;
            cache = null;
            value = null;
        }
    }
}
