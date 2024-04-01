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

import plus.kat.lang.*;
import plus.kat.actor.*;

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
     * @param value the specified byte value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        byte value
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param value the specified char value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        char value
    ) throws IOException;

    /**
     * Concatenates the number to this flux
     *
     * @param value the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        int value
    ) throws IOException;

    /**
     * Concatenates the number to this flux
     *
     * @param value the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        long value
    ) throws IOException;

    /**
     * Concatenates the number to this flux
     *
     * @param value the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        short value
    ) throws IOException;

    /**
     * Concatenates the number to this flux
     *
     * @param value the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        float value
    ) throws IOException;

    /**
     * Concatenates the number to this flux
     *
     * @param value the specified number value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        double value
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param value the specified boolean value
     * @throws IOException If an I/O error occurs
     */
    void emit(
        boolean value
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param value the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull byte[] value
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param value  the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull byte[] value, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param value the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull char[] value
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param value  the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull char[] value, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param value the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull Binary value
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param value  the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull Binary value, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param value the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull String value
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param value  the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull String value, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param value the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull ByteSequence value
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param value  the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull ByteSequence value, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the value to this flux
     *
     * @param value the specified sequence value
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull CharSequence value
    ) throws IOException;

    /**
     * Concatenates the value where the
     * specified offset and length to this flux
     *
     * @param value  the specified sequence value
     * @param offset the specified begin index
     * @param length the specified required length
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the sequence is null
     */
    void emit(
        @NotNull CharSequence value, int offset, int length
    ) throws IOException;
}
