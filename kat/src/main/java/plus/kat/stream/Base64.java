package plus.kat.stream;

import plus.kat.anno.NotNull;

import static plus.kat.kernel.Chain.EMPTY_BYTES;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Base64 {
    /**
     * Returns a newly-allocated byte array
     * containing the resulting encoded bytes
     *
     * @param src the byte array to encode
     */
    @NotNull
    default byte[] encode(
        @NotNull byte[] src
    ) {
        return encode(
            src, 0, src.length
        );
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting encoded bytes
     *
     * @param src    the byte array to encode
     * @param index  the start index
     * @param length the length of byte array
     */
    @NotNull
    byte[] encode(
        @NotNull byte[] src, int index, int length
    );

    /**
     * Returns a newly-allocated byte array
     * containing the resulting decoded bytes
     *
     * @param src the byte array to decode
     */
    @NotNull
    default byte[] decode(
        @NotNull byte[] src
    ) {
        return decode(
            src, 0, src.length
        );
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting decoded bytes
     *
     * @param src    the byte array to decode
     * @param index  the start index
     * @param length the length of byte array
     */
    @NotNull
    byte[] decode(
        @NotNull byte[] src, int index, int length
    );

    /**
     * Returns REC4648 singleton
     */
    static Base64 base() {
        return REC4648.INS;
    }

    /**
     * Returns RFC2045 singleton
     */
    static Base64 mime() {
        return RFC2045.INS;
    }

    /**
     * Returns REC4648_SAFE singleton
     */
    static Base64 safe() {
        return RFC4648_SAFE.INS;
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting encoded bytes
     *
     * @param data  the byte array to encode
     * @param i     the start index
     * @param l     the length of byte array
     * @param m     the max number of line
     * @param table the byte array of coding table
     * @throws ArrayIndexOutOfBoundsException If the index accessed is out of range
     */
    @NotNull
    static byte[] encode(
        @NotNull byte[] data,
        int i, int l, int m,
        @NotNull byte[] table
    ) {
        if (l <= 0) {
            return EMPTY_BYTES;
        }

        int t1 = l % 3;
        int t2 = l / 3;

        byte[] it;
        int b1, b2, b3;
        int b = 0, c = t2 * 3 + i;

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
    }

    /**
     * Returns a newly-allocated byte array
     * containing the resulting decoded bytes
     *
     * @param data  the byte array to decode
     * @param i     the start index
     * @param l     the length of byte array
     * @param m     the max number of line
     * @param table the byte array of decoding table
     * @throws IllegalArgumentException       If illegal base64 characters appear
     * @throws ArrayIndexOutOfBoundsException If the index accessed is out of range
     */
    @NotNull
    static byte[] decode(
        @NotNull byte[] data,
        int i, int l, int m,
        @NotNull byte[] table
    ) {
        if (l < 2) {
            return EMPTY_BYTES;
        }

        int r, e = i + l;
        if (m != 0) {
            int u = i + m;
            while (u < e) {
                byte b = data[u];
                if (b == '\r' ||
                    b == '\n') {
                    l--;
                    u++;
                } else {
                    u += m;
                }
            }
            if (i + l == e) m = 0;
        }

        int s = ((l + 3) / 4) * 3;
        if (data[e - 1] == '=') {
            if (data[e - 2] == '=') {
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
        byte[] it = new byte[s];

        for (int k = 0; (r = s - k) > 0; i++) {
            b1 = table[data[i] & 0xFF];
            if (b1 == -1) {
                throw new IllegalArgumentException(
                    "Illegal index " + i + ": 0x"
                        + Integer.toHexString(data[i])
                );
            }

            b2 = table[data[++i] & 0xFF];
            if (b2 == -1) {
                throw new IllegalArgumentException(
                    "Illegal index " + i + ": 0x"
                        + Integer.toHexString(data[i])
                );
            }

            if (r > 2) {
                b3 = table[data[++i] & 0xFF];
                if (b3 == -1) {
                    throw new IllegalArgumentException(
                        "Illegal index " + i + ": 0x"
                            + Integer.toHexString(data[i])
                    );
                }

                b4 = table[data[++i] & 0xFF];
                if (b4 == -1) {
                    throw new IllegalArgumentException(
                        "Illegal index " + i + ": 0x"
                            + Integer.toHexString(data[i])
                    );
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
                    while (++i < e) {
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
                    throw new IllegalArgumentException(
                        "Illegal index " + i + ": 0x"
                            + Integer.toHexString(data[i])
                    );
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
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    class REC4648 implements Base64 {

        public static final REC4648
            INS = new REC4648();

        private static final byte[] ENCODE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
        };

        private static final byte[] DECODE = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, // 20-2f + /
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, // 30-3f 0-9
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,           // 40-4f A-O
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, // 50-5f P-Z
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 60-6f a-o
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51                      // 70-7a p-z
        };

        @Override
        public byte[] encode(
            @NotNull byte[] d, int i, int l
        ) {
            try {
                return Base64.encode(
                    d, i, l, 0, ENCODE
                );
            } catch (Exception e) {
                return EMPTY_BYTES;
            }
        }

        @Override
        public byte[] decode(
            @NotNull byte[] d, int i, int l
        ) {
            try {
                return Base64.decode(
                    d, i, l, 0, DECODE
                );
            } catch (Exception e) {
                return EMPTY_BYTES;
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    class RFC2045 implements Base64 {

        public static final RFC2045
            INS = new RFC2045();

        @Override
        public byte[] encode(
            @NotNull byte[] d, int i, int l
        ) {
            try {
                // m = 19, MIME 76 = 19 * 4
                return Base64.encode(
                    d, i, l, 19, REC4648.ENCODE
                );
            } catch (Exception e) {
                return EMPTY_BYTES;
            }
        }

        @Override
        public byte[] decode(
            @NotNull byte[] d, int i, int l
        ) {
            try {
                // m = 4, for compatibility
                return Base64.decode(
                    d, i, l, 4, REC4648.DECODE
                );
            } catch (Exception e) {
                return EMPTY_BYTES;
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    class RFC4648_SAFE extends REC4648 {

        public static final RFC4648_SAFE
            INS = new RFC4648_SAFE();

        private static final byte[] ENCODE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
        };

        private static final byte[] DECODE = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, // 20-2f -
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, // 30-3f 0-9
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,           // 40-4f A-O
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, // 50-5f P-Z _
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 60-6f a-o
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51                      // 70-7a p-z
        };

        @Override
        public byte[] encode(
            @NotNull byte[] d, int i, int l
        ) {
            try {
                return Base64.encode(
                    d, i, l, 0, ENCODE
                );
            } catch (Exception e) {
                return EMPTY_BYTES;
            }
        }

        @Override
        public byte[] decode(
            @NotNull byte[] d, int i, int l
        ) {
            try {
                return Base64.decode(
                    d, i, l, 0, DECODE
                );
            } catch (Exception e) {
                return EMPTY_BYTES;
            }
        }
    }
}
