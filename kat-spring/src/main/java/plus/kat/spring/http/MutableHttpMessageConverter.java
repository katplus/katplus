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

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import org.springframework.http.MediaType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.*;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static plus.kat.Plan.DEF;
import static plus.kat.chain.Chain.Unsafe.value;
import static org.springframework.core.GenericTypeResolver.resolveType;

/**
 * @author kraity
 * @since 0.0.2
 */
public class MutableHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    protected final Algo algo;
    protected Supplier supplier;

    protected Plan plan = DEF;
    protected MediaType[] mediaTypes;

    /**
     * @param algo the specified algo
     * @since 0.0.2
     */
    public MutableHttpMessageConverter(
        @NonNull Algo algo
    ) {
        this(algo, Supplier.ins());
    }

    /**
     * @param algo     the specified algo
     * @param supplier the specified supplier
     * @since 0.0.2
     */
    public MutableHttpMessageConverter(
        @NonNull Algo algo,
        @NonNull Supplier supplier
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
                    "At present, not found the media-types of " + algo
                );
            }
        }
    }

    @Override
    protected boolean canRead(
        @Nullable MediaType mediaType
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
        @NonNull Type type,
        @Nullable Class<?> cxt,
        @NonNull HttpInputMessage message
    ) throws IOException, HttpMessageNotReadableException {
        if (type instanceof Class) {
            return readInternal(
                (Class<?>) type, message
            );
        }

        Charset charset = null;
        MediaType mediaType = message
            .getHeaders().getContentType();
        if (mediaType != null) {
            charset = mediaType.getCharset();
        }

        Type visa = resolveType(type, cxt);
        return supplier.solve(
            visa, algo, new Event<>(message.getBody(), charset).with(plan)
        );
    }

    @Override
    protected Object readInternal(
        @NonNull Class<?> clazz,
        @NonNull HttpInputMessage message
    ) throws IOException, HttpMessageNotReadableException {
        Charset charset = null;
        MediaType mediaType = message
            .getHeaders().getContentType();
        if (mediaType != null) {
            charset = mediaType.getCharset();
        }

        return supplier.solve(
            clazz, algo, new Event<>(message.getBody(), charset).with(plan)
        );
    }

    @Override
    protected boolean canWrite(
        @Nullable MediaType mediaType
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
        @Nullable Object data,
        @Nullable Type type,
        @NonNull HttpOutputMessage message
    ) throws IOException, HttpMessageNotWritableException {
        byte[] stream;
        try (Chan chan = supplier.telex(algo, plan)) {
            if (chan.set(null, data)) {
                Flow flow = chan.getFlow();
                if (flow instanceof Chain) {
                    Chain chain = (Chain) flow;
                    OutputStream out = message.getBody();
                    out.write(
                        value(chain), 0, chain.length()
                    );
                    out.flush();
                    return;
                } else {
                    stream = chan.toBytes();
                }
            } else {
                throw new HttpMessageNotWritableException(
                    "Failed to serialize " + data + " to " + algo
                );
            }
        }
        StreamUtils.copy(
            stream, message.getBody()
        );
    }

    /**
     * @since 0.0.3
     */
    public void setPlan(
        @NonNull Plan target
    ) {
        Assert.notNull(target, "Plan must not be null");
        plan = target;
    }

    /**
     * @since 0.0.3
     */
    @NonNull
    public Plan getPlan() {
        return plan;
    }

    /**
     * @since 0.0.5
     */
    public void setSupportedMediaTypes(
        @NonNull MediaType[] types
    ) {
        mediaTypes = types.clone();
    }

    /**
     * @since 0.0.3
     */
    @Override
    public void setSupportedMediaTypes(
        @NonNull List<MediaType> types
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
