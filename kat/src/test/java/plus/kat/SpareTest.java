package plus.kat;

import org.junit.jupiter.api.Test;
import plus.kat.anno.Expose;
import plus.kat.chain.Value;
import plus.kat.crash.IOCrash;
import plus.kat.crash.UnexpectedCrash;
import plus.kat.spare.Builder;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    public void test_flat() {
        Spare<User> spare =
            lookup(User.class);

        User user = new User();
        user.id = 1;
        user.name = "kraity";

        HashMap<String, Object>
            data = new HashMap<>();

        spare.flat(
            user, data::put
        );

        assertEquals("{name=kraity, id=1}", data.toString());
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
    public void test_provider() {
        Spare<User> spare =
            lookup(User.class);

        assertNotNull(spare);

        // assert not null
        assertNotNull(spare.getProvider());
    }

    static class User {
        @Expose("id")
        private int id;

        @Expose("name")
        private String name;
    }

    @Test
    public void test_getType() {
        Type[] types = new Type[]{
            String.class,
            Value.class,
            StringBuilder.class
        };

        Spare<CharSequence> spare = new Spare<CharSequence>() {
            @Override
            public CharSequence getSpace() {
                return null;
            }

            @Override
            public Boolean getFlag() {
                return null;
            }

            @Override
            public boolean accept(Class<?> klass) {
                return false;
            }

            @Override
            public CharSequence read(
                Flag flag,
                Value value
            ) throws IOCrash {
                Type type = value.getType();

                if (type == String.class) {
                    return value.toString();
                }

                if (type == Value.class) {
                    return new Value(value);
                }

                if (type == StringBuilder.class) {
                    return new StringBuilder(
                        value.toString()
                    );
                }

                throw new UnexpectedCrash();
            }

            @Override
            public Class<CharSequence> getType() {
                return null;
            }

            @Override
            public Builder<CharSequence> getBuilder(Type type) {
                return null;
            }
        };

        for (Type type : types) {
            Event<CharSequence> event =
                new Event<>("$(test)");
            event.with(type).with(spare);

            assertEquals(
                type, Kat.decode(event).getClass()
            );
        }
    }
}
