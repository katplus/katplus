package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Flag;
import plus.kat.Flow;
import plus.kat.Spare;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class BufferSpareTest {

    @Test
    public void test_base_ByteBuffer() throws IOException {
        Spare<ByteBuffer> spare
            = ByteBufferSpare.INSTANCE;

        ByteBuffer buf = spare.read(
            Flow.of("a3JhaXR5")
        );

        assertNotNull(buf);
        assertEquals("kraity", new String(buf.array()));
        try (Chan chan = spare.write(buf, Flag.NORM)) {
            assertEquals("@ByteArray \"a3JhaXR5\"", chan.toString());
        }
    }
}
