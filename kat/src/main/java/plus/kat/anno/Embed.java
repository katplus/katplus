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
package plus.kat.anno;

import plus.kat.Spare;

import java.lang.annotation.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Embed {
    /**
     * Embed Flags
     */
    int SEALED = 0x1;
    int DIRECT = 0x2;

    /**
     * Returns the space of this
     */
    String[] value() default {};

    /**
     * Returns the flags of this
     */
    int claim() default 0;

    /**
     * Whether it can be exposed
     */
    boolean expose() default true;

    /**
     * Returns the specified {@link Spare}
     */
    Class<? extends Spare> with() default Spare.class;
}
