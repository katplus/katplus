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

import java.lang.reflect.Type;
import java.util.UUID;
import java.io.IOException;

import static plus.kat.chain.Chain.Unsafe.value;

/**
 * @author kraity
 * @since 0.0.5
 */
public class UUIDSpare extends Property<UUID> {

    public static final UUIDSpare
        INSTANCE = new UUIDSpare();

    public UUIDSpare() {
        super(UUID.class);
    }

    @Override
    public UUID apply() {
        return UUID.randomUUID();
    }

    @Override
    public UUID apply(
        @Nullable Type type
    ) {
        if (type == null ||
            type == UUID.class) {
            return UUID.randomUUID();
        }

        throw new Collapse(
            this + " unable to build " + type
        );
    }

    @Override
    public String getSpace() {
        return "UUID";
    }

    @Override
    public UUID read(
        @NotNull Flag flag,
        @NotNull Chain chain
    ) {
        int size = chain.length();
        if (size < 9 ||
            size > 36) {
            return null;
        }

        int i = 0, k = 0;
        long m = 0, n = 0, u = 0;

        byte[] it = value(chain);
        while (i < size) {
            byte b = it[i++];
            if (b > 0x2F) {
                if (b < 0x3A) {
                    u = u << 4 | (b - 0x30);
                    continue;
                }
                if (b > 0x60 && b < 0x67) {
                    u = u << 4 | (b - 0x57);
                    continue;
                }
                if (b > 0x40 && b < 0x47) {
                    u = u << 4 | (b - 0x37);
                    continue;
                }
            }
            if (b == 0x2D) {
                switch (k++) {
                    case 0: {
                        m = u & 0xFFFFFFFFL;
                        u = 0;
                        continue;
                    }
                    case 1:
                    case 2: {
                        m = m << 16 | u & 0xFFFFL;
                        u = 0;
                        continue;
                    }
                    case 3: {
                        n = u & 0xFFFFL;
                        u = 0;
                        continue;
                    }
                }
            }
            return null;
        }

        return k != 4 ? null : new UUID(
            m, n << 48 | u & 0xFFFFFFFFFFFFL
        );
    }

    @Override
    public void write(
        @NotNull Flow flow,
        @NotNull Object value
    ) throws IOException {
        UUID u = (UUID) value;

        long m = u.getMostSignificantBits();
        long n = u.getLeastSignificantBits();

        flow.emit(
            (m >> 32) & 0xFFFFFFFFL, 4, 8
        );
        flow.emit((byte) '-');
        flow.emit(
            (m >> 16) & 0xFFFFL, 4, 4
        );
        flow.emit((byte) '-');
        flow.emit(
            m & 0xFFFFL, 4, 4
        );
        flow.emit((byte) '-');
        flow.emit(
            (n >> 48) & 0xFFFFL, 4, 4
        );
        flow.emit((byte) '-');
        flow.emit(
            n & 0xFFFFFFFFFFFFL, 4, 12
        );
    }

    @Override
    public UUID cast(
        @Nullable Object object,
        @NotNull Supplier supplier
    ) {
        if (object == null) {
            return null;
        }

        if (object instanceof UUID) {
            return (UUID) object;
        }

        if (object instanceof Chain) {
            return read(
                null, (Chain) object
            );
        }

        if (object instanceof CharSequence) {
            CharSequence c = (CharSequence) object;
            int size = c.length();
            if (size < 9 ||
                size > 36) {
                return null;
            }

            int i = 0, k = 0;
            long m = 0, n = 0, u = 0;

            while (i < size) {
                char b = c.charAt(i++);
                if (b > 0x2F) {
                    if (b < 0x3A) {
                        u = u << 4 | (b - 0x30);
                        continue;
                    }
                    if (b > 0x60 && b < 0x67) {
                        u = u << 4 | (b - 0x57);
                        continue;
                    }
                    if (b > 0x40 && b < 0x47) {
                        u = u << 4 | (b - 0x37);
                        continue;
                    }
                }
                if (b == 0x2D) {
                    switch (k++) {
                        case 0: {
                            m = u & 0xFFFFFFFFL;
                            u = 0;
                            continue;
                        }
                        case 1:
                        case 2: {
                            m = m << 16 | u & 0xFFFFL;
                            u = 0;
                            continue;
                        }
                        case 3: {
                            n = u & 0xFFFFL;
                            u = 0;
                            continue;
                        }
                    }
                }
                return null;
            }

            return k != 4 ? null : new UUID(
                m, n << 48 | u & 0xFFFFFFFFFFFFL
            );
        }

        throw new IllegalStateException(
            object + " cannot be converted to " + klass
        );
    }
}
