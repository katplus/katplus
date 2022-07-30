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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class ByteArrayCoder implements Coder<byte[]> {

    public static final ByteArrayCoder
        INSTANCE = new ByteArrayCoder();

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$s;
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return null;
    }

    @Nullable
    @Override
    public Builder<byte[]> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @Nullable
    @Override
    public byte[] read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        return value.copyBytes();
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOCrash {
        flow.emit(
            (byte[]) value
        );
    }
}
