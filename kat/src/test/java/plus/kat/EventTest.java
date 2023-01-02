package plus.kat;

import org.junit.jupiter.api.Test;

import plus.kat.chain.Alias;
import plus.kat.chain.Space;
import plus.kat.spare.Coder;

import java.util.Map;
import java.util.List;

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

    @Test
    public void test_getCoderWithSpace() {
        Supplier supplier = Supplier.ins();

        Coder<?> intCoder = supplier.lookup(int.class);
        Coder<?> longCoder = supplier.lookup(long.class);
        Coder<?> floatCoder = supplier.lookup(float.class);
        Coder<?> stringCoder = supplier.lookup(String.class);

        class TestEvent extends Event<Object> {

            TestEvent(String text) {
                super(text);
            }

            @Override
            public Coder<?> seek(
                Space space, Alias alias
            ) {
                if (space.equals("int_type")) {
                    return intCoder;
                }

                if (space.equals("long_type")) {
                    return longCoder;
                }

                return alias.equals("decimal") ?
                    floatCoder : stringCoder;
            }
        }

        assertEquals(143, Kat.decode(
            Object.class, new TestEvent("int_type(143)"))
        );
        assertEquals(143L, Kat.decode(
            Object.class, new TestEvent("long_type(143)"))
        );
        assertEquals("143", Kat.decode(
            Object.class, new TestEvent("super_type(143)"))
        );
        assertEquals(143F, Kat.decode(
            Object.class, new TestEvent("super_type:decimal(143)"))
        );
    }
}
