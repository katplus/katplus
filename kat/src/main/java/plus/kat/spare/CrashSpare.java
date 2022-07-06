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

import plus.kat.Spare;
import plus.kat.Supplier;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.utils.Casting;

import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.1
 */
public class CrashSpare implements Spare<Crash> {

    public static final CrashSpare
        INSTANCE = new CrashSpare();

    @NotNull
    @Override
    public Space getSpace() {
        return Space.$E;
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass.isAssignableFrom(Crash.class);
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return Boolean.TRUE;
    }

    @NotNull
    @Override
    public Class<Crash> getType() {
        return Crash.class;
    }

    @Nullable
    @Override
    public Crash cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data instanceof Crash) {
            return (Crash) data;
        }

        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, supplier
            );
        }

        return null;
    }

    @Nullable
    @Override
    public Builder<Crash> getBuilder(
        @Nullable Type type
    ) {
        return new Builder0();
    }

    public static class Builder0 extends Builder<Crash> {

        private Crash entity;
        private int code;
        private String message;

        @Override
        public void create(
            @NotNull Alias alias
        ) {
            // NOOP
        }

        @Override
        public void accept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) {
            switch (alias.head()) {
                case 'c': {
                    code = value.toInt();
                    break;
                }
                case 'm': {
                    message = value.toString();
                }
            }
        }

        @Nullable
        @Override
        public Crash bundle() {
            if (entity != null) {
                return entity;
            }
            return entity = new Crash(message, code);
        }

        @Override
        public Builder<?> observe(
            @NotNull Space space,
            @NotNull Alias alias
        ) {
            return null;
        }

        @Override
        public void dispose(
            @NotNull Builder<?> child
        ) throws IOCrash {
            // NOOP
        }

        @Override
        public void close() {
            entity = null;
            code = 0;
            message = null;
        }
    }
}
