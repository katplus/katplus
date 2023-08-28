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
import plus.kat.chain.*;

import java.io.*;
import java.lang.reflect.Type;

import static plus.kat.spare.Parser.*;
import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public abstract class BeanSpare<T> implements Spare<T> {

    protected final Class<T> klass;
    protected final Context context;

    protected BeanSpare(
        @NotNull Class<T> klass
    ) {
        this(
            klass, Supplier.ins()
        );
    }

    protected BeanSpare(
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
        return Boolean.TRUE;
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.BRACE;
    }

    @Override
    public Class<T> getType() {
        return klass;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public T read(
        @NotNull Flag flag,
        @NotNull Value data
    ) throws IOException {
        if (data.isNothing()) {
            return null;
        }

        if (flag.isFlag(Flag.VALUE_AS_BEAN)) {
            Algo algo = algoOf(data);
            if (algo == null) {
                return null;
            }
            try (Parser op = with(this)) {
                return op.solve(
                    algo, Flow.of(data)
                );
            }
        }

        throw new IOException(
            "Failed to parse the object to `" + klass
                + "` unless `Flag.VALUE_AS_BEAN` is enabled"
        );
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        // Default to nothing,
        // waiting for subclasses to implement
    }
}
