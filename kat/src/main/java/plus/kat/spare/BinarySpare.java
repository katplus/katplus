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
import plus.kat.lang.*;
import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.IOException;

import static plus.kat.Algo.*;
import static plus.kat.lang.Uniform.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public class BinarySpare extends BaseSpare<Binary> {

    public static final BinarySpare
        INSTANCE = new BinarySpare();

    public BinarySpare() {
        super(Binary.class);
    }

    @Override
    public String getSpace() {
        return "Binary";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public Binary read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        int l = value.size();
        if (l == 0) {
            return new Binary();
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

        byte[] buffer = new byte[l];
        System.arraycopy(
            v, 0, buffer, 0, l
        );
        return new Binary(buffer, l);
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            (Binary) value
        );
    }

    @Nullable
    public static Algo algoOf(
        @NotNull Binary text
    ) {
        int e = text.size();
        if (e < 2) {
            return null;
        }

        byte c1, c2;
        byte[] v = valueOf(text);

        int i = 0;
        do {
            c1 = v[i];
        } while (
            c1 <= 0x20 && ++i < e
        );

        do {
            c2 = v[e - 1];
        } while (
            c2 <= 0x20 && --e > i
        );

        check:
        {
            // {}
            if (c1 == '{' && c2 == '}') {
                break check;
            }

            // @{}
            if (c1 == '@' && c2 == '}') {
                break check;
            }

            // <>
            if (c1 == '<' && c2 == '>') {
                if (e > 6) {
                    return DOC;
                } else {
                    return null;
                }
            }

            // []
            if (c1 != '[' || c2 != ']') {
                return null;
            }

            int m = 0;
            while (++i < e) {
                c1 = v[i];
                if (c1 == '"') {
                    m++;
                    continue;
                }
                if (c1 == '\\') {
                    i++;
                    continue;
                }
                if (c1 == '{' &&
                    m % 2 == 0) {
                    break check;
                }
            }
            return KAT;
        }

        int m = -1;
        while (++i < e) {
            c1 = v[i];
            if (c1 == '"') {
                m++;
                continue;
            }
            if (c1 == '\\') {
                i++;
                continue;
            }
            if (c1 == '=') {
                if (m % 2 != 0) {
                    return KAT;
                }
            } else if (c1 == ':') {
                if (m % 2 == 1) {
                    return JSON;
                }
            }
        }
        return null;
    }
}
