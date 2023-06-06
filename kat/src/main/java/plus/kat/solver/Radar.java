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
package plus.kat.solver;

import plus.kat.*;
import plus.kat.actor.*;

import plus.kat.chain.*;
import plus.kat.spare.*;

import java.io.IOException;

import static plus.kat.Event.Spoiler.*;

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
     * @param alias the specified {@code alias} of solver
     * @param space the specified {@code space} of solver
     * @param value the specified {@code value} of solver
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
     * Reads kat stream
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
        @NotNull Flow t,
        @NotNull Spider n
    ) throws IOException {
        try {
            // local access
            Alias a = alias;
            Space s = space;
            Value v = value;

            // local buffer
            byte[] key = a.flow();
            byte[] map = s.flow();
            byte[] val = v.flow();

            // local status
            int i = 1, m = 1;
            int x = 0, y = 0, z = 0;

            Radar:
            // decode kat stream
            while (true) {
                byte w;
                switch (w = t.read()) {
                    case 0x7B:
                    case 0x5B: {
                        if (m < m << i) {
                            Spider pipe = n.onOpen(
                                a.slip(x), s.slip(y, w)
                            );
                            if (pipe != null) {
                                n = pipe;
                                x = y = z = 0;
                                m = (i = w >> 5 ^ 3) | m << 1;
                            } else {
                                wipe(w, t);
                                if (t.also()) {
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
                        if (m == 0x1) {
                            if (z != 0) {
                                n.onEach(
                                    a.slip(x),
                                    s.slip(y),
                                    v.slip(z)
                                );
                            }
                            break Radar;
                        } else {
                            throw new IOException(
                                "Symbol: `" + w + "`, iv: "
                                    + Integer.toBinaryString(i)
                            );
                        }
                    }
                    case 0x40: {
                        if (i == 1 && x == 0) {
                            i = 0x21;
                            continue;
                        } else {
                            throw new IOException(
                                "Requires `@` can't be " +
                                    "repeated, alias is empty"
                            );
                        }
                    }
                    case 0x3D: {
                        if (i == 0x00 || i == 0x40 ||
                            i == 0x20 || i == 0x60) {
                            i = 0x01;
                            continue;
                        } else {
                            throw new IOException(
                                "Requires `=` can't be here"
                            );
                        }
                    }
                    case 0x3A: {
                        if (i == 0 && x != 0) {
                            i = 0x20;
                            continue;
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
                            case 0x40:
                            case 0x41:
                            case 0x60:
                            case 0x81: {
                                continue;
                            }
                            case 0x20: {
                                i = 0x60;
                                continue;
                            }
                            case 0x21: {
                                i = 0x01;
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
                    case 0x22: {
                        switch (i) {
                            case 0x00: {
                                if (x == 0) {
                                    i = 0x40;
                                    x = text(t, key);
                                } else {
                                    throw new IOException(
                                        "The Alias is not empty"
                                    );
                                }
                                continue;
                            }
                            case 0x01: {
                                if (z == 0) {
                                    n.onEach(
                                        a.slip(x),
                                        s.slip(y, w),
                                        v.slip(
                                            text(t, val)
                                        )
                                    );
                                    i = 0x81;
                                    x = y = 0;
                                } else {
                                    throw new IOException(
                                        "The Value is not empty"
                                    );
                                }
                                continue;
                            }
                        }
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
                        );
                    }
                    case 0x2C: {
                        if (i == 0x1) {
                            n.onEach(
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
                                "Symbol: `" + w + "`, iv: "
                                    + Integer.toBinaryString(i)
                            );
                        }
                        continue;
                    }
                    case 0x23: {
                        if (i < 2 && x == 0) {
                            while (true) {
                                switch (t.next()) {
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
                    case 0x5C: {
                        switch (w = t.next()) {
                            case 0x73: {
                                w = ' ';
                                break;
                            }
                            case 0x62: {
                                w = '\b';
                                break;
                            }
                            case 0x66: {
                                w = '\f';
                                break;
                            }
                            case 0x74: {
                                w = '\t';
                                break;
                            }
                            case 0x72: {
                                w = '\r';
                                break;
                            }
                            case 0x6E: {
                                w = '\n';
                                break;
                            }
                            case 0x75: {
                                switch (i) {
                                    case 0x00: {
                                        x = word(
                                            t, x, key
                                        );
                                        continue;
                                    }
                                    case 0x01: {
                                        z = word(
                                            t, z, val
                                        );
                                        continue;
                                    }
                                    case 0x20:
                                    case 0x21: {
                                        y = word(
                                            t, y, map
                                        );
                                        continue;
                                    }
                                }
                                throw new IOException(
                                    "Symbol: `" + w + "`, iv: "
                                        + Integer.toBinaryString(i)
                                );
                            }
                        }
                    }
                    case 0x21:
                    case 0x24:
                    case 0x25:
                    case 0x26:
                    case 0x27:
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
                                map[y++] = w;
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
                                n.onEach(
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
                                        switch (w = t.next()) {
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
                                            "The comma is missing"
                                        );
                                    }
                                }
                                break Radar;
                            }
                            else {
                                throw new IOException(
                                    "The parent spider is missing"
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
     * Clean up the currently useless entity
     *
     * @throws IOException If an I/O error occurs
     */
    protected void wipe(
        @NotNull byte w,
        @NotNull Flow t
    ) throws IOException {
        int m = w >> 5 ^ 3 | 2;
        stage:
        while (true) {
            switch (w = t.next()) {
                case 0x7B:
                case 0x5B: {
                    if ((m <<= 1) > 0) {
                        m |= w >> 5 ^ 3;
                        continue;
                    }
                    throw new IOException(
                        "Mask: " + Integer.toBinaryString(m)
                    );
                }
                case 0x23: {
                    while (true) {
                        switch (t.next()) {
                            case 0x0A:
                            case 0x0D:
                            case 0x23: {
                                continue stage;
                            }
                        }
                    }
                }
                case 0x22: {
                    while (true) {
                        switch (t.next()) {
                            case 0x5C: {
                                t.next();
                                continue;
                            }
                            case 0x22: {
                                continue stage;
                            }
                        }
                    }
                }
                case 0x5D:
                case 0x7D: {
                    if ((m & 1 | 2) != w >> 5) {
                        if ((m >>= 1) != 1) {
                            continue;
                        } else break stage;
                    }
                    throw new IOException(
                        "Mask: " + Integer.toBinaryString(m)
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
            new Alias(512),
            new Space(256),
            new Value(8192)
        );
    }
}
