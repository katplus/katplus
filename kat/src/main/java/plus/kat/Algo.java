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
package plus.kat;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.chain.*;

import static plus.kat.chain.Chain.Unsafe.value;

/**
 * @author kraity
 * @since 0.0.5
 */
public final class Algo {

    public static final Algo
        KAT = new Algo((byte) 0x5E, "kat"),
        DOC = new Algo((byte) 0x5C, "xml"),
        JSON = new Algo((byte) 0x5C, "json");

    private final byte esc;
    private final String name;

    /**
     * Constructs an algo with the specified name
     *
     * @param name the specified algo name
     * @throws NullPointerException If the specified name is null
     */
    public Algo(
        @NotNull String name
    ) {
        this.esc = 0x5C;
        this.name = name.toLowerCase();
    }

    /**
     * Constructs an algo with the specified name
     *
     * @param esc  the specified esc char
     * @param name the specified algo name
     * @throws NullPointerException If the specified name is null
     */
    public Algo(
        @NotNull byte esc,
        @NotNull String name
    ) {
        this.esc = esc;
        this.name = name.toLowerCase();
    }

    /**
     * Constructs an algo with the specified name
     *
     * @param esc  the specified esc char
     * @param name the specified algo name
     * @throws NullPointerException  If the specified name is null
     * @throws IllegalStateException If the specified esc exceeds 0xFF
     */
    public Algo(
        @NotNull char esc,
        @NotNull String name
    ) {
        if (esc < 0x100) {
            this.esc = (byte) esc;
            this.name = name.toLowerCase();
        } else {
            throw new IllegalStateException(
                "The specified esc<" + (int) esc + "> exceeds 0xFF"
            );
        }
    }

    /**
     * Returns the escape char of this {@link Algo}
     */
    public byte esc() {
        return esc;
    }

    /**
     * Returns the lowercase name of this {@link Algo}
     */
    @NotNull
    public String name() {
        return name;
    }

    /**
     * Compares this {@link Algo} with the specified algo
     */
    public boolean is(
        @NotNull String algo
    ) {
        return name.equals(algo);
    }

    /**
     * Returns a hash code value for this {@link Algo}
     */
    @Override
    public int hashCode() {
        return name.hashCode() ^ esc;
    }

    /**
     * Compares this {@link Algo} to the specified {@link Object}
     */
    @Override
    public boolean equals(
        @Nullable Object o
    ) {
        if (o instanceof Algo) {
            Algo a = (Algo) o;
            return esc == a.esc &&
                name.equals(a.name);
        }
        return false;
    }

    /**
     * Returns the {@link Algo} of the sample data format
     *
     * @param text the specified sample data
     * @throws NullPointerException If the specified text is null
     */
    @Nullable
    public static Algo of(
        @NotNull Chain text
    ) {
        int e = text.length();
        if (e < 2) {
            return null;
        }
        byte[] it = value(text);

        int i = 0;
        byte c1, c2;

        do {
            c1 = it[i];
        } while (
            c1 <= 0x20 && ++i < e
        );

        do {
            c2 = it[e - 1];
        } while (
            c2 <= 0x20 && --e > i
        );

        if (c2 != '}') {
            // ()
            if (c2 == ')') {
                return KAT;
            }

            // []
            if (c2 == ']') {
                if (c1 == '[') {
                    return JSON;
                } else {
                    return null;
                }
            }

            // <>
            if (c2 == '>') {
                if (c1 == '<' && e > 6) {
                    return DOC;
                } else {
                    return null;
                }
            }
        } else {
            // ${}
            if (c1 != '{') {
                return KAT;
            }

            int k = i + 1;
            while (true) {
                switch (c1 = it[k++]) {
                    case '"':
                    case '\'':
                    case '\\': {
                        return JSON;
                    }
                    default: {
                        if (e <= k || c1 > 0x20)
                            return KAT;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the {@link Algo} of the sample data format
     *
     * @param text the specified sample data
     * @throws NullPointerException If the specified text is null
     */
    @Nullable
    public static Algo of(
        @NotNull CharSequence text
    ) {
        int e = text.length();
        if (e < 2) {
            return null;
        }

        int i = 0;
        char c1, c2;

        do {
            c1 = text.charAt(i);
        } while (
            c1 <= 0x20 && ++i < e
        );

        do {
            c2 = text.charAt(e - 1);
        } while (
            c2 <= 0x20 && --e > i
        );

        if (c2 != '}') {
            // ()
            if (c2 == ')') {
                return KAT;
            }

            // []
            if (c2 == ']') {
                if (c1 == '[') {
                    return JSON;
                } else {
                    return null;
                }
            }

            // <>
            if (c2 == '>') {
                if (c1 == '<' && e > 6) {
                    return DOC;
                } else {
                    return null;
                }
            }
        } else {
            // ${}
            if (c1 != '{') {
                return KAT;
            }

            int k = i + 1;
            while (true) {
                c1 = text.charAt(k++);
                switch (c1) {
                    case '"':
                    case '\'':
                    case '\\': {
                        return JSON;
                    }
                    default: {
                        if (e <= k || c1 > 0x20)
                            return KAT;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return the concatenated string for the {@link Algo} name, etc.
     */
    @Override
    public String toString() {
        return "Algo(" + name + ")";
    }
}
