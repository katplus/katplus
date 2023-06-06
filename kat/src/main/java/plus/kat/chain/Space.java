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
package plus.kat.chain;

import plus.kat.stream.Binary;

import static plus.kat.stream.Toolkit.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Space extends Binary {
    /**
     * Constructs a space with the size
     *
     * @param size the specified capacity
     */
    public Space(
        int size
    ) {
        super(size);
    }

    /**
     * Constructs a space with the flow
     *
     * @param flow the specified buffer stream
     */
    public Space(
        byte[] flow
    ) {
        super(flow);
    }

    /**
     * Constructs a space with the flow and size
     *
     * @param flow the specified buffer stream
     * @param size the specified size of stream
     */
    public Space(
        int size,
        byte[] flow
    ) {
        super(flow, size);
    }

    /**
     * Returns the internal byte array and try not
     * to modify it, because this method is unsafe
     *
     * @throws IllegalStateException If this space refuses to open
     */
    public byte[] flow() {
        return value;
    }

    /**
     * Concatenates the byte value to this space,
     * checking the remaining capacity before calling
     *
     * @param w the byte value to be joined
     * @throws IndexOutOfBoundsException If this space capacity is full
     */
    public void join(byte w) {
        hash = 0;
        value[size++] = w;
    }

    /**
     * Gets the specified index value of this binary
     *
     * @param i the specified index of element
     * @throws IndexOutOfBoundsException If the index is out of bounds
     */
    public byte get(int i) {
        if ((-1 < i || (i += size) > -1) && i < size) {
            return value[i];
        }
        throw new IndexOutOfBoundsException(
            "Received index<" + i + "> is out of bounds"
        );
    }

    /**
     * Sets the value of the specified index for this binary
     *
     * @param i the specified index of element
     * @param b the specified value of element
     * @throws IndexOutOfBoundsException If the index is out of bounds
     */
    public void set(int i, byte b) {
        if ((-1 < i || (i += size) > -1) && i < size) {
            hash = 0;
            value[i] = b;
        } else {
            throw new IndexOutOfBoundsException(
                "Received index<" + i + "> is out of bounds"
            );
        }
    }

    /**
     * Sets the specified length of this {@link Space}
     *
     * @param i the specified length
     * @return this {@link Space} itself
     * @throws IndexOutOfBoundsException If index is out of bounds
     */
    public Space slip(int i) {
        if (i == 0) {
            size = 0;
            hash = 0;
        } else {
            if (0 < i && i <= value.length) {
                size = i;
                hash = 0;
            } else {
                throw new IndexOutOfBoundsException(
                    "Index<" + i + "> is out of bounds"
                );
            }
        }
        return this;
    }

    /**
     * Sets the specified length of this {@link Space}
     *
     * @param i the specified length
     * @param v the specified default value
     * @return this {@link Space} itself
     * @throws IndexOutOfBoundsException If index is out of bounds
     */
    public Space slip(int i, byte v) {
        if (i == 0) {
            size = 1;
            hash = value[0] = v;
        } else {
            if (0 < i && i <= value.length) {
                size = i;
                hash = 0;
            } else {
                throw new IndexOutOfBoundsException(
                    "Index<" + i + "> is out of bounds"
                );
            }
        }
        return this;
    }

    /**
     * Returns true if {@link #size()} is zero
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns true if this binary is empty
     * or contains only white space codepoints
     * <p>
     * White space: {@code 9,10,11,12,13,28,29,30,31,32}
     */
    public boolean isBlank() {
        int l = size;
        if (l != 0) {
            int i = 0;
            byte[] v = value;
            while (i < l) {
                byte w = v[i++];
                if (w > 32 || w < 9) {
                    return false;
                }
                if (13 < w && w < 28) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns true if this space is a simple
     * validated class name (Camel-Case). If space contains an
     * element that is not in {@code [A-Za-z0-9.$]}, it must return false
     *
     * <pre>{@code
     *  isClass() -> true
     *  // User
     *  // kat.User
     *  // byte.User // illegal
     *  // plus.kat.User
     *  // plus.kat.v2.API
     *  // plus.kat.v2.User
     *  // plus.kat.v2.UserName
     *  // plus.kat.v2.User$Name
     *
     *  isClass() -> false
     *  // $
     *  // .
     *  // plus
     *  // plus.$a
     *  // plus.kat.1v
     *  // plus.kat.$User
     *  // plus.kat.User$
     *  // plus.kat_v2.User
     *  // plus.kat.I_O
     *  // plus.kat.v2.3Q
     *  // plus.kat.User-Name
     * }</pre>
     */
    public boolean isClass() {
        int l = size;
        if (l == 0) {
            return false;
        }

        int i = 0, m = 0;
        byte[] it = value;

        for (; i < l; i++) {
            byte w = it[i];
            if (w > 0x60) {   // a-z
                if (w < 0x7B) {
                    continue;
                }
                return false;
            }

            if (w == 0x2E) {   // .
                if (m != i) {
                    m = i + 1;
                    if (m != l) {
                        continue;
                    }
                }
                return false;
            }

            if (w < 0x3A) {   // 0-9
                if (w > 0x2F
                    && i != m) {
                    continue;
                }
                return false;
            }

            if (l - i > 255 ||  // max-len
                w < 0x41 || w > 0x5A) {   // A-Z
                return false;
            }

            for (++i; i < l; i++) {
                byte v = it[i];
                if (v > 0x60) {   // a-z
                    if (v < 0x7B) {
                        continue;
                    }
                    return false;
                }

                if (v > 0x40) {   // A-Z
                    if (v < 0x5B) {
                        continue;
                    }
                    return false;
                }

                if (v < 0x3A) {   // 0-9
                    if (v > 0x2F ||
                        (v == 0x24 &&
                            i + 1 != l)) { // $
                        continue;
                    }
                    return false;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Resets this space to
     * make it an empty space
     */
    public void clear() {
        size = 0;
        hash = 0;
    }

    /**
     * Closes this space and releases
     * the resources associated with it
     */
    public void close() {
        if (value.length != 0) {
            this.clear();
            value = EMPTY_BYTES;
        }
    }

    /**
     * Returns the value of this
     * {@link Space} as a {@link String}
     */
    public String toString() {
        return size == 0 ? "" : (
            new String(
                value, 0, size, UTF_8
            )
        );
    }
}
