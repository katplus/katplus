package plus.kat;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kraity
 */
public class FlagTest {

    static class Time {
        public Date date;
    }

    @Test
    public void test_date_as_timestamp() throws IOException {
        Time time = new Time();
        time.date = new Date(1641871353000L);

        assertEquals(
            "{\"date\":\"2022-01-11 11:22:33\"}", Json.encode(time)
        );

        assertEquals(
            "{\"date\":1641871353000}", Json.encode(time, Flag.DATE_AS_DIGIT)
        );
    }

    enum Role {
        A, B, C
    }

    static class Bean0 {
        public Role role;
    }

    @Test
    public void test_enum_as_index() {
        Bean0 bean = new Bean0();

        bean.role = Role.A;
        assertEquals("{\"role\":\"A\"}", Json.encode(bean));
        assertEquals("{\"role\":0}", Json.encode(bean, Flag.ENUM_AS_INDEX));

        bean.role = Role.B;
        assertEquals("{\"role\":\"B\"}", Json.encode(bean));
        assertEquals("{\"role\":1}", Json.encode(bean, Flag.ENUM_AS_INDEX));


        bean.role = Role.C;
        assertEquals("{\"role\":\"C\"}", Json.encode(bean));
        assertEquals("{\"role\":2}", Json.encode(bean, Flag.ENUM_AS_INDEX));
    }
}
