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
package plus.kat.netty.flow;

import plus.kat.*;
import plus.kat.actor.*;

import io.netty.util.AsciiString;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class AsciiStringFlow extends Flow {

    /**
     * Constructs this flow for the specified text
     *
     * @throws NullPointerException If the specified text is null
     */
    public AsciiStringFlow(
        @NotNull AsciiString text
    ) {
        if (text != null) {
            value = text.array();
            limit = (index = text.arrayOffset()) + text.length();
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * Constructs this flow for the specified text
     *
     * @param index  the start index of the text
     * @param length the specified length of the text
     * @throws NullPointerException If the specified text is null
     */
    public AsciiStringFlow(
        @NotNull AsciiString text, int index, int length
    ) {
        if (text == null) {
            throw new NullPointerException();
        }

        int len = text.length();
        int off = text.arrayOffset();

        int limit = index + length;
        if (limit <= off + len && off <= index && length >= 0) {
            this.index = index;
            this.limit = limit;
            this.value = text.array();
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
}
