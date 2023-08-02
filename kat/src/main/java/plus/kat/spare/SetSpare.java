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
package plus.kat.spare;

import plus.kat.actor.NotNull;
import plus.kat.actor.Nullable;

import plus.kat.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class SetSpare extends BeanSpare<Set> {

    public static final SetSpare
        INSTANCE = new SetSpare(Set.class);

    final int mode;

    public SetSpare(
        @NotNull Class<?> klass
    ) {
        super(
            (Class<Set>) klass
        );
        if (klass == Set.class ||
            klass == HashSet.class ||
            klass == AbstractSet.class) {
            mode = 1;
        } else if (klass == TreeSet.class ||
            klass == SortedSet.class ||
            klass == NavigableSet.class) {
            mode = 2;
        } else if (klass == LinkedHashSet.class) {
            mode = 3;
        } else if (klass == ConcurrentSkipListSet.class) {
            mode = 4;
        } else {
            mode = -1;
        }
    }

    @Override
    public Set apply() {
        switch (mode) {
            case 1: {
                return new HashSet<>();
            }
            case 2: {
                return new TreeSet<>();
            }
            case 3: {
                return new LinkedHashSet<>();
            }
            case 4: {
                return new ConcurrentSkipListSet<>();
            }
            case 0: {
                return null;
            }
            case -1: {
                try {
                    return klass.newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException(
                        "Failed to build " + klass, e
                    );
                }
            }
        }

        throw new IllegalStateException(
            "Failed to build " + klass
        );
    }

    @Override
    public Set apply(
        @NotNull Object... args
    ) {
        switch (args.length) {
            case 0: {
                return apply();
            }
            case 1: {
                Object arg = args[0];
                if (arg instanceof Collection) {
                    Set set = apply();
                    if (set != null) {
                        set.addAll(
                            (Collection) arg
                        );
                    }
                    return set;
                }
            }
        }

        throw new IllegalStateException(
            "No matching constructor found"
        );
    }

    @Override
    public String getSpace() {
        return "Set";
    }

    @Override
    public Boolean getScope() {
        return Boolean.FALSE;
    }

    @Override
    public Border getBorder(
        @NotNull Flag flag
    ) {
        return Border.BRACKET;
    }

    @Override
    public Factory getFactory(
        @Nullable Type type
    ) {
        return new ListSpare.Builder0(type, this);
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        for (Object element : (Set<?>) value) {
            chan.set(
                null, element
            );
        }
    }
}
