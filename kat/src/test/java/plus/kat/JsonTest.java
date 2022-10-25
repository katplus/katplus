package plus.kat;

import org.junit.jupiter.api.Test;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;
import plus.kat.stream.Binary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonTest {

    @Test
    public void test_pretty() {
        User user = new User();
        user.id = 1;
        user.name = "kraity";

        String pretty =
            "{\n" +
                "  \"id\":1,\n" +
                "  \"name\":\"kraity\",\n" +
                "  \"blocked\":false\n" +
                "}";
        assertEquals(pretty, Json.pretty(user));

        Map<String, Object> extra = new HashMap<>();
        extra.put("k", new User[]{user});

        String string =
            "{\n" +
                "  \"k\":[\n" +
                "    {\n" +
                "      \"id\":1,\n" +
                "      \"name\":\"kraity\",\n" +
                "      \"blocked\":false\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        assertEquals(string, Json.pretty(extra));
    }

    @Test
    public void test_parse() {
        Supplier supplier = Supplier.ins();

        User user = supplier.parse(
            User.class, Event.latin(
                "{\"id\":1,\"name\":\"kraity\",\"extra\":[123,456,789,{\"key\":\"val\"}],\"disabled\":true}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertTrue(user.blocked);
        assertEquals("kraity", user.name);
    }


    @Test
    public void test_parse1() {
        Supplier supplier = Supplier.ins();

        Meta meta0 = supplier.parse(
            Meta.class, new Event<>(
                "{\"user\":{\"id\":1,\"name\":\"kraity\"}}"
            )
        );

        assertNotNull(meta0);
        assertNotNull(meta0.user);
        assertEquals(1, meta0.user.id);
        assertEquals("kraity", meta0.user.name);

        Meta meta1 = supplier.parse(
            Meta.class, new Event<Meta>(
                "{\"user\":\"{\\\"id\\\":1,\\\"name\\\":\\\"kraity\\\"}\"}"
            ).with(
                Flag.STRING_AS_OBJECT
            )
        );

        assertNotNull(meta1);
        assertNotNull(meta0.user);
        assertEquals(1, meta1.user.id);
        assertEquals("kraity", meta1.user.name);
    }

    @Test
    public void test_parse2() {
        Supplier supplier = Supplier.ins();

        User user = supplier.parse(
            User.class, Event.latin(
                "{'id':1,'name':'kraity','disabled':true}"
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertTrue(user.blocked);
        assertEquals("kraity", user.name);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_decode() {
        List<Object> data = Json.decode(
            List.class, new Event<>(
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

        HashMap<String, Object> data = Json.decode(
            Map.class, new Event<>(text)
        );
        assertNotNull(data);

        Supplier supplier = Supplier.ins();

        User user = supplier.cast(
            User.class, data
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
        assertEquals(text, Json.encode(data));
    }

    @Test
    public void test_decode2() {
        String text = "[0,true,[123,456,{\"id\":1,\"name\":\"kraity\",\"extras\":[456,789]}],{\"id\":1,\"name\":\"kraity\"},789,{\"id\":1,\"name\":\"kraity\"}]";

        ArrayList<Object> data = Json.decode(
            List.class, new Event<>(text)
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
        String text = Sugar.json(it -> {
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

    @Embed("User")
    static class User {
        @Expose("id")
        private int id;

        @Expose("name")
        private String name;

        @Expose({"blocked", "disabled"})
        private boolean blocked;
    }

    @Embed("Meta")
    static class Meta {
        @Expose("user")
        private User user;
    }
}
