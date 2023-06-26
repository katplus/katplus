package plus.kat;

import org.junit.jupiter.api.Test;

import plus.kat.entity.*;
import plus.kat.stream.*;

import java.io.*;
import java.nio.*;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @author kraity
 */
public class FlowTest {

    @Test
    public void test_skip() throws IOException {
        byte[] bin = new byte[3072];
        char[] val = new char[3072];
        for (int i = 0; i < 3072; i++) {
            bin[i] = (byte) (i % 128);
            val[i] = (char) (i % 128);
        }
        String text = new String(val);
        for (Flow flow : new Flow[]{
            new ByteFlow(bin),
            new CharFlow(val),
            new StringFlow(text),
            new ReaderFlow(new StringReader(text)),
            new ByteBufferFlow(ByteBuffer.wrap(bin)),
            new CharBufferFlow(CharBuffer.wrap(val)),
            new InputStreamFlow(new ByteArrayInputStream(bin))
        }) {
            try {
                assertFalse(flow.skip(0));
                assertFalse(flow.skip(-1));

                assertTrue(flow.skip(6));
                assertEquals((byte) 6, flow.read());

                assertTrue(flow.skip(2));
                assertEquals((byte) 9, flow.read());

                assertTrue(flow.skip(6));
                assertEquals((byte) 16, flow.next());

                assertTrue(flow.skip(512));
                assertEquals((byte) 17, flow.next());

                assertTrue(flow.skip(1024));
                assertEquals((byte) 18, flow.next());

                assertTrue(flow.skip(1024));
                assertThrows(EOFException.class, () -> flow.skip(1024));
            } finally {
                flow.close();
            }
        }
    }

    @Test
    public void test_stream_kat() throws Exception {
        try (InputStream stream = getClass()
            .getResourceAsStream("/entity/user.kat")) {
            User user = Kat.decode(
                User.class, stream
            );

            assertNotNull(user);
            assertEquals(1, user.id);
            assertEquals("kraity", user.name);
        }
    }

    @Test
    public void test_stream_json() throws Exception {
        try (InputStream stream = getClass()
            .getResourceAsStream("/entity/user.json")) {
            User user = Json.decode(
                User.class, stream
            );

            assertNotNull(user);
            assertEquals(1, user.id);
            assertEquals("kraity", user.name);
        }
    }

    @Test
    public void test_stream_xml() throws Exception {
        try (InputStream stream = getClass()
            .getResourceAsStream("/entity/user.xml")) {
            User user = Doc.decode(
                User.class, stream
            );

            assertNotNull(user);
            assertEquals(1, user.id);
            assertEquals("kraity", user.name);
        }
    }

    static class Model {
        public int id;
        public String name;
    }

    @Test
    public void test_CharFlow() throws IOException {
        String text = "{id=1,name=\"陆之岇\"}";
        char[] chars = text.toCharArray();

        Model model = Kat.decode(
            Model.class, new CharFlow(chars)
        );

        assertNotNull(model);
        try (Chan chan = Kat.encode(model)) {
            assertEquals(text, chan.toString());
        }
    }

    @Test
    public void test_StringFlow() throws IOException {
        String text = "{id=1,name=\"陆之岇\"}";
        Model model = Kat.decode(
            Model.class, new StringFlow(text)
        );

        assertNotNull(model);
        try (Chan chan = Kat.encode(model)) {
            assertEquals(text, chan.toString());
        }
    }

    @Test
    public void test_ReaderFlow() throws IOException {
        String text = "{id=1,name=\"陆之岇\"}";
        try (Reader reader = new StringReader(text)) {
            Model model = Kat.decode(
                Model.class, new ReaderFlow(reader)
            );

            assertNotNull(model);
            assertEquals(-1, reader.read());
            try (Chan chan = Kat.encode(model)) {
                assertEquals(text, chan.toString());
            }
        }
    }

    @Test
    public void test_ByteBufferFlow() throws IOException {
        String text = "{id=1,name=\"陆之岇\"}";
        ByteBuffer buffer =
            ByteBuffer.wrap(
                text.getBytes(UTF_8)
            );

        Model model = Kat.decode(
            Model.class, new ByteBufferFlow(buffer)
        );

        assertNotNull(model);
        assertFalse(buffer.hasRemaining());
        try (Chan chan = Kat.encode(model)) {
            assertEquals(text, chan.toString());
        }
    }

    @Test
    public void test_CharBufferFlow() throws IOException {
        String text = "{id=1,name=\"陆之岇\"}";
        CharBuffer buffer =
            CharBuffer.wrap(
                text.toCharArray()
            );

        Model model = Kat.decode(
            Model.class, new CharBufferFlow(buffer)
        );

        assertNotNull(model);
        assertFalse(buffer.hasRemaining());
        try (Chan chan = Kat.encode(model)) {
            assertEquals(text, chan.toString());
        }
    }
}
