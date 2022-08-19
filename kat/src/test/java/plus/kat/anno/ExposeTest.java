package plus.kat.anno;

import org.junit.jupiter.api.Test;

import plus.kat.Json;
import plus.kat.chain.Alias;

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
}
