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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;

import java.io.IOException;

/**
 * @author kraity
 * @since 0.0.4
 */
public class CallCrash extends RuntimeException implements Kat {
    /**
     * @param m the detail message
     */
    public CallCrash(String m) {
        super(m);
    }

    /**
     * @param e the cause saved for later retrieval by the {@link #getCause()} method
     */
    public CallCrash(Throwable e) {
        super(e.getMessage(), e, false, false);
    }

    /**
     * @param m the detail message
     * @param t enable suppression and writing stack trace
     */
    public CallCrash(String m, boolean t) {
        super(m, null, t, t);
    }

    /**
     * @param m the detail message
     * @param e the cause saved for later retrieval by the {@link #getCause()} method
     */
    public CallCrash(String m, Throwable e) {
        super(m, e, false, false);
    }

    /**
     * @param m the detail message
     * @param e the cause saved for later retrieval by the {@link #getCause()} method
     * @param a whether suppression is enabled or disabled
     * @param b whether the stack trace should be writable
     */
    public CallCrash(String m, Throwable e, boolean a, boolean b) {
        super(m, e, a, b);
    }

    /**
     * Returns the space of this
     */
    @Nullable
    @Override
    public Space space() {
        return Space.$E;
    }

    /**
     * @param chan the specified chan
     */
    @Override
    public void coding(
        @NotNull Chan chan
    ) throws IOException {
        chan.set(
            "message", getMessage()
        );
    }
}
