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
package plus.kat;

import plus.kat.actor.*;
import plus.kat.chain.*;
import plus.kat.spare.*;
import plus.kat.stream.*;

import plus.kat.utils.Config;
import plus.kat.utils.KatLoader;

import java.io.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import java.math.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static plus.kat.Algo.*;
import static plus.kat.spare.Parser.*;
import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Supplier extends Context {
    /**
     * Resolves the kat text and converts the result to {@link T}
     *
     * @param type the specified type of {@link T}
     * @param flow the specified flow to be resolved
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws NullPointerException     If the specified arguments is null
     * @throws IllegalStateException    If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     * @since 0.0.6
     */
    @Nullable
    default <T> T read(
        @NotNull Type type,
        @NotNull Flow flow
    ) {
        return solve(
            KAT, type, flow
        );
    }

    /**
     * Serializes the specified value to kat {@link Chan} with the flags
     *
     * @param value the specified value to serialized
     * @param flags the specified flags for serialize
     * @throws IOException              If an I/O error occurs
     * @throws IllegalArgumentException If no chan available is found
     */
    @NotNull
    default Chan write(
        @Nullable Object value, long flags
    ) throws IOException {
        Chan chan = telex(
            KAT, flags
        );
        try {
            chan.set(
                null, value
            );
        } catch (Throwable alas) {
            try {
                chan.close();
            } catch (Throwable e) {
                alas.addSuppressed(e);
            }
            throw alas;
        }
        return chan;
    }

    /**
     * Resolves the xml text and converts the result to {@link T}
     *
     * @param type the specified type of {@link T}
     * @param flow the specified flow to be resolved
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws NullPointerException     If the specified arguments is null
     * @throws IllegalStateException    If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     * @since 0.0.6
     */
    @Nullable
    default <T> T down(
        @NotNull Type type,
        @NotNull Flow flow
    ) {
        return solve(
            DOC, type, flow
        );
    }

    /**
     * Serializes the specified value to xml {@link Chan} with the flags
     *
     * @param value the specified value to serialized
     * @param flags the specified flags for serialize
     * @throws IOException              If an I/O error occurs
     * @throws IllegalArgumentException If no chan available is found
     */
    @NotNull
    default Chan mark(
        @Nullable Object value, long flags
    ) throws IOException {
        Chan chan = telex(
            DOC, flags
        );
        try {
            chan.set(
                null, value
            );
        } catch (Throwable alas) {
            try {
                chan.close();
            } catch (Throwable e) {
                alas.addSuppressed(e);
            }
            throw alas;
        }
        return chan;
    }

    /**
     * Resolves the json text and converts the result to {@link T}
     *
     * @param type the specified type of {@link T}
     * @param flow the specified flow to be resolved
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws NullPointerException     If the specified arguments is null
     * @throws IllegalStateException    If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     * @since 0.0.6
     */
    @Nullable
    default <T> T parse(
        @NotNull Type type,
        @NotNull Flow flow
    ) {
        return solve(
            JSON, type, flow
        );
    }

    /**
     * Serializes the specified value to json {@link Chan} with the flags
     *
     * @param value the specified value to serialized
     * @param flags the specified flags for serialize
     * @throws IOException              If an I/O error occurs
     * @throws IllegalArgumentException If no chan available is found
     */
    @NotNull
    default Chan serial(
        @Nullable Object value, long flags
    ) throws IOException {
        Chan chan = telex(
            JSON, flags
        );
        try {
            chan.set(
                null, value
            );
        } catch (Throwable alas) {
            try {
                chan.close();
            } catch (Throwable e) {
                alas.addSuppressed(e);
            }
            throw alas;
        }
        return chan;
    }

    /**
     * Returns the default supplier
     */
    @NotNull
    static Supplier ins() {
        return Vendor.INS;
    }

    /**
     * If the spare can create an instance of the actual
     * subclass type, it returns it, otherwise it will throw collapse
     *
     * @param type the specified type for lookup
     * @return {@link T}, it is not null
     * @throws ClassCastException       If {@link T} type is not the type
     * @throws NullPointerException     If the specified {@code argument} is null
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the type is found
     * @see Spare#apply(Type)
     * @see Supplier#assign(Type)
     * @since 0.0.6
     */
    @NotNull
    default <T> T apply(
        @NotNull Type type
    ) {
        Spare<T> spare = assign(type);

        if (spare != null) {
            T bean = spare.apply(type);
            if (bean != null) {
                return bean;
            }
            throw new IllegalStateException(
                "Failed to build this " + type
            );
        } else {
            throw new IllegalArgumentException(
                "Not found the spare of " + type
            );
        }
    }

    /**
     * If the spare can create an instance of the actual
     * subclass klass, it returns it, otherwise it will throw collapse
     *
     * @param klass the specified klass for lookup
     * @return {@link T}, it is not null
     * @throws ClassCastException       If {@link T} klass is not the klass
     * @throws NullPointerException     If the specified {@code argument} is null
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the klass is found
     * @see Spare#apply(Type)
     * @since 0.0.6
     */
    @NotNull
    default <T> T apply(
        @NotNull Class<T> klass
    ) {
        Spare<T> spare = assign(klass);

        if (spare != null) {
            T bean = spare.apply(klass);
            if (bean != null) {
                return bean;
            }
            throw new IllegalStateException(
                "Failed to build this " + klass
            );
        } else {
            throw new IllegalArgumentException(
                "Not found the spare of " + klass
            );
        }
    }

    /**
     * Returns the {@link Chan} of the algo with the specified flags
     *
     * @param algo  the specified algo for telex
     * @param flags the specified flags for serialize
     * @throws NullPointerException     If the specified algo for telex is null
     * @throws IllegalArgumentException If no chan available for the algo is found
     * @since 0.0.6
     */
    @NotNull
    default Chan telex(
        @NotNull Algo algo,
        @NotNull long flags
    ) {
        switch (algo.hashCode()) {
            case kat: {
                return new Kat(flags, this);
            }
            case doc: {
                return new Doc(flags, this);
            }
            case json: {
                return new Json(flags, this);
            }
        }
        throw new IllegalArgumentException(
            "Failed to find the chan of " + algo
        );
    }

    /**
     * Resolves the specified text and converts the result to {@link T}
     *
     * @param algo the specified algo for solve
     * @param type the specified type of {@link T}
     * @param flow the specified flow to be resolved
     * @throws NullPointerException     If the specified arguments is null
     * @throws IllegalStateException    If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the klass is found
     * @since 0.0.6
     */
    @Nullable
    default <T> T solve(
        @NotNull Algo algo,
        @NotNull Type type,
        @NotNull Flow flow
    ) {
        Spare<T> spare = assign(type);

        if (spare != null) {
            try (Parser op = with(spare)) {
                op.setType(type);
                op.setContext(this);
                return op.solve(algo, flow);
            }
        }

        throw new IllegalArgumentException(
            "No available spare for " + type + " was found"
        );
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    @SuppressWarnings("unchecked")
    class Vendor implements Supplier, Provider {

        static Vendor INS;
        static Provider[] PRO;

        static {
            INS = new Vendor(
                Config.get(
                    "kat.supplier.buffer", 64
                ),
                Config.get(
                    "kat.supplier.capacity", 64
                )
            );

            Map<Object, Spare<?>>
                minor = INS.minor,
                major = INS.major;

            minor.put("", ObjectSpare.INSTANCE);
            minor.put("{", MapSpare.INSTANCE);
            minor.put("[", ListSpare.INSTANCE);
            minor.put("\"", StringSpare.INSTANCE);
            minor.put("Any", ObjectSpare.INSTANCE);
            minor.put("String", StringSpare.INSTANCE);
            minor.put("Int", IntSpare.INSTANCE);
            minor.put("Long", LongSpare.INSTANCE);
            minor.put("Boolean", BooleanSpare.INSTANCE);
            minor.put("Float", FloatSpare.INSTANCE);
            minor.put("Double", DoubleSpare.INSTANCE);
            minor.put("Char", CharSpare.INSTANCE);
            minor.put("Byte", ByteSpare.INSTANCE);
            minor.put("Short", ShortSpare.INSTANCE);
            minor.put("Number", NumberSpare.INSTANCE);
            minor.put("Map", MapSpare.INSTANCE);
            minor.put("Set", SetSpare.INSTANCE);
            minor.put("List", ListSpare.INSTANCE);
            minor.put("Array", ArraySpare.INSTANCE);
            minor.put("ByteArray", ByteArraySpare.INSTANCE);

            major.put(String.class, StringSpare.INSTANCE);
            major.put(int.class, IntSpare.INSTANCE);
            major.put(Integer.class, IntSpare.INSTANCE);
            major.put(long.class, LongSpare.INSTANCE);
            major.put(Long.class, LongSpare.INSTANCE);
            major.put(boolean.class, BooleanSpare.INSTANCE);
            major.put(Boolean.class, BooleanSpare.INSTANCE);
            major.put(float.class, FloatSpare.INSTANCE);
            major.put(Float.class, FloatSpare.INSTANCE);
            major.put(double.class, DoubleSpare.INSTANCE);
            major.put(Double.class, DoubleSpare.INSTANCE);
            major.put(char.class, CharSpare.INSTANCE);
            major.put(byte.class, ByteSpare.INSTANCE);
            major.put(Byte.class, ByteSpare.INSTANCE);
            major.put(short.class, ShortSpare.INSTANCE);
            major.put(Short.class, ShortSpare.INSTANCE);
            major.put(void.class, VoidSpare.INSTANCE);
            major.put(Void.class, VoidSpare.INSTANCE);
            major.put(Object[].class, ArraySpare.INSTANCE);
            major.put(Character.class, CharSpare.INSTANCE);

            try (KatLoader<Provider> loader =
                     new KatLoader<>(Provider.class)) {
                loader.load(
                    Config.get(
                        "kat.spare.provider",
                        "plus.kat.spare.Provider"
                    )
                );

                if (loader.hasNext()) {
                    final int size = loader.size() + 1;
                    Provider[] RS = new Provider[size];

                    int m = 0;
                    while (loader.hasNext()) {
                        RS[m++] = loader.next();
                    }

                    int n = 0;
                    for (int i = 0; i < m; i++) {
                        Provider p = RS[i];
                        try {
                            if (p.alive(INS)) {
                                RS[n++] = p;
                            }
                        } catch (Exception e) {
                            throw new Error(
                                "Failed to activate " + p, e
                            );
                        }
                    }

                    RS[n++] = INS;
                    if (n != size) {
                        Provider[] rs = new Provider[n];
                        System.arraycopy(
                            RS, 0, RS = rs, 0, n
                        );
                    }

                    if (n != 1) {
                        Arrays.sort(
                            RS, Collections.reverseOrder()
                        );
                    }
                    PRO = RS;
                } else {
                    PRO = new Provider[]{INS};
                }
            } catch (Exception e) {
                throw new Error(
                    "Failed to load the external providers", e
                );
            }
        }

        /**
         * Internal mapping table
         */
        protected final ConcurrentHashMap
            <Object, Spare<?>> minor, major;

        /**
         * Constructs a supplier which has a service provider
         *
         * @param buffer   the init capacity of minor mapping table
         * @param capacity the init capacity of major mapping table
         */
        public Vendor(
            int buffer, int capacity
        ) {
            minor = new ConcurrentHashMap<>(buffer);
            major = new ConcurrentHashMap<>(capacity);
        }

        @Override
        public boolean alive(
            @NotNull Context o
        ) {
            throw new IllegalStateException(
                "This is a private provider"
            );
        }

        @Override
        public int grade() {
            return 0x88888888;
        }

        @Override
        public Spare<?> active(
            @NotNull Type type,
            @NotNull Spare<?> spare
        ) {
            return major.put(type, spare);
        }

        @Override
        public Spare<?> revoke(
            @NotNull Type type,
            @Nullable Spare<?> spare
        ) {
            if (spare == null) {
                spare = major.remove(type);
                if (spare != null) {
                    String name =
                        spare.getSpace();
                    if (name != null) {
                        minor.remove(
                            name, spare
                        );
                    }
                }
            } else {
                if (major.remove(type, spare)) {
                    String name =
                        spare.getSpace();
                    if (name != null) {
                        minor.remove(
                            name, spare
                        );
                    }
                } else {
                    return null;
                }
            }

            return spare;
        }

        @Override
        public <T> Spare<T> assign(
            @NotNull Type type
        ) {
            Spare<?> spare = major.get(type);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            Provider[] PS = PRO;
            if (PS != null) {
                for (Provider p : PS) {
                    spare = p.search(
                        type, this
                    );

                    if (spare != null) {
                        return (Spare<T>) spare;
                    }
                }
            }

            return null;
        }

        @Override
        public <T> Spare<T> assign(
            @NotNull Type type,
            @NotNull Space name
        ) {
            Spare<?> spare = major.get(type);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            Provider[] PS = PRO;
            if (PS != null) {
                for (Provider p : PS) {
                    spare = p.search(
                        type, name, this
                    );

                    if (spare != null) {
                        return (Spare<T>) spare;
                    }
                }
            }

            return null;
        }

        @Override
        public Spare<?> search(
            @NotNull Type type,
            @NotNull Context context
        ) {
            return search(
                type, null, context
            );
        }

        @Override
        public Spare<?> search(
            @NotNull Type type,
            @Nilable Space name,
            @NotNull Context context
        ) {
            Class<?> clazz;
            if (type instanceof Class) {
                clazz = (Class<?>) type;
            } else {
                while (true) {
                    if (type instanceof ParameterizedType) {
                        type = ((ParameterizedType) type).getRawType();
                        if (type instanceof Class) {
                            Spare<?> spare = major.get(type);
                            if (spare == null) {
                                clazz = (Class<?>) type;
                                break;
                            } else {
                                return spare;
                            }
                        }
                        continue;
                    }

                    if (type instanceof WildcardType) {
                        type = ((WildcardType) type).getUpperBounds()[0];
                        if (type instanceof Class) {
                            Spare<?> spare = major.get(type);
                            if (spare == null) {
                                clazz = (Class<?>) type;
                                break;
                            } else {
                                return spare;
                            }
                        }
                        continue;
                    }

                    if (type instanceof GenericArrayType) {
                        Class<?> elem = classOf(
                            ((GenericArrayType) type).getGenericComponentType()
                        );
                        if (elem != null) {
                            if (elem == Object.class) {
                                return assign(Object[].class);
                            }
                            if (elem == String.class) {
                                return assign(String[].class);
                            }
                            if (elem.isPrimitive()) {
                                if (elem == int.class) {
                                    return assign(int[].class);
                                }
                                if (elem == long.class) {
                                    return assign(long[].class);
                                }
                                if (elem == float.class) {
                                    return assign(float[].class);
                                }
                                if (elem == double.class) {
                                    return assign(double[].class);
                                }
                                if (elem == byte.class) {
                                    return assign(byte[].class);
                                }
                                if (elem == short.class) {
                                    return assign(short[].class);
                                }
                                if (elem == char.class) {
                                    return assign(char[].class);
                                }
                                if (elem == boolean.class) {
                                    return assign(boolean[].class);
                                }
                            } else {
                                return assign(
                                    Array.newInstance(elem, 0).getClass()
                                );
                            }
                        }
                    }

                    return null;
                }
            }

            if (name != null) {
                Spare<?> spare = minor.get(name);
                if (spare != null &&
                    clazz.isAssignableFrom(spare.getType())) {
                    return spare;
                }
            }

            final String alias = clazz.getName();
            edge:
            {
                loop:
                {
                    switch (alias.charAt(0)) {
                        case 'j': {
                            if (alias.startsWith("java.")) {
                                if (alias.indexOf('$', 6) == -1) {
                                    break loop;
                                } else {
                                    return null;
                                }
                            }
                            if (alias.startsWith("jdk.") ||
                                alias.startsWith("javax.")) {
                                return null;
                            }
                            break;
                        }
                        case 's': {
                            if (alias.startsWith("sun.") ||
                                alias.startsWith("scala.")) {
                                return null;
                            }
                            break;
                        }
                        case 'k': {
                            if (alias.startsWith("kotlin.") ||
                                alias.startsWith("kotlinx.")) {
                                return null;
                            }
                            break;
                        }
                        case 'a': {
                            if (alias.startsWith("android.") ||
                                alias.startsWith("androidx.")) {
                                return null;
                            }
                            break;
                        }
                        case '[': {
                            Spare<?> spare;
                            if (clazz == Object[].class) {
                                spare = ArraySpare.INSTANCE;
                            } else if (clazz == byte[].class) {
                                spare = ByteArraySpare.INSTANCE;
                            } else {
                                spare = new ArraySpare(clazz, this);
                            }
                            major.put(
                                clazz, spare
                            );
                            return spare;
                        }
                    }

                    String space = alias;
                    String[] spaces = null;

                    Spare<?> coder;
                    stage:
                    {
                        Magus magus = clazz.getAnnotation(Magus.class);
                        if (magus == null) {
                            if (clazz.isInterface() ||
                                Coder.class.isAssignableFrom(clazz) ||
                                Entity.class.isAssignableFrom(clazz) ||
                                Binary.class.isAssignableFrom(clazz) ||
                                Throwable.class.isAssignableFrom(clazz)) {
                                if (name != null) {
                                    break edge;
                                } else {
                                    return null;
                                }
                            }
                        } else {
                            Class<?> agent = magus.agent();
                            if (agent != void.class) {
                                // check to see if it's a proxy class
                                if (clazz.isAssignableFrom(agent)) {
                                    Spare<?> spare = assign(agent);
                                    if (spare == null) {
                                        if (name != null) {
                                            break edge;
                                        } else {
                                            return null;
                                        }
                                    } else {
                                        major.putIfAbsent(
                                            clazz, spare
                                        );
                                    }
                                    return spare;
                                }

                                // check to see if it's a spare class
                                if (Spare.class.isAssignableFrom(agent)) {
                                    // double-checking
                                    Spare<?> spare = major.get(clazz);

                                    if (spare != null ||
                                        agent.isInterface()) {
                                        return spare;
                                    }

                                    try {
                                        Constructor<?>[] cs = agent
                                            .getDeclaredConstructors();
                                        Constructor<?> buffer,
                                            latest = cs[0];
                                        Class<?>[] bufferType,
                                            latestType = latest.getParameterTypes();
                                        for (int i = 1; i < cs.length; i++) {
                                            buffer = cs[i];
                                            bufferType = buffer.getParameterTypes();
                                            if (latestType.length <=
                                                bufferType.length) {
                                                latest = buffer;
                                                latestType = bufferType;
                                            }
                                        }

                                        Object[] args;
                                        final int size = latestType.length;

                                        String[] names = magus.value();
                                        if (names.length != 0) {
                                            space = (spaces = names)[0];
                                        }

                                        if (size == 0) {
                                            args = ArraySpare.EMPTY_ARRAY;
                                        } else {
                                            args = new Object[size];
                                            for (int i = 0; i < size; i++) {
                                                Class<?> m = latestType[i];
                                                if (m == Class.class) {
                                                    args[i] = clazz;
                                                } else if (m == Magus.class) {
                                                    args[i] = magus;
                                                } else if (m == Space.class) {
                                                    args[i] = name;
                                                } else if (m == String.class) {
                                                    args[i] = space;
                                                } else if (m == Context.class) {
                                                    args[i] = this;
                                                } else if (m == Supplier.class) {
                                                    args[i] = this;
                                                } else if (m.isPrimitive()) {
                                                    args[i] = assign(m).apply();
                                                } else if (m.isAnnotation()) {
                                                    args[i] = clazz.getAnnotation(
                                                        (Class<? extends Annotation>) m
                                                    );
                                                }
                                            }
                                        }

                                        if (!latest.isAccessible()) {
                                            latest.setAccessible(true);
                                        }

                                        coder = ((Spare<?>) latest.newInstance(args));
                                        break stage;
                                    } catch (Exception e) {
                                        throw new IllegalArgumentException(
                                            "Failed to build the '"
                                                + alias + "' coder: " + agent, e
                                        );
                                    }
                                } else {
                                    throw new IllegalArgumentException(
                                        "Failed to resolve " + alias + "'s agent " + agent
                                    );
                                }
                            }

                            String[] names = magus.value();
                            if (names.length != 0) {
                                space = (spaces = names)[0];
                            }

                            if (clazz.isInterface()) {
                                coder = new ProxySpare(
                                    space, clazz, this
                                );
                                break stage;
                            }
                        }

                        // exclude abstract class
                        if ((clazz.getModifiers() & 0x400) != 0) {
                            if (name != null) {
                                break edge;
                            } else {
                                return null;
                            }
                        }

                        switch (clazz.getSuperclass().getName()) {
                            case "java.lang.Enum": {
                                coder = new EnumSpare<>(
                                    space, clazz, this
                                );
                                break stage;
                            }
                            case "java.lang.Record": {
                                coder = new RecordSpare<>(
                                    space, clazz, this
                                );
                                break stage;
                            }
                            case "java.lang.reflect.Proxy": {
                                coder = new ProxySpare(
                                    space, clazz, this
                                );
                                break stage;
                            }
                            default: {
                                coder = new ReflectSpare<>(
                                    space, clazz, this
                                );
                            }
                        }
                    }

                    major.put(clazz, coder);
                    if (spaces != null) {
                        for (String key : spaces) {
                            if (key.indexOf('.', 1) != -1) {
                                minor.put(key, coder);
                            }
                        }
                    }
                    return coder;
                }

                // lookup the appropriate spare
                Spare<?> spare;
                switch (alias.charAt(5)) {
                    // java.io
                    case 'i': {
                        if (clazz == File.class) {
                            spare = FileSpare.INSTANCE;
                        } else {
                            break edge;
                        }
                        major.put(clazz, spare);
                        return spare;
                    }
                    // java.nio
                    // java.net
                    case 'n': {
                        if (clazz == URI.class) {
                            spare = URISpare.INSTANCE;
                        } else if (clazz == URL.class) {
                            spare = URLSpare.INSTANCE;
                        } else if (ByteBuffer.class.isAssignableFrom(clazz)) {
                            spare = ByteBufferSpare.INSTANCE;
                        } else {
                            break edge;
                        }
                        major.put(clazz, spare);
                        return spare;
                    }
                    // java.math
                    case 'm': {
                        if (clazz == BigInteger.class) {
                            spare = BigIntegerSpare.INSTANCE;
                        } else if (clazz == BigDecimal.class) {
                            spare = BigDecimalSpare.INSTANCE;
                        } else {
                            break edge;
                        }
                        major.put(clazz, spare);
                        return spare;
                    }
                    // java.lang
                    case 'l': {
                        if (clazz == Object.class) {
                            return ObjectSpare.INSTANCE;
                        } else if (clazz == Number.class) {
                            if (name == null ||
                                name.isBlank()) {
                                return NumberSpare.INSTANCE;
                            }
                            break edge;
                        } else if (clazz == Class.class) {
                            spare = ClassSpare.INSTANCE;
                        } else if (clazz == Iterable.class) {
                            if (name == null ||
                                name.isBlank()) {
                                return ListSpare.INSTANCE;
                            }
                            break edge;
                        } else if (clazz == CharSequence.class) {
                            if (name == null ||
                                name.isBlank()) {
                                return StringSpare.INSTANCE;
                            }
                            break edge;
                        } else if (clazz == StringBuffer.class) {
                            spare = StringBufferSpare.INSTANCE;
                        } else if (clazz == StringBuilder.class) {
                            spare = StringBuilderSpare.INSTANCE;
                        } else {
                            break edge;
                        }
                        major.put(clazz, spare);
                        return spare;
                    }
                    // java.util
                    case 'u': {
                        switch (alias.lastIndexOf('.')) {
                            // java.util.concurrent.
                            case 20: {
                                if (Map.class.isAssignableFrom(clazz)) {
                                    spare = new MapSpare(clazz);
                                } else if (Set.class.isAssignableFrom(clazz)) {
                                    spare = new SetSpare(clazz);
                                } else if (List.class.isAssignableFrom(clazz)) {
                                    spare = new ListSpare(clazz);
                                } else {
                                    break edge;
                                }
                                major.put(clazz, spare);
                                return spare;
                            }
                            // java.util.concurrent.atomic.
                            case 27: {
                                if (clazz == AtomicLong.class) {
                                    spare = AtomicLongSpare.INSTANCE;
                                } else if (clazz == AtomicInteger.class) {
                                    spare = AtomicIntegerSpare.INSTANCE;
                                } else if (clazz == AtomicBoolean.class) {
                                    spare = AtomicBooleanSpare.INSTANCE;
                                } else {
                                    break edge;
                                }
                                major.put(clazz, spare);
                                return spare;
                            }
                            // java.util.
                            case 9: {
                                if (clazz == Date.class) {
                                    spare = DateSpare.INSTANCE;
                                } else if (clazz == UUID.class) {
                                    spare = UUIDSpare.INSTANCE;
                                } else if (clazz == Currency.class) {
                                    spare = CurrencySpare.INSTANCE;
                                } else if (clazz == Locale.class) {
                                    spare = LocaleSpare.INSTANCE;
                                } else if (clazz == Queue.class) {
                                    if (name == null ||
                                        name.isBlank()) {
                                        return assign(LinkedList.class);
                                    }
                                    break edge;
                                } else if (clazz == Deque.class) {
                                    if (name == null ||
                                        name.isBlank()) {
                                        return assign(LinkedList.class);
                                    }
                                    break edge;
                                } else if (clazz == Collection.class) {
                                    if (name == null ||
                                        name.isBlank()) {
                                        return ListSpare.INSTANCE;
                                    }
                                    break edge;
                                } else if (clazz == Dictionary.class) {
                                    if (name == null ||
                                        name.isBlank()) {
                                        return assign(Hashtable.class);
                                    }
                                    break edge;
                                } else if (Map.class.isAssignableFrom(clazz)) {
                                    if (clazz == Map.class) {
                                        if (name == null ||
                                            name.isBlank()) {
                                            return MapSpare.INSTANCE;
                                        }
                                        break edge;
                                    } else {
                                        if (clazz == LinkedHashMap.class) {
                                            spare = MapSpare.INSTANCE;
                                        } else {
                                            spare = new MapSpare(clazz);
                                        }
                                    }
                                } else if (Set.class.isAssignableFrom(clazz)) {
                                    if (clazz == Set.class) {
                                        if (name == null ||
                                            name.isBlank()) {
                                            return SetSpare.INSTANCE;
                                        }
                                        break edge;
                                    } else {
                                        if (clazz == HashSet.class) {
                                            spare = SetSpare.INSTANCE;
                                        } else {
                                            spare = new SetSpare(clazz);
                                        }
                                    }
                                } else if (List.class.isAssignableFrom(clazz)) {
                                    if (clazz == List.class) {
                                        if (name == null ||
                                            name.isBlank()) {
                                            return ListSpare.INSTANCE;
                                        }
                                        break edge;
                                    } else {
                                        if (clazz == ArrayList.class) {
                                            spare = ListSpare.INSTANCE;
                                        } else {
                                            spare = new ListSpare(clazz);
                                        }
                                    }
                                } else {
                                    break edge;
                                }
                                major.put(clazz, spare);
                                return spare;
                            }
                        }
                    }
                }
            }

            if (name != null && name.isClass()) {
                Class<?> child;
                ClassLoader cl = null;

                try {
                    cl = Thread.currentThread()
                        .getContextClassLoader();
                } catch (Throwable e) {
                    // Cannot access thread ClassLoader
                }

                if (cl == null) {
                    try {
                        cl = clazz.getClassLoader();
                        if (cl == null) {
                            cl = getClass().getClassLoader();
                        }
                    } catch (Throwable e) {
                        // Cannot access caller ClassLoader
                    }

                    if (cl == null) {
                        try {
                            cl = ClassLoader
                                .getSystemClassLoader();
                        } catch (Throwable e) {
                            // Cannot access system ClassLoader
                        }
                    }
                }

                String space = name.toString();
                try {
                    child = Class.forName(
                        space, false, cl
                    );
                } catch (LinkageError |
                         ClassNotFoundException e) {
                    throw new IllegalStateException(
                        "Not found subclass " +
                            space + " of " + alias, e
                    );
                }

                if (clazz.isAssignableFrom(child)) {
                    Spare<?> spare = major.get(child);
                    search:
                    if (spare == null) {
                        Provider[] PS = PRO;
                        if (PS != null) {
                            for (Provider p : PS) {
                                spare = p.search(
                                    child, this
                                );

                                if (spare != null) {
                                    break search;
                                }
                            }
                        }
                        return null;
                    }

                    minor.putIfAbsent(
                        space.intern(), spare
                    );
                    return spare;
                }

                throw new IllegalStateException(
                    "Received " + space +
                        " is not a subclass of " + alias
                );
            }

            return null;
        }

        @Override
        public String toString() {
            return "plus.kat.Supplier$Vendor@"
                + Integer.toHexString(
                System.identityHashCode(this)
            );
        }
    }
}
