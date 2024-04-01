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
package plus.kat.flow;

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

/**
 * @author kraity
 * @since 0.0.3
 */
public final class KatLoader<T> implements Iterator<T>, Closeable {

    private static final String
        PREFIX = "META-INF/services/";
    private static final byte LF = '\n';

    private int size;
    private int left;
    private int count;
    private byte[] buffer;

    private final Class<T> service;
    private final ClassLoader classLoader;
    private final AccessControlContext accessControl;

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
        accessControl = System.getSecurityManager()
            == null ? null : AccessController.getContext();
    }

    public KatLoader(
        Class<T> klass,
        ClassLoader loader
    ) {
        service = klass;
        classLoader = loader;
        accessControl = System.getSecurityManager()
            == null ? null : AccessController.getContext();
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
    public void load()
        throws IOException {
        load(
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
        Enumeration<URL> source = classLoader
            .getResources(PREFIX + name);

        if (source.hasMoreElements()) {
            if (buffer != null) {
                size = left = 0;
            } else {
                left = 0;
                buffer = new byte[256];
            }
            do {
                read(
                    source.nextElement()
                );
            } while (
                source.hasMoreElements()
            );
        }
    }

    /**
     * Returns the next element in the {@link KatLoader}
     *
     * @throws IllegalAccessError        If the iteration has no more elements
     * @throws AccessControlException    If the katLoader has no permission
     * @throws ServiceConfigurationError If the provider class is loaded with errors
     */
    public T next() {
        if (size <= 0) {
            throw new LinkageError(
                "No more instances"
            );
        }

        if (accessControl == null) {
            return build();
        }

        return AccessController.doPrivileged(
            (PrivilegedAction<T>) this::build, accessControl
        );
    }

    /**
     * Returns true if the loader has more elements
     */
    @Override
    public boolean hasNext() {
        return size > 0;
    }

    /**
     * Constructs the next element in the {@link KatLoader}
     *
     * @throws ServiceConfigurationError If the provider class is loaded with errors
     */
    @SuppressWarnings({
        "unchecked", "deprecation"
    })
    private T build() throws Error {
        int iv = left;
        byte[] it = buffer;

        int ix = iv, l = count;
        for (; ix < l; ix++) {
            if (it[ix] == LF) {
                break;
            }
        }

        String name;
        if (ix > iv) {
            size--;
            left = ix + 1;
            name = new String(
                it, 0, iv, ix - iv
            );
        } else {
            throw new IllegalAccessError(
                "No more names: " + ix + " <= " + iv
            );
        }

        Class<?> child, parent = service;
        try {
            child = Class.forName(
                name, false, classLoader
            );
        } catch (ClassNotFoundException e) {
            throw new ServiceConfigurationError(
                parent + ": " + name + " not found", e
            );
        }

        if (parent.isAssignableFrom(child)) {
            try {
                return (T) child.newInstance();
            } catch (Throwable e) {
                throw new ServiceConfigurationError(
                    parent + ": failed to create " + name, e
                );
            }
        } else {
            throw new ServiceConfigurationError(
                parent + ": " + name + " not a subtype of " + parent
            );
        }
    }

    /**
     * Reads the class name from the specified source
     *
     * @param url the specified url
     * @throws IOException If an I/O exception occurs
     */
    private void read(URL url) throws IOException {
        int block = 0;
        byte[] it = buffer;

        try (InputStream in = url.openStream()) {
            Scope:
            while (true) {
                int i = in.read();
                if (i > 0x20) {
                    if (count == it.length) {
                        System.arraycopy(
                            it, 0, buffer = it = new
                                byte[count * 2], 0, count
                        );
                    }
                    it[count++] = (byte) i;
                    continue;
                }

                if (i == 0x20) {
                    continue;
                }

                int e = count - 1;
                if (e > -1 && it[e] == LF) {
                    if (i == -1) break;
                } else {
                    if (count == it.length) {
                        System.arraycopy(
                            it, 0, buffer = it = new
                                byte[count * 2], 0, count
                        );
                    }
                    it[count++] = LF;
                    if (block != 0) {
                        int max = count;
                        int len = max - block,
                            lim = block - len;

                        byte data = it[block];
                        for (int k = 0; k <= lim; k++) {
                            if (it[k] != data) {
                                continue;
                            }

                            int i1 = k, i2 = block;
                            while (++i2 < max) {
                                if (it[++i1] != it[i2]) break;
                            }
                            if (i2 == max) {
                                count = block;
                                if (i == -1) {
                                    break Scope;
                                } else {
                                    continue Scope;
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

    /**
     * Closes the resources of this {@link KatLoader}
     */
    @Override
    public void close() {
        size = 0;
        buffer = null;
    }
}
