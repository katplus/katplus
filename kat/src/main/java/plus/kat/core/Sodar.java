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
package plus.kat.core;

import plus.kat.*;
import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.IOException;

import static plus.kat.lang.Uniform.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public class Sodar implements Solver {
    /**
     * snapshot
     */
    protected final Alias alias;
    protected final Space space;
    protected final Value value;

    /**
     * Constructs a sodar with the specified alias, space and value
     *
     * @param alias the specified {@code alias} of solver
     * @param space the specified {@code space} of solver
     * @param value the specified {@code value} of solver
     */
    public Sodar(
        @NotNull Alias alias,
        @NotNull Space space,
        @NotNull Value value
    ) {
        if (alias != null &&
            space != null &&
            value != null) {
            this.alias = alias;
            this.space = space;
            this.value = value;
        } else {
            throw new NullPointerException(
                "Received: (" + alias + ", "
                    + space + ", " + value + ")"
            );
        }
    }

    /**
     * Reads json stream
     *
     * <pre>{@code
     *  {
     *     "id": 1,
     *     "name": "kraity"
     *  }
     * }</pre>
     *
     * @throws IOException           If a read error occurs
     * @throws IllegalStateException If a fatal error occurs
     */
    @Override
    public void solve(
        @NotNull Flow u,
        @NotNull Pipe n
    ) throws IOException {
        try {
            // local access
            Alias a = alias;
            Space s = space;
            Value v = value;

            // local status
            int i = 1, m = 1;
            int x = 0, z = 0, o;

            // local buffer
            byte[] key = a.flow();
            byte[] val = v.flow();

            Sodar:
            // decode json stream
            while (true) {
                byte w = u.i < u.l ?
                    u.v[u.i++] : u.read();
                switch (w) {
                    case 0x7B:
                    case 0x5B: {
                        if (m < m << i) {
                            Pipe pipe = n.onOpen(
                                a.slip(x), s.slip(0, w)
                            );
                            if (pipe != null) {
                                n = pipe;
                                x = z = 0;
                                m = (i = w >> 5 ^ 3) | m << 1;
                            } else {
                                wipe(u, w);
                                if (u.also()) {
                                    i = 0x81;
                                    x = z = 0;
                                } else {
                                    throw new IOException(
                                        "No more data after clean"
                                    );
                                }
                            }
                            continue;
                        }
                        throw new IOException(
                            "Reach the limit depth or otherwise, "
                                + "Mask: " + Integer.toBinaryString(m)
                        );
                    }
                    case 0x01:
                    case 0x02:
                    case 0x03:
                    case 0x04:
                    case 0x05:
                    case 0x06:
                    case 0x07:
                    case 0x08:
                    case 0x0B:
                    case 0x0C:
                    case 0x0E:
                    case 0x0F:
                    case 0x10:
                    case 0x11:
                    case 0x12:
                    case 0x13:
                    case 0x14:
                    case 0x15:
                    case 0x16:
                    case 0x17:
                    case 0x18:
                    case 0x19:
                    case 0x1A:
                    case 0x1B:
                    case 0x1C:
                    case 0x1D:
                    case 0x1E:
                    case 0x1F:
                    case 0x7F: {
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
                        );
                    }
                    case 0x09:
                    case 0x0A:
                    case 0x0D:
                    case 0x20: {
                        switch (i) {
                            case 0x40:
                            case 0x41:
                            case 0x81: {
                                continue;
                            }
                            case 0x00: {
                                if (x != 0) i = 0x40;
                                continue;
                            }
                            case 0x01: {
                                if (z != 0) i = 0x41;
                                continue;
                            }
                        }
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
                        );
                    }
                    case 0x00: {
                        if (m == 0x1) {
                            if (z != 0) {
                                n.onNext(
                                    a.slip(x),
                                    s.slip(0),
                                    v.slip(z)
                                );
                            }
                            break Sodar;
                        } else {
                            throw new IOException(
                                "Missing part of data, iv: "
                                    + Integer.toBinaryString(i)
                            );
                        }
                    }
                    case 0x3A: {
                        if (i == 0x00 || i == 0x40) {
                            i = 0x01;
                            continue;
                        } else {
                            throw new IOException(
                                "Requires `:` can't be here"
                            );
                        }
                    }
                    case 0x22:
                    case 0x27: {
                        byte[] g;
                        switch (i) {
                            case 0x00: {
                                if (x == 0) {
                                    o = 0;
                                    g = key;
                                    break;
                                } else {
                                    throw new IOException(
                                        "The Alias is not empty"
                                    );
                                }
                            }
                            case 0x01: {
                                if (z == 0) {
                                    o = 0;
                                    g = val;
                                    break;
                                } else {
                                    throw new IOException(
                                        "The Value is not empty"
                                    );
                                }
                            }
                            default: {
                                throw new IOException(
                                    "Illegal value block, iv: "
                                        + Integer.toBinaryString(i)
                                );
                            }
                        }

                        byte it = w;
                        Scope:
                        while (true) {
                            int j = u.i,
                                l = j,
                                k = u.l;
                            byte[] e = u.v;
                            while (true) {
                                if (j < k) {
                                    w = e[j];
                                    if (w != it &&
                                        w != 0x5C) {
                                        j++;
                                        continue;
                                    }
                                }

                                if (l < j) {
                                    System.arraycopy(
                                        e, l, g,
                                        o, j - l
                                    );
                                    o = j - l + o;
                                }

                                if (j < k) {
                                    u.i = j + 1;
                                    if (w == 0x5C) {
                                        break;
                                    } else {
                                        switch (i) {
                                            case 0x00: {
                                                x = o;
                                                i = 0x40;
                                                break;
                                            }
                                            case 0x01: {
                                                n.onNext(
                                                    a.slip(x),
                                                    s.slip(0, w),
                                                    v.slip(o, w)
                                                );
                                                x = 0;
                                                i = 0x81;
                                                break;
                                            }
                                        }
                                        continue Sodar;
                                    }
                                }

                                if (u.load() > 0) {
                                    continue Scope;
                                }
                                throw new IOException(
                                    "No more readable bytes, please " +
                                        "check whether this flow is damaged"
                                );
                            }

                            while (true) {
                                w = u.i < u.l ?
                                    u.v[u.i++] : u.next();
                                switch (w) {
                                    case 0x62:
                                        g[o++] = '\b';
                                        break;
                                    case 0x66:
                                        g[o++] = '\f';
                                        break;
                                    case 0x74:
                                        g[o++] = '\t';
                                        break;
                                    case 0x72:
                                        g[o++] = '\r';
                                        break;
                                    case 0x6E:
                                        g[o++] = '\n';
                                        break;
                                    case 0x75:
                                        int H, L = u.code(4);
                                        if (L < 0x80) {
                                            g[o++] = (byte) L;
                                        } else if (L < 0x800) {
                                            g[o++] = (byte) (L >> 6 | 0xC0);
                                            g[o++] = (byte) (L & 0x3F | 0x80);
                                        } else if (L < 0xD800 || 0xDFFF < L) {
                                            g[o++] = (byte) (L >> 12 | 0xE0);
                                            g[o++] = (byte) (L >> 6 & 0x3F | 0x80);
                                            g[o++] = (byte) (L & 0x3F | 0x80);
                                        } else if (0xDC40 > (L += 0x40) &&
                                            0x5C == u.next() && 0x75 == u.next() &&
                                            0xDBFF < (H = u.code(4)) && H < 0xE000) {
                                            g[o++] = (byte) (L >> 8 & 0x07 | 0xF0);
                                            g[o++] = (byte) (L >> 2 & 0x3F | 0x80);
                                            g[o++] = (byte) (L << 4 & 0x30 | H >> 6 & 0x0F | 0x80);
                                            g[o++] = (byte) (H & 0x3F | 0x80);
                                        } else {
                                            throw new IOException("Illegal unicode");
                                        }
                                        break;
                                    default: {
                                        g[o++] = w;
                                    }
                                }

                                w = u.next();
                                if (w != 0x5C) {
                                    u.i--;
                                    continue Scope;
                                }
                            }
                        }
                    }
                    case 0x2C: {
                        if (i == 0x1) {
                            n.onNext(
                                a.slip(x),
                                s.slip(0),
                                v.slip(z)
                            );
                            i = m & 1;
                            x = z = 0;
                        } else if (i == 0x81) {
                            i = m & 1;
                        } else {
                            throw new IOException(
                                "Illegal partition, iv: "
                                    + Integer.toBinaryString(i)
                            );
                        }
                        continue;
                    }
                    case 0x2B:
                    case 0x2D:
                    case 0x2E:
                    case 0x30:
                    case 0x31:
                    case 0x32:
                    case 0x33:
                    case 0x34:
                    case 0x35:
                    case 0x36:
                    case 0x37:
                    case 0x38:
                    case 0x39:
                    case 0x61:
                    case 0x65:
                    case 0x66:
                    case 0x6C:
                    case 0x6E:
                    case 0x72:
                    case 0x73:
                    case 0x74:
                    case 0x75: {
                        if (i == 0x1) {
                            val[z++] = w;
                            continue;
                        }
                    }
                    default: {
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
                        );
                    }
                    case 0x5D:
                    case 0x7D: {
                        if (m > m >> i) {
                            if ((x | z) != 0) {
                                n.onNext(
                                    a.slip(x),
                                    s.slip(0),
                                    v.slip(z)
                                );
                            }
                        } else if ((i | x) != 0) {
                            throw new IOException(
                                "Symbol: `" + w + "`, iv: "
                                    + Integer.toBinaryString(i)
                            );
                        }

                        while ((m & 1 | 2) != w >> 5) {
                            n = n.onClose(true, true);
                            if (n != null) then:{
                                if ((m >>= 1) != 1) {
                                    while (true) {
                                        switch (w = u.next()) {
                                            case 0x09:
                                            case 0x0A:
                                            case 0x0D:
                                            case 0x20: {
                                                continue;
                                            }
                                            case 0x5D:
                                            case 0x7D: {
                                                break then;
                                            }
                                            case 0x2C: {
                                                i = m & 1;
                                                x = z = 0;
                                                continue Sodar;
                                            }
                                        }
                                        throw new IOException(
                                            "The comma is missing"
                                        );
                                    }
                                }
                                break Sodar;
                            }
                            else {
                                throw new IOException(
                                    "The parent pipe is missing"
                                );
                            }
                        }
                        throw new IOException(
                            "Terminator: `" + w + "` is unpaired, "
                                + "Mask: " + Integer.toBinaryString(m)
                        );
                    }
                }
            }
        } finally {
            while (n != null) {
                n = n.onClose(
                    false, false
                );
            }
        }
    }

    /**
     * Filter the currently useless parts
     *
     * @throws IOException If an I/O error occurs
     */
    protected void wipe(
        @NotNull Flow u,
        @NotNull byte w
    ) throws IOException {
        int m = w >> 5 ^ 3 | 2;
        Scope:
        while (true) {
            w = u.i < u.l ?
                u.v[u.i++] : u.next();
            switch (w) {
                case 0x7B:
                case 0x5B: {
                    if ((m <<= 1) > 0) {
                        m |= w >> 5 ^ 3;
                        continue;
                    }
                    throw new IOException(
                        Integer.toBinaryString(m)
                    );
                }
                case 0x22: {
                    while (true) {
                        switch (u.next()) {
                            case 0x5C: {
                                u.next();
                                continue;
                            }
                            case 0x22: {
                                continue Scope;
                            }
                        }
                    }
                }
                case 0x5D:
                case 0x7D: {
                    if ((m & 1 | 2) != w >> 5) {
                        if ((m >>= 1) != 1) {
                            continue;
                        } else break Scope;
                    }
                    throw new IOException(
                        Integer.toBinaryString(m)
                    );
                }
            }
        }
    }

    /**
     * Clean up this {@link Sodar}
     */
    @Override
    public void clear() {
        space.clear();
        alias.clear();
        value.clear();
    }

    /**
     * Returns an instance of {@link Sodar}
     */
    @NotNull
    public static Sodar apply() {
        return new Sodar(
            new Alias(
                ALIAS_CAPACITY
            ),
            new Space(
                SPACE_CAPACITY
            ),
            new Value(
                VALUE_CAPACITY
            )
        );
    }
}
