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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * @author kraity
 * @since 0.0.3
 */
public class FileSpare extends Property<File> {

    public static final FileSpare
        INSTANCE = new FileSpare();

    public FileSpare() {
        super(File.class);
    }

    @Override
    public String getSpace() {
        return "File";
    }

    @Override
    public File read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) throws IOException {
        if (chain.isEmpty()) {
            return null;
        }
        return new File(
            chain.toString()
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.emit(
            ((File) value).getPath()
        );
    }

    @Override
    public File cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (object instanceof File) {
            return (File) object;
        }

        if (object instanceof URI) {
            return new File(
                (URI) object
            );
        }

        if (object instanceof URL) {
            return new File(
                ((URL) object).toExternalForm()
            );
        }

        if (object instanceof CharSequence) {
            String s = object.toString();
            if (s.isEmpty() ||
                "null".equalsIgnoreCase(s)) {
                return null;
            } else {
                return new File(s);
            }
        }

        throw new IllegalStateException(
            object + " cannot be converted to " + klass
        );
    }
}
