package plus.kat;

import org.junit.jupiter.api.Test;
import plus.kat.kernel.Chain;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kraity
 */
public class ChanTest {
    @Test
    public void test_chan_toString() {
        Chan chan = new Chan(c ->
            c.set("id", 1)
        );

        String expected = "M{i:id(1)}";
        assertEquals(expected, chan.toString());

        // Chan has been closed
        assertEquals("", chan.toString());
    }

    @Test
    public void test_chan_toBytes() {
        Chan chan = new Chan(c ->
            c.set("id", 1)
        );

        byte[] expected = "M{i:id(1)}".getBytes(UTF_8);
        assertArrayEquals(expected, chan.toBytes());

        // Chan has been closed
        assertArrayEquals(Chain.EMPTY_BYTES, chan.toBytes());
    }

    @Test
    public void test_json_toString() {
        Json json = new Json(c ->
            c.set("id", 1)
        );

        String expected = "{\"id\":1}";
        assertEquals(expected, json.toString());

        // Chan has been closed
        assertEquals("", json.toString());
    }

    @Test
    public void test_json_toBytes() {
        Json json = new Json(c ->
            c.set("id", 1)
        );

        byte[] expected = "{\"id\":1}".getBytes(UTF_8);
        assertArrayEquals(expected, json.toBytes());

        // Chan has been closed
        assertArrayEquals(Chain.EMPTY_BYTES, json.toBytes());
    }

    @Test
    public void test_doc_toString() {
        Doc doc = new Doc("User", c ->
            c.set("id", 1)
        );

        String expected = "<User><id>1</id></User>";
        assertEquals(expected, doc.toString());

        // Chan has been closed
        assertEquals("", doc.toString());
    }

    @Test
    public void test_doc_toBytes() {
        Doc doc = new Doc("User", c ->
            c.set("id", 1)
        );

        byte[] expected = "<User><id>1</id></User>".getBytes(UTF_8);
        assertArrayEquals(expected, doc.toBytes());

        // Chan has been closed
        assertArrayEquals(Chain.EMPTY_BYTES, doc.toBytes());
    }
}
