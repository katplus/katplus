package plus.kat.entity;

import org.junit.jupiter.api.Test;

import plus.kat.Event;
import plus.kat.Supplier;
import plus.kat.anno.Expose;
import plus.kat.chain.Alias;
import plus.kat.chain.Space;
import plus.kat.chain.Value;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ClutterTest {

    @Test
    public void test() {
        Supplier supplier = Supplier.ins();

        User user = supplier.read(
            User.class, new Event<>(
                "User{$:meta{$(101)$(RSA)}}"
            )
        );

        assertNotNull(user);
        assertNotNull(user.meta);

        assertEquals(101, user.meta.sig);
        assertEquals("RSA", user.meta.algo);
    }

    static class User {
        @Expose(value = "meta", with = MetaCoder.class)
        private Meta meta;
    }

    static class Meta {
        private int sig;
        private String algo;
    }

    static class MetaCoder implements Clutter<Meta> {

        @Override
        public Meta apply(
            Alias alias
        ) {
            return new Meta();
        }

        @Override
        public void accept(
            Meta entity,
            int index,
            Space space,
            Alias alias,
            Value value
        ) {
            switch (index) {
                case 0: {
                    entity.sig = value.toInt();
                    break;
                }
                case 1: {
                    entity.algo = value.string();
                    break;
                }
            }
        }
    }
}
