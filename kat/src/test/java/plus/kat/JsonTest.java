package plus.kat;

import org.junit.jupiter.api.Test;

import plus.kat.actor.*;

import java.io.*;
import java.util.*;

import static plus.kat.Pure.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class JsonTest {

    static class Meta {
        @Magic("user")
        private User user;
    }

    static class User {
        @Magic("id")
        private int id;

        @Magic("name")
        private String name;
    }

    @Test
    public void test_pretty() throws IOException {
        User user = new User();
        user.id = 1;
        user.name = "kraity";

        String pretty =
            "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"kraity\"\n" +
                "}";
        try (Chan chan = Json.pretty(user)) {
            assertEquals(pretty, chan.toString());
        }

        Map<String, Object> extra = new HashMap<>();
        extra.put("developers", new User[]{user});

        String string = "{\n" +
            "  \"developers\": [\n" +
            "    {\n" +
            "      \"id\": 1,\n" +
            "      \"name\": \"kraity\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        try (Chan chan = Json.pretty(extra)) {
            assertEquals(string, chan.toString());
        }
    }

    @Test
    public void test_parse() throws IOException {
        Spare<User> spare =
            Spare.of(User.class);

        User user = spare.parse(
            Flow.of(
                "{\"id\":1,\"name\":\"kraity\",\"extra\":[123,456,789,{\"key\":\"val\"}]}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_parse1() throws IOException {
        Spare<Meta> spare =
            Spare.of(Meta.class);

        Meta meta0 = spare.parse(
            Flow.of(
                "{\"user\":{\"id\":1,\"name\":\"kraity\"}}"
            )
        );

        assertNotNull(meta0);
        assertNotNull(meta0.user);
        assertEquals(1, meta0.user.id);
        assertEquals("kraity", meta0.user.name);

        Meta meta1 = spare.parse(
            Flow.of(
                "{\"user\":\"{\\\"id\\\":1,\\\"name\\\":\\\"kraity\\\"}\"}"
            ).with(Flag.VALUE_AS_BEAN)
        );

        assertNotNull(meta1);
        assertNotNull(meta0.user);
        assertEquals(1, meta1.user.id);
        assertEquals("kraity", meta1.user.name);
    }

    @Test
    public void test_parse2() throws IOException {
        Spare<User> spare =
            Spare.of(User.class);

        User user = spare.parse(
            Flow.of(
                "{\"id\":1,\"name\":\"kraity\"}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_decode() throws IOException {
        List<Object> data = Json.decode(
            List.class, Flow.of(
                "[1, 1.25, null, true, false, \"kraity\", [], {}, -2147483648, 2147483647, 9223372036854775807, -9223372036854775808, 123.456, 123456789.12345]"
            )
        );

        assertTrue(data.get(0) instanceof Integer);
        assertTrue(data.get(1) instanceof Double);

        assertNull(data.get(2));
        assertEquals(true, data.get(3));
        assertEquals(false, data.get(4));
        assertEquals("kraity", data.get(5));

        List<Object> list = (List<Object>) data.get(6);
        assertTrue(list.isEmpty());

        HashMap<String, Object> map = (HashMap<String, Object>) data.get(7);
        assertTrue(map.isEmpty());

        assertEquals(Integer.MIN_VALUE, data.get(8));
        assertEquals(Integer.MAX_VALUE, data.get(9));

        assertEquals(Long.MAX_VALUE, data.get(10));
        assertEquals(Long.MIN_VALUE, data.get(11));

        assertEquals(123.456D, data.get(12));
        assertEquals(123456789.12345D, data.get(13));
    }

    @Test
    public void test_decode1() throws IOException {
        String text = "{\"id\":1,\"name\":\"kraity\",\"extras\":[0,true,[123,{\"id\":1,\"name\":\"kraity\"},456],{\"id\":1,\"name\":\"kraity\",\"extras\":[456,789]}]}";

        User user = Json.decode(
            User.class, Flow.of(text)
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_decode2() throws IOException {
        String text = "[0,true,[123,456,{\"id\":1,\"name\":\"kraity\",\"extras\":[456,789]}],{\"id\":1,\"name\":\"kraity\"},789,{\"id\":1,\"name\":\"kraity\"}]";

        List<Object> data = Json.decode(
            List.class, Flow.of(text)
        );
        assertNotNull(data);

        assertEquals(true, data.get(1));
        try (Chan chan = Json.encode(data)) {
            assertEquals(text, chan.toString());
        }
    }

    @Test
    public void test_encode1() throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("name", "kraity");
        try (Chan chan = Json.encode(map)) {
            assertEquals(
                "{\"name\":\"kraity\",\"id\":1}", chan.toString()
            );
        }

        ArrayList<Object> list = new ArrayList<>();
        list.add(1);
        list.add("kraity");
        try (Chan chan = Json.encode(list)) {
            assertEquals("[1,\"kraity\"]", chan.toString());
        }
    }

    @Test
    public void test_encode2() throws IOException {
        try (Chan chan = Json.encode(null)) {
            assertEquals(
                "null", chan.toString()
            );
        }

        try (Chan chan = Json.encode(
            new Object[]{null, 1, "kat"})) {
            assertEquals(
                "[null,1,\"kat\"]", chan.toString()
            );
        }

        try (Chan chan = Json.encode(
            new Object[]{"kat", null, null}
        )) {
            assertEquals(
                "[\"kat\",null,null]", chan.toString()
            );
        }
    }

    @Test
    public void test_encode3() throws IOException {
        Map<Object, Object> data = new HashMap<>();
        data.put("别名", "陆之岇");
        data.put("\u0000\u0001\u0006\u0009\u0020", null);
        try (Chan chan = Json.encode(data)) {
            assertEquals(
                "{\"别名\":\"陆之岇\",\"\\u0000\\u0001\\u0006\\t \":null}", chan.toString()
            );
        }

        try (Chan chan = Json.encode(data, Flag.UNICODE)) {
            assertEquals(
                "{\"\\u522B\\u540D\":\"\\u9646\\u4E4B\\u5C87\",\"\\u0000\\u0001\\u0006\\t \":null}", chan.toString()
            );
        }
    }

    @Test
    public void test_json_channel() throws IOException {
        try (Chan chan = json(it -> {
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
        })) {
            assertEquals(
                "{\"id\":100001,\"title\":\"kat\",\"meta\":{\"tag\":\"kat\",\"view\":9999},\"author\":{\"id\":1,\"name\":\"kraity\"}}", chan.toString()
            );
        }
    }
}
