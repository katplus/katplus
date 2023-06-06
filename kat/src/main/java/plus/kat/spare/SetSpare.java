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

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import plus.kat.*;
import plus.kat.chain.*;

import static plus.kat.spare.Parser.*;
import static plus.kat.stream.Toolkit.*;

/**
 * @author kraity
 * @since 0.0.1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class SetSpare extends BaseSpare<Set> {

    public static final SetSpare
        INSTANCE = new SetSpare(HashSet.class);

    public SetSpare(
        @NotNull Class<?> klass
    ) {
        super((Class<Set>) klass);
    }

    @Override
    public Set apply() {
        return apply(klass);
    }

    @Override
    public Set apply(
        @Nullable Type type
    ) {
        if (type == null) {
            type = klass;
        }

        if (type == Set.class ||
            type == HashSet.class) {
            return new HashSet<>();
        }

        if (type == LinkedHashSet.class) {
            return new LinkedHashSet<>();
        }

        if (type == TreeSet.class ||
            type == SortedSet.class ||
            type == NavigableSet.class) {
            return new TreeSet<>();
        }

        if (type == ConcurrentSkipListSet.class) {
            return new ConcurrentSkipListSet<>();
        }

        if (type == AbstractSet.class) {
            return new HashSet<>();
        }

        throw new IllegalStateException(
            "Failed to build this " + type
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
        return new ListSpare.Builder0(
            type, klass, this
        );
    }

    @Override
    public Set read(
        @NotNull Flag flag,
        @NotNull Value data
    ) throws IOException {
        if (data.isNothing()) {
            return null;
        }

        if (flag.isFlag(Flag.VALUE_AS_BEAN)) {
            Algo algo = algoOf(data);
            if (algo == null) {
                return null;
            }
            try (Parser op = with(this)) {
                return op.solve(
                    algo, Flow.of(data)
                );
            }
        }

        throw new IOException(
            "Failed to parse the value to `" + klass
                + "` unless `Flag.VALUE_AS_BEAN` is enabled"
        );
    }

    @Override
    public void write(
        @NotNull Chan chan,
        @NotNull Object value
    ) throws IOException {
        for (Object elem : (Iterable<?>) value) {
            chan.set(
                null, elem
            );
        }
    }
}
