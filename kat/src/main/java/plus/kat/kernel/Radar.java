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
package plus.kat.kernel;

import plus.kat.anno.NotNull;

import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.stream.*;

import static plus.kat.kernel.Radar.Event.*;
import static plus.kat.stream.Binary.hex;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Radar implements Solver {
    /**
     * chain stream
     */
    protected final Space space;
    protected final Alias alias;
    protected final Value value;

    enum Event {
        SPACE, ALIAS, VALUE
    }

    /**
     * default
     */
    public Radar() {
        space = Space.apply();
        alias = Alias.apply();
        value = Value.apply();
    }

    /**
     * @param b1 the specified {@link Bucket} of {@link Space}
     * @param b2 the specified {@link Bucket} of {@link Alias}
     * @param b3 the specified {@link Bucket} of {@link Value}
     */
    public Radar(
        @NotNull Bucket b1,
        @NotNull Bucket b2,
        @NotNull Bucket b3
    ) {
        space = new Space(b1);
        alias = new Alias(b2);
        value = new Value(b3);
    }

    /**
     * decode kat stream
     *
     * @param p specify the data transfer pipeline
     * @param r specify the source of decoded data
     * @throws IOCrash Unexpected errors by {@link Pipe} or {@link Reader}
     */
    @Override
    public void read(
        @NotNull Pipe p,
        @NotNull Reader r
    ) throws IOCrash {
        // event status
        Event event = SPACE;

        // local access
        Space s = space;
        Alias a = alias;
        Value v = value;

        Radar:
        // decode kat stream
        while (r.also()) switch (event) {
            case SPACE: {
                do {
                    byte b = r.read();
                    if (b <= 0x20) {
                        if (s.isEmpty())
                            switch (b) {
                                case 0x09:
                                case 0x0A:
                                case 0x0D:
                                case 0x20: {
                                    continue;
                                }
                            }
                        throw new UnexpectedCrash(
                            "Unexpectedly, byte '" + b + "' <= 32 in space"
                        );
                    }
                    switch (b) {
                        case '{': {
                            if (p.create(s, a)) {
                                s.clean();
                            } else {
                                s.clean();
                                dropdown(r);
                            }
                            continue;
                        }
                        case '(': {
                            event = VALUE;
                            continue Radar;
                        }
                        case ':': {
                            event = ALIAS;
                            continue Radar;
                        }
                        case '#': {
                            if (s.isEmpty()) {
                                explain(r);
                                continue;
                            }
                            throw new UnexpectedCrash(
                                "Unexpectedly, byte '" + b + "' in space."
                            );
                        }
                        case '}': {
                            if (s.isEmpty()) {
                                if (p.bundle()) {
                                    continue;
                                } else {
                                    break Radar;
                                }
                            }
                            throw new UnexpectedCrash(
                                "Unexpectedly, byte '" + b + "' in space."
                            );
                        }
                        case '^':
                        case ')': {
                            throw new UnexpectedCrash(
                                "Unexpectedly, byte '" + b + "' in space, it can't be here."
                            );
                        }
                        default: {
                            s.chain(b);
                        }
                    }
                } while (
                    r.also()
                );
                break Radar;
            }
            case ALIAS: {
                do {
                    byte b = r.read();
                    if (b <= 0x20) {
                        throw new UnexpectedCrash(
                            "Unexpectedly, byte '" + b + "' <= 32 in alias"
                        );
                    }
                    switch (b) {
                        case '{': {
                            event = SPACE;
                            if (p.create(s, a)) {
                                s.clean();
                                a.clean();
                            } else {
                                s.clean();
                                a.clean();
                                dropdown(r);
                            }
                            continue Radar;
                        }
                        case '(': {
                            event = VALUE;
                            continue Radar;
                        }
                        case '^': {
                            escape(a, r);
                            continue;
                        }
                        case '#':
                        case ':':
                        case ')':
                        case '}': {
                            throw new UnexpectedCrash(
                                "Unexpectedly, byte '" + b + "' in alias, it can't be here."
                            );
                        }
                        default: {
                            a.chain(b);
                        }
                    }
                } while (
                    r.also()
                );
                break Radar;
            }
            case VALUE: {
                do {
                    byte b = r.read();
                    switch (b) {
                        case '^': {
                            escape(v, r);
                            continue;
                        }
                        case ')': {
                            p.accept(
                                s, a, v
                            );
                            s.clean();
                            a.clean();
                            v.clean();
                            event = SPACE;
                            continue Radar;
                        }
                        case '(': {
                            throw new UnexpectedCrash(
                                "Unexpectedly, byte '" + b + "' in value, it can't be here."
                            );
                        }
                        default: {
                            v.chain(b);
                        }
                    }
                } while (
                    r.also()
                );
                break Radar;
            }
        }
    }

    /**
     * escape special byte
     */
    protected void escape(
        @NotNull Chain c,
        @NotNull Reader r
    ) throws IOCrash {
        if (r.also()) {
            byte b = r.read();
            switch (b) {
                case '^': {
                    c.chain(b);
                    return;
                }
                case 's': {
                    b = ' ';
                    break;
                }
                case 'r': {
                    b = '\r';
                    break;
                }
                case 'n': {
                    b = '\n';
                    break;
                }
                case 'u': {
                    uncork(c, r);
                    return;
                }
            }
            c.chain(b);
        }
    }

    /**
     * drops {@code #comment here#}
     */
    protected void explain(
        @NotNull Reader r
    ) throws IOCrash {
        while (r.also()) {
            switch (r.read()) {
                case '#':
                case '\r':
                case '\n': {
                    return;
                }
            }
        }
    }

    /**
     * drops {@code space:alias{...}}
     */
    protected void dropdown(
        @NotNull Reader r
    ) throws IOCrash {
        int i = 0;
        while (r.also()) {
            switch (r.read()) {
                case '{': {
                    i++;
                    continue;
                }
                case '}': {
                    if (i-- == 0) {
                        return;
                    } else {
                        continue;
                    }
                }
                case '#': {
                    explain(r);
                    continue;
                }
                case '^': {
                    if (r.also()) {
                        r.read();
                    }
                    continue;
                }
                case '(': {
                    Drop:
                    while (r.also()) {
                        switch (r.read()) {
                            case '(': {
                                throw new UnexpectedCrash(
                                    "Unexpectedly, byte '40', it can't be here."
                                );
                            }
                            case ')': {
                                break Drop;
                            }
                            case '^': {
                                if (r.also()) {
                                    r.read();
                                }
                            }
                        }
                    }
                    continue;
                }
                case ')': {
                    throw new UnexpectedCrash(
                        "Unexpectedly, byte '41', it can't be here."
                    );
                }
            }
        }
    }

    /**
     * escape unicode byte
     */
    static void uncork(
        @NotNull Chain c,
        @NotNull Reader r
    ) throws IOCrash {
        // hex number
        int c1 = hex(r.next());
        int c2 = hex(r.next());
        int c3 = hex(r.next());
        int c4 = hex(r.next());

        // U+0000 ~ U+0080 ~ U+07FF
        // 0xxxxxx & 110xxxxx 10xxxxxx
        if (c1 == 0x0) {
            // U+0000 ~ U+007F
            // 0xxxxxx
            if (c2 == 0x0 && c3 < 0x8) {
                // 0xxx xxxx
                c.chain((byte) (
                    c3 << 4 | c4
                ));
            }
            // U+0080 ~ U+07FF
            // 110xxxxx 10xxxxxx
            else {
                // 110xxx xx : 10xx xxxx
                c.chain((byte) (
                    (c2 << 2) | (c3 >> 2) | 0xC0
                ));
                c.chain((byte) (
                    ((c3 & 0x03) << 4) | c4 | 0x80
                ));
            }
        }

        // U+10000 ~ U+10FFFF
        // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
        // 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
        else if (c1 == 0xD && c2 > 0x7) {
            // escape
            r.next();

            // check mark
            if (r.next() != 'u') {
                c.chain(
                    (byte) '?'
                );
                return;
            }

            // hex number
            int d1 = hex(r.next());
            int d2 = hex(r.next());
            int d3 = hex(r.next());
            int d4 = hex(r.next());

            // check surrogate pair
            if (d1 != 0xD || d2 < 0xC) {
                c.chain(
                    (byte) '?'
                );
                return;
            }

            // 11110x xx : 10xxxx xx : 10xx xx xx : 10xx xxxx
            // 11110x xx : 10x100 00
            // 1101 10xx xxxx xxxx 1101 11xx xxxx xxxx
            c.chain((byte) (
                (c2 & 0x03) | 0xF0
            ));
            c.chain((byte) (
                ((c3 + 0x04) << 2) | (c4 >> 2) | 0x80
            ));
            c.chain((byte) (
                ((c4 & 0x03) << 4) | ((d2 & 0x03) << 2) | (d3 >> 2) | 0x80
            ));
            c.chain((byte) (
                ((d3 & 0x03) << 4) | d4 | 0x80
            ));
        }

        // U+0800 ~ U+FFFF
        // 1110xxxx 10xxxxxx 10xxxxxx
        else {
            // xxxx : 10xxxx xx : 10xx xxxx
            c.chain((byte) (
                c1 | 0xE0
            ));
            c.chain((byte) (
                (c2 << 2) | (c3 >> 2) | 0x80
            ));
            c.chain((byte) (
                ((c3 & 0x03) << 4) | c4 | 0x80
            ));
        }
    }

    /**
     * clear this {@link Radar}
     */
    @Override
    public void clear() {
        space.clean();
        alias.clean();
        value.clear();
    }

    /**
     * close this {@link Radar}
     */
    @Override
    public void close() {
        space.close();
        alias.close();
        value.close();
    }
}
