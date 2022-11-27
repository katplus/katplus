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
package plus.kat;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;

/**
 * @author kraity
 * @since 0.0.1
 */
@FunctionalInterface
public interface Flag {
    /**
     * Embed Flags
     *
     * @see Embed
     */
    long SEALED = 0x1;

    /**
     * Expose Flags
     *
     * @see Expose
     */
    long NOTNULL = 0x1;
    long READONLY = 0x2;
    long INTERNAL = 0x4;
    long EXCLUDED = 0x6;
    long UNWRAPPED = 0x8;

    /**
     * Write Flags
     *
     * @see Chan
     */
    long PRETTY = 0x1;
    long UNICODE = 0x2;
    long ENUM_AS_INDEX = 0x4;
    long DATE_AS_DIGIT = 0x8;

    /**
     * Read Flags
     *
     * @see Event
     */
    long INDEX_AS_ENUM = 0x4;
    long DIGIT_AS_DATE = 0x8;
    long VALUE_AS_BEAN = 0x10;

    /**
     * Check if this {@link Object} uses the {@code flag}
     *
     * <pre>{@code
     *  Flag flag = ...
     *  boolean status = flag.isFlag(Flag.UNICODE);
     * }</pre>
     *
     * @param flag the specified {@code flag}
     */
    boolean isFlag(
        long flag
    );

    /**
     * Check if this {@link Object} uses the
     * {@code flag} under the specified branch {@code code}
     *
     * <pre>{@code
     *  Flag flag = ...
     *  // equivalent to 'flag.isFlag(0x1L);'
     *  boolean status = flag.isFlag(0x1L, 0);
     *
     *  // code=1, flag = 0x1L
     *  boolean status = flag.isFlag(0x1L, 1);
     *
     *  // code=1, flag = 0x2L
     *  boolean status = flag.isFlag(0x2L, 1);
     *
     *  // code=2, flag = 0x1L
     *  boolean status = flag.isFlag(0x1L, 2);
     *
     *  // code=2, flag = 0x2L
     *  boolean status = flag.isFlag(0x2L, 2);
     * }</pre>
     * <p>
     * Uses {@code code} as the distinguishing mark,
     * when code is '0', metamorphoses to {@link #isFlag(long)},
     * otherwise use {@code code} as branch to check the {@code flag}
     *
     * @param flag the specified {@code flag}
     * @param code the specified branch {@code code}
     * @since 0.0.3
     */
    default boolean isFlag(
        long flag, int code
    ) {
        return code == 0 && isFlag(flag);
    }
}
