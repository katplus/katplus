package plus.kat;

import org.junit.jupiter.api.Test;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;

import java.io.*;
import java.util.*;

import static plus.kat.Flag.*;
import static plus.kat.Spare.lookup;
import static org.junit.jupiter.api.Assertions.*;

public class KatTest {

    @Embed("plus.kat.User")
    static class User {
        @Expose("id")
        private int id;

        @Expose("name")
        private String name;

        @Expose({"blocked", "disabled"})
        private boolean blocked;
    }

    @Test
    public void test_pretty() {
        User user = new User();
        user.id = 1;
        user.name = "kraity";

        String pretty =
            "plus.kat.User{\n" +
                "  i:id(1)\n" +
                "  s:name(kraity)\n" +
                "  b:blocked(0)\n" +
                "}";
        assertEquals(pretty, Kat.pretty(user));

        Map<String, Object> extra = new HashMap<>();
        extra.put("k", new User[]{user});

        String string =
            "M{\n" +
                "  A:k{\n" +
                "    plus.kat.User{\n" +
                "      i:id(1)\n" +
                "      s:name(kraity)\n" +
                "      b:blocked(0)\n" +
                "    }\n" +
                "  }\n" +
                "}";
        assertEquals(string, Kat.pretty(extra));
    }

    @Test
    public void test_encode1() {
        assertEquals(
            "E{i:c(403)s:m(error)}", Kat.encode(
                new plus.kat.crash.Crash(
                    "error", 403
                )
            )
        );
        assertEquals(
            "f(0x42F6E979)", Kat.encode(
                123.456F, Flag.FLOAT_AS_BITMAP
            )
        );
        assertEquals(
            "d(0x405EDD3C07EE0B0B)", Kat.encode(
                123.456789D, Flag.FLOAT_AS_BITMAP
            )
        );
    }

    @Test
    public void test_encode2() {
        Map<Object, Object> data = new HashMap<>();
        data.put("别名", "陆之岇");
        assertEquals(
            "M{s:别名(陆之岇)}", Kat.encode(data)
        );
        assertEquals(
            "M{s:^u522B^u540D(^u9646^u4E4B^u5C87)}", Kat.encode(data, Flag.UNICODE)
        );
    }

    @Test
    public void test_optional() {
        assertEquals("i(1)", Kat.encode(Optional.of(1)));
        assertEquals("$()", Kat.encode(Optional.empty()));
        assertEquals("s(kraity)", Kat.encode(Optional.of("kraity")));
        assertEquals("plus.kat.User{i:id(0)$:name()b:blocked(0)}", Kat.encode(Optional.of(new User())));
    }

    @Test
    public void test_decode1() {
        HashMap<String, Object> any = new HashMap<>();
        any.put("name", "kraity");

        assertEquals(
            "M{s:name(kraity)}", Kat.encode(any)
        );
    }

