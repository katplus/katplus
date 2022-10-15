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
package plus.kat.spring.http;

import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.util.Assert;

import plus.kat.*;
import plus.kat.crash.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * @author kraity
 * @since 0.0.2
 */
public class MutableHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    protected final Algo algo;
    protected Supplier supplier;

    protected Plan plan = Plan.DEF;
    protected MediaType[] mediaTypes;

    /**
     * @param algo the specified algo
     * @since 0.0.2
     */
    public MutableHttpMessageConverter(
        Algo algo
    ) {
        this(algo, Supplier.ins());
    }

    /**
     * @param algo     the specified algo
     * @param supplier the specified supplier
     * @since 0.0.2
     */
    public MutableHttpMessageConverter(
        Algo algo,
        Supplier supplier
    ) {
        super();
        Assert.notNull(algo, "Algo must not be null");
        Assert.notNull(supplier, "Supplier must not be null");

        this.algo = algo;
        this.supplier = supplier;
        switch (algo.name()) {
            case "kat": {
                mediaTypes = new MediaType[]{
                    MediaTypes.TEXT_KAT,
                    MediaTypes.APPLICATION_KAT
                };
                break;
            }
            case "xml": {
                mediaTypes = new MediaType[]{
                    MediaType.TEXT_XML,
                    MediaType.APPLICATION_XML
                };
                break;
            }
            case "json": {
                mediaTypes = new MediaType[]{
                    MediaType.APPLICATION_JSON
                };
                break;
            }
            default: {
                throw new HttpMessageNotWritableException(
                    "Unexpectedly, Converter did not find " + algo
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
            clazz, algo, new Event<>(in.getBody()).with(plan)
        );
    }

    @Override
    protected Object readInternal(
        Class<?> clazz,
        HttpInputMessage in
    ) throws IOException, HttpMessageNotReadableException {
        return supplier.solve(
            clazz, algo, new Event<>(in.getBody()).with(plan)
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
        try (Chan chan = supplier.telex(algo, plan)) {
            if (chan.set(null, data)) {
                chan.getSteam().update(
                    output.getBody()
                );
            } else {
                throw new HttpMessageNotWritableException(
                    "Unexpectedly, Cannot serialize "
                        + data + " to " + algo.name()
                );
            }
        } catch (Collapse collapse) {
            throw new HttpMessageNotWritableException(
                collapse.getMessage(), collapse
            );
        }
    }

    /**
     * @since 0.0.3
     */
    public void setPlan(
        Plan target
    ) {
        Assert.notNull(target, "Plan must not be null");
        plan = target;
    }

    /**
     * @since 0.0.3
     */
    public Plan getPlan() {
        return plan;
    }

    /**
     * @since 0.0.3
     */
    @Override
    public void setSupportedMediaTypes(
        List<MediaType> types
    ) {
        mediaTypes = types.toArray(
            new MediaType[types.size()]
        );
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Arrays.asList(mediaTypes);
    }
}
