package plus.kat.netty.flow;

import io.netty.buffer.ByteBuf;
import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.flow.*;
import plus.kat.chain.*;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static plus.kat.flow.Stream.*;
import static org.junit.jupiter.api.Assertions.*;

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
            assertEquals("{id=1,name=\"kraity\"}", buffer.toString(UTF_8));
            assertTrue(buffer.release()); // finally, call #release
        }
    }

    @Test
    public void test_recycle_after_Stream_close() throws IOException {
        Cache cache = new Cache();
        try (Stream ignored = new Stream(0, cache) {
            {
                emit("kat.plus");
                ByteBuf buf = ByteBufStream.of(this);

                assertTrue(clean);
                assertSame(cache,
                    ((ByteBufStream) buf).bucket
                );

                close();
                // avoid value being used by others
                assertFalse(cache.status);
                assertEquals(0, size);
                assertEquals(
                    "kat.plus", buf.toString(UTF_8)
                );

                assertTrue(buf.release());
                assertTrue(cache.status);
                assertEquals(0, value.length);
            }
        }) {

        }
    }

    @Test
    public void test_recycle_before_Stream_close() throws IOException {
        Cache cache = new Cache();
        try (Stream ignored = new Stream(0, cache) {
            {
                emit("kat.plus");

                byte[] val = value;
                ByteBuf buf = ByteBufStream.of(this);

                assertTrue(clean);
                assertSame(cache,
                    ((ByteBufStream) buf).bucket
                );
                assertEquals(
                    "kat.plus", buf.toString(UTF_8)
                );

                assertTrue(buf.release());
                assertSame(val, value);
                assertTrue(cache.status);

                cache.status = false;
                close();

                // avoid value being used by others
                assertFalse(cache.status);
                assertEquals(0, value.length);
            }
        }) {

        }
    }

    static class Cache implements Bucket {

        boolean status;

        @Override
        public byte[] store(byte[] flow) {
            status = true;
            return BUCKET.store(flow);
        }

        @Override
        public byte[] apply(byte[] flow, int size, int length) {
            return BUCKET.apply(flow, size, length);
        }
    }
}
