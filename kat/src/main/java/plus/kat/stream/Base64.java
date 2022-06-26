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
    byte[] encode(
        @NotNull byte[] src
    );

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
    byte[] decode(
        @NotNull byte[] src
    );

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
    static REC4648 base() {
        return REC4648.INS;
    }

    /**
     * Returns RFC2045 singleton
     */
    static RFC2045 mime() {
        return RFC2045.INS;
    }

    /**
     * Returns REC4648_SAFE singleton
     */
    static RFC4648_SAFE safe() {
        return RFC4648_SAFE.INS;
    }

    /**
     * @param input the byte array to encode
     * @param i     the start index
     * @param l     the length of byte array
     * @param m     the max number of line
     * @param table the byte array of coding table
     */
    @NotNull
    static byte[] encode(
        @NotNull byte[] input,
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

        if (m <= 0) {
            if (t1 == 0) {
                it = new byte[t2 * 4];
            } else {
                it = new byte[t2 * 4 + 4];
            }

            while (i < c) {
                b1 = input[i++] & 0xFF;
                b2 = input[i++] & 0xFF;
                b3 = input[i++] & 0xFF;

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

            int x = 0;
            while (i < c) {
                b1 = input[i++] & 0xFF;
                b2 = input[i++] & 0xFF;
                b3 = input[i++] & 0xFF;

                it[b++] = table[b1 >>> 2];
                it[b++] = table[((b1 & 0x3) << 4) | (b2 >>> 4)];
                it[b++] = table[((b2 & 0xF) << 2) | (b3 >>> 6)];
                it[b++] = table[b3 & 0x3F];

                if (++x == m && b < it.length) {
                    x = 0;
                    it[b++] = '\r';
                    it[b++] = '\n';
                }
            }
        }

        if (t1 != 0) {
            b1 = input[i++] & 0xFF;
            if (t1 == 1) {
                it[b++] = table[b1 >>> 2];
                it[b++] = table[(b1 & 0x3) << 4];
                it[b++] = '=';
            } else {
                b2 = input[i] & 0xFF;
                it[b++] = table[b1 >>> 2];
                it[b++] = table[((b1 & 0x3) << 4) | (b2 >>> 4)];
                it[b++] = table[(b2 & 0xF) << 2];
            }
            it[b] = '=';
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

        private static final byte[] TABLE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
        };

        public int get(
            byte b
        ) {
            // a-z
            if (b > 0x60) {
                if (b > 0x7A) {
                    return -1;
                }
                return b - 0x47;
            }

            // A-Z
            if (b > 0x40) {
                if (b > 0x5A) {
                    return -1;
                }
                return b - 0x41;
            }

            // 0-9
            if (b > 0x2F) {
                if (b < 0x3A) {
                    return b + 0x4;
                }
                return b == '=' ? -2 : -1;
            }

            if (b == '/') {
                return 63;
            }

            return b == '+' ? 62 : -1;
        }

        @NotNull
        @Override
        public byte[] encode(
            @NotNull byte[] d
        ) {
            return Base64.encode(
                d, 0, d.length, 0, TABLE
            );
        }

        @NotNull
        @Override
        public byte[] encode(
            @NotNull byte[] d, int i, int l
        ) {
            return Base64.encode(
                d, i, l, 0, TABLE
            );
        }

        @NotNull
        @Override
        public byte[] decode(
            @NotNull byte[] d
        ) {
            return decode(
                d, 0, d.length
            );
        }

        @NotNull
        @Override
        public byte[] decode(
            @NotNull byte[] d, int i, int l
        ) {
            if (l < 2) {
                return EMPTY_BYTES;
            }

            int r, e = i + l,
                s = ((l + 3) / 4) * 3;
            if (d[e - 1] == '=') {
                if (d[e - 2] == '=') {
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

            int o = 0, b1, b2, b3, b4;
            byte[] it = new byte[s];

            while ((r = s - o) > 0) {
                b1 = get(d[i++]);
                b2 = get(d[i++]);

                if (r > 2) {
                    b3 = get(d[i++]);
                    b4 = get(d[i++]);

                    it[o++] = (byte) (
                        (b1 << 2) | (b2 >> 4)
                    );
                    it[o++] = (byte) (
                        ((b2 & 0xF) << 4) | (b3 >> 2)
                    );
                    it[o++] = (byte) (
                        ((b3 & 0x3) << 6) | b4
                    );
                } else if (r == 2) {
                    b3 = get(d[i++]);

                    it[o++] = (byte) (
                        (b1 << 2) | (b2 >> 4)
                    );
                    it[o++] = (byte) (
                        ((b2 & 0xF) << 4) | (b3 >> 2)
                    );
                } else {
                    it[o++] = (byte) (
                        (b1 << 2) | (b2 >> 4)
                    );
                }
            }

            return it;
        }
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    class RFC2045 extends REC4648 {

        public static final RFC2045
            INS = new RFC2045();

        @NotNull
        @Override
        public byte[] encode(
            @NotNull byte[] d
        ) {
            return Base64.encode(
                d, 0, d.length, 19, REC4648.TABLE
            );
        }

        @NotNull
        @Override
        public byte[] encode(
            @NotNull byte[] d, int i, int l
        ) {
            return Base64.encode(
                d, 0, d.length, 19, REC4648.TABLE
            );
        }

        @NotNull
        @Override
        public byte[] decode(
            @NotNull byte[] d, int i, int l
        ) {
            if (l < 2) {
                return EMPTY_BYTES;
            }

            int e = i + l;
            for (int u = i + 4; u < e; ) {
                if (d[u] > 0x20) {
                    u += 4;
                } else {
                    l--;
                    u++;
                }
            }

            int r, s = ((l + 3) / 4) * 3;
            if (d[e - 1] == '=') {
                if (d[e - 2] == '=') {
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

            int o = 0, b1, b2, b3, b4;
            byte[] it = new byte[s];

            while ((r = s - o) > 0) {
                b1 = get(d[i++]);
                b2 = get(d[i++]);

                if (r > 2) {
                    b3 = get(d[i++]);
                    b4 = get(d[i++]);

                    it[o++] = (byte) (
                        (b1 << 2) | (b2 >> 4)
                    );
                    it[o++] = (byte) (
                        ((b2 & 0xF) << 4) | (b3 >> 2)
                    );
                    it[o++] = (byte) (
                        ((b3 & 0x3) << 6) | b4
                    );

                    for (; i < e; i++) {
                        if (d[i] > 0x20) {
                            break;
                        }
                    }
                } else if (r == 2) {
                    b3 = get(d[i++]);

                    it[o++] = (byte) (
                        (b1 << 2) | (b2 >> 4)
                    );
                    it[o++] = (byte) (
                        ((b2 & 0xF) << 4) | (b3 >> 2)
                    );
                } else {
                    it[o++] = (byte) (
                        (b1 << 2) | (b2 >> 4)
                    );
                }
            }

            return it;
        }
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    class RFC4648_SAFE extends REC4648 {

        public static final RFC4648_SAFE
            INS = new RFC4648_SAFE();

        private static final byte[] TABLE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
        };

        @Override
        public int get(
            byte b
        ) {
            // a-z
            if (b > 0x60) {
                if (b > 0x7A) {
                    return -1;
                }
                return b - 0x47;
            }

            // A-Z
            if (b > 0x40) {
                if (b < 0x5B) {
                    return b - 0x41;
                }
                return b == '_' ? 63 : -1;
            }

            // 0-9
            if (b > 0x2F) {
                if (b < 0x3A) {
                    return b + 0x4;
                }
                return b == '=' ? -2 : -1;
            }

            return b == '-' ? 62 : -1;
        }

        @NotNull
        @Override
        public byte[] encode(
            @NotNull byte[] d
        ) {
            return Base64.encode(
                d, 0, d.length, 0, TABLE
            );
        }

        @NotNull
        @Override
        public byte[] encode(
            @NotNull byte[] d, int i, int l
        ) {
            return Base64.encode(
                d, i, l, 0, TABLE
            );
        }
    }
}
