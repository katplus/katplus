package plus.kat;

import org.junit.jupiter.api.Test;

import plus.kat.actor.*;

import java.io.*;
import java.util.*;

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
    public void test_pretty() {
        User user = new User();
        user.id = 1;
        user.name = "kraity";

        String pretty =
            "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"kraity\"\n" +
                "}";
        assertEquals(pretty, Json.pretty(user));

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
        assertEquals(string, Json.pretty(extra));
    }

    @Test
    public void test_parse() {
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
    public void test_parse1() {
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
    public void test_parse2() {
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
    public void test_decode() {
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
    public void test_decode1() {
        String text = "{\"id\":1,\"name\":\"kraity\",\"extras\":[0,true,[123,{\"id\":1,\"name\":\"kraity\"},456],{\"id\":1,\"name\":\"kraity\",\"extras\":[456,789]}]}";

        User user = Json.decode(
            User.class, Flow.of(text)
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    @Test
    public void test_decode2() {
        String text = "[0,true,[123,456,{\"id\":1,\"name\":\"kraity\",\"extras\":[456,789]}],{\"id\":1,\"name\":\"kraity\"},789,{\"id\":1,\"name\":\"kraity\"}]";

        List<Object> data = Json.decode(
            List.class, Flow.of(text)
        );
        assertNotNull(data);

        assertEquals(true, data.get(1));
        assertEquals(text, Json.encode(data));
    }

    @Test
    public void test_encode1() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("name", "kraity");
        assertEquals(
            "{\"name\":\"kraity\",\"id\":1}", Json.encode(map)
        );

        ArrayList<Object> list = new ArrayList<>();
        list.add(1);
        list.add("kraity");
        assertEquals(
            "[1,\"kraity\"]", Json.encode(list)
        );
    }

    @Test
    public void test_encode2() {
        assertEquals(
            "null", Json.encode(null)
        );
        assertEquals(
            "[null,1,\"kat\"]", Json.encode(
                new Object[]{null, 1, "kat"}
            )
        );
        assertEquals(
            "[\"kat\",null,null]", Json.encode(
                new Object[]{"kat", null, null}
            )
        );
    }

    @Test
    public void test_encode3() {
        Map<Object, Object> data = new HashMap<>();
        data.put("别名", "陆之岇");
        data.put("\u0000\u0001\u0006\u0009\u0020", null);
        assertEquals(
            "{\"别名\":\"陆之岇\",\"\\u0000\\u0001\\u0006\\t \":null}", Json.encode(data)
        );
        assertEquals(
            "{\"\\u522B\\u540D\":\"\\u9646\\u4E4B\\u5C87\",\"\\u0000\\u0001\\u0006\\t \":null}", Json.encode(data, Flag.UNICODE)
        );
    }

    @Test
    public void test_json_channel() throws IOException {
        String text = Span.json(it -> {
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
            "{\"id\":100001,\"title\":\"kat\",\"meta\":{\"tag\":\"kat\",\"view\":9999},\"author\":{\"id\":1,\"name\":\"kraity\"}}", text
        );
    }
}
