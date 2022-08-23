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

    private Class<?>[] interfaces;
    private Class<?> proxy;
    private ClassLoader classLoader;

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
            Class<?>[] is = interfaces;
            if (is == null) {
                ClassLoader cl = klass.getClassLoader();
                if (cl == null) {
                    cl = Thread.currentThread()
                        .getContextClassLoader();
                    if (cl == null) {
                        cl = ClassLoader.getSystemClassLoader();
                    }
                }
                classLoader = cl;
                interfaces = is = new Class[]{klass};

                Object o = Proxy
                    .newProxyInstance(
                        cl, is, new Explorer()
                    );

                supplier.embed(
                    proxy = o.getClass(), this
                );
                return o;
            }
            return Proxy.newProxyInstance(
                classLoader, is, new Explorer()
            );
        } catch (Exception e) {
            throw new Crash(
                "Failed to create", e
            );
        }
    }

    @Override
    public Setter setter(
        @NotNull Object alias
    ) {
        return (Setter) get(alias);
    }

    @Override
    public Setter setter(
        @NotNull int index,
        @NotNull Alias alias
    ) {
        return (Setter) get(
            alias.isEmpty() ? index : alias
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

                byte[] name = Reflect
                    .alias(method);
                if (name == null) {
                    continue;
                }

                Expose expose = method
                    .getAnnotation(
                        Expose.class
                    );

                Task node = new Task(
                    method, expose, this
                );

                String id = Binary.ascii(name);
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

                    if (expose.export()) {
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
    public static class Task
        extends Node<Object>
        implements Setter<Object, Object> {

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
                alias = "g" + name.substring(1);
            }
        }

        @Override
        public Object apply(
            @NotNull Object it
        ) {
            try {
                Class<?> cl = it.getClass();
                if (cl != spare.proxy) {
                    return method.invoke(it);
                }

                return ((Explorer) Proxy
                    .getInvocationHandler(it)
                ).get(alias);
            } catch (Throwable e) {
                // Nothing
            }
            return null;
        }

        @Override
        public void accept(
            @NotNull Object it,
            @Nullable Object val
        ) {
            if (val != null || nullable) {
                try {
                    Class<?> cl = it.getClass();
                    if (cl != spare.proxy) {
                        method.invoke(
                            it, val
                        );
                    } else {
                        ((Explorer) Proxy
                            .getInvocationHandler(it)
                        ).put(
                            alias, val
                        );
                    }
                } catch (Throwable e) {
                    // Nothing
                }
            }
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
            Object[] args
        ) {
            String name = method.getName();
            if (args == null) {
                if (name.equals("hashCode")) {
                    return hashCode();
                } else {
                    return super.get(name);
                }
            }

            if (args.length == 1) {
                if (name.startsWith("set")) {
                    super.put(
                        "g" + name.substring(1), args[0]
                    );
                }
                if (name.equals("equals")) {
                    return proxy == args[0] ||
                        super.equals(args[0]);
                }
            }

            return null;
        }
    }
}