    @Test
    public void test_decode2() {
        lookup(User.class);
        HashMap<String, User> map = Kat.decode(
            Map.class, new Event<>(
                "M{plus.kat.User:user{i:id(1)s:name(kraity)b:blocked(1)}}"
            )
        );

        User user = map.get("user");
        assertEquals(1, user.id);
        assertTrue(user.blocked);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_decode_string() throws IOException {
        Spare<String> spare =
            lookup(String.class);

        String plain = "hello world";
        String cipher = "s(hello world)";

        assertEquals(
            plain, spare.read(cipher)
        );
        try (Chan kat = spare.write(plain)) {
            assertEquals(
                cipher, kat.toString()
            );
        }
    }

    @Test
    public void test_decode_integer() throws IOException {
        Spare<Integer> spare =
            lookup(Integer.class);

        int plain = 123456789;
        String cipher = "i(123456789)";

        assertEquals(
            plain, spare.read(cipher)
        );
        try (Chan kat = spare.write(plain)) {
            assertEquals(
                cipher, kat.toString()
            );
        }
    }

    @Test
    public void test_decode_long() throws IOException {
        Spare<Long> spare =
            lookup(Long.class);

        long plain = 123456789;
        String cipher = "l(123456789)";

        assertEquals(
            plain, spare.read(cipher)
        );
        try (Chan kat = spare.write(plain)) {
            assertEquals(
                cipher, kat.toString()
            );
        }
    }

    @Test
    public void test_write() throws IOException {
        Spare<User> spare =
            lookup(User.class);

        String text = "plus.kat.User{i:id(1)s:name(kraity)b:blocked(1)}";

        User user = spare.read(
            Event.ascii(text)
        );

        assertNotNull(user);
        try (Chan kat = spare.write(user)) {
            assertEquals(
                text, kat.toString()
            );
        }
    }

    @Test
    public void test_read() {
        Supplier supplier = Supplier.ins();

        User user = supplier.read(
            User.class, Event.ascii(
                "User{i:id(1)s:name(kraity)M:m{i:i(123)M:m{d:d()}}b:disabled(1)}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertTrue(user.blocked);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_read1() {
        Supplier supplier = Supplier.ins();

        Meta meta0 = supplier.read(
            Meta.class, new Event<Meta>(
                "${$:user(${$:id^(1^)$:name^(kraity^)})}"
            ).with(
                Flag.STRING_AS_OBJECT
            )
        );

        assertNotNull(meta0);
        assertNotNull(meta0.user);
        assertEquals(1, meta0.user.id);
        assertEquals("kraity", meta0.user.name);

        Meta meta1 = supplier.read(
            Meta.class, new Event<>(
                "${$:user{$:id(1)$:name(kraity)}}"
            )
        );

        assertNotNull(meta1);
        assertNotNull(meta0.user);
        assertEquals(1, meta1.user.id);
        assertEquals("kraity", meta1.user.name);
    }

    @Test
    public void test_read_wildcard() {
        Supplier supplier = Supplier.ins();

        Map<String, Object> user = supplier.read(
            Map.class, Event.ascii(
                "User{:id(1):name(kraity):blocked(true)}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.get("id"));
        assertEquals(true, user.get("blocked"));
        assertEquals("kraity", user.get("name"));
    }

    @Test
    public void test_member_cast() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("i", 1);
        map.put("s", "kraity");
        map.put("map", "${i:id(1)}");
        map.put("set", "${s(id)}");
        map.put("list", "${i(123)i(456)}");

        Spare<Util> spare =
            lookup(Util.class);

        Util u = spare.cast(map);

        assertNotNull(u);
        assertNull(spare.cast(1));

        assertEquals(1, u.i);
        assertEquals("kraity", u.s);

        assertNotNull(u.map);
        assertEquals(1, u.map.get("id"));

        assertNotNull(u.set);
        assertTrue(u.set.contains("id"));

        assertNotNull(u.list);
        assertEquals(123, u.list.get(0));
        assertEquals(456, u.list.get(1));
    }

    @Test
    public void test_channel() throws IOException {
        String text = Sugar.kat("Story", it -> {
            it.set("id", 100001);
            it.set("title", "kat");
            it.set("meta", meta -> {
                meta.set("tag", "kat");
                meta.set("view", 9999);
            });
            it.set("author", "User", user -> {
                user.set("id", 1);
                user.set("name", "kraity");
            });
        });

        assertEquals(
            "Story{i:id(100001)s:title(kat)M:meta{s:tag(kat)i:view(9999)}User:author{i:id(1)s:name(kraity)}}", text
        );
    }

    @Test
    public void test_relation() {
        Supplier supplier =
            Supplier.ins();
        supplier.lookup(User.class);

        User u1 = supplier.read(
            "plus.kat.User", new Event<>(
                "${$:id(1)$:name(kraity)$:blocked(1)}"
            )
        );

        assertNotNull(u1);
        assertEquals(1, u1.id);
        assertTrue(u1.blocked);
        assertEquals("kraity", u1.name);

        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("name", "kraity");
        map.put("blocked", true);

        User u2 = supplier.cast(
            "plus.kat.User", map
        );

        assertNotNull(u2);
        assertEquals(1, u2.id);
        assertTrue(u2.blocked);
        assertEquals("kraity", u2.name);
    }

    @Test
    public void test_member_cast_2() {
        Spare<User> spare =
            lookup(User.class);

        User u1 = spare.cast(
            "${$:id(1)$:name(kraity)$:blocked(1)}"
        );
        assertEquals(1, u1.id);
        assertTrue(u1.blocked);
        assertEquals("kraity", u1.name);

        User u2 = spare.cast(
            "{\"id\":1,\"name\":\"kraity\",\"blocked\":true}"
        );
        assertEquals(1, u2.id);
        assertTrue(u2.blocked);
        assertEquals("kraity", u2.name);

        User u3 = spare.cast(
            "<user><id>1</id><name>kraity</name><blocked>true</blocked></user>"
        );
        assertEquals(1, u3.id);
        assertTrue(u3.blocked);
        assertEquals("kraity", u3.name);
    }

    @Test
    public void test_enum() {
        Spare<State> spare =
            lookup(State.class);

        assertEquals(State.OPEN, spare.read("State(OPEN)"));
        assertEquals(State.SELF, spare.read("State(SELF)"));

        Supplier supplier = Supplier.ins();
        Note note = supplier.read(
            Note.class, new Event<Note>(
                "Note{State:state(1)}"
            ).with(
                Flag.INDEX_AS_ENUM
            )
        );

        assertNotNull(note);
        assertEquals(note.state, State.SELF);
    }

    @Test
    public void test_reflect() throws IOException {
        Spare<Note> spare =
            lookup(Note.class);

        String text = "plus.kat.Note{i:id(1)s:title(KAT+)B:cipher(S0FUKw==)A:meta{A{i(8)i(16)}A{i(32)}}A:tags{s(kat)}State:state(OPEN)f:version(0.1)l:created(1645539742000)b:deleted(1)L:authors{plus.kat.User{i:id(1)s:name(kraity)b:blocked(1)}}}";
        Note note = spare.read(
            new Event<>(text)
        );

        // assert not null
        assertNotNull(
            spare.getSupplier()
        );

        try (Chan chan = spare.write(note)) {
            assertEquals(
                text, chan.toString()
            );
        }
    }

    enum State {
        OPEN, SELF
    }

    @Embed("plus.kat.Note")
    static class Note {
        @Expose("id")
        private int id;

        @Expose("title")
        private String title;

        @Expose("cipher")
        private byte[] cipher;

        @Expose("meta")
        private int[][] meta;

        @Expose("tags")
        private String[] tags;

        @Expose("state")
        private State state;

        @Expose("version")
        private float version;

        @Expose("created")
        private long created;

        @Expose("deleted")
        private boolean deleted;

        @Expose("authors")
        private List<? extends User> authors;
    }

    static class Meta {
        @Expose(
            value = "user",
            require = NotNull
        )
        private User user;
    }

    @SuppressWarnings("rawtypes")
    static class Util {
        @Expose("i")
        private int i;

        @Expose("s")
        private String s;

        @Expose("map")
        private HashMap map;

        @Expose("set")
        private HashSet set;

        @Expose("list")
        private ArrayList list;
    }

    @Embed("UserVO")
    static class UserVO {
        @Expose("uid")
        private int uid;

        @Expose(
            value = "token",
            require = Readonly
        )
        private String token;
    }

    @Test
    public void test_serialOnly() {
        Supplier supplier = Supplier.ins();

        UserVO user = supplier.read(
            UserVO.class, new Event<>(
                "{:uid(1):token(TOKEN)}"
            )
        );

        assertNull(user.token);
        assertEquals(1, user.uid);

        user.token = "ACCESS";
        assertEquals("UserVO{i:uid(1)s:token(ACCESS)}", Kat.encode(user));
    }
}
