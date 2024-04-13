package plus.kat;

import plus.kat.actor.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class CoreTest {

    static class User {
        public int id;
        public String name;
    }

    @Test
    public void test0() {
        final int max = 100000000;
        final Random random = new Random();

        User[] group = new User[64];
        Thread[] threads = new Thread[group.length];

        Spare<User> spare =
            Spare.of(User.class);
        CountDownLatch latch =
            new CountDownLatch(group.length);

        for (int i = 0; i < group.length; i++) {
            final int v = i;
            final String text =
                "{id=" + v * max + ",name=kraity}";
            threads[v] = new Thread(() -> {
                try {
                    Thread.sleep(
                        random.nextInt(32)
                    );
                    group[v] = spare.read(
                        Flow.of(text)
                    );
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            latch.await();
            for (int i = 0; i < group.length; i++) {
                User user = group[i];
                assertNotNull(user);
                assertEquals(i * max, user.id);
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    static class Data<T> {
        public T data;
    }

    @Test
    public void test1() throws IOException {
        Data<User[]> data = Kat.decode(
            new Klass<Data<User[]>>(
            ) {
            },
            "{data=[{id=1,name=kraity},{id=2,name=kraithy}]}"
        );

        assertNotNull(data);
        try (Chan chan = Json.encode(data)) {
            assertEquals(
                "{\"data\":[{\"id\":1,\"name\":\"kraity\"},{\"id\":2,\"name\":\"kraithy\"}]}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data)) {
            assertEquals(
                "{data=[{id=1,name=\"kraity\"},{id=2,name=\"kraithy\"}]}", chan.toString()
            );
        }
        try (Chan chan = Kat.encode(data, Flag.NORM | Flag.PRETTY)) {
            String text = "@plus.kat.CoreTest$Data {\n" +
                "  data:Array = [\n" +
                "    @plus.kat.CoreTest$User {\n" +
                "      id:Int = 1,\n" +
                "      name:String = \"kraity\"\n" +
                "    },\n" +
                "    @plus.kat.CoreTest$User {\n" +
                "      id:Int = 2,\n" +
                "      name:String = \"kraithy\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
            assertEquals(text, chan.toString());
        }
    }

    static class Bundle<T> {
        public T bean;
    }

    @Test
    public void test2() throws IOException {
        Bundle<Data<HashMap<String, Long>>> data = Kat.decode(
            new Klass<Bundle<Data<HashMap<String, Long>>>>(
            ) {
            },
            "{bean={data={id=123456}}}"
        );

        assertNotNull(data);
        try (Chan chan = Json.encode(data)) {
            assertEquals(
                "{\"bean\":{\"data\":{\"id\":123456}}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data)) {
            assertEquals(
                "{bean={data={id=123456}}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data, Flag.NORM | Flag.PRETTY)) {
            String text = "@plus.kat.CoreTest$Bundle {\n" +
                "  bean:plus.kat.CoreTest$Data = {\n" +
                "    data:Map = {\n" +
                "      id:Long = 123456\n" +
                "    }\n" +
                "  }\n" +
                "}";
            assertEquals(text, chan.toString());
        }
    }

    @Test
    public void test3() throws IOException {
        Bundle<Data<User>> data = Kat.decode(
            new Klass<Bundle<Data<User>>>(
            ) {
            },
            "{bean={data={id=1,name=kraity}}}"
        );

        assertNotNull(data);
        try (Chan chan = Json.encode(data)) {
            assertEquals(
                "{\"bean\":{\"data\":{\"id\":1,\"name\":\"kraity\"}}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data)) {
            assertEquals(
                "{bean={data={id=1,name=\"kraity\"}}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data, Flag.NORM | Flag.PRETTY)) {
            String text = "@plus.kat.CoreTest$Bundle {\n" +
                "  bean:plus.kat.CoreTest$Data = {\n" +
                "    data:plus.kat.CoreTest$User = {\n" +
                "      id:Int = 1,\n" +
                "      name:String = \"kraity\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
            assertEquals(text, chan.toString());
        }
    }

    static class Group<T> {
        public Bundle<? extends T> bundle;
    }

    @Test
    public void test4() throws IOException {
        Group<Data<User>> data = Kat.decode(
            new Klass<Group<Data<User>>>(
            ) {
            },
            "{bundle={bean={data={id=1,name=kraity}}}}"
        );

        assertNotNull(data);
        try (Chan chan = Json.encode(data)) {
            assertEquals(
                "{\"bundle\":{\"bean\":{\"data\":{\"id\":1,\"name\":\"kraity\"}}}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data)) {
            assertEquals(
                "{bundle={bean={data={id=1,name=\"kraity\"}}}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data, Flag.NORM | Flag.PRETTY)) {
            String text = "@plus.kat.CoreTest$Group {\n" +
                "  bundle:plus.kat.CoreTest$Bundle = {\n" +
                "    bean:plus.kat.CoreTest$Data = {\n" +
                "      data:plus.kat.CoreTest$User = {\n" +
                "        id:Int = 1,\n" +
                "        name:String = \"kraity\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
            assertEquals(text, chan.toString());
        }
    }

    static class Bucket<K, V> {
        public V oneself;
        public Map<K, V> friends;
    }

    @Test
    public void test5() throws IOException {
        Bucket<String, User> data = Kat.decode(
            new Klass<Bucket<String, User>>(
            ) {
            },
            "{oneself={id=1,name=kraity},friends={one={id=2,name=kat},two={id=3,name=plus}}}"
        );

        assertNotNull(data);
        try (Chan chan = Json.encode(data)) {
            assertEquals(
                "{\"oneself\":{\"id\":1,\"name\":\"kraity\"}," +
                    "\"friends\":{" +
                    "\"one\":{\"id\":2,\"name\":\"kat\"}," +
                    "\"two\":{\"id\":3,\"name\":\"plus\"}}}", chan.toString());
        }


        try (Chan chan = Kat.encode(data)) {
            assertEquals(
                "{oneself={id=1,name=\"kraity\"}," +
                    "friends={" +
                    "one={id=2,name=\"kat\"}," +
                    "two={id=3,name=\"plus\"}}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data, Flag.NORM | Flag.PRETTY)) {
            String text = "@plus.kat.CoreTest$Bucket {\n" +
                "  oneself:plus.kat.CoreTest$User = {\n" +
                "    id:Int = 1,\n" +
                "    name:String = \"kraity\"\n" +
                "  },\n" +
                "  friends:Map = {\n" +
                "    one:plus.kat.CoreTest$User = {\n" +
                "      id:Int = 2,\n" +
                "      name:String = \"kat\"\n" +
                "    },\n" +
                "    two:plus.kat.CoreTest$User = {\n" +
                "      id:Int = 3,\n" +
                "      name:String = \"plus\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
            assertEquals(text, chan.toString());
        }
    }

    static class Bag<T> {
        public T bag;
    }

    static class Packet<T> extends Bag<T> {

    }

    @Test
    public void test6() throws IOException {
        Packet<User> data = Kat.decode(
            new Klass<Packet<User>>(
            ) {
            },
            "{bag={id=1,name=kraity}}"
        );

        assertNotNull(data);
        try (Chan chan = Json.encode(data)) {
            assertEquals(
                "{\"bag\":{\"id\":1,\"name\":\"kraity\"}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data)) {
            assertEquals(
                "{bag={id=1,name=\"kraity\"}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data, Flag.NORM | Flag.PRETTY)) {
            String text = "@plus.kat.CoreTest$Packet {\n" +
                "  bag:plus.kat.CoreTest$User = {\n" +
                "    id:Int = 1,\n" +
                "    name:String = \"kraity\"\n" +
                "  }\n" +
                "}";
            assertEquals(text, chan.toString());
        }
    }

    @Magus
    interface Animal<T> {
        T getMaster();

        void setMaster(T user);
    }

    @Magus
    interface Dragon<T> extends Animal<T> {

    }

    @Test
    public void test7() throws IOException {
        Animal<User> data = Kat.decode(
            new Klass<Animal<User>>(
            ) {
            },
            "{master={id=1,name=kraity}}"
        );

        assertNotNull(data);
        try (Chan chan = Json.encode(data)) {
            assertEquals(
                "{\"master\":{\"id\":1,\"name\":\"kraity\"}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data)) {
            assertEquals(
                "{master={id=1,name=\"kraity\"}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data, Flag.NORM | Flag.PRETTY)) {
            String text = "@plus.kat.CoreTest$Animal {\n" +
                "  master:plus.kat.CoreTest$User = {\n" +
                "    id:Int = 1,\n" +
                "    name:String = \"kraity\"\n" +
                "  }\n" +
                "}";
            assertEquals(text, chan.toString());
        }
    }

    @Test
    public void test8() throws IOException {
        Dragon<User> data = Kat.decode(
            new Klass<Dragon<User>>(
            ) {
            },
            "{master={id=1,name=kraity}}"
        );

        assertNotNull(data);
        try (Chan chan = Json.encode(data)) {
            assertEquals(
                "{\"master\":{\"id\":1,\"name\":\"kraity\"}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data)) {
            assertEquals(
                "{master={id=1,name=\"kraity\"}}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data, Flag.NORM | Flag.PRETTY)) {
            String text = "@plus.kat.CoreTest$Dragon {\n" +
                "  master:plus.kat.CoreTest$User = {\n" +
                "    id:Int = 1,\n" +
                "    name:String = \"kraity\"\n" +
                "  }\n" +
                "}";
            assertEquals(text, chan.toString());
        }
    }

    static class Container<T> {
        public T[][][][] elems;
    }

    @Test
    public void test9() throws IOException {
        Container<Long> data = Kat.decode(
            new Klass<Container<Long>>(
            ) {
            },
            "{elems=[[[[1,2]]],[[[3,4]]]]}"
        );

        assertNotNull(data);
        try (Chan chan = Json.encode(data)) {
            assertEquals(
                "{\"elems\":[[[[1,2]]],[[[3,4]]]]}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data)) {
            assertEquals(
                "{elems=[[[[1,2]]],[[[3,4]]]]}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data, Flag.NORM | Flag.PRETTY)) {
            String text = "@plus.kat.CoreTest$Container {\n" +
                "  elems:Array = [\n" +
                "    @Array [\n" +
                "      @Array [\n" +
                "        @Array [\n" +
                "          @Long 1,\n" +
                "          @Long 2\n" +
                "        ]\n" +
                "      ]\n" +
                "    ],\n" +
                "    @Array [\n" +
                "      @Array [\n" +
                "        @Array [\n" +
                "          @Long 3,\n" +
                "          @Long 4\n" +
                "        ]\n" +
                "      ]\n" +
                "    ]\n" +
                "  ]\n" +
                "}";
            assertEquals(text, chan.toString());
        }
    }

    @Test
    public void test10() throws IOException {
        Container<HashMap<String, Double>> data = Kat.decode(
            new Klass<Container<HashMap<String, Double>>>(
            ) {
            },
            "{elems=[[[[{volume=123456}]]]]}"
        );

        assertNotNull(data);
        try (Chan chan = Json.encode(data)) {
            assertEquals(
                "{\"elems\":[[[[{\"volume\":123456.0}]]]]}", chan.toString()
            );
        }

        try (Chan chan = Kat.encode(data)) {
            assertEquals(
                "{elems=[[[[{volume=123456.0}]]]]}", chan.toString()
            );
        }


        try (Chan chan = Kat.encode(data, Flag.NORM | Flag.PRETTY)) {
            String text = "@plus.kat.CoreTest$Container {\n" +
                "  elems:Array = [\n" +
                "    @Array [\n" +
                "      @Array [\n" +
                "        @Array [\n" +
                "          @Map {\n" +
                "            volume:Double = 123456.0\n" +
                "          }\n" +
                "        ]\n" +
                "      ]\n" +
                "    ]\n" +
                "  ]\n" +
                "}";
            assertEquals(text, chan.toString());
        }
    }
}
