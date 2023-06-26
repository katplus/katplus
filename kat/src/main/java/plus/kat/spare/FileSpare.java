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

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * @author kraity
 * @since 0.0.3
 */
public class FileSpare extends BaseSpare<File> {

    public static final FileSpare
        INSTANCE = new FileSpare();

    public FileSpare() {
        super(File.class);
    }

    @Override
    public File apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return apply();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof URI) {
                    return new File(
                        (URI) arg
                    );
                }
                if (arg instanceof String) {
                    return new File(
                        (String) arg
                    );
                }
                break;
            }
            case 2: {
                Object arg0 = args[0];
                Object arg1 = args[1];
                if (arg0 instanceof String &&
                    arg1 instanceof String) {
                    return new File(
                        (String) arg0, (String) arg1
                    );
                }
            }
        }

        throw new IllegalStateException(
            "No matching constructor found"
        );
    }

    @Override
    public String getSpace() {
        return "File";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public File read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (value.isNothing()) {
            return null;
        } else {
            return new File(
                value.toString()
            );
        }
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        flux.emit(
            ((File) value).getPath()
        );
    }
}
