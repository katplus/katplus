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
package plus.kat.spare;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

/**
 * @author kraity
 * @since 0.0.5
 */
public interface Converter {
    /**
     * Converts the {@link Object} to {@link E}
     *
     * <pre>{@code
     *  Class<User> clazz = ...
     *  Converter converter = ...
     *
     *  User user = converter.cast(
     *      clazz, "{:id(1):name(kraity)}"
     *  )
     *  User user = converter.cast(
     *      clazz, Map.of("id", 1, "name", "kraity")
     *  );
     * }</pre>
     *
     * @param klass  the specified klass for lookup
     * @param object the specified object to be converted
     * @return {@link E} or {@code null}
     * @throws IllegalStateException If the object cannot be converted to {@link E}
     */
    @Nullable <E> E cast(
        @NotNull Class<E> klass,
        @Nullable Object object
    );

    /**
     * Converts the {@link Object} to {@link E}
     *
     * <pre>{@code
     *  String clazz = "plus.kat.entity.User"
     *  Converter converter = ...
     *
     *  User user = converter.cast(
     *      clazz, "{:id(1):name(kraity)}"
     *  );
     *  User user = converter.cast(
     *      clazz, Map.of("id", 1, "name", "kraity")
     *  );
     * }</pre>
     *
     * @param klass  the specified klass for lookup
     * @param object the specified object to be converted
     * @return {@link E} or {@code null}
     * @throws ClassCastException    If {@link E} is not an instance of the klass
     * @throws IllegalStateException If the object cannot be converted to {@link E}
     */
    @Nullable <E> E cast(
        @NotNull CharSequence klass,
        @Nullable Object object
    );
}
