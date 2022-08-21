package plus.kat;

import org.junit.jupiter.api.Test;
import plus.kat.anno.Expose;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
@SuppressWarnings("unchecked")
public class SupplierTest {

    @Test
    public void test_embed1() {
        String[] list = new String[]{
            "java.util.Map",
            "java.util.Set",
            "java.util.List",
            "java.lang.Object"
        };

        Supplier supplier = Supplier.ins();
        for (String o : list) {
            assertNotNull(
                supplier.lookup(o, Object.class)
            );
        }
    }

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
    public void test_read_Array() {
        Supplier supplier = Supplier.ins();

        HashMap<Job, String> data0 = new HashMap<>();
        data0.put(
            Job.KAT, "{{:id(0):name(kraity)}{:id(1):name(kraity)}{:id(2):name(kraity)}{:id(3):name(kraity)}}"
        );
        data0.put(
            Job.JSON, "[{\"id\":0,\"name\":\"kraity\"},{\"id\":1,\"name\":\"kraity\"},{\"id\":2,\"name\":\"kraity\"},{\"id\":3,\"name\":\"kraity\"}]"
        );
        data0.put(
            Job.DOC, "<data><user><id>0</id><name>kraity</name></user><user><id>1</id><name>kraity</name></user><user><id>2</id><name>kraity</name></user><user><id>3</id><name>kraity</name></user></data>"
        );

        for (Map.Entry<Job, String> entry : data0.entrySet()) {
            User[] users0 = supplier.solve(
                User[].class,
                entry.getKey(),
                new Event<>(
                    entry.getValue()
                )
            );
            assertNotNull(users0);
            assertEquals(4, users0.length);
            for (int i = 0; i < users0.length; i++) {
                User user = users0[i];
                assertNotNull(user);
                assertEquals(i, user.id);
                assertEquals("kraity", user.name);
            }

            List<User> users1 = supplier.solve(
                List.class, entry.getKey(),
                new Event<List<User>>(
                    entry.getValue()
                ) {
                }
            );
            assertNotNull(users1);
            assertEquals(4, users1.size());
            for (int i = 0; i < users1.size(); i++) {
                User user = users1.get(i);
                assertNotNull(user);
                assertEquals(i, user.id);
                assertEquals("kraity", user.name);
            }
        }

        HashMap<Job, String> data1 = new HashMap<>();
        data1.put(
            Job.KAT, "{(0)(1)(2)(3)}"
        );
        data1.put(
            Job.JSON, "[0,1,2,3]"
        );
        data1.put(
            Job.DOC, "<data><item>0</item><item>1</item><item>2</item><item>3</item></data>"
        );

        for (Map.Entry<Job, String> entry : data1.entrySet()) {
            int[] d0 = supplier.solve(
                int[].class,
                entry.getKey(),
                new Event<>(
                    entry.getValue()
                )
            );
            assertNotNull(d0);
            assertEquals(4, d0.length);
            for (int i = 0; i < d0.length; i++) {
                assertEquals(i, d0[i]);
            }

            long[] d1 = supplier.solve(
                long[].class,
                entry.getKey(),
                new Event<>(
                    entry.getValue()
                )
            );
            assertNotNull(d1);
            assertEquals(4, d1.length);
            for (long i = 0; i < d1.length; i++) {
                assertEquals(i, d1[(int) i]);
            }

            short[] d2 = supplier.solve(
                short[].class,
                entry.getKey(),
                new Event<>(
                    entry.getValue()
                )
            );
            assertNotNull(d2);
            assertEquals(4, d2.length);
            for (short i = 0; i < d2.length; i++) {
                assertEquals(i, d2[i]);
            }

            boolean[] d3 = supplier.solve(
                boolean[].class,
                entry.getKey(),
                new Event<>(
                    entry.getValue()
                )
            );
            assertNotNull(d3);
            assertEquals(4, d3.length);
            for (int i = 0; i < d3.length; i++) {
                assertEquals(i != 0, d3[i]);
            }

            for (Class<?> klass : new Class[]{
                Integer[].class, Long[].class, Short[].class
            }) {
                Object e0 = supplier.solve(
                    klass, entry.getKey(),
                    new Event<>(
                        entry.getValue()
                    )
                );
                assertNotNull(e0);

                int size = Array.getLength(e0);
                assertEquals(4, size);

                Class<?> type = klass.getComponentType();
                for (int i = 0; i < size; i++) {
                    Object d = Array.get(e0, i);
                    assertNotNull(d);
                    assertSame(type, d.getClass());
                    assertEquals(i, ((Number) d).intValue());
                }

                List<?> e1 = supplier.solve(
                    List.class, entry.getKey(),
                    new Event<List<?>>(
                        entry.getValue()
                    ).with(
                        new ParameterizedType() {
                            @Override
                            public Type getRawType() {
                                return List.class;
                            }

                            @Override
                            public Type getOwnerType() {
                                return null;
                            }

                            @Override
                            public Type[] getActualTypeArguments() {
                                return new Type[]{type};
                            }
                        }
                    )
                );
                assertNotNull(e1);
                assertEquals(4, e1.size());
                for (int i = 0; i < e1.size(); i++) {
                    Object d = e1.get(i);
                    assertNotNull(d);
                    assertSame(type, d.getClass());
                    assertEquals(i, ((Number) d).intValue());
                }
            }
        }
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

    static class User {
        @Expose("id")
        private int id;

        @Expose("name")
        private String name;

        @Override
        public String toString() {
            return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
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

    interface Model {
        int getId();

        String getName();
    }

    static class Entity implements Model {
        private int id;
        private String name;

        public void setId(
            int id
        ) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setName(
            String name
        ) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    public void test_lookup_name() {
        Supplier supplier = Supplier.ins();

        Entity entity = supplier.read(
            "plus.kat.SupplierTest$Entity",
            new Event<>(
                "{:id(1):name(kraity)}"
            )
        );

        assertNotNull(entity);
        assertEquals(1, entity.id);
        assertEquals("kraity", entity.name);
    }

    static class Service {
        public Model model;
        public Number version;
    }

    @Test
    public void test_lookup_read() {
        Supplier supplier = Supplier.ins();

        Service s0 = supplier.read(
            Service.class, new Event<>(
                "{:model{:id(1):name(kraity)}:version(1)}"
            )
        );
        assertNotNull(s0);
        assertNull(s0.model);
        assertEquals(1, s0.version);
        assertEquals("{\"model\":null,\"version\":1}", Json.encode(s0));

        Service s1 = supplier.read(
            Service.class, new Event<>(
                "{plus.kat.SupplierTest$Entity:model{:id(1):name(kraity)}:version()}"
            )
        );
        assertNotNull(s1);
        assertNotNull(s1.model);
        assertNull(s1.version);
        assertEquals(1, s1.model.getId());
        assertEquals("kraity", s1.model.getName());
    }
}
