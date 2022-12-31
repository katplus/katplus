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

import plus.kat.anno.NotNull;

import plus.kat.chain.*;
import plus.kat.crash.*;

import java.io.IOException;
import java.io.EOFException;

import static plus.kat.chain.Chain.Unsafe.value;

/**
 * @author kraity
 * @since 0.0.1
 */
public class BytePaper implements Paper {

    private int index;
    private int limit;
    private byte[] queue;

    public BytePaper(
        @NotNull byte[] data
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        this.queue = data;
        this.limit = data.length;
    }

    public BytePaper(
        @NotNull Chain data
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        this.queue = value(data);
        this.limit = data.length();
    }

    public BytePaper(
        @NotNull Chain data, int index, int length
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        int limit = index + length;
        if (index < 0 ||
            limit <= index ||
            limit > data.length()
        ) {
            throw new IndexOutOfBoundsException();
        }

        this.index = index;
        this.limit = limit;
        this.queue = value(data);
    }

    public BytePaper(
        @NotNull byte[] data, int index, int length
    ) {
        if (data == null) {
            throw new NullPointerException();
        }

        int limit = index + length;
        if (index < 0 ||
            limit <= index ||
            limit > data.length
        ) {
            throw new IndexOutOfBoundsException();
        }

        this.queue = data;
        this.index = index;
        this.limit = limit;
    }

    @Override
    public boolean also() {
        return index < limit;
    }

    @Override
    public byte read() {
        return queue[index++];
    }

    @Override
    public void skip(
        int size
    ) throws IOException {
        if (size > 0) {
            if (size <= limit - index) {
                index += size;
            } else {
                throw new EOFException(
                    "Unable to skip exactly"
                );
            }
        }
    }

    @Override
    public byte next() throws IOException {
        if (index < limit) {
            return queue[index++];
        }

        throw new FlowCrash(
            "No readable byte, please " +
                "check whether the stream is damaged"
        );
    }

    @Override
    public void close() {
        limit = 0;
        queue = null;
    }
}