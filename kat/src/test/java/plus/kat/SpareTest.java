package plus.kat;

import plus.kat.actor.Magic;
import plus.kat.actor.Magus;

import java.util.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class SpareTest {

    static class Entity {
        public int id;
        public String name;
    }

    @Test
    public void test_context() {
        Spare<User> spare =
            Spare.of(User.class);

        assertNotNull(spare);
        assertNotNull(spare.getContext());
    }

    @Magus
    static class User {
        @Magic("id")
        private int id;

        @Magic("name")
        private String name;
    }

    static class UserVO extends User {
        public boolean blocked;
    }

    @Test
    public void test_apply() {
        Spare<User> spare =
            Spare.of(User.class);

        assertNotNull(spare.apply());
    }

    @Test
    public void test_apply_type() {
        Spare<User> spare =
            Spare.of(User.class);

        assertSame(User.class, spare.apply(null).getClass());
        assertSame(User.class, spare.apply(User.class).getClass());
        assertSame(UserVO.class, spare.apply(UserVO.class).getClass());

        assertThrows(IllegalStateException.class, () -> spare.apply(Object.class));
        assertThrows(IllegalStateException.class, () -> spare.apply(Entity.class));
        assertThrows(IllegalStateException.class, () -> spare.apply(HashMap.class));
    }
}
