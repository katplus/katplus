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

import static java.lang.reflect.Modifier.*;
import static plus.kat.spare.ReflectSpare.*;

/**
 * @author kraity
 * @since 0.0.2
 */
@SuppressWarnings("unchecked")
public class RecordSpare<T> extends BeanSpare<T> {

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

    protected void onFields(
        @NotNull Field[] fields
    ) {
        for (Field field : fields) {
            int mask = field.getModifiers();
            if ((mask & (STATIC | TRANSIENT)) != 0) {
                continue;
            }

            Magic magic1 = field.getAnnotation(Magic.class);
            ParamWidget param = new ParamWidget(
                width++, field, magic1, this
            );

            String[] keys = null;
            String name = field.getName();

            if (magic1 == null) {
                setParameter(
                    name, param
                );
            } else {
                String[] ks = magic1.value();
                if (ks.length == 0) {
                    setParameter(
                        name, param
                    );
                } else {
                    for (String key : (keys = ks)) {
                        if (addParameter(key, param)) {
                            continue;
                        }
                        throw new IllegalStateException(
                            "Parameter for " + key + " has been setup"
                        );
                    }
                }
            }

            Method method;
            try {
                method = klass.getMethod(
                    field.getName()
                );
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(
                    "Can't find the `" + field.getName() + "` method", e
                );
            }

            Class<?>[] params = method.getParameterTypes();
            if (params.length > 1) {
                continue;
            }

            Magic magic2 = method.getAnnotation(Magic.class);
            Widget widget;
            if (magic2 == null) {
                widget = new MethodWidget(
                    magic1 == null ?
                        -1 : magic1.index(),
                    magic1, method, this, params
                );
                if (keys == null) {
                    setWriter(
                        name, widget
                    );
                } else {
                    for (String key : keys) {
                        setWriter(
                            key, widget
                        );
                    }
                }
            } else {
                widget = new MethodWidget(
                    magic2.index(),
                    magic2, method, this, params
                );
                keys = magic2.value();
                if (keys.length == 0) {
                    setWriter(
                        name, widget
                    );
                } else {
                    for (String key : keys) {
                        setWriter(
                            key, widget
                        );
                    }
                }
            }
        }
    }

    protected void onConstructors(
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
                for (Nodus n : table) {
                    for (; n != null; n = n.next) {
                        if (n instanceof ParamWidget) {
                            Widget m = (Widget) n;
                            if (m.type != ct[m.index]) {
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
