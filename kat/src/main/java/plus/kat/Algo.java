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

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class Algo {

    public static final int
        kat = 0x6B6174,
        doc = 0x786D6C,
        json = 0x6A736F6E;

    public static final Algo
        KAT = new Algo("kat"),
        DOC = new Algo("xml"),
        JSON = new Algo("json");

    private final int hash;
    private final String name;

    /**
     * Constructs an algo with the specified name, the name is
     * required to be 1~4 in length and non-space visual ascii code
     *
     * @param name the specified algo name
     * @throws NullPointerException  If the specified name is null
     * @throws IllegalStateException If the specified name is illegal
     */
    public Algo(
        @NotNull String name
    ) {
        int l = name.length();
        if (1 <= l && l <= 4) {
            int hash = 0;
            for (int i = 0; i < l; i++) {
                int n = name.charAt(i);
                if (0x20 < n && n < 0x7F) {
                    hash = hash << 8 | n;
                } else {
                    throw new IllegalStateException(
                        "Received illegal name: " + name
                    );
                }
            }
            this.hash = hash;
            this.name = name;
        } else {
            throw new IllegalStateException(
                "Currently, required length is between 1 and 4,"
                    + " and received " + name + " is out of range"
            );
        }
    }

    /**
     * Returns the name of this {@link Algo}
     */
    @NotNull
    public String name() {
        return name;
    }

    /**
     * Returns the hash of this {@link Algo}
     */
    @Override
    public int hashCode() {
        return hash;
    }

    /**
     * Compares this algo to the specified object
     */
    @Override
    public boolean equals(
        @Nullable Object o
    ) {
        return o == this
            || (o instanceof Algo
            && hash == ((Algo) o).hash);
    }

    /**
     * Returns the algo of this name
     *
     * @param name the specified algo name
     */
    public static Algo of(
        @NotNull String name
    ) {
        switch (name) {
            case "kat": {
                return KAT;
            }
            case "xml": {
                return DOC;
            }
            case "json": {
                return JSON;
            }
        }
        return new Algo(name);
    }

    /**
     * Return a literal string for this {@link Algo}'s name
     */
    @Override
    public String toString() {
        return "Algo(" + name + ")";
    }
}
