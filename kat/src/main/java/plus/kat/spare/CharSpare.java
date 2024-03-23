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
 * @since 0.0.1
 */
public class CharSpare extends BaseSpare<Character> {

    static final Character ZERO = '\0';

    public static final CharSpare
        INSTANCE = new CharSpare();

    public CharSpare() {
        super(Character.class);
    }

    @Override
    public Character apply() {
        return ZERO;
    }

    @Override
    public String getSpace() {
        return "Char";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public Character read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        int size = value.size();
        if (size == 0) {
            return null;
        }

        byte[] flow = value.flow();
        if (size == 1) {
            return (char) (
                flow[0] & 0xFF
            );
        }

        stage:
        {
            // 110xxxxx 10xxxxxx
            if (size == 2) {
                int b1 = flow[0],
                    b2 = flow[1];
                if ((b1 >> 5) != -2 ||
                    (b2 >> 6) != -2) {
                    break stage;
                }

                return (char) (
                    b1 << 6 & 0xFC0 | b2 & 0x3F
                );
            }

            // 1110xxxx 10xxxxxx 10xxxxxx
            if (size == 3) {
                int b1 = flow[0],
                    b2 = flow[1],
                    b3 = flow[2];
                if ((b1 >> 4) != -2 ||
                    (b2 >> 6) != -2 ||
                    (b3 >> 6) != -2) {
                    break stage;
                }

                return (char) (
                    b1 << 12 & 0xF000 |
                        b2 << 6 & 0xFC0 | b3 & 0x3F
                );
            }

            if (value.isNothing()) {
                return null;
            }
        }

        throw new IllegalArgumentException(
            "Failed to convert the value to Character, " +
                "where this value is literally `" + value + '`'
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            (Character) value
        );
    }
}
