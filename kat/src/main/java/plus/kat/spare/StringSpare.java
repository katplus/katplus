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
import java.nio.charset.Charset;

import static plus.kat.stream.Toolkit.*;

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
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return apply();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof byte[]) {
                    return new String(
                        (byte[]) arg
                    );
                }
                if (arg instanceof char[]) {
                    return new String(
                        (char[]) arg
                    );
                }
                if (arg instanceof String) {
                    return (String) arg;
                }
                if (arg instanceof StringBuffer) {
                    return new String(
                        (StringBuffer) arg
                    );
                }
                if (arg instanceof StringBuilder) {
                    return new String(
                        (StringBuilder) arg
                    );
                }
                break;
            }
            case 2: {
                Object arg0 = args[0];
                if (arg0 instanceof byte[]) {
                    Object arg1 = args[1];
                    if (arg1 instanceof String) {
                        arg1 = Charset.forName(
                            (String) arg1
                        );
                    }
                    if (arg1 instanceof Charset) {
                        return new String(
                            (byte[]) arg0, (Charset) arg1
                        );
                    }
                }
                break;
            }
            case 3: {
                Object arg1 = args[1];
                Object arg2 = args[2];
                if (arg1 instanceof Integer &&
                    arg2 instanceof Integer) {
                    Object arg0 = args[0];
                    if (arg0 instanceof byte[]) {
                        return new String(
                            (byte[]) arg0, (int) arg1, (int) arg2
                        );
                    }
                    if (arg0 instanceof char[]) {
                        return new String(
                            (char[]) arg0, (int) arg1, (int) arg2
                        );
                    }
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
    public String read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return string(value);
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
