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

import plus.kat.actor.*;
import plus.kat.stream.*;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.6
 */
public interface Flux extends Flag, Closeable {
    /**
     * Concatenates the value to this flux
     *
     * @param val the specified byte value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        byte val
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param val the specified char value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        char val
    ) throws IOException;

    /**
     * Concatenates the number to this flux
     *
     * @param val the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        int val
    ) throws IOException;

    /**
     * Concatenates the number to this flux
     *
     * @param val the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        long val
    ) throws IOException;

    /**
     * Concatenates the number to this flux
     *
     * @param val the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        short val
    ) throws IOException;

    /**
     * Concatenates the number to this flux
     *
     * @param val the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        float val
    ) throws IOException;

    /**
     * Concatenates the number to this flux
     *
     * @param val the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        double val
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param val the specified boolean value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        boolean val
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param val the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull byte[] val
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param val    the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull byte[] val, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param val the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull char[] val
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param val    the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull char[] val, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param val the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull Binary val
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param val    the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull Binary val, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param val the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull String val
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param val    the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull String val, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param val the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull ByteSequence val
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param val    the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull ByteSequence val, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param val the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull CharSequence val
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param val    the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull CharSequence val, int offset, int length
    ) throws IOException;
}
