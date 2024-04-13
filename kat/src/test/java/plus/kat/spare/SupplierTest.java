package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.Flow;

import plus.kat.actor.*;
import plus.kat.chain.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import static plus.kat.Algo.*;
import static plus.kat.spare.Supplier.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 */
@SuppressWarnings("unchecked")
public class SupplierTest {

    static Space space(String s) {
        return new Space(
            s.length(),
            s.getBytes(UTF_8)
        );
    }

    @Test
    public void test_life() {
        Vendor vendor = new Vendor();
        vendor.onCreate();
        assertNotNull(
            vendor.assign(User.class)
        );
        vendor.onDestroy();
        assertTrue(vendor.major.isEmpty());
        assertTrue(vendor.minor.isEmpty());
    }

    @Test
    public void test_embed() {
        Vendor context = Vendor.INS;
        Space space = space("plus.kat.supplier.User");

        Class<?> clazz = User.class;
        Spare<?> spare = context.assign(clazz);

        assertSame(spare, context.assign(clazz));
        assertSame(clazz, context.minor.get(space));
        assertSame(spare, context.revoke(clazz, null));

        assertNull(context.minor.get(space));
        assertNull(context.major.get(clazz));

        Spare<?> coder = context.assign(clazz);
        assertNotSame(spare, coder);
        assertNotNull(context.minor.get(space));

        assertNull(context.revoke(clazz, spare));
        assertSame(clazz, context.minor.get(space));

        assertSame(coder, context.revoke(clazz, coder));
        assertSame(clazz, context.minor.get(space));
    }

    @Test
    public void test_context() {
        Supplier supplier = Supplier.ins();
        for (Class<?> c : new Class[]{
            int.class,
            long.class,
            float.class,
            double.class,
            void.class,
            byte.class,
            short.class,
            boolean.class}) {
            Spare<?> spare = supplier.assign(c);
            assertNotNull(spare);

            // assert not null
            assertNotNull(spare.getContext());
        }

        Spare<?>[] spares = new Spare[]{
            ObjectSpare.INSTANCE,
            StringSpare.INSTANCE,
            IntSpare.INSTANCE,
            LongSpare.INSTANCE,
            FloatSpare.INSTANCE,
            DoubleSpare.INSTANCE,
            BooleanSpare.INSTANCE,
            ByteSpare.INSTANCE,
            ShortSpare.INSTANCE,
            CharSpare.INSTANCE,
            ByteArraySpare.INSTANCE,
            ArraySpare.INSTANCE,
            MapSpare.INSTANCE,
            SetSpare.INSTANCE,
            ListSpare.INSTANCE,
            BigIntegerSpare.INSTANCE,
            BigDecimalSpare.INSTANCE,
        };

        for (Spare<?> spare : spares) {
            // assert not null
            assertNotNull(spare.getContext());
        }
    }

