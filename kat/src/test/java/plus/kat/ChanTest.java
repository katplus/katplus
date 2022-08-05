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
    public void test0() {
        Chan chan = new Chan(c ->
            c.set("id", 1)
        );

        String expected = "M{i:id(1)}";
        assertEquals(expected, chan.toString());

        // Chan has been closed
        assertEquals("", chan.toString());
    }

    @Test
    public void test1() {
        Chan chan = new Chan(c ->
            c.set("id", 1)
        );

        byte[] expected = "M{i:id(1)}".getBytes(UTF_8);
        assertArrayEquals(expected, chan.toBytes());

        // Chan has been closed
        assertArrayEquals(Chain.EMPTY_BYTES, chan.toBytes());
    }
}
