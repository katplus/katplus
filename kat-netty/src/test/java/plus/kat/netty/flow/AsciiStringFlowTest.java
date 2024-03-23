package plus.kat.netty.flow;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.actor.*;

import io.netty.util.AsciiString;

import java.util.Arrays;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class AsciiStringFlowTest {

    static class User {
        @Magic("id")
        private int id;

        @Magic("name")
        private String name;
    }

    @Test
    public void test() throws IOException {
        String span = "###";
        String text = "{id=1,name=kraity}";

        Flow[] flows = new Flow[]{
            new AsciiStringFlow(
                new AsciiString(text)
            ),
            new AsciiStringFlow(
                new AsciiString(span + text + span), span.length(), text.length()
            )
        };

        Spare<User> spare
            = Spare.of(User.class);

        for (Flow flow : flows) {
            User user = spare.read(flow);
            assertNotNull(user);
            assertEquals(1, user.id);
            assertEquals("kraity", user.name);
        }
    }

    @Test
    public void test2() throws IOException {
        int i = 0;
        byte[] stream = new byte[2048];

        stream[i++] = '{';
        stream[i++] = 'i';
        stream[i++] = 'd';
        stream[i++] = '=';
        stream[i++] = '1';
        stream[i++] = ',';
        stream[i++] = 'n';
        stream[i++] = 'a';
        stream[i++] = 'm';
        stream[i++] = 'e';
        stream[i++] = '=';
        for (int j = 0; j < 300; j++) {
            stream[i++] = 'k';
            stream[i++] = 'a';
            stream[i++] = 't';
        }
        stream[i++] = '}';

        String name = new String(
            stream, 11, i - 12
        );

        Spare<User> spare
            = Spare.of(User.class);

        User user = spare.read(
            new AsciiStringFlow(
                new AsciiString(
                    Arrays.copyOf(stream, i)
                )
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals(name, user.name);
    }
}
