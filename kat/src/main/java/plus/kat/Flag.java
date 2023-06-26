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
 * @since 0.0.1
 */
@FunctionalInterface
public interface Flag {
    /**
     * Check if this uses the feature
     *
     * <pre>{@code
     *  Flag flag = ...
     *  boolean status = flag.isFlag(
     *      Flag.NORM | Flag.STANDARD
     *  );
     * }</pre>
     *
     * @param flag the specified flag code
     */
    boolean isFlag(long flag);

    /**
     * Uses this feature in serialization,
     * if required by the standard specification
     */
    long NORM = Long.MIN_VALUE;

    /**
     * Uses this feature in serialization,
     * if the serialization result is required to be formatted
     */
    long PRETTY = 0x1;

    /**
     * Uses this feature in serialization,
     * if a character in the serialization result
     * is greater than 0x7F, it is converted to unicode format
     */
    long UNICODE = 0x2;

    /**
     * Uses this feature in serialization
     */
    long ENUM_AS_INDEX = 0x10;

    /**
     * Uses this feature in serialization
     */
    long DATE_AS_DIGIT = 0x20;

    /**
     * Uses this feature in deserialization
     */
    long INDEX_AS_ENUM = 0x10;

    /**
     * Uses this feature in deserialization
     */
    long DIGIT_AS_DATE = 0x20;

    /**
     * Uses this feature in deserialization
     */
    long VALUE_AS_BEAN = 0x40;
}
