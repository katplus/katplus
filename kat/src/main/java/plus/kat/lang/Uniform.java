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
package plus.kat.lang;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author kraity
 * @since 0.0.6
 */
public final class Uniform {

    static final Properties
        properties = new Properties();

    static {
        try {
            ClassLoader cl = Thread
                .currentThread()
                .getContextClassLoader();

            if (cl == null) {
                cl = ClassLoader
                    .getSystemClassLoader();
            }

            URL src = cl.getResource(
                "katplus.properties"
            );
            if (src != null) {
                try (InputStream in = src.openStream()) {
                    properties.load(in);
                }
            }
        } catch (Throwable e) {
            // Ignore this exception
        }
    }

    private Uniform() {
        throw new Error();
    }

    /**
     * Unsafe, may be deleted later
     */
    public static final byte[]
        EMPTY_BYTES = {};

    /**
     * empty char array
     */
    public static final char[]
        EMPTY_CHARS = {};

    /**
     * Unsafe, may be deleted later
     */
    public static final byte[] LOWER = {
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f', 'g', 'h',
        'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    };
    /**
     * Unsafe, may be deleted later
     */
    public static final byte[] UPPER = {
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F', 'G', 'H',
        'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    /**
     * property etc.
     */
    public static final int ALIAS_CAPACITY =
        getProperty("katplus.alias.capacity", 512);
    public static final int SPACE_CAPACITY =
        getProperty("katplus.space.capacity", 256);
    public static final int VALUE_CAPACITY =
        getProperty("katplus.value.capacity", 8192);

    public static final int PARSER_GROUP =
        getProperty("katplus.parser.group", 16);

    public static final int STREAM_GROUP =
        getProperty("katplus.stream.group", 8);
    public static final int STREAM_CAPACITY =
        getProperty("katplus.stream.capacity", 8192);

    public static final int SUPPLIER_BUFFER =
        getProperty("katplus.supplier.buffer", 64);
    public static final int SUPPLIER_CAPACITY =
        getProperty("katplus.supplier.capacity", 64);

    /**
     * Returns the attribute indicated by the specified key
     *
     * @param key the specified key value
     * @param def the specified default value
     */
    public static int getProperty(
        String key, int def
    ) {
        String data = (String)
            properties.get(key);
        return data == null || data.isEmpty() ?
            def : Integer.parseInt(data);
    }

    /**
     * Returns the attribute indicated by the specified key
     *
     * @param key the specified key value
     * @param def the specified default value
     */
    public static String getProperty(
        String key, String def
    ) {
        String data = (String) properties.get(key);
        return data == null || data.isEmpty() ? def : data;
    }

    /**
     * Unsafe, may be deleted later
     */
    public static byte stateOf(Binary bin) {
        return bin.state;
    }

    /**
     * Unsafe, may be deleted later
     */
    public static byte[] valueOf(Binary bin) {
        return bin.value;
    }
}
