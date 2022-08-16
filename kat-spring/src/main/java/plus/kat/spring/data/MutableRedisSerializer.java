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

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

import plus.kat.*;
import plus.kat.kernel.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.3
 */
public class MutableRedisSerializer<T> implements RedisSerializer<T> {

    protected final Job job;
    protected Supplier supplier;

    protected Plan plan = Plan.INS;
    protected final Class<T> type;

    /**
     * @param job  the specified job
     * @param type the specified type
     * @since 0.0.3
     */
    public MutableRedisSerializer(
        Job job, Class<T> type
    ) {
        this(job, type, Supplier.ins());
    }

    /**
     * @param job      the specified job
     * @param type     the specified type
     * @param supplier the specified supplier
     * @since 0.0.3
     */
    public MutableRedisSerializer(
        Job job,
        Class<T> type,
        Supplier supplier
    ) {
        super();
        Assert.notNull(job, "Job must not be null");
        Assert.notNull(type, "Class must not be null");
        Assert.notNull(supplier, "Supplier must not be null");

        this.job = job;
        this.type = type;
        this.supplier = supplier;
    }

    @Override
    public byte[] serialize(
        T data
    ) throws SerializationException {
        if (data == null) {
            return Chain.EMPTY_BYTES;
        }
        Chan chan;
        switch (job) {
            case KAT: {
                chan = new Chan(
                    plan.getWriteFlags(), supplier
                );
                break;
            }
            case DOC: {
                chan = new Doc(
                    plan.getWriteFlags(), supplier
                );
                break;
            }
            case JSON: {
                chan = new Json(
                    plan.getWriteFlags(), supplier
                );
                break;
            }
            default: {
                throw new SerializationException(
                    "Unexpectedly, Converter did not find " + job + "'s Chan"
                );
            }
        }

        try {
            if (chan.set(null, data)) {
                return chan.toBytes();
            } else {
                chan.closeFlow();
                throw new SerializationException(
                    "Unexpectedly, Cannot serialize " + data + " to " + job
                );
            }
        } catch (IOException e) {
            throw new SerializationException(
                "Unexpectedly, Cannot serialize " + data + " to " + job, e
            );
        }
    }

    @Override
    public T deserialize(
        byte[] data
    ) throws SerializationException {
        if (data == null ||
            data.length == 0) {
            return null;
        }

        return supplier.solve(
            type, job, new Event<T>(
                data
            ).with(
                plan.getReadFlags()
            )
        );
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
}
