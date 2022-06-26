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

import plus.kat.anno.NotNull;

import plus.kat.stream.*;

/**
 * @author kraity
 * @since 0.0.1
 */
public class Config {
    /**
     * Returns the attribute indicated by the specified key
     */
    public static String get(
        @NotNull String key, String def
    ) {
        try {
            String s = System.getProperty(key);
            if (s == null) {
                return def;
            }
            return s.isEmpty() ? def : s;
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns the attribute indicated by the specified key
     */
    public static int get(
        @NotNull String key, int def
    ) {
        int l;
        String s;
        try {
            s = System.getProperty(key);
            if (s == null) {
                return def;
            }
            l = s.length();
            if (l == 0) {
                return def;
            }
        } catch (Exception e) {
            return def;
        }

        return Convert.toInt(
            s, l, 10, def
        );
    }

    /**
     * Returns the attribute indicated by the specified key
     */
    public static long get(
        @NotNull String key, long def
    ) {
        int l;
        String s;
        try {
            s = System.getProperty(key);
            if (s == null) {
                return def;
            }
            l = s.length();
            if (l == 0) {
                return def;
            }
        } catch (Exception e) {
            return def;
        }

        return Convert.toLong(
            s, l, 10L, def
        );
    }

    /**
     * Returns the attribute indicated by the specified key
     */
    public static boolean get(
        @NotNull String key, boolean def
    ) {
        int l;
        String s;
        try {
            s = System.getProperty(key);
            if (s == null) {
                return def;
            }
            l = s.length();
            if (l == 0) {
                return def;
            }
        } catch (Exception e) {
            return def;
        }

        return Convert.toBoolean(
            s, l, def
        );
    }
}
