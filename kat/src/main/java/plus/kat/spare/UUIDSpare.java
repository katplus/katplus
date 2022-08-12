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
import plus.kat.kernel.*;
import plus.kat.stream.*;

import java.io.IOException;
import java.util.UUID;

/**
 * @author kraity
 * @since 0.0.2
 */
public class UUIDSpare extends Property<UUID> {

    public static final UUIDSpare
        INSTANCE = new UUIDSpare();

    public UUIDSpare() {
        super(UUID.class);
    }

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

        if (data instanceof Chain) {
            try {
                return parse(
                    (Chain) data
                );
            } catch (Exception e) {
                return null;
            }
        }

        if (data instanceof CharSequence) {
            CharSequence c = (CharSequence) data;
            int len = c.length();
            if (len < 8 ||
                len > 36) {
                return null;
            }

            try {
                return parse(
                    new Value(c)
                );
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    @Override
    public UUID read(
        @NotNull Flag flag,
        @NotNull Alias alias
    ) throws IOCrash {
        return parse(alias);
    }

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
    ) throws IOException {
        UUID u = (UUID) value;

        long most = u.getMostSignificantBits();
        long least = u.getLeastSignificantBits();

        flow.addLong(
            (most >> 32) & 0xFFFFFFFFL, 4, 8
        );
        flow.addByte(
            (byte) '-'
        );
        flow.addLong(
            (most >> 16) & 0xFFFFL, 4, 4
        );
        flow.addByte(
            (byte) '-'
        );
        flow.addLong(
            most & 0xFFFFL, 4, 4
        );
        flow.addByte(
            (byte) '-'
        );
        flow.addLong(
            (least >> 48) & 0xFFFFL, 4, 4
        );
        flow.addByte(
            (byte) '-'
        );
        flow.addLong(
            least & 0xFFFFFFFFFFFFL, 4, 12
        );
    }

    private static long hex(
        Chain c, int i, int o
    ) throws IOCrash {
        long d = Binary.hex(
            c.at(i++)
        );
        while (i < o) {
            d <<= 4;
            d |= Binary.hex(
                c.at(i++)
            );
        }
        return d;
    }

    @Nullable
    public static UUID parse(
        @NotNull Chain c
    ) throws IOCrash {
        int len = c.length();
        if (len < 8 ||
            len > 36) {
            return null;
        }

        int dash1 = c.indexOf((byte) '-', 0);
        int dash2 = c.indexOf((byte) '-', dash1 + 1);
        int dash3 = c.indexOf((byte) '-', dash2 + 1);
        int dash4 = c.indexOf((byte) '-', dash3 + 1);
        int dash5 = c.indexOf((byte) '-', dash4 + 1);

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
