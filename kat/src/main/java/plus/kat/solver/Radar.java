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
import plus.kat.anno.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.stream.*;

import java.io.IOException;

import static plus.kat.stream.Binary.digit;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Radar implements Solver {
    /**
     * snapshot
     */
    protected final Space space;
    protected final Alias alias;
    protected final Value value;

    /**
     * Constructs a radar with the specified buckets
     *
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
     * Constructs a radar with the specified space, alias and value
     *
     * @param space the specified {@code space} of solver
     * @param alias the specified {@code alias} of solver
     * @param value the specified {@code value} of solver
     */
    public Radar(
        @NotNull Space space,
        @NotNull Alias alias,
        @NotNull Value value
    ) {
        if (space != null &&
            alias != null &&
            value != null) {
            this.space = space;
            this.alias = alias;
            this.value = value;
        } else {
            throw new NullPointerException(
                "Received: (" + space + ", "
                    + alias + ", " + value + ")"
            );
        }
    }

    /**
     * Returns the algo of solver
     */
    @Override
    public Algo algo() {
        return Algo.KAT;
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
     *     # extras
     *     M:resource{
     *         I:age(6)
     *         D:devote(1024)
     *     }
     *  }
     * }</pre>
     *
     * @param r the specified source to be parsed
     * @param e the specified data transfer pipeline
     * @throws FlowCrash   If an I/O error occurs by paper
     * @throws SolverCrash If an I/O error occurs by solver
     */
    @Override
    public void read(
        @NotNull Paper r,
        @NotNull Pipage e
    ) throws IOException {
        try {
            // local access
            Chain c = space;
            Space s = space;
            Alias a = alias;
            Value v = value;

            Radar:
            // decode kat stream
            while (true) {
                // table switch
                byte b = r.next();
                switch (b) {
                    case 0x7B: {
                        Pipage pipe = e.onOpen(s, a);
                        if (pipe != null) {
                            e = pipe;
                            s.reset();
                            a.reset();
                        } else {
                            washing(r);
                            if (r.also()) {
                                s.reset();
                                a.reset();
                            } else {
                                throw new SolverCrash(
                                    "No more data after" +
                                        " flushing a useless entity"
                                );
                            }
                        }
                        c = s;
                        continue;
                    }
                    case 0x3A: {
                        if (c == s) {
                            c = a;
                            continue;
                        } else {
                            throw new SolverCrash(
                                a.getClass() + " is not empty <"
                                    + a + "> and `:` can't be repeated"
                            );
                        }
                    }
                    case 0x7D: {
                        if (c == s && s.isEmpty()) {
                            e = e.onClose(true, true);
                            if (e != null) {
                                if (r.also()) {
                                    continue;
                                } else break Radar;
                            } else {
                                throw new SolverCrash(
                                    "The parent pipage is missing"
                                );
                            }
                        }
                        throw new SolverCrash(
                            "`" + b + "` can't be in " + c.getClass()
                        );
                    }
                    case 0x21:
                    case 0x22:
                    case 0x24:
                    case 0x25:
                    case 0x26:
                    case 0x27:
                    case 0x2A:
                    case 0x2B:
                    case 0x2C:
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
                    case 0x3D:
                    case 0x3E:
                    case 0x3F:
                    case 0x40:
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
                    case 0x5B:
                    case 0x5C:
                    case 0x5D:
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
                        c.join(b);
                        continue;
                    }
                    case 0x00:
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
                        throw new SolverCrash(
                            "Control character `"
                                + b + "` can't be here"
                        );
                    }
                    case 0x5E: {
                        escape(c, r);
                        continue;
                    }
                    case 0x29: {
                        throw new SolverCrash(
                            "Close parentheses can't be here"
                        );
                    }
                    case 0x09:
                    case 0x0A:
                    case 0x0D:
                    case 0x20: {
                        if (c.isEmpty()) {
                            if (r.also()) {
                                continue;
                            } else break Radar;
                        }
                        throw new SolverCrash(
                            c.getClass() + " is not empty <"
                                + c + "> and whitespace can't be here"
                        );
                    }
                    case 0x28: {
                        while (true) {
                            switch (b = r.next()) {
                                case 0x5E: {
                                    escape(v, r);
                                    continue;
                                }
                                case 0x29: {
                                    e.onEmit(
                                        s, a, v
                                    );
                                    s.reset();
                                    a.reset();
                                    v.reset();
                                    if (r.also()) {
                                        c = s;
                                        continue Radar;
                                    } else break Radar;
                                }
                                default: {
                                    v.join(b);
                                }
                            }
                        }
                    }
                    case 0x23: {
                        if (c == s && s.isEmpty()) {
                            while (true) {
                                switch (r.next()) {
                                    case 0x0A:
                                    case 0x0D:
                                    case 0x23: {
                                        continue Radar;
                                    }
                                }
                            }
                        }
                        throw new SolverCrash(
                            c.getClass() + " is not empty <"
                                + c + "> and comment block can't be here"
                        );
                    }
                }
            }
        } finally {
            while (e != null) {
                e = e.onClose(
                    false, false
                );
            }
        }
    }

    /**
     * Escapes the special character
     *
     * <pre>{@code
     *   '^^' -> '^'
     *   '^s' -> ' '
     *   '^r' -> '\r'
     *   '^n' -> '\n'
     *   '^u' -> unicode
     * }</pre>
     *
     * @throws IOException If an I/O error occurs
     */
    protected void escape(
        @NotNull Chain c,
        @NotNull Paper r
    ) throws IOException {
        byte b = r.next();
        switch (b) {
            case '^': {
                break;
            }
            case 's': {
                b = ' ';
                break;
            }
            case 't': {
                b = '\t';
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
                rotate(
                    c, r
                );
                return;
            }
        }
        c.join(b);
    }

    /**
     * Escapes the unicode character
     *
     * <pre>{@code
     *   '^u0020' -> ' '
     *   '^u0040' -> '@'
     * }</pre>
     *
     * @throws IOException If an I/O error occurs
     */
    static void rotate(
        @NotNull Chain c,
        @NotNull Paper r
    ) throws IOException {
        // hex number
        int c1 = digit(r.next());
        int c2 = digit(r.next());
        int c3 = digit(r.next());
        int c4 = digit(r.next());

        // U+0000 ~ U+0080 ~ U+07FF
        // 0xxxxxx & 110xxxxx 10xxxxxx
        if (c1 == 0x0) {
            // U+0000 ~ U+007F
            // 0xxxxxx
            if (c2 == 0x0 && c3 < 0x8) {
                // 0xxx xxxx
                c.join((byte) (
                    c3 << 4 | c4
                ));
            }
            // U+0080 ~ U+07FF
            // 110xxxxx 10xxxxxx
            else {
                // 110xxx xx : 10xx xxxx
                c.join((byte) (
                    c2 << 2 | c3 >> 2 | 0xC0
                ));
                c.join((byte) (
                    (c3 & 0x03) << 4 | c4 | 0x80
                ));
            }
        }

        // U+10000 ~ U+10FFFF
        // 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
        else if (c1 == 0xD && c2 > 0x7) {
            if (0xC <= c2) {
                throw new SolverCrash(
                    "Illegal agent pair: " + c1
                        + ',' + c2 + ',' + c3 + ',' + c4
                );
            }

            // check esc
            byte m = r.next();
            if (m != '^' &&
                m != '\\') {
                throw new SolverCrash(
                    "Illegal esc char: " + m
                );
            }

            // check mark
            byte n = r.next();
            if (n != 'u') {
                throw new SolverCrash(
                    "Illegal esc mark: " + n
                );
            }

            // hex number
            int d1 = digit(r.next());
            int d2 = digit(r.next());
            int d3 = digit(r.next());
            int d4 = digit(r.next());

            // check surrogate pair
            if (d1 != 0xD || d2 < 0xC) {
                throw new SolverCrash(
                    "Not another agent pair: " + d1
                        + ',' + d2 + ',' + d3 + ',' + d4
                );
            }

            // 11110x xx : 10xxxx xx : 10xx xx xx : 10xx xxxx
            // 11110x xx : 10x100 00
            // 1101 10xx xxxx xxxx 1101 11xx xxxx xxxx
            c.join((byte) (
                (c2 & 0x03) | 0xF0
            ));
            c.join((byte) (
                (c3 + 0x04) << 2 | c4 >> 2 | 0x80
            ));
            c.join((byte) (
                (c4 & 0x03) << 4 | (d2 & 0x03) << 2 | d3 >> 2 | 0x80
            ));
            c.join((byte) (
                (d3 & 0x03) << 4 | d4 | 0x80
            ));
        }

        // U+0800 ~ U+FFFF
        // 1110xxxx 10xxxxxx 10xxxxxx
        else {
            // xxxx : 10xxxx xx : 10xx xxxx
            c.join((byte) (
                c1 | 0xE0
            ));
            c.join((byte) (
                c2 << 2 | c3 >> 2 | 0x80
            ));
            c.join((byte) (
                (c3 & 0x03) << 4 | c4 | 0x80
            ));
        }
    }

    /**
     * Filter out the useless body
     *
     * <pre>{@code
     *   space:alias{...}
     * }</pre>
     *
     * @throws IOException If an I/O error occurs
     */
    protected void washing(
        @NotNull Paper r
    ) throws IOException {
        Stream:
        for (int i = 0; ; ) {
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
                case '^': {
                    r.next();
                    continue;
                }
                case '#': {
                    while (true) {
                        switch (r.next()) {
                            case '#':
                            case '\r':
                            case '\n': {
                                continue Stream;
                            }
                        }
                    }
                }
                case '(': {
                    while (true) {
                        switch (r.next()) {
                            case '^': {
                                r.next();
                                continue;
                            }
                            case ')': {
                                continue Stream;
                            }
                        }
                    }
                }
                case ')': {
                    throw new SolverCrash(
                        "Close parentheses can't be here"
                    );
                }
            }
        }
    }

    /**
     * Clears this {@link Radar}
     */
    @Override
    public void clear() {
        space.reset();
        alias.reset();
        value.clear();
    }

    /**
     * Closes this {@link Radar}
     */
    @Override
    public void close() {
        space.close();
        alias.close();
        value.close();
    }

    /**
     * Returns an instance of {@link Radar}
     */
    @NotNull
    public static Radar apply() {
        return new Radar(
            Space.Buffer.INS,
            Alias.Buffer.INS,
            Value.Buffer.INS
        );
    }
}
