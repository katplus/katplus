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

import java.io.IOException;

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
     * @param b1 the bucket of {@code space}
     * @param b2 the bucket of {@code alias}
     * @param b3 the bucket of {@code value}
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
     * Reads kat stream
     *
     * <pre>{@code
     *  # this is a test entity
     *  User{
     *     l:uid(1)
     *     s:name(kraity)
     *     s:role(developer)
     *
     *     # status
     *     b:blocked(0)
     *
     *     # extra data
     *     M:resource{
     *         I:age(6)
     *         D:devote(1024)
     *     }
     *  }
     * }</pre>
     *
     * @param p specify the data transfer pipeline
     * @param r specify the source of decoded data
     * @throws IOException Unexpected errors by {@link Pipe} or {@link Reader}
     */
    @Override
    public void read(
        @NotNull Pipe p,
        @NotNull Reader r
    ) throws IOException {
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
                while (true) {
                    byte b = r.next();
                    if (b <= 0x20) {
                        if (s.isEmpty())
                            switch (b) {
                                case 0x09:
                                case 0x0A:
                                case 0x0D:
                                case 0x20: {
                                    if (r.also()) {
                                        continue;
                                    } else break Radar;
                                }
                            }
                        throw new UnexpectedCrash(
                            "Unexpectedly, byte '" + b + "' <= 32 in space"
                        );
                    }
                    switch (b) {
                        case '{': {
                            if (p.attach(s, a)) {
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
                                if (!p.detach()) {
                                    break Radar;
                                } else {
                                    continue Radar;
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
                }
            }
            case ALIAS: {
                while (true) {
                    byte b = r.next();
                    if (b <= 0x20) {
                        throw new UnexpectedCrash(
                            "Unexpectedly, byte '" + b + "' <= 32 in alias"
                        );
                    }
                    switch (b) {
                        case '{': {
                            event = SPACE;
                            if (p.attach(s, a)) {
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
                }
            }
            case VALUE: {
                while (true) {
                    byte b = r.next();
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
                }
            }
        }
    }

    /**
     * Escape special character
     *
     * <pre>{@code
     *   '^^' -> '^'
     *   '^s' -> ' '
     *   '^r' -> '\r'
     *   '^n' -> '\n'
     *   '^u' -> unicode
     * }</pre>
     *
     * @throws IOException Unexpected errors by {@link Reader}
     */
    protected void escape(
        @NotNull Chain c,
        @NotNull Reader r
    ) throws IOException {
        byte b = r.next();
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

    /**
     * Filter comments
     *
     * <pre>{@code
     *   #comment here#
     * }</pre>
     *
     * @throws IOException Unexpected errors by {@link Reader}
     */
    protected void explain(
        @NotNull Reader r
    ) throws IOException {
        while (true) {
            switch (r.next()) {
                case '#':
                case '\r':
                case '\n': {
                    return;
                }
            }
        }
    }

    /**
     * Filter out the useless
     *
     * <pre>{@code
     *   space:alias{...}
     * }</pre>
     *
     * @throws IOException Unexpected errors by {@link Reader}
     */
    protected void dropdown(
        @NotNull Reader r
    ) throws IOException {
        int i = 0;
        while (true) {
            switch (r.next()) {
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
                    r.next();
                    continue;
                }
                case '(': {
                    Drop:
                    while (true) {
                        switch (r.next()) {
                            case '^': {
                                r.next();
                                continue;
                            }
                            case ')': {
                                break Drop;
                            }
                            case '(': {
                                throw new UnexpectedCrash(
                                    "Unexpectedly, byte '40', it can't be here."
                                );
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
     * Escape unicode character
     *
     * @throws IOException Unexpected errors by {@link Reader}
     */
    static void uncork(
        @NotNull Chain c,
        @NotNull Reader r
    ) throws IOException {
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
