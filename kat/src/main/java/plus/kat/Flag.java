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
package plus.kat;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;

/**
 * @author kraity
 * @since 0.0.1
 */
@FunctionalInterface
public interface Flag {
    /**
     * Uses this feature on {@link Embed#require()}, if the bean
     * requires a field or method annotated by {@link Expose} to take effect
     *
     * <pre>{@code
     *  import plus.kat.anno.*;
     *  import static plus.kat.Flag.*;
     *
     *  @Embed(
     *     require = SEALED
     *  )
     *  class User {
     *      @Expose
     *      public int id; // It will be used
     *      public String name; // It won't be used
     *
     *      // It will be used
     *      @Expose
     *      public String getAlias() {
     *          return this.name;
     *      }
     *
     *      // It won't be used
     *      public void setAlias(String name) {
     *          this.name = name;
     *      }
     *  }
     * }</pre>
     */
    long SEALED = 0x1;

    /**
     * Uses this feature on {@link Expose#require()}, if a field or
     * method requires that the value of setter is non-null before it
     * is updated, the value of getter is serialized only if it is non-null
     *
     * <pre>{@code
     *  import plus.kat.anno.*;
     *  import static plus.kat.Flag.*;
     *
     *  class User {
     *      @Expose(
     *         require = NOTNULL
     *      )
     *      public String name;
     *
     *      @Expose(
     *         require = NOTNULL
     *      )
     *      public String getAlias() {
     *          return this.name;
     *      }
     *
     *      @Expose(
     *         require = NOTNULL
     *      )
     *      public setAlias(String name) {
     *          this.name = name;
     *      }
     *  }
     * }</pre>
     */
    long NOTNULL = 0x1;

    /**
     * Uses this feature on {@link Expose#require()}, if a field
     * or method requires that it can only be serialized, not updated
     *
     * <pre>{@code
     *  import plus.kat.anno.*;
     *  import static plus.kat.Flag.*;
     *
     *  class User {
     *      @Expose(
     *         require = READONLY
     *      )
     *      public String name;
     *  }
     * }</pre>
     */
    long READONLY = 0x2;

    /**
     * Uses this feature on {@link Expose#require()}, if a field
     * or method requires that it can only be updated rather than serialized
     *
     * <pre>{@code
     *  import plus.kat.anno.*;
     *  import static plus.kat.Flag.*;
     *
     *  class User {
     *      @Expose(
     *         require = INTERNAL
     *      )
     *      public String name;
     *  }
     * }</pre>
     */
    long INTERNAL = 0x4;

    /**
     * Uses this feature on {@link Expose#require()},
     * if a field or method requires that it cannot be used
     * as setter and getter, it means that it will not take effect
     *
     * <pre>{@code
     *  import plus.kat.anno.*;
     *  import static plus.kat.Flag.*;
     *
     *  class User {
     *      @Expose(
     *         require = EXCLUDED
     *      )
     *      public String name;
     *
     *      @Expose(
     *         require = EXCLUDED
     *      )
     *      public String getAlias() {
     *          return this.name;
     *      }
     *  }
     * }</pre>
     */
    long EXCLUDED = 0x6;

    /**
     * Uses this feature on {@link Expose#require()}, if a field
     * or method requires that the elements inside it be serialized
     *
     * <pre>{@code
     *  import plus.kat.anno.*;
     *  import static plus.kat.Flag.*;
     *
     *  class User {
     *      @Expose(
     *         require = UNWRAPPED
     *      )
     *      public Map<?, ?> extra;
     *  }
     * }</pre>
     */
    long UNWRAPPED = 0x8;

    /**
     * Uses this feature in serialization,
     * if serialization is required to be pure, that means erasing types, etc
     *
     * <pre>{@code
     *  import plus.kat.Kat;
     *  import plus.kat.Flag;
     *
     *  class User {
     *      public int id;
     *      public String name;
     *  }
     *
     *  User user = new User(1, "kraity");
     *  // {:id(1):name(kraity)}
     *  String text = Kat.encode(user, Flag.PURE);
     * }</pre>
     */
    long PURE = Long.MIN_VALUE;

    /**
     * Uses this feature in serialization,
     * if the serialization result is required to be formatted
     *
     * <pre>{@code
     *  import plus.kat.Kat;
     *  import plus.kat.Flag;
     *
     *  User user = ...;
     *
     *  // User{
     *  //   i:id(1)
     *  //   s:name(kraity)
     *  // }
     *  String text = Kat.encode(user, Flag.PRETTY);
     *
     *  // {
     *  //   "id": 1,
     *  //   "name": "kraity"
     *  // }
     *  String text = Json.encode(user, Flag.PRETTY);
     * }</pre>
     */
    long PRETTY = 0x1;

    /**
     * Uses this feature in serialization,
     * if a character in the serialization result
     * is greater than 0x7F, it is converted to unicode format
     *
     * <pre>{@code
     *  import plus.kat.Kat;
     *  import plus.kat.Flag;
     *
     *  User user = ...;
     *
     *  // User{i:id(1)s:name(^u9646^u4E4B^u5C87)}
     *  String text = Kat.encode(user, Flag.UNICODE);
     *  String text = Json.encode(user, Flag.UNICODE);
     * }</pre>
     */
    long UNICODE = 0x2;

    /**
     * Uses this feature in serialization
     */
    long ENUM_AS_INDEX = 0x10;

    /**
     * Uses this feature in serialization
     */
    long DATE_AS_DIGIT = 0x20;

    /**
     * Uses this feature in deserialization
     */
    long INDEX_AS_ENUM = 0x10;

    /**
     * Uses this feature in deserialization
     */
    long DIGIT_AS_DATE = 0x20;

    /**
     * Uses this feature in deserialization
     */
    long VALUE_AS_BEAN = 0x40;

    /**
     * Check if this {@link Object} uses the {@code flag}
     *
     * <pre>{@code
     *  Flag flag = ...
     *  boolean status = flag.isFlag(Flag.UNICODE);
     * }</pre>
     *
     * @param flag the specified {@code flag}
     */
    boolean isFlag(
        long flag
    );

    /**
     * Check if this {@link Object} uses the
     * {@code flag} under the specified branch {@code code}
     *
     * <pre>{@code
     *  Flag flag = ...
     *  // equivalent to 'flag.isFlag(0x1L);'
     *  boolean status = flag.isFlag(0x1L, 0);
     *
     *  // code=1, flag = 0x1L
     *  boolean status = flag.isFlag(0x1L, 1);
     *
     *  // code=1, flag = 0x2L
     *  boolean status = flag.isFlag(0x2L, 1);
     *
     *  // code=2, flag = 0x1L
     *  boolean status = flag.isFlag(0x1L, 2);
     *
     *  // code=2, flag = 0x2L
     *  boolean status = flag.isFlag(0x2L, 2);
     * }</pre>
     * <p>
     * Uses {@code code} as the distinguishing mark,
     * when code is '0', metamorphoses to {@link #isFlag(long)},
     * otherwise use {@code code} as branch to check the {@code flag}
     *
     * @param flag the specified {@code flag}
     * @param code the specified branch {@code code}
     * @since 0.0.3
     */
    default boolean isFlag(
        long flag, int code
    ) {
        return code == 0 && isFlag(flag);
    }
}
