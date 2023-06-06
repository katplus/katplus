package plus.kat.netty.buffer;

import org.junit.jupiter.api.Test;

import plus.kat.*;
import plus.kat.actor.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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
    }

    @Test
    public void test2() {
        StringBuilder sb = new StringBuilder(2048);
        for (int i = 0; i < 300; i++) {
            sb.append("kraity");
        }
        String name = sb.toString();

        sb.append("}");
        sb.insert(0, "{id=1,name=");
        byte[] bytes = sb.toString().getBytes();

        Spare<User> spare
            = Spare.of(User.class);
        for (int i = 0; i < 3; i++) {
            User user = spare.read(
                new ByteBufFlow(
                    Unpooled.wrappedBuffer(bytes)
                )
            );

            assertNotNull(user);
            assertEquals(1, user.id);
            assertEquals(name, user.name);
        }
    }
}
