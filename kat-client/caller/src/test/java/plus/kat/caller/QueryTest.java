package plus.kat.caller;

import org.junit.jupiter.api.Test;
import plus.kat.Supplier;
import plus.kat.spare.Spoiler;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class QueryTest {

    @Test
    public void test() {
        Query query = new Query();
        query.set("id", 1);
        query.set("name", "陆之岇");
        assertEquals("id=1&name=%E9%99%86%E4%B9%8B%E5%B2%87", query.toString());
        assertEquals("{name=陆之岇, id=1}", query.toMap().toString());
        assertEquals("{name=陆之岇, id=1}", Query.toMap(query.toString()).toString());
    }

    @Test
    public void test2() {
        Query query = new Query();
        query.set("id", 1);
        query.set("tag", "kat");
        query.set("name", "陆之岇");
        assertEquals("id=1&tag=kat&name=%E9%99%86%E4%B9%8B%E5%B2%87", query.toString());
        assertEquals("{name=陆之岇, id=1, tag=kat}", query.toMap().toString());
        assertEquals("{name=陆之岇, id=1, tag=kat}", Query.toMap(query.toString()).toString());
    }

    @Test
    public void test3() {
        Query query = new Query(
            "https://kat.plus/test/user"
        );
        query.set("id", 1);
        query.set("tag", "kat");
        assertEquals("https://kat.plus/test/user?id=1&tag=kat", query.toString());
        assertEquals("{id=1, tag=kat}", query.toMap().toString());
        assertEquals("{id=1, tag=kat}", Query.toMap(query.toString()).toString());
    }

    @Test
    public void test4() {
        Query query = new Query(
            "https://kat.plus/test/user?do=get"
        );
        query.set("id", 1);
        query.set("tag", "kat");
        assertEquals("https://kat.plus/test/user?do=get&id=1&tag=kat", query.toString());
        assertEquals("{do=get, id=1, tag=kat}", query.toMap().toString());
        assertEquals("{do=get, id=1, tag=kat}", Query.toMap(query.toString()).toString());
    }

    @Test
    public void test5() {
        HashMap<String, Object>
            map = new HashMap<>();
        map.put("id", 1);
        map.put("tag", "kat");

        assertEquals("id=1&tag=kat", new Query(map).toString());

        map.put("id", null);
        assertEquals("id=&tag=kat", new Query(map).toString());
    }

    @Test
    public void test7() {
        Query query = new Query();
        query.set("id", 1);
        query.set("name", "陆之岇");

        Spoiler spoiler = query.spoiler();
        assertTrue(spoiler.hasNext());
        assertEquals("id", spoiler.getKey());

        assertTrue(spoiler.hasNext());
        assertEquals("陆之岇", spoiler.getValue().toString());
    }

    @Test
    public void test8() {
        Query query = new Query();
        query.set("id", 1);
        query.set("tag", "kat");
        query.set("name", "陆之岇");

        Spoiler spoiler = query.spoiler();
        assertTrue(spoiler.hasNext());
        assertTrue(spoiler.hasNext());
        assertTrue(spoiler.hasNext());
        assertEquals("name", spoiler.getKey());
        assertEquals("陆之岇", spoiler.getValue().toString());

        Spoiler spoiler0 = query.spoiler();
        assertTrue(spoiler0.hasNext());
        assertEquals("id", spoiler0.getKey());
        assertEquals("id", spoiler0.getKey());

        assertTrue(spoiler0.hasNext());
        assertEquals("kat", spoiler0.getValue().toString());
        assertEquals("kat", spoiler0.getValue().toString());

        assertTrue(spoiler0.hasNext());
        assertEquals("陆之岇", spoiler0.getValue().toString());
        assertEquals("陆之岇", spoiler0.getValue().toString());
        assertFalse(spoiler0.hasNext());
    }

    static class User {
        public int id;
        public String name;
    }

    @Test
    public void test9() {
        Query query = new Query();
        query.set("id", 1);
        query.set("name", "陆之岇");

        Supplier supplier = Supplier.ins();
        User user = supplier.apply(
            User.class, query.spoiler()
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("陆之岇", user.name);
    }
}
