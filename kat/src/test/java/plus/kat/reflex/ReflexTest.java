package plus.kat.reflex;

import org.junit.jupiter.api.Test;

import plus.kat.Event;
import plus.kat.Supplier;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;

import static plus.kat.anno.Embed.POJO;
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
                "Author{$:id(1)$:name(kraity)$:meta(kat)}"
            )
        );

        assertNotNull(author);
        assertEquals(1, author.id);
        assertEquals("kat", author.meta);
        assertEquals("kraity", author.name);
    }

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

    @Embed(claim = POJO)
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

    static class Author {
        private int id;
        private String name;

        @Expose("meta")
        private String meta;

        public Author(
            int id, String name
        ) {
            this.id = id;
            this.name = name;
        }
    }
}
