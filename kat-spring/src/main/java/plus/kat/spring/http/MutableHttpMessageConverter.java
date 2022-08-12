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
import plus.kat.chain.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * @author kraity
 * @since 0.0.2
 */
public class MutableHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    protected final Job job;
    protected Supplier supplier;

    protected long readFlags;
    protected long writeFlags;

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
        Assert.notNull(job, "Job must not be null");
        Assert.notNull(supplier, "Supplier must not be null");

        this.supplier = supplier;
        switch (this.job = job) {
            case KAT: {
                mediaTypes = new MediaType[]{
                    MediaTypes.TEXT_KAT,
                    MediaTypes.APPLICATION_KAT
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
            ).with(
                readFlags
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
            ).with(
                readFlags
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
                chan = new Chan(
                    writeFlags, supplier
                );
                break;
            }
            case DOC: {
                chan = new Doc(
                    writeFlags, supplier
                );
                break;
            }
            case JSON: {
                chan = new Json(
                    writeFlags, supplier
                );
                break;
            }
            default: {
                throw new HttpMessageNotWritableException(
                    "Unexpectedly, Converter did not find " + job + "'s Chan"
                );
            }
        }

        chan.set(
            null, data
        );

        Paper flow = chan.getFlow();
        flow.update(
            output.getBody()
        );
        chan.closeFlow();
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

    /**
     * add the {@code flag} on the basis of {@code readFlags}
     *
     * @param flag the specified {@code flag}
     * @since 0.0.3
     */
    public void readFlag(
        long flag
    ) {
        readFlags |= flag;
    }

    /**
     * Overwrite {@code readFlags} over the specified {@code flags}
     *
     * @param flags the specified {@code flags}
     * @since 0.0.3
     */
    public void setReadFlags(
        long flags
    ) {
        readFlags = flags;
    }

    /**
     * Returns the {@code ReadFlags} of {@link MutableHttpMessageConverter}
     *
     * @since 0.0.3
     */
    public long getReadFlags() {
        return readFlags;
    }

    /**
     * add the {@code flag} on the basis of {@code writeFlags}
     *
     * @param flag the specified {@code flag}
     * @since 0.0.3
     */
    public void writeFlag(
        long flag
    ) {
        writeFlags |= flag;
    }

    /**
     * Overwrite {@code writeFlags} over the specified {@code flags}
     *
     * @param flags the specified {@code flags}
     * @since 0.0.3
     */
    public void setWriteFlags(
        long flags
    ) {
        writeFlags = flags;
    }

    /**
     * Returns the {@code WriteFlags} of {@link MutableHttpMessageConverter}
     *
     * @since 0.0.3
     */
    public long getWriteFlags() {
        return writeFlags;
    }
}
