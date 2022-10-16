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

import plus.kat.*;
import plus.kat.anno.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.stream.*;

import java.io.IOException;

import plus.kat.stream.Reader;

import static plus.kat.chain.Space.*;
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
     * Constructs a default radar
     */
    public Radar() {
        space = Space.apply();
        alias = Alias.apply();
        value = Value.apply();
    }

    /**
     * Constructs a radar with the specified bucket
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
     *     # extra data
     *     M:resource{
     *         I:age(6)
     *         D:devote(1024)
     *     }
     *  }
     * }</pre>
     *
     * @param p the specified data transfer pipeline
     * @param r the specified data source to be parsed
     * @throws IOException Unexpected errors by {@link Proxy} or {@link Reader}
     */
    @Override
    public void read(
        @NotNull Proxy p,
        @NotNull Reader r
    ) throws IOException {
        // local access
        Chain c = space;
        Space s = space;
        Alias a = alias;
        Value v = value;

        Radar:
        // decode kat stream
        while (true) {
            byte b = r.next();
            switch (b) {
                case 0x09:
                case 0x0A:
                case 0x0D:
                case 0x20: {
                    if (c.isEmpty()) {
                        if (r.also()) {
                            continue;
                        } else return;
                    }
                    throw new UnexpectedCrash(
                        "Parse error, `" + b + "` can't be in " + c.getClass()
                    );
                }
                case '{': {
                    if (p.attach(s, a)) {
                        s.clean();
                        a.clean();
                    } else {
                        s.clean();
                        a.clean();
                        washing(r);
                    }
                    c = s;
                    continue;
                }
                case '^': {
                    escape(c, r);
                    continue;
                }
                case '#': {
                    if (c.isSpace() &&
                        s.isEmpty()) {
                        while (true) {
                            switch (r.next()) {
                                case '#':
                                case '\r':
                                case '\n': {
                                    continue Radar;
                                }
                            }
                        }
                    }
                    throw new UnexpectedCrash(
                        "Parse error, `" + b + "` can't be in " + c.getClass()
                    );
                }
                case ')': {
                    throw new UnexpectedCrash(
                        "Parse error, `" + b + "` can't be in " + c.getClass()
                    );
                }
                case ':': {
                    if (c.isSpace()) {
                        c = a;
                        continue;
                    } else {
                        throw new UnexpectedCrash(
                            "Parse error, `" + b + "` can't be in " + c.getClass()
                        );
                    }
                }
                case '}': {
                    if (c.isSpace() &&
                        s.isEmpty()) {
                        if (p.detach()) {
                            c = s;
                            continue;
                        } else break Radar;
                    }
                    throw new UnexpectedCrash(
                        "Parse error, `" + b + "` can't be in " + c.getClass()
                    );
                }
                case '(': {
                    while (true) {
                        switch (b = r.next()) {
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
                                if (r.also()) {
                                    c = s;
                                    continue Radar;
                                } else return;
                            }
                            case '(': {
                                throw new UnexpectedCrash(
                                    "Parse error, `" + b + "` can't be in " + v.getClass()
                                );
                            }
                            default: {
                                v.concat(b);
                            }
                        }
                    }
                }
                default: {
                    c.concat(b);
                }
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
        @NotNull Reader r
    ) throws IOException {
        byte b = r.next();
        switch (b) {
            case '^': {
                c.concat(b);
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
                rotate(c, r);
                return;
            }
        }
        c.concat(b);
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
        @NotNull Reader r
    ) throws IOException {
        Filter:
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
                case ')': {
                    throw new UnexpectedCrash(
                        "Parse error, `41` can't be here"
                    );
                }
                case '#': {
                    while (true) {
                        switch (r.next()) {
                            case '#':
                            case '\r':
                            case '\n': {
                                continue Filter;
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
                                continue Filter;
                            }
                            case '(': {
                                throw new UnexpectedCrash(
                                    "Parse error, `40` can't be here"
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Clear this {@link Radar}
     */
    @Override
    public void clear() {
        space.clean();
        alias.clean();
        value.clear();
    }

    /**
     * Close this {@link Radar}
     */
    @Override
    public void close() {
        space.close();
        alias.close();
        value.close();
    }

    /**
     * Rotates with the unicode character
     *
     * @throws IOException If an I/O error occurs
     */
    static void rotate(
        @NotNull Chain c,
        @NotNull Reader r
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
                c.concat((byte) (
                    c3 << 4 | c4
                ));
            }
            // U+0080 ~ U+07FF
            // 110xxxxx 10xxxxxx
            else {
                // 110xxx xx : 10xx xxxx
                c.concat((byte) (
                    (c2 << 2) | (c3 >> 2) | 0xC0
                ));
                c.concat((byte) (
                    ((c3 & 0x03) << 4) | c4 | 0x80
                ));
            }
        }

        // U+10000 ~ U+10FFFF
        // U+D800 ~ U+DBFF & U+DC00 ~ U+DFFF
        // 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
        else if (c1 == 0xD && c2 > 0x7) {
            // escape
            byte b = r.next();
            if (b != '^' &&
                b != '\\') {
                throw new IOException(
                    "Illegal esc char: " + b
                );
            }

            // check mark
            if (r.next() != 'u') {
                throw new IOException(
                    "Illegal esc mark: " + b
                );
            }

            // hex number
            int d1 = digit(r.next());
            int d2 = digit(r.next());
            int d3 = digit(r.next());
            int d4 = digit(r.next());

            // check surrogate pair
            if (d1 != 0xD || d2 < 0xC) {
                throw new IOException(
                    "Not another agent pair: "
                        + d1 + ',' + d2 + ',' + d3 + ',' + d4
                );
            }

            // 11110x xx : 10xxxx xx : 10xx xx xx : 10xx xxxx
            // 11110x xx : 10x100 00
            // 1101 10xx xxxx xxxx 1101 11xx xxxx xxxx
            c.concat((byte) (
                (c2 & 0x03) | 0xF0
            ));
            c.concat((byte) (
                ((c3 + 0x04) << 2) | (c4 >> 2) | 0x80
            ));
            c.concat((byte) (
                ((c4 & 0x03) << 4) | ((d2 & 0x03) << 2) | (d3 >> 2) | 0x80
            ));
            c.concat((byte) (
                ((d3 & 0x03) << 4) | d4 | 0x80
            ));
        }

        // U+0800 ~ U+FFFF
        // 1110xxxx 10xxxxxx 10xxxxxx
        else {
            // xxxx : 10xxxx xx : 10xx xxxx
            c.concat((byte) (
                c1 | 0xE0
            ));
            c.concat((byte) (
                (c2 << 2) | (c3 >> 2) | 0x80
            ));
            c.concat((byte) (
                ((c3 & 0x03) << 4) | c4 | 0x80
            ));
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public class Lidar implements Solver {
        /**
         * snapshot
         */
        private long data = 0L;
        private long mask = 1L;
        private boolean mutable;

        /**
         * Returns the algo of solver
         */
        @Override
        public Algo algo() {
            return Algo.JSON;
        }

        /**
         * Reads json stream
         *
         * <pre>{@code
         *  {
         *     "uid": 1,
         *     "name": "kraity",
         *     "role": "developer",
         *     "blocked": 0,
         *     "resource": {
         *         "age": 6,
         *         "devote": 1024
         *     }
         *  }
         * }</pre>
         *
         * @param p the specified data transfer pipeline
         * @param r the specified data source to be parsed
         * @throws IOException Unexpected errors by {@link Proxy} or {@link Reader}
         */
        @Override
        public void read(
            @NotNull Proxy p,
            @NotNull Reader r
        ) throws IOException {
            // local access
            Alias a = alias;
            Value v = value;

            Boot:
            // decode json stream
            while (true) {
                byte b = r.next();
                if (b <= 0x20) {
                    switch (b) {
                        case 0x09:
                        case 0x0A:
                        case 0x0D:
                        case 0x20: {
                            continue;
                        }
                    }
                    throw new UnexpectedCrash(
                        "Parse error, `" + b + "`  can't be here"
                    );
                }

                switch (b) {
                    case '{': {
                        attach(
                            a, p, r, true
                        );
                        break Boot;
                    }
                    case '[': {
                        attach(
                            a, p, r, false
                        );
                        break Boot;
                    }
                    default: {
                        throw new UnexpectedCrash(
                            "Parse error, `" + b + "`  can't be here"
                        );
                    }
                }
            }

            Boot:
            // decode json stream
            while (r.also()) {
                if (mutable) Alias:
                    while (true) {
                        byte b = r.next();
                        if (b <= 0x20) {
                            switch (b) {
                                case 0x09:
                                case 0x0A:
                                case 0x0D:
                                case 0x20: {
                                    continue;
                                }
                            }
                            throw new UnexpectedCrash(
                                "Parse error, `" + b + "`  can't be here"
                            );
                        }

                        switch (b) {
                            case ':': {
                                break Alias;
                            }
                            case '"':
                            case '\'': {
                                escape(a, b, r);
                                continue;
                            }
                            case ',': {
                                if (a.isEmpty()) {
                                    continue;
                                } else {
                                    throw new UnexpectedCrash(
                                        "Parse error, ',' is not ':'"
                                    );
                                }
                            }
                            case '}': {
                                if (a.isEmpty()) {
                                    detach(p, true);
                                    continue Boot;
                                } else {
                                    throw new UnexpectedCrash(
                                        "Parse error, '}' is not ':'"
                                    );
                                }
                            }
                            default: {
                                throw new UnexpectedCrash(
                                    "Parse error, `" + b + "`  can't be here"
                                );
                            }
                        }
                    }

                while (true) {
                    byte b = r.next();
                    if (b <= 0x20) {
                        switch (b) {
                            case 0x09:
                            case 0x0A:
                            case 0x0D:
                            case 0x20: {
                                if (mask != 1) {
                                    continue;
                                } else break Boot;
                            }
                        }
                        throw new UnexpectedCrash(
                            "Parse error, `" + b + "`  can't be here"
                        );
                    }

                    switch (b) {
                        case '{': {
                            attach(
                                a, p, r, true
                            );
                            continue Boot;
                        }
                        case '[': {
                            attach(
                                a, p, r, false
                            );
                            continue Boot;
                        }
                        case 'n':
                        case 'N': {
                            escape(r);
                            p.accept(
                                $, a, v
                            );
                            a.clean();
                            v.clean();
                            continue;
                        }
                        case '"':
                        case '\'': {
                            escape(v, b, r);
                            p.accept(
                                $s, a, v
                            );
                            a.clean();
                            v.clean();
                            continue Boot;
                        }
                        case ',': {
                            if (a.isEmpty()) {
                                continue;
                            } else {
                                throw new UnexpectedCrash(
                                    "Parse error, ',' is not a value"
                                );
                            }
                        }
                        case '}': {
                            if (a.isEmpty()) {
                                detach(p, true);
                                continue Boot;
                            } else {
                                throw new UnexpectedCrash(
                                    "Parse error, '}' is not a value"
                                );
                            }
                        }
                        case ']': {
                            if (a.isEmpty()) {
                                detach(p, false);
                                continue Boot;
                            } else {
                                throw new UnexpectedCrash(
                                    "Parse error, ']' is not a value"
                                );
                            }
                        }
                        default: {
                            v.concat(b);
                        }
                    }

                    while (true) {
                        byte c = r.next();
                        if (c <= 0x20) {
                            switch (c) {
                                case 0x09:
                                case 0x0A:
                                case 0x0D:
                                case 0x20: {
                                    continue;
                                }
                            }
                            throw new UnexpectedCrash(
                                "Parse error, `" + b + "`  can't be here"
                            );
                        }

                        switch (c) {
                            case ',': {
                                p.accept(
                                    $, a, v
                                );
                                a.clean();
                                v.clean();
                                continue Boot;
                            }
                            case '}': {
                                p.accept(
                                    $, a, v
                                );
                                a.clean();
                                v.clean();
                                detach(p, true);
                                continue Boot;
                            }
                            case ']': {
                                p.accept(
                                    $, a, v
                                );
                                a.clean();
                                v.clean();
                                detach(p, false);
                                continue Boot;
                            }
                            default: {
                                v.concat(c);
                            }
                        }
                    }
                }
            }
        }

        /**
         * @param b is it a map?
         * @throws IOException If an I/O error occurs
         */
        protected void attach(
            Alias a,
            Proxy p,
            Reader r,
            boolean b
        ) throws IOException {
            if (mask == Long.MIN_VALUE) {
                throw new IOException(
                    "Parse error, out of range"
                );
            }

            if (b) {
                if (p.attach($M, a)) {
                    mask <<= 1;
                    data |= mask;
                    mutable = true;
                } else {
                    washing(
                        (byte) '}', r
                    );
                }
            } else {
                if (p.attach($L, a)) {
                    mask <<= 1;
                    mutable = false;
                } else {
                    washing(
                        (byte) ']', r
                    );
                }
            }
            a.clean();
        }

        /**
         * @throws IOException If an I/O error occurs
         */
        protected void detach(
            Proxy p,
            boolean b
        ) throws IOException {
            if (mutable == b) {
                p.detach();
                mask >>>= 1;
                mutable = (data & mask) != 0L;
            } else {
                throw new IOException(
                    "Parse error, mismatched terminator"
                );
            }
        }

        /**
         * Escapes the special character
         *
         * @throws IOException If an I/O error occurs
         */
        protected void escape(
            Reader r
        ) throws IOException {
            byte b2 = r.next();
            byte b3 = r.next();
            byte b4 = r.next();

            if ((b2 != 'u' && b2 != 'U') ||
                (b3 != 'l' && b3 != 'L') ||
                (b4 != 'l' && b4 != 'L')) {
                throw new UnexpectedCrash(
                    "Parse error, N" +
                        (char) (b2 & 0xFF) +
                        (char) (b3 & 0xFF) +
                        (char) (b3 & 0xFF) + " is not null"
                );
            }
        }

        /**
         * Escapes the special character
         *
         * @throws IOException If an I/O error occurs
         */
        protected void escape(
            Chain c,
            byte e,
            Reader r
        ) throws IOException {
            while (true) {
                byte b = r.next();
                if (b == e) {
                    break;
                }

                if (b != '\\') {
                    c.concat(b);
                    continue;
                }

                b = r.next();
                switch (b) {
                    case 'r': {
                        b = '\r';
                        break;
                    }
                    case 'n': {
                        b = '\n';
                        break;
                    }
                    case 't': {
                        b = '\t';
                        break;
                    }
                    case 'u': {
                        rotate(c, r);
                        continue;
                    }
                }
                c.concat(b);
            }
        }

        /**
         * Filter out the useless body
         *
         * @throws IOException If an I/O error occurs
         */
        protected void washing(
            byte a,
            Reader r
        ) throws IOException {
            while (true) {
                byte b = r.next();
                if (a == b) {
                    break;
                }
                switch (b) {
                    case '{': {
                        washing(
                            (byte) '}', r
                        );
                        continue;
                    }
                    case '[': {
                        washing(
                            (byte) ']', r
                        );
                        continue;
                    }
                    case '"': {
                        Drop:
                        while (true) {
                            switch (r.next()) {
                                case '"': {
                                    break Drop;
                                }
                                case '\\': {
                                    r.next();
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Clear this {@link Lidar}
         */
        @Override
        public void clear() {
            data = 0L;
            mask = 1L;
            alias.clean();
            value.clear();
        }

        /**
         * Close this {@link Lidar}
         */
        @Override
        public void close() {
            data = 0L;
            mask = 1L;
            alias.close();
            value.close();
        }
    }

    /**
     * @author kraity
     * @since 0.0.4
     */
    public class Motor implements Solver {

        public static final byte
            LT = '<', GT = '>', AMP = '&',
            APOS = '\'', QUOT = '"', SLASH = '/';

        /**
         * Returns the algo of solver
         */
        @Override
        public Algo algo() {
            return Algo.DOC;
        }

        /**
         * Reads xml stream
         *
         * <pre>{@code
         *  <User>
         *     <uid>1</uid>
         *     <name>kraity</name>
         *     <role>developer</role>
         *
         *     <!-- status -->
         *     <blocked>0</blocked>
         *
         *     <!-- extra data -->
         *     <resource>
         *         <age>6</age>
         *         <devote>1024</devote>
         *     </resource>
         *  </User>
         * }</pre>
         *
         * @param p the specified data transfer pipeline
         * @param r the specified data source to be parsed
         * @throws IOException Unexpected errors by {@link Proxy} or {@link Reader}
         */
        @Override
        public void read(
            @NotNull Proxy p,
            @NotNull Reader r
        ) throws IOException {
            // local access
            Alias a = alias;
            Value v = value;

            Boot:
            // decode xml stream
            while (r.also()) {
                byte b = r.read();
                if (b != LT) {
                    if (b != AMP) {
                        v.concat(b);
                    } else {
                        escape(v, r);
                    }
                    continue;
                }

                byte c = r.next();
                switch (c) {
                    case '?': {
                        decide(r);
                        continue;
                    }
                    case '!': {
                        explain(v, r);
                        continue;
                    }
                    case SLASH: {
                        if (a.isEmpty()) {
                            while (true) {
                                byte d = r.next();
                                if (d != GT) {
                                    continue;
                                }
                                p.detach();
                                v.clean();
                                continue Boot;
                            }
                        } else {
                            int i = 0;
                            while (true) {
                                byte d = r.next();
                                if (d == GT) {
                                    p.accept(
                                        $s, a, v
                                    );

                                    a.clean();
                                    v.clean();
                                    continue Boot;
                                }

                                if (a.is(i++, d)) {
                                    continue;
                                }

                                throw new UnexpectedCrash(
                                    "Parse error, `" + b + "`  can't be in namespace"
                                );
                            }
                        }
                    }
                    default: {
                        if (a.isNotEmpty()) {
                            if (p.attach($M, a)) {
                                a.clean();
                            } else {
                                a.clean();
                                v.clean();
                                washing(2, r);
                                continue;
                            }
                        }

                        v.clean();
                        a.concat(c);

                        while (true) {
                            b = r.next();
                            if (b == AMP) {
                                escape(a, r);
                                continue;
                            }

                            if (b == GT) {
                                if (a.get(-1) == SLASH) {
                                    a.clean();
                                }
                                continue Boot;
                            }

                            if (b != ' ') {
                                a.concat(b);
                                continue;
                            }

                            if (p.attach($M, a)) {
                                a.clean();
                                collate(
                                    a, v, p, r
                                );
                                a.clean();
                                v.clean();
                                continue Boot;
                            } else {
                                a.clean();
                                v.clean();
                                washing(1, r);
                                continue Boot;
                            }
                        }
                    }
                }
            }
        }

        /**
         * Collects the attribute values
         *
         * @throws IOException If an I/O error occurs
         */
        protected void collate(
            @NotNull Alias a,
            @NotNull Value v,
            @NotNull Proxy p,
            @NotNull Reader r
        ) throws IOException {
            Boot:
            while (true) {
                byte b = r.next();

                if (b == GT) {
                    break;
                }

                if (b <= 0x20) {
                    continue;
                }

                if (b == SLASH) {
                    b = r.next();
                    if (b == GT) {
                        p.detach();
                        break;
                    }
                    throw new UnexpectedCrash(
                        "Parse error, `" + b + "`  can't be here"
                    );
                }

                if (b != '=') {
                    a.concat(b);
                    continue;
                }

                b = r.next();
                if (b == QUOT) {
                    while (true) {
                        b = r.next();
                        if (b != QUOT) {
                            v.concat(b);
                            continue;
                        }

                        p.accept(
                            $s, a, v
                        );
                        a.clean();
                        v.clean();
                        continue Boot;
                    }
                }

                throw new UnexpectedCrash(
                    "Parse error, `" + b + "`  can't be here"
                );
            }
        }

        /**
         * Filter out the comments
         *
         * @throws IOException If an I/O error occurs
         */
        protected void decide(
            @NotNull Reader r
        ) throws IOException {
            byte b;
            while (true) {
                b = r.next();
                if (b != '?') {
                    continue;
                }

                b = r.next();
                if (b == GT) {
                    break;
                }

                throw new UnexpectedCrash(
                    "Parse error, `" + b + "`  can't be here"
                );
            }
        }

        /**
         * Escapes the special character
         *
         * @throws IOException If an I/O error occurs
         */
        protected void escape(
            @NotNull Chain c,
            @NotNull Reader r
        ) throws IOException {
            byte b = r.next();
            switch (b) {
                case 'l': {
                    b = r.next();
                    if (b != 't') {
                        break;
                    }
                    b = r.next();
                    if (b != ';') {
                        break;
                    }
                    c.concat(LT);
                    return;
                }
                case 'g': {
                    b = r.next();
                    if (b != 't') {
                        break;
                    }
                    b = r.next();
                    if (b != ';') {
                        break;
                    }
                    c.concat(GT);
                    return;
                }
                case 'q': {
                    b = r.next();
                    if (b != 'u') {
                        break;
                    }
                    b = r.next();
                    if (b != 'o') {
                        break;
                    }
                    b = r.next();
                    if (b != 't') {
                        break;
                    }
                    b = r.next();
                    if (b != ';') {
                        break;
                    }
                    c.concat(QUOT);
                    return;
                }
                case 'a': {
                    b = r.next();
                    if (b == 'm') {
                        b = r.next();
                        if (b != 'p') {
                            break;
                        }
                        b = r.next();
                        if (b != ';') {
                            break;
                        }
                        c.concat(AMP);
                        return;
                    } else if (b == 'p') {
                        b = r.next();
                        if (b != 'o') {
                            break;
                        }
                        b = r.next();
                        if (b != 's') {
                            break;
                        }
                        b = r.next();
                        if (b != ';') {
                            break;
                        }
                        c.concat(APOS);
                        return;
                    }
                }
            }

            throw new UnexpectedCrash(
                "Parse error, `" + b + "`  can't be here"
            );
        }

        /**
         * Filter out the comments
         *
         * @throws IOException If an I/O error occurs
         */
        protected void explain(
            @NotNull Value v,
            @NotNull Reader r
        ) throws IOException {
            byte b;
            Boot:
            switch (r.next()) {
                case '-': {
                    b = r.next();
                    if (b != '-') {
                        throw new UnexpectedCrash(
                            "Parse error, `" + b + "`  can't be here"
                        );
                    }

                    while (true) {
                        b = r.next();
                        if (b != '-') {
                            continue;
                        }

                        b = r.next();
                        if (b != '-') {
                            continue;
                        }

                        b = r.next();
                        if (b == GT) {
                            break Boot;
                        }
                    }
                }
                case '[': {
                    byte[] m = {
                        'C', 'D', 'A', 'T', 'A', '['
                    };
                    for (byte n : m) {
                        b = r.next();
                        if (b == n) {
                            continue;
                        }

                        throw new UnexpectedCrash(
                            "Parse error, `" + b + "`  can't be here"
                        );
                    }

                    while (true) {
                        byte c = r.next();
                        if (c != ']') {
                            v.concat(c);
                            continue;
                        }

                        byte d = r.next();
                        if (d != ']') {
                            v.concat(c);
                            v.concat(d);
                            continue;
                        }

                        byte e = r.next();
                        if (e != GT) {
                            v.concat(c);
                            v.concat(d);
                            v.concat(e);
                        } else {
                            break Boot;
                        }
                    }
                }
            }
        }

        /**
         * Filter out the useless body
         *
         * @throws IOException If an I/O error occurs
         */
        protected void washing(
            int i,
            Reader r
        ) throws IOException {
            boolean in = true;
            Boot:
            while (r.also()) {
                byte b = r.read();
                switch (b) {
                    case '>': {
                        in = false;
                        if (i == 0) {
                            return;
                        } else {
                            continue;
                        }
                    }
                    case '<': {
                        switch (r.next()) {
                            case '?': {
                                decide(r);
                                continue;
                            }
                            case '/': {
                                i--;
                                in = true;
                                continue;
                            }
                            case '!': {
                                byte c = r.next();
                                if (c != '[') {
                                    c = '-';
                                } else {
                                    c = ']';
                                }
                                while (true) {
                                    if (r.next() != c) {
                                        continue;
                                    }
                                    if (r.next() != c) {
                                        continue;
                                    }
                                    if (r.next() == '>') {
                                        continue Boot;
                                    }
                                }
                            }
                            default: {
                                i++;
                                in = false;
                                continue;
                            }
                        }
                    }
                    case '/': {
                        if (in && r.next() == '>') i--;
                        continue;
                    }
                    case '"': {
                        if (in) while (true) {
                            if (r.next() == '"') {
                                continue Boot;
                            }
                        }
                    }
                }
            }
        }

        /**
         * Clear this {@link Motor}
         */
        @Override
        public void clear() {
            alias.clean();
            value.clear();
        }

        /**
         * Close this {@link Motor}
         */
        @Override
        public void close() {
            alias.close();
            value.close();
        }
    }
}
