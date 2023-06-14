package plus.kat.netty.flow;

import io.netty.buffer.ByteBuf;
import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Spare;
import plus.kat.chain.Space;
import plus.kat.stream.Bucket;
import plus.kat.stream.Stream;

import java.io.IOException;

import static plus.kat.stream.Toolkit.*;
import static plus.kat.stream.Toolkit.Streams.*;
import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 */
public class ByteBufStreamTest {

    static class User {
        public int id;
        public String name;
    }

    @Test
    public void test_of_Space() {
        String name = "kraity";
        Space space = new Space(
            name.length(), name.getBytes(UTF_8)
        );

        ByteBuf buffer = ByteBufStream.of(space);
        assertFalse(
            ((ByteBufStream) buffer).recycle
        );
        assertEquals(name, buffer.toString(UTF_8));
    }

    @Test
    public void test_of_ChanStream() throws IOException {
        Spare<User> spare
            = Spare.of(User.class);

        User user = new User();
        user.id = 1;
        user.name = "kraity";

        try (Chan chan = spare.write(user)) {
            ByteBuf buffer = ByteBufStream.of(chan);
            assertTrue(
                ((ByteBufStream) buffer).recycle
            );
            assertEquals("{id=1,name=\"kraity\"}", buffer.toString(UTF_8));
            assertTrue(buffer.release()); // finally, call #release
        }
    }

    @Test
    public void test_recycle_after_Stream_close() throws IOException {
        Cache cache = new Cache();
        Stream stream = new Stream(0, cache);
        stream.emit("kat.plus");
        ByteBuf buf = ByteBufStream.of(stream);

        assertTrue(isIsolate(stream));
        assertTrue(
            ((ByteBufStream) buf).recycle
        );

        stream.close();
        // avoid value being used by others
        assertFalse(cache.status);
        assertEquals(0, stream.size());
        assertEquals(
            "kat.plus", buf.toString(UTF_8)
        );

        assertTrue(buf.release());
        // avoid value being used by others
        assertFalse(cache.status);
        assertEquals(0, valueOf(stream).length);
    }

    @Test
    public void test_recycle_before_Stream_close() throws IOException {
        Cache cache = new Cache();
        Stream stream = new Stream(0, cache);
        stream.emit("kat.plus");

        byte[] val = valueOf(stream);
        ByteBuf buf = ByteBufStream.of(stream);

        assertTrue(isIsolate(stream));
        assertTrue(
            ((ByteBufStream) buf).recycle
        );
        assertEquals(
            "kat.plus", buf.toString(UTF_8)
        );

        assertTrue(buf.release());
        // avoid value being used by others
        assertFalse(cache.status);
        assertSame(val, valueOf(stream));

        stream.close();
        // avoid value being used by others
        assertFalse(cache.status);
        assertEquals(0, valueOf(stream).length);
    }

    static class Cache implements Bucket {

        boolean status;

        @Override
        public byte[] store(byte[] flow) {
            status = true;
            return STREAMS.store(flow);
        }

        @Override
        public byte[] apply(byte[] flow, int size, int length) {
            return STREAMS.apply(flow, size, length);
        }
    }
}
