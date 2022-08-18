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

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.3
 */
public class MutableConverterFactory extends Converter.Factory {

    protected final Job job;
    protected Supplier supplier;
    protected Plan plan = Plan.DEF;

    /**
     * @param job      the specified job
     * @param supplier the specified supplier
     * @since 0.0.3
     */
    public MutableConverterFactory(
        Job job,
        Supplier supplier
    ) {
        super();
        assert job != null : "Job must not be null";
        assert supplier != null : "Supplier must not be null";

        this.job = job;
        this.supplier = supplier;
    }

    public static MutableConverterFactory create() {
        return create(Job.JSON, Supplier.ins());
    }

    public static MutableConverterFactory create(
        Job job, Supplier supplier
    ) {
        return new MutableConverterFactory(job, supplier);
    }

    @Override
    public Converter<ResponseBody, Object> responseBodyConverter(
        Type type,
        Annotation[] annotations,
        Retrofit retrofit
    ) {
        return new MutableResponseBodyConverter<>(
            type, job, plan, supplier
        );
    }

    @Override
    public Converter<Object, RequestBody> requestBodyConverter(
        Type type,
        Annotation[] parameterAnnotations,
        Annotation[] methodAnnotations,
        Retrofit retrofit
    ) {
        return new MutableRequestBodyConverter<>(
            type, job, plan, supplier
        );
    }
}
