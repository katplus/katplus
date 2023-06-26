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

import plus.kat.*;
import plus.kat.actor.*;

import java.util.*;
import java.lang.reflect.*;

import java.beans.Transient;

import static java.lang.reflect.Modifier.*;
import static plus.kat.spare.ReflectSpare.*;

/**
 * @author kraity
 * @since 0.0.3
 */
@SuppressWarnings("unchecked")
public class ProxySpare extends BeanSpare<Object> {

    private Constructor<?> builder;

    private final Map<String, String>
        setter = new HashMap<>(),
        getter = new HashMap<>();

    public ProxySpare(
        @Nilable String space,
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super(
            space, (Class<Object>) klass, context
        );
        onMethods(
            klass.getMethods()
        );
    }

    @NotNull
    public Object apply() {
        Constructor<?> maker = builder;
        try {
            if (maker == null) {
                Class<?> clazz = klass;
                if (Proxy.class.isAssignableFrom(clazz)) {
                    builder = maker
                        = clazz.getConstructor(
                        InvocationHandler.class
                    );

                    if (!maker.isAccessible()) {
                        maker.setAccessible(true);
                    }
                } else {
                    ClassLoader cl =
                        clazz.getClassLoader();
                    if (cl == null) {
                        cl = Thread.currentThread()
                            .getContextClassLoader();
                        if (cl == null) {
                            cl = ClassLoader
                                .getSystemClassLoader();
                        }
                    }

                    Class<?> proxy = Proxy.getProxyClass(
                        cl, clazz, Holder.class
                    );
                    builder = maker
                        = proxy.getConstructor(
                        InvocationHandler.class
                    );

                    if (!maker.isAccessible()) {
                        maker.setAccessible(true);
                    }
                    context.active(proxy, this);
                }
            }

            return maker.newInstance(
                new Handler(
                    setter, getter
                )
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                "Failed to call " + maker, e
            );
        }
    }

