package plus.kat.spare;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.actor.*;

import java.beans.Transient;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ReflexSpareTest {

    static <T> Spare<T> spare(
        Class<T> clazz
    ) {
        return new ReflectSpare<>(
            null, clazz, Supplier.ins()
        );
    }

    static class User {
        private int id;
        private String name;

        @Magic("id")
        public int getId() {
            return id;
        }

        @Magic("id")
        public void setId(
            int id
        ) {
            this.id = id;
        }

        @Magic("alias")
        public void setName(
            String name
        ) {
            this.name = name;
        }

        @Magic("alias")
        public String getName() {
            return name;
        }
    }

    @Test
    public void test_with_magic() throws IOException {
        Spare<User> spare =
            spare(User.class);
        assertInstanceOf(
            ReflectSpare.class, spare
        );

        User user = spare.read(
            Flow.of(
                "{id=1,alias=kraity}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);

        Subject<User> subject =
            (Subject<User>) spare;

        assertNotNull(subject.set("id"));
        assertTrue(
            subject.set("id").accept(user, 2)
        );
        assertEquals(2, user.id);

        assertNotNull(subject.get("id"));
        assertEquals(2, subject.get("id").apply(user));

        assertNotNull(subject.set("alias"));
        assertTrue(
            subject.set("alias").accept(user, "katplus")
        );
        assertEquals("katplus", user.name);

        assertNotNull(subject.get("alias"));
        assertEquals("katplus", subject.get("alias").apply(user));
    }

    static class Mate {
        private int id;
        private String tag;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    @Test
    public void test_with_method_name() throws IOException {
        Spare<Mate> spare =
            spare(Mate.class);

        Mate mate = spare.read(
            Flow.of(
                "{id=1,tag=kat}"
            )
        );

        assertNotNull(mate);
        assertEquals(1, mate.id);
        assertEquals("kat", mate.tag);
    }

    static class Meta {
        private int a;
        private String url;

        public void setA(int a) {
            this.a = a;
        }

        public int getA() {
            return a;
        }

        public void setURL(String url) {
            this.url = url;
        }

        public String getURL() {
            return url;
        }
    }

    @Test
    public void test_with_name_uppercase() throws IOException {
        Spare<Meta> spare =
            spare(Meta.class);

        Meta meta = spare.read(
            Flow.of(
                "{a=1,URL=kat.plus}"
            )
        );

        assertNotNull(meta);
        assertEquals(1, meta.a);
        assertEquals("kat.plus", meta.url);
    }

    static class Model {
        private int id;
        private String name;

        @Magic("tag")
        private String tag;
        public String meta;

        @Magic("mask")
        static String mask = "test";

        public Model(
            @Magic("id") int id,
            @Magic("name") String name
        ) {
            this.id = id;
            this.name = name;
        }

        public static int getMeta() {
            return 143;
        }
    }

    static class Tag {
        @Magic("id")
        private String id;
        private String name;

        public void setName(
            String name
        ) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    public void test_magic_in_field() throws IOException {
        Spare<Tag> spare =
            spare(Tag.class);

        Tag tag = spare.read(
            Flow.of("{id=1}")
        );

        assertNotNull(tag);
        assertEquals("1", tag.id);

        assertNull(tag.name);
        try (Chan chan = spare.write(tag)) {
            assertEquals("{id=\"1\",name=null}", chan.toString());
        }

        tag.name = "kat";
        try (Chan chan = spare.write(tag)) {
            assertEquals("{id=\"1\",name=\"kat\"}", chan.toString());
        }

        tag.id = null;
        try (Chan chan = spare.write(tag)) {
            assertEquals("{id=null,name=\"kat\"}", chan.toString());
        }
    }

    @Test
    public void test_with_multi_args() throws IOException {
        Spare<Model> spare =
            spare(Model.class);

        Model model = spare.read(
            Flow.of(
                "{id=1,tag=kat,name=kraity,meta=kat.plus}"
            )
        );

        assertNotNull(model);
        assertEquals(1, model.id);
        assertEquals("kat", model.tag);
        assertEquals("kraity", model.name);
        assertEquals("kat.plus", model.meta);

        try (Chan chan = spare.write(model)) {
            assertEquals("{tag=\"kat\",meta=\"kat.plus\"}", chan.toString());
        }
    }

    static class A {
        public B b;
        public C c;
        public D d;
        public E e;

        class B {
            public int i;
        }

        class C {
            public int i;

            C(@Magic("i") int i) {
                this.i = i;
            }
        }

        class D {
            public int i;
            public int j;

            D(@Magic("i") int i,
              @Magic("j") int j) {
                this.i = i;
                this.j = j;
            }
        }

        class E {
            public F f;
            private int k;

            E(@Magic("k") int k) {
                this.k = k;
            }

            public int getK() {
                return k;
            }

            class F {
                private int m;

                F(@Magic("m") int m) {
                    this.m = m;
                }

                @Magic(value = "m", index = 0)
                public int getM() {
                    return m;
                }

                @Magic(value = "n", index = 1)
                public int getN() {
                    return E.this.k;
                }
            }
        }
    }

    @Test
    public void test_with_multi_args_and_non_static() throws IOException {
        A a = Kat.decode(
            A.class, "{b={i=1},c={i=2},d={i=3,j=4},e={k=5,f={m=6}}}"
        );
        try (Chan chan = Json.encode(a)) {
            assertEquals(
                "{\"b\":{\"i\":1},\"c\":{\"i\":2},\"d\":{\"i\":3,\"j\":4},\"e\":{\"f\":{\"m\":6,\"n\":5},\"k\":5}}", chan.toString()
            );
        }
    }

    static class Bean1 {
        public int one;
        public transient int two;

        @Transient
        public int getThree() {
            return two;
        }
    }

    @Test
    public void test_transient_field_and_method() throws IOException {
        Spare<Bean1> spare =
            spare(Bean1.class);

        Bean1 bean = spare.read(
            Flow.of(
                "{one=1,two=2}"
            )
        );

        assertNotNull(bean);
        assertEquals(1, bean.one);
        assertEquals(0, bean.two);

        bean.two = 2;
        try (Chan chan = Json.encode(bean)) {
            assertEquals("{\"one\":1}", chan.toString());
        }
    }

    static class Bean2 extends Bean1 {
        public int id;
    }

    static class Bean3 extends Bean2 {
        private String name;

        public void setName(
            String name
        ) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    public void test_inherit() throws IOException {
        Spare<Bean3> spare =
            spare(Bean3.class);

        Bean3 bean = spare.read(
            Flow.of(
                "{one=1,id=1,name=kraity}"
            )
        );

        assertNotNull(bean);
        assertEquals(1, bean.id);
        assertEquals(1, bean.one);
        assertEquals("kraity", bean.name);

        try (Chan chan = spare.write(bean)) {
            assertEquals("{one=1,id=1,name=\"kraity\"}", chan.toString());
        }
    }

    static class Modal {
        private int id;
        private String name;

        public Modal(
            @Magic("id")
            int id,
            @Magic("name")
            String name
        ) {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    public void test_magic_in_constructor() throws IOException {
        Spare<Modal> spare =
            spare(Modal.class);

        Modal user = spare.read(
            Flow.of(
                "{id=1,name=kraity}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
        try (Chan chan = Json.encode(user)) {
            assertEquals("{}", chan.toString());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Form {
        private int id;
        private String tag;
    }

    @Test
    public void test_lombok() throws IOException {
        Spare<Form> spare =
            new ReflectSpare<>(
                null,
                Form.class,
                Supplier.ins()
            );

        Form form = spare.read(
            Flow.of(
                "{id=1,tag=kat.plus}"
            )
        );

        assertNotNull(form);
        assertEquals(1, form.id);
        assertEquals("kat.plus", form.tag);
    }

    static class Temp {
        public int userId;
        public int user_id;
        private String nickname;
        private String nickName;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickname) {
            this.nickName = nickname;
        }
    }

    @Test
    public void test_multi_name() throws Exception {
        Spare<Temp> spare =
            spare(Temp.class);

        Temp temp = spare.read(
            Flow.of(
                "{userId=1,user_id=2,nickname=kat,nick_name=kraity}"
            )
        );

        assertNotNull(temp);
        assertEquals(1, temp.userId);
        assertEquals(2, temp.user_id);
        assertEquals("kat", temp.nickname);
        assertEquals("kraity", temp.nickName);
    }

    static class Mage {
        @Magic("id")
        public int id;

        @Magic("userName")
        public String name;
    }

    @Test
    public void test_snake_case_name() throws Exception {
        Spare<Mage> spare =
            spare(Mage.class);

        String[] texts = {
            "{id=1,user_name=kraity}",
            "{Id=1,User_Name=kraity}",
            "{ID=1,USER_NAME=kraity}",
        };

        for (String text : texts) {
            Mage mage = spare.read(
                Flow.of(text)
            );
            assertNotNull(mage, text);
            assertEquals(1, mage.id, text);
            assertEquals("kraity", mage.name, text);
        }
    }

    static class Hook {
        public void test(
            List<Integer> list,
            Map<String, String> data
        ) {
            assertEquals(1, data.size());
            assertEquals(2, list.size());
            assertEquals(1, list.get(0));
            assertEquals(2, list.get(1));
            assertEquals("kraity", data.get("name"));
        }
    }

    @Test
    public void test_array_hook() throws Exception {
        Type type = null;
        Method method = null;

        for (Method m : Hook.class.getDeclaredMethods()) {
            if (m.getName().equals("test")) {
                type = new ParameterizedType() {
                    @Override
                    public Type getOwnerType() {
                        return null;
                    }

                    @Override
                    public Type getRawType() {
                        return Object[].class;
                    }

                    @Override
                    public Type[] getActualTypeArguments() {
                        return m.getGenericParameterTypes();
                    }
                };
                method = m;
            }
        }

        assertNotNull(type);
        assertNotNull(method);

        method.invoke(
            new Hook(), (Object[]) Spare.of(type).read(
                type, Flow.of("[[1,2],{name=kraity}]")
            )
        );
    }
}
