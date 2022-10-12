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
package plus.kat.utils;

import plus.kat.anno.*;
import plus.kat.kernel.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author kraity
 * @since 0.0.3
 */
public class KatLoader<T> implements Iterator<T>, Closeable {

    private static final String
        PREFIX = "META-INF/services/";
    private static final byte LF = '\n';

    private int size;
    private Parser parser;

    protected final Class<T> service;
    protected final ClassLoader classLoader;

    public KatLoader(
        Class<T> klass
    ) {
        service = klass;
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread()
                .getContextClassLoader();
        } catch (Throwable e) {
            // Cannot access thread ClassLoader
        }

        if (cl == null) {
            try {
                cl = klass.getClassLoader();
            } catch (Throwable e) {
                // Cannot access caller ClassLoader
            }

            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable e) {
                    // Cannot access system ClassLoader
                }
            }
        }
        classLoader = cl;
    }

    public KatLoader(
        Class<T> klass,
        ClassLoader loader
    ) {
        service = klass;
        classLoader = loader;
    }

    /**
     * Returns the length of elements
     */
    public int size() {
        return size;
    }

    /**
     * Creates a new service loader for the given service type
     *
     * @throws IOException If an I/O exception occurs
     */
    public void load() throws IOException {
        this.load(
            service.getName()
        );
    }

    /**
     * Creates a new service loader for the given service type
     *
     * @throws IOException If an I/O exception occurs
     */
    public void load(
        String name
    ) throws IOException {
        Enumeration<URL> configs = classLoader
            .getResources(PREFIX + name);

        while (configs.hasMoreElements()) {
            Parser it = parser;
            if (it == null) {
                parser = it = new Parser();
            }
            size += it.read(
                configs.nextElement()
            );
        }
    }

    /**
     * Returns true if the loader has more elements
     */
    @Override
    public boolean hasNext() {
        return size != 0;
    }

    /**
     * Returns the next element in the {@link KatLoader}
     *
     * @throws IllegalAccessError        If the iteration has no more elements
     * @throws ServiceConfigurationError If the provider class is loaded with errors
     */
    @NotNull
    public T next() {
        if (--size < 0) {
            throw new IllegalAccessError(
                "No more elements"
            );
        }

        Class<?> clazz;
        String name = parser.next();

        try {
            clazz = Class.forName(
                name, false, classLoader
            );
        } catch (ClassNotFoundException e) {
            throw new ServiceConfigurationError(
                service.getName() + ": Provider '" + name + "' not found", e
            );
        }

        if (!service.isAssignableFrom(clazz)) {
            throw new ServiceConfigurationError(
                service.getName() + ": Provider '" + name + "' not a subtype"
            );
        }

        try {
            return service.cast(
                clazz.newInstance()
            );
        } catch (Throwable e) {
            throw new ServiceConfigurationError(
                service.getName() + ": Provider '" + name + "' could not be instantiated ", e
            );
        }
    }

    /**
     * Close this {@link KatLoader}
     */
    @Override
    public void close() {
        size = 0;
        Parser it = parser;
        if (it != null) {
            it.close();
            parser = null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.5
     */
    public static class Parser extends Dram {

        private int index;

        /**
         * Reads the class name from the specified source
         *
         * @param url the specified url
         * @throws IOException If an I/O exception occurs
         */
        public int read(
            @NotNull URL url
        ) throws IOException {
            int size = 0, block = 0;
            try (InputStream in = url.openStream()) {
                Stream:
                while (true) {
                    int i = in.read();
                    if (i > 0x20) {
                        chain(
                            (byte) i
                        );
                        continue;
                    }

                    if (i == 0x20) {
                        continue;
                    }

                    if (get(-1, LF) == LF) {
                        if (i == -1) break;
                    } else {
                        chain(LF);
                        if (block != 0) {
                            byte[] it = value;
                            byte data = it[block];

                            int l = count;
                            int len = l - block,
                                lim = block - len;

                            for (int k = 0; k <= lim; k++) {
                                if (it[k] != data) {
                                    continue;
                                }

                                int i1 = k, i2 = block;
                                while (++i2 < l) {
                                    if (it[++i1] != it[i2]) break;
                                }
                                if (i2 == l) {
                                    count = block;
                                    if (i == -1) {
                                        break Stream;
                                    } else {
                                        continue Stream;
                                    }
                                }
                            }
                        }
                        size++;
                        if (i == -1) {
                            break;
                        } else {
                            block = count;
                        }
                    }
                }
            }
            return size;
        }

        /**
         * Returns the next class name
         *
         * @throws IllegalAccessError If the parser has no more name
         */
        @SuppressWarnings(
            "deprecation"
        )
        public String next() {
            int start = index,
                point = indexOf(
                    (byte) '\n', start
                );

            int size = point - start;
            if (size <= 0) {
                throw new IllegalAccessError(
                    point + " <= " + start
                );
            }

            index = point + 1;
            return new String(
                value, 0, start, size
            );
        }
    }
}
