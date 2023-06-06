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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.IOException;
import java.util.Locale;
import java.lang.reflect.Type;

import static java.util.Locale.*;

/**
 * @author kraity
 * @since 0.0.2
 */
@SuppressWarnings("deprecation")
public class LocaleSpare extends BaseSpare<Locale> {

    public static final LocaleSpare
        INSTANCE = new LocaleSpare();

    public LocaleSpare() {
        super(Locale.class);
    }

    @Override
    public Locale apply() {
        return getDefault();
    }

    @Override
    public Locale apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == Locale.class) {
            return getDefault();
        }

        throw new IllegalStateException(
            "Failed to build this " + type
        );
    }

    @Override
    public String getSpace() {
        return "Locale";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public Locale read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (value.isNothing()) {
            return null;
        }

        int size = value.size();
        if (size > 1 &&
            size < 32) {
            byte[] v = value.flow();

            byte c1 = v[0];
            byte c2 = v[1];

            if (c1 > 0x40 && c1 < 0x5B) {
                v[0] = c1 += 0x20;
            }
            if (c2 > 0x40 && c2 < 0x5B) {
                v[1] = c2 += 0x20;
            }

            if (size == 2) {
                if (c1 == 'z' && c2 == 'h') {
                    return CHINESE;
                }
                if (c1 == 'e' && c2 == 'n') {
                    return ENGLISH;
                }
                if (c1 == 'f' && c2 == 'r') {
                    return FRENCH;
                }
                if (c1 == 'd' && c2 == 'e') {
                    return GERMAN;
                }
                if (c1 == 'i' && c2 == 't') {
                    return ITALIAN;
                }
                if (c1 == 'k' && c2 == 'o') {
                    return KOREAN;
                }
                if (c1 == 'j' && c2 == 'a') {
                    return JAPANESE;
                }

                return new Locale(
                    new String(v, 0, 0, 2)
                );
            }

            if (size == 5 && v[2] == '_') {
                byte c3 = v[3];
                byte c4 = v[4];

                if (c3 > 0x60 && c3 < 0x7B) {
                    v[3] = c3 -= 0x20;
                }
                if (c4 > 0x60 && c4 < 0x7B) {
                    v[4] = c4 -= 0x20;
                }

                if (c1 == 'z' && c2 == 'h') {
                    if (c3 == 'C' && c4 == 'N') {
                        return SIMPLIFIED_CHINESE;
                    }
                    if (c3 == 'T' && c4 == 'W') {
                        return TRADITIONAL_CHINESE;
                    }
                } else if (c1 == 'e' && c2 == 'n') {
                    if (c3 == 'G' && c4 == 'B') {
                        return UK;
                    }
                    if (c3 == 'U' && c4 == 'S') {
                        return US;
                    }
                    if (c3 == 'C' && c4 == 'A') {
                        return CANADA;
                    }
                } else if (c1 == 'f' && c2 == 'r') {
                    if (c3 == 'F' && c4 == 'R') {
                        return FRANCE;
                    }
                    if (c3 == 'C' && c4 == 'A') {
                        return CANADA_FRENCH;
                    }
                } else if (c1 == 'd' && c2 == 'e') {
                    if (c3 == 'D' && c4 == 'E') {
                        return GERMANY;
                    }
                } else if (c1 == 'i' && c2 == 't') {
                    if (c3 == 'I' && c4 == 'T') {
                        return ITALY;
                    }
                } else if (c1 == 'k' && c2 == 'o') {
                    if (c3 == 'K' && c4 == 'R') {
                        return KOREA;
                    }
                } else if (c1 == 'j' && c2 == 'a') {
                    if (c3 == 'J' && c4 == 'P') {
                        return JAPAN;
                    }
                }

                return new Locale(
                    new String(v, 0, 0, 2),
                    new String(v, 0, 3, 2)
                );
            }

            int x = 0, y = 0;
            for (int i = 2; i < size; i++) {
                if (v[i] == '_') {
                    if (x == 0) {
                        x = i;
                    } else if (y == 0) {
                        y = i;
                    }
                }
            }

            if (x == 0) {
                return new Locale(
                    new String(v, 0, 0, size)
                );
            }

            if (y == 0) {
                return new Locale(
                    new String(v, 0, 0, x),
                    new String(v, 0, ++x, size - x)
                );
            } else {
                return new Locale(
                    new String(v, 0, 0, x),
                    new String(v, 0, ++x, y - x),
                    new String(v, 0, ++y, size - y)
                );
            }
        }

        throw new IOException(
            "Received `" + value
                + "` is not a locale string"
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            value.toString()
        );
    }
}
