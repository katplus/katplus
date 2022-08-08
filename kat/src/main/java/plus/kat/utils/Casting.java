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
import plus.kat.entity.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;

import static plus.kat.Job.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public final class Casting {
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
     * @param klass the specified {@code klass}
     * @param text  specify the {@code text} to be parsed
     * @since 0.0.3
     */
    @Nullable
    public static <K> K cast(
        @NotNull Class<K> klass,
        @Nullable CharSequence text
    ) {
        Supplier supplier = Supplier.ins();
        Spare<K> spare = supplier.lookup(klass);

        if (spare == null) {
            return null;
        }

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
                job = KAT;
            }

            // []
            else if (c2 == ']') {
                if (c1 == '[') {
                    job = JSON;
                } else {
                    return null;
                }
            }

            // <>
            else if (c2 == '>') {
                if (c1 == '<' && e > 6) {
                    job = DOC;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            if (c1 != '{') {
                job = Job.KAT;
            } else {
                char ch;
                int t = i + 1;

                BOOT:
                while (true) {
                    ch = text.charAt(t++);
                    switch (ch) {
                        case '"':
                        case '\'':
                        case '\\': {
                            job = JSON;
                            break BOOT;
                        }
                        default: {
                            if (ch > 0x20 || t >= e) {
                                job = KAT;
                                break BOOT;
                            }
                        }
                    }
                }
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

    /**
     * @param it  the entity
     * @param val the value of entity
     * @since 0.0.3
     */
    public static void update(
        @NotNull Object[] it,
        @Nullable Object val,
        @NotNull Target target,
        @Nullable Supplier supplier
    ) {
        // get class specified
        Class<?> klass = target.getType();

        // check type
        if (klass == null) {
            return;
        }

        // update field
        int i = target.getIndex();
        if (val != null) {
            Class<?> type = val.getClass();
            if (klass.isAssignableFrom(type)) {
                it[i] = val;
                return;
            }
        }

        // check supplier
        if (supplier == null) {
            supplier = Supplier.ins();
        }

        // get spare specified
        Spare<?> spare = supplier.lookup(klass);

        // update field
        if (spare != null) {
            it[i] = spare.cast(
                supplier, val
            );
        }
    }

    /**
     * @param it  the entity
     * @param val the value of entity
     * @since 0.0.3
     */
    public static <T> void update(
        @NotNull T it,
        @Nullable Object val,
        @NotNull Setter<T, ?> set,
        @Nullable Supplier supplier
    ) {
        // get class specified
        Class<?> klass = set.getType();

        // check type
        if (klass == null) {
            set.onAccept(
                it, val
            );
            return;
        }

        // update field
        if (val != null) {
            Class<?> type = val.getClass();
            if (klass.isAssignableFrom(type)) {
                set.onAccept(
                    it, val
                );
                return;
            }
        }

        // check supplier
        if (supplier == null) {
            supplier = Supplier.ins();
        }

        // get spare specified
        Spare<?> spare = supplier.lookup(klass);

        // update field
        if (spare != null) {
            set.onAccept(
                it, spare.cast(
                    supplier, val
                )
            );
        }
    }
}
