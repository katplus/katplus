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
package plus.kat.reflex;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.spare.*;
import plus.kat.stream.*;
import plus.kat.utils.*;

import java.lang.reflect.*;
import java.util.HashMap;

/**
 * @author kraity
 * @since 0.0.3
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ProxySpare extends AbstractSpare<Object> {

    private Class<?> proxy;
    private Constructor<?> cons;

    public ProxySpare(
        @Nullable Embed embed,
        @NotNull Class klass,
        @NotNull Supplier supplier
    ) {
        super(embed, klass, supplier);
        onMethods(
            klass.getMethods()
        );
    }

    @Override
    public Object apply(
        @NotNull Alias alias
    ) throws Crash {
        try {
            Constructor<?> c = cons;
            if (c == null) {
                ClassLoader cl = klass.getClassLoader();
                if (cl == null) {
                    cl = Thread.currentThread()
                        .getContextClassLoader();
                    if (cl == null) {
                        cl = ClassLoader.getSystemClassLoader();
                    }
                }

                proxy = Proxy.getProxyClass(cl, klass);
                cons = c = proxy.getConstructor(
                    InvocationHandler.class
                );

                if (!c.isAccessible()) {
                    c.setAccessible(true);
                }
                supplier.embed(proxy, this);
            }

            return c.newInstance(
                new Explorer()
            );
        } catch (Exception e) {
            throw new Crash(
                "Failed to create", e
            );
        }
    }

    protected void onMethods(
        @NotNull Method[] methods
    ) {
        for (Method method : methods)
            try {
                int count = method.
                    getParameterCount();
                if (count > 1) {
                    continue;
                }

                // filter invalid
                int mod = method.getModifiers();
                if ((mod & Modifier.STATIC) != 0) {
                    continue;
                }

                Expose expose = method
                    .getAnnotation(
                        Expose.class
                    );

                String name = Find.name(method);
                if (name == null) {
                    name = method.getName();
                }

                Handle node = new Handle(
                    method, expose, this
                );

                if (count != 0) {
                    if (expose == null) {
                        setReader(
                            name, node
                        );
                        continue;
                    }

                    String[] keys = expose.value();
                    if (keys.length == 0) {
                        setReader(
                            name, node
                        );
                        continue;
                    }

                    for (String alias : expose.value()) {
                        if (!alias.isEmpty()) {
                            setReader(
                                alias, node
                            );
                        }
                    }
                } else {
                    setReader(
                        false, name, node
                    );
                    if (expose == null) {
                        setWriter(
                            name, node
                        );
                        continue;
                    }

                    if (!It.internal(expose.require())) {
                        String[] keys = expose.value();
                        if (keys.length == 0) {
                            setWriter(
                                name, node
                            );
                        } else {
                            for (String key : keys) {
                                setWriter(
                                    key, node
                                );
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Nothing
            }
    }

    /**
     * @author kraity
     * @since 0.0.3
     */
    public static class Handle extends Medium<Object, Object> {

        final String alias;
        final Method method;
        final ProxySpare spare;

        public Handle(
            Method method,
            Expose expose,
            ProxySpare spare
        ) {
            super(method, expose);
            coder = spare.inflate(
                expose, this
            );

            this.spare = spare;
            this.method = method;

            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            String name = method.getName();
            if (!name.startsWith("set")) {
                alias = name;
            } else {
                alias = 'g' + name.substring(1);
            }
        }

        @Override
        public Object apply(
            @NotNull Object bean
        ) {
            try {
                Class<?> cl = bean.getClass();
                if (cl != spare.proxy) {
                    return method.invoke(bean);
                }

                Object val = ((Explorer) Proxy
                    .getInvocationHandler(bean)
                ).get(alias);
                if (val != null) {
                    return val;
                }

                Class<?> c = method
                    .getReturnType();
                if (c.isPrimitive()) {
                    return Find.value(c);
                }
                return null;
            } catch (Throwable e) {
                throw new Collapse(e);
            }
        }

        @Override
        public boolean accept(
            @NotNull Object bean,
            @Nullable Object value
        ) {
            if (value != null || (flags & It.NotNull) == 0) {
                try {
                    Class<?> cl = bean.getClass();
                    if (cl != spare.proxy) {
                        method.invoke(
                            bean, value
                        );
                    } else {
                        ((Explorer) Proxy
                            .getInvocationHandler(bean)
                        ).put(
                            alias, value
                        );
                    }
                    return true;
                } catch (Throwable e) {
                    throw new Collapse(e);
                }
            }
            return false;
        }
    }

    /**
     * @author kraity
     * @since 0.0.3
     */
    public static class Explorer extends HashMap<Object, Object> implements InvocationHandler {

        @Override
        public Object invoke(
            Object proxy,
            Method method,
            Object[] data
        ) {
            String key = method.getName();
            if (data == null) {
                if (key.equals("hashCode")) {
                    return hashCode();
                }

                Object val = get(key);
                if (val != null) {
                    return val;
                }

                Class<?> c = method
                    .getReturnType();
                if (c.isPrimitive()) {
                    return Find.value(c);
                }
                return null;
            }

            if (data.length == 1) {
                Object val = data[0];
                if (key.equals("equals")) {
                    return proxy == val ||
                        super.equals(val);
                }

                if (!key.startsWith("set")) {
                    super.put(
                        key, val
                    );
                } else {
                    super.put(
                        'g' + key.substring(1), val
                    );
                }

                Class<?> c = method
                    .getReturnType();
                if (c == void.class ||
                    c == Void.class) {
                    return null;
                }

                if (c.isInstance(proxy)) {
                    return proxy;
                }

                throw new Collapse(
                    c + " not supported"
                );
            }

            throw new Collapse(
                "Unexpectedly, Not currently supported: " + method
            );
        }
    }
}
