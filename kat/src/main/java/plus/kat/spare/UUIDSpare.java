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
import plus.kat.entity.*;
import plus.kat.kernel.*;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * @author kraity
 * @since 0.0.1
 */
public class UUIDSpare implements Spare<UUID> {

    public static final UUIDSpare
        INSTANCE = new UUIDSpare();

    @NotNull
    @Override
    public String getSpace() {
        return "UUID";
    }

    @Override
    public boolean accept(
        @NotNull Class<?> klass
    ) {
        return klass == UUID.class
            || klass == Object.class;
    }

    @Nullable
    @Override
    public Boolean getFlag() {
        return null;
    }

    @NotNull
    @Override
    public Class<UUID> getType() {
        return UUID.class;
    }

    @Nullable
    @Override
    public Builder<UUID> getBuilder(
        @Nullable Type type
    ) {
        return null;
    }

    @NotNull
    @Override
    public UUID cast(
        @NotNull Supplier supplier,
        @Nullable Object data
    ) {
        if (data == null) {
            return null;
        }

        if (data instanceof UUID) {
            return (UUID) data;
        }

        if (data instanceof String) {
            try {
                return UUID.fromString(
                    (String) data
                );
            } catch (Exception e) {
                // Nothing
            }
        }

        return null;
    }

    @Nullable
    @Override
    public UUID read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) throws IOCrash {
        return parse(alias);
    }

    @Nullable
    @Override
    public UUID read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOCrash {
        return parse(value);
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOCrash {
        flow.addData(
            value.toString()
        );
    }

    private static int hex(
        byte b
    ) throws IOCrash {
        if (b > 0x2F) {
            if (b < 0x3A) {
                return b - 0x30;
            }
            if (b > 0x60 && b < 0x67) {
                return b - 0x57;
            }
            if (b > 0x40 && b < 0x47) {
                return b - 0x37;
            }
        }
        throw new UnexpectedCrash(
            "Unexpectedly, " + (char) b + " is not a hexadecimal number"
        );
    }

    private static long hex(
        Chain c, int i, int o
    ) throws IOCrash {
        long d = hex(
            c.at(i++)
        );
        while (i < o) {
            d <<= 4;
            d |= hex(
                c.at(i++)
            );
        }
        return d;
    }

    @Nullable
    private static UUID parse(
        @NotNull Chain c
    ) throws IOCrash {
        int len = c.length();
        if (len < 8 ||
            len > 36) {
            return null;
        }

        int dash1 = c.indexOf('-', 0);
        int dash2 = c.indexOf('-', dash1 + 1);
        int dash3 = c.indexOf('-', dash2 + 1);
        int dash4 = c.indexOf('-', dash3 + 1);
        int dash5 = c.indexOf('-', dash4 + 1);

        if (dash4 < 0 || dash5 >= 0) {
            return null;
        }

        long most = hex(c, 0, dash1) & 0xFFFFFFFFL;
        most <<= 16;
        most |= hex(c, dash1 + 1, dash2) & 0xFFFFL;
        most <<= 16;
        most |= hex(c, dash2 + 1, dash3) & 0xFFFFL;

        long least = hex(c, dash3 + 1, dash4) & 0xFFFFL;
        least <<= 48;
        least |= hex(c, dash4 + 1, len) & 0xFFFFFFFFFFFFL;

        return new UUID(
            most, least
        );
    }
}
