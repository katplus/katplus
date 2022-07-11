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
import plus.kat.entity.*;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * @author kraity
 * @since 0.0.2
 */
public class URLSpare implements Spare<URL> {

    public static final URLSpare
        INSTANCE = new URLSpare();

    @NotNull
    @Override
    public String getSpace() {
        return "URL";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == URL.class
            || klass == Object.class;
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return null;
    }

    @NotNull
    @Override
    public Class<URL> getType() {
        return URL.class;
    }

    @Nullable
    @Override
    public Builder<URL> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @Nullable
    @Override
    public URL cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

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

        return null;
    }

    @Nullable
    @Override
    public URL read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOCrash {
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
    ) throws IOCrash {
        flow.addText(
            ((URL) value).toExternalForm()
        );
    }
}
