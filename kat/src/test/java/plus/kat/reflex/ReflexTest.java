package plus.kat.reflex;

import org.junit.jupiter.api.Test;

import plus.kat.Event;
import plus.kat.Kat;
import plus.kat.Supplier;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import plus.kat.anno.NotNull;

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
                "Author{$:id(1)$:tag(kat)$:name(kraity)$:mark(down)}"
            )
        );

        assertNotNull(author);
        assertEquals(1, author.id);
        assertEquals("kat", author.tag);
        assertEquals("kraity", author.name);
        assertEquals("Author{s:tag(kat)s:mark(down)}", Kat.encode(author));
        assertNotEquals("Author{s:tag(kat)s:mark(down)s:meta(plus)}", Kat.encode(author));
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
        @NotNull
        @Expose("id")
        private String id;
        private String name;

        public void setName(
            String name
        ) {
            this.name = name;
        }

        @NotNull
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

        public String mark;

        @Expose("meta")
        static String meta = "plus";

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
}
