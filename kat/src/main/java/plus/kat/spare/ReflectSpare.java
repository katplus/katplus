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

import java.beans.Transient;
import java.lang.reflect.*;
import java.lang.annotation.*;

import static plus.kat.spare.ArraySpare.*;
import static java.lang.reflect.Modifier.*;

/**
 * @author kraity
 * @since 0.0.2
 */
@SuppressWarnings("unchecked")
public class ReflectSpare<T> extends BeanSpare<T> {

    private Class<?> self;
    private Class<?>[] args;

    private int extra;
    private boolean variable;

    private Constructor<T> creator;
    private Constructor<T> builder;

    static boolean TRANSIENTLY;

    static {
        try {
            // Generally no
            // @Transient in Android
            Class.forName(
                "java.beans.Transient"
            );
            TRANSIENTLY = true;
        } catch (ClassNotFoundException e) {
            // Ignore this exception
        }
    }

    public ReflectSpare(
        @Nilable String space,
        @NotNull Class<?> klass,
        @NotNull Context context
    ) {
        super(
            space, (Class<T>) klass, context
        );
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
        Constructor<T> maker = creator;
        if (maker != null) {
            try {
                return maker.newInstance(
                    EMPTY_ARRAY
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
        @Nullable Object[] data
    ) {
        if (data == null ||
            data.length == 0) {
            return apply();
        }

        Constructor<T> maker = builder;
        if (maker != null) {
            int i = 0;
            Class<?>[] as = args;

            int mask = extra;
            int count = as.length;

            if (mask == 0) {
                for (; i < count; i++) {
                    if (data[i] == null) {
                        Class<?> c = as[i];
                        if (c.isPrimitive()) {
                            data[i] = Spare.of(c).apply();
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
                        if (data[i] == null) {
                            flag |= (1 << i);
                            Class<?> c = as[i];
                            if (c.isPrimitive()) {
                                data[i] = Spare.of(c).apply();
                            }
                        }
                    }
                    data[count - mask + n - 1] = flag;
                }
            }

            try {
                return maker.newInstance(data);
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
        Class<?>[] as = args;
        if (as == null) {
            return new Builder0<>(type, this);
        }

        Class<?> cls = self;
        if (cls == null && !variable) {
            return new Builder1<>(
                type, new Object[as.length], this
            );
        }

        return new Builder2<>(
            type, cls, new Object[as.length], this
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

            Magic magic = field.getAnnotation(Magic.class);
            if (magic == null && (mask & PUBLIC) == 0) {
                continue;
            }

            String name;
            String[] keys;

            Member member;
            if (magic == null) {
                name = field.getName();
                if (setProperty(name) == null) {
                    variable = true;
                    member = new FieldMember(
                        null, field, context
                    );
                    if (addProperty(name, member)) {
                        continue;
                    }
                    throw new IllegalStateException(
                        "Property for " + name + " has been setup"
                    );
                }
                continue;
            }

            keys = magic.value();
            if (keys.length != 0) {
                name = keys[0];
            } else {
                name = field.getName();
            }

            if (getProperty(name) == null) {
                member = new FieldMember(
                    magic, field, context
                );

                if (keys.length <= 1) {
                    setWriter(
                        name, member
                    );
                } else {
                    for (String alias : keys) {
                        addWriter(
                            alias, member
                        );
                    }
                }
            }

            if (setProperty(name) == null) {
                variable = true;
                member = new FieldMember(
                    magic, field, context
                );

                if (keys.length == 0) {
                    setReader(
                        name, member
                    );
                } else {
                    for (String alias : keys) {
                        if (addReader(alias, member)) {
                            continue;
                        }
                        throw new IllegalStateException(
                            "Reader for " + alias + " has been setup"
                        );
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    protected void onMethods(
        @NotNull Method[] methods
    ) {
        for (Method method : methods) {
            int mask = method.getModifiers();
            if ((mask & (STATIC | ABSTRACT)) != 0) {
                continue;
            }

            if (TRANSIENTLY) {
                Transient hidden = method
                    .getAnnotation(Transient.class);
                if (hidden != null && hidden.value()) {
                    continue;
                }
            }

            Member member;
            Class<?>[] params;

            int count;
            Magic magic = method
                .getAnnotation(Magic.class);

            if (magic == null) {
                if ((mask & PUBLIC) == 0) {
                    continue;
                }
                params = method.getParameterTypes();
                if ((count = params.length) > 1) {
                    continue;
                }
            } else {
                params = method.getParameterTypes();
                if ((count = params.length) > 1) {
                    continue;
                }

                String[] keys = magic.value();
                if (keys.length != 0) {
                    String name = keys[0];
                    if (count == 0) {
                        if (getProperty(name) != null) {
                            continue;
                        }

                        member = new MethodMember(
                            magic, method, params, context
                        );

                        if (keys.length == 1) {
                            setWriter(
                                name, member
                            );
                        } else {
                            for (String alias : keys) {
                                addWriter(
                                    alias, member
                                );
                            }
                        }
                        continue;
                    } else {
                        if (setProperty(name) != null) {
                            continue;
                        }

                        variable = true;
                        member = new MethodMember(
                            magic, method, params, context
                        );

                        for (String alias : keys) {
                            if (addReader(alias, member)) {
                                continue;
                            }
                            throw new IllegalStateException(
                                "Reader for " + alias + " has been setup"
                            );
                        }
                    }
                }
            }

            String name = method.getName();
            int i = 1, len = name.length();

            // set
            char ch = name.charAt(0);
            if (ch == 's') {
                if (count == 0 || len < 4 ||
                    name.charAt(i++) != 'e' ||
                    name.charAt(i++) != 't') {
                    continue;
                }
            }

            // get
            else if (ch == 'g') {
                if (count != 0 || len < 4 ||
                    name.charAt(i++) != 'e' ||
                    name.charAt(i++) != 't') {
                    continue;
                }
            }

            // is
            else if (ch == 'i') {
                if (count != 0 || len < 3 ||
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

            char c1 = name.charAt(i++);
            if (c1 < 'A' || 'Z' < c1) {
                continue;
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
            if (count != 0) {
                if (setProperty(name) == null) {
                    variable = true;
                    setReader(
                        name, new MethodMember(
                            magic, method, params, context
                        )
                    );
                }
            } else {
                if (getProperty(name) == null) {
                    setWriter(
                        name, new MethodMember(
                            magic, method, params, context
                        )
                    );
                }
            }
        }
    }

    protected void onConstructors(
        @NotNull Constructor<?>[] constructors
    ) {
        Constructor<?> buffer, before = null,
            latest = constructors[0];
        Class<?>[] bufferType, beforeType = null,
            latestType = latest.getParameterTypes();
        for (int i = 1; i < constructors.length; i++) {
            buffer = constructors[i];
            bufferType = buffer.getParameterTypes();
            if (bufferType.length != 0) {
                if (latestType.length <=
                    bufferType.length) {
                    before = latest;
                    latest = buffer;
                    beforeType = latestType;
                    latestType = bufferType;
                } else if (i == 1 ||
                    beforeType.length <=
                        bufferType.length) {
                    before = buffer;
                    beforeType = bufferType;
                }
            } else {
                before = buffer;
                beforeType = bufferType;
                if (!latest.isAccessible()) {
                    latest.setAccessible(true);
                }
                builder = (Constructor<T>) latest;
            }
        }

        if (!latest.isAccessible()) {
            latest.setAccessible(true);
        }

        int size = latestType.length;
        if (size == 0) {
            creator = (Constructor<T>) latest;
        } else {
            builder = (Constructor<T>) latest;
            if (before != null) {
                int i = beforeType.length;
                int j = i / 32 + 2;
                if (i + j == size && latestType[i] == int.class &&
                    "kotlin.jvm.internal.DefaultConstructorMarker".equals(latestType[size - 1].getName())) {
                    size = i;
                    extra = j;
                    latest = before;
                }
            }

            args = latestType;
            Type[] ts = latest.getGenericParameterTypes();
            Annotation[][] as = latest.getParameterAnnotations();

            int i = 0, j = as.length - size;
            Class<?> declaringClass = klass.getDeclaringClass();

            if (declaringClass != null &&
                (klass.getModifiers() & STATIC) == 0) {
                if (declaringClass == latestType[0]) {
                    i++;
                    self = declaringClass;
                }
            }

            for (; i < size; i++) {
                int n = i + j;
                ParamMember arg = new ParamMember(
                    i, ts[i], context, as[n]
                );

                Magic magic = arg.getAnnotation(Magic.class);
                if (magic != null) {
                    String[] v = magic.value();
                    if (v.length != 0) {
                        for (String alias : v) {
                            if (addParameter(alias, arg)) {
                                continue;
                            }
                            throw new IllegalStateException(
                                "Parameter for " + alias + " has been setup"
                            );
                        }
                        continue;
                    }
                }
                setParameter(
                    "arg" + n, arg
                );
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    static final class ParamMember extends Member {

        private Annotation[] annotations;

        ParamMember(
            int idx,
            Magic magic,
            Field field,
            Context context
        ) {
            index = idx;
            element = field;
            Class<?> cls = field.getType();
            if (cls.isPrimitive()) {
                type = cls;
            } else {
                type = field.getGenericType();
            }
            init(magic, context);
        }

        ParamMember(
            int index,
            Type type,
            Context context,
            Annotation[] annotations
        ) {
            this.type = type;
            this.index = index;
            this.annotations = annotations;
            Magic magic = getAnnotation(Magic.class);
            init(magic, context);
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
    static final class FieldMember extends Member {

        private final Field field;

        public FieldMember(
            Magic magic,
            Field field,
            Context context
        ) {
            super();
            element = field;
            if (magic != null) {
                index = magic.index();
            }
            Class<?> cls = field.getType();
            if (cls.isPrimitive()) {
                type = cls;
            } else {
                type = field.getGenericType();
            }
            init(magic, context);
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
    static final class MethodMember extends Member {

        private final Method method;

        public MethodMember(
            Magic magic,
            Method method,
            Class<?>[] params,
            Context context
        ) {
            element = method;
            if (magic != null) {
                index = magic.index();
            }
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
            init(magic, context);
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
                    bean, EMPTY_ARRAY
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
