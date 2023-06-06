package plus.kat;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class EventTest {

    @Test
    public void test_map1() {
        Map<Integer, Long> map = Kat.decode(
            new Event<Map<Integer, Long>>(
                "{16:Double=123,32:Boolean=456}"
            ) {
            }
        );

        assertNotNull(map);
        assertEquals(123L, map.get(16));
        assertEquals(456L, map.get(32));
    }

    @Test
    public void test_map2() {
        Map<Integer, Long> map = Json.decode(
            new Event<Map<Integer, Long>>(
                "{\"16\":\"123\",\"32\":\"456\"}"
            ) {
            }
        );

        assertNotNull(map);
        assertEquals(123L, map.get(16));
        assertEquals(456L, map.get(32));
    }

    @Test
    public void test_map3() {
        Map<Integer, Long> map = Doc.decode(
            new Event<Map<Integer, Long>>(
                "<data><16>123</16><32>456</32></data>"
            ) {
            }
        );

        assertNotNull(map);
        assertEquals(123L, map.get(16));
        assertEquals(456L, map.get(32));
    }

    @Test
    public void test_list1() {
        List<Long> list = Kat.decode(
            new Event<List<Long>>(
                "[@Int 123,@Float 456,@Double 789]"
            ) {
            }
        );

        assertNotNull(list);
        assertEquals(123L, list.get(0));
        assertEquals(456L, list.get(1));
        assertEquals(789L, list.get(2));
    }
}
