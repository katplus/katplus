package plus.kat.anno;

import org.junit.jupiter.api.Test;
import plus.kat.Event;
import plus.kat.Supplier;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class NotNullTest {

    @Test
    public void test() {
        Supplier supplier = Supplier.ins();

        User user = supplier.read(
            User.class, new Event<>(
                "${$:date()}"
            )
        );

        assertNotNull(user);
        assertNotNull(user.date);
        assertEquals(1645539742000L, user.date.getTime());
    }

    @Embed("User")
    static class User {
        @NotNull
        @Expose("date")
        private Date date = new Date(1645539742000L);
    }
}