    @SuppressWarnings("deprecation")
    protected void onMethods(
        @NotNull Method[] methods
    ) {
        for (Method method : methods) {
            Class<?>[] params =
                method.getParameterTypes();
            int count;
            if ((count = params.length) > 1) {
                continue;
            }

            int mask = method.getModifiers();
            if ((mask & STATIC) != 0) {
                continue;
            }

            if (HAS_TRANSIENT) {
                Transient hidden = method
                    .getAnnotation(Transient.class);
                if (hidden != null && hidden.value()) {
                    continue;
                }
            }

            Magic magic = method
                .getAnnotation(Magic.class);

            String name;
            String alias = method.getName();

            if (magic != null) {
                String[] keys = magic.value();
                if (keys.length != 0) {
                    name = keys[0];
                    Handle node = new Handle(
                        magic, name,
                        method, this, params
                    );

                    if (count == 0) {
                        addReader(name, node);
                        addWriter(name, node);
                        getter.put(alias, name);
                    } else {
                        setter.put(alias, name);
                        for (String key : keys) {
                            addReader(key, node);
                        }
                    }
                    continue;
                }
            }

            name = alias;
            int i = 1, len = name.length();

            stage:
            {
                // set
                char ch = name.charAt(0);
                if (ch == 's') {
                    if (count == 0 || len < 4 ||
                        name.charAt(i++) != 'e' ||
                        name.charAt(i++) != 't') {
                        break stage;
                    }
                }

                // get
                else if (ch == 'g') {
                    if (count != 0 || len < 4 ||
                        name.charAt(i++) != 'e' ||
                        name.charAt(i++) != 't') {
                        break stage;
                    }
                }

                // is
                else if (ch == 'i') {
                    if (count != 0 || len < 3 ||
                        name.charAt(i++) != 's') {
                        break stage;
                    }
                    Class<?> cls = method.getReturnType();
                    if (cls != boolean.class &&
                        cls != Boolean.class) {
                        break stage;
                    }
                } else {
                    break stage;
                }

                char c1 = name.charAt(i++);
                if (c1 < 'A' || 'Z' < c1) {
                    break stage;
                }

                if (i == len) {
                    name = String.valueOf(
                        (char) (c1 + 0x20)
                    );
                } else {
                    char c2 = name.charAt(i);
                    if (c2 < 'A' || 'Z' < c2) {
                        c1 += 0x20;
                    }

                    byte[] it = new byte[len - i + 1];
                    it[0] = (byte) c1;
                    name.getBytes(
                        i, len, it, 1
                    );
                    name = new String(
                        it, 0, 0, it.length
                    );
                }
                name = name.intern();
            }

            Handle node = new Handle(
                magic, name,
                method, this, params
            );

            addReader(name, node);
            if (count == 0) {
                addWriter(name, node);
                getter.put(alias, name);
            } else {
                setter.put(alias, name);
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    static final class Handle extends Visitor {

        final String alias;
        final Method method;

        public Handle(
            Magic magic,
            String alias,
            Method method,
            BeanSpare<?> spare,
            Class<?>[] params
        ) {
            super(
                magic == null ?
                    -1 : magic.index()
            );
            element = method;
            switch (params.length) {
                case 0: {
                    Class<?> cls = method.getReturnType();
                    if (cls.isPrimitive()) {
                        type = cls;
                    } else {
                        type = method.getGenericReturnType();
                    }
                    break;
                }
                case 1: {
                    Class<?> cls = params[0];
                    if (cls.isPrimitive()) {
                        type = cls;
                    } else {
                        type = method.getGenericParameterTypes()[0];
                    }
                    break;
                }
                default: {
                    throw new NullPointerException(
                        "The argument length of `" +
                            method.getName() + "` is greater than '1'"
                    );
                }
            }

            if (magic != null) {
                Class<?> agent = magic.agent();
                if (agent != void.class) {
                    setup(
                        agent, spare
                    );
                }
            }

            this.alias = alias;
            this.method = method;

            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
        }

        @Override
        public Object apply(
            @NotNull Object bean
        ) {
            try {
                if (bean instanceof Holder) {
                    return (
                        ((Holder) bean).$_$()
                    ).get(alias);
                } else {
                    return method.invoke(bean);
                }
            } catch (IllegalStateException e) {
                throw e;
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public boolean accept(
            @NotNull Object bean,
            @Nullable Object value
        ) {
            // Not operate when value is null
            if (value != null) {
                try {
                    if (bean instanceof Holder) {
                        (((Holder) bean).$_$())
                            .put(alias, value);
                    } else {
                        method.invoke(bean, value);
                    }
                    return true;
                } catch (IllegalStateException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new IllegalStateException(e);
                }
            }
            return false;
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    public interface Holder {
        Handler $_$();
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    public static class Handler extends HashMap<Object, Object> implements InvocationHandler {

        Map<String, String>
            setter, getter;

        public Handler(
            Map<String, String> setter,
            Map<String, String> getter
        ) {
            this.setter = setter;
            this.getter = getter;
        }

        @Override
        public Object invoke(
            Object proxy,
            Method method,
            Object[] params
        ) throws Throwable {
            String name = method.getName();
            switch (name) {
                case "$_$": {
                    return this;
                }
                case "equals": {
                    if (params == null ||
                        params.length != 1) {
                        break;
                    } else {
                        return equals(params[0]);
                    }
                }
                case "hashCode": {
                    if (params != null) {
                        break;
                    } else {
                        return System.identityHashCode(this);
                    }
                }
                default: {
                    // getter
                    if (params == null) {
                        String alias = getter.get(name);
                        if (alias != null) {
                            Object value = super.get(alias);
                            if (value != null) {
                                return value;
                            }

                            Class<?> type =
                                method.getReturnType();
                            if (type.isPrimitive()) {
                                return Spare.of(type).apply();
                            }
                            return null;
                        }
                    }

                    // setter
                    else if (params.length == 1) {
                        String alias = setter.get(name);
                        if (alias != null) {
                            super.put(
                                alias, params[0]
                            );

                            Class<?> type =
                                method.getReturnType();
                            if (type == void.class ||
                                type == Void.class) {
                                return null;
                            }

                            if (type.isInstance(proxy)) {
                                return proxy;
                            }

                            throw new IllegalAccessException(
                                "Not supported type: " + type
                            );
                        }
                    }
                }
            }

            throw new IllegalAccessException(
                "Not currently supported: " + method
            );
        }
    }
}
