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
package plus.kat.utils;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Casting {
    /**
     * Parse {@link CharSequence} and convert result to {@link K}
     *
     * @param text specify the {@code text} to be parsed
     */
    @Nullable
    public static <K> K cast(
        @NotNull Spare<K> spare,
        @Nullable CharSequence text
    ) {
        return cast(
            spare, text, null, null
        );
    }

    /**
     * Parse {@link CharSequence} and convert result to {@link K}
     *
     * @param supplier the specified {@code supplier}
     * @param text     specify the {@code text} to be parsed
     */
    @Nullable
    public static <K> K cast(
        @NotNull Spare<K> spare,
        @Nullable CharSequence text,
        @Nullable Supplier supplier
    ) {
        return cast(
            spare, text, null, supplier
        );
    }

    /**
     * @since 0.0.2
     */
    static <K> Event<K> event(
        CharSequence data,
        int i, int l,
        Flag flag, Supplier supplier
    ) {
        Event<K> event =
            new Event<>();

        event.setFlag(flag);
        event.with(supplier);

        if (data instanceof Chain) {
            Chain c = (Chain) data;
            event.with(
                c.reader(
                    i, l
                )
            );
        } else {
            event.with(
                new CharReader(
                    data, i, l
                )
            );
        }

        return event;
    }

    /**
     * Parse {@link CharSequence} and convert result to {@link K}
     *
     * @param supplier the specified {@code supplier}
     * @param text     specify the {@code text} to be parsed
     * @since 0.0.2
     */
    @Nullable
    public static <K> K cast(
        @NotNull Spare<K> spare,
        @Nullable CharSequence text,
        @Nullable Flag flag,
        @Nullable Supplier supplier
    ) {
        if (text == null) {
            return null;
        }

        int l = text.length();
        if (l < 2) {
            return null;
        }

        int i = 0;
        char c1, c2;

        do {
            c1 = text.charAt(i);
            if (c1 <= 0x20) {
                i++;
            } else {
                break;
            }
        } while (i < l);

        do {
            c2 = text.charAt(l - 1);
            if (c2 <= 0x20) {
                l--;
            } else {
                break;
            }
        } while (i < l);

        switch (c2) {
            case ')': {
                return spare.read(event(
                    text, i, l, flag, supplier
                ));
            }
            case ']': {
                if (c1 != '[') {
                    return null;
                }
                return spare.parse(event(
                    text, i, l, flag, supplier
                ));
            }
            case '>': {
                if (c1 != '<' || l < 8) {
                    return null;
                }
                return spare.down(event(
                    text, i, l, flag, supplier
                ));
            }
            case '}': {
                if (c1 != '{') {
                    return spare.read(event(
                        text, i, l, flag, supplier
                    ));
                } else {
                    return spare.parse(event(
                        text, i, l, flag, supplier
                    ));
                }
            }
        }

        return null;
    }
}
