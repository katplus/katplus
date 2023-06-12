package plus.kat.netty.flow;

import io.netty.buffer.ByteBuf;
import org.junit.jupiter.api.Test;

import plus.kat.Chan;
import plus.kat.Spare;
import plus.kat.chain.Space;

import java.io.IOException;

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
        }
    }
}
