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

import plus.kat.anno.NotNull;

import plus.kat.chain.*;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Flow extends Flag, Closeable {
    /**
     * Returns the algo of this {@link Flow}
     *
     * @return {@link Algo}, NotNull
     */
    Algo algo();

    /**
     * Concatenates the byte value to this {@link Flow},
     * which will be escaped if it is a special character
     *
     * <pre>{@code
     *   Flow flow = ...
     *
     *   // kat
     *   flow.emit((byte) '^'); // escape: ^^
     *   flow.emit((byte) 'k'); // not escaped
     *   flow.emit((byte) 't'); // not escaped
     *
     *   // json
     *   flow.emit((byte) '"'); // escape: \"
     *   flow.emit((byte) '\'); // escape: \\
     *   flow.emit((byte) 'k'); // not escaped
     * }</pre>
     *
     * @param b the specified byte value to be appended
     * @throws IOException If an I/O error occurs
     */
    void emit(
        byte b
    ) throws IOException;

    /**
     * Concatenates the char value to this {@link Flow},
     * which will be escaped if it is a special character
     *
     * <pre>{@code
     *   Flow flow = ...
     *
     *   // kat
     *   flow.emit('^'); // escape: ^^
     *   flow.emit('k'); // not escaped
     *   flow.emit('t'); // not escaped
     *
     *   // json
     *   flow.emit('"'); // escape: \"
     *   flow.emit('\'); // escape: \\
     *   flow.emit('k'); // not escaped
     * }</pre>
     *
     * @param ch the specified char value to be appended
     * @throws IOException If an I/O error occurs
     */
    void emit(
        char ch
    ) throws IOException;

    /**
     * Concatenates the string representation
     * of the integer value to this {@link Flow}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   flow.emit(32); // 32
     *   flow.emit(64); // 64
     * }</pre>
     *
     * @param num the specified number to be appended
     * @throws IOException If an I/O error occurs
     */
    void emit(
        int num
    ) throws IOException;

    /**
     * Concatenates the format an integer value
     * (treated as unsigned) to this {@link Flow}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   flow.emit(36, 1, 3); // 100
     *   flow.emit(36, 2, 2); // 10
     *   flow.emit(36, 3, 3); // 044
     *   flow.emit(36, 4, 4); // 0024
     *   flow.emit(-36, 1, 8); // 11011100
     *   flow.emit(-36, 4, 12); // 0000ffffffdc
     * }</pre>
     *
     * @param num   the specified number to be appended
     * @param shift the log2 of the base to format in (4 for hex, 3 for octal, 1 for binary)
     * @throws IOException If an I/O error occurs
     */
    void emit(
        int num, int shift
    ) throws IOException;

    /**
     * Concatenates the format an integer value
     * (treated as unsigned) to this {@link Flow}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   flow.emit(36, 1); // 100100
     *   flow.emit(36, 2); // 210
     *   flow.emit(36, 3); // 44
     *   flow.emit(36, 4); // 24
     *   flow.emit(-36, 1); // 11111111111111111111111111011100
     *   flow.emit(-36, 4); // ffffffdc
     * }</pre>
     *
     * @param num    the specified number to be appended
     * @param shift  the log2 of the base to format in (4 for hex, 3 for octal, 1 for binary)
     * @param length the specified formatted length of the output bit
     * @throws IOException If an I/O error occurs
     */
    void emit(
        int num, int shift, int length
    ) throws IOException;

    /**
     * Concatenates the string representation
     * of the long value to this {@link Flow}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   flow.emit(32L); // 32
     *   flow.emit(64L); // 64
     * }</pre>
     *
     * @param num the specified number to be appended
     * @throws IOException If an I/O error occurs
     */
    void emit(
        long num
    ) throws IOException;

    /**
     * Concatenates the format a long value
     * (treated as unsigned) to this {@link Flow}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   flow.emit(36L, 1); // 100100
     *   flow.emit(36L, 2); // 210
     *   flow.emit(36L, 3); // 44
     *   flow.emit(36L, 4); // 24
     *   flow.emit(-36L, 1); // 1111111111111111111111111111111111111111111111111111111111011100
     *   flow.emit(-36L, 4); // ffffffffffffffdc
     * }</pre>
     *
     * @param num   the specified number to be appended
     * @param shift the log2 of the base to format in (4 for hex, 3 for octal, 1 for binary)
     * @throws IOException If an I/O error occurs
     */
    void emit(
        long num, int shift
    ) throws IOException;

    /**
     * Concatenates the format a long value
     * (treated as unsigned) to this {@link Flow}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   flow.emit(36L, 1, 3); // 100
     *   flow.emit(36L, 2, 2); // 10
     *   flow.emit(36L, 3, 3); // 044
     *   flow.emit(36L, 4, 4); // 0024
     *   flow.emit(-36L, 1, 8); // 11011100
     *   flow.emit(-36L, 4, 12); // 0000ffffffdc
     * }</pre>
     *
     * @param num    the specified number to be appended
     * @param shift  the log2 of the base to format in (4 for hex, 3 for octal, 1 for binary)
     * @param length the specified formatted length of the output bit
     * @throws IOException If an I/O error occurs
     */
    void emit(
        long num, int shift, int length
    ) throws IOException;

    /**
     * Concatenates the string representation
     * of the short value to this {@link Steam}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   flow.emit((short) 32); // 32
     *   flow.emit((short) 64); // 64
     * }</pre>
     *
     * @param num the specified number to be appended
     * @throws IOException If an I/O error occurs
     */
    void emit(
        short num
    ) throws IOException;

    /**
     * Concatenates the string representation
     * of the float value to this {@link Steam}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   flow.emit(16F);     // 16
     *   flow.emit(32.64F);  // 32.64
     *   flow.emit(64.128F); // 64.128
     * }</pre>
     *
     * @param num the specified number to be appended
     * @throws IOException If an I/O error occurs
     */
    void emit(
        float num
    ) throws IOException;

    /**
     * Concatenates the string representation
     * of the double value to this {@link Steam}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   flow.emit(16D);     // 16
     *   flow.emit(32.64D);  // 32.64
     *   flow.emit(64.128D); // 64.128
     * }</pre>
     *
     * @param num the specified number to be appended
     * @throws IOException If an I/O error occurs
     */
    void emit(
        double num
    ) throws IOException;

    /**
     * Concatenates the string representation
     * of the boolean value to this {@link Steam}
     *
     * <pre>{@code
     *   Flow flow = ...
     *   flow.emit(true);  // kat:1, xml:true, json:true
     *   flow.emit(false); // kat:0, xml:false, json:false
     * }</pre>
     *
     * @param bool the specified boolean to be appended
     * @throws IOException If an I/O error occurs
     */
    void emit(
        boolean bool
    ) throws IOException;

    /**
     * Concatenates the char array to this {@link Flow},
     * which will be escaped if it contains special characters
     *
     * <pre>{@code
     *   Flow flow = ...
     *   // literally
     *   char[] data = new char[]{
     *       ^, k, t, ", \
     *   };
     *
     *   // kat
     *   flow.emit(data); // escape: ^^, k, t, ", \
     *
     *   // json
     *   flow.emit(data); // escape: ^, k, t, \", \\
     * }</pre>
     *
     * @param data the specified source to be appended
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified data is null
     */
    void emit(
        @NotNull char[] data
    ) throws IOException;

    /**
     * Concatenates the char array to this {@link Flow},
     * which will be escaped if it contains special characters
     *
     * <pre>{@code
     *   Flow flow = ...
     *   // literally
     *   char[] data = new char[]{
     *       ^, k, t, ", \
     *   };
     *
     *   // kat
     *   flow.emit(data, 0, 5); // escape: ^^, k, t, ", \
     *
     *   // json
     *   flow.emit(data, 0, 5); // escape: ^, k, t, \", \\
     * }</pre>
     *
     * @param data   the specified source to be appended
     * @param offset the specified start index for array
     * @param length the specified length of chars to concat
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified data is null
     */
    void emit(
        @NotNull char[] data, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the sequence to this {@link Flow},
     * which will be escaped if it contains special characters
     *
     * <pre>{@code
     *   Flow flow = ...
     *   String data = new String(
     *      // literally
     *      new char[]{
     *         ^, k, t, ", \
     *      }
     *   );
     *
     *   // kat
     *   flow.emit(data); // escape: ^^, k, t, ", \
     *
     *   // json
     *   flow.emit(data); // escape: ^, k, t, \", \\
     * }</pre>
     *
     * @param data the specified sequence to be appended
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified data is null
     */
    void emit(
        @NotNull CharSequence data
    ) throws IOException;

    /**
     * Concatenates the sequence to this {@link Flow},
     * which will be escaped if it contains special characters
     *
     * <pre>{@code
     *   Flow flow = ...
     *   String data = new String(
     *      // literally
     *      new char[]{
     *         ^, k, t, ", \
     *      }
     *   );
     *
     *   // kat
     *   flow.emit(data, 0, 5); // escape: ^^, k, t, ", \
     *
     *   // json
     *   flow.emit(data, 0, 5); // escape: ^, k, t, \", \\
     * }</pre>
     *
     * @param data   the specified sequence to be appended
     * @param offset the specified start index for sequence
     * @param length the specified length of sequence to concat
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified data is null
     */
    void emit(
        @NotNull CharSequence data, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the byte array to this {@link Flow},
     * which will be escaped if it contains special characters
     *
     * <pre>{@code
     *   Flow flow = ...
     *   // literally
     *   byte[] data = new byte[]{
     *       ^, k, t, ", \
     *   };
     *
     *   // kat
     *   flow.emit(data); // escape: ^^, k, t, ", \
     *
     *   // json
     *   flow.emit(data); // escape: ^, k, t, \", \\
     * }</pre>
     *
     * @param data the specified source to be appended
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified data is null
     */
    void emit(
        @NotNull byte[] data
    ) throws IOException;

    /**
     * Concatenates the byte array to this {@link Flow},
     * which will be escaped if it contains special characters
     *
     * <pre>{@code
     *   Flow flow = ...
     *   // literally
     *   byte[] data = new byte[]{
     *       ^, k, t, ", \
     *   };
     *
     *   // kat
     *   flow.emit(data, 0, 5); // escape: ^^, k, t, ", \
     *
     *   // json
     *   flow.emit(data, 0, 5); // escape: ^, k, t, \", \\
     * }</pre>
     *
     * @param data   the specified source to be appended
     * @param offset the specified start index for array
     * @param length the specified length of bytes to concat
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified data is null
     */
    void emit(
        @NotNull byte[] data, int offset, int length
    ) throws IOException;

    /**
     * Concatenates the byte array to this {@link Flow}
     * and copy it directly if the specified type does not conflict with algo,
     * otherwise check to see if it contains special characters, concat it after escape
     *
     * <pre>{@code
     *   Unified:
     *   // 'K' -> Kat/Standard
     *   // 'X' -> XML/Standard
     *   // 'J' -> Json/Standard
     *   // 'B' -> Base64/REC4648
     *
     *   Flow flow = ...
     *   byte[] data = ...
     *
     *   // Algo-Kat
     *   flow.emit(data, 'J', 0, 5); // concat it after escape
     *   flow.emit(data, 'B', 0, 5); // will not escape, copy it directly
     *
     *   // Algo-Json
     *   flow.emit(data, 'K', 0, 5); // concat it after escape
     *   flow.emit(data, 'B', 0, 5); // will not escape, copy it directly
     * }</pre>
     *
     * @param data   the specified source to be appended
     * @param type   the specified type of source
     * @param offset the specified start index for array
     * @param length the specified length of bytes to concat
     * @throws IOException          If an I/O error occurs
     * @throws NullPointerException If the specified data is null
     */
    void emit(
        @NotNull byte[] data, char type, int offset, int length
    ) throws IOException;
}
