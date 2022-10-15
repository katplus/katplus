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

import plus.kat.anno.Format;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

/**
 * @author kraity
 * @since 0.0.2
 */
public class InstantSpare extends TemporalSpare<Instant> {

    public static final InstantSpare
        INSTANCE = new InstantSpare();

    public InstantSpare() {
        super(Instant.class,
            ISO_INSTANT
        );
    }

    public InstantSpare(
        @NotNull Format format
    ) {
        super(Instant.class, format);
    }

    @Override
    public String getSpace() {
        return "Instant";
    }

    @Override
    public Boolean getBorder(
        @NotNull Flag flag
    ) {
        if (flag.isFlag(Flag.INSTANT_AS_TIMESTAMP)) {
            return Boolean.FALSE;
        }
        return null;
    }

    @Override
    public Instant read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        int len = value.length();
        if (len == 0) {
            return null;
        }

        long mil = value.toLong(-1);
        if (mil >= 0) {
            return Instant.ofEpochMilli(mil);
        }

        return Instant.from(
            formatter.parse(
                value.toString()
            )
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        if (flow.isFlag(Flag.INSTANT_AS_TIMESTAMP)) {
            flow.emit(
                ((Instant) value).toEpochMilli()
            );
        } else {
            StringBuilder builder
                = new StringBuilder(32);
            formatter.formatTo(
                (TemporalAccessor) value, builder
            );
            flow.emit(builder);
        }
    }

    @Override
    public Instant cast(
        @Nullable Object data,
        @NotNull Supplier supplier
    ) {
        if (data != null) {
            if (data instanceof Instant) {
                return (Instant) data;
            }

            if (data instanceof Long) {
                // as millisecond
                return Instant.ofEpochMilli(
                    (long) data
                );
            }

            if (data instanceof Integer) {
                // as seconds
                return Instant.ofEpochSecond(
                    (int) data
                );
            }

            if (data instanceof CharSequence) {
                String d = data.toString();
                if (d.isEmpty()) {
                    return null;
                }
                try {
                    return Instant.from(
                        formatter.parse(d)
                    );
                } catch (Exception e) {
                    return null;
                }
            }


            if (data instanceof AtomicLong) {
                // as millisecond
                return Instant.ofEpochMilli(
                    ((AtomicLong) data).get()
                );
            }

            if (data instanceof AtomicInteger) {
                // as seconds
                return Instant.ofEpochSecond(
                    ((AtomicInteger) data).get()
                );
            }
        }
        return null;
    }
}
