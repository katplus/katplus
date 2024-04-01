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
import plus.kat.core.*;
import plus.kat.flow.*;
import plus.kat.chain.*;

import java.io.*;
import java.lang.reflect.*;

import static plus.kat.Algo.*;
import static plus.kat.lang.Uniform.*;
import static plus.kat.spare.ClassSpare.*;

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
    protected Object target;

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
        this.podar = new Podar(
            alias, space, value
        );
        this.sodar = new Sodar(
            alias, space, value
        );
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
     * Resolves the {@link Flow} by using specified {@link Solver}
     *
     * @param datum the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    public <T> T solve(
        @NotNull Flow datum,
        @NotNull Solver robot
    ) throws IOException {
        if (datum == null) {
            throw new IOException(
                "Received flow is null"
            );
        }

        try {
            flow = datum;
            if (context == null) {
                context = spare.getContext();
            }

            if (datum.also()) {
                robot.solve(
                    datum, this
                );
                Object result = target;
                if (result != null) {
                    target = null;
                    return (T) result;
                }
            }
        } catch (Exception alas) {
            throw new IOException(
                "Failed to solve " + datum, alas
            );
        } finally {
            robot.clear();
            datum.close();
        }

        return null;
    }

    /**
     * Resolves the {@link Flow} with specified {@link Algo}
     *
     * @param algo  the specified algo of flow
     * @param datum the specified flow to be resolved
     * @throws IOException If an I/O error or parsing error occurs
     */
    @Nullable
    public <T> T solve(
        @NotNull Algo algo,
        @NotNull Flow datum
    ) throws IOException {
        switch (algo.hashCode()) {
            case kat: {
                return solve(datum, radar);
            }
            case doc: {
                return solve(datum, podar);
            }
            case json: {
                return solve(datum, sodar);
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
    public void onCreate()
        throws IOException {
        // Nothing
    }

    /**
     * Receive the property of this {@link Factory}
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void onNext(
        @Nullable Object value
    ) throws IOException {
        target = value;
    }

    /**
     * Releases the resources for this {@link Factory}
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void onDestroy()
        throws IOException {
        // Nothing
    }

    /**
     * Opens a child of this pipe and returns the child
     *
     * @param alias the alias of the current property
     * @param space the space of the current property
     * @return the child pipeline, may be null
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    @Override
    public Pipe onOpen(
        @NotNull Alias alias,
        @NotNull Space space
    ) throws IOException {
        Spare<?> coder = spare;
        if (coder == null) {
            coder = context
                .assign(type, space);
            if (coder == null) {
                throw new IOException(
                    "No spare is available"
                );
            }
        }

        Factory member =
            coder.getFactory(type);
        if (member != null) {
            return member.attach(this);
        }

        throw new IOException(
            "No factory is available"
        );
    }

    /**
     * Receives the alias, spare and value in a loop
     *
     * @param alias the alias of the current property
     * @param space the space of the current property
     * @param value the value of the current property
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    @Override
    public void onNext(
        @NotNull Alias alias,
        @NotNull Space space,
        @NotNull Value value
    ) throws IOException {
        Spare<?> coder = spare;
        if (coder == null) {
            coder = context
                .assign(type, space);
            if (coder == null) {
                throw new IOException(
                    "No spare is available"
                );
            }
        }

        target = coder.read(flow, value);
    }

    /**
     * Closes the transport of this pipe and returns the parent
     *
     * @param alert the flag that can be thrown errors
     * @param state the flag that was successfully built
     * @return the parent pipeline, may be null
     * @throws IOException              If a read error occurs
     * @throws IllegalStateException    If a fatal error occurs
     * @throws IllegalArgumentException If a params error occurs
     */
    @Override
    public Pipe onClose(
        boolean alert,
        boolean state
    ) throws IOException {
        Factory target = parent;
        if (target != null) {
            if (state) {
                target.onNext(
                    target
                );
            }
            parent = null;
        }
        flow = null;
        return target;
    }

    /**
     * Returns the type of factory to build
     */
    @Override
    public Type getType() {
        return type;
    }

    /**
     * Use this factory to resolve generic type
     * and replace type variables as much as possible
     *
     * @param generic the specified generic type
     * @throws IllegalArgumentException If the generic is illegal
     */
    @Override
    public Type getType(Type generic) {
        if (generic instanceof Class ||
            generic instanceof GenericArrayType ||
            generic instanceof ParameterizedType) {
            return generic;
        }

        if (generic instanceof WildcardType) {
            return getType(
                ((WildcardType) generic).getUpperBounds()[0]
            );
        }

        if (generic instanceof TypeVariable) {
            Type actor = type;
            Class<?> clazz = classOf(actor);

            if (clazz != null) {
                // If GenericDeclaration is method,
                // then a ClassCastException is thrown
                Class<?> entry = (Class<?>) (
                    (TypeVariable<?>) generic).getGenericDeclaration();

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

                    if (parent != null) {
                        return parent.getType(generic);
                    }
                    throw new IllegalStateException(
                        "Failed to resolve " + generic + " from " + actor
                    );
                }

                if (actor instanceof ParameterizedType) {
                    Object[] items = entry.getTypeParameters();
                    for (int i = 0; i < items.length; i++) {
                        if (generic == items[i]) {
                            return getType(
                                ((ParameterizedType) actor).getActualTypeArguments()[i]
                            );
                        }
                    }
                }
            }
            throw new IllegalStateException(
                "Failed to resolve " + generic + " from " + actor
            );
        }

        if (generic != null) {
            return parent == null ?
                generic : parent.getType(generic);
        }
        throw new IllegalArgumentException(
            "Received unknown mold type is illegal"
        );
    }

    /**
     * Sets the type of this {@link Parser}
     */
    public void setType(
        @Nilable Type last
    ) {
        if (last != null) {
            type = last;
        }
    }

    /**
     * Gets the spare of this {@link Parser}
     */
    @Nullable
    public Spare<?> getSpare() {
        return spare;
    }

    /**
     * Sets the spare of this {@link Parser}
     */
    public void setSpare(
        @Nilable Spare<?> last
    ) {
        if (last != null) {
            spare = last;
        }
    }

    /**
     * Gets the context of this {@link Parser}
     */
    @Nullable
    public Context getContext() {
        return context;
    }

    /**
     * Sets the context of this {@link Parser}
     */
    public void setContext(
        @Nilable Context last
    ) {
        if (last != null) {
            context = last;
        }
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
            if (node.resume(this)) {
                return;
            }

            try {
                radar = null;
                sodar = null;
                podar = null;
                alias.close();
                space.close();
                value.close();
            } catch (Exception e) {
                // Nothing
            } finally {
                alias = null;
                space = null;
                value = null;
            }
        }
    }

    /**
     * Returns an instance of {@link Parser}
     */
    @NotNull
    public static Parser apply() {
        KatBuffer<Parser> buffer = TABLE[Thread
            .currentThread().hashCode() & MASK];

        Parser target = buffer.borrow();
        if (target == null) target = new Parser();

        target.buffer = buffer;
        return target;
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
