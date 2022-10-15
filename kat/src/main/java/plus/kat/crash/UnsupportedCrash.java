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
 * @since 0.0.4
 */
public class UnsupportedCrash extends RuntimeException {
    /**
     * @param m the detail message
     */
    public UnsupportedCrash(String m) {
        super(m);
    }

    /**
     * @param e the specified cause to saved
     */
    public UnsupportedCrash(Throwable e) {
        super(e);
    }

    /**
     * @param m the detail message
     * @param e the specified cause to saved
     */
    public UnsupportedCrash(String m, Throwable e) {
        super(m, e);
    }
}
