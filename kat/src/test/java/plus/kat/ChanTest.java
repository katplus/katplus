package plus.kat;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kraity
 */
public class ChanTest {

    @Test
    public void test_to_kat() throws IOException {
        try (Chan chan = new Kat()) {
            chan.set(null, it -> {
                it.set("id", 1);
                it.set("name", "陆之岇");
            });

            String expected = "{id=1,name=\"陆之岇\"}";
            assertEquals(expected, chan.toString());
            assertArrayEquals(expected.getBytes(UTF_8), chan.toBinary());
        }
    }

    @Test
    public void test_to_xml() throws IOException {
        try (Chan chan = new Doc()) {
            chan.set("User", it -> {
                it.set("id", 1);
                it.set("name", "陆之岇");
            });

            String expected = "<User><id>1</id><name>陆之岇</name></User>";
            assertEquals(expected, chan.toString());
            assertArrayEquals(expected.getBytes(UTF_8), chan.toBinary());
        }
    }

    @Test
    public void test_to_json() throws IOException {
        try (Chan chan = new Json()) {
            chan.set(null, it -> {
                it.set("id", 1);
                it.set("name", "陆之岇");
            });

            String expected = "{\"id\":1,\"name\":\"陆之岇\"}";
            assertEquals(expected, chan.toString());
            assertArrayEquals(expected.getBytes(UTF_8), chan.toBinary());
        }
    }
}
