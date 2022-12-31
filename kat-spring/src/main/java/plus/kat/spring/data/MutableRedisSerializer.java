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
import plus.kat.chain.*;
import plus.kat.crash.*;

import java.io.IOException;

import static plus.kat.Plan.DEF;

/**
 * @author kraity
 * @since 0.0.3
 */
public class MutableRedisSerializer<T> implements RedisSerializer<T> {

    protected final Algo algo;
    protected Supplier supplier;

    protected Plan plan = DEF;
    protected final Class<T> type;

    /**
     * @param algo the specified algo
     * @param type the specified type
     * @since 0.0.3
     */
    public MutableRedisSerializer(
        @NonNull Algo algo,
        @NonNull Class<T> type
    ) {
        this(algo, type, Supplier.ins());
    }

    /**
     * @param algo     the specified algo
     * @param type     the specified type
     * @param supplier the specified supplier
     * @since 0.0.3
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
            return Chain.EMPTY_BYTES;
        }
        try (Chan chan = supplier.telex(algo, plan)) {
            if (chan.set(null, data)) {
                return chan.toBytes();
            }
        } catch (Collapse e) {
            throw new SerializationException(
                "Not found the Chan of " + algo, e
            );
        } catch (IOException e) {
            throw new SerializationException(
                "Failed to serialize " + algo, e
            );
        }

        throw new SerializationException(
            "Cannot serialize " + data + " to " + algo.name()
        );
    }

    @Override
    public T deserialize(
        @Nullable byte[] data
    ) throws SerializationException {
        if (data == null ||
            data.length == 0) {
            return null;
        }

        return supplier.solve(
            type, algo, new Event<T>(data).with(plan)
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
}
