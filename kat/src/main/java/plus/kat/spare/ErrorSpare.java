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
import plus.kat.utils.*;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author kraity
 * @since 0.0.4
 */
@SuppressWarnings("unchecked")
public class ErrorSpare extends Property<Exception> {

    public static final ErrorSpare
        INSTANCE = new ErrorSpare(Crash.class);

    public ErrorSpare(
        @NotNull Class<?> klass
    ) {
        super((Class<Exception>) klass);
    }

    @Override
    public Space getSpace() {
        return Space.$E;
    }

    @Override
    public Boolean getFlag() {
        return Boolean.TRUE;
    }

    @Override
    public Exception cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data == null) {
            return null;
        }

        if (klass.isInstance(data)) {
            return (Exception) data;
        }

        if (data instanceof CharSequence) {
            return Casting.cast(
                this, (CharSequence) data, null, supplier
            );
        }

        return null;
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        if (value instanceof Crash) {
            Crash e = (Crash) value;
            chan.set("c", e.getCode());
            chan.set("m", e.getMessage());
        } else {
            Exception e = (Exception) value;
            chan.set("message", e.getMessage());
        }
    }

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
        public void onCreate(
            @NotNull Alias alias
        ) {
            // NOOP
        }

        @Override
        public void onAccept(
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

        @Override
        public void onAccept(
            @NotNull Alias alias,
            @NotNull Builder<?> child
        ) {
            // NOOP
        }

        @Override
        public Builder<?> getBuilder(
            @NotNull Space space,
            @NotNull Alias alias
        ) {
            return null;
        }

        @Nullable
        @Override
        public Crash getResult() {
            if (entity != null) {
                return entity;
            }
            return entity = new Crash(
                message, code
            );
        }

        @Override
        public void onDestroy() {
            entity = null;
        }
    }
}
