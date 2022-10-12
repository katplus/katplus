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

import java.util.Locale;

/**
 * @author kraity
 * @since 0.0.5
 */
public final class Algo {

    public static final Algo
        KAT = new Algo("kat"),
        DOC = new Algo("xml"),
        JSON = new Algo("json");

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
        this.name = name.toLowerCase(
            Locale.ENGLISH
        );
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
        return name.hashCode();
    }

    /**
     * Compares this {@link Algo} to the specified {@link Object}
     */
    @Override
    public boolean equals(
        @Nullable Object o
    ) {
        return o instanceof Algo
            && name.equals(
            ((Algo) o).name
        );
    }

    /**
     * Return the concatenated string for the {@link Algo} name, etc.
     */
    @Override
    public String toString() {
        return "Algo(" + name + ")";
    }
}
