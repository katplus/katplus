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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.crash.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;
import plus.kat.utils.*;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import static plus.kat.stream.Binary.toLower;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Value extends Alpha {
    /**
     * Constructs a mutable value
     */
    public Value() {
        super();
    }

    /**
     * Constructs a mutable value
     *
     * @param size the initial capacity
     */
    public Value(
        int size
    ) {
        super(size);
    }

    /**
     * Constructs a mutable value
     *
     * @param data the initial byte array
     */
    public Value(
        @NotNull byte[] data
    ) {
        super(data);
    }

    /**
     * Constructs a mutable value
     *
     * @param data the specified chain to be used
     */
    public Value(
        @NotNull Chain data
    ) {
        super(data);
    }

    /**
     * Constructs a mutable value
     *
     * @param bucket the specified bucket to be used
     */
    public Value(
        @Nullable Bucket bucket
    ) {
        super(bucket);
    }

    /**
     * Constructs a mutable value
     *
     * @param sequence the specified sequence to be used
     */
    public Value(
        @Nullable CharSequence sequence
    ) {
        super(sequence);
    }

    /**
     * Returns a {@link Value} that
     * is a subsequence of this {@link Value}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public Value subSequence(
        int start, int end
    ) {
        return new Value(
            toBytes(start, end)
        );
    }

    /**
     * Returns a lowercase {@code MD5} of this {@link Chain}
     *
     * @throws FatalCrash If the MD5 algo is not supported
     */
    @NotNull
    public String digest() {
        return digest(
            "MD5", 0, count
        );
    }

    /**
     * Returns a lowercase message digest of this {@link Chain}
     *
     * @param algo the name of the algorithm requested
     * @throws FatalCrash If the specified algo is not supported
     * @see MessageDigest
     * @see Binary#toLower(byte[])
     * @see Value#digest(String, int, int)
     */
    @NotNull
    public String digest(
        @NotNull String algo
    ) {
        return digest(
            algo, 0, count
        );
    }

    /**
     * Returns a lowercase message digest of this {@link Chain}
     *
     * @param algo the name of the algorithm requested
     * @param i    the specified offset
     * @param l    the specified length
     * @throws FatalCrash               If the specified algo is not supported
     * @throws IllegalArgumentException If the length out of range
     * @see MessageDigest
     * @see Binary#toLower(byte[])
     */
    @NotNull
    public String digest(
        @NotNull String algo, int i, int l
    ) {
        if (i <= count - l && 0 <= i && 0 <= l) {
            try {
                MessageDigest md =
                    MessageDigest
                        .getInstance(algo);

                md.update(
                    value, i, l
                );

                return toLower(
                    md.digest()
                );
            } catch (NoSuchAlgorithmException e) {
                throw new FatalCrash(
                    algo + " is not supported", e
                );
            }
        } else {
            throw new IllegalArgumentException(
                "Specified offset(" + i + ")/length("
                    + l + ") index is out of bounds: " + count
            );
        }
    }

    /**
     * Completes the {@link Base64} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException     If the specified {@code base64} is null
     * @throws IllegalArgumentException If the offset is negative or the length out of range
     * @see Base64#encode(byte[], int, int)
     * @since 0.0.5
     */
    @NotNull
    public byte[] encode(
        @NotNull Base64 base64
    ) {
        return base64.encode(
            value, 0, count
        );
    }

    /**
     * Completes the {@link Base64} using the internal value of this {@link Chain}
     *
     * @param i the specified offset
     * @param l the specified length
     * @throws NullPointerException      If the specified {@code base64} is null
     * @throws IndexOutOfBoundsException If the offset is negative or the length out of range
     * @see Base64#encode(byte[], int, int)
     * @since 0.0.5
     */
    @NotNull
    public byte[] encode(
        @NotNull Base64 base64, int i, int l
    ) {
        if (i <= count - l && 0 <= i && 0 <= l) {
            return base64.encode(
                value, i, l
            );
        } else {
            throw new IndexOutOfBoundsException(
                "Specified offset(" + i + ")/length("
                    + l + ") index is out of bounds: " + count
            );
        }
    }

    /**
     * Completes the {@link Base64} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code base64} is null
     * @see Base64#decode(byte[], int, int)
     * @since 0.0.5
     */
    @NotNull
    public byte[] decode(
        @NotNull Base64 base64
    ) {
        return base64.decode(
            value, 0, count
        );
    }

    /**
     * Completes the {@link Base64} using the internal value of this {@link Chain}
     *
     * @param i the specified offset
     * @param l the specified length
     * @throws NullPointerException      If the specified {@code base64} is null
     * @throws IndexOutOfBoundsException If the offset is negative or the length out of range
     * @see Base64#decode(byte[], int, int)
     * @since 0.0.5
     */
    @NotNull
    public byte[] decode(
        @NotNull Base64 base64, int i, int l
    ) {
        if (i <= count - l && 0 <= i && 0 <= l) {
            return base64.decode(
                value, i, l
            );
        } else {
            throw new IndexOutOfBoundsException(
                "Specified offset(" + i + ")/length("
                    + l + ") index is out of bounds: " + count
            );
        }
    }

    /**
     * Updates the {@link Mac} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code mac} is null
     * @see Mac#update(byte[], int, int)
     */
    public void update(
        @NotNull Mac m
    ) {
        m.update(
            value, 0, count
        );
    }

    /**
     * Updates the {@link Mac} using the internal value of this {@link Chain}
     *
     * @param i the specified offset
     * @param l the specified length
     * @throws NullPointerException     If the specified {@code mac} is null
     * @throws IllegalArgumentException If the offset is negative or the length out of range
     * @see Mac#update(byte[], int, int)
     */
    public void update(
        @NotNull Mac m, int i, int l
    ) {
        if (i <= count - l) {
            m.update(
                value, i, l
            );
        } else {
            throw new IllegalArgumentException(
                "Specified offset(" + i + ")/length("
                    + l + ") index is out of bounds: " + count
            );
        }
    }

    /**
     * Updates the {@link Signature} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code signature} is null
     * @see Signature#update(byte[], int, int)
     */
    public void update(
        @NotNull Signature s
    ) throws SignatureException {
        s.update(
            value, 0, count
        );
    }

    /**
     * Updates the {@link Signature} using the internal value of this {@link Chain}
     *
     * @param i the specified offset
     * @param l the specified length
     * @throws NullPointerException     If the specified {@code signature} is null
     * @throws IllegalArgumentException If the offset is negative or the length out of range
     * @see Signature#update(byte[], int, int)
     */
    public void update(
        @NotNull Signature s, int i, int l
    ) throws SignatureException {
        if (i <= count - l) {
            s.update(
                value, i, l
            );
        } else {
            throw new IllegalArgumentException(
                "Specified offset(" + i + ")/length("
                    + l + ") index is out of bounds: " + count
            );
        }
    }

    /**
     * Updates the {@link MessageDigest} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code digest} is null
     * @see MessageDigest#update(byte[], int, int)
     */
    public void update(
        @NotNull MessageDigest m
    ) {
        m.update(
            value, 0, count
        );
    }

    /**
     * Updates the {@link MessageDigest} using the internal value of this {@link Chain}
     *
     * @param i the specified offset
     * @param l the specified length
     * @throws NullPointerException     If the specified {@code digest} is null
     * @throws IllegalArgumentException If the offset is negative or the length out of range
     * @see MessageDigest#update(byte[], int, int)
     */
    public void update(
        @NotNull MessageDigest m, int i, int l
    ) {
        if (i <= count - l) {
            m.update(
                value, i, l
            );
        } else {
            throw new IllegalArgumentException(
                "Specified offset(" + i + ")/length("
                    + l + ") index is out of bounds: " + count
            );
        }
    }

    /**
     * Updates the {@link Cipher} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code cipher} is null
     * @see Cipher#update(byte[], int, int)
     */
    @Nullable
    public byte[] update(
        @NotNull Cipher c
    ) {
        return c.update(
            value, 0, count
        );
    }

    /**
     * Updates the {@link Cipher} using the internal value of this {@link Chain}
     *
     * @param i the specified offset
     * @param l the specified length
     * @throws NullPointerException     If the specified {@code cipher} is null
     * @throws IllegalArgumentException If the offset is negative or the length out of range
     * @see Cipher#update(byte[], int, int)
     */
    @Nullable
    public byte[] update(
        @NotNull Cipher c, int i, int l
    ) {
        if (i <= count - l) {
            return c.update(
                value, i, l
            );
        } else {
            throw new IllegalArgumentException(
                "Specified offset(" + i + ")/length("
                    + l + ") index is out of bounds: " + count
            );
        }
    }

    /**
     * Completes the {@link Cipher} using the internal value of this {@link Chain}
     *
     * @throws NullPointerException If the specified {@code cipher} is null
     * @see Cipher#doFinal(byte[], int, int)
     */
    @Nullable
    public byte[] doFinal(
        @NotNull Cipher c
    ) throws IllegalBlockSizeException, BadPaddingException {
        return c.doFinal(
            value, 0, count
        );
    }

    /**
     * Completes the {@link Cipher} using the internal value of this {@link Chain}
     *
     * @param i the specified offset
     * @param l the specified length
     * @throws NullPointerException     If the specified {@code cipher} is null
     * @throws IllegalArgumentException If the offset is negative or the length out of range
     * @see Cipher#doFinal(byte[], int, int)
     */
    @Nullable
    public byte[] doFinal(
        @NotNull Cipher c, int i, int l
    ) throws IllegalBlockSizeException, BadPaddingException {
        if (i <= count - l) {
            return c.doFinal(
                value, i, l
            );
        } else {
            throw new IllegalArgumentException(
                "Specified offset(" + i + ")/length("
                    + l + ") index is out of bounds: " + count
            );
        }
    }

    /**
     * Returns a {@link Reader} of this {@link Chain}
     *
     * @see Reader
     * @see ByteReader
     */
    @NotNull
    public Reader asReader() {
        return new ByteReader(
            value, 0, count
        );
    }

    /**
     * Returns a {@link Reader} of this {@link Chain}
     *
     * @param index  the specified index
     * @param length the specified length
     * @throws IndexOutOfBoundsException If the index is negative or the length out of range
     * @see Reader
     * @see ByteReader
     */
    @NotNull
    public Reader asReader(
        int index, int length
    ) {
        return new ByteReader(
            value, index, length
        );
    }

    /**
     * Returns this {@link Alpha} as an {@link InputStream}
     *
     * @since 0.0.5
     */
    @NotNull
    public InputStream asInputStream() {
        return new ByteArrayInputStream(
            value, 0, count
        );
    }

    /**
     * Returns this {@link Alpha} as an {@link InputStream}
     *
     * @since 0.0.5
     */
    @NotNull
    public InputStream asInputStream(
        int index, int length
    ) {
        return new ByteArrayInputStream(
            value, index, length
        );
    }

    /**
     * Returns this {@link Chain} as a {@link SecretKeySpec}
     *
     * @throws IllegalArgumentException If the algo is null
     * @since 0.0.5
     */
    @NotNull
    public SecretKeySpec asSecretKeySpec(
        @NotNull String algo
    ) {
        return new SecretKeySpec(
            value, 0, count, algo
        );
    }

    /**
     * Returns this {@link Chain} as a {@link SecretKeySpec}
     *
     * @throws IllegalArgumentException       If the algo is null or the offset out of range
     * @throws ArrayIndexOutOfBoundsException If the length is negative
     * @since 0.0.5
     */
    @NotNull
    public SecretKeySpec asSecretKeySpec(
        @NotNull String algo, int offset, int length
    ) {
        return new SecretKeySpec(
            value, offset, length, algo
        );
    }

    /**
     * Returns this {@link Chain} as a {@link IvParameterSpec}
     *
     * @since 0.0.5
     */
    @NotNull
    public IvParameterSpec asIvParameterSpec() {
        return new IvParameterSpec(
            value, 0, count
        );
    }

    /**
     * Returns this {@link Chain} as a {@link IvParameterSpec}
     *
     * @throws IllegalArgumentException       If the offset out of range
     * @throws ArrayIndexOutOfBoundsException If the length is negative
     * @since 0.0.5
     */
    @NotNull
    public IvParameterSpec asIvParameterSpec(
        int offset, int length
    ) {
        return new IvParameterSpec(
            value, offset, length
        );
    }

    /**
     * @see Value#Value(Bucket)
     */
    public static Value apply() {
        return new Value(
            Buffer.INS
        );
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public static class Buffer implements Bucket {

        private static final int SIZE, LIMIT, SCALE;

        static {
            SIZE = Config.get(
                "kat.value.size", 8
            );
            LIMIT = Config.get(
                "kat.value.limit", 16
            );

            if (LIMIT < SIZE) {
                throw new Error(
                    "Bucket's size(" + SIZE + ") cannot be greater than the limit(" + LIMIT + ")"
                );
            }

            SCALE = Config.get(
                "kat.value.scale", 1024
            );
        }

        public static final Buffer
            INS = new Buffer();

        private final byte[][]
            bucket = new byte[SIZE][];

        @Override
        public boolean join(
            @NotNull byte[] it
        ) {
            int i = it.length / SCALE;
            if (i < SIZE) {
                synchronized (this) {
                    bucket[i] = it;
                }
                return true;
            }
            return false;
        }

        @Override
        public byte[] swap(
            @NotNull byte[] it
        ) {
            int i = it.length / SCALE;
            if (i == 0) {
                return it;
            }

            byte[] data;
            synchronized (this) {
                if (i < SIZE) {
                    bucket[i] = it;
                }
                data = bucket[i];
                bucket[i] = null;
            }
            return data == null ? EMPTY_BYTES : data;
        }

        @Override
        public byte[] alloc(
            @NotNull byte[] it, int len, int size
        ) {
            byte[] data;
            int i = size / SCALE;

            if (i < SIZE) {
                synchronized (this) {
                    data = bucket[i];
                    bucket[i] = null;
                }
                if (data == null ||
                    data.length < size) {
                    data = new byte[(i + 1) * SCALE - 1];
                }
            } else {
                if (i < LIMIT) {
                    data = new byte[(i + 1) * SCALE - 1];
                } else {
                    throw new FatalCrash(
                        "Unexpectedly, Exceeding range '" + LIMIT * SCALE + "' in value"
                    );
                }
            }

            if (it.length != 0) {
                System.arraycopy(
                    it, 0, data, 0, len
                );

                int k = it.length / SCALE;
                if (k < SIZE) {
                    synchronized (this) {
                        bucket[k] = it;
                    }
                }
            }

            return data;
        }
    }
}
