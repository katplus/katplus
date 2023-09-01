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

import plus.kat.actor.Nilable;
import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.solver.*;
import plus.kat.stream.*;

import java.io.*;
import java.lang.reflect.*;

import static plus.kat.Algo.*;
import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.6
 */
@SuppressWarnings("unchecked")
public class Parser extends Factory implements Closeable {
    /**
     * chain etc.
     */
    protected Space space;
    protected Alias alias;
    protected Value value;

    /**
     * model etc.
     */
    protected Type type;
    protected Spare<?> spare;

    /**
     * state etc.
     */
    protected Flow flow;
    protected Object result;

    /**
     * solver etc.
     */
    protected Radar radar;
    protected Solver podar, sodar;
    protected KatBuffer<Parser> buffer;

    /**
     * default
     */
    public Parser() {
        this(
            new Alias(
                ALIAS_CAPACITY
            ),
            new Space(
                SPACE_CAPACITY
            ),
            new Value(
                VALUE_CAPACITY
            )
        );
    }

    /**
     * @param space the specified {@code space} of parser
     * @param alias the specified {@code alias} of parser
     * @param value the specified {@code value} of parser
     */
    public Parser(
        @NotNull Alias alias,
        @NotNull Space space,
        @NotNull Value value
    ) {
        this.alias = alias;
        this.space = space;
        this.value = value;
        this.radar = new Radar(
            alias, space, value
        );
        this.sodar = new Sodar(
            alias, space, value
        );
    }

    /**
     * Resolves the {@link Flow} by using {@link Radar}
     *
     * @param flow the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    public <T> T read(
        @NotNull Flow flow
    ) throws IOException {
        return solve(
            flow, radar
        );
    }

    /**
     * Resolves the {@link Flow} by using {@link Radar}
     *
     * @param flow the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    public <T> T down(
        @NotNull Flow flow
    ) throws IOException {
        Solver solver = podar;
        if (solver != null) {
            return solve(
                flow, solver
            );
        }
        return solve(
            flow, podar = new Podar(
                alias, space, value
            )
        );
    }

    /**
     * Resolves the {@link Flow} by using {@link Radar}
     *
     * @param flow the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    public <T> T parse(
        @NotNull Flow flow
    ) throws IOException {
        return solve(
            flow, sodar
        );
    }

    /**
     * Resolves the {@link Flow} by using specified {@link Solver}
     *
     * @param flow the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    public <T> T solve(
        @NotNull Flow flow,
        @NotNull Solver edge
    ) throws IOException {
        if (flow == null) {
            throw new IOException(
                "Received flow is null"
            );
        }

        if (spare == null) {
            throw new IOException(
                "Received spare is null"
            );
        }

        try {
            this.flow = flow;
            if (context == null) {
                context = spare.getContext();
            }

            if (flow.also()) {
                edge.solve(
                    flow, this
                );
                Object bean = result;
                if (bean != null) {
                    result = null;
                    return (T) bean;
                }
            }
        } catch (Exception alas) {
            throw new IOException(
                "Failed to solve " + flow, alas
            );
        } finally {
            edge.clear();
            flow.close();
        }

        return null;
    }

    /**
     * Resolves the {@link Flow} with specified {@link Algo}
     *
     * @param algo the specified algo of flow
     * @param flow the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    public <T> T solve(
        @NotNull Algo algo,
        @NotNull Flow flow
    ) throws IOException {
        switch (algo.hashCode()) {
            case kat: {
                return solve(
                    flow, radar
                );
            }
            case doc: {
                Solver solver = podar;
                if (solver != null) {
                    return solve(
                        flow, solver
                    );
                }
                return solve(
                    flow, podar = new Podar(
                        alias, space, value
                    )
                );
            }
            case json: {
                return solve(
                    flow, sodar
                );
            }
            default: {
                throw new IOException(
                    "Not found the solver of " + algo
                );
            }
        }
    }

    /**
     * Prepare the {@link Factory} before parsing
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void onOpen()
        throws IOException {
        // Nothing
    }

    /**
     * Receive the property of this {@link Factory}
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void onEach(
        @Nullable Object value
    ) throws IOException {
        result = value;
    }

    /**
     * Closes the resources for this {@link Factory}
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void onClose()
        throws IOException {
        // Nothing
    }

    /**
     * Starts a child of this spider
     *
     * @return the child spider, may be null
     * @throws IOException If an I/O error occurs
     */
    @Override
    public Spider onOpen(
        @NotNull Alias alias,
        @NotNull Space space
    ) throws IOException {
        Spare<?> coder = spare;
        if (coder != null) {
            Factory child =
                coder.getFactory(type);
            if (child != null) {
                return child.init(
                    this, context
                );
            }
        } else {
            throw new IOException(
                "No spare is available"
            );
        }

        throw new IOException(
            "The root builder<" + coder
                + "> is not allowed to be null"
        );
    }

