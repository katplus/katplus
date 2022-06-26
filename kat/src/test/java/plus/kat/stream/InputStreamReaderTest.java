package plus.kat.stream;

import org.junit.jupiter.api.Test;

import plus.kat.Event;
import plus.kat.Spare;
import plus.kat.entity.User;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class InputStreamReaderTest {

    public void assertTest(User user) {
        assertNotNull(user);
        assertEquals(1, user.uid);
        assertFalse(user.blocked);
        assertEquals("kraity", user.name);
        assertEquals("developer", user.role);
    }

    @Test
    public void test_kat() throws Exception {
        Spare<User> spare = Spare
            .embed(User.class);

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
    public void test_json() throws Exception {
        Spare<User> spare = Spare
            .embed(User.class);

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
    public void test_xml() throws Exception {
        Spare<User> spare = Spare
            .embed(User.class);

        User user = spare.down(
            Event.file(
                getClass(), "/entity/user.xml"
            )
        );

        assertTest(user);
        assertEquals("6", user.resource.get("age"));
        assertEquals("1024", user.resource.get("devote"));
    }
}
