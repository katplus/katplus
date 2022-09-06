package plus.kat.caller;

import org.junit.jupiter.api.Test;

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
}
