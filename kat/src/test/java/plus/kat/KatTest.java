package plus.kat;

import org.junit.jupiter.api.Test;

import plus.kat.actor.Magic;
import plus.kat.actor.Magus;

import java.io.*;
import java.util.*;

import static plus.kat.Flag.*;
import static plus.kat.Span.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class KatTest {

    @Magus
    static class User {
        @Magic("id")
        private int id;

        @Magic("name")
        private String name;
    }

    @Test
    public void test_pretty() {
        User user = new User();
        user.id = 1;
        user.name = "kraity";

        String pretty =
            "{\n" +
                "  id = 1,\n" +
                "  name = \"kraity\"\n" +
                "}";
        assertEquals(pretty, Kat.pretty(user));

        Map<String, Object> extra = new HashMap<>();
        extra.put("developers", new User[]{user});

        String string = "{\n" +
            "  developers = [\n" +
            "    {\n" +
            "      id = 1,\n" +
            "      name = \"kraity\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        assertEquals(string, Kat.pretty(extra));
    }

    @Test
    public void test_normal() throws IOException {
        Spare<User> spare =
            Spare.of(User.class);

        User user = spare.read(
            Flow.of(
                "@plus.kat.User{id:Int=1,name:String=kraity}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);

        try (Chan chan = spare.write(user, NORM)) {
            assertEquals(
                "@plus.kat.KatTest$User{id:Int=1,name:String=\"kraity\"}", chan.toString()
            );
        }
    }

    @Test
    public void test_encode1() {
        assertEquals(
            "\"fault\"",
            Kat.encode(
                new Error("fault")
            )
        );
        assertEquals(
            "\"fault\"",
            Kat.encode(
                new Throwable("fault")
            )
        );
        assertEquals(
            "\"fault\"",
            Kat.encode(
                new Exception("fault")
            )
        );
    }

    @Test
    public void test_unicode() {
        Map<Object, Object> data = new HashMap<>();
        data.put("别\u0000\u0001\u0009\u0020\u0006\u0007名", "陆之岇");
        assertEquals(
            "{别\\u0000\\u0001\\t\\s\\u0006\\u0007名=\"陆之岇\"}", Kat.encode(data)
        );
        String text = "{\\u522B\\u0000\\u0001\\t\\s\\u0006\\u0007\\u540D=\"\\u9646\\u4E4B\\u5C87\"}";
        assertEquals(
            text, Kat.encode(data, Flag.UNICODE)
        );
        assertEquals(
            data, Kat.decode(HashMap.class, text)
        );
        assertEquals(
            "@L©µŁƎʪ˩Σ『陆之岇』🧬🏷⛰️🌏", Kat.decode(String.class,
                "\\u0040\\u004c\\u00a9\\u00b5\\u0141\\u018e\\u02aa\\u02e9\\u03a3\\u300e" +
                    "\\u9646\\u4e4b\\u5c87\\u300f\\ud83e\\uddec\\ud83c\\udff7\\u26f0\\ufe0f\\ud83c\\udf0f")
        );
    }

    @Test
    public void test_optional() {
        assertEquals("1", Kat.encode(Optional.of(1)));
        assertEquals("null", Kat.encode(Optional.empty()));
        assertEquals("\"kraity\"", Kat.encode(Optional.of("kraity")));
        assertEquals("{id=0,name=null}", Kat.encode(Optional.of(new User())));
    }

    @Test
    public void test_decode1() {
        Map<Object, Object> user = Kat.decode(
            Map.class, Flow.of(
                "@用户{名字=陆之岇}"
            )
        );
        assertNotNull(user);
        assertEquals("陆之岇", user.get("名字"));
    }

    @Test
    public void test_decode2() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("name", "kraity");
        Map<String, Object> group = new HashMap<>();
        user.put("group", group);
        group.put("department", 636);
        group.put("permissions", Arrays.asList(666, "rw-rw-rw-"));

        Map<String, Object> _user = Kat.decode(
            Map.class, Flow.of(
                user.toString()
            )
        );

        assertNotNull(_user);
        assertEquals(1, _user.get("id"));
        assertEquals("kraity", _user.get("name"));

        Map _group = (Map) _user.get("group");
        assertNotNull(_group);
        assertEquals(636, _group.get("department"));

        List _extra = (List) _group.get("permissions");
        assertNotNull(_extra);
        assertEquals(666, _extra.get(0));
        assertEquals("rw-rw-rw-", _extra.get(1));
    }

    @Test
    public void test_decode_string() throws IOException {
        Spare<String> spare =
            Spare.of(String.class);

        String plain = "hello world";
        String cipher = "\"hello world\"";

        assertEquals(
            plain, spare.read(
                Flow.of(cipher)
            )
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
            Spare.of(Integer.class);

        int plain = 123456789;
        assertEquals(
            plain, spare.read(
                Flow.of(
                    "123456789"
                )
            )
        );
        try (Chan kat = spare.write(plain, NORM)) {
            assertEquals(
                "@Int 123456789", kat.toString()
            );
        }
    }

    @Test
    public void test_decode_long() throws IOException {
        Spare<Long> spare =
            Spare.of(Long.class);

        long plain = 123456789;
        assertEquals(
            plain, spare.read(
                Flow.of(
                    "123456789"
                )
            )
        );
        try (Chan kat = spare.write(plain, NORM)) {
            assertEquals(
                "@Long 123456789", kat.toString()
            );
        }
    }

    @Test
    public void test_write() throws IOException {
        Spare<User> spare =
            Spare.of(User.class);

        String text = "{id=1,name=\"kraity\"}";

        User user = spare.read(
            Flow.of(text)
        );

        assertNotNull(user);
        try (Chan kat = spare.write(user)) {
            assertEquals(
                text, kat.toString()
            );
        }
    }

    enum State {
        OPEN, SELF
    }

    @Test
    public void test_enum() {
        Spare<State> spare =
            Spare.of(State.class);

        assertEquals(State.OPEN, spare.read(Flow.of("\"OPEN\"")));
        assertEquals(State.SELF, spare.read(Flow.of("\"SELF\"")));
    }

    @Test
    public void test_filter() {
        String[] texts = {
            "{id=1,a={b=123,c=[123]},name=kraity,d={e=789,f={g=abc}}}",
            "{id = 1, a = {b = 123, c = [123]}, name = kraity, d = {e = 789, f = {g = abc}}}",
        };

        Spare<User> spare
            = Spare.of(User.class);

        for (String text : texts) {
            User user = spare.read(
                Flow.of(text)
            );

            assertNotNull(user);
            assertEquals(1, user.id, text);
            assertEquals("kraity", user.name, text);
        }
    }

    static class Model {
        @Magic("user")
        private User user;
    }

    @Test
    public void test_read1() {
        Spare<Model> spare =
            Spare.of(Model.class);

        Model model1 = spare.read(
            Flow.of(
                "{user={id=1,name=kraity}}"
            )
        );

        assertNotNull(model1);
        assertNotNull(model1.user);
        assertEquals(1, model1.user.id);
        assertEquals("kraity", model1.user.name);

        Model model2 = spare.read(
            Flow.of("{user=\"{id=1,name=kraity}\"}").with(VALUE_AS_BEAN)
        );

        assertNotNull(model2);
        assertNotNull(model2.user);
        assertEquals(1, model2.user.id);
        assertEquals("kraity", model2.user.name);
    }

    @Test
    public void test_read_wildcard() {
        Map<String, Object> user = Kat.decode(
            Map.class, Flow.of(
                "{id=1,name=kraity,blocked=true}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.get("id"));
        assertEquals(true, user.get("blocked"));
        assertEquals("kraity", user.get("name"));
    }

    @Test
    public void test_sugar_kat() throws IOException {
        String text = kat(it -> {
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
            "{id=100001,title=\"kat\",meta={tag=\"kat\",view=9999},author={id=1,name=\"kraity\"}}", text
        );
    }
}
