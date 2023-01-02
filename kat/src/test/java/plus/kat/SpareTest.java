package plus.kat;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import org.junit.jupiter.api.Test;

import plus.kat.crash.*;
import plus.kat.spare.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;

import static plus.kat.Supplier.Sample;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class SpareTest {

    @Test
    public void test_embed() {
        Sample sample = (Sample) Sample.INS;
        Spare<User> spare = sample.lookup(User.class);

        assertNotNull(spare);
        assertEquals(spare, spare.drop(sample));
        assertNull(sample.major.get(User.class));
        assertNull(sample.minor.get("plus.kat.spare.User"));

        assertEquals(spare, spare.join(sample));
        assertEquals(spare, sample.major.get(User.class));
        assertEquals(spare, sample.minor.get("plus.kat.spare.User"));

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
        assertTrue(supplier.update(user, spoiler) > 0);

        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_mutate() {
        User user = new User();
        user.id = 1;
        user.name = "kraity";
        Entity entity = new Entity();

        Supplier supplier = Supplier.ins();
        assertTrue(supplier.mutate(user, entity) > 0);

        assertEquals(user.id, entity.id);
        assertEquals(user.name, entity.name);
    }

    @Test
    public void test_supplier() {
        Spare<User> spare =
            Spare.lookup(User.class);

        assertNotNull(spare);
        assertNotNull(spare.getSupplier());
    }

    @Embed("plus.kat.spare.User")
    static class User {
        @Expose("id")
        private int id;

        @Expose("name")
        private String name;
    }

    static class UserVO extends User {
        public boolean blocked;
    }

    @Test
    public void test_apply() {
        Spare<User> spare =
            Spare.lookup(User.class);

        assertNotNull(spare.apply());
    }

    @Test
    public void test_apply_type() {
        Spare<User> spare =
            Spare.lookup(User.class);

        assertSame(User.class, spare.apply(User.class).getClass());
        assertSame(User.class, spare.apply((Type) null).getClass());
        assertSame(UserVO.class, spare.apply(UserVO.class).getClass());

        assertThrows(Collapse.class, () -> spare.apply(Object.class));
        assertThrows(Collapse.class, () -> spare.apply(Entity.class));
        assertThrows(Collapse.class, () -> spare.apply(HashMap.class));
    }
}
