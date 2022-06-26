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
package plus.kat.reflex;

import plus.kat.anno.NotNull;

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface ArrayType extends Type {
    /**
     * Returns the length of Type array
     */
    int size();

    /**
     * Returns the {@link Type} of specified index
     */
    @NotNull
    Type getType(
        int index
    );

    /**
     * Returns an implementation of {@code types}
     */
    static Impl of(
        @NotNull Type[] types
    ) {
        return new Impl(
            types, false
        );
    }

    /**
     * Returns an implementation of {@code types}
     */
    static Impl of(
        @NotNull Type[] types, boolean copy
    ) {
        return new Impl(
            types, copy
        );
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    class Impl implements ArrayType {

        private final Type[] types;

        public Impl(
            @NotNull Type[] types
        ) {
            this(types, false);
        }

        public Impl(
            @NotNull Type[] types, boolean copy
        ) {
            this.types = copy ? types.clone() : types;
        }

        @Override
        public int size() {
            return types.length;
        }

        @Override
        public Type getType(
            int index
        ) {
            return types[index];
        }
    }
}
