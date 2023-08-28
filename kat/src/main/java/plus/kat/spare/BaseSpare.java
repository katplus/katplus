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

import plus.kat.*;
import plus.kat.actor.*;

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.3
 */
public abstract class BaseSpare<T> implements Spare<T> {

    protected final Class<T> klass;
    protected final Context context;

    protected BaseSpare(
        @NotNull Class<T> klass
    ) {
        this(
            klass, Supplier.ins()
        );
    }

    protected BaseSpare(
        @NotNull Class<T> klass,
        @NotNull Context context
    ) {
        if (klass != null && context != null) {
            this.klass = klass;
            this.context = context;
        } else {
            throw new NullPointerException(
                "Received: (" + klass + ", " + context + ")"
            );
        }
    }

    @Override
    public String getSpace() {
        return klass.getName();
    }

    @Override
    public Boolean getScope() {
        return null;
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return null;
    }

    @Override
    public Factory getFactory(
        @Nullable Type type
    ) {
        throw new IllegalStateException(
            "Failed to parse the entity to `" + klass
                + "` because parsing structure is not supported"
        );
    }

    @Override
    public Class<T> getType() {
        return klass;
    }

    @Override
    public Context getContext() {
        return context;
    }
}
