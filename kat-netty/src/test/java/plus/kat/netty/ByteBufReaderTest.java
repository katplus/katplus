package plus.kat.netty;

import org.junit.jupiter.api.Test;

import plus.kat.anno.Expose;

import plus.kat.Event;
import plus.kat.Supplier;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class ByteBufReaderTest {

    @Test
    public void test() {
        Supplier supplier = Supplier.ins();

        ByteBuf buf = Unpooled.wrappedBuffer(
            "${$:id(1)$:name(kraity)}".getBytes()
        );

        User user = supplier.read(
            User.class, new Event<>(
                new ByteBufReader(buf)
            )
        );

        assertNotNull(user);
        assertEquals(1, user.id);
        assertEquals("kraity", user.name);
    }

    static class User {
        @Expose("id")
        private int id;

        @Expose("name")
        private String name;
    }
}
