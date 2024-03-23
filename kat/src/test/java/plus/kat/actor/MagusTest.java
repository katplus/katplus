package plus.kat.actor;

import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Flow;
import plus.kat.Spare;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class MagusTest {

    @Magus
    interface User {
        String getName();
    }

    @Test
    public void test_proxy() throws IOException {
        Spare<User> spare =
            Spare.of(User.class);

        assertNotNull(spare);
        User user = spare.read(
            Flow.of(
                "{name=kraity}"
            )
        );

        assertNotNull(user);
        assertEquals("kraity", user.getName());
        try (Chan chan = spare.write(user)) {
            assertEquals("{name=\"kraity\"}", chan.toString());
        }
    }

    @Test
    public void test_agent() throws IOException {
        Spare<Model> spare =
            Spare.of(Model.class);

        assertNotNull(spare);
        Model model = spare.read(
            Flow.of(
                "{name=kraity}"
            )
        );

        assertNotNull(model);
        assertEquals("kraity", model.getName());

        assertSame(Entity.class, model.getClass());
        try (Chan chan = spare.write(model)) {
            assertEquals("{name=\"kraity\"}", chan.toString());
        }
    }

    @Magus(agent = Entity.class)
    interface Model {
        String getName();
    }

    static class Entity implements Model {
        private String name;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
