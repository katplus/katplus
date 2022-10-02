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
package plus.kat.caller;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author kraity
 * @since 0.0.3
 */
public abstract class Caller extends Chain {

    protected Job job;
    protected Plan plan;
    protected Supplier supplier;

    /**
     * @param bucket the specified {@link Bucket} to be used
     */
    protected Caller(
        @Nullable Bucket bucket
    ) {
        super(bucket);
        this.plan = Plan.DEF;
        this.supplier = Supplier.ins();
    }

    /**
     * @param in the specified {@link InputStream}
     */
    protected void stream(
        @NotNull InputStream in
    ) {
        byte[] data;
        try {
            chain(in, 1024);
            data = toBytes();
        } catch (Exception e) {
            data = EMPTY_BYTES;
        } finally {
            try {
                close();
                in.close();
            } catch (Exception e) {
                // Nothing
            }
        }

        value = data;
        count = data.length;
    }

    /**
     * Returns a {@link Value} of this {@link Client}
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     */
    @Override
    public Value subSequence(
        int start, int end
    ) {
        return new Value(
            toBytes(start, end)
        );
    }

    /**
     * Use the specified {@link Job}
     *
     * @param target the specified job
     */
    public Caller with(
        @NotNull Job target
    ) {
        job = target;
        return this;
    }

    /**
     * Use the specified {@link Plan}
     *
     * @param target the specified plan
     * @throws NullPointerException If the target is null
     */
    public Caller with(
        @NotNull Plan target
    ) {
        if (target != null) {
            plan = target;
        } else {
            throw new NullPointerException(
                "Plan must not be null"
            );
        }
        return this;
    }

    /**
     * Use the specified {@link Supplier}
     *
     * @param target the specified supplier
     * @throws NullPointerException If the target is null
     */
    public Caller with(
        @NotNull Supplier target
    ) {
        if (target != null) {
            supplier = target;
        } else {
            throw new NullPointerException(
                "Supplier must not be null"
            );
        }
        return this;
    }

    /**
     * Parse this {@link Caller} and convert result to {@link T}
     *
     * @throws Collapse If no specified job
     */
    @Nullable
    public <E, T extends E> T to(
        @NotNull Class<E> klass
    ) {
        if (count == 0) {
            return null;
        }

        Job job = job();
        return supplier.solve(
            klass, job, new Event<T>(
                reader()
            ).with(plan)
        );
    }

    /**
     * Parse this {@link Caller} and convert result to {@link T}
     */
    @Nullable
    public <E, T extends E> T to(
        @NotNull Job job,
        @NotNull Class<E> klass
    ) {
        if (count == 0) {
            return null;
        }

        return supplier.solve(
            klass, job, new Event<T>(
                reader()
            ).with(plan)
        );
    }

    /**
     * Parse {@link Event} and convert result to {@link T}
     */
    @Nullable
    public <E, T extends E> T solve(
        @NotNull Class<E> klass,
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        if (count == 0) {
            return null;
        }

        return supplier.solve(
            klass, job, event.with(
                reader()
            ).with(plan)
        );
    }

    /**
     * Parse this {@link Caller} and convert result to {@code Array}
     *
     * @throws Collapse If no specified job
     * @since 0.0.4
     */
    @Nullable
    public Object[] toArray() {
        return to(
            Object[].class
        );
    }

    /**
     * Parse this {@link Caller} and convert result to {@link List}
     *
     * @throws Collapse If no specified job
     * @since 0.0.4
     */
    @Nullable
    public List<Object> toList() {
        return to(List.class);
    }

    /**
     * Parse this {@link Caller} and convert result to {@link Map}
     *
     * @throws Collapse If no specified job
     * @since 0.0.4
     */
    @Nullable
    public Map<String, Object> toMap() {
        return to(Map.class);
    }

    /**
     * Returns the specified {@link Job}
     *
     * @throws Collapse If no specified job
     */
    @NotNull
    public Job job() {
        Job j = job;
        if (j != null) {
            return j;
        }

        throw new Collapse(
            "Could not find the specified Job"
        );
    }

    /**
     * Returns the internal {@link Job}
     */
    public Job getJob() {
        return job;
    }

    /**
     * Sets the job of this Caller
     *
     * @param job the specified job
     * @since 0.0.4
     */
    public void setJob(
        @Nullable Job job
    ) {
        this.job = job;
    }

    /**
     * Returns the internal {@link Supplier}
     */
    public Supplier getSupplier() {
        return supplier;
    }
}
