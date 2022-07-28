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
import plus.kat.crash.*;

import java.time.Instant;
import java.time.LocalDate;

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

    @NotNull
    @Override
    public String getSpace() {
        return "Instant";
    }

    @Nullable
    @Override
    public Instant cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data instanceof Instant) {
            return (Instant) data;
        }

        if (data instanceof Long) {
            return Instant.ofEpochSecond(
                (long) data
            );
        }

        if (data instanceof CharSequence) {
            String d = data.toString();
            if (d.isEmpty()) {
                return null;
            }
            try {
                return Instant.parse(d);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Instant read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        int len = value.length();
        if (len == 0) {
            return null;
        }

        if (len == 10) {
            long sec = value.toLong();
            if (sec > 0) {
                return Instant.ofEpochSecond(sec);
            }
        } else if (len == 13) {
            long mil = value.toLong();
            if (mil > 0) {
                return Instant.ofEpochMilli(mil);
            }
        }

        return Instant.parse(
            value.toString()
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOCrash {
        flow.addLong(
            ((Instant) value).toEpochMilli()
        );
    }
}
