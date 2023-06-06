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

import static plus.kat.Doc.*;

/**
 * @author kraity
 * @since 0.0.5
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
     * @param alias the specified {@code alias} of solver
     * @param space the specified {@code space} of solver
     * @param value the specified {@code value} of solver
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
     * Reads xml stream
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
        @NotNull Flow t,
        @NotNull Spider n
    ) throws IOException {
        try {
            // local access
            Space s = space;
            Alias a = alias;
            Value v = value;

            Podar:
            // decode xml stream
            while (t.also()) {
                byte b = t.read();
                if (b != LT) {
                    if (b != AMP) {
                        v.join(b);
                    } else {
                        escape(t, v);
                    }
                    continue;
                }

                byte c = t.next();
                switch (c) {
                    case '?': {
                        decide(t);
                        continue;
                    }
                    case '!': {
                        explain(t, v);
                        continue;
                    }
                    case SLASH: {
                        if (a.isEmpty()) {
                            while (true) {
                                byte d = t.next();
                                if (d != GT) {
                                    continue;
                                }
                                v.clear();
                                n = n.onClose(
                                    true, true
                                );
                                continue Podar;
                            }
                        } else {
                            int i = 0;
                            while (true) {
                                byte d = t.next();
                                if (d == GT) {
                                    n.onEach(
                                        a, s.slip(0), v
                                    );
                                    a.clear();
                                    v.clear();
                                    continue Podar;
                                }

                                if (a.get(i++) == d) {
                                    continue;
                                }

                                throw new IOException(
                                    "Parse error, `" + b + "`  can't be in namespace"
                                );
                            }
                        }
                    }
                    default: {
                        if (!a.isEmpty()) {
                            Spider it = n.onOpen(
                                a, s.slip(0, (byte) '{')
                            );
                            if (it != null) {
                                n = it;
                                a.clear();
                            } else {
                                a.clear();
                                v.clear();
                                washing(2, t);
                                continue;
                            }
                        }

                        v.clear();
                        a.join(c);

                        while (true) {
                            b = t.next();
                            if (b == AMP) {
                                escape(t, a);
                                continue;
                            }

                            if (b == GT) {
                                if (a.get(-1) == SLASH) {
                                    a.clear();
                                }
                                continue Podar;
                            }

                            if (b != ' ') {
                                a.join(b);
                                continue;
                            }

                            Spider it = n.onOpen(
                                a, s.slip(0, (byte) '{')
                            );
                            if (it != null) {
                                n = it;
                                a.clear();
                                n = collate(
                                    t, s, a, v, n
                                );
                                a.clear();
                                v.clear();
                            } else {
                                a.clear();
                                v.clear();
                                washing(1, t);
                            }
                            continue Podar;
                        }
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
     * Collects the attribute values
     *
     * @throws IOException If an I/O error occurs
     */
    protected Spider collate(
        @NotNull Flow t,
        @NotNull Space s,
        @NotNull Alias a,
        @NotNull Value v,
        @NotNull Spider n
    ) throws IOException {
        stage:
        while (true) {
            byte w = t.next();

            if (w == GT) {
                return n;
            }

            if (w <= 0x20) {
                continue;
            }

            if (w == SLASH) {
                w = t.next();
                if (w == GT) {
                    return n.onClose(
                        true, true
                    );
                }
                throw new IOException(
                    "Parse error, `" + w + "`  can't be here"
                );
            }

            if (w != '=') {
                a.join(w);
                continue;
            }

            w = t.next();
            if (w == QUOT) {
                while (true) {
                    w = t.next();
                    if (w != QUOT) {
                        v.join(w);
                        continue;
                    }
                    n.onEach(
                        a, s.slip(0), v
                    );
                    a.clear();
                    v.clear();
                    continue stage;
                }
            }

            throw new IOException(
                "Parse error, `" + w + "`  can't be here"
            );
        }
    }

    /**
     * Filter out the comments
     *
     * @throws IOException If an I/O error occurs
     */
    protected void decide(
        @NotNull Flow t
    ) throws IOException {
        byte w;
        while (true) {
            w = t.next();
            if (w != '?') {
                continue;
            }

            w = t.next();
            if (w == GT) {
                break;
            }

            throw new IOException(
                "Parse error, `" + w + "`  can't be here"
            );
        }
    }

    /**
     * Escapes the special character
     *
     * @throws IOException If an I/O error occurs
     */
    protected void escape(
        @NotNull Flow t,
        @NotNull Value v
    ) throws IOException {
        byte w = t.next();
        switch (w) {
            case 'l': {
                w = t.next();
                if (w != 't') {
                    break;
                }
                w = t.next();
                if (w != ';') {
                    break;
                }
                v.join(LT);
                return;
            }
            case 'g': {
                w = t.next();
                if (w != 't') {
                    break;
                }
                w = t.next();
                if (w != ';') {
                    break;
                }
                v.join(GT);
                return;
            }
            case 'q': {
                w = t.next();
                if (w != 'u') {
                    break;
                }
                w = t.next();
                if (w != 'o') {
                    break;
                }
                w = t.next();
                if (w != 't') {
                    break;
                }
                w = t.next();
                if (w != ';') {
                    break;
                }
                v.join(QUOT);
                return;
            }
            case 'a': {
                w = t.next();
                if (w == 'm') {
                    w = t.next();
                    if (w != 'p') {
                        break;
                    }
                    w = t.next();
                    if (w != ';') {
                        break;
                    }
                    v.join(AMP);
                    return;
                } else if (w == 'p') {
                    w = t.next();
                    if (w != 'o') {
                        break;
                    }
                    w = t.next();
                    if (w != 's') {
                        break;
                    }
                    w = t.next();
                    if (w != ';') {
                        break;
                    }
                    v.join(APOS);
                    return;
                }
            }
        }

        throw new IOException(
            "Parse error, `" + w + "`  can't be here"
        );
    }

    /**
     * Filter out the comments
     *
     * @throws IOException If an I/O error occurs
     */
    protected void explain(
        @NotNull Flow t,
        @NotNull Value v
    ) throws IOException {
        byte w;
        stage:
        switch (t.next()) {
            case '-': {
                w = t.next();
                if (w != '-') {
                    throw new IOException(
                        "Parse error, `" + w + "`  can't be here"
                    );
                }

                while (true) {
                    w = t.next();
                    if (w != '-') {
                        continue;
                    }

                    w = t.next();
                    if (w != '-') {
                        continue;
                    }

                    w = t.next();
                    if (w == GT) {
                        break stage;
                    }
                }
            }
            case '[': {
                byte[] m = {
                    'C', 'D', 'A', 'T', 'A', '['
                };
                for (byte n : m) {
                    w = t.next();
                    if (w == n) {
                        continue;
                    }

                    throw new IOException(
                        "Parse error, `" + w + "`  can't be here"
                    );
                }

                while (true) {
                    byte c = t.next();
                    if (c != ']') {
                        v.join(c);
                        continue;
                    }

                    byte d = t.next();
                    if (d != ']') {
                        v.join(c);
                        v.join(d);
                        continue;
                    }

                    byte e = t.next();
                    if (e != GT) {
                        v.join(c);
                        v.join(d);
                        v.join(e);
                    } else {
                        break stage;
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
        @NotNull int i,
        @NotNull Flow t
    ) throws IOException {
        boolean in = true;
        stage:
        while (t.also()) {
            byte w = t.read();
            switch (w) {
                case '>': {
                    in = false;
                    if (i == 0) {
                        return;
                    } else {
                        continue;
                    }
                }
                case '<': {
                    switch (t.next()) {
                        case '?': {
                            decide(t);
                            continue;
                        }
                        case '/': {
                            i--;
                            in = true;
                            continue;
                        }
                        case '!': {
                            byte c = t.next();
                            if (c != '[') {
                                c = '-';
                            } else {
                                c = ']';
                            }
                            while (true) {
                                if (t.next() != c) {
                                    continue;
                                }
                                if (t.next() != c) {
                                    continue;
                                }
                                if (t.next() == '>') {
                                    continue stage;
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
                    if (in && t.next() == '>') i--;
                    continue;
                }
                case '"': {
                    if (in) while (true) {
                        if (t.next() == '"') {
                            continue stage;
                        }
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
            new Alias(512),
            new Space(32),
            new Value(8192)
        );
    }
}
