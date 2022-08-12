package plus.kat.netty;

import org.junit.jupiter.api.Test;

import plus.kat.anno.Expose;

import plus.kat.Event;
import plus.kat.Supplier;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import plus.kat.netty.buffer.ByteBufReader;

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

    @Test
    public void test2() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("kraity");
        }
        String name = sb.toString();

        sb.append(")}");
        sb.insert(0, "{$:id(1)$:name(");
        byte[] bytes = sb.toString().getBytes();

        Supplier supplier = Supplier.ins();

        for (int i = 0; i < 3; i++) {
            User user = supplier.read(
                User.class, new Event<>(
                    new ByteBufReader(
                        Unpooled.wrappedBuffer(bytes)
                    )
                )
            );

            assertNotNull(user);
            assertEquals(1, user.id);
            assertEquals(name, user.name);
        }
    }

    static class User {
        @Expose("id")
        private int id;

        @Expose("name")
        private String name;
    }
}
