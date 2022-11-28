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

import static plus.kat.Doc.*;

/**
 * @author kraity
 * @since 0.0.5
 */
public class Podar implements Solver {
    /**
     * snapshot
     */
    protected final Space space;
    protected final Alias alias;
    protected final Value value;

    /**
     * Constructs a podar with the specified bucket
     *
     * @param b1 the bucket of {@code space}
     * @param b2 the bucket of {@code alias}
     * @param b3 the bucket of {@code value}
     */
    public Podar(
        @NotNull Bucket b1,
        @NotNull Bucket b2,
        @NotNull Bucket b3
    ) {
        space = new Space(b1);
        alias = new Alias(b2);
        value = new Value(b3);
    }

    /**
     * Constructs a podar with the specified chains
     *
     * @param space the specified {@code space}
     * @param alias the specified {@code alias}
     * @param value the specified {@code value}
     */
    public Podar(
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
     * @param e the specified data transfer pipeline
     * @param r the specified data source to be parsed
     * @throws IOException If an I/O error occurs by pipage
     * @throws FlowCrash   If an I/O error occurs by reader
     * @throws SolverCrash If an I/O error occurs by solver
     */
    @Override
    public void read(
        @NotNull Reader r,
        @NotNull Pipage e
    ) throws IOException {
        try {
            // local access
            Space s = space;
            Alias a = alias;
            Value v = value;

            Boot:
            // decode xml stream
            while (r.also()) {
                byte b = r.read();
                if (b != LT) {
                    if (b != AMP) {
                        v.join(b);
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
                                v.reset();
                                e = e.onClose(
                                    true, true
                                );
                                continue Boot;
                            }
                        } else {
                            int i = 0;
                            while (true) {
                                byte d = r.next();
                                if (d == GT) {
                                    e.onEmit(
                                        s.as('s'), a, v
                                    );
                                    a.reset();
                                    v.reset();
                                    continue Boot;
                                }

                                if (a.is(i++, d)) {
                                    continue;
                                }

                                throw new SolverCrash(
                                    "Parse error, `" + b + "`  can't be in namespace"
                                );
                            }
                        }
                    }
                    default: {
                        if (!a.isEmpty()) {
                            Pipage it = e.onOpen(
                                s.as('M'), a
                            );
                            if (it != null) {
                                e = it;
                                a.reset();
                            } else {
                                a.reset();
                                v.reset();
                                washing(2, r);
                                continue;
                            }
                        }

                        v.reset();
                        a.join(c);

                        while (true) {
                            b = r.next();
                            if (b == AMP) {
                                escape(a, r);
                                continue;
                            }

                            if (b == GT) {
                                if (a.get(-1) == SLASH) {
                                    a.reset();
                                }
                                continue Boot;
                            }

                            if (b != ' ') {
                                a.join(b);
                                continue;
                            }

                            Pipage it = e.onOpen(
                                s.as('M'), a
                            );
                            if (it != null) {
                                e = it;
                                a.reset();
                                e = collate(
                                    s, a, v, r, e
                                );
                                a.reset();
                                v.reset();
                            } else {
                                a.reset();
                                v.reset();
                                washing(1, r);
                            }
                            continue Boot;
                        }
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
     * Collects the attribute values
     *
     * @throws IOException If an I/O error occurs
     */
    protected Pipage collate(
        @NotNull Space s,
        @NotNull Alias a,
        @NotNull Value v,
        @NotNull Reader r,
        @NotNull Pipage e
    ) throws IOException {
        Boot:
        while (true) {
            byte b = r.next();

            if (b == GT) {
                return e;
            }

            if (b <= 0x20) {
                continue;
            }

            if (b == SLASH) {
                b = r.next();
                if (b == GT) {
                    return e.onClose(
                        true, true
                    );
                }
                throw new SolverCrash(
                    "Parse error, `" + b + "`  can't be here"
                );
            }

            if (b != '=') {
                a.join(b);
                continue;
            }

            b = r.next();
            if (b == QUOT) {
                while (true) {
                    b = r.next();
                    if (b != QUOT) {
                        v.join(b);
                        continue;
                    }
                    e.onEmit(
                        s.as('s'), a, v
                    );
                    a.reset();
                    v.reset();
                    continue Boot;
                }
            }

            throw new SolverCrash(
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

            throw new SolverCrash(
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
                c.join(LT);
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
                c.join(GT);
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
                c.join(QUOT);
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
                    c.join(AMP);
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
                    c.join(APOS);
                    return;
                }
            }
        }

        throw new SolverCrash(
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
                    throw new SolverCrash(
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

                    throw new SolverCrash(
                        "Parse error, `" + b + "`  can't be here"
                    );
                }

                while (true) {
                    byte c = r.next();
                    if (c != ']') {
                        v.join(c);
                        continue;
                    }

                    byte d = r.next();
                    if (d != ']') {
                        v.join(c);
                        v.join(d);
                        continue;
                    }

                    byte e = r.next();
                    if (e != GT) {
                        v.join(c);
                        v.join(d);
                        v.join(e);
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
     * Clears this {@link Podar}
     */
    @Override
    public void clear() {
        space.reset();
        alias.reset();
        value.clear();
    }

    /**
     * Closes this {@link Podar}
     */
    @Override
    public void close() {
        space.close();
        alias.close();
        value.close();
    }
}
