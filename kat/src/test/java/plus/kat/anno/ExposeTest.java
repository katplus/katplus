package plus.kat.anno;

import org.junit.jupiter.api.Test;

import plus.kat.Event;
import plus.kat.Json;
import plus.kat.Supplier;
import plus.kat.chain.Alias;

import java.util.Date;

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
        @Expose(value = "date", mode = 1)
        private Date date = new Date(1645539742000L);

        private Date time = new Date(1645539742000L);

        @Expose(value = "time", mode = 1)
        public void setTime(
            Date time
        ) {
            this.time = time;
        }

        public Date getTime() {
            return time;
        }
    }
}
