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

/**
 * @author kraity
 * @since 0.0.1
 */
public class Binary implements ByteSequence {

    protected int size;
    protected int hash;

    protected byte state;
    protected byte[] value;

    /**
     * Constructs an empty binary
     */
    public Binary() {
        value = Toolkit.EMPTY_BYTES;
    }

    /**
     * Constructs a binary with the size
     *
     * @param size the specified capacity
     */
    public Binary(
        int size
    ) {
        if (size > 0) {
            value = new byte[size];
        } else {
            value = Toolkit.EMPTY_BYTES;
        }
    }

    /**
     * Constructs a binary with the flow
     *
     * @param flow the specified value of binary
     * @throws NullPointerException If the specified flow is null
     */
    public Binary(
        byte[] flow
    ) {
        if (flow != null) {
            size = (value = flow).length;
        } else {
            throw new NullPointerException(
                "Received byte array is null"
            );
        }
    }

    /**
     * Constructs a binary with the flow and size
     *
     * @param size the specified size of binary
     * @param flow the specified value of binary
     * @throws NullPointerException      If the specified flow is null
     * @throws IndexOutOfBoundsException If the specified size is out of range
     */
    public Binary(
        byte[] flow, int size
    ) {
        if (0 <= size && size <= flow.length) {
            this.size = size;
            this.value = flow;
        } else {
            throw new IndexOutOfBoundsException(
                "Received size is out of range; (arg: "
                    + size + ", max: " + flow.length + ")"
            );
        }
    }

    /**
     * Returns the size of this {@link Binary}
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns the hashCode of this {@link Binary}
     */
    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            int l = size;
            if (l != 0) {
                int i = 0;
                byte[] v = value;
                while (i < l) {
                    h = 31 * h + v[i++];
                }
                return hash = h;
            }
        }
        return h;
    }

    /**
     * Returns the index value of this binary
     *
     * @param i the specified index of element
     */
    @Override
    public byte get(int i) {
        if (-1 < i && i < size) {
            return value[i];
        }
        throw new IndexOutOfBoundsException(
            "Index<" + i + "> is out of bounds"
        );
    }

    /**
     * Compares a {@link Object} with this binary
     * to determine if their contents are the same
     *
     * @param o the specified object to be compared
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        // ByteArray
        if (o instanceof Binary) {
            Binary b = (Binary) o;
            int l = b.size;
            if (l == size) {
                byte[] it = value;
                byte[] dt = b.value;
                for (int i = 0; i < l; i++) {
                    if (it[i] != dt[i]) {
                        return false;
                    }
                }
                return true;
            }
        }

        // CharArray
        else if (o instanceof String) {
            String s = (String) o;
            int l = s.length();
            if (l == size) {
                byte[] it = value;
                for (int i = 0; i < l; i++) {
                    if (s.charAt(i) !=
                        (char) (it[i] & 0xFF)) {
                        return false;
                    }
                }
                return true;
            }
        }

        return false;
    }
}
