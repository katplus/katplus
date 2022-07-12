package plus.kat;

import org.junit.jupiter.api.Test;
import plus.kat.anno.Expose;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class SupplierTest {

    @Test
    public void test() {
        Supplier supplier = Supplier.ins();

        Meta meta = supplier.read(
            Meta.class, new Event<>(
                "{:id(1):tag{:1(2):3(4)}}"
            )
        );

        assertNotNull(meta);
        assertEquals(1, meta.id);
        assertNotNull(meta.tag);
        assertEquals(2L, meta.tag.get(1));
        assertEquals(4L, meta.tag.get(3));
    }

    @Test
    public void test_read_Map() {
        Supplier supplier = Supplier.ins();

        Class<Map<String, Object>>[] cls = new Class[]{
            AbstractMap.class,
            Map.class,
            HashMap.class,
            LinkedHashMap.class,
            TreeMap.class,
            Hashtable.class,
            WeakHashMap.class,
            ConcurrentMap.class,
            NavigableMap.class,
            ConcurrentHashMap.class,
            ConcurrentNavigableMap.class,
            ConcurrentSkipListMap.class
        };

        for (Class<Map<String, Object>> klass : cls) {
            Map<String, Object> map = supplier.read(
                klass, new Event<>(
                    "{i:id(1)s:tag(kat)}"
                )
            );

            assertNotNull(map);
            assertTrue(
                klass.isAssignableFrom(
                    map.getClass()
                )
            );
            assertEquals(1, map.get("id"));
            assertEquals("kat", map.get("tag"));
        }
    }

    @Test
    public void test_read_Set() {
        Supplier supplier = Supplier.ins();

        Class<Set<Object>>[] cls = new Class[]{
            AbstractSet.class,
            Set.class,
            HashSet.class,
            LinkedHashSet.class,
            TreeSet.class,
            SortedSet.class,
            NavigableSet.class,
            ConcurrentSkipListSet.class
        };

        for (Class<Set<Object>> klass : cls) {
            Set<Object> set = supplier.read(
                klass, new Event<>(
                    "{s(1)s(kat)}"
                )
            );

            assertNotNull(set);
            assertTrue(
                klass.isAssignableFrom(
                    set.getClass()
                )
            );
            assertTrue(set.contains("1"));
            assertTrue(set.contains("kat"));
            assertFalse(set.contains("plus"));
        }
    }

    @Test
    public void test_read_List() {
        Supplier supplier = Supplier.ins();

        Class<List<Object>>[] cls = new Class[]{
            AbstractList.class,
            List.class,
            ArrayList.class,
            Stack.class,
            Vector.class,
            LinkedList.class,
            CopyOnWriteArrayList.class
        };

        for (Class<List<Object>> klass : cls) {
            List<Object> list = supplier.read(
                klass, new Event<>(
                    "{i(1)s(kat)}"
                )
            );

            assertNotNull(list);
            assertTrue(
                klass.isAssignableFrom(
                    list.getClass()
                )
            );
            assertEquals(1, list.get(0));
            assertEquals("kat", list.get(1));
        }
    }

    static class Meta {
        private int id;
        private Map<Integer, Long> tag;

        public Meta(
            @Expose("id") int id,
            @Expose("tag") Map<Integer, Long> tag
        ) {
            this.id = id;
            this.tag = tag;
        }
    }
}
