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
import plus.kat.stream.*;

import java.io.IOException;

import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.6
 */
@SuppressWarnings("unchecked")
public class BinaryifySpare extends BaseSpare<Object> {

    public static final BinaryifySpare
        INSTANCE = new BinaryifySpare();

    final int type;

    public BinaryifySpare() {
        this(
            ByteSequence.class
        );
    }

    public BinaryifySpare(
        @NotNull Class<?> klass
    ) {
        super(
            (Class<Object>) klass
        );
        if (klass == Binary.class ||
            klass == ByteSequence.class) {
            type = 0;
        } else if (klass == Alias.class) {
            type = 1;
        } else if (klass == Space.class) {
            type = 2;
        } else if (klass == Value.class) {
            type = 3;
        } else {
            throw new IllegalStateException(
                "Received unsupported: " + klass.getName()
            );
        }
    }

    @Override
    public Object apply() {
        return null;
    }

    @Override
    public String getSpace() {
        return "Binary";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public Binary read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        byte[] flow = copyOf(value);
        if (flow == null) {
            return null;
        }
        switch (type) {
            case 1: {
                return new Alias(flow);
            }
            case 2: {
                return new Space(flow);
            }
            case 3: {
                return new Value(flow);
            }
        }
        return new Binary(flow);
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        if (value instanceof Binary) {
            flux.emit(
                (Binary) value
            );
        } else {
            flux.emit(
                (ByteSequence) value
            );
        }
    }
}
