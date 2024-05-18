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
import static plus.kat.lang.Uniform.*;

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

        check:
        {
            int l = value.size();
            byte[] v = value.flow();

            int i = 0, x = 0, e = 8;
            long m = 0, n = 0, u = 0;

            switch (l) {
                case 32:
                    break;
                case 36:
                    if (v[0x08] == '-' &&
                        v[0x0D] == '-' &&
                        v[0x12] == '-' &&
                        v[0x17] == '-') {
                        x = 1;
                        break;
                    }
                default:
                    break check;
            }

            while (i < l) {
                byte w = v[i++];
                if (w > 0x2F && w < 0x3A) {
                    u = u << 4 | (w - 0x30);
                } else if (w > 0x60 && w < 0x67) {
                    u = u << 4 | (w - 0x57);
                } else if (w > 0x40 && w < 0x47) {
                    u = u << 4 | (w - 0x37);
                } else {
                    break check;
                }

                if (i == e) {
                    switch (e) {
                        case 8: {
                            m = u & 0xFFFFFFFFL;
                            u = 0;
                            e = (i += x) + 4;
                            continue;
                        }
                        case 12:
                        case 13:
                        case 16:
                        case 18: {
                            m = m << 16 | u & 0xFFFFL;
                            u = 0;
                            e = (i += x) + 4;
                            continue;
                        }
                        case 20:
                        case 23: {
                            n = u & 0xFFFFL;
                            u = 0;
                            e = (i += x) + 12;
                            continue;
                        }
                        case 32:
                        case 36: {
                            n = n << 48 | u & 0xFFFFFFFFFFFFL;
                            u = 0;
                            continue;
                        }
                    }
                    break check;
                }
            }

            return new UUID(m, n);
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
        byte[] hex = LOWER;

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
