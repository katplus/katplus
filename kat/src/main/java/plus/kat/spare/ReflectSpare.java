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

import java.lang.reflect.*;
import java.lang.annotation.*;

import java.beans.Transient;

import kotlin.jvm.internal.DefaultConstructorMarker;

import static java.lang.reflect.Modifier.*;

/**
 * @author kraity
 * @since 0.0.2
 */
@SuppressWarnings("unchecked")
public class ReflectSpare<T> extends SimpleSpare<T> {

    private Class<?> owner;
    private Class<?>[] types;

    private int extra;
    private boolean variable;
    private Constructor<T> loader, builder;

    public ReflectSpare(
        @Nilable String space,
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super(space, (Class<T>) klass, context);
        onConstructors(
            klass.getDeclaredConstructors()
        );
        do {
            onFields(
                klass.getDeclaredFields()
            );
            onMethods(
                klass.getDeclaredMethods()
            );
            grade++;
        } while (
            (klass = klass.getSuperclass()) != Object.class
        );
    }

    @NotNull
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
            int i = 0;
            Class<?>[] ts = types;

            int mask = extra;
            int count = ts.length;

            if (mask == 0) {
                for (; i < count; i++) {
                    if (args[i] == null) {
                        Class<?> c = ts[i];
                        if (c.isPrimitive()) {
                            args[i] = Spare.of(c).apply();
                        }
                    }
                }
            } else {
                for (int n = 1; n < mask; n++) {
                    int flag = 0,
                        mark = i + 32;
                    if (mark > count) {
                        mark = count;
                    }
                    for (; i < mark; i++) {
                        if (args[i] == null) {
                            flag |= (1 << i);
                            Class<?> c = ts[i];
                            if (c.isPrimitive()) {
                                args[i] = Spare.of(c).apply();
                            }
                        }
                    }
                    args[count - mask + n - 1] = flag;
                }
            }

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
        Class<?>[] as = types;
        if (as == null) {
            return new Builder0<>(type, this);
        }

        Class<?> own = owner;
        if (own == null && !variable) {
            return new Builder1<>(
                type, new Object[as.length], this
            );
        }

