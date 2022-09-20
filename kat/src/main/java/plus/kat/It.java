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
 * @since 0.0.4
 */
public interface It {
    /**
     * @see Embed#require()
     */
    int sealed = 0x1;

    /**
     * @see Embed#require()
     */
    int nimble = 0x2;

    /**
     * @see Expose#require()
     */
    int NotNull = 0x1;

    /**
     * @see Expose#require()
     */
    int readonly = 0x2;

    /**
     * @see Expose#require()
     */
    int internal = 0x4;

    /**
     * @see Expose#require()
     */
    int disabled = 0x6;

    /**
     * @see Expose#require()
     */
    int unwrapped = 0x8;

    /**
     * @param it the specified flags
     */
    static boolean sealed(int it) {
        return (it & sealed) == sealed;
    }

    /**
     * @param it the specified flags
     */
    static boolean nimble(int it) {
        return (it & nimble) == nimble;
    }

    /**
     * @param it the specified flags
     */
    static boolean notNull(int it) {
        return (it & NotNull) == NotNull;
    }

    /**
     * @param it the specified flags
     */
    static boolean readonly(int it) {
        return (it & readonly) == readonly;
    }

    /**
     * @param it the specified flags
     */
    static boolean internal(int it) {
        return (it & internal) == internal;
    }

    /**
     * @param it the specified flags
     */
    static boolean unwrapped(int it) {
        return (it & unwrapped) == unwrapped;
    }
}
