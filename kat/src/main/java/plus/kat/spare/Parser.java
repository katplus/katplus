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
import plus.kat.utils.*;
import plus.kat.solver.*;
import plus.kat.stream.*;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Parser implements Channel, Callback, Closeable {
    /**
     * state etc.
     */
    volatile boolean lock;
    protected Object result;

    /**
     * chain etc.
     */
    protected Space space;
    protected Alias alias;
    protected Value value;

    /**
     * solver etc.
     */
    protected Radar radar;
    protected Solver podar, sodar;

    /**
     * snapshot etc.
     */
    protected Event<?> event;
    protected Supplier supplier;

    /**
     * default
     */
    public Parser() {
        this(
            Space.Buffer.INS,
            Alias.Buffer.INS,
            Value.Buffer.INS
        );
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
        space = new Space(b1);
        alias = new Alias(b2);
        value = new Value(b3);
        radar = new Radar(
            space, alias, value
        );
    }

    /**
     * Parses the {@link Event} by using {@link Radar}
     *
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws IOException          Unexpected errors by {@link Pipage} or {@link Reader}
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
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws IOException          Unexpected errors by {@link Pipage} or {@link Reader}
     * @throws NullPointerException If the specified {@code radar} or {@code event} is null
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> T read(
        @NotNull Solver radar,
        @NotNull Event<T> event
    ) throws IOException {
        Reader reader =
            event.getReader();
        if (reader == null) {
            throw new Collapse(
                "Reader is null"
            );
        }

        Supplier supplier =
            event.getSupplier();
        if (supplier == null) {
            throw new Collapse(
                "Supplier is null"
            );
        }

        this.event = event;
        this.supplier = supplier;

        try {
            if (reader.also()) {
                radar.read(
                    reader, this
                );
                Object data = result;
                if (data != null) {
                    return (T) data;
                }
            }
        } finally {
            radar.clear();
            reader.close();
        }

        throw new Collapse(
            "Parsing error, the result is null"
        );
    }

    /**
     * Parses the {@link Event} with specified {@link Algo}
     *
     * @param event the specified event to be handled
     * @throws Collapse             If parsing fails or the result is null
     * @throws FatalCrash           If no solver available for algo is found
     * @throws IOException          Unexpected errors by {@link Pipage} or {@link Reader}
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
                Solver it = podar;
                if (it == null) {
                    podar = it = new Podar(
                        space, alias, value
                    );
                }
                return read(
                    it, event
                );
            }
            case "json": {
                Solver it = sodar;
                if (it == null) {
                    sodar = it = new Sodar(
                        space, alias, value
                    );
                }
                return read(
                    it, event
                );
            }
            default: {
                throw new FatalCrash(
                    "Parser didn't find the Solver of " + algo
                );
            }
        }
    }

    /**
     * Starts a sub pipage of this pipage
     *
     * @return the sub pipage, may be null
     * @throws IOException If an I/O error occurs
     */
    @Override
    public Pipage onOpen(
        @NotNull Space space,
        @NotNull Alias alias
    ) throws IOException {
        Type type = event.getType();
        Coder<?> coder = event.getCoder();

        if (coder != null) {
            Builder<?> child =
                coder.getBuilder(type);
            if (child != null) {
                return child.init(this, this);
            }
        } else {
            coder = supplier.lookup(
                Space.wipe(type), space
            );
            if (coder != null) {
                Builder<?> child =
                    coder.getBuilder(type);
                if (child != null) {
                    return child.init(this, this);
                }
            } else {
                throw new IOException(
                    "The spare of " + alias
                        + "<" + space + "> was not found"
                );
            }
        }

        throw new IOException(
            "The root builder<" + space
                + ", " + alias + "> is not allowed to be null"
        );
    }

    /**
     * Sets an attribute for this parser
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void onEmit(
        @NotNull Pipage pipage,
        @Nullable Object object
    ) throws IOException {
        result = object;
    }

    /**
     * Sets an attribute for this parser
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void onEmit(
        @NotNull Space space,
        @NotNull Alias alias,
        @NotNull Value value
    ) throws IOException {
        Coder<?> coder = event.getCoder();
        if (coder != null) {
            result = coder.read(
                event, value
            );
        } else {
            coder = supplier.lookup(
                Space.wipe(event.getType()), space
            );
            if (coder != null) {
                result = coder.read(
                    event, value
                );
            } else {
                throw new IOException(
                    "The spare of " + alias
                        + "<" + space + "> was not found"
                );
            }
        }
    }

    /**
     * Closes the property update of this parser
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public Pipage onClose(
        boolean state,
        boolean alarm
    ) throws IOException {
        event = null;
        supplier = null;
        return null;
    }

    /**
     * Requests to lock and return success status
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
     * Releases the lock and return success status
     */
    public boolean unlock() {
        synchronized (this) {
            if (lock) {
                clear();
                lock = false;
            }
        }
        return true;
    }

    /**
     * Clears this {@link Parser}
     */
    public void clear() {
        result = null;
    }

    /**
     * Closes this {@link Parser}
     */
    @Override
    public void close() {
        this.clear();
        radar.close();
    }

    /**
     * Returns the flag of this {@link Channel}
     *
     * @return {@link Flag} or {@code null}
     */
    @Nullable
    public Flag flag() {
        return event;
    }

    /**
     * Returns the parent of this {@link Channel}
     *
     * @return {@link Channel} or {@code null}
     */
    @Nullable
    public Channel holder() {
        return null;
    }

    /**
     * Returns the supplier of this {@link Channel}
     *
     * @return {@link Supplier} or {@code null}
     */
    @Nullable
    public Supplier supplier() {
        return supplier;
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
                "kat.parser.size", 32
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
