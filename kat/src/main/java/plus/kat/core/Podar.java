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

import static plus.kat.Doc.*;
import static plus.kat.lang.Uniform.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public class Podar implements Solver {
    /**
     * snapshot
     */
    protected final Alias alias;
    protected final Space space;
    protected final Value value;

    /**
     * Constructs a podar with the specified alias, space and value
     *
     * @param alias the specified {@code alias} of podar
     * @param space the specified {@code space} of podar
     * @param value the specified {@code value} of podar
     */
    public Podar(
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
     * Reads xml flow
     *
     * <pre>{@code
     *  <!-- this is a test entity -->
     *  <User>
     *     <id>1</id>
     *     <name>kraity</name>
     *  </User>
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
            int x = 0, z = 0;

            // local buffer
            byte[] key = a.flow();
            byte[] val = v.flow();

            Podar:
            // decode xml flow
            while (true) {
                byte w = u.i < u.l ?
                    u.v[u.i++] : u.read();
                switch (w) {
                    default: {
                        switch (i) {
                            case 0x2: {
                                Pipe pipe = n.onOpen(
                                    a.slip(x), s.slip(0, LT)
                                );
                                if (pipe != null) {
                                    n = pipe;
                                    m = m << 1;
                                    x = z = i = 0;
                                } else {
                                    wipe(2, u);
                                    if (u.also()) {
                                        i = 0x1;
                                        x = z = 0;
                                    } else {
                                        throw new IOException(
                                            "No more data after clean"
                                        );
                                    }
                                    continue;
                                }
                            }
                            case 0x0: {
                                key[x++] = w;
                                continue;
                            }
                            case 0x1:
                            case 0x3: {
                                val[z++] = w;
                                continue;
                            }
                        }
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
                    case 0x3C: {
                        if (i == 0x3) {
                            i = 0x2;
                            continue;
                        } else if (i == 0x1) {
                            i = 0x0;
                            if (z > 0) {
                                n.onNext(
                                    a.slip(0),
                                    s.slip(0),
                                    v.slip(z)
                                );
                                x = z = 0;
                            }
                            continue;
                        }
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
                        );
                    }
                    case 0x3F: {
                        switch (i) {
                            case 0x1:
                            case 0x3: {
                                val[z++] = w;
                                continue;
                            }
                            case 0x0: {
                                if (m != 1) break;
                                while (true) {
                                    w = u.next();
                                    if (w == GT) {
                                        i = 0x1;
                                        continue Podar;
                                    }
                                }
                            }
                        }
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
                        );
                    }
                    case 0x21: {
                        switch (i) {
                            case 0x1:
                            case 0x3: {
                                val[z++] = w;
                                continue;
                            }
                            case 0x0:
                            case 0x2: {
                                switch (u.next() | u.next() << 8) {
                                    case 0x2D2D: {
                                        while (true) {
                                            w = u.next();
                                            if (w == GT) {
                                                i++;
                                                continue Podar;
                                            }
                                        }
                                    }
                                    case 0x435B: {
                                        if ((w = u.next()) != 'D' ||
                                            (w = u.next()) != 'A' ||
                                            (w = u.next()) != 'T' ||
                                            (w = u.next()) != 'A' ||
                                            (w = u.next()) != '[') {
                                            break;
                                        }

                                        byte j, k;
                                        while (true) {
                                            w = u.i < u.l ?
                                                u.v[u.i++] : u.next();
                                            if (w != ']') {
                                                val[z++] = w;
                                                continue;
                                            }

                                            if ((j = u.next()) != ']' &
                                                (k = u.next()) != GT) {
                                                val[z++] = w;
                                                val[z++] = j;
                                                val[z++] = k;
                                            } else {
                                                i++;
                                                continue Podar;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
                        );
                    }
                    case 0x26: {
                        switch (i) {
                            case 0x0: {
                                key[x++] = code(u);
                                continue;
                            }
                            case 0x1:
                            case 0x3: {
                                val[z++] = code(u);
                                continue;
                            }
                        }
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
                        );
                    }
                    case 0x22:
                    case 0x3D: {
                        switch (i) {
                            case 0x1:
                            case 0x3: {
                                val[z++] = w;
                                continue;
                            }
                        }
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
                        );
                    }
                    case 0x3E: {
                        switch (i) {
                            case 0x0: {
                                i = 0x3;
                                continue;
                            }
                            case 0x4: {
                                n.onNext(
                                    a.slip(x),
                                    s.slip(0),
                                    v.slip(z)
                                );
                                i = 0x1;
                                x = z = 0;
                                continue;
                            }
                            case 0x1:
                            case 0x3: {
                                val[z++] = w;
                                continue;
                            }
                        }
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
                        );
                    }
                    case 0x00: {
                        if (m == 1 && (x | z) == 0) {
                            break Podar;
                        } else {
                            throw new IOException(
                                "Missing part of data, iv: "
                                    + Integer.toBinaryString(i)
                            );
                        }
                    }
                    case 0x09:
                    case 0x0A:
                    case 0x0D:
                    case 0x20: {
                        switch (i) {
                            case 0x1: {
                                continue;
                            }
                            case 0x3: {
                                val[z++] = w;
                                continue;
                            }
                            case 0x0: {
                                Pipe pipe = null;
                                while (true) {
                                    w = u.next();
                                    if (w <= 0x20) {
                                        continue;
                                    }

                                    if (w == GT) {
                                        if (pipe != null) {
                                            i = 0x1;
                                            x = z = 0;
                                            continue Podar;
                                        }
                                        throw new IOException();
                                    }
                                    if (w == SOL) {
                                        if (pipe != null) {
                                            w = u.next();
                                            if (w == GT) {
                                                m = m >> (i = 1);
                                                n = n.onClose(true, true);
                                                continue Podar;
                                            }
                                        }
                                        throw new IOException();
                                    }

                                    if (pipe == null) {
                                        pipe = n.onOpen(
                                            a.slip(x), s.slip(0, LT)
                                        );
                                        if (pipe != null) {
                                            n = pipe;
                                            x = z = 0;
                                            m = m << 1;
                                        } else {
                                            wipe(1, u);
                                            if (u.also()) {
                                                i = 0x1;
                                                x = z = 0;
                                            } else {
                                                throw new IOException(
                                                    "No more data after clean"
                                                );
                                            }
                                            continue Podar;
                                        }
                                    }

                                    key[x++] = w;
                                    while (true) {
                                        w = u.next();
                                        if (w != '=') {
                                            key[x++] = w;
                                            continue;
                                        }

                                        w = u.next();
                                        if (w == QUOT) {
                                            while (true) {
                                                w = u.next();
                                                if (w == QUOT) {
                                                    break;
                                                } else {
                                                    val[z++] = w;
                                                }
                                            }
                                            n.onNext(
                                                a.slip(x),
                                                s.slip(0),
                                                v.slip(z)
                                            );
                                            x = z = 0;
                                            break;
                                        }
                                        throw new IOException();
                                    }
                                }
                            }
                        }
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
                        );
                    }
                    case 0x2F: {
                        switch (i) {
                            case 0x1:
                            case 0x3: {
                                val[z++] = w;
                                continue;
                            }
                            case 0x2: {
                                i = 0x4;
                                if (x > 0) {
                                    for (int j = 0; j < x; j++) {
                                        w = u.i < u.l ?
                                            u.v[u.i++] : u.next();
                                        if (w == AMP) {
                                            w = code(u);
                                        }
                                        if (key[j] != w) {
                                            throw new IOException();
                                        }
                                    }
                                    continue;
                                }
                                while (true) {
                                    w = u.next();
                                    if (w == GT) {
                                        n.onNext(
                                            a.slip(x),
                                            s.slip(0),
                                            v.slip(z)
                                        );
                                        z = 0;
                                        m = m >> (i = 1);
                                        n = n.onClose(true, true);
                                        continue Podar;
                                    }
                                }
                            }
                            case 0x0: {
                                if (x != 0) break;
                                while (true) {
                                    w = u.i < u.l ?
                                        u.v[u.i++] : u.next();
                                    if (w == GT) {
                                        m = m >> (i = 1);
                                        n = n.onClose(true, true);
                                        continue Podar;
                                    }
                                }
                            }
                        }
                        throw new IOException(
                            "Symbol: `" + w + "`, iv: "
                                + Integer.toBinaryString(i)
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
     * Returns the current escape content
     *
     * @throws IOException If an I/O error occurs
     */
    protected byte code(
        @NotNull Flow u
    ) throws IOException {
        byte w = u.next();
        switch (w) {
            case 'l': {
                if ((w = u.next()) != 't' ||
                    (w = u.next()) != ';') {
                    break;
                }
                return LT;
            }
            case 'g': {
                if ((w = u.next()) != 't' ||
                    (w = u.next()) != ';') {
                    break;
                }
                return GT;
            }
            case 's': {
                if ((w = u.next()) != 'o' ||
                    (w = u.next()) != 'l' ||
                    (w = u.next()) != ';') {
                    break;
                }
                return SOL;
            }
            case 'n': {
                if ((w = u.next()) != 'u' ||
                    (w = u.next()) != 'm' ||
                    (w = u.next()) != ';') {
                    break;
                }
                return NUM;
            }
            case 'q': {
                if ((w = u.next()) != 'u' ||
                    (w = u.next()) != 'o' ||
                    (w = u.next()) != 't' ||
                    (w = u.next()) != ';') {
                    break;
                }
                return QUOT;
            }
            case 'e': {
                if ((w = u.next()) != 'q' ||
                    (w = u.next()) != 'u' ||
                    (w = u.next()) != 'a' ||
                    (w = u.next()) != 'l' ||
                    (w = u.next()) != ';') {
                    break;
                }
                return EQUAL;
            }
            case 'a': {
                switch (u.next()) {
                    case 'm': {
                        if ((w = u.next()) != 'p' ||
                            (w = u.next()) != ';') {
                            break;
                        }
                        return AMP;
                    }
                    case 'p': {
                        if ((w = u.next()) != 'o' ||
                            (w = u.next()) != 's' ||
                            (w = u.next()) != ';') {
                            break;
                        }
                        return APOS;
                    }
                }
            }
        }

        throw new IOException(
            "Symbol: `" + w + "`, can't be here"
        );
    }

    /**
     * Filter the currently useless parts
     *
     * @throws IOException If an I/O error occurs
     */
    protected void wipe(
        @NotNull int i,
        @NotNull Flow u
    ) throws IOException {
        Scope:
        while (true) {
            byte w = u.next();
            switch (w) {
                case '/': {
                    w = u.next();
                    if (w == '>') {
                        if (i > 1) {
                            i--;
                            break Scope;
                        }
                        return;
                    }
                    throw new IOException();
                }
                case '>': {
                    break Scope;
                }
                case '"': {
                    while (true) {
                        w = u.next();
                        if (w == '"') {
                            continue Scope;
                        }
                    }
                }
            }
        }

        Scope:
        while (true) {
            byte w = u.next();
            if (w == '<') {
                switch (u.next()) {
                    default: {
                        while (true) {
                            w = u.next();
                            if (w != '>') {
                                continue;
                            }
                            i++;
                            continue Scope;
                        }
                    }
                    case '/': {
                        while (true) {
                            w = u.next();
                            if (w != '>') {
                                continue;
                            }
                            if ((--i) == 0) {
                                return;
                            } else {
                                continue Scope;
                            }
                        }
                    }
                    case '!': {
                        w = u.next();
                        switch (w) {
                            case '[': {
                                w = ']';
                            }
                            case '-': {
                                while (true) {
                                    if (u.next() != w) {
                                        continue;
                                    }
                                    if (u.next() != w) {
                                        continue;
                                    }
                                    if (u.next() == '>') {
                                        continue Scope;
                                    }
                                }
                            }
                        }
                        throw new IOException();
                    }
                }
            }
        }
    }

    /**
     * Clean up this {@link Podar}
     */
    @Override
    public void clear() {
        space.clear();
        alias.clear();
        value.clear();
    }

    /**
     * Returns an instance of {@link Podar}
     */
    @NotNull
    public static Podar apply() {
        return new Podar(
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
