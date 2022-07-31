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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

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
        Thread thread = Thread.currentThread();
        ClassLoader cl = thread.getContextClassLoader();

        service = klass;
        classLoader = cl != null ? cl : ClassLoader.getSystemClassLoader();
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
                        hash = 0;
                        value[count++] = (byte) i;
                    } else if (tail() != '\n') {
                        grow(count + 1);
                        hash = 0;
                        value[count++] = '\n';
                        if (allow(mark)) {
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
                    hash = 0;
                    value[count++] = '\n';
                    if (allow(mark)) {
                        size++;
                    } else {
                        count = mark;
                    }
                }
            }
        }
    }

    /**
     * @since 0.0.3
     */
    private boolean allow(
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

    /**
     * Returns the next element
     *
     * @throws RunCrash if the iteration has no more elements
     */
    @Override
    public T next() {
        int offset = indexOf(
            (byte) '\n', index
        );

        if (offset <= index) {
            throw new RunCrash();
        }

        Class<?> clazz;
        String klass = string(
            index, offset
        );

        size--;
        index = offset + 1;

        try {
            clazz = Class.forName(
                klass, false, classLoader
            );
        } catch (ClassNotFoundException e) {
            throw new RunCrash(
                service.getName() + ": Provider '" + klass + "' not found", e
            );
        }

        if (!service.isAssignableFrom(clazz)) {
            throw new RunCrash(
                service.getName() + ": Provider '" + klass + "' not a subtype"
            );
        }

        try {
            return service.cast(
                clazz.newInstance()
            );
        } catch (Throwable e) {
            throw new RunCrash(
                service.getName() + ": Provider '" + klass + "' could not be instantiated ", e
            );
        }
    }

    /**
     * Returns {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return size != 0;
    }

    /**
     * Unsupported
     */
    @Override
    public KatLoader<T> subSequence(
        int start, int end
    ) {
        throw new RunCrash(
            "Unsupported Operation"
        );
    }
}
