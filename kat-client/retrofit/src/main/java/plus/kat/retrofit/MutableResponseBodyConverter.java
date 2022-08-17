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

import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.3
 */
public class MutableResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    protected final Job job;
    protected final Type type;

    protected Plan plan;
    protected Supplier supplier;

    public MutableResponseBodyConverter(
        Type type,
        Job job,
        Plan plan,
        Supplier supplier
    ) {
        this.type = type;
        this.job = job;
        this.plan = plan;
        this.supplier = supplier;
    }

    @Override
    public T convert(
        ResponseBody value
    ) throws IOException {
        return supplier.solve(
            type, job, new Event<T>(
                value.byteStream()
            ).with(
                plan.getReadFlags()
            )
        );
    }
}
