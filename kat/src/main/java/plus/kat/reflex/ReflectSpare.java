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

import plus.kat.anno.NotNull;
import plus.kat.anno.Nullable;

import java.lang.annotation.*;
import java.lang.reflect.*;

import plus.kat.*;
import plus.kat.anno.*;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.entity.*;
import plus.kat.utils.KatMap;

/**
 * @author kraity
 * @since 0.0.2
 */
public class ReflectSpare<E> extends AspectSpare<E> {

    private Constructor<E> builder;
    private KatMap<Object, Param1> params;

    /**
     * @throws SecurityException If the {@link Constructor#setAccessible(boolean)} is denied
     */
    public ReflectSpare(
        @NotNull Class<E> klass,
        @NotNull Supplier supplier
    ) {
        this(klass.getAnnotation(Embed.class), klass, supplier);
    }

    /**
     * @throws SecurityException If the {@link Constructor#setAccessible(boolean)} is denied
     */
    public ReflectSpare(
        @Nullable Embed embed,
        @NotNull Class<E> klass,
        @NotNull Supplier supplier
    ) {
        super(embed, klass, supplier);
    }

    @Override
    @Nullable
    public E apply(
        @NotNull Alias alias
    ) throws Crash {
        try {
            return builder.newInstance();
        } catch (Exception e) {
            throw new Crash(e);
        }
    }

    @Override
    public Builder<E> getBuilder(
        @Nullable Type type
    ) {
        if (params == null) {
            return new Builder0<>(this);
        }
        return new Builder1<>(this);
    }

