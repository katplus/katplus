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

import static plus.kat.chain.Space.$s;
import static plus.kat.chain.Space.$M;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Docx implements Solver {
    /**
     * chain stream
     */
    protected final Alias alias;
    protected final Value value;

    private static final byte
        LT = '<', GT = '>',
        AMP = '&', APOS = '\'',
        QUOT = '"', SLASH = '/';

    /**
     * @param radar the specified {@link Radar}
     */
    public Docx(
        @NotNull Radar radar
    ) {
        alias = radar.alias;
        value = radar.value;
    }

    /**
     * @param p specify the data transfer pipeline
     * @param r specify the source of decoded data
     * @throws IOException Unexpected errors by {@link Pipe} or {@link Reader}
     */
    @Override
    public void read(
        @NotNull Pipe p,
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
                            if (a.tail(SLASH)) {
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

    protected void collate(
        @NotNull Alias a,
        @NotNull Value v,
        @NotNull Pipe p,
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
     * clear this {@link Docx}
     */
    @Override
    public void clear() {
        alias.clean();
        value.clear();
    }

    /**
     * close this {@link Docx}
     */
    @Override
    public void close() {
        alias.close();
        value.close();
    }
}
