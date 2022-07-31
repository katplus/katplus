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
package plus.kat.spring;

/**
 * @author kraity
 * @since 0.0.3
 */
public class KatConfig {

    public static final KatConfig
        INSTANCE = new KatConfig();

    protected long readFlags;
    protected long writeFlags;

    /**
     * add the {@code flag} on the basis of {@code readFlags}
     *
     * @param flag the specified {@code flag}
     */
    public void readFlag(
        long flag
    ) {
        readFlags |= flag;
    }

    /**
     * Overwrite {@code readFlags} over the specified {@code flags}
     *
     * @param flags the specified {@code flags}
     */
    public void setReadFlags(
        long flags
    ) {
        readFlags = flags;
    }

    /**
     * Returns the read-flags of {@link KatConfig}
     */
    public long getReadFlags() {
        return readFlags;
    }

    /**
     * add the {@code flag} on the basis of {@code writeFlags}
     *
     * @param flag the specified {@code flag}
     */
    public void writeFlag(
        long flag
    ) {
        writeFlags |= flag;
    }

    /**
     * Overwrite {@code writeFlags} over the specified {@code flags}
     *
     * @param flags the specified {@code flags}
     */
    public void setWriteFlags(
        long flags
    ) {
        writeFlags = flags;
    }

    /**
     * Returns the write-flags of {@link KatConfig}
     */
    public long getWriteFlags() {
        return writeFlags;
    }
}
