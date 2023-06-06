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
package plus.kat.stream;

import plus.kat.Flow;
import plus.kat.actor.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class ByteFlow extends Flow {

    public ByteFlow(
        @NotNull byte[] text
    ) {
        if (text != null) {
            this.value = text;
            this.limit = text.length;
        } else {
            throw new NullPointerException();
        }
    }

    public ByteFlow(
        @NotNull byte[] text, int index, int length
    ) {
        if (text == null) {
            throw new NullPointerException();
        }

        int limit = index + length;
        if (index >= 0 &&
            limit > index &&
            limit <= text.length) {
            this.value = text;
            this.index = index;
            this.limit = limit;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public ByteFlow(
        @NotNull Binary text
    ) {
        if (text != null) {
            this.limit = text.size;
            this.value = text.value;
        } else {
            throw new NullPointerException();
        }
    }

    public ByteFlow(
        @NotNull Binary text, int index, int length
    ) {
        if (text == null) {
            throw new NullPointerException();
        }

        int limit = index + length;
        if (index >= 0 &&
            limit > index &&
            limit <= text.size) {
            this.index = index;
            this.limit = limit;
            this.value = text.value;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
}
