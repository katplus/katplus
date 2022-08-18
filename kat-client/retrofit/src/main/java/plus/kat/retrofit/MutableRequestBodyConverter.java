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
import plus.kat.okhttp.*;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.3
 */
public class MutableRequestBodyConverter<T> implements Converter<T, RequestBody> {

    protected final Job job;
    protected final Type type;

    protected Plan plan;
    protected Supplier supplier;

    public MutableRequestBodyConverter(
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
    public RequestBody convert(
        T value
    ) throws IOException {
        Chan chan;
        MediaType media;
        switch (job) {
            case KAT: {
                media = MediaTypes.APPLICATION_KAT;
                chan = new Chan(
                    plan, supplier
                );
                break;
            }
            case DOC: {
                media = MediaTypes.APPLICATION_DOC;
                chan = new Doc(
                    plan, supplier
                );
                break;
            }
            case JSON: {
                media = MediaTypes.APPLICATION_JSON;
                chan = new Json(
                    plan, supplier
                );
                break;
            }
            default: {
                throw new IOException(
                    "Unexpectedly, Converter did not find " + job + "'s Chan"
                );
            }
        }

        if (chan.set(null, value)) {
            return new RequestPaper(
                chan.getFlow(), media
            );
        } else {
            chan.closeFlow();
            throw new IOException(
                "Unexpectedly, Cannot serialize " + value + " to " + job
            );
        }
    }
}
