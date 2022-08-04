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

import plus.kat.anno.Nullable;

import plus.kat.*;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.3
 */
public abstract class NumberSpare<K extends Number> implements Spare<K>, Serializable {
    @Nullable
    @Override
    public final Boolean getFlag() {
        return null;
    }

    @Nullable
    @Override
    public final Builder<K> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }
}
