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

import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.3
 */
public class StringBufferSpare extends BaseSpare<StringBuffer> {

    public static final StringBufferSpare
        INSTANCE = new StringBufferSpare();

    public StringBufferSpare() {
        super(StringBuffer.class);
    }

    @Override
    public StringBuffer apply() {
        return new StringBuffer();
    }

    @Override
    public StringBuffer apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return new StringBuffer();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof String) {
                    return new StringBuffer(
                        (String) arg
                    );
                }
                if (arg instanceof CharSequence) {
                    return new StringBuffer(
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
    public StringBuffer read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        String text = string(value);
        if (text == null) {
            return null;
        }
        return new StringBuffer(text);
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
