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
     * chain stream
     */
    protected final Space space;
    protected final Alias alias;
    protected final Value value;

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
        // event status
        byte event = 0;

        // local access
        Space s = space;
        Alias a = alias;
        Value v = value;

        Radar:
        // decode kat stream
        while (r.also()) switch (event) {
            case 0: {
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
                            event = 2;
                            continue Radar;
                        }
                        case ':': {
                            event = 1;
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
            case 1: {
                while (true) {
                    byte b = r.next();
                    if (b <= 0x20) {
                        throw new UnexpectedCrash(
                            "Unexpectedly, byte '" + b + "' <= 32 in alias"
                        );
                    }
                    switch (b) {
                        case '{': {
                            event = 0;
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
                            event = 2;
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
            case 2: {
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
                            event = 0;
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
                rotate(c, r);
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
     * @author kraity
     * @since 0.0.1
     */
    public class DOC implements Solver {
        /**
         * codec analyze
         */
        private static final byte
            LT = '<', GT = '>',
            AMP = '&', APOS = '\'',
            QUOT = '"', SLASH = '/';

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
            // local
            Alias a = alias;
            Value v = value;

            Boot:
            // decode doc stream
            while (r.also()) {
                byte b = r.read();
                if (b != LT) {
                    if (b != AMP) {
                        v.chain(b);
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
                                    "Unexpectedly, byte '" + d + "' in end space"
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
                                dropdown(2, r);
                                continue;
                            }
                        }

                        v.clean();
                        a.chain(c);

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
                                a.chain(b);
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
                                dropdown(1, r);
                                continue Boot;
                            }
                        }
                    }
                }
            }
        }

        /**
         * Collect attribute values
         *
         * @throws IOException Unexpected errors by {@link Reader}
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
                        "Unexpectedly, byte '" + b + "'"
                    );
                }

                if (b != '=') {
                    a.chain(b);
                    continue;
                }

                b = r.next();
                if (b == QUOT) {
                    while (true) {
                        b = r.next();
                        if (b != QUOT) {
                            v.chain(b);
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
                    "Unexpectedly, byte '" + b + "'"
                );
            }
        }

        /**
         * Filter comments
         *
         * @throws IOException Unexpected errors by {@link Reader}
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
                    "Unexpectedly, byte '" + b + "'"
                );
            }
        }

        /**
         * Escape special character
         *
         * @throws IOException Unexpected errors by {@link Reader}
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
                    c.chain(LT);
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
                    c.chain(GT);
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
                    c.chain(QUOT);
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
                        c.chain(AMP);
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
                        c.chain(APOS);
                        return;
                    }
                }
            }

            throw new UnexpectedCrash(
                "Unexpectedly, byte '" + b + "'"
            );
        }

        /**
         * Filter comments
         *
         * @throws IOException Unexpected errors by {@link Reader}
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
                            "Unexpectedly, byte '" + b + "'"
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
                            "Unexpectedly, byte '" + b + "'"
                        );
                    }

                    while (true) {
                        byte c = r.next();
                        if (c != ']') {
                            v.chain(c);
                            continue;
                        }

                        byte d = r.next();
                        if (d != ']') {
                            v.chain(c);
                            v.chain(d);
                            continue;
                        }

                        byte e = r.next();
                        if (e != GT) {
                            v.chain(c);
                            v.chain(d);
                            v.chain(e);
                        } else {
                            break Boot;
                        }
                    }
                }
            }
        }

        /**
         * Filter out the useless
         *
         * @throws IOException Unexpected errors by {@link Reader}
         */
        protected void dropdown(
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
         * Clear this {@link DOC}
         */
        @Override
        public void clear() {
            alias.clean();
            value.clear();
        }

        /**
         * Close this {@link DOC}
         */
        @Override
        public void close() {
            alias.close();
            value.close();
        }
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    public class JSON implements Solver {
        /**
         * codec analyze
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
            while (r.also()) {
                byte b = r.read();
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
                        "Unexpectedly, byte '" + b + "' <= 32"
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
                            "Unexpectedly, byte '" + b + "'"
                        );
                    }
                }
            }

            Boot:
            // codec
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
                                "Unexpectedly, byte '" + b + "' <= 32"
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
                                        "Unexpectedly, ',' is not ':'"
                                    );
                                }
                            }
                            case '}': {
                                if (a.isEmpty()) {
                                    detach(p, true);
                                    continue Boot;
                                } else {
                                    throw new UnexpectedCrash(
                                        "Unexpectedly, '}' is not ':'"
                                    );
                                }
                            }
                            default: {
                                throw new UnexpectedCrash(
                                    "Unexpectedly, byte '" + b + "' in alias"
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
                            "Unexpectedly, byte '" + b + "' <= 32"
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
                                    "Unexpectedly, ',' is not a value"
                                );
                            }
                        }
                        case '}': {
                            if (a.isEmpty()) {
                                detach(p, true);
                                continue Boot;
                            } else {
                                throw new UnexpectedCrash(
                                    "Unexpectedly, '}' is not a value"
                                );
                            }
                        }
                        case ']': {
                            if (a.isEmpty()) {
                                detach(p, false);
                                continue Boot;
                            } else {
                                throw new UnexpectedCrash(
                                    "Unexpectedly, ']' is not a value"
                                );
                            }
                        }
                        default: {
                            v.chain(b);
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
                                "Unexpectedly, byte '" + b + "' <= 32"
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
                                v.chain(c);
                            }
                        }
                    }
                }
            }
        }

        /**
         * Notify to create a receiver
         *
         * @param b is it a map?
         * @throws IOException Unexpected errors by {@link Reader}
         */
        protected void attach(
            Alias a,
            Proxy p,
            Reader r,
            boolean b
        ) throws IOException {
            if (mask == Long.MIN_VALUE) {
                throw new UnexpectedCrash(
                    "Unexpectedly, out of range"
                );
            }

            if (b) {
                if (p.attach($M, a)) {
                    mask <<= 1;
                    data |= mask;
                    mutable = true;
                } else {
                    dropdown(
                        (byte) '}', r
                    );
                }
            } else {
                if (p.attach($L, a)) {
                    mask <<= 1;
                    mutable = false;
                } else {
                    dropdown(
                        (byte) ']', r
                    );
                }
            }
            a.clean();
        }

        /**
         * Notify the current receiver to end the transmission
         *
         * @throws IOException Unexpected errors by {@link Reader}
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
                throw new UnexpectedCrash(
                    "Unexpectedly, mismatched terminator"
                );
            }
        }

        /**
         * Escape special character
         *
         * @throws IOException Unexpected errors by {@link Reader}
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
                    "Unexpectedly, N" +
                        (char) (b2 & 0xFF) +
                        (char) (b3 & 0xFF) +
                        (char) (b3 & 0xFF) + " is not null"
                );
            }
        }

        /**
         * Escape special character
         *
         * @throws IOException Unexpected errors by {@link Reader}
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
                    c.chain(b);
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
                c.chain(b);
            }
        }

        /**
         * Filter out the useless
         *
         * @throws IOException Unexpected errors by {@link Reader}
         */
        protected void dropdown(
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
                        dropdown(
                            (byte) '}', r
                        );
                        continue;
                    }
                    case '[': {
                        dropdown(
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
         * Clear this {@link JSON}
         */
        @Override
        public void clear() {
            data = 0L;
            mask = 1L;
            alias.clean();
            value.clear();
        }

        /**
         * Close this {@link JSON}
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
     * Escape unicode character
     *
     * @throws IOException Unexpected errors by {@link Reader}
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
            int d1 = digit(r.next());
            int d2 = digit(r.next());
            int d3 = digit(r.next());
            int d4 = digit(r.next());

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
}
