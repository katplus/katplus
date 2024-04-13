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

import plus.kat.*;
import plus.kat.Flow;

import plus.kat.flow.*;
import plus.kat.lang.*;

import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.*;
import java.math.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import static plus.kat.Algo.*;
import static plus.kat.lang.Uniform.*;
import static plus.kat.spare.ClassSpare.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public interface Supplier extends Context {
    /**
     * Decodes the kat text and converts the result to {@link T}
     *
     * @param type the specified type of {@link T}
     * @param text the specified flow to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws NullPointerException     If the specified arguments is null
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    default <T> T read(
        @NotNull Type type,
        @NotNull Flow text
    ) throws IOException {
        return solve(
            KAT, type, text
        );
    }

    /**
     * Encodes the specified value to kat {@link Chan} with the flags
     *
     * @param value the specified value to be encoded
     * @param flags the specified flags for serialize
     * @throws IOException              If an I/O error occurs
     * @throws IllegalArgumentException If no chan available is found
     */
    @NotNull
    default Chan write(
        @Nullable Object value, long flags
    ) throws IOException {
        return telex(
            KAT, value, flags
        );
    }

    /**
     * Decodes the xml text and converts the result to {@link T}
     *
     * @param type the specified type of {@link T}
     * @param text the specified flow to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws NullPointerException     If the specified arguments is null
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    default <T> T down(
        @NotNull Type type,
        @NotNull Flow text
    ) throws IOException {
        return solve(
            DOC, type, text
        );
    }

    /**
     * Encodes the specified value to xml {@link Chan} with the flags
     *
     * @param value the specified value to be encoded
     * @param flags the specified flags for serialize
     * @throws IOException              If an I/O error occurs
     * @throws IllegalArgumentException If no chan available is found
     */
    @NotNull
    default Chan mark(
        @Nullable Object value, long flags
    ) throws IOException {
        return telex(
            DOC, value, flags
        );
    }

    /**
     * Decodes the json text and converts the result to {@link T}
     *
     * @param type the specified type of {@link T}
     * @param text the specified flow to be decoded
     * @throws ClassCastException       If {@link T} is not an instance of the type
     * @throws NullPointerException     If the specified arguments is null
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @Nullable
    default <T> T parse(
        @NotNull Type type,
        @NotNull Flow text
    ) throws IOException {
        return solve(
            JSON, type, text
        );
    }

    /**
     * Encodes the specified value to json {@link Chan} with the flags
     *
     * @param value the specified value to be encoded
     * @param flags the specified flags for serialize
     * @throws IOException              If an I/O error occurs
     * @throws IllegalArgumentException If no chan available is found
     */
    @NotNull
    default Chan serial(
        @Nullable Object value, long flags
    ) throws IOException {
        return telex(
            JSON, value, flags
        );
    }

    /**
     * Returns the default supplier
     */
    @NotNull
    static Supplier ins() {
        return Vendor.INS;
    }

    /**
     * Returns an object based on no arguments,
     * otherwise an exception will be thrown directly
     *
     * @param type the specified type for lookup
     * @return {@link T}, it's not null
     * @throws ClassCastException       If {@link T} type is not the type
     * @throws NullPointerException     If the specified argument is null
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the type is found
     */
    @NotNull
    default <T> T apply(
        @NotNull Type type
    ) {
        Spare<T> spare = assign(type);

        if (spare != null) {
            T bean = spare.apply();
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
     * Returns an object based on the arguments,
     * otherwise an exception will be thrown directly
     *
     * @param type the specified type for lookup
     * @param args the specified args of constructor
     * @return {@link T}, it's not null
     * @throws ClassCastException       If {@link T} klass is not the klass
     * @throws NullPointerException     If the specified arguments is null
     * @throws IllegalStateException    If parsing fails or the result is null
     * @throws IllegalArgumentException If no spare available for the klass is found
     */
    @NotNull
    default <T> T apply(
        @NotNull Type type,
        @NotNull Object... args
    ) {
        Spare<T> spare = assign(type);

        if (spare != null) {
            T bean = spare.apply(args);
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
     * Decodes the specified text and converts the result to {@link T}
     *
     * @param algo the specified algo for solve
     * @param type the specified type of {@link T}
     * @param text the specified flow to be decoded
     * @throws NullPointerException     If the specified arguments is null
     * @throws IOException              If an I/O error or parsing error occurs
     * @throws IllegalArgumentException If no spare available for the klass is found
     */
    @Nullable
    default <T> T solve(
        @NotNull Algo algo,
        @NotNull Type type,
        @NotNull Flow text
    ) throws IOException {
        if (algo == null ||
            type == null ||
            text == null) {
            throw new NullPointerException();
        } else {
            try (Parser op = Parser.apply()) {
                op.setType(type);
                op.setContext(this);
                return op.solve(algo, text);
            }
        }
    }

    /**
     * Encodes the specified value to target {@link Chan} with the flags
     *
     * @param algo  the specified algo for telex
     * @param flags the specified flags for serialize
     * @throws IOException If an I/O error or analysis error occurs
     */
    @NotNull
    default Chan telex(
        @NotNull Algo algo,
        @Nullable Object value, @NotNull long flags
    ) throws IOException {
        Chan chan;
        switch (algo.hashCode()) {
            case kat: {
                chan = new Kat(flags, this);
                break;
            }
            case doc: {
                chan = new Doc(flags, this);
                break;
            }
            case json: {
                chan = new Json(flags, this);
                break;
            }
            default: {
                throw new IOException(
                    "Not supported " + algo
                );
            }
        }
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
     * @author kraity
     * @since 0.0.6
     */
    @SuppressWarnings("unchecked")
    class Vendor implements Supplier, Provider {

        public static final Vendor INS;
        public static final Provider[] PRO;

        static {
            Vendor ins = INS = new Vendor();
            ins.minor.put("", Object.class);
            ins.minor.put("[", ArrayList.class);
            ins.minor.put("{", LinkedHashMap.class);
            ins.minor.put("<", LinkedHashMap.class);
            ins.minor.put("\"", String.class);
            ins.minor.put("Any", Object.class);
            ins.minor.put("String", String.class);
            ins.minor.put("Int", Integer.class);
            ins.minor.put("Long", Long.class);
            ins.minor.put("Boolean", Boolean.class);
            ins.minor.put("Float", Float.class);
            ins.minor.put("Double", Double.class);
            ins.minor.put("Byte", Byte.class);
            ins.minor.put("Char", Character.class);
            ins.minor.put("Short", Short.class);
            ins.minor.put("Map", LinkedHashMap.class);
            ins.minor.put("Set", HashSet.class);
            ins.minor.put("List", ArrayList.class);
            ins.minor.put("Array", Object[].class);

            try (KatLoader<Provider> loader =
                     new KatLoader<>(Provider.class)) {
                loader.load(
                    "plus.kat.spare.Provider"
                );

                if (loader.hasNext()) {
                    final int s = loader.size() + 1;
                    Provider[] RS = new Provider[s];

                    int m = 0;
                    while (loader.hasNext()) {
                        RS[m++] = loader.next();
                    }

                    int i = 0, n = 0;
                    Provider v = null;

                    try {
                        while (i < m) {
                            v = RS[i++];
                            if (v.alive(ins)) {
                                RS[n++] = v;
                            }
                        }
                    } catch (Exception e) {
                        throw new Error(
                            "Failed to activate " + v, e
                        );
                    }

                    RS[n++] = ins;
                    if (n < s) {
                        Provider[] rs = new Provider[n];
                        System.arraycopy(
                            RS, 0, RS = rs, 0, n
                        );
                    }

                    if (n > 1) {
                        i = 0;
                        m = n - 1;
                        do {
                            n = 0;
                            v = null;
                            while (n < m - i) {
                                if (RS[n].grade() <
                                    RS[++n].grade()) {
                                    v = RS[n];
                                    RS[n] = RS[n - 1];
                                    RS[n - 1] = v;
                                }
                            }
                        } while (
                            v != null && ++i < m
                        );
                    }
                    PRO = RS;
                } else {
                    PRO = new Provider[]{ins};
                }
            } catch (Exception e) {
                throw new Error(
                    "Failed to load the external providers", e
                );
            }
        }

        /**
         * Internal mapping class table
         */
        protected final ConcurrentHashMap
            <Object, Class<?>> minor;

        /**
         * Internal mapping spare table
         */
        protected final ConcurrentHashMap
            <Object, Spare<?>> major;

        /**
         * Constructs a supplier with default config
         */
        public Vendor() {
            this(
                SUPPLIER_BUFFER, SUPPLIER_CAPACITY
            );
        }

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

        public void onCreate() {
            // Nothing
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
            return major.put(
                type, spare
            );
        }

        @Override
        public Spare<?> revoke(
            @NotNull Type type,
            @Nullable Spare<?> spare
        ) {
            if (spare == null) {
                spare = major.remove(type);
            } else {
                if (major.remove(type, spare)) {
                    return spare;
                }
                return null;
            }

            if (type instanceof Class) {
                Magus magus = ((Class<?>) type)
                    .getAnnotation(Magus.class);
                if (magus != null) {
                    for (String name : magus.value()) {
                        minor.remove(
                            name, type
                        );
                    }
                }
            }

            return spare;
        }

        @Override
        public <T> Spare<T> assign(
            @Nullable Type type
        ) {
            if (type == null) {
                type = Object.class;
            }

            Spare<?> spare = major.get(type);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            Provider[] PS = PRO;
            if (PS != null) {
                type = typeOf(type);
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
            @Nullable Type type,
            @Nullable Space name
        ) {
            if (type == null) {
                type = Object.class;
            }

            Spare<?> spare = major.get(type);

            if (spare != null) {
                return (Spare<T>) spare;
            }

            Provider[] PS = PRO;
            if (PS != null) {
                type = typeOf(type);
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

        public Spare<?> search(
            @NotNull int mode,
            @Nilable Space name,
            @NotNull Class<?> clazz
        ) {
            Spare<?> spare;
            switch (mode) {
                case 0:
                    break;
                case 1:
                    if (clazz == Binary.class) {
                        spare = BinarySpare.INSTANCE;
                    } else if (clazz == ByteSequence.class) {
                        if (name == null ||
                            name.isBlank()) {
                            return BinaryifySpare.INSTANCE;
                        }
                        break;
                    } else if (clazz == Alias.class ||
                        clazz == Space.class || clazz == Value.class) {
                        spare = new BinaryifySpare(clazz);
                    } else {
                        break;
                    }
                    major.put(clazz, spare);
                    return spare;
                case 2:
                    String alias = clazz.getName();
                    switch (alias.charAt(5)) {
                        // java.io
                        case 'i': {
                            if (clazz == File.class) {
                                spare = FileSpare.INSTANCE;
                            } else {
                                break;
                            }
                            major.put(clazz, spare);
                            return spare;
                        }
                        // java.net
                        case 'n': {
                            if (clazz == URI.class) {
                                spare = URISpare.INSTANCE;
                            } else if (clazz == URL.class) {
                                spare = URLSpare.INSTANCE;
                            } else {
                                break;
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
                                break;
                            }
                            major.put(clazz, spare);
                            return spare;
                        }
                        // java.lang
                        case 'l': {
                            if (clazz == Object.class) {
                                return ObjectSpare.INSTANCE;
                            } else if (clazz == String.class) {
                                spare = StringSpare.INSTANCE;
                            } else if (clazz == Integer.class) {
                                spare = IntSpare.INSTANCE;
                            } else if (clazz == Long.class) {
                                spare = LongSpare.INSTANCE;
                            } else if (clazz == Void.class) {
                                spare = VoidSpare.INSTANCE;
                            } else if (clazz == Byte.class) {
                                spare = ByteSpare.INSTANCE;
                            } else if (clazz == Short.class) {
                                spare = ShortSpare.INSTANCE;
                            } else if (clazz == Float.class) {
                                spare = FloatSpare.INSTANCE;
                            } else if (clazz == Double.class) {
                                spare = DoubleSpare.INSTANCE;
                            } else if (clazz == Boolean.class) {
                                spare = BooleanSpare.INSTANCE;
                            } else if (clazz == Character.class) {
                                spare = CharSpare.INSTANCE;
                            } else if (clazz == Class.class) {
                                spare = ClassSpare.INSTANCE;
                            } else if (clazz == Number.class) {
                                if (name == null ||
                                    name.isBlank()) {
                                    return NumberSpare.INSTANCE;
                                }
                                break;
                            } else if (clazz == Iterable.class) {
                                if (name == null ||
                                    name.isBlank()) {
                                    return ListSpare.INSTANCE;
                                }
                                break;
                            } else if (clazz == CharSequence.class) {
                                if (name == null ||
                                    name.isBlank()) {
                                    return StringifySpare.INSTANCE;
                                }
                                break;
                            } else if (clazz == StringBuffer.class) {
                                spare = new StringifySpare(clazz);
                            } else if (clazz == StringBuilder.class) {
                                spare = new StringifySpare(clazz);
                            } else {
                                return null;
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
                                        spare = new MapSpare(clazz, this);
                                    } else if (Set.class.isAssignableFrom(clazz)) {
                                        spare = new SetSpare(clazz, this);
                                    } else if (List.class.isAssignableFrom(clazz)) {
                                        spare = new ListSpare(clazz, this);
                                    } else {
                                        break;
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
                                    } else if (clazz == TimeZone.class) {
                                        spare = TimeZoneSpare.INSTANCE;
                                    } else if (clazz == Calendar.class
                                        || clazz == GregorianCalendar.class) {
                                        spare = CalendarSpare.INSTANCE;
                                    } else if (clazz == Queue.class) {
                                        if (name == null ||
                                            name.isEntry()) {
                                            return assign(LinkedList.class);
                                        }
                                        break;
                                    } else if (clazz == Deque.class) {
                                        if (name == null ||
                                            name.isEntry()) {
                                            return assign(LinkedList.class);
                                        }
                                        break;
                                    } else if (clazz == Collection.class) {
                                        if (name == null ||
                                            name.isEntry()) {
                                            return ListSpare.INSTANCE;
                                        }
                                        break;
                                    } else if (clazz == Dictionary.class) {
                                        if (name == null ||
                                            name.isEntry()) {
                                            return assign(Hashtable.class);
                                        }
                                        break;
                                    } else if (Map.class.isAssignableFrom(clazz)) {
                                        if (clazz == Map.class) {
                                            if (name == null ||
                                                name.isEntry()) {
                                                return MapSpare.INSTANCE;
                                            }
                                            break;
                                        } else {
                                            if (clazz == LinkedHashMap.class) {
                                                spare = MapSpare.INSTANCE;
                                            } else {
                                                spare = new MapSpare(clazz, this);
                                            }
                                        }
                                    } else if (Set.class.isAssignableFrom(clazz)) {
                                        if (clazz == Set.class) {
                                            if (name == null ||
                                                name.isEntry()) {
                                                return SetSpare.INSTANCE;
                                            }
                                            break;
                                        } else {
                                            if (clazz == HashSet.class) {
                                                spare = SetSpare.INSTANCE;
                                            } else {
                                                spare = new SetSpare(clazz, this);
                                            }
                                        }
                                    } else if (List.class.isAssignableFrom(clazz)) {
                                        if (clazz == List.class) {
                                            if (name == null ||
                                                name.isEntry()) {
                                                return ListSpare.INSTANCE;
                                            }
                                            break;
                                        } else {
                                            if (clazz == ArrayList.class) {
                                                spare = ListSpare.INSTANCE;
                                            } else {
                                                spare = new ListSpare(clazz, this);
                                            }
                                        }
                                    } else {
                                        return null;
                                    }
                                    major.put(clazz, spare);
                                    return spare;
                                }
                            }
                        }
                    }
                    break;
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

                String ns = name.toString();
                try {
                    child = Class.forName(
                        ns, false, cl
                    );
                } catch (LinkageError |
                         ClassNotFoundException e) {
                    throw new IllegalStateException(
                        "Not found subclass " +
                            ns + " of " + clazz, e
                    );
                }

                if (clazz.isAssignableFrom(child)) {
                    spare = major.get(child);
                    if (spare != null) {
                        minor.putIfAbsent(
                            ns.intern(), child
                        );
                        return spare;
                    }

                    Provider[] PS = PRO;
                    if (PS != null) {
                        for (Provider p : PS) {
                            spare = p.search(
                                child, this
                            );

                            if (spare != null) {
                                minor.putIfAbsent(
                                    ns.intern(), child
                                );
                                return spare;
                            }
                        }
                    }
                    return null;
                }

                throw new IllegalStateException(
                    "Received " + ns + " is not a subclass of " + clazz
                );
            }

            return null;
        }

        @Override
        public Spare<?> search(
            @NotNull Type type,
            @Nilable Space name,
            @NotNull Context context
        ) {
            Class<?> clazz;
            Spare<?> spare;

            if (type instanceof Class) {
                clazz = (Class<?>) type;
            } else {
                while (true) {
                    if (type instanceof ParameterizedType) {
                        type = ((ParameterizedType) type).getRawType();
                        if (type instanceof Class) {
                            spare = major.get(type);
                            if (spare == null) {
                                clazz = (Class<?>) type;
                                break;
                            } else {
                                return spare;
                            }
                        }
                        continue;
                    }

                    if (type instanceof TypeVariable) {
                        type = ((TypeVariable<?>) type).getBounds()[0];
                        if (type instanceof Class) {
                            spare = major.get(type);
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
                            spare = major.get(type);
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
                        int dim = 1;
                        while (true) {
                            if (type instanceof Class) {
                                clazz = (Class<?>) type;
                                break;
                            }

                            if (type instanceof ParameterizedType) {
                                type = ((ParameterizedType) type).getRawType();
                                continue;
                            }

                            if (type instanceof TypeVariable) {
                                type = ((TypeVariable<?>) type).getBounds()[0];
                                continue;
                            }

                            if (type instanceof WildcardType) {
                                type = ((WildcardType) type).getUpperBounds()[0];
                                continue;
                            }

                            if (type instanceof GenericArrayType) {
                                dim++;
                                type = ((GenericArrayType) type).getGenericComponentType();
                                continue;
                            }

                            return null;
                        }

                        if (dim == 1) {
                            if (clazz == Object.class) {
                                return assign(Object[].class);
                            }
                            if (clazz == String.class) {
                                return assign(String[].class);
                            }
                        }

                        return assign(
                            Array.newInstance(clazz, new int[dim]).getClass()
                        );
                    }

                    return null;
                }
            }

            if (name != null) {
                Class<?> alias = minor.get(name);
                if (alias != null &&
                    alias != clazz &&
                    clazz.isAssignableFrom(alias)) {
                    return assign(alias);
                }
            }

            String alias = clazz.getName();
            switch (alias.charAt(0)) {
                case 0x6A: {
                    if (alias.startsWith("java.")) {
                        return search(2, name, clazz);
                    }
                    if (alias.startsWith("jdk.") ||
                        alias.startsWith("javax.")) {
                        return null;
                    }
                    break;
                }
                case 0x73: {
                    if (alias.startsWith("sun.") ||
                        alias.startsWith("scala.")) {
                        return null;
                    }
                    break;
                }
                case 0x6B: {
                    if (alias.startsWith("kotlin.") ||
                        alias.startsWith("kotlinx.")) {
                        return search(3, name, clazz);
                    }
                    break;
                }
                case 0x61: {
                    if (alias.startsWith("android.") ||
                        alias.startsWith("androidx.")) {
                        return search(4, name, clazz);
                    }
                    break;
                }
                case 0x5B: {
                    if (clazz == Object[].class) {
                        spare = ArraySpare.INSTANCE;
                    } else if (clazz == byte[].class) {
                        spare = ByteArraySpare.INSTANCE;
                    } else if (clazz == char[].class) {
                        spare = CharArraySpare.INSTANCE;
                    } else {
                        spare = new ArraySpare(clazz, this);
                    }
                    major.put(clazz, spare);
                    return spare;
                }
            }

            if (clazz.isPrimitive()) {
                if (clazz == int.class) {
                    spare = IntSpare.INSTANCE;
                } else if (clazz == long.class) {
                    spare = LongSpare.INSTANCE;
                } else if (clazz == byte.class) {
                    spare = ByteSpare.INSTANCE;
                } else if (clazz == char.class) {
                    spare = CharSpare.INSTANCE;
                } else if (clazz == short.class) {
                    spare = ShortSpare.INSTANCE;
                } else if (clazz == float.class) {
                    spare = FloatSpare.INSTANCE;
                } else if (clazz == double.class) {
                    spare = DoubleSpare.INSTANCE;
                } else if (clazz == boolean.class) {
                    spare = BooleanSpare.INSTANCE;
                } else {
                    spare = VoidSpare.INSTANCE;
                }
                major.put(clazz, spare);
                return spare;
            }

            String space = alias;
            String[] spaces = null;

            Scope:
            {
                Magus magus = clazz.getAnnotation(Magus.class);
                if (magus == null) {
                    if (clazz.isInterface() ||
                        Coder.class.isAssignableFrom(clazz) ||
                        Entity.class.isAssignableFrom(clazz) ||
                        Throwable.class.isAssignableFrom(clazz)) {
                        return search(0, name, clazz);
                    }
                    if (ByteSequence.class.isAssignableFrom(clazz)) {
                        return search(1, name, clazz);
                    }
                } else {
                    Class<?> agent = magus.agent();
                    String[] names = magus.value();
                    if (names.length != 0) {
                        space = (spaces = names)[0];
                    }

                    if (agent != void.class && agent != clazz) {
                        // check to see if it's a proxy class
                        if (clazz.isAssignableFrom(agent)) {
                            spare = assign(agent);
                            if (spare != null) {
                                major.putIfAbsent(clazz, spare);
                                return spare;
                            } else {
                                return search(0, name, clazz);
                            }
                        }

                        // check to see if it's a spare class
                        if (Spare.class.isAssignableFrom(agent)) {
                            // double-checking
                            spare = major.get(clazz);

                            if (spare != null) {
                                return spare;
                            }

                            Class<?>[] cs = null;
                            Constructor<?> ctor = null;

                            int mx = -1;
                            for (Constructor<?> cto : agent
                                .getDeclaredConstructors()) {
                                Class<?>[] cls =
                                    cto.getParameterTypes();

                                int i = 0,
                                    j = cls.length;
                                while (true) {
                                    if (i == j) {
                                        if (mx < j) {
                                            mx = j;
                                            cs = cls;
                                            ctor = cto;
                                        }
                                    } else {
                                        Class<?> m = cls[i++];
                                        if (m == Class.class ||
                                            m == String.class ||
                                            m == Context.class) {
                                            continue;
                                        }
                                    }
                                    break;
                                }
                            }

                            if (ctor != null) try {
                                if (!ctor.isAccessible()) {
                                    ctor.setAccessible(true);
                                }

                                Object[] args = null;
                                if (mx != 0) {
                                    args = new Object[mx];
                                    for (int i = 0; i < mx; i++) {
                                        Class<?> m = cs[i];
                                        if (m == Class.class) {
                                            args[i] = clazz;
                                        } else if (m == String.class) {
                                            args[i] = space;
                                        } else if (m == Context.class) {
                                            args[i] = Vendor.this;
                                        }
                                    }
                                }

                                spare = ((Spare<?>) ctor.newInstance(args));
                                break Scope;
                            } catch (Exception e) {
                                throw new IllegalStateException(
                                    "Failed to build the " +
                                        alias + "'s agent coder: " + agent, e
                                );
                            }
                        }

                        throw new IllegalStateException(
                            "Failed to resolve "
                                + alias + "'s agent: " + agent
                        );
                    }
                }

                // double-checking
                spare = major.get(clazz);

                if (spare != null) {
                    return spare;
                }

                if (clazz.isInterface()) {
                    spare = new ProxySpare(
                        space, clazz, this
                    );
                    break Scope;
                }

                // exclude abstract class
                if ((clazz.getModifiers() & 0x400) != 0) {
                    return search(0, name, clazz);
                }

                switch (clazz.getSuperclass().getName()) {
                    case "java.lang.Enum":
                        spare = new EnumSpare<>(space, clazz, this);
                        break;
                    case "java.lang.Record":
                        spare = new RecordSpare<>(space, clazz, this);
                        break;
                    case "java.lang.reflect.Proxy":
                        spare = new ProxySpare(space, clazz, this);
                        break;
                    default:
                        spare = new ReflectSpare<>(space, clazz, this);
                        break;
                }
            }

            major.put(clazz, spare);
            if (spaces != null) {
                for (String ns : spaces) {
                    Class<?> cs = minor.putIfAbsent(ns, clazz);
                    if (cs == null || cs == clazz) {
                        continue;
                    }
                    throw new IllegalStateException(
                        "Mixed class of `" + ns + "` already exists."
                            + " Actual: " + cs + ", Expected: " + clazz
                    );
                }
            }
            return spare;
        }

        public void onDestroy() {
            minor.clear();
            major.clear();
        }
    }
}
