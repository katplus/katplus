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

import plus.kat.crash.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Flow extends Flag {
    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addByte(
        byte b
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addBytes(
        @NotNull byte[] data
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addBytes(
        @NotNull byte[] data, int offset, int length
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addShort(
        short num
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addInt(
        int num
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addInt(
        int num, int shift
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addLong(
        long num
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addLong(
        long num, int shift
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addFloat(
        float num
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addFloat(
        float num, boolean hint
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addDouble(
        double num
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addDouble(
        double num, boolean hint
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addBoolean(
        boolean b
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addChar(
        char c
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addChars(
        @NotNull char[] data
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addChars(
        @NotNull char[] data, int offset, int length
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addChars(
        @NotNull CharSequence data
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addChars(
        @NotNull CharSequence data, int offset, int length
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addData(
        byte b
    ) throws IOCrash;

    /**
     * @throws IOCrash If an I/O error occurs
     */
    void addData(
        char c
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addData(
        @NotNull byte[] data
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addData(
        @NotNull byte[] data, int offset, int length
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addData(
        @NotNull CharSequence data
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addData(
        @NotNull CharSequence data, int offset, int length
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addText(
        @NotNull byte[] data
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addText(
        @NotNull byte[] data, int offset, int length
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addText(
        @NotNull CharSequence data
    ) throws IOCrash;

    /**
     * @throws IOCrash              If an I/O error occurs
     * @throws NullPointerException If the specified {@code data} is null
     */
    void addText(
        @NotNull CharSequence data, int offset, int length
    ) throws IOCrash;
}
