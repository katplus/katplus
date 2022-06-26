package plus.kat;

import org.junit.jupiter.api.Test;
import plus.kat.anno.Expose;

import static plus.kat.Spare.embed;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class SpareTest {

    @Test
    public void test_embed() {
        assertNotNull(
            embed(User.class)
        );
    }

    @Test
    public void test_casting() {
        Spare<User> spare =
            embed(User.class);

        User user = spare.cast(
            "   ${$:id(1)$:name(kraity)}   "
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    static class User {
        @Expose("id")
        private int id;

        @Expose("name")
        private String name;
    }
}
