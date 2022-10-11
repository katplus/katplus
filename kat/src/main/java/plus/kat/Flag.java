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
     * Write Flags
     *
     * @see Chan
     */
    long PRETTY = 0x1;
    long UNICODE = 0x2;
    long ENUM_AS_INDEX = 0x4;
    long FLOAT_AS_BITMAP = 0x8;
    long DATE_AS_TIMESTAMP = 0x10;
    long INSTANT_AS_TIMESTAMP = 0x20;

    /**
     * Read Flags
     *
     * @see Event
     */
    long INDEX_AS_ENUM = 0x4;
    long STRING_AS_OBJECT = 0x8;

    /**
     * Embed Flags
     *
     * @see Embed
     */
    int Sealed = 0x1;
    int Nimble = 0x2;

    /**
     * Expose Flags
     *
     * @see Expose
     */
    int NotNull = 0x1;
    int Readonly = 0x2;
    int Internal = 0x4;
    int Excluded = 0x6;
    int Unwrapped = 0x8;

    /**
     * Check if this {@link Object} use the {@code flag}
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
     * Check if this {@link Object} use the {@code flag}.
     * The method is to extend {@link Flag#isFlag(long)} to derive custom {@code flags}.
     *
     * <pre>{@code
     *  Flag flag = ...
     *  boolean status = flag.isFlag(Flag.UNICODE, 0); // equivalent to 'flag.isFlag(Flag.UNICODE);'
     *
     *  // custom
     *  int kat = 1;
     *  int json = 2;
     *
     *  long ASM = 0x1L;
     *  boolean status = flag.isFlag(ASM, kat);
     *  boolean status = flag.isFlag(ASM, json);
     *
     *  long AUTO = 0x2L;
     *  boolean status = flag.isFlag(AUTO, kat);
     *  boolean status = flag.isFlag(AUTO, json);
     * }</pre>
     * <p>
     * Use {@code code} as the distinguishing mark,
     * when code is 0, check whether to use official {@code flags},
     * otherwise use {@code code} as branch to check custom {@code flags}
     *
     * @param flag the specified {@code flag}
     * @param code the specified {@code code}
     * @since 0.0.3
     */
    default boolean isFlag(
        long flag, int code
    ) {
        return code == 0 && isFlag(flag);
    }
}
