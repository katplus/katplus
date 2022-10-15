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
import plus.kat.crash.*;

import java.io.*;
import java.net.*;

/**
 * @author kraity
 * @since 0.0.2
 */
public class URLSpare extends Property<URL> {

    public static final URLSpare
        INSTANCE = new URLSpare();

    public URLSpare() {
        super(URL.class);
    }

    @Override
    public String getSpace() {
        return "URL";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == URL.class
            || clazz == Object.class;
    }

    @Override
    public URL read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (value.isEmpty()) {
            return null;
        }
        try {
            return new URL(
                value.toString()
            );
        } catch (MalformedURLException e) {
            throw new UnexpectedCrash(e);
        }
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.emit(
            ((URL) value).toExternalForm()
        );
    }

    @Override
    public URL cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
            if (data instanceof URL) {
                return (URL) data;
            }

            if (data instanceof URI) {
                try {
                    return ((URI) data).toURL();
                } catch (Exception e) {
                    return null;
                }
            }

            if (data instanceof CharSequence) {
                String d = data.toString();
                if (d.isEmpty()) {
                    return null;
                }
                try {
                    return new URL(d);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }
}
