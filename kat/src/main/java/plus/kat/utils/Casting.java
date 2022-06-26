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
            spare, text, null
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
                return spare.read(
                    new Event<>(
                        text, i, l, supplier
                    )
                );
            }
            case ']': {
                if (c1 != '[') {
                    return null;
                }
                return spare.parse(
                    new Event<>(
                        text, i, l, supplier
                    )
                );
            }
            case '>': {
                if (c1 != '<' || l < 8) {
                    return null;
                }
                return spare.down(
                    new Event<>(
                        text, i, l, supplier
                    )
                );
            }
            case '}': {
                if (c1 != '{') {
                    return spare.read(
                        new Event<>(
                            text, i, l, supplier
                        )
                    );
                } else {
                    return spare.parse(
                        new Event<>(
                            text, i, l, supplier
                        )
                    );
                }
            }
        }

        return null;
    }
}