        return new Builder2<>(
            type, own, new Object[as.length], this
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
            if (magic == null && (mask & PUBLIC) == 0) {
                continue;
            }

            String name;
            String[] more = null;

            int size = 0;
            if (magic == null) {
                name = field.getName();
                caller = new FieldCaller(
                    -1, null, field, context
                );
            } else {
                more = magic.value();
                size = more.length;
                if (size != 0) {
                    name = more[0];
                } else {
                    name = field.getName();
                }
                caller = new FieldCaller(
                    magic.index(), magic, field, context
                );
            }

            Node node = node(
                hash1(name), caller
            );
            if (node.setter == null && node.getter == null) {
                variable = true;
                node.setter = caller;
                show(name, caller);
                node.getter = caller;
                for (int i = 1; i < size; i++) {
                    node = node(
                        hash1(more[i]), caller
                    );
                    if (node.setter == null) {
                        node.setter = caller;
                        continue;
                    }
                    throw new IllegalStateException(
                        "Failed to set the reader<" + more[i] + "> of `"
                            + klass.getName() + "` because it already exists"
                    );
                }
            } else {
                throw new IllegalStateException(
                    "Failed to set the property<" + name + "> of `" +
                        klass.getName() + "` because it already exists"
                );
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void onMethods(
        @NotNull Method[] methods
    ) {
        Magic magic;
        Caller caller;

        for (Method method : methods) {
            int mask = method.getModifiers();
            if ((mask & (STATIC | ABSTRACT)) != 0) {
                continue;
            }

            magic = method.getAnnotation(Magic.class);
            if (magic == null && (mask & PUBLIC) == 0) {
                continue;
            }

            if (HAS_TRANSIENT) {
                Transient hidden = method
                    .getAnnotation(Transient.class);
                if (hidden != null && hidden.value()) {
                    continue;
                }
            }

            int flag;
            Class<?>[] args = method.getParameterTypes();
            if ((flag = args.length) > 1) {
                continue;
            }

            int size = 0;
            int index = -1;

            String name;
            long hash = 0;
            String[] more = null;

            if (magic == null) {
                name = method.getName();
            } else {
                more = magic.value();
                size = more.length;
                index = magic.index();
                if (size != 0) {
                    name = more[0];
                    hash = hash1(name);
                } else {
                    name = method.getName();
                }
            }

            int i = 1,
                m = 0;
            char c1 = 0;

            if (size == 0) {
                m = name.length();
                c1 = name.charAt(0);

                // set
                if (c1 == 's') {
                    if (flag == 0 || m < 4 ||
                        name.charAt(i++) != 'e' ||
                        name.charAt(i++) != 't') {
                        continue;
                    }
                }

                // get
                else if (c1 == 'g') {
                    if (flag != 0 || m < 4 ||
                        name.charAt(i++) != 'e' ||
                        name.charAt(i++) != 't') {
                        continue;
                    }
                }

                // is
                else if (c1 == 'i') {
                    if (flag != 0 || m < 3 ||
                        name.charAt(i++) != 's') {
                        continue;
                    }
                    Class<?> cls = method.getReturnType();
                    if (cls != boolean.class &&
                        cls != Boolean.class) {
                        continue;
                    }
                } else {
                    continue;
                }

                c1 = name.charAt(i++);
                if (c1 < 'A' || 'Z' < c1) {
                    continue;
                }

                if (i == m) {
                    c1 += 0x20;
                } else {
                    char c2 = name.charAt(i);
                    if (c2 < 'A' || 'Z' < c2) {
                        c1 += 0x20;
                    }
                }

                hash = (c1 ^ FNV_BASIS) * FNV_PRIME;
                for (int j = i; j < m; j++) {
                    hash = (name.charAt(j) ^ hash) * FNV_PRIME;
                }
            }

            Node[] tab = table;
            if (tab != null) {
                Node node = tab[(int)
                    (hash & tab.length - 1)];

                while (node != null
                    && hash != node.hash) {
                    node = node.next;
                }
                if (node != null) {
                    if (flag == 0) {
                        if (node.getter != null) {
                            continue;
                        }
                    } else {
                        if (node.setter != null) {
                            continue;
                        }
                    }
                }
            }

            if (size == 0) {
                if (i == m) {
                    name = String.valueOf(c1);
                } else {
                    byte[] it = new byte[m - i + 1];
                    it[0] = (byte) c1;
                    name.getBytes(
                        i, m, it, 1
                    );
                    name = new String(
                        it, 0, 0, it.length
                    );
                }
                name = name.intern();
            }

            if (flag == 1) {
                caller = new MethodCaller(
                    index, magic,
                    method, context, args[0]
                );
                variable = true;
                node(hash, caller).setter = caller;
                for (i = 1; i < size; i++) {
                    Node node = node(
                        hash1(more[i]), caller
                    );
                    if (node.setter == null) {
                        node.setter = caller;
                        continue;
                    }
                    throw new IllegalStateException(
                        "Failed to set the reader<" + more[i] + "> of `"
                            + klass.getName() + "` because it already exists"
                    );
                }
            } else {
                caller = new MethodCaller(
                    index, magic,
                    method, context, null
                );
                show(name, caller);
                node(hash, caller).getter = caller;
                for (i = 1; i < size; i++) {
                    Node node = node(
                        hash1(more[i]), caller
                    );
                    if (node.getter == null) {
                        node.getter = caller;
                        continue;
                    }
                    throw new IllegalStateException(
                        "Failed to set the writer<" + more[i] + "> of `"
                            + klass.getName() + "` because it already exists"
                    );
                }
            }
        }
    }

    static int FLAG_PARAM_NAME;

    private void onConstructors(
        @NotNull Constructor<?>[] constructors
    ) {
        Class<?>[] bt = null, lt = null;
        Constructor<?> before = null, latest = null;

        for (Constructor<?> current : constructors) {
            Class<?>[] ct = current.getParameterTypes();
            if (ct.length != 0) {
                if (latest == null) {
                    lt = ct;
                    latest = current;
                    continue;
                }

                if (lt.length <= ct.length) {
                    if (IN_KOTLIN) {
                        bt = lt;
                        before = latest;
                    }
                    lt = ct;
                    latest = current;
                    continue;
                }

                if (IN_KOTLIN && bt != null) {
                    if (bt.length <= ct.length) {
                        bt = ct;
                        before = current;
                    }
                }
            } else {
                if (latest == null) {
                    lt = ct;
                    latest = current;
                }
                if (!current.isAccessible()) {
                    current.setAccessible(true);
                }
                loader = (Constructor<T>) current;
            }
        }

        if (latest == null) {
            throw new IllegalStateException(
                "No valid constructor found"
            );
        }

        int max = lt.length;
        if (max != 0) {
            if (!latest.isAccessible()) {
                latest.setAccessible(true);
            }
            types = lt;
            builder = (Constructor<T>) latest;

            // Run in kotlin
            if (before != null) {
                int i = bt.length;
                int j = i / 32 + 2;
                check:
                if (i + j == max) {
                    int e = max - 1;
                    for (int x = i; x < e; x++) {
                        if (lt[x] != int.class) {
                            break check;
                        }
                    }
                    if (lt[e] == DefaultConstructorMarker.class) {
                        max = i;
                        extra = j;
                        latest = before;
                    }
                }
            }

            Type[] ts = latest.getGenericParameterTypes();
            Annotation[][] as = latest.getParameterAnnotations();

            int pos = 0, off = as.length - max;
            Class<?> declaringClass = klass.getDeclaringClass();

            if (declaringClass != null &&
                (klass.getModifiers() & STATIC) == 0) {
                if (declaringClass == lt[0]) {
                    pos++;
                    owner = declaringClass;
                }
            }

            Magic magic;
            Parameter[] parameters = null;

            for (int fpn = FLAG_PARAM_NAME; pos < max; pos++) {
                int idx = pos + off;
                ParamCaller caller = new ParamCaller(
                    pos, ts[pos], context, as[idx]
                );

                magic = caller.getAnnotation(Magic.class);
                if (magic != null) {
                    String[] more = magic.value();
                    if (more.length != 0) {
                        for (String alias : more) {
                            Node node = node(
                                hash1(alias), caller
                            );
                            if (node.arguer == null) {
                                node.arguer = caller;
                                continue;
                            }
                            throw new IllegalStateException(
                                "Failed to set the parameter<" + alias + "> of `"
                                    + klass.getName() + "` because it already exists"
                            );
                        }
                        continue;
                    }
                }

                if (fpn != -1) lookup:{
                    if (parameters == null) {
                        try {
                            parameters = latest.getParameters();
                        } catch (NoSuchMethodError e) {
                            // Android API < 26
                            FLAG_PARAM_NAME = fpn = -1;
                            break lookup;
                        }
                    }
                    Parameter parameter = parameters[pos];
                    if (fpn == 0) {
                        if (parameter.isNamePresent()) {
                            fpn = 1;
                        } else {
                            fpn = -1;
                            break lookup;
                        }
                    }
                    String alias = parameter.getName();
                    Node node = node(
                        hash1(alias), caller
                    );
                    if (node.arguer == null) {
                        node.arguer = caller;
                        continue;
                    }
                    throw new IllegalStateException(
                        "Failed to set the parameter<" + alias + "> of `"
                            + klass.getName() + "` because it already exists"
                    );
                }

                long m = 1;
                long h = 0xE756C6190570D6E7L;

                do {
                    m = m << 4 | idx % 10;
                } while ((idx /= 10) != 0);

                do {
                    h = ((m & 0xF) + 0x30 ^ h) * FNV_PRIME;
                } while (
                    (m >>>= 4) != 1
                );

                Node node = node(h, caller);
                if (node.arguer == null) {
                    node.arguer = caller;
                    continue;
                }
                throw new IllegalStateException(
                    "Failed to set the parameter<" + h + "> of `"
                        + klass.getName() + "` because it already exists"
                );
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    static final class ParamCaller extends Caller {

        private Annotation[] annotations;

        ParamCaller(
            int index, Magic magic,
            Field field, Context context
        ) {
            super(index);
            element = field;
            prepare(
                type = field.getGenericType(), magic, context
            );
        }

        ParamCaller(
            int index, Type type,
            Context context, Annotation[] annotations
        ) {
            super(index);
            this.type = type;
            this.annotations = annotations;
            prepare(
                type, getAnnotation(Magic.class), context
            );
        }

        @Override
        public Object apply(
            @NotNull Object bean
        ) {
            return ((Object[]) bean)[index];
        }

        @Override
        public boolean accept(
            @NotNull Object bean,
            @Nullable Object value
        ) {
            if (value != null) {
                ((Object[]) bean)[index] = value;
                return true;
            }
            return false;
        }

        @Override
        public <A extends Annotation> A getAnnotation(
            @NotNull Class<A> clazz
        ) {
            AnnotatedElement elem = element;
            if (elem != null) {
                return elem.getAnnotation(clazz);
            }
            Annotation[] array = annotations;
            if (array != null) {
                for (Annotation a : array) {
                    if (a.annotationType() == clazz) {
                        return (A) a;
                    }
                }
            }
            return null;
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    static final class FieldCaller extends Caller {

        private final Field field;

        public FieldCaller(
            int index, Magic magic,
            Field field, Context context
        ) {
            super(index);

            element = field;
            prepare(
                type = field.getGenericType(), magic, context
            );

            this.field = field;
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
        }

        @Override
        public Object apply(
            @NotNull Object bean
        ) {
            try {
                return field.get(bean);
            } catch (Throwable e) {
                throw new IllegalStateException(
                    field + " call 'invoke' failed", e
                );
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
                    field.set(
                        bean, value
                    );
                    return true;
                } catch (Throwable e) {
                    throw new IllegalStateException(
                        field + " call 'invoke' failed", e
                    );
                }
            }
            return false;
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    static final class MethodCaller extends Caller {

        private final Method method;

        public MethodCaller(
            int index, Magic magic,
            Method method, Context context, Class<?> target
        ) {
            super(index);
            if (target == null) {
                type = method.getGenericReturnType();
            } else {
                if (target.isPrimitive()) {
                    type = target;
                } else {
                    type = method.getGenericParameterTypes()[0];
                }
            }

            element = method;
            prepare(
                type, magic, context
            );

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
                return method.invoke(
                    bean, (Object[]) null
                );
            } catch (Throwable e) {
                throw new IllegalStateException(
                    method + " call 'invoke' failed", e
                );
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
                    method.invoke(
                        bean, value
                    );
                    return true;
                } catch (Throwable e) {
                    throw new IllegalStateException(
                        method + " call 'invoke' failed", e
                    );
                }
            }
            return false;
        }
    }
}
