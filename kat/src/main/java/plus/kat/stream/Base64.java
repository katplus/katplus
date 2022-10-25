package plus.kat.stream;

import plus.kat.anno.NotNull;

import static plus.kat.kernel.Chain.EMPTY_BYTES;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Base64 {

    private final int E, D;
    private final byte[] ENCO, DECO;

    /**
     * @param enco the specified encode table
     * @param deco the specified decode table
     */
    public Base64(
        @NotNull byte[] enco,
        @NotNull byte[] deco
    ) {
        this(
            enco, 0, deco, 0
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
            b.ENCO, e, b.DECO, d
        );
    }

    /**
     * @param enco the specified encode table
     * @param e    the pair num of lines encoded
     * @param deco the specified decode table
     * @param d    the pair num of lines decoded
     */
    public Base64(
        @NotNull byte[] enco, int e,
        @NotNull byte[] deco, int d
    ) {
        E = e;
        D = d;
        ENCO = enco;
        DECO = deco;
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting encoded bytes
     *
     * @param data the byte array to encode
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
     * @param data the byte array to encode
     * @param i    the specified index
     * @param l    the specified length of array
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
                table = ENCO;

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
            return EMPTY_BYTES;
        }
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting decoded bytes
     *
     * @param data the byte array to decode
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
     * @param i    the specified index
     * @param l    the specified length of array
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
            byte[] it = new byte[s], table = DECO;

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
            return EMPTY_BYTES;
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
        // REC4648|Basic
        BASE = new Base64(new byte[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
        }, new byte[]{
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, // 20-2f + /
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, // 30-3f 0-9
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,           // 40-4f A-O
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, // 50-5f P-Z
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 60-6f a-o
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51                      // 70-7a p-z
        });

        // RFC2045|MIME
        // en = 19, 76 = 19 * 4
        // de = 4, for compatibility
        MIME = new Base64(
            BASE, 19, 4
        );

        // RFC4648_SAFE|URL/Filename Safe
        SAFE = new Base64(new byte[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
        }, new byte[]{
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, // 20-2f -
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, // 30-3f 0-9
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,           // 40-4f A-O
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, // 50-5f P-Z _
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 60-6f a-o
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51                      // 70-7a p-z
        });
    }
}
