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
package plus.kat.retrofit;

import plus.kat.*;
import plus.kat.spare.*;
import plus.kat.okhttp.*;

import okhttp3.RequestBody;
import retrofit2.Converter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.3
 */
public class MutableRequestBodyConverter<T> implements Converter<T, RequestBody> {

    protected final Algo algo;
    protected final Type type;

    protected final long flags;
    protected final Supplier supplier;

    public MutableRequestBodyConverter(
        Type type,
        Algo algo,
        long flags,
        Supplier supplier
    ) {
        this.type = type;
        this.algo = algo;
        this.flags = flags;
        this.supplier = supplier;
    }

    @Override
    public RequestBody convert(
        T value
    ) throws IOException {
        try (Chan chan = supplier.telex(
            algo, value, flags)) {
            return new RequestStream(
                chan, MediaTypes.of(algo)
            );
        }
    }
}
