package plus.kat.spare;

import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Flag;
import plus.kat.Flow;
import plus.kat.Spare;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ByteBufferSpareTest {

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

        assertEquals(
            0, buf.remaining()
        );
    }

    @Test
    public void test_base_MappedBuffer() throws IOException {
        Spare<MappedByteBuffer> spare
            = Spare.of(MappedByteBuffer.class);

        assertInstanceOf(
            ByteBufferSpare.class, spare
        );

        MappedByteBuffer buf = spare.read(
            Flow.of("a3JhaXR5")
        );

        assertNotNull(buf);
        buf.flip(); // read mode
        int size = buf.remaining();
        byte[] array = new byte[size];

        buf.get(array);
        assertEquals(6, size);
        assertEquals("kraity", new String(array));

        buf.position(0);
        try (Chan chan = spare.write(buf, Flag.NORM)) {
            assertEquals("@ByteArray \"a3JhaXR5\"", chan.toString());
        }

        assertEquals(
            0, buf.remaining()
        );
    }
}
