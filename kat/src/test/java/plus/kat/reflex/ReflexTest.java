package plus.kat.reflex;

import org.junit.jupiter.api.Test;

import plus.kat.*;

import plus.kat.anno.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReflexTest {

    @Test
    public void test() {
        Supplier supplier = Supplier.ins();

        User user = supplier.read(
            User.class, Event.ascii(
                "User{i:id(1)s:name(kraity)}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test1() {
        Supplier supplier = Supplier.ins();

        Meta meta = supplier.read(
            Meta.class, Event.ascii(
                "Meta{i:id(1)s:tag(kat)}"
            )
        );

        assertNotNull(meta);
        assertEquals(1, meta.id);
        assertEquals("kat", meta.tag);
    }

    @Test
    public void test2() {
        Supplier supplier = Supplier.ins();

        Author author = supplier.read(
            Author.class, Event.ascii(
                "Author{$:id(1)$:tag(kat)$:name(kraity)$:meta(kat.plus)}"
            )
        );

        assertNotNull(author);
        assertEquals(1, author.id);
        assertEquals("kat", author.tag);
        assertEquals("kraity", author.name);
        assertEquals("kat.plus", author.meta);
        assertEquals("Author{s:tag(kat)s:meta(kat.plus)}", Kat.encode(author));
    }

    @Test
    public void test3() {
        Supplier supplier = Supplier.ins();

        Bean bean = supplier.read(
            Bean.class, Event.ascii(
                "{:a(1):URL(kat.plus)}"
            )
        );

        assertNotNull(bean);
        assertEquals(1, bean.a);
        assertEquals("kat.plus", bean.url);
    }

    @Test
    public void test4() {
        Supplier supplier = Supplier.ins();

        Tag tag = supplier.read(
            Tag.class, Event.ascii(
                "{:id(1)}"
            )
        );

        assertNotNull(tag);
        assertEquals("1", tag.id);

        assertNull(tag.name);
        assertEquals("Tag{s:id(1)}", Kat.encode(tag));

        tag.name = "kat";
        assertEquals("Tag{s:id(1)s:name(kat)}", Kat.encode(tag));

        tag.id = null;
        assertEquals("Tag{s:name(kat)}", Kat.encode(tag));
    }

    @Test
    public void test5() {
        A a = Kat.decode(
            A.class, "{:b{:i(1)}:c{:i(2)}:d{:i(3):j(4)}:e{:k(5):f{:m(6)}}}"
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

            C(@Expose("i") int i) {
                this.i = i;
            }
        }

        class D {
            public int i;
            public int j;

            D(@Expose("i") int i,
              @Expose("j") int j) {
                this.i = i;
                this.j = j;
            }
        }

        class E {
            public F f;
            private int k;

            E(@Expose("k") int k) {
                this.k = k;
            }

            public int getK() {
                return k;
            }

            class F {
                private int m;

                F(@Expose("m") int m) {
                    this.m = m;
                }

                @Expose(value = "m", index = 0)
                public int getM() {
                    return m;
                }

                @Expose(value = "n", index = 1)
                public int getN() {
                    return E.this.k;
                }
            }
        }
    }

    static class Bean {
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

    @Embed("Tag")
    static class Tag {
        @Expose(value = "id", mode = 1)
        private String id;
        private String name;

        public void setName(
            String name
        ) {
            this.name = name;
        }

        @Expose(mode = 1)
        public String getName() {
            return name;
        }
    }

    @Embed("User")
    static class User {
        private int id;
        private String name;

        @Expose("id")
        public int getId() {
            return id;
        }

        @Expose("id")
        public void setId(int id) {
            this.id = id;
        }

        @Expose("name")
        public String getName() {
            return name;
        }

        @Expose("name")
        public void setName(String name) {
            this.name = name;
        }
    }

    @Embed("Meta")
    static class Meta {
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

    @Embed("Author")
    static class Author {
        private int id;
        private String name;

        @Expose("tag")
        private String tag;
        public String meta;

        @Expose("mask")
        static String mask = "test";

        public Author(
            @Expose("id") int id,
            @Expose("name") String name
        ) {
            this.id = id;
            this.name = name;
        }

        public static int getMage() {
            return 143;
        }
    }

    static class Name {
        public String firstName;
        public String lastName;
    }

    @Embed("Master")
    static class Master {
        public int id;

        @Expose(mode = 2)
        public Name name;
    }

    @Test
    public void test6() {
        Master master = new Master();
        master.id = 1;
        master.name = new Name();
        master.name.firstName = "kat";
        master.name.lastName = "plus";

        assertEquals("Master{i:id(1)s:firstName(kat)s:lastName(plus)}", Kat.encode(master));
        assertEquals("{\"id\":1,\"firstName\":\"kat\",\"lastName\":\"plus\"}", Json.encode(master));
        assertEquals("<Master><id>1</id><firstName>kat</firstName><lastName>plus</lastName></Master>", Doc.encode(master));
    }

    static class Bean1 {
        public int one;
        public transient int two;
    }

    @Test
    public void test7() {
        Supplier supplier = Supplier.ins();

        Bean1 bean = supplier.read(
            Bean1.class, new Event<>(
                "{:one(1):two(2)}"
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
            Bean3.class, new Event<>(
                "{:one(1):id(1):name(kraity)}"
            )
        );

        assertNotNull(bean);
        assertEquals(1, bean.one);
        assertEquals(1, bean.id);
        assertEquals("kraity", bean.name);
        assertEquals("{\"name\":\"kraity\",\"id\":1,\"one\":1}", Json.encode(bean));
    }

    static class User1 {
        private int id;
        private String name;

        public User1(
            @Expose("id")
                int id,
            @Expose("name")
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
            User1.class, new Event<>(
                "{:id(1):name(kraity)}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
        assertEquals("{}", Json.encode(user));
    }
}
