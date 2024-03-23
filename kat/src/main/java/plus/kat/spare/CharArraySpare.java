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

import static plus.kat.lang.Uniform.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public class CharArraySpare extends BaseSpare<char[]> {

    public static final CharArraySpare
        INSTANCE = new CharArraySpare();

    public CharArraySpare() {
        super(char[].class);
    }

    @Override
    public char[] apply() {
        return EMPTY_CHARS;
    }

    @Override
    public String getSpace() {
        return "CharArray";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public char[] read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        int l = value.size();
        if (l == 0) {
            return EMPTY_CHARS;
        }

        byte w = value.flag();
        byte[] v = value.flow();

        if (l == 4 && w == 0
            && v[0] == 'n'
            && v[1] == 'u'
            && v[2] == 'l'
            && v[3] == 'l') {
            return null;
        }

        int i = 0, n = 0;
        char[] ch = new char[l];

        stage:
        {
            do {
                if ((w = v[i++]) >= 0) {
                    ch[n++] = (char) w;
                } else {
                    int w1 = w & 0xFF,
                        w2 = v[i++] & 0xFF;
                    if (w2 >> 6 != 2) {
                        break stage;
                    }
                    if (w1 >> 5 == 6) {
                        ch[n++] = (char) (
                            (w1 & 0x1F) << 6 | w2 & 0x3F
                        );
                        continue;
                    }

                    int w3 = v[i++] & 0xFF;
                    if (w3 >> 6 != 2) {
                        break stage;
                    }
                    if (w1 >> 4 == 14) {
                        ch[n++] = (char) (
                            (w1 & 0x0F) << 12 |
                                (w2 & 0x3F) << 6 | w3 & 0x3F
                        );
                        continue;
                    }

                    int w4 = v[i++] & 0xFF;
                    if (w3 >> 6 != 2 ||
                        w1 >> 3 != 30) {
                        break stage;
                    } else {
                        ch[n++] = (char) (
                            0xD800 | w1 << 8 & 0x300 | w2 << 2 & 0x0C
                                | w2 - 0x10 << 2 & 0xF0 | w3 >> 4 & 0x03
                        );
                        ch[n++] = (char) (
                            0xDC00 | w3 << 6 & 0x3C0 | w4 & 0x3F
                        );
                    }
                }
            } while (i < l);

            if (i == l) {
                if (n != l) {
                    char[] cs = new char[n];
                    System.arraycopy(
                        ch, 0, ch = cs, 0, n
                    );
                }
                return ch;
            }
        }

        throw new IOException(
            "Decoding source(" + value
                + ") failed at position: " + i
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            (char[]) value
        );
    }
}
