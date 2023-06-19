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
import plus.kat.entity.*;

import java.beans.Transient;
import java.lang.reflect.*;
import java.lang.annotation.*;

import static plus.kat.stream.Toolkit.*;
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
    private Constructor<T> creator, builder;

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

            Widget widget;
            if (magic == null) {
                name = field.getName();
                if (setProperty(name) == null) {
                    variable = true;
                    widget = new FieldVisitor(
                        -1, null, field, this
                    );
                    if (addProperty(name, widget)) {
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
                widget = new FieldVisitor(
                    magic.index(),
                    magic, field, this
                );

                if (keys.length <= 1) {
                    setWriter(
                        name, widget
                    );
                } else {
                    for (String alias : keys) {
                        addWriter(
                            alias, widget
                        );
                    }
                }
            }

            if (setProperty(name) == null) {
                variable = true;
                widget = new FieldVisitor(
                    magic.index(),
                    magic, field, this
                );

                if (keys.length == 0) {
                    setReader(
                        name, widget
                    );
                } else {
                    for (String alias : keys) {
                        if (addReader(alias, widget)) {
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

            Widget widget;
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

                        widget = new MethodVisitor(
                            magic.index(),
                            magic, method, this, params
                        );

                        if (keys.length == 1) {
                            setWriter(
                                name, widget
                            );
                        } else {
                            for (String alias : keys) {
                                addWriter(
                                    alias, widget
                                );
                            }
                        }
                        continue;
                    } else {
                        if (setProperty(name) != null) {
                            continue;
                        }

                        variable = true;
                        widget = new MethodVisitor(
                            magic.index(),
                            magic, method, this, params
                        );

                        for (String alias : keys) {
                            if (addReader(alias, widget)) {
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
                        name, new MethodVisitor(
                            magic == null ?
                                -1 : magic.index(),
                            magic, method, this, params
                        )
                    );
                }
            } else {
                if (getProperty(name) == null) {
                    setWriter(
                        name, new MethodVisitor(
                            magic == null ?
                                -1 : magic.index(),
                            magic, method, this, params
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
                ParamVisitor arg = new ParamVisitor(
                    i, ts[i], this, as[n]
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
    static abstract class Visitor extends Widget {

        protected AnnotatedElement element;

        /**
         * @param i the specified index
         */
        protected Visitor(int i) {
            super(i);
        }

        /**
         * Unsafe and may be deleted
         */
        protected void setup(
            Class<?> agent,
            BeanSpare<?> spare
        ) {
            if (Coder.class.isAssignableFrom(agent)) {
                try {
                    Constructor<?>[] cs = agent
                        .getDeclaredConstructors();
                    Constructor<?> buffer,
                        latest = cs[0];
                    Class<?>[] bufferType,
                        latestType = latest.getParameterTypes();
                    for (int i = 1; i < cs.length; i++) {
                        buffer = cs[i];
                        bufferType = buffer.getParameterTypes();
                        if (latestType.length <=
                            bufferType.length) {
                            latest = buffer;
                            latestType = bufferType;
                        }
                    }

                    Object[] args = null;
                    final int size = latestType.length;

                    if (size != 0) {
                        args = new Object[size];
                        for (int i = 0; i < size; i++) {
                            Class<?> m = latestType[i];
                            if (m == Class.class) {
                                args[i] = classOf(type);
                            } else if (m == Type.class) {
                                args[i] = type;
                            } else if (m == Spare.class) {
                                args[i] = spare;
                            } else if (m == Subject.class) {
                                args[i] = spare;
                            } else if (m == Context.class) {
                                args[i] = spare.context;
                            } else if (m.isPrimitive()) {
                                args[i] = Spare.of(m).apply();
                            } else if (m.isAnnotation()) {
                                args[i] = getAnnotation(
                                    (Class<? extends Annotation>) m
                                );
                            }
                        }
                    }

                    if (!latest.isAccessible()) {
                        latest.setAccessible(true);
                    }
                    coder = (Coder<?>) latest.newInstance(args);
                } catch (Exception e) {
                    throw new IllegalStateException(
                        "Failed to build the '"
                            + this + "' coder: " + agent, e
                    );
                }
            } else {
                coder = spare.context.assign(agent);
            }
        }

        /**
         * Returns the annotation of the {@code class}
         */
        public <A extends Annotation> A getAnnotation(
            @NotNull Class<A> clazz
        ) {
            AnnotatedElement elem = element;
            if (elem != null) {
                return elem.getAnnotation(clazz);
            } else {
                return clazz.getAnnotation(clazz);
            }
        }
    }

    /**
     * @author kraity
     * @since 0.0.6
     */
    static final class ParamVisitor extends Visitor {

        private Annotation[] annotations;

        ParamVisitor(
            int index,
            Field field,
            Magic magic,
            BeanSpare<?> spare
        ) {
            super(index);
            element = field;
            Class<?> cls = field.getType();
            if (cls.isPrimitive()) {
                type = cls;
            } else {
                type = field.getGenericType();
            }
            if (magic != null) {
                Class<?> agent = magic.agent();
                if (agent != void.class) {
                    setup(
                        agent, spare
                    );
                }
            }
        }

        ParamVisitor(
            int index,
            Type type,
            BeanSpare<?> spare,
            Annotation[] annotations
        ) {
            super(index);
            this.type = type;
            this.annotations = annotations;
            Magic magic = getAnnotation(Magic.class);
            if (magic != null) {
                Class<?> agent = magic.agent();
                if (agent != void.class) {
                    setup(
                        agent, spare
                    );
                }
            }
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
    static final class FieldVisitor extends Visitor {

        private final Field field;

        public FieldVisitor(
            int index,
            Magic magic,
            Field field,
            BeanSpare<?> spare
        ) {
            super(index);
            element = field;
            Class<?> cls = field.getType();
            if (cls.isPrimitive()) {
                type = cls;
            } else {
                type = field.getGenericType();
            }

            if (magic != null) {
                Class<?> agent = magic.agent();
                if (agent != void.class) {
                    setup(
                        agent, spare
                    );
                }
            }

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
    static final class MethodVisitor extends Visitor {

        private final Method method;

        public MethodVisitor(
            int index,
            Magic magic,
            Method method,
            BeanSpare<?> spare,
            Class<?>[] params
        ) {
            super(index);
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
