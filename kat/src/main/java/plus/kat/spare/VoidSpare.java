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
public class VoidSpare extends BaseSpare<Void> {

    public static final VoidSpare
        INSTANCE = new VoidSpare();

    public VoidSpare() {
        super(Void.class);
    }

    @Override
    public Void apply() {
        return null;
    }

    @Override
    public Void apply(
        @NotNull Object... args
    ) {
        if (args.length == 0) {
            return null;
        }

        throw new IllegalStateException(
            "No matching constructor found"
        );
    }

    @Override
    public String getSpace() {
        return "Any";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return null;
    }

    @Override
    public Void read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) throws IOException {
        return null;
    }

    @Override
    public Void read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        return null;
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @Nullable Object value
    ) throws IOException {
        chan.set(
            null, null
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @Nullable Object value
    ) throws IOException {
        flux.emit((byte) 'n');
        flux.emit((byte) 'u');
        flux.emit((byte) 'l');
        flux.emit((byte) 'l');
    }
}
