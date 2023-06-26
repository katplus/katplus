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
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author kraity
 * @since 0.0.2
 */
public class URISpare extends BaseSpare<URI> {

    public static final URISpare
        INSTANCE = new URISpare();

    public URISpare() {
        super(URI.class);
    }

    @Override
    public URI apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return apply();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof String) {
                    try {
                        return new URI(
                            (String) arg
                        );
                    } catch (URISyntaxException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }

        throw new IllegalStateException(
            "No matching constructor found"
        );
    }

    @Override
    public String getSpace() {
        return "URI";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public URI read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (value.isNothing()) {
            return null;
        }
        try {
            return new URI(
                value.toString()
            );
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            ((URI) value).toASCIIString()
        );
    }
}
