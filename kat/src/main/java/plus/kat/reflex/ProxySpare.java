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
import plus.kat.entity.*;
import plus.kat.stream.*;
import plus.kat.utils.*;

import java.lang.reflect.*;

/**
 * @author kraity
 * @since 0.0.3
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class ProxySpare extends Workman<Object> {

    private Class<?> proxy;
    private Constructor<?> cons;

    public ProxySpare(
        @NotNull Class klass,
        @NotNull Supplier supplier
    ) {
        super(klass, supplier);
    }

    public ProxySpare(
        @Nullable Embed embed,
        @NotNull Class klass,
        @NotNull Supplier supplier,
        @Nullable Provider provider
    ) {
        super(embed, klass, supplier, provider);
    }

    @Override
    protected void initialize() {
        onMethods(
            klass.getMethods()
        );
    }

    @Override
    public Object apply() {
        try {
            return apply(
                Alias.EMPTY
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object apply(
        Alias alias
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

                c.setAccessible(true);
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

    @Override
    public Setter set(
        @NotNull Object alias
    ) {
        return (Setter) getOrDefault(alias, null);
    }

    @Override
    public Setter set(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return (Setter) getOrDefault(
            alias.isEmpty() ? index : alias, null
        );
    }

    /**
     * @param methods the specified {@link Method} collection
     */
    private void onMethods(
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

                String id;
                byte[] name = Reflect
                    .alias(method);
                if (name == null) {
                    id = method.getName();
                } else {
                    id = Binary.ascii(name);
                }

                Task node = new Task(
                    method, expose, this
                );

                if (count != 0) {
                    if (expose == null) {
                        super.put(
                            id, node
                        );
                        continue;
                    }

                    String[] keys = expose.value();
                    if (keys.length == 0) {
                        super.put(
                            id, node
                        );
                        continue;
                    }

                    for (String alias : expose.value()) {
                        // check empty
                        if (!alias.isEmpty()) {
                            super.put(
                                alias, node
                            );
                        }
                    }
                } else {
                    super.putIfAbsent(
                        id, node
                    );
                    if (expose == null) {
                        setup(
                            id, node
                        );
                        continue;
                    }

                    if ((expose.mode() & Expose.HIDDEN) == 0) {
                        String[] keys = expose.value();
                        if (keys.length == 0) {
                            setup(
                                id, node
                            );
                        } else {
                            // register all aliases
                            for (int i = 0; i < keys.length; i++) {
                                setup(
                                    keys[i], i == 0 ? node : new Task(node)
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
    public static class Task extends Node<Object, Object> {

        final String alias;
        final Method method;
        final ProxySpare spare;

        public Task(
            Task node
        ) {
            super(node);
            spare = node.spare;
            alias = node.alias;
            method = node.method;
        }

        public Task(
            Method method,
            Expose expose,
            ProxySpare spare
        ) {
            super(expose, method, spare.supplier);
            this.spare = spare;
            this.method = method;

            method.setAccessible(true);
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
                    return Reflect.def(c);
                }
                return null;
            } catch (Throwable e) {
                throw new CallCrash(e);
            }
        }

        @Override
        public boolean accept(
            @NotNull Object bean,
            @Nullable Object value
        ) {
            if (value != null || (flags & Expose.NOTNULL) == 0) {
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
                    throw new CallCrash(e);
                }
            }
            return false;
        }
    }

    /**
     * @author kraity
     * @since 0.0.3
     */
    public static class Explorer extends KatMap<Object, Object> implements InvocationHandler {

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
                    return Reflect.def(c);
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

                throw new CallCrash(
                    c + " not supported"
                );
            }

            throw new CallCrash(
                "Unexpectedly, Not currently supported: " + method
            );
        }
    }
}
