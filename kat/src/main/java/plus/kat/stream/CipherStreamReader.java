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

import plus.kat.crash.*;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;

import static plus.kat.stream.Reader.Bucket.INS;

/**
 * @author kraity
 * @since 0.0.1
 */
public class CipherStreamReader implements Reader {

    private int index;
    private int offset;

    private byte[] cache;
    private byte[] buffer;

    private Cipher cipher;
    private InputStream value;

    public CipherStreamReader(
        @NotNull InputStream data,
        @NotNull Cipher cipher
    ) {
        if (data == null ||
            cipher == null) {
            throw new NullPointerException();
        }

        this.value = data;
        this.cipher = cipher;
        this.buffer = INS.alloc();
        this.index = buffer.length;
        this.offset = buffer.length;
    }

    @Override
    public boolean also() throws IOException {
        if (index < offset) {
            return true;
        }

        if (offset > 0) {
            offset = read(buffer);
            if (offset > 0) {
                index = 0;
                return true;
            }
        }

        return false;
    }

    @Override
    public byte read() {
        return cache[index++];
    }

    @Override
    public byte next() throws IOException {
        if (index < offset) {
            return cache[index++];
        }

        if (offset > 0) {
            offset = read(buffer);
            if (offset > 0) {
                index = 0;
                return cache[index++];
            }
        }

        throw new UnexpectedCrash(
            "Unexpectedly, no readable byte"
        );
    }

    private int read(
        @NotNull byte[] buf
    ) throws IOException {
        int in = value.read(buf);
        if (in > 0) {
            cache = cipher.update(
                buf, 0, in
            );
            if (cache == null ||
                cache.length == 0) {
                return read(buf);
            }
        } else try {
            cache = cipher.doFinal();
            if (cache == null) {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }

        return cache.length;
    }

    @Override
    public void close() {
        try {
            INS.revert(
                buffer
            );
            value.close();
            if (offset != -1) {
                cipher.doFinal();
            }
        } catch (Exception e) {
            // NOOP
        } finally {
            offset = 0;
            cache = null;
            value = null;
            cipher = null;
            buffer = null;
        }
    }
}
