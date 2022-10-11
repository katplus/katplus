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
package plus.kat.entity;

import plus.kat.anno.NotNull;

import plus.kat.crash.*;

/**
 * @author kraity
 * @since 0.0.2
 */
@FunctionalInterface
public interface Maker<K> {
    /**
     * If this {@link Maker} can create an instance,
     * it returns it, otherwise it will throw {@link Collapse}
     *
     * @param args the specified args
     * @return {@link K}, it is not null
     * @throws Collapse If a failure occurs
     */
    @NotNull
    K apply(
        @NotNull Object[] args
    );
}
