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
    public boolean accept(
        @NotNull Class<?> clazz
    ) {
        return clazz == File.class
            || clazz == Object.class;
    }

    @Override
    public File cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data == null) {
            return null;
        }

        if (data instanceof File) {
            return (File) data;
        }

        if (data instanceof URI) {
            try {
                return new File(
                    (URI) data
                );
            } catch (Exception e) {
                return null;
            }
        }

        if (data instanceof URL) {
            try {
                return new File(
                    ((URL) data).toExternalForm()
                );
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
                return new File(d);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    @Override
    public File read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (value.isEmpty()) {
            return null;
        }
        return new File(
            value.toString()
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        flow.text(
            ((File) value).getPath()
        );
    }
}
