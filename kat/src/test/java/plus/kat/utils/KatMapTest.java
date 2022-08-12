package plus.kat.utils;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class KatMapTest {

    @Test
    public void test_code() {
        KatMap<String, Object> m = new KatMap<>();
        m.put("id", 1);
        assertEquals(1, m.putIfAbsent("id", 2));
        assertEquals(1, m.get("id"));
        assertEquals("kraity", m.getOrDefault("name", "kraity"));

        KatMap<String, Object> map = new KatMap<>();

        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        map.put("id", 1);
        map.put("name", "kraity");

        assertFalse(map.isEmpty());
        assertEquals(2, map.size());

        assertNull(map.get("k"));
        assertEquals(1, map.get("id"));
        assertEquals("kraity", map.get("name"));

        assertEquals(1, map.remove("id"));
        assertEquals(1, map.size());

        assertEquals("kraity", map.put("name", "kat"));
        assertEquals(1, map.size());

        assertFalse(map.containsKey("id"));
        assertTrue(map.containsKey("name"));

        assertTrue(map.containsValue("kat"));
        assertFalse(map.containsValue("kraity"));

        assertNull(map.remove("kat"));
        map.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }


    @Test
    public void test_iter() {
        KatMap<Object, Object> map = new KatMap<>();

        map.put("1", "1");
        map.put(2, 2);
        map.put(true, true);

        for (Map.Entry<Object, Object> m : map) {
            assertEquals(m.getKey(), m.getValue());
        }
    }
}
