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

import plus.kat.actor.*;

import java.lang.reflect.*;
import java.beans.Transient;

import static java.lang.reflect.Modifier.*;
import static plus.kat.stream.Toolkit.*;
import static plus.kat.spare.ReflectSpare.*;

/**
 * @author kraity
 * @since 0.0.2
 */
@SuppressWarnings("unchecked")
public class RecordSpare<T> extends SimpleSpare<T> {

    private int width;
    private Constructor<T> loader, builder;

    public RecordSpare(
        @Nilable String space,
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super(
            space, (Class<T>) klass, context
        );
        onFields(
            klass.getDeclaredFields()
        );
        onConstructors(
            klass.getDeclaredConstructors()
        );
    }

    @Override
    public T apply() {
        Constructor<T> maker = loader;
        if (maker != null) {
            try {
                return maker.newInstance(
                    (Object[]) null
                );
            } catch (Throwable e) {
                throw new IllegalStateException(
                    "Failed to call " + maker, e
                );
            }
        } else {
            throw new IllegalStateException(
                "Not found the builder of " + klass
            );
        }
    }

    @NotNull
    public T apply(
        @NotNull Object... args
    ) {
        if (args.length == 0) {
            return apply();
        }

        Constructor<T> maker = builder;
        if (maker != null) {
            try {
                return maker.newInstance(args);
            } catch (Throwable e) {
                throw new IllegalStateException(
                    "Failed to call " + maker, e
                );
            }
        } else {
            throw new IllegalStateException(
                "Not found the builder of " + klass
            );
        }
    }

    @Override
    public Factory getFactory(
        @Nullable Type type
    ) {
        return new Builder1<>(
            type, new Object[width], this
        );
    }

    private void onFields(
        @NotNull Field[] fields
    ) {
        Magic magic;
        Caller caller;

        for (Field field : fields) {
            int mask = field.getModifiers();
            if ((mask & (STATIC | TRANSIENT)) != 0) {
                continue;
            }

            magic = field.getAnnotation(Magic.class);
            ParamCaller param = new ParamCaller(
                width++, magic, field, context
            );

            String[] more = null;
            String name = field.getName();

            if (magic == null) {
                node(hash1(name), param).arguer = param;
            } else {
                more = magic.value();
                if (more.length == 0) {
                    node(hash1(name), param).arguer = param;
                } else {
                    for (String alias : more) {
                        Node node = node(
                            hash1(alias), param
                        );
                        if (node.arguer == null) {
                            node.arguer = param;
                            continue;
                        }
                        throw new IllegalStateException(
                            "Failed to set the property<" + alias + "> of `" +
                                klass.getName() + "` because it already exists"
                        );
                    }
                }
            }

            Method method;
            try {
                method = klass.getMethod(
                    name, (Class<?>[]) null
                );
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(
                    "Can't find the `" + name + "` method", e
                );
            }

            if (HAS_TRANSIENT) {
                Transient hidden = method
                    .getAnnotation(Transient.class);
                if (hidden != null && hidden.value()) {
                    continue;
                }
            }

            Magic magic2 = method.getAnnotation(Magic.class);
            if (magic2 == null) {
                caller = new MethodCaller(
                    magic == null ?
                        -1 : magic.index(),
                    magic, method, context, null
                );
            } else {
                more = magic2.value();
                caller = new MethodCaller(
                    magic2.index(),
                    magic2, method, context, null
                );
            }

            if (more == null ||
                more.length == 0) {
                show(name, caller);
                node(hash1(name), caller).getter = caller;
            } else {
                for (String alias : more) {
                    show(alias, caller);
                    node(hash1(alias), caller).getter = caller;
                }
            }
        }
    }

    private void onConstructors(
        @NotNull Constructor<?>[] constructors
    ) {
        Constructor<?> latest = null;
        stage:
        for (Constructor<?> ctor : constructors) {
            Class<?>[] ct = ctor.getParameterTypes();
            if (ct.length == 0) {
                if (!ctor.isAccessible()) {
                    ctor.setAccessible(true);
                }
                loader = (Constructor<T>) ctor;
                if (width == 0) {
                    builder = (Constructor<T>) ctor;
                    return;
                } else {
                    continue;
                }
            }
            if (ct.length == width) {
                if (latest == null) {
                    latest = ctor;
                    continue;
                }
                for (Node n : table) {
                    for (; n != null; n = n.next) {
                        if (n instanceof ParamCaller) {
                            Caller c = (Caller) n;
                            if (c.type != ct[c.index]) {
                                continue stage;
                            }
                        }
                    }
                }
                latest = ctor;
            }
        }

        if (latest != null) {
            if (!latest.isAccessible()) {
                latest.setAccessible(true);
            }
            builder = (Constructor<T>) latest;
        } else {
            throw new IllegalArgumentException(
                "No accurate constructor was found"
            );
        }
    }
}
