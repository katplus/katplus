package plus.kat;

import org.junit.jupiter.api.Test;
import plus.kat.anno.Expose;

import java.util.Collections;

import static plus.kat.Spare.lookup;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class SpareTest {

    @Test
    public void test_embed() {
        assertNotNull(
            lookup(User.class)
        );
    }

    @Test
    public void test_embed2() {
        Object[] list = new Object[]{
            Collections.EMPTY_MAP,
            Collections.EMPTY_SET,
            Collections.EMPTY_LIST
        };

        for (Object o : list) {
            assertNull(
                lookup(o.getClass())
            );
        }
    }

    @Test
    public void test_casting() {
        Spare<User> spare =
            lookup(User.class);

        User user = spare.cast(
            "   ${$:id(1)$:name(kraity)}   "
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_pProvider() {
        Spare<User> spare =
            lookup(User.class);

        assertNotNull(spare);
        assertNotNull(spare.getProvider());
    }

    static class User {
        @Expose("id")
        private int id;

        @Expose("name")
        private String name;
    }
}