    private void register(
        @NotNull Constructor<?> b,
        @NotNull Supplier supplier
    ) {
        Parameter[] ps = null;
        params = new KatMap<>();

        Class<?>[] cs = b.getParameterTypes();
        Type[] ts = b.getGenericParameterTypes();
        Annotation[][] as = b.getParameterAnnotations();

        for (int i = 0; i < cs.length; i++) {
            Format format = null;
            Expose expose = null;
            for (Annotation a : as[i]) {
                Class<?> at = a.annotationType();
                if (at == Expose.class) {
                    expose = (Expose) a;
                } else if (at == Format.class) {
                    format = (Format) a;
                }
            }

            Coder<?> c = Reflex.lookup(
                cs[i], expose, format, supplier
            );

            Param1 param =
                new Param1(
                    i, c, ts[i], cs[i]
                );

            if (expose == null) {
                if (ps == null) {
                    ps = b.getParameters();
                }
                params.put(
                    ps[i].getName(), param
                );
            } else {
                String[] keys = expose.value();
                for (int k = 0; k < keys.length; k++) {
                    params.put(
                        keys[k], k == 0 ? param : param.clone()
                    );
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onHandle() {
        Constructor<?>[] a =
            klass.getDeclaredConstructors();
        Constructor<?> b = a[0];
        for (int i = 1; i < a.length; i++) {
            if (b.getParameterCount() <
                a[i].getParameterCount()) {
                b = a[i];
            }
        }

        b.setAccessible(true);
        builder = (Constructor<E>) b;
        if (b.getParameterCount() != 0) {
            register(b, supplier);
        }
    }

    @Override
    protected void onFields() {
        boolean direct = (flags & Embed.DIRECT) != 0;
        for (Field field : klass.getDeclaredFields()) {
            Expose expose = field
                .getAnnotation(
                    Expose.class
                );
            if (expose == null) continue;

            Field1<E> field1 =
                new Field1<>(
                    field, expose, supplier
                );

            int h = field1.getHash();
            // check use index
            if (direct && h > -1) put(
                h, field1.clone()
            );

            String[] keys = expose.value();
            if (keys.length == 0) {
                String name = field.getName();
                if (expose.export()) {
                    // register getter
                    addGetter(name, field1);
                    // register setter
                    put(name, field1.clone());
                } else put(
                    name, field1
                );
            } else {
                // register only the first alias
                if (expose.export()) {
                    addGetter(keys[0], field1);
                }

                for (String alias : keys) {
                    // check empty
                    if (!alias.isEmpty()) put(
                        alias, field1.clone()
                    );
                }
            }
        }
    }

    @Override
    protected void onMethods() {
        boolean sealed = (flags & Embed.SEALED) != 0;
        boolean direct = (flags & Embed.DIRECT) != 0;

        for (Method method : klass.getDeclaredMethods()) {
            int count = method.getParameterCount();
            if (count > 1) continue;

            Method1<E> method1;
            Expose expose = method
                .getAnnotation(
                    Expose.class
                );

            // via Expose
            if (expose != null) {
                // have aliases
                String[] keys = expose.value();
                if (keys.length != 0) {
                    method1 = new Method1<>(
                        method, expose, supplier
                    );

                    if (count != 0) {
                        int h = method1.getHash();
                        // check use index
                        if (direct && h > -1) {
                            // register setter
                            put(h, method1);
                        }

                        for (String alias : keys) {
                            // check empty
                            if (!alias.isEmpty()) put(
                                alias, method1.clone()
                            );
                        }
                    } else {
                        // register all aliases
                        for (int i = 0; i < keys.length; i++) {
                            addGetter(
                                keys[i], i == 0 ? method1 : method1.clone()
                            );
                        }
                    }
                    continue;
                }

                // empty alias and use index
                else if (direct && count == 1) {
                    int h = expose.index();
                    // check use index
                    if (h > -1) {
                        method1 = new Method1<>(
                            method, expose, supplier
                        );

                        // register setter
                        put(h, method1);
                        // use index only
                        continue;
                    }
                }

                // directly via POJO
            }

            // check if via POJO
            else if (sealed) {
                continue;
            }

            String key = method.getName();
            int i = 0, l = key.length();
            if (l < 4) {
                continue;
            }

            char ch = key.charAt(i++);
            if (ch == 's') {
                if (count == 0 ||
                    key.charAt(i++) != 'e' ||
                    key.charAt(i++) != 't') {
                    continue;
                }
            } else if (ch == 'g') {
                if (count != 0 ||
                    key.charAt(i++) != 'e' ||
                    key.charAt(i++) != 't') {
                    continue;
                }
                if (l == 8 &&
                    key.charAt(i) == 'C' &&
                    key.charAt(i + 1) == 'l' &&
                    key.charAt(i + 2) == 'a' &&
                    key.charAt(i + 3) == 's' &&
                    key.charAt(i + 4) == 's') {
                    continue;
                }
            } else if (ch == 'i') {
                if (count != 0 ||
                    key.charAt(i++) != 's') {
                    continue;
                }
            } else {
                continue;
            }

            ch = key.charAt(i);
            if (ch < 'A' || 'Z' < ch) {
                continue;
            }

            byte[] k = new byte[l - i];
            k[0] = (byte) (ch + 0x20);

            for (int o = 1; ++i < l; ) {
                k[o++] = (byte) key.charAt(i);
            }

            method1 = new Method1<>(
                method, expose, supplier
            );

            Alias alias = new Alias(k);
            if (count != 0) {
                // register setter
                put(alias, method1);
            } else {
                // register getter
                addGetter(
                    alias, method1
                );
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Field1<K> extends Node<K>
        implements Setter<K, Object>, Getter<K, Object> {

        final Field field;
        final Coder<?> coder;
        final Type type;
        final Class<?> klass;
        final boolean nullable;

        public Field1(
            Field1<?> field
        ) {
            this.field = field.field;
            this.coder = field.coder;
            this.type = field.type;
            this.klass = field.klass;
            this.nullable = field.nullable;
        }

        public Field1(
            Field field,
            Expose expose,
            Supplier supplier
        ) {
            super(expose == null
                ? 0 : expose.index()
            );
            this.field = field;
            field.setAccessible(true);

            this.klass = field.getType();
            this.type = field.getGenericType();
            this.nullable = field.getAnnotation(NotNull.class) == null;

            Format format = field
                .getAnnotation(Format.class);
            this.coder = Reflex.lookup(
                klass, expose, format, supplier
            );
        }

        @Override
        @Nullable
        public Object apply(
            @NotNull K it
        ) {
            try {
                return field.get(it);
            } catch (Exception e) {
                // nothing
            }
            return null;
        }

        @Override
        @Nullable
        public Object onApply(
            @NotNull Object it
        ) {
            try {
                return field.get(it);
            } catch (Exception e) {
                // nothing
            }
            return null;
        }

        @Override
        public void accept(
            @NotNull K it,
            @Nullable Object val
        ) {
            if (val != null || nullable) {
                try {
                    field.set(it, val);
                } catch (Exception e) {
                    // nothing
                }
            }
        }

        @Override
        public void onAccept(
            @NotNull K it,
            @Nullable Object val
        ) {
            if (val != null || nullable) {
                try {
                    field.set(it, val);
                } catch (Exception e) {
                    // nothing
                }
            }
        }

        @Override
        public Coder<?> getCoder() {
            return coder;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Class<?> getKlass() {
            return klass;
        }

        @Override
        public Field1<K> clone() {
            return new Field1<>(this);
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Method1<K> extends Node<K>
        implements Setter<K, Object>, Getter<K, Object> {

        final Method method;
        final Coder<?> coder;
        final Type type;
        final Class<?> klass;

        public Method1(
            Method1<?> method
        ) {
            this.coder = method.coder;
            this.type = method.type;
            this.klass = method.klass;
            this.method = method.method;
        }

        public Method1(
            Method method,
            Expose expose,
            Supplier supplier
        ) {
            super(expose == null
                ? 0 : expose.index()
            );
            switch (method.getParameterCount()) {
                case 0: {
                    this.klass = method.getReturnType();
                    this.type = klass;
                    break;
                }
                case 1: {
                    this.klass = method.getParameterTypes()[0];
                    this.type = method.getGenericParameterTypes()[0];
                    break;
                }
                default: {
                    throw new NullPointerException(
                        "Unexpectedly the parameter length of '" + method.getName() + "' is greater than '1'"
                    );
                }
            }

            this.method = method;
            method.setAccessible(true);

            Format format = method
                .getAnnotation(Format.class);
            this.coder = Reflex.lookup(
                klass, expose, format, supplier
            );
        }

        @Override
        @Nullable
        public Object apply(
            @NotNull K it
        ) {
            try {
                return method.invoke(it);
            } catch (Exception e) {
                // nothing
            }
            return null;
        }

        @Override
        @Nullable
        public Object onApply(
            @NotNull Object it
        ) {
            try {
                return method.invoke(it);
            } catch (Exception e) {
                // nothing
            }
            return null;
        }

        @Override
        public void accept(
            @NotNull K it,
            @Nullable Object val
        ) {
            try {
                method.invoke(it, val);
            } catch (Exception e) {
                // nothing
            }
        }

        @Override
        public void onAccept(
            @NotNull K it,
            @Nullable Object val
        ) {
            try {
                method.invoke(it, val);
            } catch (Exception e) {
                // nothing
            }
        }

        @Override
        public Coder<?> getCoder() {
            return coder;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Class<?> getKlass() {
            return klass;
        }

        @Override
        public Method1<K> clone() {
            return new Method1<>(this);
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Param1 extends
        KatMap.Entry<String, Param1> {

        final int index;
        final Coder<?> coder;
        final Type type;
        final Class<?> klass;

        public Param1(
            int index, Coder<?> coder,
            Type type, Class<?> klass
        ) {
            super(0);
            this.index = index;
            this.coder = coder;
            this.type = type;
            this.klass = klass;
        }

        @Override
        public Param1 clone() {
            return new Param1(
                index, coder, type, klass
            );
        }
    }

    /**
     * @author kraity
     * @since 0.0.2
     */
    static class Builder1<K> extends Builder0<K> {

        private Object[] params;
        private Constructor<K> c;

        private int count;
        private Param1 param;
        private Cache<K> cache;

        private ReflectSpare<K> reflex;
        private KatMap<Object, Param1> a;

        public Builder1(
            @NotNull ReflectSpare<K> reflex
        ) {
            super(reflex);
            this.reflex = reflex;
        }

        @Override
        public void create(
            @NotNull Alias alias
        ) {
            a = reflex.params;
            c = reflex.builder;
            params = new Object[c.getParameterCount()];
        }

        @Override
        public void accept(
            @NotNull Space space,
            @NotNull Alias alias,
            @NotNull Value value
        ) throws IOCrash {
            if (entity == null) {
                Param1 param = a.get(alias);
                if (param != null) {
                    ++index; // increment

                    // specified coder
                    Coder<?> coder = param.coder;

                    if (coder != null) {
                        params[param.index] =
                            coder.read(
                                flag, value
                            );
                    } else {
                        // specified spare
                        coder = supplier.embed(
                            param.klass
                        );

                        // skip if null
                        if (coder != null) {
                            params[param.index] =
                                coder.read(
                                    flag, value
                                );
                        }
                    }

                    embark();
                    return;
                }
            }

            super.accept(
                space, alias, value
            );
        }

        @Override
        public Builder<?> observe(
            @NotNull Space space,
            @NotNull Alias alias
        ) throws IOCrash {
            if (entity == null) {
                param = a.get(alias);
                if (param != null) {
                    ++index; // increment

                    // specified coder
                    Coder<?> coder = param.coder;

                    if (coder == null) {
                        // specified spare
                        coder = supplier.embed(
                            param.klass
                        );

                        if (coder == null) {
                            return null;
                        }
                    }

                    return coder.getBuilder(param.type);
                }
            }

            return super.observe(
                space, alias
            );
        }

        @Override
        public void dispose(
            @Nullable Object value
        ) throws IOCrash {
            if (entity != null) {
                setter.onAccept(
                    entity, value
                );
            } else {
                Cache<K> c = new Cache<>();
                c.value = value;
                c.setter = setter;

                if (cache == null) {
                    cache = c;
                } else {
                    cache.next = c;
                }
            }
        }

        @Override
        public void dispose(
            @NotNull Builder<?> child
        ) throws IOCrash {
            if (entity != null) {
                setter.onAccept(
                    entity, child.bundle()
                );
            } else if (param == null) {
                dispose(
                    child.bundle()
                );
            } else {
                params[param.index] = child.bundle();
                param = null;
                embark();
            }
        }

        static class Cache<K> {
            Object value;
            Cache<K> next;
            Setter<K, ?> setter;
        }

        private void embark()
            throws IOCrash {
            if (++count == params.length) {
                try {
                    entity = c.newInstance(params);
                    while (cache != null) {
                        cache.setter.onAccept(
                            entity, cache.value
                        );
                        cache = cache.next;
                    }
                } catch (Exception e) {
                    throw new IOCrash(e);
                }
            }
        }

        @Override
        public void close() {
            super.close();
            a = null;
            c = null;
            param = null;
            cache = null;
            params = null;
            reflex = null;
        }
    }
}
