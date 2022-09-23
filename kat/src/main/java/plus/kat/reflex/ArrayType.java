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

import java.lang.reflect.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface ArrayType extends Type {
    /**
     * Returns the length of type array
     */
    int size();

    /**
     * Returns the type of specified index
     *
     * @param i the specified index
     * @return {@link Type}
     * @throws ArrayIndexOutOfBoundsException If the {@code index} out of range
     */
    @NotNull
    Type getType(int i);

    /**
     * Returns an arrayType of parameters
     *
     * <pre>{@code
     *  Supplier supplier = ...
     *  Spare<Object[]> spare = supplier.lookup(Object[].class);
     *
     *  Proxy proxy = ...
     *  Method method = ...
     *
     *  String text = "..."
     *  method.invoke(
     *     proxy, spare.read(
     *        new Event<Object[]>(text).with(ArrayType.of(method))
     *     )
     *  );
     * }</pre>
     *
     * @throws NullPointerException If the specified {@code executor} is null
     * @since 0.0.4
     */
    @NotNull
    static ArrayType of(
        @NotNull Executable executor
    ) {
        return of(
            executor.getGenericParameterTypes()
        );
    }

    /**
     * Returns an arrayType of arguments
     *
     * @throws NullPointerException If the specified {@code types} is null
     * @since 0.0.4
     */
    @NotNull
    static ArrayType of(
        @NotNull ParameterizedType type
    ) {
        return of(
            type.getActualTypeArguments()
        );
    }

    /**
     * Returns an arrayType of {@code types}
     *
     * @throws NullPointerException If the specified {@code types} is null
     */
    @NotNull
    static ArrayType of(
        @NotNull Type[] types
    ) {
        if (types != null) {
            return new Impl(types);
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * Returns an arrayType of {@code types}
     *
     * @throws NullPointerException If the specified {@code types} is null
     */
    @NotNull
    static ArrayType of(
        @NotNull Type[] types, boolean copy
    ) {
        if (!copy) {
            return of(types);
        } else {
            return new Impl(
                types.clone()
            );
        }
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    class Impl implements ArrayType {

        private final Type[] types;

        private Impl(
            @NotNull Type[] it
        ) {
            types = it;
        }

        @Override
        public int size() {
            return types.length;
        }

        @Override
        public Type getType(int i) {
            return types[i];
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('<');

            boolean bo = true;
            for (Type type : types) {
                if (bo) {
                    bo = false;
                } else {
                    sb.append(", ");
                }
                sb.append(
                    type.getTypeName()
                );
            }

            sb.append('>');
            return sb.toString();
        }
    }
}
