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

import plus.kat.crash.*;
import plus.kat.kernel.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author kraity
 * @since 0.0.3
 */
public class KatLoader<T> extends Chain implements Iterator<T> {

    private static final String
        PREFIX = "META-INF/services/";
    private static final byte n = '\n';

    protected int size;
    protected int index;

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
            URL url = configs.nextElement();
            try (InputStream in = url.openStream()) {
                Stream:
                for (int block = 0; ; ) {
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

                    if (get(-1, n) == n) {
                        if (i == -1) break;
                    } else {
                        chain(n);
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
     * @throws Collapse                  If the iteration has no more elements
     * @throws ServiceConfigurationError If the provider class is loaded with errors
     */
    @SuppressWarnings("deprecation")
    public T next() {
        if (--size < 0) {
            throw new Collapse(
                "No more elements"
            );
        }

        int start = index,
            offset = indexOf(
                (byte) '\n', start
            );

        int length = offset - start;
        if (length <= 0) {
            throw new Collapse(
                offset + " <= " + start
            );
        }

        index = offset + 1;
        String name = new String(
            value, 0, start, length
        );

        Class<?> clazz;
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
}
