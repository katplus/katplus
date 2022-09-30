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

import static plus.kat.chain.Space.$;
import static plus.kat.chain.Space.$s;
import static plus.kat.chain.Space.$M;
import static plus.kat.chain.Space.$L;
import static plus.kat.kernel.Radar.uncork;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Edge implements Solver {
    /**
     * chain stream
     */
    protected final Alias alias;
    protected final Value value;

    /**
     * codec analyze
     */
    private long data = 0L;
    private long mask = 1L;
    private boolean mutable;

    /**
     * @param radar the specified {@code radar}
     */
    public Edge(
        @NotNull Radar radar
    ) {
        alias = radar.alias;
        value = radar.value;
    }

    /**
     * @param b1 the bucket of {@code alias}
     * @param b2 the bucket of {@code value}
     */
    public Edge(
        @NotNull Bucket b1,
        @NotNull Bucket b2
    ) {
        alias = new Alias(b1);
        value = new Value(b2);
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
     * @param e specify the data transfer pipeline
     * @param r specify the source of decoded data
     * @throws IOException Unexpected errors by {@link Entry} or {@link Reader}
     */
    @Override
    public void read(
        @NotNull Entry e,
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
                        a, e, r, true
                    );
                    break Boot;
                }
                case '[': {
                    attach(
                        a, e, r, false
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
                                detach(e, true);
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
                            a, e, r, true
                        );
                        continue Boot;
                    }
                    case '[': {
                        attach(
                            a, e, r, false
                        );
                        continue Boot;
                    }
                    case 'n':
                    case 'N': {
                        escape(r);
                        e.accept(
                            $, a, v
                        );
                        a.clean();
                        v.clean();
                        continue;
                    }
                    case '"':
                    case '\'': {
                        escape(v, b, r);
                        e.accept(
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
                            detach(e, true);
                            continue Boot;
                        } else {
                            throw new UnexpectedCrash(
                                "Unexpectedly, '}' is not a value"
                            );
                        }
                    }
                    case ']': {
                        if (a.isEmpty()) {
                            detach(e, false);
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
                            e.accept(
                                $, a, v
                            );
                            a.clean();
                            v.clean();
                            continue Boot;
                        }
                        case '}': {
                            e.accept(
                                $, a, v
                            );
                            a.clean();
                            v.clean();
                            detach(e, true);
                            continue Boot;
                        }
                        case ']': {
                            e.accept(
                                $, a, v
                            );
                            a.clean();
                            v.clean();
                            detach(e, false);
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
        Entry e,
        Reader r,
        boolean b
    ) throws IOException {
        if (mask == Long.MIN_VALUE) {
            throw new UnexpectedCrash(
                "Unexpectedly, out of range"
            );
        }

        if (b) {
            if (e.attach($M, a)) {
                mask <<= 1;
                data |= mask;
                mutable = true;
            } else {
                dropdown(
                    (byte) '}', r
                );
            }
        } else {
            if (e.attach($L, a)) {
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
        Entry e,
        boolean b
    ) throws IOException {
        if (mutable == b) {
            e.detach();
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
                    uncork(c, r);
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
     * Clear this {@link Edge}
     */
    @Override
    public void clear() {
        data = 0L;
        mask = 1L;
        alias.clean();
        value.clear();
    }

    /**
     * Close this {@link Edge}
     */
    @Override
    public void close() {
        data = 0L;
        mask = 1L;
        alias.close();
        value.close();
    }
}
