package plus.kat;

import org.junit.jupiter.api.Test;

import plus.kat.anno.Embed;
import plus.kat.anno.Expose;

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
            User.class, Event.ascii(
                "{\"id\":1,\"name\":\"kraity\",\"extra\":[123,456,789,{\"key\":\"val\"}],\"disabled\":true}"
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
    public void test_json_channel() {
        Json chan = new Json(c -> {
            c.set("id", 100001);
            c.set("title", "KAT+");
            c.set("meta", $ -> {
                $.set("tag", "kat");
                $.set("view", 9999);
            });
            c.set("author", "User", $ -> {
                $.set("id", 1);
                $.set("name", "kraity");
            });
        });

        assertEquals(
            "{\"id\":100001,\"title\":\"KAT+\",\"meta\":{\"tag\":\"kat\",\"view\":9999},\"author\":{\"id\":1,\"name\":\"kraity\"}}", chan.toString()
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
}
