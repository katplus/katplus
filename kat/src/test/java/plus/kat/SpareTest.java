package plus.kat;

import org.junit.jupiter.api.Test;
import plus.kat.anno.Expose;
import plus.kat.chain.*;
import plus.kat.crash.*;
import plus.kat.spare.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class SpareTest {

    @Test
    public void test_embed() {
        assertNotNull(
            Spare.lookup(User.class)
        );

        Object[] list = new Object[]{
            Collections.EMPTY_MAP,
            Collections.EMPTY_SET,
            Collections.EMPTY_LIST
        };

        for (Object o : list) {
            assertNull(
                Spare.lookup(o.getClass())
            );
        }
    }

    @Test
    public void test_flat() {
        Spare<User> spare =
            Spare.lookup(User.class);

        User user = new User();
        user.id = 1;
        user.name = "kraity";

        HashMap<String, Object>
            data = new HashMap<>();

        assertTrue(
            spare.flat(
                user, data::put
            )
        );
        assertEquals("{name=kraity, id=1}", data.toString());
    }

    @Test
    public void test_apply0() {
        User user = new User();
        user.id = 1;
        user.name = "kraity";

        Supplier supplier = Supplier.ins();
        Spare<Entity> spare = supplier.lookup(Entity.class);

        Entity entity = spare.apply(
            supplier.flat(user)
        );
        assertNotNull(entity);

        assertEquals(user.id, entity.id);
        assertEquals(user.name, entity.name);
    }

    @Test
    public void test_apply1() {
        HashMap<String, Object>
            data = new HashMap<>();

        data.put("id", 1);
        data.put("name", "kraity");

        Supplier supplier = Supplier.ins();
        User user = supplier.apply(
            User.class, Spoiler.of(data)
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_cast0() {
        Spare<User> spare =
            Spare.lookup(User.class);

        User user = spare.cast(
            "   ${$:id(1)$:name(kraity)}   "
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_cast1() {
        Spare<User> spare =
            Spare.lookup(User.class);

        HashMap<String, Object>
            data = new HashMap<>();

        data.put("id", 1);
        data.put("name", "kraity");

        User user = spare.cast(data);
        assertNotNull(user);

        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    static class Entity {
        public int id;
        public String name;
    }

    @Test
    public void test_cast2() {
        Spare<Entity> spare =
            Spare.lookup(Entity.class);

        User user = new User();
        user.id = 1;
        user.name = "kraity";

        Entity entity = spare.cast(user);
        assertNotNull(entity);

        assertEquals(user.id, entity.id);
        assertEquals(user.name, entity.name);
    }

    @Test
    public void test_update0() {
        User user = new User();
        user.id = 1;
        user.name = "kraity";

        Supplier supplier = Supplier.ins();
        Spare<Entity> spare = supplier.lookup(Entity.class);

        Entity entity = new Entity();
        assertEquals(2, spare.update(entity, supplier.flat(user)));

        assertEquals(user.id, entity.id);
        assertEquals(user.name, entity.name);
    }

    @Test
    public void test_update1() {
        HashMap<String, Object>
            data = new HashMap<>();

        data.put("id", 1);
        data.put("name", "kraity");

        Spoiler spoiler = Spoiler.of(data);
        Supplier supplier = Supplier.ins();

        User user = new User();
        assertTrue(supplier.update(user, spoiler));

        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_migrate() {
        User user = new User();
        user.id = 1;
        user.name = "kraity";
        Entity entity = new Entity();

        Supplier supplier = Supplier.ins();
        assertTrue(supplier.migrate(user, entity));

        assertEquals(user.id, entity.id);
        assertEquals(user.name, entity.name);
    }

    @Test
    public void test_provider() {
        Spare<User> spare =
            Spare.lookup(User.class);

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

        Spare<CharSequence> spare = new Property<CharSequence>(
            CharSequence.class
        ) {
            @Override
            public CharSequence read(
                Flag flag,
                Value value
            ) throws IOException {
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

                throw new UnexpectedCrash("Error");
            }
        };

        for (Type type : types) {
            Event<CharSequence> event =
                new Event<>("$(test)");
            event.with(type);

            assertEquals(
                type, spare.read(event).getClass()
            );
        }
    }
}
