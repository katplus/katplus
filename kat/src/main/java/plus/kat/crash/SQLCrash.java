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
package plus.kat.crash;

import java.sql.SQLException;

/**
 * @author kraity
 * @since 0.0.3
 */
public class SQLCrash extends SQLException {
    /**
     * default
     */
    public SQLCrash() {
        super();
    }

    /**
     * @param m the detail message
     */
    public SQLCrash(String m) {
        super(m);
    }

    /**
     * @param e the cause saved for later retrieval by the {@link #getCause()} method
     */
    public SQLCrash(Throwable e) {
        super(e);
    }

    /**
     * @param m the detail message
     * @param e the cause saved for later retrieval by the {@link #getCause()} method
     */
    public SQLCrash(String m, Throwable e) {
        super(m, e);
    }
}
