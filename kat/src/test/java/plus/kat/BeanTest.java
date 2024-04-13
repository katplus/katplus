package plus.kat;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class BeanTest {

    @Test
    public void test_map1() throws IOException {
        Map<Integer, Long> map = Kat.decode(
            new Klass<Map<Integer, Long>>(
            ) {
            },
            "{16:Double=123,32:Boolean=456}"
        );

        assertNotNull(map);
        assertEquals(123L, map.get(16));
        assertEquals(456L, map.get(32));
    }

    @Test
    public void test_map2() throws IOException {
        Map<Integer, Long> map = Json.decode(
            new Klass<Map<Integer, Long>>(
            ) {
            },
            "{\"16\":\"123\",\"32\":\"456\"}"
        );

        assertNotNull(map);
        assertEquals(123L, map.get(16));
        assertEquals(456L, map.get(32));
    }

    @Test
    public void test_map3() throws IOException {
        Map<Integer, Long> map = Doc.decode(
            new Klass<Map<Integer, Long>>(
            ) {
            },
            "<data><16>123</16><32>456</32></data>"
        );

        assertNotNull(map);
        assertEquals(123L, map.get(16));
        assertEquals(456L, map.get(32));
    }

    @Test
    public void test_list1() throws IOException {
        List<Long> list = Kat.decode(
            new Klass<List<Long>>(
            ) {
            },
            "[@Int 123,@Float 456,@Double 789]"
        );

        assertNotNull(list);
        assertEquals(123L, list.get(0));
        assertEquals(456L, list.get(1));
        assertEquals(789L, list.get(2));
    }

    @Test
    public void test_map_array() throws IOException {
        Map<Long, Long>[][] data = Kat.decode(
            new Klass<Map<Long, Long>[][]>(
            ) {
            },
            "[[{16=16,32=32},{64=64,128=128}]]"
        );

        assertNotNull(data);
        assertEquals(1, data.length);

        assertNotNull(data[0]);
        assertEquals(2, data[0].length);

        Map<Long, Long> map1 = data[0][0];
        Map<Long, Long> map2 = data[0][1];

        assertNotNull(map1);
        assertNotNull(map1);

        assertEquals(16L, map1.get(16L));
        assertEquals(32L, map1.get(32L));
        assertEquals(64L, map2.get(64L));
        assertEquals(128L, map2.get(128L));
    }
}
