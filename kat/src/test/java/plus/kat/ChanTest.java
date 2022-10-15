package plus.kat;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import plus.kat.kernel.Chain;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author kraity
 */
public class ChanTest {

    @Test
    public void test_chan_toString() throws IOException {
        Chan chan = new Kat();
        chan.set(null, it ->
            it.set("id", 1)
        );

        String expected = "M{i:id(1)}";
        assertEquals(expected, chan.toString());

        chan.close();
        // Chan has been closed
        assertEquals("", chan.toString());
    }

    @Test
    public void test_chan_toBytes() throws IOException {
        Chan chan = new Kat();
        chan.set(null, it ->
            it.set("id", 1)
        );

        byte[] expected = "M{i:id(1)}".getBytes(UTF_8);
        assertArrayEquals(expected, chan.toBytes());

        chan.close();
        // Chan has been closed
        assertArrayEquals(Chain.EMPTY_BYTES, chan.toBytes());
    }

    @Test
    public void test_json_toString() throws IOException {
        Json chan = new Json();
        chan.set(null, it ->
            it.set("id", 1)
        );

        String expected = "{\"id\":1}";
        assertEquals(expected, chan.toString());

        chan.close();
        // Chan has been closed
        assertEquals("", chan.toString());
    }

    @Test
    public void test_json_toBytes() throws IOException {
        Json chan = new Json();
        chan.set(null, it ->
            it.set("id", 1)
        );

        byte[] expected = "{\"id\":1}".getBytes(UTF_8);
        assertArrayEquals(expected, chan.toBytes());

        chan.close();
        // Chan has been closed
        assertArrayEquals(Chain.EMPTY_BYTES, chan.toBytes());
    }

    @Test
    public void test_doc_toString() throws IOException {
        Doc chan = new Doc();
        chan.set("User", it ->
            it.set("id", 1)
        );

        String expected = "<User><id>1</id></User>";
        assertEquals(expected, chan.toString());

        chan.close();
        // Chan has been closed
        assertEquals("", chan.toString());
    }

    @Test
    public void test_doc_toBytes() throws IOException {
        Doc chan = new Doc();
        chan.set("User", it ->
            it.set("id", 1)
        );

        byte[] expected = "<User><id>1</id></User>".getBytes(UTF_8);
        assertArrayEquals(expected, chan.toBytes());

        chan.close();
        // Chan has been closed
        assertArrayEquals(Chain.EMPTY_BYTES, chan.toBytes());
    }
}
