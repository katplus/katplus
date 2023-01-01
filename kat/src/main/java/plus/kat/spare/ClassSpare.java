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

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.5
 */
@SuppressWarnings("rawtypes")
public class ClassSpare extends Property<Class> {

    public static final ClassSpare
        INSTANCE = new ClassSpare();

    public ClassSpare() {
        super(Class.class);
    }

    @Override
    public String getSpace() {
        return "$";
    }

    @Override
    public Class read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) throws IOException {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread()
                .getContextClassLoader();
        } catch (Throwable e) {
            // Cannot access thread ClassLoader
        }

        if (cl == null) {
            try {
                cl = flag.getClass().getClassLoader();
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

        try {
            return Class.forName(
                chain.toString(), false, cl
            );
        } catch (ClassNotFoundException e) {
            throw new IOException(
                "Unable to parse chain as Class", e
            );
        }
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.emit(
            ((Class) value).getName()
        );
    }

    @Override
    public Class cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (object instanceof Class) {
            return (Class) object;
        }

        if (object instanceof Type) {
            return Space.wipe((Type) object);
        }

        if (object instanceof CharSequence) {
            ClassLoader cl = null;
            try {
                cl = Thread.currentThread()
                    .getContextClassLoader();
            } catch (Throwable e) {
                // Cannot access thread ClassLoader
            }

            if (cl == null) {
                try {
                    cl = object.getClass().getClassLoader();
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

            try {
                return Class.forName(
                    object.toString(), false, cl
                );
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        throw new IllegalStateException(
            object + " cannot be converted to " + klass
        );
    }
}
