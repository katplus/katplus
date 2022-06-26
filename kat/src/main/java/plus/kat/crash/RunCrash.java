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

/**
 * @author kraity
 * @since 0.0.1
 */
public class RunCrash extends RuntimeException {
    /**
     * default
     */
    public RunCrash() {
        super();
    }

    /**
     * @param m the detail message
     */
    public RunCrash(String m) {
        super(m);
    }

    /**
     * @param e the cause saved for later retrieval by the {@link #getCause()} method
     */
    public RunCrash(Throwable e) {
        super(e.getMessage(), e, false, false);
    }

    /**
     * @param m the detail message
     * @param t enable suppression and writing stack trace
     */
    public RunCrash(String m, boolean t) {
        super(m, null, t, t);
    }

    /**
     * @param m the detail message
     * @param e the cause saved for later retrieval by the {@link #getCause()} method
     */
    public RunCrash(String m, Throwable e) {
        super(m, e, false, false);
    }

    /**
     * @param m the detail message
     * @param e the cause saved for later retrieval by the {@link #getCause()} method
     * @param a whether suppression is enabled or disabled
     * @param b whether the stack trace should be writable
     */
    public RunCrash(String m, Throwable e, boolean a, boolean b) {
        super(m, e, a, b);
    }
}