    /**
     * Sets an attribute for this parser
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void onEach(
        @NotNull Alias alias,
        @NotNull Space space,
        @NotNull Value value
    ) throws IOException {
        Spare<?> coder = spare;
        if (coder != null) {
            result = coder.read(
                flow, value
            );
        } else {
            throw new IOException(
                "No spare is available"
            );
        }
    }

    /**
     * Closes the property update of this parser
     *
     * @return the parent spider, may be null
     * @throws IOException If an I/O error occurs
     */
    @Override
    public Spider onClose(
        boolean alert,
        boolean state
    ) throws IOException {
        Factory parent = holder;
        if (parent != null) {
            if (state) {
                parent.onEach(
                    result
                );
            }
            holder = null;
        }
        flow = null;
        return parent;
    }

    /**
     * Resolves the unknown type with this helper,
     * substituting type variables as far as possible
     */
    @Override
    public Type solve(Type type) {
        if (type instanceof WildcardType) {
            return solve(
                ((WildcardType) type).getUpperBounds()[0]
            );
        }

        if (type instanceof TypeVariable) {
            Type actor = this.type;
            Class<?> clazz = classOf(actor);

            if (clazz != null) {
                // If GenericDeclaration is method,
                // then a ClassCastException is thrown
                Class<?> entry = (Class<?>) (
                    (TypeVariable<?>) type).getGenericDeclaration();

                Search:
                for (Class<?> cls; ; clazz = cls) {
                    if (entry == clazz) break;
                    if (entry.isInterface()) {
                        Class<?>[] a = clazz.getInterfaces();
                        for (int i = 0; i < a.length; i++) {
                            cls = a[i];
                            if (cls == entry) {
                                actor = clazz.getGenericInterfaces()[i];
                                break Search;
                            } else if (entry.isAssignableFrom(cls)) {
                                actor = clazz.getGenericInterfaces()[i];
                                continue Search;
                            }
                        }
                    }
                    if (!clazz.isInterface()) {
                        for (; clazz != Object.class; clazz = cls) {
                            cls = clazz.getSuperclass();
                            if (cls == entry) {
                                actor = clazz.getGenericSuperclass();
                                break Search;
                            } else if (entry.isAssignableFrom(cls)) {
                                actor = clazz.getGenericSuperclass();
                                continue Search;
                            }
                        }
                    }
                    throw new IllegalStateException(
                        "Failed to resolve " + type + " from " + actor
                    );
                }

                if (actor instanceof ParameterizedType) {
                    Object[] items = entry.getTypeParameters();
                    for (int i = 0; i < items.length; i++) {
                        if (type == items[i]) {
                            return solve(
                                ((ParameterizedType) actor).getActualTypeArguments()[i]
                            );
                        }
                    }
                }
            }
            throw new IllegalStateException(
                "Failed to resolve " + type + " from " + actor
            );
        }
        return type;
    }

    /**
     * Sets the type of this {@link Parser}
     */
    public void setType(
        @Nilable Type value
    ) {
        if (value != null) {
            type = value;
        }
    }

    /**
     * Sets the spare of this {@link Parser}
     */
    public void setSpare(
        @Nilable Spare<?> value
    ) {
        if (value != null) {
            spare = value;
        }
    }

    /**
     * Sets the context of this {@link Parser}
     */
    public void setContext(
        @Nilable Context value
    ) {
        if (value != null) {
            context = value;
        }
    }

    /**
     * Check if this {@link Parser} uses the feature
     *
     * @param flag the specified flag code
     */
    @Override
    public boolean isFlag(
        @NotNull long flag
    ) {
        return flow.isFlag(flag);
    }

    /**
     * Closes resources associated with this {@link Parser}
     */
    @Override
    public void close() {
        type = null;
        spare = null;
        context = null;

        KatBuffer<Parser> node = buffer;
        if (node != null) {
            buffer = null;
            if (node.compareAndSet(null, this)
                || node.release(this)) {
                return;
            }

            try {
                radar = null;
                sodar = null;
                podar = null;
                alias.close();
                alias = null;
                space.close();
                space = null;
                value.close();
                value = null;
            } catch (Exception e) {
                // Ignore this exception
            }
        }
    }

    /**
     * Returns an instance of {@link Parser}
     */
    @NotNull
    public static Parser with(
        @Nullable Spare<?> spare
    ) {
        KatBuffer<Parser> buffer
            = TABLE[
            Thread.currentThread()
                .hashCode() & MASK];

        Parser parser =
            buffer.getAndSet(null);
        if (parser == null) {
            parser = buffer.acquire();
            if (parser == null) {
                parser = new Parser();
            }
        }

        parser.spare = spare;
        parser.buffer = buffer;
        return parser;
    }

    private static final int MASK;
    private static final KatBuffer<Parser>[] TABLE;

    static {
        int g = PARSER_GROUP;
        if ((g & (MASK = g - 1)) == 0) {
            TABLE = new KatBuffer[g];
            do {
                TABLE[--g] = new KatBuffer<>();
            } while (g > 0);
        } else {
            throw new Error(
                "Received " + g + " is not a power of two"
            );
        }
    }
}
