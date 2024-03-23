package plus.kat;

import lombok.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class SpareTest {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {
        private int id;
        private String name;
    }

    @Test
    public void test_context() {
        Spare<User> spare =
            Spare.of(User.class);

        assertNotNull(spare);
        assertNotNull(spare.getContext());
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

        User user0 = spare.apply();
        assertNotNull(user0);
        assertEquals(0, user0.id);

        User user1 = spare.apply(1, "kraity");
        assertNotNull(user1);
        assertEquals(1, user1.id);
        assertEquals("kraity", user1.name);

        Object[] nil = null;
        assertThrows(NullPointerException.class, () -> spare.apply(nil));
    }
}
