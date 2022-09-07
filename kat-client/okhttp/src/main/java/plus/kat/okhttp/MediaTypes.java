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
package plus.kat.okhttp;

import plus.kat.Job;
import plus.kat.crash.Collapse;

import okhttp3.MediaType;

/**
 * @author kraity
 * @since 0.0.3
 */
public class MediaTypes {
    public static final MediaType
        APPLICATION_KAT = MediaType.parse("application/kat; charset=UTF-8");

    public static final MediaType
        APPLICATION_DOC = MediaType.parse("application/xml; charset=UTF-8");

    public static final MediaType
        APPLICATION_JSON = MediaType.parse("application/json; charset=UTF-8");

    public static MediaType of(
        Job job
    ) {
        switch (job) {
            case KAT: {
                return APPLICATION_KAT;
            }
            case DOC: {
                return APPLICATION_DOC;
            }
            case JSON: {
                return APPLICATION_JSON;
            }
        }

        throw new Collapse(
            "Unexpectedly, Converter did not find " + job + "'s MediaType"
        );
    }
}
