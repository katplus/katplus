package plus.kat.stream;

import plus.kat.anno.NotNull;

import plus.kat.chain.*;
import plus.kat.crash.*;

import static plus.kat.chain.Chain.EMPTY_BYTES;
import static plus.kat.chain.Chain.Unsafe.value;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Base64 {

    private final int E, D;
    private final byte[] ENCODE, DECODE;

    /**
     * @param encode the specified encode table
     * @param decode the specified decode table
     */
    public Base64(
        @NotNull byte[] encode,
        @NotNull byte[] decode
    ) {
        this(
            encode, 0, decode, 0
        );
    }

    /**
     * @param b the base64 for reuse
     * @param e the pair num of lines encoded
     * @param d the pair num of lines decoded
     */
    public Base64(
        Base64 b, int e, int d
    ) {
        this(
            b.ENCODE, e, b.DECODE, d
        );
    }

    /**
     * @param encode the specified encode table
     * @param e      the pair num of lines encoded
     * @param decode the specified decode table
     * @param d      the pair num of lines decoded
     */
    public Base64(
        @NotNull byte[] encode, int e,
        @NotNull byte[] decode, int d
    ) {
        E = e;
        D = d;
        ENCODE = encode;
        DECODE = decode;
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting encoded bytes
     *
     * @param data the byte array to encode
     * @throws FatalCrash If encoding byte array as base64 fails
     */
    @NotNull
    public final byte[] encode(
        @NotNull byte[] data
    ) {
        return encode(
            data, 0, data.length
        );
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting encoded bytes
     *
     * @param data the specified chain to encode
     * @throws FatalCrash If encoding byte array as base64 fails
     */
    @NotNull
    public final byte[] encode(
        @NotNull Chain data
    ) {
        return encode(
            value(data), 0, data.length()
        );
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting encoded bytes
     *
     * @param data the specified chain to encode
     * @param i    the specified index
     * @param l    the specified length of array
     * @throws FatalCrash                If encoding byte array as base64 fails
     * @throws IndexOutOfBoundsException If the offset is negative or the length out of range
     */
    @NotNull
    public final byte[] encode(
        @NotNull Chain data, int i, int l
    ) {
        int count = data.length();
        if (i <= count - l && 0 <= i && 0 <= l) {
            return encode(
                value(data), i, l
            );
        } else {
            throw new IndexOutOfBoundsException(
                "Specified offset(" + i + ")/length("
                    + l + ") index is out of bounds: " + count
            );
        }
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting encoded bytes
     *
     * @param data the byte array to encode
     * @param i    the specified index
     * @param l    the specified length of array
     * @throws FatalCrash If encoding byte array as base64 fails
     */
    @NotNull
    public final byte[] encode(
        @NotNull byte[] data, int i, int l
    ) {
        if (l <= 0) {
            return EMPTY_BYTES;
        }

        try {
            int t1 = l % 3;
            int t2 = l / 3;

            int b1, b2, b3;
            int b = 0, m = E,
                c = t2 * 3 + i;

            byte[] it,
                table = ENCODE;

            if (m == 0) {
                if (t1 == 0) {
                    it = new byte[t2 * 4];
                } else {
                    it = new byte[t2 * 4 + 4];
                }

                while (i < c) {
                    b1 = data[i++] & 0xFF;
                    b2 = data[i++] & 0xFF;
                    b3 = data[i++] & 0xFF;

                    it[b++] = table[b1 >>> 2];
                    it[b++] = table[((b1 & 0x3) << 4) | (b2 >>> 4)];
                    it[b++] = table[((b2 & 0xF) << 2) | (b3 >>> 6)];
                    it[b++] = table[b3 & 0x3F];
                }
            } else {
                if (t1 != 0) {
                    it = new byte[t2 * 4 + 4 + (t2 / m) * 2];
                } else {
                    it = new byte[t2 * 4 + ((t2 - 1) / m) * 2];
                }

                int k = 0;
                while (i < c) {
                    b1 = data[i++] & 0xFF;
                    b2 = data[i++] & 0xFF;
                    b3 = data[i++] & 0xFF;

                    it[b++] = table[b1 >>> 2];
                    it[b++] = table[((b1 & 0x3) << 4) | (b2 >>> 4)];
                    it[b++] = table[((b2 & 0xF) << 2) | (b3 >>> 6)];
                    it[b++] = table[b3 & 0x3F];

                    if (++k == m && b < it.length) {
                        k = 0;
                        it[b++] = '\r';
                        it[b++] = '\n';
                    }
                }
            }

            if (t1 != 0) {
                b1 = data[i++] & 0xFF;
                if (t1 == 1) {
                    it[b++] = table[b1 >>> 2];
                    it[b++] = table[(b1 & 0x3) << 4];
                    it[b++] = '=';
                } else {
                    b2 = data[i] & 0xFF;
                    it[b++] = table[b1 >>> 2];
                    it[b++] = table[((b1 & 0x3) << 4) | (b2 >>> 4)];
                    it[b++] = table[(b2 & 0xF) << 2];
                }
                it[b] = '=';
            }

            return it;
        } catch (Exception e) {
            throw new FatalCrash(
                "Failed to encode byte array as base64", e
            );
        }
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting decoded bytes
     *
     * @param data the byte array to decode
     * @throws FatalCrash If decoding byte array from base64 fails
     */
    @NotNull
    public final byte[] decode(
        @NotNull byte[] data
    ) {
        return decode(
            data, 0, data.length
        );
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting decoded bytes
     *
     * @param data the byte array to decode
     * @throws FatalCrash If decoding byte array from base64 fails
     */
    @NotNull
    public final byte[] decode(
        @NotNull Chain data
    ) {
        return decode(
            value(data), 0, data.length()
        );
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting decoded bytes
     *
     * @param data the specified chain to decode
     * @param i    the specified index
     * @param l    the specified length of array
     * @throws FatalCrash                If decoding byte array from base64 fails
     * @throws IndexOutOfBoundsException If the offset is negative or the length out of range
     */
    @NotNull
    public final byte[] decode(
        @NotNull Chain data, int i, int l
    ) {
        int count = data.length();
        if (i <= count - l && 0 <= i && 0 <= l) {
            return decode(
                value(data), i, l
            );
        } else {
            throw new IndexOutOfBoundsException(
                "Specified offset(" + i + ")/length("
                    + l + ") index is out of bounds: " + count
            );
        }
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting decoded bytes
     *
     * @param data the byte array to decode
     * @param i    the specified index
     * @param l    the specified length of array
     * @throws FatalCrash If decoding byte array from base64 fails
     */
    @NotNull
    public final byte[] decode(
        @NotNull byte[] data, int i, int l
    ) {
        if (l < 2) {
            return EMPTY_BYTES;
        }

        try {
            int r, m = D,
                n = i + l;
            if (m != 0) {
                int u = i + m;
                while (u < n) {
                    byte b = data[u];
                    if (b == '\r' ||
                        b == '\n') {
                        l--;
                        u++;
                    } else {
                        u += m;
                    }
                }
                if (i + l == n) m = 0;
            }

            int s = ((l + 3) / 4) * 3;
            if (data[n - 1] == '=') {
                if (data[n - 2] == '=') {
                    s -= 2;
                } else {
                    s -= 1;
                }
            } else {
                r = l & 0x3;
                if (r != 0) {
                    s += r - 0x4;
                }
            }

            int b1, b2, b3, b4;
            byte[] it = new byte[s], table = DECODE;

            for (int k = 0; (r = s - k) > 0; i++) {
                b1 = table[data[i] & 0xFF];
                if (b1 == -1) {
                    return EMPTY_BYTES;
                }

                b2 = table[data[++i] & 0xFF];
                if (b2 == -1) {
                    return EMPTY_BYTES;
                }

                if (r > 2) {
                    b3 = table[data[++i] & 0xFF];
                    if (b3 == -1) {
                        return EMPTY_BYTES;
                    }

                    b4 = table[data[++i] & 0xFF];
                    if (b4 == -1) {
                        return EMPTY_BYTES;
                    }

                    it[k++] = (byte) (
                        (b1 << 2) | (b2 >> 4)
                    );
                    it[k++] = (byte) (
                        ((b2 & 0xF) << 4) | (b3 >> 2)
                    );
                    it[k++] = (byte) (
                        ((b3 & 0x3) << 6) | b4
                    );

                    if (m != 0) {
                        while (++i < n) {
                            byte b = data[i];
                            if (b != '\r' &&
                                b != '\n') {
                                i--;
                                break;
                            }
                        }
                    }
                } else if (r == 2) {
                    b3 = table[data[++i] & 0xFF];
                    if (b3 == -1) {
                        return EMPTY_BYTES;
                    }

                    it[k++] = (byte) (
                        (b1 << 2) | (b2 >> 4)
                    );
                    it[k++] = (byte) (
                        ((b2 & 0xF) << 4) | (b3 >> 2)
                    );
                } else {
                    it[k++] = (byte) (
                        (b1 << 2) | (b2 >> 4)
                    );
                }
            }

            return it;
        } catch (Exception e) {
            throw new FatalCrash(
                "Failed to decode byte array from base64", e
            );
        }
    }

    /**
     * Returns REC4648 singleton
     */
    public static Base64 base() {
        return BASE;
    }

    /**
     * Returns RFC2045 singleton
     */
    public static Base64 mime() {
        return MIME;
    }

    /**
     * Returns REC4648_SAFE singleton
     */
    public static Base64 safe() {
        return SAFE;
    }

    /**
     * Base64 singleton
     */
    private static final Base64
        BASE, MIME, SAFE;

    static {
        // RFC4648|Basic
        BASE = new Base64(
            Binary.Unsafe.RFC4648_ENCODE,
            Binary.Unsafe.RFC4648_DECODE
        );

        // RFC2045|MIME
        // en = 19, 76 = 19 * 4
        // de = 4, for compatibility
        MIME = new Base64(
            BASE, 19, 4
        );

        // RFC4648_SAFE|URL/Filename Safe
        SAFE = new Base64(
            Binary.Unsafe.RFC4648_SAFE_ENCODE,
            Binary.Unsafe.RFC4648_SAFE_DECODE
        );
    }
}
