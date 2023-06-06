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
     * Appends this byte to the current content
     *
     * @param bin the specified byte value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        byte bin
    ) throws IOException;

    /**
     * Appends this char to the current content
     *
     * @param val the specified char value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        char val
    ) throws IOException;

    /**
     * Appends the literal representation
     * of the int value to the current content
     *
     * @param val the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        int val
    ) throws IOException;

    /**
     * Appends the literal representation
     * of the long value to the current content
     *
     * @param val the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        long val
    ) throws IOException;

    /**
     * Appends the literal representation
     * of the short value to the current content
     *
     * @param val the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        short val
    ) throws IOException;

    /**
     * Appends the literal representation
     * of the float value to the current content
     *
     * @param val the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        float val
    ) throws IOException;

    /**
     * Appends the literal representation
     * of the double value to the current content
     *
     * @param val the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        double val
    ) throws IOException;

    /**
     * Appends the literal representation
     * of the boolean value to the current content
     *
     * @param val the specified boolean value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        boolean val
    ) throws IOException;

    /**
     * Appends this byte array to the current content
     *
     * @param bin the specified source to be appended
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified array is null
     */
    void emit(
        @NotNull byte[] bin
    ) throws IOException;

    /**
     * Appends this byte array where the
     * specified offset and length to the current content
     *
     * @param bin    the specified source to be appended
     * @param offset the specified start index for array
     * @param length the specified length of bytes to concat
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified array is null
     */
    void emit(
        @NotNull byte[] bin, int offset, int length
    ) throws IOException;

    /**
     * Appends this char array to the current content
     *
     * @param val the specified source to be appended
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified array is null
     */
    void emit(
        @NotNull char[] val
    ) throws IOException;

    /**
     * Appends this char array where the
     * specified offset and length to the current content
     *
     * @param val    the specified source to be appended
     * @param offset the specified start index for array
     * @param length the specified length of chars to concat
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified array is null
     */
    void emit(
        @NotNull char[] val, int offset, int length
    ) throws IOException;

    /**
     * Appends this binary to the current content
     *
     * @param bin the specified source to be appended
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified sequence is null
     */
    void emit(
        @NotNull Binary bin
    ) throws IOException;

    /**
     * Appends this binary where the
     * specified offset and length to the current content
     *
     * @param bin    the specified source to be appended
     * @param offset the specified start index for binary
     * @param length the specified length of flow to concat
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified sequence is null
     */
    void emit(
        @NotNull Binary bin, int offset, int length
    ) throws IOException;

    /**
     * Appends this string to the current content
     *
     * @param val the specified sequence to be appended
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified sequence is null
     */
    void emit(
        @NotNull String val
    ) throws IOException;

    /**
     * Appends this string where the
     * specified offset and length to the current content
     *
     * @param val    the specified sequence to be appended
     * @param offset the specified start index for sequence
     * @param length the specified length of sequence to concat
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified sequence is null
     */
    void emit(
        @NotNull String val, int offset, int length
    ) throws IOException;
}
