package plus.kat.reflex;

import org.junit.jupiter.api.Test;
import plus.kat.Event;
import plus.kat.Supplier;
import plus.kat.anno.Embed;
import plus.kat.anno.Expose;

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

    @Embed(claim = Embed.POJO)
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
}
