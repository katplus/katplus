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
package plus.kat.flow;

import plus.kat.*;
import plus.kat.lang.*;
import plus.kat.actor.*;

import static plus.kat.lang.Uniform.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class ByteFlow extends Flow {

    /**
     * Constructs this flow for the specified text
     *
     * @throws NullPointerException If the specified text is null
     */
    public ByteFlow(
        @NotNull byte[] text
    ) {
        if (text != null) {
            this.v = text;
            this.l = text.length;
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
    public ByteFlow(
        @NotNull byte[] text, int index, int length
    ) {
        if (text == null) {
            throw new NullPointerException();
        }

        int limit = index + length;
        if (limit <= text.length &&
            index >= 0 && length >= 0) {
            this.v = text;
            this.i = index;
            this.l = limit;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Constructs a flow for the specified text
     *
     * @throws NullPointerException If the specified text is null
     */
    public ByteFlow(
        @NotNull Binary text
    ) {
        if (text != null) {
            this.l = text.size();
            this.v = valueOf(text);
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * Constructs a flow for the specified text
     *
     * @param index  the start index of the text
     * @param length the specified length of the text
     * @throws NullPointerException If the specified text is null
     */
    public ByteFlow(
        @NotNull Binary text, int index, int length
    ) {
        if (text == null) {
            throw new NullPointerException();
        }

        int limit = index + length;
        if (limit <= text.size() &&
            index >= 0 && length >= 0) {
            this.i = index;
            this.l = limit;
            this.v = valueOf(text);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
}
