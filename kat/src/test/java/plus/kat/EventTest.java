package plus.kat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    public void test_array() {
        Supplier supplier = Supplier.ins();

        Object[] data = supplier.read(
            Object[].class, new Event<>(
                "A{M{s:method(kat)i:offset(10)i:length(20)}}"
            )
        );

        assertNotNull(data);
        assertEquals(1, data.length);
        assertTrue(data[0] instanceof Map);

        @SuppressWarnings("rawtypes")
        Map map = (Map) data[0];
        assertEquals(10, map.get("offset"));
        assertEquals(20, map.get("length"));
        assertEquals("kat", map.get("method"));
    }

    @Test
    public void test_char_reader_$1() {
        Supplier supplier = Supplier.ins();

        Map<Integer, Long> map = supplier.read(
            Map.class, new Event<Map<Integer, Long>>(
                "${d:16(123)b:32(456)}"
            ) {
            }
        );

        assertNotNull(map);
        assertEquals(123L, map.get(16));
        assertEquals(456L, map.get(32));
    }

    @Test
    public void test_char_reader_$2() {
        Supplier supplier = Supplier.ins();

        Map<Integer, Long> map = supplier.parse(
            Map.class, new Event<Map<Integer, Long>>(
                "{\"16\":\"123\",\"32\":\"456\"}"
            ) {
            }
        );

        assertNotNull(map);
        assertEquals(123L, map.get(16));
        assertEquals(456L, map.get(32));
    }

    @Test
    public void test_char_reader_$3() {
        Supplier supplier = Supplier.ins();

        Map<Integer, Long> map = supplier.down(
            Map.class, new Event<Map<Integer, Long>>(
                "<data><16>123</16><32>456</32></data>"
            ) {
            }
        );

        assertNotNull(map);
        assertEquals(123L, map.get(16));
        assertEquals(456L, map.get(32));
    }

    @Test
    public void test_char_reader_$4() {
        Supplier supplier = Supplier.ins();

        List<Long> list = supplier.read(
            List.class, new Event<List<Long>>(
                "${i(123)d(456)f(789)}"
            ) {
            }
        );

        assertNotNull(list);
        assertEquals(123L, list.get(0));
        assertEquals(456L, list.get(1));
        assertEquals(789L, list.get(2));
    }
}
