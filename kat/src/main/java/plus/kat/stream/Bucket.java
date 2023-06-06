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
package plus.kat.stream;

/**
 * @author kraity
 * @since 0.0.6
 */
public interface Bucket {
    /**
     * Resumes the specified old buffer to this
     * bucket, and returns the old flow if unsuccessful
     *
     * @param flow the specified array that will be shared
     */
    byte[] store(
        byte[] flow
    );

    /**
     * Borrows a buffer of the specified minimum length and copies old
     * flow of the specified size into the buffer, then reclaims the old flow
     *
     * @param flow   the specified array that will be released
     * @param size   the specified size of stream buffer array
     * @param length the specified minimum length of buffer array
     */
    byte[] apply(
        byte[] flow, int size, int length
    );
}
