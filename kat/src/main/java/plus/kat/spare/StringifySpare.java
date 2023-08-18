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
 * @since 0.0.6
 */
@SuppressWarnings("unchecked")
public class StringifySpare extends BaseSpare<Object> {

    public static final StringifySpare
        INSTANCE = new StringifySpare();

    final int type;

    public StringifySpare() {
        this(
            CharSequence.class
        );
    }

    public StringifySpare(
        @NotNull Class<?> klass
    ) {
        super(
            (Class<Object>) klass
        );
        if (klass == String.class ||
            klass == CharSequence.class) {
            type = 0;
        } else if (klass == StringBuffer.class) {
            type = 1;
        } else if (klass == StringBuilder.class) {
            type = 2;
        } else {
            throw new IllegalStateException(
                "Received unsupported: " + klass.getName()
            );
        }
    }

    @Override
    public Object apply() {
        switch (type) {
            case 1: {
                return new StringBuffer();
            }
            case 2: {
                return new StringBuilder();
            }
        }
        return null;
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
    public Object read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        String text = string(value);
        if (text != null) {
            switch (type) {
                case 1: {
                    return new StringBuffer(text);
                }
                case 2: {
                    return new StringBuilder(text);
                }
            }
        }
        return text;
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
