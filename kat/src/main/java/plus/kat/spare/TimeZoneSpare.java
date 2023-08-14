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

import java.util.TimeZone;
import java.io.IOException;

import static java.util.TimeZone.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public class TimeZoneSpare extends BaseSpare<TimeZone> {

    public static final TimeZoneSpare
        INSTANCE = new TimeZoneSpare();

    static final TimeZone DEF =
        TimeZone.getDefault();

    static {
        INSTANCE.context.active(
            DEF.getClass(), INSTANCE
        );
    }

    public TimeZoneSpare() {
        super(TimeZone.class);
    }

    @Override
    public TimeZone apply() {
        return getDefault();
    }

    @Override
    public TimeZone apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return getDefault();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof String) {
                    return getTimeZone(
                        (String) arg
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
        return "TimeZone";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public TimeZone read(
        @NotNull Flag flag,
        @NotNull Value value
    ) {
        if (value.isNothing()) {
            return null;
        } else {
            return getTimeZone(
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
            ((TimeZone) value).getID()
        );
    }
}
