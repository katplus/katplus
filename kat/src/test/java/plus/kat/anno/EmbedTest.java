package plus.kat.anno;

import org.junit.jupiter.api.Test;

import plus.kat.Event;
import plus.kat.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class EmbedTest {

    @Test
    public void test_with_other() {
        Supplier supplier = Supplier.ins();

        Model model = supplier.read(
            Model.class, new Event<>(
                "{:name(kraity)}"
            )
        );

        assertNotNull(model);
        assertSame(Entity.class, model.getClass());
        assertEquals("kraity", model.getName());
    }

    @Embed(with = Entity.class)
    interface Model {
        String getName();
    }

    static class Entity implements Model {
        String name;

        public void setName(
            String key
        ) {
            name = key;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
