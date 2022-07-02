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
package plus.kat.spring;

import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;

import plus.kat.*;
import plus.kat.chain.Paper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * @author kraity
 * @since 0.0.2
 */
public class MutableHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    protected Job job;
    protected Supplier supplier;
    protected MediaType[] mediaTypes;

    /**
     * @param job the specified job
     * @since 0.0.2
     */
    public MutableHttpMessageConverter(
        Job job
    ) {
        this(job, Supplier.ins());
    }

    /**
     * @param job      the specified job
     * @param supplier the specified supplier
     * @since 0.0.2
     */
    public MutableHttpMessageConverter(
        Job job,
        Supplier supplier
    ) {
        super();
        this.supplier = supplier;
        switch (this.job = job) {
            case KAT: {
                mediaTypes = new MediaType[]{
                    new MediaType("text", "kat"),
                    new MediaType("application", "kat")
                };
                break;
            }
            case DOC: {
                mediaTypes = new MediaType[]{
                    MediaType.TEXT_XML,
                    MediaType.APPLICATION_XML
                };
                break;
            }
            case JSON: {
                mediaTypes = new MediaType[]{
                    MediaType.APPLICATION_JSON
                };
                break;
            }
            default: {
                throw new HttpMessageNotWritableException(
                    "Unexpectedly, Converter did not find " + job
                );
            }
        }
    }

    @Override
    protected boolean canRead(
        MediaType mediaType
    ) {
        if (mediaType == null) {
            return false;
        }

        for (MediaType m : mediaTypes) {
            if (m.includes(mediaType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object read(
        Type type,
        Class<?> cxt,
        HttpInputMessage in
    ) throws IOException, HttpMessageNotReadableException {
        if (type instanceof Class) {
            return readInternal(
                (Class<?>) type, in
            );
        }

        Type clazz = GenericTypeResolver
            .resolveType(
                type, cxt
            );

        return supplier.solve(
            clazz, job, new Event<>(
                in.getBody()
            )
        );
    }

    @Override
    protected Object readInternal(
        Class<?> clazz,
        HttpInputMessage in
    ) throws IOException, HttpMessageNotReadableException {
        return supplier.solve(
            clazz, job, new Event<>(
                in.getBody()
            )
        );
    }

    @Override
    protected boolean canWrite(
        MediaType mediaType
    ) {
        if (mediaType == null) {
            return false;
        }

        for (MediaType m : mediaTypes) {
            if (m.isCompatibleWith(mediaType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void writeInternal(
        Object data,
        Type type,
        HttpOutputMessage output
    ) throws IOException, HttpMessageNotWritableException {
        Chan chan;
        switch (job) {
            case KAT: {
                chan = new Chan(supplier);
                break;
            }
            case DOC: {
                chan = new Doc(supplier);
                break;
            }
            case JSON: {
                chan = new Json(supplier);
                break;
            }
            default: {
                throw new HttpMessageNotWritableException(
                    "Unexpectedly, Converter did not find " + job + "'s Chan"
                );
            }
        }

        chan.set(null, data);
        Paper flow = chan.getFlow();

        output.getBody().write(
            flow.getSource(), 0, flow.length()
        );
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Arrays.asList(mediaTypes);
    }
}
