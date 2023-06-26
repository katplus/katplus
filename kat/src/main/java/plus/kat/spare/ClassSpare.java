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
import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.6
 */
@SuppressWarnings("rawtypes")
public class ClassSpare extends BaseSpare<Class> {

    public static final ClassSpare
        INSTANCE = new ClassSpare();

    public ClassSpare() {
        super(Class.class);
    }

    @Override
    public String getSpace() {
        return "Class";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public Class read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (value.isClass()) {
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
                    value.toString(), false, cl
                );
            } catch (ClassNotFoundException e) {
                throw new IOException(
                    "Failed to resolve to class", e
                );
            }
        }

        if (value.isNothing()) {
            return null;
        }

        throw new IOException(
            "Received `" + value + "` is " +
                "not a secure fully qualified name"
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            ((Class) value).getName()
        );
    }
}
