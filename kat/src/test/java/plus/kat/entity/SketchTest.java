package plus.kat.entity;

import org.junit.jupiter.api.Test;

import plus.kat.Event;
import plus.kat.Spare;
import plus.kat.Supplier;
import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import plus.kat.reflex.ReflectSpare;

import static plus.kat.anno.Embed.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class SketchTest {

    @Test
    public void test() throws NoSuchMethodException {
        Spare<User> spare = new ReflectSpare<>(
            User.class, Supplier.ins()
        );

        User user = spare.read(
            new Event<>(
                "User{$:meta{$(101)$(K)$(RSA)}}"
            )
        );

        assertNotNull(user);
        assertNotNull(user.meta);

        assertEquals(101, user.meta.sig);
        assertEquals("K", user.meta.key);
        assertEquals("RSA", user.meta.algo);
        assertEquals("User{Meta:meta{i:sig(101)s:key(K)s:algo(RSA)}}", spare.write(user).toString());
    }

    @Embed("User")
    static class User {
        @Expose("meta")
        private Meta meta;
    }

    @Embed(value = "Meta", claim = DIRECT)
    static class Meta {
        @Expose(index = 0)
        private int sig;

        private String key;

        @Expose(index = 2)
        private String algo;

        @Expose(index = 1)
        public void setKey(
            String key
        ) {
            this.key = key;
        }

        @Expose(value = "key", index = 1)
        public String getKey() {
            return key;
        }
    }
}
