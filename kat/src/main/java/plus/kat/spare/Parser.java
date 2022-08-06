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

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Parser implements Pipe {
    /**
     * state etc.
     */
    volatile boolean lock;

    /**
     * snapshot etc.
     */
    protected Object bundle;
    protected int depth, range;

    protected Event<?> event;
    protected Builder<?> active;

    /**
     * solver etc.
     */
    protected Docx docx;
    protected Mage mage;
    protected Radar radar;

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
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code event} is null
     */
    @Nullable
    public Object read(
        @NotNull Event<?> event
    ) {
        return read(
            radar, event
        );
    }

    /**
     * Parses the {@link Event} by using specified {@link Solver}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code coder} or {@code event} is null
     */
    @Nullable
    public Object read(
        @NotNull Solver coder,
        @NotNull Event<?> event
    ) {
        Reader reader =
            event.getReader();

        if (reader == null) {
            return null;
        }

        this.event = event;
        this.range = event.getRange();

        try {
            coder.read(
                this, reader
            );
        } catch (Exception e) {
            event.onError(e);
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

        return bundle;
    }

    /**
     * Parses the {@link Event} with specified {@link Job}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code job} or {@code event} is null
     */
    @Nullable
    public Object read(
        @NotNull Job job,
        @NotNull Event<?> event
    ) {
        switch (job) {
            case KAT: {
                return read(
                    radar, event
                );
            }
            case DOC: {
                return read(
                    docx != null ? docx : (docx = new Docx(radar)), event
                );
            }
            case JSON: {
                return read(
                    mage != null ? mage : (mage = new Mage(radar)), event
                );
            }
            default: {
                throw new RunCrash(
                    "Unexpectedly, Parser did not find " + job + "'s Solver"
                );
            }
        }
    }

    /**
     * Parse {@link Event} and convert result to {@link T}
     *
     * @param event specify the {@code event} to be handled
     * @throws NullPointerException If the specified {@code event} is null
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T solve(
        @NotNull Job job,
        @NotNull Event<T> event
    ) {
        // parser pool
        Cluster cluster = Cluster.INS;

        // borrow parser
        Parser parser = cluster.borrow();

        // solve
        Object data = parser.read(
            job, event
        );

        // returns parser
        cluster.retreat(parser);

        // convert result
        return data == null ? null : (T) data;
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
            throw new OutOfRangeCrash(
                "Parse depth out of range"
            );
        }

        Alias name = alias.copy();
        Builder<?> child, parent = active;

        if (depth != 0) {
            child = active.getBuilder(
                space, name
            );
        } else {
            Spare<?> spare = event
                .getSpare(
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
        } catch (Crash e) {
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
            active.onAccept(
                space, alias, value
            );
        } else {
            Spare<?> spare = event
                .getSpare(
                    space, alias.copy()
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
            throw new OutOfRangeCrash(
                "Parse depth out of range"
            );
        }

        Builder<?> child = active;
        active = child.getParent();

        try {
            if (depth != 0) {
                active.onAccept(
                    child.getAlias(), child
                );
                return true;
            } else {
                bundle = child.getResult();
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
            lock = true;
            return true;
        }
    }

    /**
     * Release use and return status
     */
    public boolean unlock() {
        synchronized (this) {
            if (lock) {
                clear();
            }
            lock = false;
            return true;
        }
    }

    /**
     * clear this {@link Parser}
     */
    public void clear() {
        depth = 0;
        range = 0;
        bundle = null;
    }

    /**
     * close this {@link Parser}
     */
    public void close() {
        this.clear();
        radar.close();
    }

    /**
     * @author kraity
     * @since 0.0.1
     */
    public static class Cluster {

        private int grow;
        private final int size;
        private final boolean block;

        private volatile int count;
        private final Object[] group;

        /**
         * default cluster
         */
        private static final Cluster
            INS = new Cluster();

        public Cluster() {
            this(Config.get(
                "kat.parser.capacity", 16
            ), Config.get(
                "kat.parser.block", true
            ));
        }

        public Cluster(
            int size, boolean block
        ) {
            this.size = size;
            this.block = block;
            this.group = new Object[size];
        }

        @NotNull
        public Parser borrow() {
            synchronized (this) {
                while (true) {
                    if (count != 0) {
                        Parser f = (Parser)
                            group[--count];
                        group[count] = null;

                        if (f != null) {
                            if (f.lock()) {
                                return f;
                            } else {
                                continue;
                            }
                        }
                    }

                    if (block) {
                        if (grow < size) {
                            grow++;
                            break;
                        } else try {
                            wait(1000);
                        } catch (Exception e) {
                            grow++;
                            break;
                        }
                    } else {
                        grow++;
                        break;
                    }
                }
            }

            return new Parser();
        }

        public void retreat(
            @Nullable Parser f
        ) {
            if (f != null &&
                count < size && f.unlock()) {
                synchronized (this) {
                    if (count < size) {
                        group[count++] = f;
                        if (block) {
                            notify();
                        }
                    }
                }
            }
        }
    }
}
