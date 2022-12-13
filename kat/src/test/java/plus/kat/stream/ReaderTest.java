package plus.kat.stream;

import org.junit.jupiter.api.Test;

import plus.kat.Event;
import plus.kat.Kat;
import plus.kat.Spare;
import plus.kat.entity.User;

import java.io.*;
import java.nio.*;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ReaderTest {

    @Test
    public void test_skip() throws Exception {
        byte[] bs = new byte[3072];
        for (int i = 0; i < 3072; i++) {
            bs[i] = (byte) (i % 128);
        }

        for (Reader in : new Reader[]{
            new ByteReader(bs),
            new CharReader(new String(bs)),
            new ByteBufferReader(ByteBuffer.wrap(bs)),
            new InputStreamReader(new ByteArrayInputStream(bs))
        }) {
            try (Reader reader = in) {
                reader.skip(0);
                reader.skip(-1);

                reader.skip(6);
                assertEquals((byte) 6, reader.next());

                reader.skip(2);
                assertEquals((byte) 9, reader.next());

                reader.skip(6);
                assertEquals((byte) 16, reader.next());

                reader.skip(512);
                assertEquals((byte) 17, reader.next());

                reader.skip(1024);
                assertEquals((byte) 18, reader.next());

                reader.skip(1024);
                assertThrows(EOFException.class, () -> reader.skip(1024));
            }
        }
    }

    public void assertTest(User user) {
        assertNotNull(user);
        assertEquals(1, user.uid);
        assertFalse(user.blocked);
        assertEquals("kraity", user.name);
        assertEquals("developer", user.role);
    }

    @Test
    public void test_stream_kat() throws Exception {
        Spare<User> spare = Spare
            .lookup(User.class);

        User user = spare.read(
            Event.file(
                getClass(), "/entity/user.kat"
            )
        );

        assertTest(user);
        assertEquals(new BigInteger("6"), user.resource.get("age"));
        assertEquals(new BigDecimal("1024"), user.resource.get("devote"));
    }

    @Test
    public void test_stream_json() throws Exception {
        Spare<User> spare = Spare
            .lookup(User.class);

        User user = spare.parse(
            Event.file(
                getClass(), "/entity/user.json"
            )
        );

        assertTest(user);
        assertEquals(6, user.resource.get("age"));
        assertEquals(1024, user.resource.get("devote"));
    }

    @Test
    public void test_stream_xml() throws Exception {
        Spare<User> spare = Spare
            .lookup(User.class);

        User user = spare.down(
            Event.file(
                getClass(), "/entity/user.xml"
            )
        );

        assertTest(user);
        assertEquals("6", user.resource.get("age"));
        assertEquals("1024", user.resource.get("devote"));
    }

    static class Bean {
        public int id;
        public String name;
    }

    @Test
    public void test_byte_buffer() {
        String text = "{:id(1):name(kraity)}";
        ByteBuffer buffer =
            ByteBuffer.wrap(text.getBytes());

        Bean bean = Kat.decode(
            Bean.class, new ByteBufferReader(buffer)
        );

        assertNotNull(bean);
        assertFalse(buffer.hasRemaining());
        assertEquals(text, Kat.pure(bean));
    }
}
