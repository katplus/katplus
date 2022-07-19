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
                "${$:date()$:time()}"
            )
        );

        assertNotNull(user);
        assertNotNull(user.date);
        assertEquals(1645539742000L, user.date.getTime());
        assertEquals(1645539742000L, user.time.getTime());
    }

    @Embed("User")
    static class User {
        @NotNull
        @Expose("date")
        private Date date = new Date(1645539742000L);

        private Date time = new Date(1645539742000L);

        @NotNull
        @Expose("time")
        public void setTime(
            Date time
        ) {
            this.time = time;
        }

        public Date getTime() {
            return time;
        }
    }
}
