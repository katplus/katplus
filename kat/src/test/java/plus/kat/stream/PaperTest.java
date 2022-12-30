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

import static plus.kat.Spare.lookup;
import static org.junit.jupiter.api.Assertions.*;

public class PaperTest {

    @Test
    public void test_skip() throws Exception {
        byte[] bs = new byte[3072];
        for (int i = 0; i < 3072; i++) {
            bs[i] = (byte) (i % 128);
        }

        for (Paper in : new Paper[]{
            new BytePaper(bs),
            new CharPaper(new String(bs)),
            new ByteBufferPaper(ByteBuffer.wrap(bs)),
            new InputStreamPaper(new ByteArrayInputStream(bs))
        }) {
            try (Paper paper = in) {
                paper.skip(0);
                paper.skip(-1);

                paper.skip(6);
                assertEquals((byte) 6, paper.next());

                paper.skip(2);
                assertEquals((byte) 9, paper.next());

                paper.skip(6);
                assertEquals((byte) 16, paper.next());

                paper.skip(512);
                assertEquals((byte) 17, paper.next());

                paper.skip(1024);
                assertEquals((byte) 18, paper.next());

                paper.skip(1024);
                assertThrows(EOFException.class, () -> paper.skip(1024));
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
        Spare<User> spare = lookup(User.class);

        try (InputStream in = getClass()
            .getResourceAsStream("/entity/user.kat")) {
            User user = spare.read(
                new Event<>(in)
            );

            assertTest(user);
            assertEquals(new BigInteger("6"), user.resource.get("age"));
            assertEquals(new BigDecimal("1024"), user.resource.get("devote"));
        }
    }

    @Test
    public void test_stream_json() throws Exception {
        Spare<User> spare = lookup(User.class);

        try (InputStream in = getClass()
            .getResourceAsStream("/entity/user.json")) {
            User user = spare.parse(
                new Event<>(in)
            );

            assertTest(user);
            assertEquals(6, user.resource.get("age"));
            assertEquals(1024, user.resource.get("devote"));
        }
    }

    @Test
    public void test_stream_xml() throws Exception {
        Spare<User> spare = lookup(User.class);

        try (InputStream in = getClass()
            .getResourceAsStream("/entity/user.xml")) {
            User user = spare.down(
                new Event<>(in)
            );

            assertTest(user);
            assertEquals("6", user.resource.get("age"));
            assertEquals("1024", user.resource.get("devote"));
        }
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
            Bean.class, new ByteBufferPaper(buffer)
        );

        assertNotNull(bean);
        assertFalse(buffer.hasRemaining());
        assertEquals(text, Kat.pure(bean));
    }
}
