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

import java.io.*;
import java.net.*;

/**
 * @author kraity
 * @since 0.0.2
 */
public class URISpare extends Property<URI> {

    public static final URISpare
        INSTANCE = new URISpare();

    public URISpare() {
        super(URI.class);
    }

    @Override
    public String getSpace() {
        return "URI";
    }

    @Override
    public URI read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) throws IOException {
        if (chain.isEmpty()) {
            return null;
        }
        try {
            return new URI(
                chain.toString()
            );
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.emit(
            ((URI) value).toASCIIString()
        );
    }

    @Override
    public URI cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (object instanceof URI) {
            return (URI) object;
        }

        if (object instanceof URL) {
            try {
                return ((URL) object).toURI();
            } catch (URISyntaxException e) {
                throw new IllegalStateException(
                    object + " cannot be converted to " + klass, e
                );
            }
        }

        if (object instanceof CharSequence) {
            String s = object.toString();
            if (s.isEmpty() ||
                "null".equalsIgnoreCase(s)) {
                return null;
            }
            try {
                return new URI(s);
            } catch (URISyntaxException e) {
                throw new IllegalStateException(
                    object + " cannot be converted to " + klass, e
                );
            }
        }

        throw new IllegalStateException(
            object + " cannot be converted to " + klass
        );
    }
}
