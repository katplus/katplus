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
            .getResources(
                PREFIX + name
            );

        while (configs.hasMoreElements()) {
            URL url = configs.nextElement();
            try (InputStream in = url.openStream()) {
                int mark = 0;
                while (true) {
                    int i = in.read();
                    if (i == -1) {
                        break;
                    }

                    if (i == 0x20) {
                        continue;
                    }

                    if (i > 0x20) {
                        grow(count + 1);
                        star = 0;
                        value[count++] = (byte) i;
                    } else if (tail() != '\n') {
                        grow(count + 1);
                        star = 0;
                        value[count++] = '\n';
                        if (permit(mark)) {
                            size++;
                            mark = count;
                        } else {
                            count = mark;
                        }
                    }
                }

                int i = count - 1;
                if (i > 0 && value[i] != '\n') {
                    grow(count + 1);
                    star = 0;
                    value[count++] = '\n';
                    if (permit(mark)) {
                        size++;
                    } else {
                        count = mark;
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

    /**
     * Returns the status of collect permit
     */
    protected boolean permit(
        int mark
    ) {
        if (mark == 0) {
            return true;
        }

        byte[] it = value;
        byte fir = it[mark];

        int len = count - mark;
        int lim = mark - len;

        for (int o = 0; o <= lim; o++) {
            if (it[o] != fir) {
                continue;
            }

            int o1 = o, o2 = mark;
            while (++o2 < count) {
                if (it[++o1] != it[o2]) break;
            }
            if (o2 == count) {
                return false;
            }
        }

        return true;
    }
}
