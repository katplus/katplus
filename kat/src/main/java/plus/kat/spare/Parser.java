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
package plus.kat.spare;

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.kernel.*;
import plus.kat.stream.*;
import plus.kat.utils.*;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Parser implements Proxy, Closeable {
    /**
     * state etc.
     */
    volatile boolean lock;

    /**
     * record etc.
     */
    protected Object bundle;
    protected int depth, range;

    /**
     * solver etc.
     */
    protected Radar radar;
    protected Solver doc, json;

    /**
     * snapshot etc.
     */
    protected Event<?> event;
    protected Builder<?> active;

    /**
     * default
     */
    public Parser() {
        radar = new Radar();
    }

    /**
     * @param b1 the specified {@link Bucket} of {@code Space}
     * @param b2 the specified {@link Bucket} of {@code Alias}
     * @param b3 the specified {@link Bucket} of {@code Value}
     */
    public Parser(
        @NotNull Bucket b1,
        @NotNull Bucket b2,
        @NotNull Bucket b3
    ) {
        radar = new Radar(
            b1, b2, b3
        );
    }

    /**
     * Parses the {@link Event} by using {@link Radar}
     *
     * @param event the specified {@code event} to be handled
     * @throws IOException          Unexpected errors by {@link Proxy} or {@link Reader}
     * @throws NullPointerException If the specified {@code event} is null
     */
    @NotNull
    public <T> T read(
        @NotNull Event<T> event
    ) throws IOException {
        return read(
            radar, event
        );
    }

    /**
     * Parses the {@link Event} by using specified {@link Solver}
     *
     * @param event the specified {@code event} to be handled
     * @throws Collapse             If a build error or parsing error occurs
     * @throws IOException          Unexpected errors by {@link Proxy} or {@link Reader}
     * @throws NullPointerException If the specified {@code coder} or {@code event} is null
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> T read(
        @NotNull Solver coder,
        @NotNull Event<T> event
    ) throws IOException {
        @Nullable
        Reader reader = event.getReader();
        if (reader == null) {
            throw new Collapse(
                "Reader is null"
            );
        }

        this.event = event;
        this.range = event.getRange();

        try {
            coder.read(
                this, reader
            );
        } finally {
            coder.clear();
            reader.close();

            Builder<?> k, t = active;
            active = null;
            for (; t != null; t = k) {
                k = t.getParent();
                t.onDetach();
            }
        }

        Object data = bundle;
        if (data != null) {
            return (T) data;
        }

        throw new UnexpectedCrash(
            "Unexpectedly, depth is " + depth
                + ", range is " + range + ", result is null"
        );
    }

    /**
     * Parses the {@link Event} with specified {@link Algo}
     *
     * @param event the specified event to be handled
     * @throws Collapse             If a build error or parsing error occurs
     * @throws IOException          Unexpected errors by {@link Proxy} or {@link Reader}
     * @throws NullPointerException If the specified {@code algo} or {@code event} is null
     */
    @NotNull
    public <T> T read(
        @NotNull Algo algo,
        @NotNull Event<T> event
    ) throws IOException {
        switch (algo.name()) {
            case "kat": {
                return read(
                    radar, event
                );
            }
            case "xml": {
                Solver it = doc;
                if (it == null) {
                    doc = it = radar.new DOC();
                }
                return read(
                    it, event
                );
            }
            case "json": {
                Solver it = json;
                if (it == null) {
                    json = it = radar.new JSON();
                }
                return read(
                    it, event
                );
            }
            default: {
                throw new Collapse(
                    "Unexpectedly, Parser did not find " + algo + "'s Solver"
                );
            }
        }
    }

    /**
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean attach(
        @NotNull Space space,
        @NotNull Alias alias
    ) throws IOException {
        if (depth >= range) {
            throw new UnexpectedCrash(
                "Parse depth out of range"
            );
        }

        Alias name = alias.copy();
        Builder<?> child, parent = active;

        if (depth != 0) {
            child = active.onReport(
                space, name
            );
        } else {
            Spare<?> spare;
            spare = event.assign(
                space, name
            );

            child = spare.getBuilder(
                event.getType()
            );
        }

        if (child == null) {
            return false;
        }

        try {
            child.onAttach(
                name, event, parent
            );
            ++depth;
            active = child;
            return true;
        } catch (Collapse e) {
            return false;
        }
    }

    /**
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void accept(
        @NotNull Space space,
        @NotNull Alias alias,
        @NotNull Value value
    ) throws IOException {
        if (depth != 0) {
            active.onReport(
                space, alias, value
            );
        } else {
            Spare<?> spare;
            spare = event.accept(
                space, alias
            );

            value.setType(
                event.getType()
            );
            bundle = spare.read(
                event, value
            );
        }
    }

    /**
     * @throws IOException If an I/O error occurs
     */
    @Override
    public boolean detach()
        throws IOException {
        if (--depth < 0) {
            throw new UnexpectedCrash(
                "Parse depth out of range"
            );
        }

        Builder<?> child = active;
        active = child.getParent();

        try {
            if (depth != 0) {
                active.onReport(
                    child.getAlias(), child
                );
                return true;
            } else {
                bundle = child.onPacket();
                return false;
            }
        } finally {
            child.onDetach();
        }
    }

    /**
     * Check if used by other threads
     */
    public boolean isLock() {
        synchronized (this) {
            return lock;
        }
    }

    /**
     * Apply for use and return status
     */
    public boolean lock() {
        synchronized (this) {
            if (lock) {
                return false;
            }
            return lock = true;
        }
    }

    /**
     * Release use and return status
     */
    public boolean unlock() {
        synchronized (this) {
            if (lock) {
                this.clear();
                lock = false;
            }
        }
        return true;
    }

    /**
     * Clear this {@link Parser}
     */
    public void clear() {
        depth = 0;
        range = 0;
        bundle = null;
    }

    /**
     * Close this {@link Parser}
     */
    @Override
    public void close() {
        this.clear();
        radar.close();
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    public static class Group extends KatCluster<Parser> {

        /**
         * default cluster
         */
        public static final Group
            INS = new Group();

        public Group() {
            super(Config.get(
                "kat.parser.capacity", 16
            ), Config.get(
                "kat.parser.block", true
            ));
        }

        @Override
        public Parser make() {
            return new Parser();
        }

        @Override
        public boolean stop(
            Parser parser
        ) {
            parser.close();
            return true;
        }

        @Override
        public boolean lock(
            Parser parser
        ) {
            return parser.lock();
        }

        @Override
        public boolean unlock(
            Parser parser
        ) {
            return parser.unlock();
        }
    }
}
