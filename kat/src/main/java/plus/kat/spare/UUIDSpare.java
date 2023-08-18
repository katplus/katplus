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

import java.util.UUID;
import java.io.IOException;

import static java.util.UUID.*;
import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.6
 */
public class UUIDSpare extends BaseSpare<UUID> {

    public static final UUIDSpare
        INSTANCE = new UUIDSpare();

    public UUIDSpare() {
        super(UUID.class);
    }

    @Override
    public UUID apply() {
        return randomUUID();
    }

    @Override
    public String getSpace() {
        return "UUID";
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.QUOTE;
    }

    @Override
    public UUID read(
        @NotNull Flag flag,
        @NotNull Value value
    ) throws IOException {
        if (value.isNothing()) {
            return null;
        }

        stage:
        {
            int size = value.size();
            if (size < 9 ||
                size > 36) {
                break stage;
            }

            int i = 0, k = 0;
            long m = 0, n = 0, u = 0;

            byte[] flow = value.flow();
            while (i < size) {
                byte b = flow[i++];
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
                break stage;
            }

            return k != 4 ? null : new UUID(
                m, n << 48 | u & 0xFFFFFFFFFFFFL
            );
        }

        throw new IOException(
            "Received `" + value
                + "` is not a UUID string"
        );
    }

    @Override
    public void write(
        @NotNull Flux flux,
        @NotNull Object value
    ) throws IOException {
        UUID u = (UUID) value;

        long m = u.getMostSignificantBits();
        long l = u.getLeastSignificantBits();

        long n, v;
        byte[] hex = HEX_LOWER;

        v = 28;
        n = m >> 32 & 0xFFFFFFFFL;
        do {
            flux.emit(
                hex[(int) (n >> v & 0xF)]
            );
        } while ((v -= 4) != -4);

        flux.emit((byte) '-');

        v = 12;
        n = m >> 16 & 0xFFFFL;
        do {
            flux.emit(
                hex[(int) (n >> v & 0xF)]
            );
        } while ((v -= 4) != -4);

        flux.emit((byte) '-');

        v = 12;
        n = m & 0xFFFFL;
        do {
            flux.emit(
                hex[(int) (n >> v & 0xF)]
            );
        } while ((v -= 4) != -4);

        flux.emit((byte) '-');

        v = 12;
        n = l >> 48 & 0xFFFFL;
        do {
            flux.emit(
                hex[(int) (n >> v & 0xF)]
            );
        } while ((v -= 4) != -4);

        flux.emit((byte) '-');

        v = 44;
        n = l & 0xFFFFFFFFFFFFL;
        do {
            flux.emit(
                hex[(int) (n >> v & 0xF)]
            );
        } while ((v -= 4) != -4);
    }
}
