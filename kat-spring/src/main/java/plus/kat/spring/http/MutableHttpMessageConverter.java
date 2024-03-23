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
import plus.kat.lang.*;
import plus.kat.spare.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static plus.kat.lang.Uniform.*;
import static org.springframework.core.GenericTypeResolver.resolveType;

/**
 * @author kraity
 * @since 0.0.2
 */
@lombok.Getter
@lombok.Setter
public class MutableHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    protected long readFlags;
    protected long writeFlags;

    protected final Algo algo;
    protected final Supplier supplier;

    protected MediaType[] mediaTypes;
    public static final MediaType
        TEXT_KAT = new MediaType("text", "kat"),
        APPLICATION_KAT = new MediaType("application", "kat");

    /**
     * @param algo the specified algo
     * @since 0.0.2
     */
    public MutableHttpMessageConverter(
        @NonNull Algo algo
    ) {
        this(
            algo, Supplier.ins()
        );
    }

    /**
     * @param algo the specified algo
     * @since 0.0.6
     */
    public MutableHttpMessageConverter(
        @NonNull String algo
    ) {
        this(
            algo, Supplier.ins()
        );
    }

    /**
     * @param algo     the specified algo
     * @param supplier the specified supplier
     * @since 0.0.6
     */
    public MutableHttpMessageConverter(
        @NonNull String algo,
        @NonNull Supplier supplier
    ) {
        this(
            Algo.of(algo), supplier
        );
    }

    /**
     * @param algo     the specified algo
     * @param supplier the specified supplier
     * @since 0.0.6
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
                    TEXT_KAT,
                    APPLICATION_KAT
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
                    "Not found the media-types of " + algo
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
        MediaType mediaType = message
            .getHeaders().getContentType();

        Charset charset = null;
        if (mediaType != null) {
            charset = mediaType.getCharset();
        }

        if (!(type instanceof Class)) {
            type = resolveType(type, cxt);
        }

        return supplier.solve(
            algo, type, Flow.of(message.getBody(), charset).and(readFlags)
        );
    }

    @Override
    protected Object readInternal(
        @NonNull Class<?> clazz,
        @NonNull HttpInputMessage message
    ) throws IOException, HttpMessageNotReadableException {
        MediaType mediaType = message
            .getHeaders().getContentType();

        Charset charset = null;
        if (mediaType != null) {
            charset = mediaType.getCharset();
        }

        return supplier.solve(
            algo, clazz, Flow.of(message.getBody(), charset).and(readFlags)
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
        try (Chan chan = supplier.telex(algo, writeFlags)) {
            if (chan.set(null, data)) {
                Flux flux = chan.getFlux();
                if (flux instanceof Binary) {
                    Binary src = (Binary) flux;
                    OutputStream flow = message.getBody();
                    flow.write(
                        valueOf(src), 0, src.size()
                    );
                    flow.flush();
                    return;
                } else {
                    stream = chan.toBinary();
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
    @Override
    public void setSupportedMediaTypes(
        @NonNull List<MediaType> types
    ) {
        mediaTypes = types.toArray(
            new MediaType[0]
        );
    }

    /**
     * @since 0.0.2
     */
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Arrays.asList(mediaTypes);
    }
}
