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
 * @since 0.0.1
 */
public class Radar implements Solver {
    /**
     * snapshot
     */
    protected final Alias alias;
    protected final Space space;
    protected final Value value;

    /**
     * Constructs a radar with the specified alias, space and value
     *
     * @param alias the specified {@code alias} of radar
     * @param space the specified {@code space} of radar
     * @param value the specified {@code value} of radar
     */
    public Radar(
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
     * Reads kat flow
     *
     * <pre>{@code
     *  # this is a test entity
     *  @plus.kat.entity.User {
     *     id:Int = 1,
     *     name:String = kraity
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

            // local buffer
            byte[] key = a.flow();
            byte[] kat = s.flow();
            byte[] val = v.flow();

            // local status
            int i = 1, m = 1, o;
            int x = 0, y = 0, z = 0;

            Radar:
            // decode kat flow
            while (true) {
                byte w = u.i < u.l ?
                    u.v[u.i++] : u.read();
                switch (w) {
                    case 0x7B:
                    case 0x5B: {
                        if (m < m << i) {
                            Pipe pipe = n.onOpen(
                                a.slip(x), s.slip(y, w)
                            );
                            if (pipe != null) {
                                n = pipe;
                                x = y = z = 0;
                                m = (i = w >> 5 ^ 3) | m << 1;
                            } else {
                                wipe(u, w);
                                if (u.also()) {
                                    i = 0x81;
                                    x = y = z = 0;
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
                    case 0x00: {
                        if (m == 1) {
                            if (z != 0) {
                                n.onNext(
                                    a.slip(x),
                                    s.slip(y),
                                    v.slip(z)
                                );
                            }
                            break Radar;
                        } else {
                            throw new IOException(
                                "Missing part of data, iv: "
                                    + Integer.toBinaryString(i)
                            );
                        }
                    }
                    case 0x40: {
                        if (i == 1 && x == 0) {
                            i = 0x21;
                            break;
                        } else {
                            throw new IOException(
                                "Requires `@` can't be " +
                                    "repeated, alias is empty"
                            );
                        }
                    }
                    case 0x3D: {
                        switch (i) {
                            case 0x00:
                            case 0x20:
                            case 0x40:
                            case 0x60: {
                                i = 0x01;
                                continue;
                            }
                        }
                        throw new IOException(
                            "Requires `=` can't be here"
                        );
                    }
                    case 0x3A: {
                        if (i == 0 && x != 0) {
                            i = 0x20;
                            break;
                        } else {
                            throw new IOException(
                                "Requires `:` can't be " +
                                    "repeated, alias is not empty"
                            );
                        }
                    }
                    case 0x09:
                    case 0x0A:
                    case 0x0D:
                    case 0x20: {
                        switch (i) {
                            case 0x20: {
                                i = 0x60;
                                continue;
                            }
                            case 0x21: {
                                i = 0x01;
                                continue;
                            }
                            case 0x40:
                            case 0x41:
                            case 0x60:
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
                    case 0x22:
                    case 0x27: {
                        switch (i) {
                            case 0x00: {
                                if (x == 0) {
                                    break;
                                } else {
                                    throw new IOException(
                                        "The Alias is not empty"
                                    );
                                }
                            }
                            case 0x01: {
                                if (z == 0) {
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
                    }
                    case 0x5C: {
                        byte[] g;
                        switch (i) {
                            case 0x00: {
                                o = x;
                                g = key;
                                break;
                            }
                            case 0x01: {
                                o = z;
                                g = val;
                                break;
                            }
                            case 0x20:
                            case 0x21: {
                                o = y;
                                g = key;
                                break;
                            }
                            default: {
                                throw new IOException(
                                    "Illegal escape area, iv: "
                                        + Integer.toBinaryString(i)
                                );
                            }
                        }

                        byte it = w;
                        Scope:
                        while (true) {
                            if (it != 0x5C) {
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
                                                        s.slip(y, w),
                                                        v.slip(o, w)
                                                    );
                                                    i = 0x81;
                                                    x = y = 0;
                                                    break;
                                                }
                                            }
                                            continue Radar;
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
                            }

                            while (true) {
                                w = u.i < u.l ?
                                    u.v[u.i++] : u.next();
                                switch (w) {
                                    case 0x73:
                                        g[o++] = ' ';
                                        break;
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

                                w = u.i < u.l ?
                                    u.v[u.i++] : u.next();
                                if (w != 0x5C) {
                                    u.i--;
                                    if (it == 0x5C) {
                                        switch (i) {
                                            case 0x00: {
                                                x = o;
                                                break;
                                            }
                                            case 0x01: {
                                                z = o;
                                                break;
                                            }
                                            case 0x20:
                                            case 0x21: {
                                                y = o;
                                                break;
                                            }
                                        }
                                        continue Radar;
                                    } else {
                                        continue Scope;
                                    }
                                }
                            }
                        }
                    }
                    case 0x2C: {
                        if (i == 1 && (x | z) != 0) {
                            n.onNext(
                                a.slip(x),
                                s.slip(y),
                                v.slip(z)
                            );
                            i = m & 1;
                            x = y = z = 0;
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
                    case 0x23: {
                        if (i < 2 && x == 0) {
                            while (true) {
                                switch (u.next()) {
                                    case 0x0A:
                                    case 0x0D:
                                    case 0x23: {
                                        continue Radar;
                                    }
                                }
                            }
                        } else {
                            throw new IOException(
                                "Requires `#` only in " +
                                    "whitespace, alias is empty"
                            );
                        }
                    }
                    case 0x21:
                    case 0x24:
                    case 0x25:
                    case 0x26:
                    case 0x28:
                    case 0x29:
                    case 0x2A:
                    case 0x2B:
                    case 0x2D:
                    case 0x2E:
                    case 0x2F:
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
                    case 0x3B:
                    case 0x3C:
                    case 0x3E:
                    case 0x3F:
                    case 0x41:
                    case 0x42:
                    case 0x43:
                    case 0x44:
                    case 0x45:
                    case 0x46:
                    case 0x47:
                    case 0x48:
                    case 0x49:
                    case 0x4A:
                    case 0x4B:
                    case 0x4C:
                    case 0x4D:
                    case 0x4E:
                    case 0x4F:
                    case 0x50:
                    case 0x51:
                    case 0x52:
                    case 0x53:
                    case 0x54:
                    case 0x55:
                    case 0x56:
                    case 0x57:
                    case 0x58:
                    case 0x59:
                    case 0x5A:
                    case 0x5E:
                    case 0x5F:
                    case 0x60:
                    case 0x61:
                    case 0x62:
                    case 0x63:
                    case 0x64:
                    case 0x65:
                    case 0x66:
                    case 0x67:
                    case 0x68:
                    case 0x69:
                    case 0x6A:
                    case 0x6B:
                    case 0x6C:
                    case 0x6D:
                    case 0x6E:
                    case 0x6F:
                    case 0x70:
                    case 0x71:
                    case 0x72:
                    case 0x73:
                    case 0x74:
                    case 0x75:
                    case 0x76:
                    case 0x77:
                    case 0x78:
                    case 0x79:
                    case 0x7A:
                    case 0x7C:
                    case 0x7E:
                    default: {
                        switch (i) {
                            case 0x00: {
                                key[x++] = w;
                                continue;
                            }
                            case 0x01: {
                                val[z++] = w;
                                continue;
                            }
                            case 0x20:
                            case 0x21: {
                                kat[y++] = w;
                                continue;
                            }
                        }
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
                                    s.slip(y),
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
                                        w = u.i < u.l ?
                                            u.v[u.i++] : u.next();
                                        switch (w) {
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
                                                x = y = z = 0;
                                                continue Radar;
                                            }
                                        }
                                        throw new IOException(
                                            "Missing a comma here"
                                        );
                                    }
                                }
                                break Radar;
                            }
                            else {
                                throw new IOException(
                                    "Missing parent pipeline"
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
                case 0x23: {
                    while (true) {
                        switch (u.next()) {
                            case 0x0A:
                            case 0x0D:
                            case 0x23: {
                                continue Scope;
                            }
                        }
                    }
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
     * Clean up this {@link Radar}
     */
    @Override
    public void clear() {
        space.clear();
        alias.clear();
        value.clear();
    }

    /**
     * Returns an instance of {@link Radar}
     */
    @NotNull
    public static Radar apply() {
        return new Radar(
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
