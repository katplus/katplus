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

/**
 * @author kraity
 * @since 0.0.3
 */
public class StringBuilderSpare extends BaseSpare<StringBuilder> {

    public static final StringBuilderSpare
        INSTANCE = new StringBuilderSpare();

    public StringBuilderSpare() {
        super(StringBuilder.class);
    }

    @Override
    public StringBuilder apply() {
        return new StringBuilder();
    }

    @Override
    public StringBuilder apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return new StringBuilder();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof String) {
                    return new StringBuilder(
                        (String) arg
                    );
                }
                if (arg instanceof CharSequence) {
                    return new StringBuilder(
                        (CharSequence) arg
                    );
                }
            }
        }

        throw new IllegalStateException(
            "No matching constructor found"
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
    public StringBuilder read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return new StringBuilder(
            value.toString()
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            (CharSequence) value
        );
    }
}
