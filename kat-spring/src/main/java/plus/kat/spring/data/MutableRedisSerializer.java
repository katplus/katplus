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
package plus.kat.spring.data;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import org.springframework.util.Assert;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import plus.kat.*;
import plus.kat.lang.*;
import plus.kat.spare.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.3
 */
@lombok.Getter
@lombok.Setter
public class MutableRedisSerializer<T> implements RedisSerializer<T> {

    protected long readFlags;
    protected long writeFlags;

    protected final Algo algo;
    protected final Class<T> type;
    protected final Supplier supplier;

    /**
     * @param algo the specified algo
     * @param type the specified type
     * @since 0.0.3
     */
    public MutableRedisSerializer(
        @NonNull Algo algo,
        @NonNull Class<T> type
    ) {
        this(
            algo, type, Supplier.ins()
        );
    }

    /**
     * @param algo the specified algo
     * @param type the specified type
     * @since 0.0.6
     */
    public MutableRedisSerializer(
        @NonNull String algo,
        @NonNull Class<T> type
    ) {
        this(
            algo, type, Supplier.ins()
        );
    }

    /**
     * @param algo     the specified algo
     * @param type     the specified type
     * @param supplier the specified supplier
     * @since 0.0.6
     */
    public MutableRedisSerializer(
        @NonNull String algo,
        @NonNull Class<T> type,
        @NonNull Supplier supplier
    ) {
        this(
            Algo.of(algo), type, supplier
        );
    }

    /**
     * @param algo     the specified algo
     * @param type     the specified type
     * @param supplier the specified supplier
     * @since 0.0.6
     */
    public MutableRedisSerializer(
        @NonNull Algo algo,
        @NonNull Class<T> type,
        @NonNull Supplier supplier
    ) {
        super();
        Assert.notNull(algo, "Algo must not be null");
        Assert.notNull(type, "Class must not be null");
        Assert.notNull(supplier, "Supplier must not be null");

        this.algo = algo;
        this.type = type;
        this.supplier = supplier;
    }

    @Override
    public byte[] serialize(
        @Nullable T data
    ) throws SerializationException {
        if (data == null) {
            return Uniform.EMPTY_BYTES;
        }
        try (Chan chan = supplier.telex(
            algo, data, writeFlags)) {
            return chan.toBinary();
        } catch (IOException e) {
            throw new SerializationException(
                "Failed to serialize " + algo, e
            );
        }
    }

    @Override
    public T deserialize(
        @Nullable byte[] data
    ) throws SerializationException {
        if (data == null ||
            data.length == 0) {
            return null;
        }

        try {
            return supplier.solve(
                algo, type, Flow.of(data).and(readFlags)
            );
        } catch (IOException e) {
            throw new SerializationException(
                "Failed to deserialize " + algo, e
            );
        }
    }
}
