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
package plus.kat.utils;

import plus.kat.actor.*;

import java.util.Properties;

/**
 * @author kraity
 * @since 0.0.1
 */
public final class Config {

    private final static Properties
        props = System.getProperties();

    private Config() {
        throw new IllegalStateException();
    }

    /**
     * Returns the attribute indicated by the specified key
     */
    public static int get(
        @NotNull String key, int def
    ) {
        String data = props
            .getProperty(key);

        if (data == null ||
            data.isEmpty()) {
            return def;
        }

        return Integer.parseInt(data);
    }

    /**
     * Returns the attribute indicated by the specified key
     */
    public static String get(
        @NotNull String key, String def
    ) {
        String data = props.getProperty(key);
        return data == null || data.isEmpty() ? def : data;
    }
}
