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
import java.util.Collections;

/**
 * @author kraity
 * @since 0.0.2
 */
public class MutableHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    protected Job job;
    protected Supplier supplier;

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
                setSupportedMediaTypes(
                    Arrays.asList(
                        new MediaType("text", "kat"),
                        new MediaType("application", "kat")
                    )
                );
                break;
            }
            case DOC: {
                setSupportedMediaTypes(
                    Arrays.asList(
                        MediaType.TEXT_XML,
                        MediaType.APPLICATION_XML
                    )
                );
                break;
            }
            case JSON: {
                setSupportedMediaTypes(
                    Collections.singletonList(
                        MediaType.APPLICATION_JSON
                    )
                );
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
}
