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

        int e = text.length();
        if (e < 2) {
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
        } while (i < e);

        do {
            c2 = text.charAt(e - 1);
            if (c2 <= 0x20) {
                e--;
            } else {
                break;
            }
        } while (i < e);

        Job job;
        if (c2 != '}') {
            // ()
            if (c2 == ')') {
                job = Job.KAT;
            }

            // []
            else if (c2 == ']') {
                if (c1 != '[') {
                    return null;
                } else {
                    job = Job.JSON;
                }
            }

            // <>
            else if (c2 == '>') {
                if (c1 != '<' || e < 8) {
                    return null;
                } else {
                    job = Job.DOC;
                }
            } else {
                return null;
            }
        } else {
            if (c1 != '{') {
                job = Job.KAT;
            } else {
                char ch = 0;
                for (int t = i + 1; t < e; t++) {
                    ch = text.charAt(t);
                    if (ch > 0x20) {
                        break;
                    }
                }
                job = ch != '"' ? Job.KAT : Job.JSON;
            }
        }

        Event<K> event;
        if (text instanceof Chain) {
            Chain c = (Chain) text;
            event = new Event<>(
                flag, c.reader(i, e - i)
            );
        } else {
            event = new Event<>(
                flag, new CharReader(text, i, e - i)
            );
        }

        return spare.solve(
            job, event.with(
                supplier
            )
        );
    }
}