    @Test
    public void test_read_Array() throws IOException {
        Supplier context = Supplier.ins();

        HashMap<Algo, String> data0 = new HashMap<>();
        HashMap<Algo, String> data1 = new HashMap<>();

        data0.put(
            KAT, "[{id=0,name=kraity},{id=1,name=kraity},{id=2,name=kraity},{id=3,name=kraity}]"
        );
        data0.put(
            DOC, "<data><user><id>0</id><name>kraity</name></user><user><id>1</id><name>kraity</name></user><user><id>2</id><name>kraity</name></user><user><id>3</id><name>kraity</name></user></data>"
        );
        data0.put(
            JSON, "[{\"id\":0,\"name\":\"kraity\"},{\"id\":1,\"name\":\"kraity\"},{\"id\":2,\"name\":\"kraity\"},{\"id\":3,\"name\":\"kraity\"}]"
        );

        for (Map.Entry<Algo, String> entry : data0.entrySet()) {
            User[] users0 = context.solve(
                entry.getKey(),
                User[].class,
                Flow.of(
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

            List<User> users1 = context.solve(
                entry.getKey(),
                new Klass<List<User>>() {
                },
                Flow.of(
                    entry.getValue()
                )
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

        data1.put(
            KAT, "[0,1,2,3]"
        );
        data1.put(
            DOC, "<data><item>0</item><item>1</item><item>2</item><item>3</item></data>"
        );
        data1.put(
            JSON, "[0,1,2,3]"
        );

        for (Map.Entry<Algo, String> entry : data1.entrySet()) {
            int[] d0 = context.solve(
                entry.getKey(),
                int[].class,
                Flow.of(
                    entry.getValue()
                )
            );
            assertNotNull(d0);
            assertEquals(4, d0.length);
            for (int i = 0; i < d0.length; i++) {
                assertEquals(i, d0[i]);
            }

            long[] d1 = context.solve(
                entry.getKey(),
                long[].class,
                Flow.of(
                    entry.getValue()
                )
            );
            assertNotNull(d1);
            assertEquals(4, d1.length);
            for (long i = 0; i < d1.length; i++) {
                assertEquals(i, d1[(int) i]);
            }

            short[] d2 = context.solve(
                entry.getKey(),
                short[].class,
                Flow.of(
                    entry.getValue()
                )
            );
            assertNotNull(d2);
            assertEquals(4, d2.length);
            for (short i = 0; i < d2.length; i++) {
                assertEquals(i, d2[i]);
            }

            boolean[] d3 = context.solve(
                entry.getKey(),
                boolean[].class,
                Flow.of(
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
                Object e0 = context.solve(
                    entry.getKey(),
                    klass,
                    Flow.of(
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

                List<?> e1 = context.solve(
                    entry.getKey(),
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
                    },
                    Flow.of(
                        entry.getValue()
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
    public void test_read_Map() throws IOException {
        Supplier context = Supplier.ins();

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
            Map<String, Object> map = context.read(
                klass, Flow.of(
                    "{id:Int=1,tag:String=kat}"
                )
            );

            assertNotNull(map);
            assertTrue(klass.isInstance(map));
            assertEquals(1, map.get("id"));
            assertEquals("kat", map.get("tag"));
        }
    }

    @Test
    public void test_read_Set() throws IOException {
        Supplier context = Supplier.ins();

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
            Set<Object> set = context.read(
                klass, Flow.of(
                    "[@String 1,@String kat]"
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
    public void test_read_List() throws IOException {
        Supplier context = Supplier.ins();

        Class<List<Object>>[] cls = new Class[]{
            Iterable.class,
            Collection.class,
            AbstractList.class,
            List.class,
            ArrayList.class,
            Stack.class,
            Vector.class,
            LinkedList.class,
            CopyOnWriteArrayList.class
        };

        for (Class<List<Object>> klass : cls) {
            List<Object> list = context.read(
                klass, Flow.of(
                    "[@Int 1,@String kat]"
                )
            );

            assertNotNull(list);
            assertTrue(klass.isInstance(list));
            assertEquals(1, list.get(0));
            assertEquals("kat", list.get(1));
        }
    }

    @Magus({
        "plus.kat.vendor.User",
        "plus.kat.supplier.User"
    })
    static class User {
        @Magic("id")
        private int id;

        @Magic("name")
        private String name;

        @Override
        public String toString() {
            return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
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

    static class Service {
        public Model model;
        public Number version;
    }

    @Test
    public void test_lookup_read() throws IOException {
        Supplier context = Supplier.ins();

        assertThrows(IOException.class, () -> context.read(
            Service.class, Flow.of(
                "{model={id=1,name=kraity},version=1}"
            )
        ));

        Service bean = context.read(
            Service.class, Flow.of(
                "{model:plus.kat.spare.SupplierTest$Entity={id=1,name=kraity},version=null}"
            )
        );

        assertNotNull(bean);
        assertNotNull(bean.model);
        assertNull(bean.version);
        assertEquals(1, bean.model.getId());
        assertEquals("kraity", bean.model.getName());
    }

    @Magus("plus.kat.Blog")
    static class BlogVO {

    }

    @Magus("plus.kat.Blog")
    static class BlogDTO {

    }

    @Test
    public void test_alias_conflict() {
        Vendor context = Vendor.INS;
        assertNotNull(context.assign(BlogVO.class));
        assertNotNull(context.minor.get("plus.kat.Blog"));
        assertThrows(RuntimeException.class, () -> context.assign(BlogDTO.class));
    }

    static class Prototype {
        public Object map;
        public Object list;
        public Object digit;
        public Object string;
    }

    @Test
    public void test_read_without_type() throws IOException {
        Vendor context = Vendor.INS;
        Spare<Prototype> spare =
            context.assign(Prototype.class);

        String[] texts = {
            "{map={},list=[],digit=1,string=\"kraity\"}",
            "{map:Map={},list:List=[],digit:Int=1,string:String=\"kraity\"}"
        };

        for (String text : texts) {
            Prototype proto = spare.read(
                Flow.of(text)
            );

            assertNotNull(proto, text);
            assertInstanceOf(Map.class, proto.map, text);
            assertInstanceOf(List.class, proto.list, text);
            assertInstanceOf(Number.class, proto.digit, text);
            assertInstanceOf(String.class, proto.string, text);
        }
    }

    @Test
    public void test_apply_subject() {
        Supplier context = Supplier.ins();
        Map<String, Object> map = context.apply(Map.class);
        assertTrue(map.isEmpty());
        map.put("id", 0);
        map.put("name", "kraity");
        assertEquals(2, map.size());
        assertEquals(0, map.get("id"));
        assertEquals("kraity", map.get("name"));
    }

    @Magus("plus.kat.supplier.UserVO")
    static class UserVO extends User {
        public boolean blocked;
    }

    @Test
    public void test_compare_spare() {
        Supplier context = Supplier.ins();

        Spare<?> userSpare = context.assign(User.class);
        Spare<?> userVOSpare = context.assign(UserVO.class);

        assertSame(userSpare, context.assign(User.class, space("")));
        assertSame(userSpare, context.assign(User.class, space("Map")));
        assertSame(userSpare, context.assign(User.class, space("Array")));
        assertSame(userSpare, context.assign(User.class, space("SuperUser")));

        Space userSpace = space("plus.kat.supplier.User");
        Space userVOSpace = space("plus.kat.supplier.UserVO");

        assertNotNull(context.assign(UserVO.class, userSpace));
        assertSame(userSpare, context.assign(User.class, userSpace));

        assertSame(userSpare, context.assign(User.class, userVOSpace));
        assertSame(userVOSpare, context.assign(UserVO.class, userVOSpace));

        Spare<?> objectSpare = context.assign(Object.class);
        assertNotSame(userSpare, objectSpare);

        Spare<?> objectArraySpare = context.assign(Object[].class);
        assertNotSame(userSpare, objectArraySpare);

        Spare<?> mapSpare = context.assign(Map.class);
        Spare<?> propertiesSpare = context.assign(Properties.class);
        assertNotSame(mapSpare, propertiesSpare);
        assertSame(mapSpare, context.assign(Map.class, space("")));
        assertSame(context.assign(Map.class), context.assign(Map.class, null));
        assertSame(propertiesSpare, context.assign(Map.class, space("java.util.Properties")));

        // The parent is Object.class but the space is active
        assertSame(userSpare, context.assign(Object.class, userSpace));
        assertSame(userVOSpare, context.assign(Object.class, userVOSpace));
        assertSame(objectSpare, context.assign(Object.class, space("")));
        assertSame(objectArraySpare, context.assign(Object.class, space("Array")));

        // The parent is Object.class but the space is not active
        assertSame(objectSpare, context.assign(Object.class, space("SuperUser")));
        assertSame(objectSpare, context.assign(Object.class, space("plus.kat.SpareTest")));
        assertSame(objectSpare, context.assign(Object.class, space("plus.kat.spare.SupplierTest")));
    }
}
