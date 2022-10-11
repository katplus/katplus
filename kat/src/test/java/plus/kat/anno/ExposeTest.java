package plus.kat.anno;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.chain.Alias;

import java.util.Date;

import static plus.kat.Flag.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ExposeTest {

    @Test
    public void test_with_other() {
        Mode mode = new Mode();
        mode.alias = new Alias("kat");
        mode.model = new Entity("kraity");

        assertEquals("{\"alias\":\"kat\",\"model\":{\"name\":\"kraity\"}}", Json.encode(mode));
    }

    interface Model {
        String getName();
    }

    static class Entity implements Model {
        String name;

        Entity(String s) {
            name = s;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    static class Mode {
        @Expose(with = String.class)
        public Alias alias;

        @Expose(with = Entity.class)
        public Model model;
    }

    @Test
    public void test() {
        Supplier supplier = Supplier.ins();

        User user = supplier.read(
            User.class, new Event<>(
                "${$:date()$:time()}"
            )
        );

        assertNotNull(user);
        assertNotNull(user.date);
        assertEquals(1645539742000L, user.date.getTime());
        assertEquals(1645539742000L, user.time.getTime());
    }

    @Embed("User")
    static class User {
        @Expose(
            value = "date",
            require = NotNull
        )
        private Date date = new Date(1645539742000L);

        private Date time = new Date(1645539742000L);

        @Expose(
            value = "time",
            require = NotNull
        )
        public void setTime(
            Date time
        ) {
            this.time = time;
        }

        public Date getTime() {
            return time;
        }
    }

    @Embed("Meta")
    static class Meta {
        @Expose("id")
        public int id;

        @Expose(
            value = "size",
            require = Excluded
        )
        public int size;

        @Expose(
            value = "salt",
            require = Readonly
        )
        public String salt;

        @Expose(
            value = "trace",
            require = Internal
        )
        public String trace;

        @Expose(
            value = "uid",
            require = Excluded
        )
        public void setUId(
            int uid
        ) {
            id = uid;
        }

        @Expose(
            value = "uid",
            require = Excluded
        )
        public int getUId() {
            return id;
        }
    }

    @Test
    public void test_only() {
        Supplier supplier = Supplier.ins();

        Meta meta = supplier.read(
            Meta.class, new Event<>(
                "{:id(1):uid(2):size(3):salt(AAA):trace(id:123-456)}"
            )
        );

        assertNull(meta.salt);
        assertEquals(0, meta.size);
        assertEquals("id:123-456", meta.trace);

        meta.size = 123;
        meta.salt = "kat.plus";
        assertEquals("Meta{i:id(1)s:salt(kat.plus)}", Kat.encode(meta));
    }
}
