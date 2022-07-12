package plus.kat;

import org.junit.jupiter.api.Test;
import plus.kat.anno.Expose;
import plus.kat.spare.IterableSpare;

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
            Properties.class,
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
            assertTrue(klass.isInstance(map));
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
            assertTrue(klass.isInstance(set));
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
            assertTrue(klass.isInstance(list));
            assertEquals(1, list.get(0));
            assertEquals("kat", list.get(1));
        }
    }

    @Test
    public void test_read_Iterable() {
        IterableSpare spare = IterableSpare.INSTANCE;

        Class<Iterable<Object>>[] cls = new Class[]{
            Iterable.class,
            ArrayList.class,
            HashSet.class,
            Collection.class,
            Deque.class,
            ArrayDeque.class
        };

        for (Class<Iterable<Object>> klass : cls) {
            Iterable<Object> iterable = spare.read(
                new Event<Iterable<Object>>(
                    "{i(1)s(kat)}"
                ).with(
                    klass
                )
            );

            assertNotNull(iterable);
            assertTrue(klass.isInstance(iterable));

            Iterator<?> it = iterable.iterator();

            assertTrue(it.hasNext());
            assertEquals(1, it.next());

            assertTrue(it.hasNext());
            assertEquals("kat", it.next());
        }

        Class<Iterable<Object>>[] cls2 = new Class[]{
            Queue.class,
            PriorityQueue.class,
            AbstractQueue.class
        };

        for (Class<Iterable<Object>> klass : cls2) {
            Iterable<Object> iterable = spare.read(
                new Event<Iterable<Object>>(
                    "{i(123)i(456)}"
                ).with(
                    klass
                )
            );

            assertNotNull(iterable);
            assertTrue(klass.isInstance(iterable));

            Iterator<?> it = iterable.iterator();

            assertTrue(it.hasNext());
            assertEquals(123, it.next());

            assertTrue(it.hasNext());
            assertEquals(456, it.next());
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
