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

import static plus.kat.lang.Binary.*;

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
        int l = value.size();
        if (l == 0) {
            return "";
        }

        byte w = value.flag();
        byte[] v = value.flow();

        if (l == 4 && w == 0
            && v[0] == 'n'
            && v[1] == 'u'
            && v[2] == 'l'
            && v[3] == 'l') {
            return null;
        }

        return new String(v, 0, l, UTF_8);
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
