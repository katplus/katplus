package plus.kat.netty.flow;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.actor.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ByteBufFlowTest {

    static class User {
        @Magic("id")
        private int id;

        @Magic("name")
        private String name;
    }

    @Test
    public void test() {
        ByteBuf buf = Unpooled.wrappedBuffer(
            "{id=1,name=kraity}".getBytes()
        );

        Spare<User> spare
            = Spare.of(User.class);
        User user = spare.read(
            new ByteBufFlow(buf)
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
        assertEquals(0, buf.readableBytes());
    }

    @Test
    public void test2() {
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
        for (int j = 0; j < 600; j++) {
            stream[i++] = 'k';
            stream[i++] = 'a';
            stream[i++] = 't';
        }
        stream[i++] = '}';

        String name = new String(
            stream, 11, i - 12
        );
        ByteBuf buf = Unpooled.wrappedBuffer(
            Arrays.copyOf(stream, i)
        );

        Spare<User> spare
            = Spare.of(User.class);

        User user = spare.read(
            new ByteBufFlow(buf)
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals(name, user.name);
        assertEquals(0, buf.readableBytes());
    }
}
