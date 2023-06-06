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

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class StringSpare extends BaseSpare<String> {

    public static final StringSpare
        INSTANCE = new StringSpare();

    public StringSpare() {
        super(String.class);
    }

    @Override
    public String apply() {
        return null;
    }

    @Override
    public String apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == String.class) {
            return null;
        }

        throw new IllegalStateException(
            "Failed to build this " + type
        );
    }

    @Override
    public String getSpace() {
        return "String";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public String read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.toString();
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            (String) value
        );
    }
}
