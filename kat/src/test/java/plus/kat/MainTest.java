package plus.kat;

import java.util.Map;
import java.util.HashMap;

import plus.kat.anno.Embed;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class MainTest {

    static class User {
        public int id;
        public String name;
    }

    static class Data<T> {
        public T data;
    }

    @Test
    public void test1() {
        Data<User[]> data = Kat.decode(
            Data.class, new Event<Data<User[]>>(
                "{:data{{:id(1):name(kraity)}{:id(2):name(b)}}}"
            ) {
            }
        );

        assertNotNull(data);
        assertEquals(
            "{\"data\":[{\"id\":1,\"name\":\"kraity\"},{\"id\":2,\"name\":\"b\"}]}", Json.encode(data)
        );
        assertEquals(
            "{:data{{:id(1):name(kraity)}{:id(2):name(b)}}}", Kat.pure(data)
        );
        assertEquals("plus.kat.MainTest$Data{" +
            "A:data{" +
            "plus.kat.MainTest$User{i:id(1)s:name(kraity)}" +
            "plus.kat.MainTest$User{i:id(2)s:name(b)}}}", Kat.encode(data));
    }

    static class Bundle<T> {
        public T bean;
    }

    @Test
    public void test2() {
        Bundle<Data<HashMap<String, Long>>> data = Kat.decode(
            Bundle.class, new Event<Bundle<Data<HashMap<String, Long>>>>(
                "{:bean{:data{:id(123456)}}}"
            ) {
            }
        );

        assertNotNull(data);
        assertEquals(
            "{\"bean\":{\"data\":{\"id\":123456}}}", Json.encode(data)
        );
        assertEquals(
            "{:bean{:data{:id(123456)}}}", Kat.pure(data)
        );
        assertEquals(
            "plus.kat.MainTest$Bundle{plus.kat.MainTest$Data:bean{M:data{l:id(123456)}}}", Kat.encode(data)
        );
    }

    @Test
    public void test3() {
        Bundle<Data<User>> data = Kat.decode(
            Bundle.class, new Event<Bundle<Data<User>>>(
                "{:bean{:data{:id(1):name(kraity)}}}"
            ) {
            }
        );

        assertNotNull(data);
        assertEquals(
            "{\"bean\":{\"data\":{\"id\":1,\"name\":\"kraity\"}}}", Json.encode(data)
        );
        assertEquals(
            "{:bean{:data{:id(1):name(kraity)}}}", Kat.pure(data)
        );
        assertEquals(
            "plus.kat.MainTest$Bundle{" +
                "plus.kat.MainTest$Data:bean{" +
                "plus.kat.MainTest$User:data{i:id(1)s:name(kraity)}}}", Kat.encode(data)
        );
    }

    static class Group<T> {
        public Bundle<? extends T> bundle;
    }

    @Test
    public void test4() {
        Group<Data<User>> data = Kat.decode(
            Group.class, new Event<Group<Data<User>>>(
                "{:bundle{:bean{:data{:id(1):name(kraity)}}}}"
            ) {
            }
        );

        assertNotNull(data);
        assertEquals(
            "{\"bundle\":{\"bean\":{\"data\":{\"id\":1,\"name\":\"kraity\"}}}}", Json.encode(data)
        );
        assertEquals(
            "{:bundle{:bean{:data{:id(1):name(kraity)}}}}", Kat.pure(data)
        );
        assertEquals(
            "plus.kat.MainTest$Group{" +
                "plus.kat.MainTest$Bundle:bundle{" +
                "plus.kat.MainTest$Data:bean{" +
                "plus.kat.MainTest$User:data{i:id(1)s:name(kraity)}}}}", Kat.encode(data)
        );
    }

    static class Bucket<K, V> {
        public V oneself;
        public Map<K, V> friends;
    }

    @Test
    public void test5() {
        Bucket<String, User> bucket = Kat.decode(
            Bucket.class, new Event<Bucket<String, User>>(
                "{:oneself{:id(1):name(kraity)}:friends{:one{:id(2):name(kat)}:two{:id(3):name(plus)}}}"
            ) {
            }
        );

        assertNotNull(bucket);
        assertEquals(
            "{\"oneself\":{\"id\":1,\"name\":\"kraity\"}," +
                "\"friends\":{" +
                "\"one\":{\"id\":2,\"name\":\"kat\"}," +
                "\"two\":{\"id\":3,\"name\":\"plus\"}}}", Json.encode(bucket));
        assertEquals(
            "{:oneself{:id(1):name(kraity)}" +
                ":friends{" +
                ":one{:id(2):name(kat)}" +
                ":two{:id(3):name(plus)}}}", Kat.pure(bucket)
        );
        assertEquals(
            "plus.kat.MainTest$Bucket{" +
                "plus.kat.MainTest$User:oneself{i:id(1)s:name(kraity)}" +
                "M:friends{" +
                "plus.kat.MainTest$User:one{i:id(2)s:name(kat)}" +
                "plus.kat.MainTest$User:two{i:id(3)s:name(plus)}}}", Kat.encode(bucket)
        );
    }

    static class Bag<T> {
        public T bag;
    }

    static class Packet<T> extends Bag<T> {

    }

    @Test
    public void test6() {
        Packet<User> data = Kat.decode(
            Packet.class, new Event<Packet<User>>(
                "{:bag{:id(1):name(kraity)}}"
            ) {
            }
        );

        assertNotNull(data);
        assertEquals(
            "{\"bag\":{\"id\":1,\"name\":\"kraity\"}}", Json.encode(data)
        );
        assertEquals(
            "{:bag{:id(1):name(kraity)}}", Kat.pure(data)
        );
        assertEquals(
            "plus.kat.MainTest$Packet{plus.kat.MainTest$User:bag{i:id(1)s:name(kraity)}}", Kat.encode(data)
        );
    }

    @Embed
    interface Animal<T> {
        T getMaster();

        void setMaster(T user);
    }

    @Embed
    interface Dragon<T> extends Animal<T> {

    }

    @Test
    public void test7() {
        Animal<User> data = Kat.decode(
            Animal.class, new Event<Animal<User>>(
                "{:master{:id(1):name(kraity)}}"
            ) {
            }
        );

        assertNotNull(data);
        assertEquals(
            "{\"master\":{\"id\":1,\"name\":\"kraity\"}}", Json.encode(data)
        );
        assertEquals(
            "{:master{:id(1):name(kraity)}}", Kat.pure(data)
        );
        assertEquals(
            "plus.kat.MainTest$Animal{plus.kat.MainTest$User:master{i:id(1)s:name(kraity)}}", Kat.encode(data)
        );
    }

    @Test
    public void test8() {
        Dragon<User> data = Kat.decode(
            Dragon.class, new Event<Dragon<User>>(
                "{:master{:id(1):name(kraity)}}"
            ) {
            }
        );

        assertNotNull(data);
        assertEquals(
            "{\"master\":{\"id\":1,\"name\":\"kraity\"}}", Json.encode(data)
        );
        assertEquals(
            "{:master{:id(1):name(kraity)}}", Kat.pure(data)
        );
        assertEquals(
            "plus.kat.MainTest$Dragon{plus.kat.MainTest$User:master{i:id(1)s:name(kraity)}}", Kat.encode(data)
        );
    }

    static class Container<T> {
        public T[][][][] elems;
    }

    @Test
    public void test9() {
        Container<Long> data = Kat.decode(
            Container.class, new Event<Container<Long>>(
                "{:elems{ {{ {(1)(2)} }} {{ {(3)(4)} }} } }"
            ) {
            }
        );

        assertNotNull(data);
        assertEquals(
            "{\"elems\":[[[[1,2]]],[[[3,4]]]]}", Json.encode(data)
        );
        assertEquals(
            "{:elems{{{{(1)(2)}}}{{{(3)(4)}}}}}", Kat.pure(data)
        );
        assertEquals(
            "plus.kat.MainTest$Container{A:elems{A{A{A{l(1)l(2)}}}A{A{A{l(3)l(4)}}}}}", Kat.encode(data)
        );
    }

    @Test
    public void test10() {
        Container<HashMap<String, Double>> data = Kat.decode(
            Container.class, new Event<Container<HashMap<String, Double>>>(
                "{:elems{ {{{ {:volume(123456)} }}} } }"
            ) {
            }
        );

        assertNotNull(data);
        assertEquals(
            "{\"elems\":[[[[{\"volume\":123456.0}]]]]}", Json.encode(data)
        );
        assertEquals(
            "{:elems{{{{{:volume(123456.0)}}}}}}", Kat.pure(data)
        );
        assertEquals(
            "plus.kat.MainTest$Container{A:elems{A{A{A{M{d:volume(123456.0)}}}}}}", Kat.encode(data)
        );
    }
}
