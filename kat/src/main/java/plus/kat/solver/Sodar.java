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
 * @since 0.0.5
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
        @NotNull Flow t,
        @NotNull Spider n
    ) throws IOException {
        try {
            // local access
            Alias a = alias;
            Space s = space;
            Value v = value;

            // local status
            int i = 1, m = 1;
            int x = 0, z = 0;

            // local buffer
            byte[] key = a.flow();
            byte[] val = v.flow();

            Sodar:
            // decode json stream
            while (true) {
                byte w;
                switch (w = t.read()) {
                    case 0x7B:
                    case 0x5B: {
                        if (m < m << i) {
                            Spider pipe = n.onOpen(
                                a.slip(x), s.slip(0, w)
                            );
                            if (pipe != null) {
                                n = pipe;
                                x = z = 0;
                                m = (i = w >> 5 ^ 3) | m << 1;
                            } else {
                                wipe(w, t);
                                if (t.also()) {
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
                                n.onEach(
                                    a.slip(x),
                                    s.slip(0),
                                    v.slip(z)
                                );
                            }
                            break Sodar;
                        } else {
                            throw new IOException(
                                "Symbol: `" + w + "`, iv: "
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
                                        s.slip(0, w),
                                        v.slip(
                                            text(t, val)
                                        )
                                    );
                                    x = 0;
                                    i = 0x81;
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
                                s.slip(0),
                                v.slip(z)
                            );
                            i = m & 1;
                            x = z = 0;
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
                                n.onEach(
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
            new Alias(512),
            new Space(32),
            new Value(8192)
        );
    }
}
