package plus.kat;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.io.IOException;

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
        time.date = new Date(1641871353003L);

        try (Chan chan = Json.encode(time)) {
            assertEquals(
                "{\"date\":\"2022-01-11 11:22:33.003\"}", chan.toString()
            );
        }

        try (Chan chan = Json.encode(time, Flag.TIME_AS_DIGIT)) {
            assertEquals(
                "{\"date\":1641871353003}", chan.toString()
            );
        }
    }

    enum Role {
        A, B, C
    }

    static class Bean0 {
        public Role role;
    }

    @Test
    public void test_enum_as_index() throws IOException {
        Bean0 bean = new Bean0();

        bean.role = Role.A;
        try (Chan chan = Json.encode(bean)) {
            assertEquals("{\"role\":\"A\"}", chan.toString());
        }
        try (Chan chan = Json.encode(bean, Flag.ENUM_AS_INDEX)) {
            assertEquals("{\"role\":0}", chan.toString());
        }

        bean.role = Role.B;
        try (Chan chan = Json.encode(bean)) {
            assertEquals("{\"role\":\"B\"}", chan.toString());
        }
        try (Chan chan = Json.encode(bean, Flag.ENUM_AS_INDEX)) {
            assertEquals("{\"role\":1}", chan.toString());
        }


        bean.role = Role.C;
        try (Chan chan = Json.encode(bean)) {
            assertEquals("{\"role\":\"C\"}", chan.toString());
        }
        try (Chan chan = Json.encode(bean, Flag.ENUM_AS_INDEX)) {
            assertEquals("{\"role\":2}", chan.toString());
        }
    }
}
