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
package plus.kat;

/**
 * @author kraity
 * @since 0.0.3
 */
public class Plan {

    public static final Plan
        DEF = new Plan();

    protected long readFlags;
    protected long writeFlags;

    /**
     * add the {@code flag} on the basis of {@code readFlags}
     *
     * <pre>{@code
     *  Plan plan = ...
     *  plan.read(
     *    Flag.INDEX_AS_ENUM
     *  );
     * }</pre>
     *
     * @param flag the specified {@code flag}
     * @since 0.0.3
     */
    public void read(
        long flag
    ) {
        readFlags |= flag;
    }

    /**
     * add the {@code flag} on the basis of {@code readFlags}
     *
     * <pre>{@code
     *  Plan plan = ...
     *  plan.read(
     *    Flag.INDEX_AS_ENUM | Flag.VALUE_AS_BEAN, false
     *  );
     * }</pre>
     *
     * @param flag  the specified {@code flag}
     * @param allow enable this feature if and only if {@code allow} is {@code true}, disable otherwise
     * @since 0.0.3
     */
    public void read(
        long flag,
        boolean allow
    ) {
        if (allow) {
            readFlags |= flag;
        } else {
            readFlags &= ~flag;
        }
    }

    /**
     * Overwrite {@code readFlags} over the specified {@code flags}
     *
     * <pre>{@code
     *  Plan plan = ...
     *  plan.setReadFlags(
     *    Flag.INDEX_AS_ENUM
     *  );
     * }</pre>
     *
     * @param flags the specified {@code flags}
     * @since 0.0.3
     */
    public void setReadFlags(
        long flags
    ) {
        readFlags = flags;
    }

    /**
     * Returns the {@code ReadFlags} of {@link Plan}
     *
     * @since 0.0.3
     */
    public long getReadFlags() {
        return readFlags;
    }

    /**
     * add the {@code flag} on the basis of {@code writeFlags}
     *
     * <pre>{@code
     *  Plan plan = ...
     *  plan.write(
     *    Flag.ENUM_AS_INDEX
     *  );
     * }</pre>
     *
     * @param flag the specified {@code flag}
     * @since 0.0.3
     */
    public void write(
        long flag
    ) {
        writeFlags |= flag;
    }

    /**
     * add the {@code flag} on the basis of {@code writeFlags}
     *
     * <pre>{@code
     *  Plan plan = ...
     *  plan.write(
     *    Flag.ENUM_AS_INDEX | Flag.TIME_AS_DIGIT, false
     *  );
     * }</pre>
     *
     * @param flag  the specified {@code flag}
     * @param allow enable this feature if and only if {@code allow} is {@code true}, disable otherwise
     * @since 0.0.3
     */
    public void write(
        long flag,
        boolean allow
    ) {
        if (allow) {
            writeFlags |= flag;
        } else {
            writeFlags &= ~flag;
        }
    }

    /**
     * Overwrite {@code writeFlags} over the specified {@code flags}
     *
     * <pre>{@code
     *  Plan plan = ...
     *  plan.setWriteFlags(
     *    Flag.ENUM_AS_INDEX
     *  );
     * }</pre>
     *
     * @param flags the specified {@code flags}
     * @since 0.0.3
     */
    public void setWriteFlags(
        long flags
    ) {
        writeFlags = flags;
    }

    /**
     * Returns the {@code WriteFlags} of {@link Plan}
     *
     * @since 0.0.3
     */
    public long getWriteFlags() {
        return writeFlags;
    }
}
