package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.actor.*;

import java.beans.Transient;

import java.util.*;
import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ReflexTest {

    @Test
    public void test() {
        Supplier supplier = Supplier.ins();

        User user = supplier.read(
            User.class, Flow.of(
                "{id=1,name=kraity}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test1() {
        Supplier supplier = Supplier.ins();

        Mate mate = supplier.read(
            Mate.class, Flow.of(
                "{id=1,tag=kat}"
            )
        );

        assertNotNull(mate);
        assertEquals(1, mate.id);
        assertEquals("kat", mate.tag);
    }

    @Test
    public void test2() {
        Supplier supplier = Supplier.ins();

        Author author = supplier.read(
            Author.class, Flow.of(
                "{id=1,tag=kat,name=kraity,meta=kat.plus}"
            )
        );

        assertNotNull(author);
        assertEquals(1, author.id);
        assertEquals("kat", author.tag);
        assertEquals("kraity", author.name);
        assertEquals("kat.plus", author.meta);
        assertEquals("{tag=\"kat\",meta=\"kat.plus\"}", Kat.encode(author));
    }

    @Test
    public void test3() {
        Supplier supplier = Supplier.ins();

        Meta meta = supplier.read(
            Meta.class, Flow.of(
                "{a=1,URL=kat.plus}"
            )
        );

        assertNotNull(meta);
        assertEquals(1, meta.a);
        assertEquals("kat.plus", meta.url);
    }

    @Test
    public void test4() {
        Supplier supplier = Supplier.ins();

        Tag tag = supplier.read(
            Tag.class, Flow.of(
                "{id=1}"
            )
        );

        assertNotNull(tag);
        assertEquals("1", tag.id);

        assertNull(tag.name);
        assertEquals("{id=\"1\",name=null}", Kat.encode(tag));

        tag.name = "kat";
        assertEquals("{id=\"1\",name=\"kat\"}", Kat.encode(tag));

        tag.id = null;
        assertEquals("{id=null,name=\"kat\"}", Kat.encode(tag));
    }

    @Test
    public void test5() {
        A a = Kat.decode(
            A.class, "{b={i=1},c={i=2},d={i=3,j=4},e={k=5,f={m=6}}}"
        );
        assertEquals("{\"b\":{\"i\":1},\"c\":{\"i\":2},\"d\":{\"i\":3,\"j\":4},\"e\":{\"f\":{\"m\":6,\"n\":5},\"k\":5}}", Json.encode(a));
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

    @Magus("Tag")
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

    @Magus("User")
    static class User {
        private int id;
        private String name;

        @Magic("id")
        public int getId() {
            return id;
        }

        @Magic("id")
        public void setId(int id) {
            this.id = id;
        }

        @Magic("name")
        public String getName() {
            return name;
        }

        @Magic("name")
        public void setName(String name) {
            this.name = name;
        }
    }

    @Magus("Mate")
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

    @Magus("Author")
    static class Author {
        private int id;
        private String name;

        @Magic("tag")
        private String tag;
        public String meta;

        @Magic("mask")
        static String mask = "test";

        public Author(
            @Magic("id") int id,
            @Magic("name") String name
        ) {
            this.id = id;
            this.name = name;
        }

        @Transient
        public String getAlias() {
            return name;
        }

        public static int getMeta() {
            return 143;
        }
    }

    static class Bean1 {
        public int one;
        public transient int two;
    }

    @Test
    public void test7() {
        Supplier supplier = Supplier.ins();

        Bean1 bean = supplier.read(
            Bean1.class, Flow.of(
                "{one=1,two=2}"
            )
        );

        assertNotNull(bean);
        assertEquals(1, bean.one);
        assertEquals(0, bean.two);

        bean.two = 2;
        assertEquals("{\"one\":1}", Json.encode(bean));
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
    public void test8() {
        Supplier supplier = Supplier.ins();

        Bean3 bean = supplier.read(
            Bean3.class, Flow.of(
                "{one=1,id=1,name=kraity}"
            )
        );

        assertNotNull(bean);
        assertEquals(1, bean.one);
        assertEquals(1, bean.id);
        assertEquals("kraity", bean.name);
        assertEquals("{\"one\":1,\"id\":1,\"name\":\"kraity\"}", Json.encode(bean));
    }

    static class User1 {
        private int id;
        private String name;

        public User1(
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
    public void test9() {
        Supplier supplier = Supplier.ins();

        User1 user = supplier.read(
            User1.class, Flow.of(
                "{id=1,name=kraity}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
        assertEquals("{}", Json.encode(user));
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
