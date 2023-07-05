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
package plus.kat.spare;

import plus.kat.*;
import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.IOException;

import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ByteArraySpare extends BaseSpare<byte[]> {

    public static final ByteArraySpare
        INSTANCE = new ByteArraySpare();

    static final byte[] RFC4648_ENCODE = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    static final byte[] RFC4648_DECODE = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, // 20-2f + /
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, // 30-3f 0-9
        -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,           // 40-4f A-O
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, // 50-5f P-Z
        -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 60-6f a-o
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51                      // 70-7a p-z
    };

    public ByteArraySpare() {
        super(byte[].class);
    }

    @Override
    public byte[] apply() {
        return EMPTY_BYTES;
    }

    @Override
    public String getSpace() {
        return "ByteArray";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public byte[] read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        int l = value.size();
        if (l < 2) {
            throw new IOException(
                "It should be at least 2 bytes"
            );
        }

        int m = 0, e, n = l;
        byte[] v = value.flow();

        if (n > 76) {
            e = 76;
            check:
            while (e < n) {
                switch (v[e]) {
                    case '\n': {
                        if (m == 2) {
                            break;
                        } else {
                            m = 1;
                            l--;
                            e += 77;
                            continue;
                        }
                    }
                    case '\r': {
                        if (n == ++e ||
                            m == 1 ||
                            v[e] != '\n') {
                            break;
                        } else {
                            l -= 2;
                            m = 2;
                            e += 77;
                            continue;
                        }
                    }
                    default: {
                        if (m == 0) {
                            break check;
                        }
                    }
                }

                throw new IOException(
                    "Missing symbol at index: " +
                        e + ", base64(" + value + ")"
                );
            }
        }

        int i = 0, x = 0;
        stage:
        {
            int s = ((l + 3) / 4) * 3;
            if (v[n - 1] == '=') {
                s -= v[n - 2] == '=' ? 2 : 1;
            } else {
                e = l & 0x3;
                if (e != 0) {
                    s += e - 0x4;
                }
            }

            int t = 0, a = s / 3;
            boolean skip = m != 0;

            byte[] it = new byte[s];
            byte[] tab = RFC4648_DECODE;

            int v1, v2, v3, v4;
            while (t < a) {
                v1 = tab[v[i++] & 0xFF];
                if (v1 == -1) {
                    break stage;
                }

                v2 = tab[v[i++] & 0xFF];
                if (v2 == -1) {
                    break stage;
                }

                v3 = tab[v[i++] & 0xFF];
                if (v3 == -1) {
                    break stage;
                }

                v4 = tab[v[i++] & 0xFF];
                if (v4 == -1) {
                    break stage;
                }

                it[x++] = (byte) (
                    (v1 << 2) | (v2 >> 4)
                );
                it[x++] = (byte) (
                    ((v2 & 0xF) << 4) | (v3 >> 2)
                );
                it[x++] = (byte) (
                    ((v3 & 0x3) << 6) | v4
                );

                t++;
                if (skip && t % 19 == 0) {
                    i += m; // skip CR and LF
                }
            }

            if ((t = s - x) > 0) {
                v1 = tab[v[i++] & 0xFF];
                if (v1 == -1) {
                    break stage;
                }

                v2 = tab[v[i++] & 0xFF];
                if (v2 == -1) {
                    break stage;
                }

                it[x] = (byte) (
                    (v1 << 2) | (v2 >> 4)
                );
                if (t == 2) {
                    v3 = tab[v[i] & 0xFF];
                    if (v3 == -1) {
                        break stage;
                    }
                    it[x + 1] = (byte) (
                        ((v2 & 0xF) << 4) | (v3 >> 2)
                    );
                }
            }

            return it;
        }

        throw new IOException(
            "Decoding base64(" + value
                + ") failed at index: " + --i
                + ", specifically: " + (char) v[i]
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        byte[] data =
            (byte[]) value;
        int size = data.length;

        if (size != 0) {
            int t1 = size % 3;
            int t2 = size / 3;

            int b1, b2, b3;
            int i = 0, c = t2 * 3;

            byte[] tab = RFC4648_ENCODE;
            while (i < c) {
                b1 = data[i++] & 0xFF;
                b2 = data[i++] & 0xFF;
                b3 = data[i++] & 0xFF;

                flux.emit(tab[b1 >>> 2]);
                flux.emit(tab[((b1 & 0x3) << 4) | (b2 >>> 4)]);
                flux.emit(tab[((b2 & 0xF) << 2) | (b3 >>> 6)]);
                flux.emit(tab[b3 & 0x3F]);
            }

            if (t1 != 0) {
                b1 = data[i++] & 0xFF;
                if (t1 == 1) {
                    flux.emit(tab[b1 >>> 2]);
                    flux.emit(tab[(b1 & 0x3) << 4]);
                    flux.emit((byte) '=');
                } else {
                    b2 = data[i] & 0xFF;
                    flux.emit(tab[b1 >>> 2]);
                    flux.emit(tab[((b1 & 0x3) << 4) | (b2 >>> 4)]);
                    flux.emit(tab[(b2 & 0xF) << 2]);
                }
                flux.emit((byte) '=');
            }
        }
    }
}
