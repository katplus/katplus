package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.actor.Magic;

import plus.kat.Chan;
import plus.kat.Flow;
import plus.kat.Spare;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class RecordSpareTest {

    static <T> Spare<T> spare(
        Class<T> clazz
    ) {
        return new RecordSpare<>(
            null, clazz, Supplier.ins()
        );
    }

    static class User {

        private final int id;
        private final String name;

        public User() {
            this.id = 0;
            this.name = null;
        }

        public User(
            int id
        ) {
            this.id = id;
            this.name = null;
        }

        public User(
            String name, int id
        ) {
            this.id = id;
            this.name = name;
        }

        // suited
        public User(
            int id, String name
        ) {
            this.id = id;
            this.name = '@' + name;
        }

        public User(
            int id, String tag, String name
        ) {
            this.id = id;
            this.name = tag + name;
        }

        public int id() {
            return id;
        }

        public String name() {
            return name;
        }
    }

    @Test
    public void test_args() throws IOException {
        Spare<User> spare =
            spare(User.class);

        User user = spare.read(
            Flow.of(
                "{id=1,name=kraity}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("@kraity", user.name);
        try (Chan chan = spare.write(user)) {
            assertEquals("{id=1,name=\"@kraity\"}", chan.toString());
        }
    }

    static class Model {

        private final int id;

        @Magic("meta")
        private final String tag;

        private final String name;

        public Model(
            int id, String tag, String name
        ) {
            this.id = id;
            this.tag = tag;
            this.name = name;
        }

        public int id() {
            return id;
        }

        public String tag() {
            return '#' + tag;
        }

        @Magic("alias")
        public String name() {
            return '@' + name;
        }
    }

    @Test
    public void test_magic() throws IOException {
        Spare<Model> spare =
            spare(Model.class);

        Model model = spare.read(
            Flow.of(
                "{id=1,name=kraity,meta=kat.plus}"
            )
        );

        assertNotNull(model);
        assertEquals(1, model.id);
        assertEquals("kraity", model.name);
        try (Chan chan = spare.write(model)) {
            assertEquals("{id=1,meta=\"#kat.plus\",alias=\"@kraity\"}", chan.toString());
        }
    }
}
