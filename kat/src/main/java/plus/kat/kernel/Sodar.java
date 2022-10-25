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

import static plus.kat.kernel.Radar.*;

/**
 * @author kraity
 * @since 0.0.5
 */
public class Sodar implements Solver {
    /**
     * snapshot
     */
    protected long data = 0L;
    protected long mask = 1L;
    protected boolean mutable;

    /**
     * snapshot
     */
    protected final Space space;
    protected final Alias alias;
    protected final Value value;

    /**
     * Constructs a sodar with the specified radar
     *
     * @param radar the specified radar
     */
    public Sodar(
        @NotNull Radar radar
    ) {
        space = radar.space;
        alias = radar.alias;
        value = radar.value;
    }

    /**
     * Constructs a sodar with the specified bucket
     *
     * @param b1 the bucket of {@code space}
     * @param b2 the bucket of {@code alias}
     * @param b3 the bucket of {@code value}
     */
    public Sodar(
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
     * @throws ReaderCrash If an I/O error occurs by reader
     * @throws ProxyCrash  If an I/O error occurs by proxy
     * @throws SolverCrash If an I/O error occurs by solver
     */
    @Override
    public void read(
        @NotNull Proxy p,
        @NotNull Reader r
    ) throws IOException {
        // local access
        Space s = space;
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
                throw new SolverCrash(
                    "Parse error, `" + b + "`  can't be here"
                );
            }

            switch (b) {
                case '{': {
                    attach(
                        s, a, p, r, true
                    );
                    break Boot;
                }
                case '[': {
                    attach(
                        s, a, p, r, false
                    );
                    break Boot;
                }
                default: {
                    throw new SolverCrash(
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
                        throw new SolverCrash(
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
                                throw new SolverCrash(
                                    "Parse error, ',' is not ':'"
                                );
                            }
                        }
                        case '}': {
                            if (a.isEmpty()) {
                                detach(p, true);
                                continue Boot;
                            } else {
                                throw new SolverCrash(
                                    "Parse error, '}' is not ':'"
                                );
                            }
                        }
                        default: {
                            throw new SolverCrash(
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
                    throw new SolverCrash(
                        "Parse error, `" + b + "`  can't be here"
                    );
                }

                switch (b) {
                    case '{': {
                        attach(
                            s, a, p, r, true
                        );
                        continue Boot;
                    }
                    case '[': {
                        attach(
                            s, a, p, r, false
                        );
                        continue Boot;
                    }
                    case 'n':
                    case 'N': {
                        escape(r);
                        p.submit(
                            s.as('$'), a, v
                        );
                        a.reset();
                        v.reset();
                        continue;
                    }
                    case '"':
                    case '\'': {
                        escape(v, b, r);
                        p.submit(
                            s.as('s'), a, v
                        );
                        a.reset();
                        v.reset();
                        continue Boot;
                    }
                    case ',': {
                        if (a.isEmpty()) {
                            continue;
                        } else {
                            throw new SolverCrash(
                                "Parse error, ',' is not a value"
                            );
                        }
                    }
                    case '}': {
                        if (a.isEmpty()) {
                            detach(p, true);
                            continue Boot;
                        } else {
                            throw new SolverCrash(
                                "Parse error, '}' is not a value"
                            );
                        }
                    }
                    case ']': {
                        if (a.isEmpty()) {
                            detach(p, false);
                            continue Boot;
                        } else {
                            throw new SolverCrash(
                                "Parse error, ']' is not a value"
                            );
                        }
                    }
                    default: {
                        v.join(b);
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
                        throw new SolverCrash(
                            "Parse error, `" + b + "`  can't be here"
                        );
                    }

                    switch (c) {
                        case ',': {
                            p.submit(
                                s.as('$'), a, v
                            );
                            a.reset();
                            v.reset();
                            continue Boot;
                        }
                        case '}': {
                            p.submit(
                                s.as('$'), a, v
                            );
                            a.reset();
                            v.reset();
                            detach(p, true);
                            continue Boot;
                        }
                        case ']': {
                            p.submit(
                                s.as('$'), a, v
                            );
                            a.reset();
                            v.reset();
                            detach(p, false);
                            continue Boot;
                        }
                        default: {
                            v.join(c);
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
        Space s,
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
            if (p.attach(s.as('M'), a)) {
                mask <<= 1;
                data |= mask;
                mutable = true;
            } else {
                washing(
                    (byte) '}', r
                );
            }
        } else {
            if (p.attach(s.as('L'), a)) {
                mask <<= 1;
                mutable = false;
            } else {
                washing(
                    (byte) ']', r
                );
            }
        }
        a.reset();
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
            throw new SolverCrash(
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
        Alpha c,
        byte e,
        Reader r
    ) throws IOException {
        while (true) {
            byte b = r.next();
            if (b == e) {
                break;
            }

            if (b != '\\') {
                c.join(b);
                continue;
            }

            b = r.next();
            switch (b) {
                case 'b': {
                    b = '\b';
                    break;
                }
                case 'f': {
                    b = '\f';
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
                    rotate(c, r);
                    continue;
                }
            }
            c.join(b);
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
     * Clear this {@link Sodar}
     */
    @Override
    public void clear() {
        data = 0L;
        mask = 1L;
        space.reset();
        alias.reset();
        value.clear();
    }

    /**
     * Close this {@link Sodar}
     */
    @Override
    public void close() {
        data = 0L;
        mask = 1L;
        space.close();
        alias.close();
        value.close();
    }
}
