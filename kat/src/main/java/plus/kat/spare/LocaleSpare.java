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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Locale;

/**
 * @author kraity
 * @since 0.0.2
 */
public class LocaleSpare extends Property<Locale> {

    public static final LocaleSpare
        INSTANCE = new LocaleSpare();

    public LocaleSpare() {
        super(Locale.class);
    }

    @Override
    public Locale apply() {
        return Locale.getDefault();
    }

    @Override
    public Locale apply(
        @NotNull Type type
    ) {
        if (type == Locale.class) {
            return Locale.getDefault();
        }

        throw new Collapse(
            "Cannot create `" + type + "` instance"
        );
    }

    @Override
    public String getSpace() {
        return "Locale";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == Locale.class
            || clazz == Object.class;
    }

    @Override
    public Locale read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) {
        return lookup(alias);
    }

    @Override
    public Locale read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return lookup(value);
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.text(
            value.toString()
        );
    }

    @Override
    public Locale cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
            if (data instanceof Locale) {
                return (Locale) data;
            }

            if (data instanceof CharSequence) {
                try {
                    return lookup(
                        (CharSequence) data
                    );
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * @since 0.0.3
     */
    @Nullable
    public static Locale lookup(
        @NotNull CharSequence c
    ) {
        int len = c.length();
        if (len < 2) {
            return null;
        }

        char c1 = c.charAt(0);
        char c2 = c.charAt(1);

        if (c1 > 0x40 && c1 < 0x5B) c1 += 0x20;
        if (c2 > 0x40 && c2 < 0x5B) c2 += 0x20;

        if (len == 2) {
            if (c1 == 'z' && c2 == 'h') {
                return Locale.CHINESE;
            }
            if (c1 == 'e' && c2 == 'n') {
                return Locale.ENGLISH;
            }
            if (c1 == 'f' && c2 == 'r') {
                return Locale.FRENCH;
            }
            if (c1 == 'd' && c2 == 'e') {
                return Locale.GERMAN;
            }
            if (c1 == 'i' && c2 == 't') {
                return Locale.ITALIAN;
            }
            if (c1 == 'k' && c2 == 'o') {
                return Locale.KOREAN;
            }
            if (c1 == 'j' && c2 == 'a') {
                return Locale.JAPANESE;
            }

            return new Locale(
                new String(new char[]{c1, c2})
            );
        }

        if (len == 5 && c.charAt(2) == '_') {
            char c3 = c.charAt(3);
            char c4 = c.charAt(4);
            if (c3 > 0x60 && c3 < 0x7B) c3 -= 0x20;
            if (c4 > 0x60 && c4 < 0x7B) c4 -= 0x20;

            if (c1 == 'z' && c2 == 'h') {
                if (c3 == 'C' && c4 == 'N') {
                    return Locale.SIMPLIFIED_CHINESE;
                }
                if (c3 == 'T' && c4 == 'W') {
                    return Locale.TRADITIONAL_CHINESE;
                }
            } else if (c1 == 'e' && c2 == 'n') {
                if (c3 == 'G' && c4 == 'B') {
                    return Locale.UK;
                }
                if (c3 == 'U' && c4 == 'S') {
                    return Locale.US;
                }
                if (c3 == 'C' && c4 == 'A') {
                    return Locale.CANADA;
                }
            } else if (c1 == 'f' && c2 == 'r') {
                if (c3 == 'F' && c4 == 'R') {
                    return Locale.FRANCE;
                }
                if (c3 == 'C' && c4 == 'A') {
                    return Locale.CANADA_FRENCH;
                }
            } else if (c1 == 'd' && c2 == 'e') {
                if (c3 == 'D' && c4 == 'E') {
                    return Locale.GERMANY;
                }
            } else if (c1 == 'i' && c2 == 't') {
                if (c3 == 'I' && c4 == 'T') {
                    return Locale.ITALY;
                }
            } else if (c1 == 'k' && c2 == 'o') {
                if (c3 == 'K' && c4 == 'R') {
                    return Locale.KOREA;
                }
            } else if (c1 == 'j' && c2 == 'a') {
                if (c3 == 'J' && c4 == 'P') {
                    return Locale.JAPAN;
                }
            }

            return new Locale(
                new String(new char[]{c1, c2}),
                new String(new char[]{c3, c4})
            );
        }

        if (len > 32) {
            return null;
        }

        char[] ch = new char[len];
        ch[0] = c1;
        ch[1] = c2;

        char it;
        int d1 = 0, d2 = 0;

        for (int i = 2; i < len; i++) {
            ch[i] = it = c.charAt(i);
            if (it == '_') {
                if (d1 == 0) {
                    d1 = i;
                } else if (d2 == 0) {
                    d2 = i;
                }
            }
        }

        if (d1 == 0) {
            return new Locale(
                new String(ch)
            );
        }

        if (d2 == 0) {
            return new Locale(
                new String(ch, 0, d1),
                new String(ch, ++d1, len - d1)
            );
        }

        return new Locale(
            new String(ch, 0, d1),
            new String(ch, ++d1, d2 - d1),
            new String(ch, ++d2, len - d2)
        );
    }

    /**
     * @since 0.0.3
     */
    @NotNull
    public static Locale lookup(
        @NotNull CharSequence c,
        @NotNull Locale.Category category
    ) {
        Locale locale = lookup(c);
        if (locale != null) {
            return locale;
        }

        return Locale.getDefault(category);
    }
}
